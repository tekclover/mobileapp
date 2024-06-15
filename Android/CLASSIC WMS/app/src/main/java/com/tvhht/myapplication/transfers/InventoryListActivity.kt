package com.tvhht.myapplication.transfers

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
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.tvhht.myapplication.R
import com.tvhht.myapplication.cases.CaseDetailActivity
import com.tvhht.myapplication.cases.ScannerErrorCustomDialog
import com.tvhht.myapplication.home.HomePageActivity
import com.tvhht.myapplication.login.LoginLiveData
import com.tvhht.myapplication.login.model.LoginModel
import com.tvhht.myapplication.putaway.utils.AddBinCustomDialog
import com.tvhht.myapplication.putaway.utils.PalletIDCustomDialog
import com.tvhht.myapplication.transfers.adapter.InventoryAdapter
import com.tvhht.myapplication.transfers.model.CreateTransferRequest
import com.tvhht.myapplication.transfers.model.InhouseTransferLine
import com.tvhht.myapplication.transfers.model.InventoryModel
import com.tvhht.myapplication.transfers.utils.ConfirmCustomDialog
import com.tvhht.myapplication.transfers.utils.InvPalletIDCustomDialog
import com.tvhht.myapplication.transfers.utils.SearchTargetBinCustomDialog
import com.tvhht.myapplication.transfers.viewmodel.TransferLiveDataModel
import com.tvhht.myapplication.utils.*
import kotlinx.android.synthetic.main.activity_asn_no.*
import kotlinx.android.synthetic.main.activity_case_details.*
import kotlinx.android.synthetic.main.activity_case_details.txt_asn_val
import kotlinx.android.synthetic.main.activity_case_details.txt_user
import kotlinx.android.synthetic.main.activity_putaway_details.*
import kotlinx.android.synthetic.main.activity_putaway_details.Recycler_view
import kotlinx.android.synthetic.main.activity_putaway_details.barcode_value
import kotlinx.android.synthetic.main.activity_putaway_details.progress
import kotlinx.android.synthetic.main.activity_putaway_details_confirm.*
import kotlinx.android.synthetic.main.activity_putaway_details_confirm.buttonNo
import kotlinx.android.synthetic.main.activity_putaway_details_confirm.buttonYes
import kotlinx.android.synthetic.main.activity_quality_details_confirm.*
import kotlinx.android.synthetic.main.tool_bar.*
import kotlinx.android.synthetic.main.tool_bar.view.*
import java.util.*
import kotlin.collections.ArrayList

