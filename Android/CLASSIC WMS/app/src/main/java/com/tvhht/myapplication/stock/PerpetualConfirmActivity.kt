package com.tvhht.myapplication.stock


import android.app.ProgressDialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.provider.ContactsContract
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.tvhht.myapplication.R
import com.tvhht.myapplication.annual.adapter.AnnualConfirmAdapter
import com.tvhht.myapplication.cases.ScannerErrorCustomDialog
import com.tvhht.myapplication.home.HomePageActivity
import com.tvhht.myapplication.login.LoginLiveData
import com.tvhht.myapplication.login.model.LoginModel
import com.tvhht.myapplication.quality.QualityListActivity
import com.tvhht.myapplication.stock.adapter.PerpetualConfirmAdapter
import com.tvhht.myapplication.stock.model.PerpetualLine
import com.tvhht.myapplication.stock.model.StockPerpetualRequest
import com.tvhht.myapplication.stock.utils.PalletIDCustomDialog
import com.tvhht.myapplication.stock.viewmodel.StockLiveData
import com.tvhht.myapplication.utils.*
import kotlinx.android.synthetic.main.activity_perpetual_confirm.*
import kotlinx.android.synthetic.main.activity_perpetual_confirm.Recycler_view
import kotlinx.android.synthetic.main.activity_perpetual_confirm.barcode_value
import kotlinx.android.synthetic.main.activity_perpetual_confirm.buttonNo
import kotlinx.android.synthetic.main.activity_perpetual_confirm.buttonYes
import kotlinx.android.synthetic.main.activity_perpetual_confirm.progress
import kotlinx.android.synthetic.main.activity_quality_details_confirm.*
import kotlinx.android.synthetic.main.tool_bar.*
import kotlinx.android.synthetic.main.tool_bar.view.*

class PerpetualConfirmActivity : AppCompatActivity() {


