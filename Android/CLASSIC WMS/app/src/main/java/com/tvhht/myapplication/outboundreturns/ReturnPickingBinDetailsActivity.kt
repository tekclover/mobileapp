package com.tvhht.myapplication.outboundreturns


import android.app.ProgressDialog
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
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.tvhht.myapplication.R
import com.tvhht.myapplication.cases.ScannerErrorCustomDialog
import com.tvhht.myapplication.home.HomePageActivity
import com.tvhht.myapplication.login.LoginLiveData
import com.tvhht.myapplication.login.model.LoginModel
import com.tvhht.myapplication.outboundreturns.adapter.PickingConfirmAdapter
import com.tvhht.myapplication.outboundreturns.utils.AddBinCustomDialog
import com.tvhht.myapplication.outboundreturns.utils.AddItemCodeCustomDialog
import com.tvhht.myapplication.outboundreturns.utils.AddPalletIDCustomDialog
import com.tvhht.myapplication.picking.model.BinResponse
import com.tvhht.myapplication.picking.model.PickingCombineResponse
import com.tvhht.myapplication.picking.model.PickingQuantityConfirm

import com.tvhht.myapplication.picking.viewmodel.PickingLiveDataModel
import com.tvhht.myapplication.utils.*
import kotlinx.android.synthetic.main.activity_picking_bin_confirm.*
import kotlinx.android.synthetic.main.tool_bar.*
import kotlinx.android.synthetic.main.tool_bar.view.*

class ReturnPickingBinDetailsActivity : AppCompatActivity() {

    lateinit var pd: ProgressDialog

    var dataList = MutableLiveData<List<PickingCombineResponse>>()
    lateinit var duplicate_dataList: List<PickingCombineResponse>
    var dataQtyList: MutableList<PickingQuantityConfirm> = ArrayList()
    var dataQtyListTemp: MutableList<PickingQuantityConfirm> = ArrayList()
    var dataQtyListFinal: MutableList<PickingQuantityConfirm> = ArrayList()
    var pickingBin_data: List<BinResponse> = ArrayList()
    lateinit var pickingInfo: PickingCombineResponse


