package com.tvhht.myapplication.home


import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.NestedScrollView
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager.widget.ViewPager
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import com.google.android.gms.tasks.Task
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.messaging.FirebaseMessaging
import com.tbuonomo.viewpagerdotsindicator.SpringDotsIndicator
import com.tvhht.myapplication.BuildConfig
import com.tvhht.myapplication.R
import com.tvhht.myapplication.home.adapter.ViewPagerAdapter
import com.tvhht.myapplication.home.model.DashBoardReportCount
import com.tvhht.myapplication.home.view.*
import com.tvhht.myapplication.login.LoginActivity
import com.tvhht.myapplication.login.LoginLiveData
import com.tvhht.myapplication.login.model.LoginModel
import com.tvhht.myapplication.picking.PickingListActivity
import com.tvhht.myapplication.pushnotification.PushNotificationViewModel
import com.tvhht.myapplication.utils.*
import com.tvhht.myapplication.utils.PrefConstant.FCM_TOKEN
import com.tvhht.myapplication.utils.PrefConstant.FCM_TOKEN_SUBMITTED
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.activity_home.progress
import kotlinx.android.synthetic.main.activity_quality_details.*
import kotlinx.android.synthetic.main.tool_bar.*
import kotlinx.android.synthetic.main.tool_bar.view.*
import java.util.Locale
import java.util.concurrent.TimeUnit


class HomePageActivity : AppCompatActivity() {
    private var viewPagerAdapter: ViewPagerAdapter? = null
    var viewPager: ViewPager? = null
    private var mPeriodicWorkRequest: PeriodicWorkRequest? = null
    private lateinit var loginLiveData: LoginLiveData

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        loginLiveData = ViewModelProvider(this)[LoginLiveData::class.java]

        val appVersion = findViewById<TextView>(R.id.text_version_no)
        appVersion.text = String.format(Locale.getDefault(), "%s%s", "V", BuildConfig.VERSION_NAME)

        toolbar.sign_out.setOnClickListener {
            callLogout()
        }

        instances2 = this

        val userInfo = WMSSharedPref.getAppPrefs(
            WMSApplication.getInstance().context
        ).userInfo

        val stringUserID = WMSSharedPref.getAppPrefs(
            WMSApplication.getInstance().context
        ).getStringValue(PrefConstant.LOGIN_ID)


        val userIndex = WMSSharedPref.getAppPrefs(
            WMSApplication.getInstance().context
        ).getStringValue(PrefConstant.USER_DETAILS_INDEX)
        val find = userInfo?.find {
            it.plantId == userIndex && it.userId?.lowercase(Locale.getDefault()) == stringUserID.lowercase(
                Locale.getDefault()
            )
        }

        wareHouse.text = find?.warehouseId.toString()
        userID.text = find?.userId
        dateID.text = DateUtil.getCurrentDateOnly()
        if (find?.warehouseId.toString() == ApiConstant.WAREHOUSE_ID_200) {
            plantID.visibility = View.GONE
            text_plant_id_label.visibility = View.GONE
        } else {
            plantID.text = find?.plantId.toString()
            plantID.visibility = View.VISIBLE
            text_plant_id_label.visibility = View.VISIBLE
            text_plant_id_label.text =
                this.getString(if (find?.warehouseId.toString() == ApiConstant.WAREHOUSE_ID_100) R.string.branch else R.string.plant)
                    .uppercase(Locale.getDefault())
        }

        WMSSharedPref.getAppPrefs(
            WMSApplication.getInstance().context
        ).saveStringValue(PrefConstant.WARE_HOUSE_ID, find?.warehouseId.toString())
        WMSSharedPref.getAppPrefs(
            WMSApplication.getInstance().context
        ).saveStringValue(PrefConstant.COMPANY_CODE_ID, find?.companyCodeId.toString())
        WMSSharedPref.getAppPrefs(
            WMSApplication.getInstance().context
        ).saveStringValue(PrefConstant.PLANT_ID, find?.plantId.toString())
        WMSSharedPref.getAppPrefs(
            WMSApplication.getInstance().context
        ).saveStringValue(PrefConstant.LANGUAGE_ID, find?.languageId.toString())