class InventoryListActivity : AppCompatActivity() {
    val myCalendar = Calendar.getInstance()
    var isScanned: Boolean = false
    var dataList: MutableList<InventoryModel> = ArrayList()
    var selecteddataList: MutableList<InventoryModel> = ArrayList()
    var str_list: MutableList<String> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_transfers)

        instances1 = this
        Recycler_view.layoutManager = LinearLayoutManager(
            this
        )

        toolbar.sign_out.setOnClickListener {
            val cdd = LogoutCustomDialog(
                this@InventoryListActivity
            )
            if (cdd.isShowing) {
                cdd.dismiss()
            }
            cdd.show()
        }


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
        txt_user.text = loginID

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
            progress.visibility = View.VISIBLE
            authTokenValidation()
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

        val shortestLength = str_list.minByOrNull { it.length!! }?.length
        if (shortestLength!! <= decodedData?.length!!) {
            if (!isScanned && isScanned((decodedData.toString()))) {
                WMSSharedPref.getAppPrefs(
                    WMSApplication.getInstance().context
                ).saveBooleanValue(
                    PrefConstant.CASE_CODE_SCANNED,
                    false
                )
                val cdd = InvPalletIDCustomDialog(
                    this@InventoryListActivity,
                    "Pallet ID Scanned",
                    decodedData.toString()
                )
                cdd.show()
            } else {
                val cdd = ScannerErrorCustomDialog(
                    this@InventoryListActivity,
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

    private fun authTokenValidation() {

        val loginKey: String =
            WMSSharedPref.getAppPrefs(WMSApplication.getInstance().context)
                .getStringValue(PrefConstant.LOGIN_USER_KEY)

        val loginID: String =
            WMSSharedPref.getAppPrefs(WMSApplication.getInstance().context)
                .getStringValue(PrefConstant.LOGIN_USER_ID)

        var request: LoginModel = LoginModel(
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

                    loadInventoryList()

                }
            }

        }


        buttonYes.setOnClickListener {
            if (selecteddataList.size == 0) {
                ToastUtils.showToast(applicationContext, "Please scan pallet ID")

            } else {

                if (selecteddataList.size == dataList.size) {
                    createInventoryRecords()
                } else {
                    val cdd = ConfirmCustomDialog(
                        this@InventoryListActivity
                    )
                    cdd.show()
                }

            }

        }

        buttonNo.setOnClickListener {
            finish()
        }


    }


    fun loadInventoryList() {

        val stringValue = WMSSharedPref.getAppPrefs(WMSApplication.getInstance().context)
            .getStringValue("SCANNED_BIN_INFO")

        txt_asn_val.text = stringValue
        var scannedBinList: MutableList<String> = ArrayList()
        scannedBinList.add(stringValue)

        val model = ViewModelProviders.of(this)[TransferLiveDataModel::class.java]
        model.getInventoryList(scannedBinList).observe(this) { vDataList ->
            // update UI
            progress.visibility = View.GONE
            if (vDataList != null) {
                val dataList2 = vDataList as MutableList<InventoryModel>

                val ware_id = WMSSharedPref.getAppPrefs(
                    WMSApplication.getInstance().context
                ).getStringValue(PrefConstant.WARE_HOUSE_ID)

                dataList =
                    dataList2.filter { it.warehouseId == ware_id } as MutableList<InventoryModel>
            }

            //putaway_txt_count_val.text = dataList!!.size.toString()

            for (cases in dataList?.indices!!) {
                str_list.add(dataList[cases].packBarcodes.toString())
            }

            Recycler_view.adapter = InventoryAdapter(this, dataList)

        }
    }

    override fun onResume() {
        super.onResume()
        getUpdatedData()
        registerReceivers()

    }


    fun getUpdatedData() {
        val qtyInfo = WMSSharedPref.getAppPrefs(
            WMSApplication.getInstance().context
        ).inventoryInfo
        isScanned =
            WMSSharedPref.getAppPrefs(WMSApplication.getInstance().context)
                .getBooleanValue(PrefConstant.CASE_CODE_SCANNED)

        if (qtyInfo != null) {
            for (i in dataList?.indices!!) {
                if (dataList[i].packBarcodes == qtyInfo.packBarcodes && qtyInfo.isSelected) {
                    dataList.removeAt(i);
                    dataList.add(qtyInfo)
                    if (!selecteddataList.contains(dataList[i]))
                        selecteddataList.add(qtyInfo)
                }
            }
            dataList.sortBy { !it.isSelected }
            Recycler_view.adapter = InventoryAdapter(this, dataList)
        }
    }


    private fun isScanned(palletID: String): Boolean {
        var returnState = false;
        for (csModel: InventoryModel in dataList!!) {
            if (csModel.packBarcodes == palletID) {
                val csDetails: InventoryModel? =
                    dataList.find {
                        it.packBarcodes == (palletID)
                    }

                WMSSharedPref.getAppPrefs(
                    WMSApplication.getInstance().context
                ).saveInventoryInfo(csDetails)
                returnState = true
                break
            }

        }


        return returnState
    }


    fun createInventoryRecords() {
        var loginID =
            WMSSharedPref.getAppPrefs(WMSApplication.getInstance().context)
                .getStringValue(PrefConstant.LOGIN_ID)
        val currentDate = DateUtil.getCurrentDate()

        var createTransferRequest = CreateTransferRequest()
        var createList: MutableList<InhouseTransferLine> = ArrayList()
        createTransferRequest.companyCodeId = dataList[0].companyCodeId
        createTransferRequest.createdby = loginID
        createTransferRequest.createdOn = currentDate
        createTransferRequest.languageId = dataList[0].languageId
        createTransferRequest.plantId = dataList[0].plantId
        createTransferRequest.warehouseId = dataList[0].warehouseId
        createTransferRequest.transferMethod = "ONESTEP"
        createTransferRequest.transferTypeId = 3


        for (i in selecteddataList?.indices!!) {
            createList.add(
                InhouseTransferLine(
                    selecteddataList[i].caseCode,
                    selecteddataList[i].companyCodeId,
                    loginID,
                    currentDate,
                    currentDate,
                    loginID,
                    selecteddataList[i].languageId,
                    selecteddataList[i].packBarcodes,
                    selecteddataList[i].palletCode,
                    selecteddataList[i].plantId,
                    selecteddataList[i].itemCode,
                    selecteddataList[i].stockTypeId,
                    selecteddataList[i].storageBin,
                    selecteddataList[i].specialStockIndicatorId,
                    selecteddataList[i].stockTypeId,
                    selecteddataList[i].itemCode,
                    selecteddataList[i].storageBin,
                    selecteddataList[i].transferQuantity,
                    selecteddataList[i].transferQuantity,
                    selecteddataList[i].inventoryUom,
                    selecteddataList[i].warehouseId
                )
            )
        }

        createTransferRequest.inhouseTransferLine = createList as ArrayList<InhouseTransferLine>

//        al cdd =
//            SearchTargetBinCustomDialog(
//                this@InventoryListActivity,
//                "Please Scan the Target Bin",
//                "", createTransferRequest
//            )
//        cdd.show()
//
//        intent.putExtra(EXTRA_PEOPLE, people)
//        return intent

        val myIntent = Intent(this@InventoryListActivity, SearchTargetBinCustomDialog::class.java)
        WMSSharedPref.getAppPrefs(
            WMSApplication.getInstance().context
        ).saveInventoryInfo(createTransferRequest)
        startActivity(myIntent)


    }

    companion object {
        private var instances1: InventoryListActivity? = null
        fun getInstance(): InventoryListActivity? {
            return instances1
        }
    }

    override fun onBackPressed() {
        HomePageActivity.getInstance()?.reload()
        super.onBackPressed()
    }

}