    var remaining_qty: String = "0"
    var allocated_qty: String = "0"
    var isScanned: Boolean = false
    lateinit var preferItemCode: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_picking_bin_confirm)
        Recycler_view.layoutManager = LinearLayoutManager(
            this
        )

        instances2 = this
        header_tt_details.text = getString(R.string.picking_returns)
        toolbar.sign_out.setOnClickListener {
            val cdd = LogoutCustomDialog(
                this@ReturnPickingBinDetailsActivity
            )
            if (cdd.isShowing) {
                cdd.dismiss()
            }
            cdd.show()
        }

        remaining_qty = intent.getStringExtra("REMAIN_QTY").toString()
        allocated_qty = WMSSharedPref.getAppPrefs(applicationContext).getStringValue("ALLOC_QTY");



        WMSSharedPref.getAppPrefs(WMSApplication.getInstance().context)
            .saveStringValue("REMAINING_QTY", remaining_qty)


        remain_qty.text = "Rem Qty- $remaining_qty"
        allocate_qty.text = "Allocated Qty- $allocated_qty"

        WMSSharedPref.getAppPrefs(
            WMSApplication.getInstance().context
        ).saveBooleanValue(
            PrefConstant.CASE_CODE_SCANNED,
            false
        )

        loadData()

        Recycler_view.addItemDecoration(
            DividerItemDecoration(
                Recycler_view.context,
                DividerItemDecoration.VERTICAL
            )
        )


    }


    private fun loadData() {

        pd = ProgressDialog(this@ReturnPickingBinDetailsActivity)
        pd.setMessage("Loading...")

        var loginID =
            WMSSharedPref.getAppPrefs(WMSApplication.getInstance().context)
                .getStringValue(PrefConstant.LOGIN_ID)
        putaway_detail_txt_user.text = loginID


        pickingInfo = WMSSharedPref.getAppPrefs(
            WMSApplication.getInstance().context
        ).pickingInfo!!

        buttonYes.setOnClickListener {

            if (dataQtyListFinal.size == 0) {
                ToastUtils.showToast(
                    this@ReturnPickingBinDetailsActivity,
                    "Please scan the Bin"
                )
            } else {
                var dataQtyList2: MutableList<PickingQuantityConfirm> = ArrayList()

                for (i in dataQtyList.indices) {
                    if (dataQtyList[i].isSelected == true) {
                        dataQtyList2.add(dataQtyList[i])
                    }
                }
                WMSSharedPref.getAppPrefs(
                    WMSApplication.getInstance().context
                ).saveListPickingQTYInfo(dataQtyList2)
                finish()
            }

        }

        buttonNo.setOnClickListener {
            finish()
        }

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


    companion object {
        private var instances2: ReturnPickingBinDetailsActivity? = null
        fun getInstance(): ReturnPickingBinDetailsActivity? {
            return instances2
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

        if (remaining_qty.toInt() == 0) {
            ToastUtils.showToast(applicationContext, "Remaining Qty is 0")
        } else {
            if (dataQtyList[0].binLocation!!.length == decodedData?.length) {

                isScanned =
                    WMSSharedPref.getAppPrefs(WMSApplication.getInstance().context)
                        .getBooleanValue(PrefConstant.CASE_CODE_SCANNED)

                if (!isScanned && isScanned((decodedData.toString()))) {


                    val cdd =
                        AddBinCustomDialog(
                            this@ReturnPickingBinDetailsActivity,
                            "Bin Number Scanned",
                            decodedData.toString()
                        )
                    cdd.show()
                } else {
                    val cdd = ScannerErrorCustomDialog(
                        this@ReturnPickingBinDetailsActivity,
                        "Invalid Bin Number",
                        decodedData.toString(), 1,
                    )
                    cdd.show()
                }

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

    fun loadPalletID(string: String) {
        var findList = dataQtyList.find {
            it.binLocation.equals(string)
        }


        val myIntent =
            Intent(this@ReturnPickingBinDetailsActivity, AddPalletIDCustomDialog::class.java)
             myIntent.putExtra("PALLET_ID_NN",findList?.palletId)
        startActivity(myIntent)
    }


    fun verifyItemCode(palletID: String) {


        val stringValue = WMSSharedPref.getAppPrefs(WMSApplication.getInstance().context)
            .getStringValue("OLD_BIN_VAL")

        var findList = dataQtyList.find {
            it.binLocation.equals(stringValue) && it.palletId.equals(palletID)
        }

        if (findList != null) {
            WMSSharedPref.getAppPrefs(
                WMSApplication.getInstance().context
            ).savePickingQTYInfo(findList)!!
        }

        val cdd =
            AddItemCodeCustomDialog(
                this@ReturnPickingBinDetailsActivity,
                "Verify Item Code", findList!!.itemCode

            )
        cdd.show()
    }


    private fun verifyToken() {
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

                    getPickingList()

                }
            }

        }


    }


    private fun getPickingList() {

        val qtyInfo = WMSSharedPref.getAppPrefs(
            WMSApplication.getInstance().context
        ).pickingQTYInfo!!

        preferItemCode = qtyInfo.itemCode.toString()

        val putawayConfirmedQty1 = qtyInfo.pickedQty!!
        val model = ViewModelProviders.of(this)[PickingLiveDataModel::class.java]

        model.getPickingBin(preferItemCode, qtyInfo).observe(this) { vDataList ->
            // update UI

            if (vDataList != null && vDataList.isNotEmpty()) {
                progress.visibility = View.GONE
                pickingBin_data = (vDataList as List<BinResponse>?)!!

                for (i in pickingBin_data.indices) {
                    dataQtyListTemp.add(
                        PickingQuantityConfirm(
                            0,
                            pickingBin_data[i].inventoryQuantity,
                            pickingBin_data[i].packBarcodes,
                            pickingBin_data[i].storageBin,
                            0,
                            "",
                            pickingBin_data[i].itemCode,
                            false
                        )
                    )
                }


                var isAvailable = false
                var isAvailable2 = false

                val qtyInfo2 = WMSSharedPref.getAppPrefs(
                    WMSApplication.getInstance().context
                ).listPickingQTYInfo


                if (qtyInfo2.isNullOrEmpty()) {

                    for (i in dataQtyListTemp?.indices!!) {
                        if (dataQtyListTemp[i].binLocation == qtyInfo.binLocation && dataQtyListTemp[i].palletId == qtyInfo.palletId && dataQtyListTemp[i].itemCode == qtyInfo.itemCode) {

                            val putawayConfirmedQty1 = qtyInfo.pickedQty!!

                            dataQtyList.add(
                                PickingQuantityConfirm(
                                    putawayConfirmedQty1,
                                    dataQtyListTemp[i].inventoryQty,
                                    dataQtyListTemp[i].palletId,
                                    dataQtyListTemp[i].binLocation,
                                    dataQtyListTemp[i].outboundOrderTypeId,
                                    dataQtyListTemp[i].heNumber,
                                    dataQtyListTemp[i].itemCode,
                                    qtyInfo.isSelected
                                )
                            )



                            isAvailable = true
                        }

                    }
                    if (!isAvailable) {
                        dataQtyList.add(qtyInfo)
                    }

                    for (i in dataQtyListTemp?.indices!!) {
                        if (!dataQtyList.contains(dataQtyListTemp[i]))
                            dataQtyList.add(dataQtyListTemp[i])
                    }

                } else {

                    for (i in dataQtyListTemp?.indices!!) {
                        if (dataQtyListTemp[i].binLocation == qtyInfo.binLocation && dataQtyListTemp[i].palletId == qtyInfo.palletId && dataQtyListTemp[i].itemCode == qtyInfo.itemCode) {
                            isAvailable2 = true
                        }

                    }
                    if (!isAvailable2) {
                        dataQtyListTemp.add(
                            PickingQuantityConfirm(
                                0,
                                qtyInfo.inventoryQty,
                                qtyInfo.palletId,
                                qtyInfo.binLocation,
                                qtyInfo.outboundOrderTypeId,
                                qtyInfo.heNumber,
                                qtyInfo.itemCode,
                                false
                            )
                        )
                    }

                    dataQtyList.addAll(dataQtyListTemp)

                    for (i in dataQtyListTemp?.indices!!) {

                        for (j in qtyInfo2?.indices!!) {
                            if (dataQtyListTemp[i].binLocation == qtyInfo2[j].binLocation && dataQtyListTemp[i].palletId == qtyInfo2[j].palletId && dataQtyListTemp[i].itemCode == qtyInfo2[j].itemCode) {

                                dataQtyList.removeAt(i)
                                dataQtyList.add(
                                    PickingQuantityConfirm(
                                        qtyInfo2[j].pickedQty,
                                        qtyInfo2[j].inventoryQty,
                                        qtyInfo2[j].palletId,
                                        qtyInfo2[j].binLocation,
                                        qtyInfo2[j].outboundOrderTypeId,
                                        qtyInfo2[j].heNumber,
                                        qtyInfo2[j].itemCode,
                                        qtyInfo2[j].isSelected
                                    )
                                )

                            }
                        }

                    }

                }
                dataQtyList.sortBy { it.isSelected == false }
                Recycler_view.adapter = PickingConfirmAdapter(this, dataQtyList)
            } else {
                progress.visibility = View.GONE
                ToastUtils.showToast(applicationContext, "No Additional Bin are Available")
            }

        }
    }


    override fun onResume() {
        //  loadFromCache()
        super.onResume()
        registerReceivers()

    }


    private fun isScanned(binID: String): Boolean {
        var returnState = false;

        val find = dataQtyList.find { it.binLocation == binID }
        if (find != null) {
            return true
        }


        return returnState
    }


    fun loadFromCache() {

        try {
            val qtyInfo = WMSSharedPref.getAppPrefs(
                WMSApplication.getInstance().context
            ).pickingQTYInfo!!


            if (qtyInfo != null) {

                var isAvailable = false

                for (i in dataQtyList?.indices!!) {
                    if (dataQtyList[i].binLocation == qtyInfo.binLocation && dataQtyList[i].palletId == qtyInfo.palletId) {
                        val putawayConfirmedQty = dataQtyList[i].pickedQty!!
                        val putawayConfirmedQty1 = qtyInfo.pickedQty!!

                        dataQtyList.removeAt(i)
                        dataQtyList.add(
                            PickingQuantityConfirm(
                                qtyInfo.pickedQty,
                                qtyInfo.inventoryQty,
                                qtyInfo.palletId,
                                qtyInfo.binLocation,
                                qtyInfo.outboundOrderTypeId,
                                qtyInfo.heNumber,
                                qtyInfo.itemCode,
                                true
                            )
                        )

                        isAvailable = true
                    }

                }
                if (!isAvailable) {
                    dataQtyList.add(qtyInfo)
                }
                getCurrentSum()
                dataQtyList.sortBy { it.isSelected == false }
                Recycler_view.adapter = PickingConfirmAdapter(this, dataQtyList)
                Recycler_view.adapter!!.notifyDataSetChanged()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }


    }

    fun getCurrentSum() {
        var myIntList: MutableList<Int> = ArrayList<Int>()
        for (i in dataQtyList.indices) {
            myIntList.add(dataQtyList[i].pickedQty!!)
        }

        val sum = myIntList.sum()

        var balanceQty1 = pickingInfo?.pickToQty?.minus(sum)

        remain_qty.text = "Rem Qty- $balanceQty1"


        WMSSharedPref.getAppPrefs(WMSApplication.getInstance().context)
            .saveStringValue("REMAINING_QTY", balanceQty1.toString())


        WMSSharedPref.getAppPrefs(WMSApplication.getInstance().context)
            .saveBooleanValue("REMAINING_QTY_BOOL", true)


        dataQtyListFinal.clear()
        dataQtyListFinal.add(
            PickingQuantityConfirm(
                sum,
                pickingInfo.pickToQty,
                pickingInfo.proposedPackBarCode,
                pickingInfo.proposedStorageBin,
                pickingInfo.outboundOrderTypeId,
                "",
                pickingInfo.itemCode,
                false
            )
        )

    }


}