        WMSSharedPref.getAppPrefs(
            WMSApplication.getInstance().context
        ).saveUserInfoVOSingle(find)


        if (!NetworkUtils().haveNetworkConnection(applicationContext)) {
            Handler().postDelayed({
                progress.visibility = View.GONE
                val parentLayout = findViewById<View>(android.R.id.content)
                val snackbar = Snackbar.make(
                    parentLayout, getString(R.string.internet_check_msg),
                    Snackbar.LENGTH_INDEFINITE
                )

                snackbar.setAction("Retry") {
                    snackbar.dismiss()
                    reload()
                }
                snackbar.setActionTextColor(Color.BLUE)
                val snackbarView = snackbar.view
                snackbarView.setBackgroundColor(Color.LTGRAY)
                val textView =
                    snackbarView.findViewById(com.google.android.material.R.id.snackbar_text) as TextView
                textView.setTextColor(Color.RED)
                textView.textSize = 16f
                snackbar.show()
            }, 1000)
        } else {
            verifyToken()
            val handler = Handler()
            handler.postDelayed(object : Runnable {
                override fun run() {
                    verifyToken()
                    handler.postDelayed(this, 30000)//1 sec delay
                }
            }, 30000)
        }


        // This is PeriodicWorkRequest it repeats every 10 Hours.
        mPeriodicWorkRequest = PeriodicWorkRequest.Builder(
            MyPeriodicWork::class.java,
            1, TimeUnit.MINUTES
        )
            .addTag("periodicWorkRequest")
            .build()

        WorkManager.getInstance().enqueue(mPeriodicWorkRequest!!)