    var dataList = ArrayList<PerpetualLine>()
    var dataList3 = ArrayList<PerpetualLine>()
    var duplicate_dataList: MutableList<PerpetualLine> = ArrayList()
    lateinit var pd: ProgressDialog
    var findstockData: PerpetualLine = PerpetualLine()
    var isScanned: Boolean = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_perpetual_confirm)
        Recycler_view.layoutManager = LinearLayoutManager(
            this
        )
        header_tt.text = getString(R.string.perpetual_txt)

        toolbar.sign_out.setOnClickListener {
            val cdd = LogoutCustomDialog(
                this@PerpetualConfirmActivity
            )
            if (cdd.isShowing) {
                cdd.dismiss()
            }
            cdd.show()
        }
        instances2 = this

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


        var loginID =
            WMSSharedPref.getAppPrefs(WMSApplication.getInstance().context)
                .getStringValue(PrefConstant.LOGIN_ID)
        putaway_txt_user.text = loginID

        getStockList()


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

        if (dataList[0].packBarcodes!!.length == decodedData?.length) {

            isScanned =
                WMSSharedPref.getAppPrefs(WMSApplication.getInstance().context)
                    .getBooleanValue(PrefConstant.CASE_CODE_SCANNED)

            if (!isScanned && isScanned((decodedData.toString()))) {

                val cdd = PalletIDCustomDialog(
                    this@PerpetualConfirmActivity,
                    "Pallet ID Scanned",
                    findstockData
                )
                if (cdd.isShowing) {
                    cdd.dismiss()
                }
                cdd.show()
            } else {
                val cdd = ScannerErrorCustomDialog(
                    this@PerpetualConfirmActivity,
                    "Invalid Pallet ID",
                    decodedData.toString(), 1,
                )
                cdd.show()
            }
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



    private fun getStockList() {


        val stockInfo = WMSSharedPref.getAppPrefs(
            WMSApplication.getInstance().context
        ).stockInfo
        val singleStock =
            WMSSharedPref.getAppPrefs(WMSApplication.getInstance().context).singleStock
        header_tt1.text = singleStock!!.storageBin
        var dataList1 = stockInfo as ArrayList<PerpetualLine>

        dataList =
            dataList1.filter { it.storageBin == singleStock.storageBin && it.itemCode == singleStock.itemCode } as ArrayList<PerpetualLine>
        Recycler_view.adapter = PerpetualConfirmAdapter(this, dataList)

        var sizeList = dataList?.size.toString()
        putaway_txt_count_val.text = sizeList

        buttonYes.setOnClickListener {

            if (duplicate_dataList.size == 0) {
                ToastUtils.showToast(
                    this@PerpetualConfirmActivity,
                    "Please scan the Pallet ID"
                )
            } else {

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
                            onBackPressed()
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

        }


        buttonNo.setOnClickListener {
            finish()
        }


    }


    fun submitQuantity() {

        try {
            val qualityInfo = WMSSharedPref.getAppPrefs(
                WMSApplication.getInstance().applicationContext
            ).stocksQualityInfo

            if (qualityInfo != null) {

                var isAvailable = false

                for (i in dataList?.indices!!) {
                    if (dataList[i].packBarcodes == qualityInfo.packBarcodes) {
                        dataList.removeAt(i)
                        dataList.add(qualityInfo)
                        isAvailable = true
                    }

                }
                if (!isAvailable) {
                    dataList.add(qualityInfo)
                }

                duplicate_dataList.clear()

                for (i in dataList?.indices!!) {

                    if ((dataList[i].countedQty != null)) {
                        if (dataList[i].referenceField5.toString() == "YES") {
                            duplicate_dataList.add(dataList[i])
                        }
                    }
                }

                dataList.sortBy { it.countedQty }
                Recycler_view.adapter = PerpetualConfirmAdapter(this, dataList)
                Recycler_view.adapter!!.notifyDataSetChanged()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    override fun onResume() {
        super.onResume()
        registerReceivers()

    }


    private fun verifyToken() {

        pd = ProgressDialog(this@PerpetualConfirmActivity)
        pd.setMessage("Loading...")
        pd.show()

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

                    val stockInfo1 = WMSSharedPref.getAppPrefs(
                        WMSApplication.getInstance().context
                    ).stockInfo1

                    var request = StockPerpetualRequest()
                    request.companyCodeId = stockInfo1!!.companyCodeId
                    request.plantId = stockInfo1!!.plantId
                    request.warehouseId = stockInfo1!!.warehouseId
                    request.cycleCountTypeId = stockInfo1!!.cycleCountTypeId
                    request.cycleCountNo = stockInfo1!!.cycleCountNo
                    request.movementTypeId = stockInfo1!!.movementTypeId
                    request.subMovementTypeId = stockInfo1!!.subMovementTypeId
                    request.statusId = stockInfo1!!.statusId
                    request.referenceField1 = ""
                    request.referenceField2 = ""
                    request.referenceField3 = ""
                    request.referenceField4 = ""
                    request.referenceField5 = ""
                    request.referenceField6 = ""
                    request.referenceField7 = ""
                    request.referenceField8 = ""
                    request.referenceField9 = ""
                    request.referenceField10 = ""
                    request.deletionIndicator = stockInfo1!!.deletionIndicator
                    request.createdBy = stockInfo1!!.createdBy
                    request.createdOn = stockInfo1!!.createdOn
                    request.countedBy = stockInfo1!!.countedBy
                    request.countedOn = stockInfo1!!.countedOn
                    request.confirmedBy = stockInfo1!!.confirmedBy
                    request.confirmedOn = stockInfo1!!.confirmedOn
                    request.perpetualLine = duplicate_dataList as ArrayList<PerpetualLine>


                    val model = ViewModelProvider(this)[StockLiveData::class.java]

                    model.createRecords(request).observe(this) { vDataList ->
                        // update UI
                        if (pd != null)
                            pd.dismiss()
                        if (vDataList != null) {
                            ToastUtils.showToast(applicationContext, "Stocks Updated Successfully")
                        } else {
                            ToastUtils.showToast(applicationContext, "Unable to Update the Stocks")
                        }


                        finish()
                        PerpetualDetailsActivity.getInstance()?.reload()


                    }

                }
            }

        }


    }

    companion object {
        private var instances2: PerpetualConfirmActivity? = null
        fun getInstance(): PerpetualConfirmActivity? {
            return instances2
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

    override fun onBackPressed() {
        PerpetualListActivity.getInstance()?.reload()
        super.onBackPressed()
    }

    private fun isScanned(binID: String): Boolean {
        var returnState = false;

        val find = dataList.find { it.packBarcodes == binID }

        if (find != null) {
            findstockData = find!!
            return true
        }


        return returnState
    }

}


