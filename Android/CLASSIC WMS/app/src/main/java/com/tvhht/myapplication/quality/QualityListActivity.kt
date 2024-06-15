package com.tvhht.myapplication.quality


import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.provider.ContactsContract
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.tvhht.myapplication.R
import com.tvhht.myapplication.cases.ScannerErrorCustomDialog
import com.tvhht.myapplication.home.HomePageActivity
import com.tvhht.myapplication.login.LoginLiveData
import com.tvhht.myapplication.login.model.LoginModel
import com.tvhht.myapplication.putaway.utils.PalletIDCustomDialog
import com.tvhht.myapplication.quality.adapter.QualityAdapter
import com.tvhht.myapplication.quality.model.QualityListResponse
import com.tvhht.myapplication.quality.utils.HECustomDialog
import com.tvhht.myapplication.quality.viewmodel.QualityLiveData
import com.tvhht.myapplication.utils.*
import kotlinx.android.synthetic.main.activity_quality_details.*
import kotlinx.android.synthetic.main.tool_bar.*
import kotlinx.android.synthetic.main.tool_bar.view.*


class QualityListActivity : AppCompatActivity() {

    var dataList: List<QualityListResponse> = ArrayList<QualityListResponse>()
    var arrayList5: List<QualityListResponse> = ArrayList<QualityListResponse>()
    var str_list: MutableList<String> = ArrayList()
    var isScanned: Boolean = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quality_details)
        Recycler_view.layoutManager = LinearLayoutManager(
            this
        )
        instances2 = this
        WMSSharedPref.getAppPrefs(WMSApplication.getInstance().context)
            .clearSharedPrefs("QUALITY_INFO_LIST_DT")

        WMSSharedPref.getAppPrefs(
            WMSApplication.getInstance().context
        ).saveBooleanValue(
            PrefConstant.CASE_CODE_SCANNED,
            false
        )


        Recycler_view.addItemDecoration(
            DividerItemDecoration(
                Recycler_view.context,
                DividerItemDecoration.VERTICAL
            )
        )

        val login_id = WMSSharedPref.getAppPrefs(
            WMSApplication.getInstance().context
        ).getStringValue(PrefConstant.LOGIN_ID)

        putaway_txt_user.text = login_id


        // checkConnection()
        if (!NetworkUtils().haveNetworkConnection(applicationContext)) {
            Handler().postDelayed({
                progress.visibility = View.GONE
                val parentLayout = findViewById<View>(android.R.id.content)
                val snackbar = Snackbar.make(
                    parentLayout, getString(R.string.internet_check_msg),
                    Snackbar.LENGTH_INDEFINITE
                )

                snackbar.setAction("Dismiss") {
                    snackbar.dismiss()
                    HomePageActivity.getInstance()?.reload()
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
        }

    }


    private fun verifyToken() {

        progress.visibility = View.VISIBLE

        var request = LoginModel(
            ApiConstant.apiName_transaction,
            ApiConstant.clientId,
            ApiConstant.clientSecretKey,
            ApiConstant.grantType,
            ApiConstant.apiName_name,
            ApiConstant.apiName_pass_key
        )
        val model = ViewModelProviders.of(this)[LoginLiveData::class.java]
        model.getLoginStatus(request).observe(this) { asnList ->
            // update UI
            if (asnList.equals("ERROR")) {
                ToastUtils.showToast(applicationContext, "User not Available")
            } else {
                if (asnList.equals("FAILED")) {
                    ToastUtils.showToast(applicationContext, "No Data Available")
                } else {

                    getQualityList()

                }
            }

        }


    }

    private fun getQualityList() {

        val ware_id = WMSSharedPref.getAppPrefs(
            WMSApplication.getInstance().context
        ).getStringValue(PrefConstant.WARE_HOUSE_ID)


        val model = ViewModelProviders.of(this)[QualityLiveData::class.java]
        model.getQualityList().observe(this) { vDataList ->
            // update UI
            val arrayList = (vDataList as ArrayList<QualityListResponse>?)!!

            val arrayList2: List<QualityListResponse> =
                arrayList.filter { (it.statusId == 54) && (it.warehouseId == ware_id) }
//            WMSSharedPref.getAppPrefs(WMSApplication.getInstance().context)
//                .saveListQualityInfo(arrayList2);

            dataList = arrayList2.distinctBy { it.actualHeNo }

            val size = dataList.size
            putaway_txt_count_val.text = size.toString()

            for (cases in dataList?.indices!!) {
                str_list.add(dataList[cases].actualHeNo.toString())
            }

            progress.visibility = View.GONE
            Recycler_view.adapter = QualityAdapter(this, dataList)

        }

        toolbar.sign_out.setOnClickListener {
            val cdd = LogoutCustomDialog(
                this@QualityListActivity
            )
            if (cdd.isShowing) {
                cdd.dismiss()
            }
            cdd.show()
        }


    }


    private val myBroadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent) {
            val action = intent.action
            val b = intent.extras
            if (intent.hasExtra("com.symbol.datawedge.api.RESULT_GET_VERSION_INFO")) {
                val versionInfo =
                    intent.getBundleExtra("com.symbol.datawedge.api.RESULT_GET_VERSION_INFO")
                val DWVersion = versionInfo!!.getString("DATAWEDGE")
                Log.d("LOG", "DataWedge Version: $DWVersion")
            }
            if (action == "com.tvhht.myapplication.ACTION") {
                try {
                    displayScanResult(intent, "via Broadcast")

                } catch (e: Exception) {
                    //  Catch error if the UI does not exist when we receive the broadcast...
                }
            }
        }
    }

    private fun displayScanResult(initiatingIntent: Intent, howDataReceived: String) {
        val decodedData =
            initiatingIntent.getStringExtra("com.symbol.datawedge.data_string")
        val decodedLabelType =
            initiatingIntent.getStringExtra("com.symbol.datawedge.label_type")
        Log.i("LOG", "DataWedge Scanned Code: $decodedData")
        Log.i("LOG", "DataWedge decodedLabelType: $decodedLabelType")

        var shortestLength = 0
        if (str_list.size > 0) {
            shortestLength = str_list.minByOrNull { it.length }!!.length
        }
        isScanned =
            WMSSharedPref.getAppPrefs(WMSApplication.getInstance().context)
                .getBooleanValue(PrefConstant.CASE_CODE_SCANNED)

        if (!isScanned && isScanned(decodedData.toString())) {

            val caseDetails: QualityListResponse =
                dataList.find { it.actualHeNo == decodedData.toString() }!!

            val myIntent =
                Intent(this@QualityListActivity, HECustomDialog::class.java)
            myIntent.putExtra("EXTRA_CODE_1",decodedData.toString())
            myIntent.putExtra("EXTRA_CODE_2",caseDetails.pickupNumber)
            myIntent.putExtra("EXTRA_CODE_3",caseDetails.qualityInspectionNo)
            myIntent.putExtra("EXTRA_CODE_4","HE Number Scanned")
            startActivity(myIntent)
        }


    }

    private fun registerReceivers() {
        Log.d("LOG", "registerReceivers()")
        val filter = IntentFilter()
        filter.addAction("com.symbol.datawedge.api.NOTIFICATION_ACTION") // for notification result
        filter.addAction("com.symbol.datawedge.api.ACTION") // for error code result
        filter.addCategory(Intent.CATEGORY_DEFAULT)
        filter.addAction(resources.getString(R.string.activity_intent_filter_action))
        filter.addAction(resources.getString(R.string.activity_action_from_service))
        registerReceiver(myBroadcastReceiver, filter)
    }

    fun unRegisterScannerStatus() {
        Log.d("LOG", "unRegisterScannerStatus()")
        val b = Bundle()
        b.putString("com.symbol.datawedge.api.APPLICATION_NAME", packageName)
        b.putString(
            "com.symbol.datawedge.api.NOTIFICATION_TYPE",
            "SCANNER_STATUS"
        )
        val i = Intent()
        i.action = ContactsContract.Intents.Insert.ACTION
        i.putExtra("com.symbol.datawedge.api.UNREGISTER_FOR_NOTIFICATION", b)
        this.sendBroadcast(i)
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(myBroadcastReceiver)
        unRegisterScannerStatus()
    }

    private fun isScanned(caseID: String): Boolean {
        var returnState = false;
        val caseDetails: QualityListResponse? =
            dataList.find { it.actualHeNo == caseID }

        if (caseDetails != null) {

            returnState = true
        }
        Recycler_view.adapter = QualityAdapter(this, dataList)
        Recycler_view.adapter!!.notifyDataSetChanged()

        return returnState
    }


    override fun onResume() {
        super.onResume()
        registerReceivers()

    }

    override fun onBackPressed() {
        HomePageActivity.getInstance()?.reload()
        super.onBackPressed()
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
        private var instances2: QualityListActivity? = null
        fun getInstance(): QualityListActivity? {
            return instances2
        }
    }


}


