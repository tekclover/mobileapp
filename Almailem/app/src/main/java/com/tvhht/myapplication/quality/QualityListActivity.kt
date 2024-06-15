package com.tvhht.myapplication.quality


import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.provider.ContactsContract
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.tvhht.myapplication.R
import com.tvhht.myapplication.home.HomePageActivity
import com.tvhht.myapplication.login.LoginLiveData
import com.tvhht.myapplication.login.model.LoginModel
import com.tvhht.myapplication.quality.adapter.QualityAdapter
import com.tvhht.myapplication.quality.model.QualityListResponse
import com.tvhht.myapplication.quality.utils.OrderNumberCustomDialog
import com.tvhht.myapplication.quality.viewmodel.QualityLiveData
import com.tvhht.myapplication.utils.*
import kotlinx.android.synthetic.main.activity_goods_receipt_list.auto_complete_view_order_number
import kotlinx.android.synthetic.main.activity_quality_details.*
import kotlinx.android.synthetic.main.tool_bar.*
import kotlinx.android.synthetic.main.tool_bar.view.*


class QualityListActivity : AppCompatActivity() {

    var dataList: List<QualityListResponse> = ArrayList()
    var filterList: List<QualityListResponse> = ArrayList()
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

        WMSSharedPref.getAppPrefs(
            WMSApplication.getInstance().context
        ).getStringValue(PrefConstant.LOGIN_ID)?.let {
            putaway_txt_user.text = it
        }


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

        btn_filter.setOnClickListener {
            if ((sales_order_no_auto_complete_view.text.toString() == "All") || sales_order_no_auto_complete_view.text.toString().isEmpty()) {
                Recycler_view.adapter = QualityAdapter(this, filterList)
            } else {
                val filteredList =
                    filterList.filter { it.refDocNumber == sales_order_no_auto_complete_view.text.toString() || it.salesOrderNumber == sales_order_no_auto_complete_view.text.toString() }
                Recycler_view.adapter = QualityAdapter(this, filteredList)
            }
        }
    }


    private fun verifyToken() {

        progress.visibility = View.VISIBLE

        val request = LoginModel(
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
        val model = ViewModelProviders.of(this)[QualityLiveData::class.java]
        model.getQualityList().observe(this) { vDataList ->
            // update UI
            val arrayList = (vDataList as ArrayList<QualityListResponse>?)!!

           /* val arrayList2: List<QualityListResponse> =
                arrayList.filter { (it.statusId == 54) && (it.warehouseId == ware_id) }*/
//            WMSSharedPref.getAppPrefs(WMSApplication.getInstance().context)
//                .saveListQualityInfo(arrayList2);

            dataList = arrayList.distinctBy { Pair(it.refDocNumber, it.referenceDocumentType) }

            val size = dataList.size
            putaway_txt_count_val.text = size.toString()

            filterList = dataList
            progress.visibility = View.GONE
            Recycler_view.adapter = QualityAdapter(this, dataList)
            initSpinner()
        }

        toolbar.sign_out.setOnClickListener {
            if (supportFragmentManager.findFragmentByTag("logout_fragment") == null) {
                val logoutFragment = LogoutFragment()
                supportFragmentManager.beginTransaction().add(logoutFragment, "logout_fragment")
                    .commitAllowingStateLoss()
            }
        }
    }
    private fun initSpinner(){
        val orderNoList:MutableList<String> = mutableListOf()
        orderNoList.add("All")
        val distinctList = dataList.distinctBy { it.refDocNumber }
        for (data in distinctList) {
            if (data.referenceDocumentType == ApiConstant.PICK_LIST) {
                data.salesOrderNumber?.let { orderNoList.add(it) }
            } else {
                data.refDocNumber?.let { orderNoList.add(it) }
            }
        }
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, orderNoList)
       // spinner_order_no.adapter = adapter
        sales_order_no_auto_complete_view.threshold = 1
        sales_order_no_auto_complete_view.setAdapter(adapter)
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

        isScanned =
            WMSSharedPref.getAppPrefs(WMSApplication.getInstance().context)
                .getBooleanValue(PrefConstant.CASE_CODE_SCANNED)

        if (!isScanned && isScanned(decodedData.toString())) {
            val myIntent =
                Intent(this@QualityListActivity, OrderNumberCustomDialog::class.java)
            myIntent.putExtra("ORDER_NO", decodedData.toString())
            myIntent.putExtra("TITLE", "Order Number Scanned")
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

    @SuppressLint("NotifyDataSetChanged")
    private fun isScanned(caseID: String): Boolean {
        val caseDetails: QualityListResponse? =
            dataList.find { it.refDocNumber == caseID || it.salesOrderNumber == caseID }

        caseDetails?.let {
            WMSSharedPref.getAppPrefs(
                WMSApplication.getInstance().context
            ).saveStringValue(PrefConstant.QUALITY_ORDER_TYPE, caseDetails.referenceDocumentType)
            return true
        }
      //  Recycler_view.adapter = QualityAdapter(this, dataList)
      //  Recycler_view.adapter?.notifyDataSetChanged()

        return false
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