        fetchFCMToken()
        onNewIntent(intent)
    }


    override fun onResume() {
        super.onResume()
        PutWayFilterHelper.putWayFilter.clear()
    }


    private fun verifyToken() {

        progress.visibility = View.VISIBLE

        var request: LoginModel = LoginModel(
            ApiConstant.apiName_transaction,
            ApiConstant.clientId,
            ApiConstant.clientSecretKey,
            ApiConstant.grantType,
            ApiConstant.apiName_name,
            ApiConstant.apiName_pass_key
        )
        loginLiveData.getLoginStatus(request).observe(this) { asnList ->
            // update UI
            if (asnList.equals("ERROR")) {
                ToastUtils.showToast(applicationContext, "User not Available")
            } else {
                if (asnList.equals("FAILED")) {
                    ToastUtils.showToast(applicationContext, "No Data Available")
                } else {
                    loginLiveData.getUserHomeCount().observe(this) {

                            listVOS: DashBoardReportCount? ->
                        processAccounts(listVOS)
                    }
                }

            }
        }

    }


    private fun processAccounts(listVOS: DashBoardReportCount?) {
        WMSSharedPref.getAppPrefs(
            WMSApplication.getInstance().context
        ).saveCountInfo(listVOS)

        viewPager = findViewById(R.id.pager)

        val find = WMSSharedPref.getAppPrefs(
            WMSApplication.getInstance().context
        ).userInfoSingle

        // setting up the adapter
        viewPagerAdapter = ViewPagerAdapter(supportFragmentManager)
        val caseReceipts = find?.caseReceipts
        val putaway = find?.putaway
        val supplierReturn = find?.supplierReturn
        val transfer = find?.transfer
        val picking = find?.picking
        val quality = find?.quality
        val customerReturn = find?.customerReturn
        val inventory = find?.inventory
        val itemReceipts = find?.itemReceipts

        val intValue = WMSSharedPref.getAppPrefs(
            WMSApplication.getInstance().context
        ).getIntValue(PrefConstant.HOME_PAGE_INDEX)

        // add the fragments
        if (caseReceipts == true || putaway == true || supplierReturn == true)
            viewPagerAdapter!!.add(InBound(), "")
        if (transfer == true)
            viewPagerAdapter!!.add(MakeOrChange(), "")

        if (picking == true || quality == true || customerReturn == true)
            viewPagerAdapter!!.add(OutBound(), "")
        if (inventory == true)
            viewPagerAdapter!!.add(StockCount(), "")
        if (itemReceipts == true)
            viewPagerAdapter!!.add(Reports(), "")
        val progress = findViewById<View>(R.id.progress) as ProgressBar
        progress.visibility = View.GONE
        // Set the adapter

        viewPager!!.adapter = viewPagerAdapter

        val scrollView = findViewById<View>(R.id.nest_scrollview) as NestedScrollView
        scrollView.isFillViewport = true


        val indicator = findViewById<View>(R.id.dots_indicator) as SpringDotsIndicator
        indicator.setViewPager(viewPager!!)
        viewPager!!.currentItem = intValue;

        viewPager?.addOnPageChangeListener(object : ViewPager.SimpleOnPageChangeListener() {
            override fun onPageSelected(position: Int) {
            }

            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
                indicator.setViewPager(viewPager!!)
                super.onPageScrolled(position, positionOffset, positionOffsetPixels)
            }
        });
    }


    private fun callLogout() {
        if (supportFragmentManager.findFragmentByTag("logout_fragment") == null) {
            val logoutFragment = LogoutFragment()
            supportFragmentManager.beginTransaction().add(logoutFragment, "logout_fragment")
                .commitAllowingStateLoss()
        }
    }


    fun reload() {
        val intent = intent
        overridePendingTransition(0, 0)
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
        finish()
        overridePendingTransition(0, 0)
        startActivity(intent)
    }


    companion object {
        private var instances2: HomePageActivity? = null
        fun getInstance(): HomePageActivity? {
            return instances2
        }
    }

    override fun onBackPressed() {
        WMSSharedPref.getAppPrefs(
            WMSApplication.getInstance().applicationContext
        ).clearAllSharedPrefs()
        super.onBackPressed()
    }

    private fun sendFCMToken() {
        val notificationViewModel = ViewModelProvider(this)[PushNotificationViewModel::class.java]
        notificationViewModel.getAuthToken(true)
        notificationViewModel.fcmTokenLiveData.observe(this) {
            it?.let {
                WMSSharedPref.getAppPrefs(this).saveBooleanValue(FCM_TOKEN_SUBMITTED, true)
            }
        }
    }
    private fun fetchFCMToken() {
        val isFCMTokenSubmitted =
            WMSSharedPref.getAppPrefs(this).getBooleanValue(FCM_TOKEN_SUBMITTED)
        if (isFCMTokenSubmitted.not()) {
            FirebaseMessaging.getInstance().token
                .addOnCompleteListener { task: Task<String> ->
                    if (!task.isSuccessful) {
                        return@addOnCompleteListener
                    }
                    val token = task.result
                    Log.d("FCM_TOKEN", "Home:$token")
                    WMSSharedPref.getAppPrefs(this).saveStringValue(FCM_TOKEN, token)
                    sendFCMToken()
                }
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        setIntent(intent)
        processIntentValue()
    }
    private fun processIntentValue() {
        intent?.extras?.let {
            val messageId = it.getString("google.message_id")
            if (messageId.isNullOrEmpty().not()) {
                val isLoggedIn = WMSSharedPref.getAppPrefs(WMSApplication.getInstance().context)
                    .getBooleanValue(PrefConstant.LOGIN_STATUS)
                if (isLoggedIn) {
                    WMSSharedPref.getAppPrefs(
                        WMSApplication.getInstance().context
                    ).saveIntValue(PrefConstant.HOME_PAGE_INDEX, 2)
                    intent?.putExtra("google.message_id", "")
                    startActivity(Intent(this, PickingListActivity::class.java))
                } else {
                   navigateToLogin()
                }
            }
        }
    }
    private fun navigateToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(intent)
        finish()
    }
}