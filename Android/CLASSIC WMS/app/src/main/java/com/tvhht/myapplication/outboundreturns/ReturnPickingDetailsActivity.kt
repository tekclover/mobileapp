package com.tvhht.myapplication.outboundreturns


import android.app.ProgressDialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.os.Handler
import android.provider.ContactsContract
import android.text.Editable
import android.text.TextWatcher
import android.text.method.ScrollingMovementMethod
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.PopupWindow
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.tvhht.myapplication.R
import com.tvhht.myapplication.cases.ScannerErrorCustomDialog
import com.tvhht.myapplication.home.HomePageActivity
import com.tvhht.myapplication.login.LoginLiveData
import com.tvhht.myapplication.login.model.LoginModel
import com.tvhht.myapplication.outboundreturns.adapter.PickingConfirmAdapter
import com.tvhht.myapplication.outboundreturns.utils.BinCustomDialog
import com.tvhht.myapplication.outboundreturns.utils.HeNoCustomDialog
import com.tvhht.myapplication.outboundreturns.utils.ItemCodeCustomDialog
import com.tvhht.myapplication.outboundreturns.utils.PalletIDCustomDialog
import com.tvhht.myapplication.picking.model.PickingCombineResponse
import com.tvhht.myapplication.picking.model.PickingQuantityConfirm
import com.tvhht.myapplication.picking.model.PickingSubmitRequest
import com.tvhht.myapplication.picking.model.PickingSubmitResponse
import com.tvhht.myapplication.picking.utils.AddPalletIDCustomDialog
import com.tvhht.myapplication.picking.viewmodel.PickingLiveDataModel
import com.tvhht.myapplication.utils.*
import kotlinx.android.synthetic.main.activity_picking_details.*
import kotlinx.android.synthetic.main.activity_picking_details.Recycler_view
import kotlinx.android.synthetic.main.activity_picking_details.progress
import kotlinx.android.synthetic.main.activity_picking_details_confirm.*
import kotlinx.android.synthetic.main.activity_picking_details_confirm.barcode_value
import kotlinx.android.synthetic.main.activity_picking_details_confirm.buttonNo
import kotlinx.android.synthetic.main.activity_picking_details_confirm.buttonYes
import kotlinx.android.synthetic.main.activity_quality_details_confirm.*
import kotlinx.android.synthetic.main.tool_bar.*
import kotlinx.android.synthetic.main.tool_bar.view.*

class ReturnPickingDetailsActivity : AppCompatActivity() {

    lateinit var pd: ProgressDialog

    var dataList = MutableLiveData<List<PickingCombineResponse>>()
    lateinit var duplicate_dataList: List<PickingCombineResponse>
    var dataQtyList: MutableList<PickingQuantityConfirm> = ArrayList()
    var dataQtyListFinal: MutableList<PickingQuantityConfirm> = ArrayList()
    lateinit var pickingInfo: PickingCombineResponse
    var request: MutableList<PickingSubmitRequest> = ArrayList()
    var proposedBin = ""
    var isScanned: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_picking_details_confirm)
        Recycler_view.layoutManager = LinearLayoutManager(
            this
        )

        instances2 = this
        header_tt_details.text = getString(R.string.picking_returns)
        proposedBin = intent?.getStringExtra("DEFAULT_BIN_NO")!!
        toolbar.sign_out.setOnClickListener {
            val cdd = LogoutCustomDialog(
                this@ReturnPickingDetailsActivity
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

        WMSSharedPref.getAppPrefs(WMSApplication.getInstance().context)
            .saveBooleanValue("REMAINING_QTY_BOOL", false)
        WMSSharedPref.getAppPrefs(
            WMSApplication.getInstance().context
        ).clearSharedPrefs("PICKING_QUANTITY_INFO_LIST_MUL")

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

            loadData()
        }

        Recycler_view.addItemDecoration(
            DividerItemDecoration(
                Recycler_view.context,
                DividerItemDecoration.VERTICAL
            )
        )

        sku_desp.movementMethod = ScrollingMovementMethod()

    }


    private fun loadData() {

        pd = ProgressDialog(this@ReturnPickingDetailsActivity)
        pd.setMessage("Loading...")

        var loginID =
            WMSSharedPref.getAppPrefs(WMSApplication.getInstance().context)
                .getStringValue(PrefConstant.LOGIN_ID)
        putaway_detail_txt_user.text = loginID

        pickingInfo = WMSSharedPref.getAppPrefs(
            WMSApplication.getInstance().context
        ).pickingInfo!!

        WMSSharedPref.getAppPrefs(WMSApplication.getInstance().context)
            .clearSharedPrefs("PICKING_QUANTITY_INFO_LIST")

        dataQtyList.add(
            PickingQuantityConfirm(
                0,
                pickingInfo?.pickToQty,
                pickingInfo?.proposedPackBarCode,
                pickingInfo?.proposedStorageBin,
                pickingInfo?.outboundOrderTypeId,
                "",
                pickingInfo.itemCode,
                false
            )
        )


        itemCode.text = pickingInfo?.itemCode.toString()
        mfr.text = pickingInfo?.manufacturerPartNo
        totalQTY.text = pickingInfo?.pickToQty.toString()
        sku_desp.text = pickingInfo?.description.toString()
        remain_qty_header.text = "Rem Qty : "
        remain_qty.text = pickingInfo?.pickToQty.toString()
        Recycler_view.adapter = PickingConfirmAdapter(this, dataQtyList)


        WMSSharedPref.getAppPrefs(applicationContext)
            .saveStringValue("ALLOC_QTY", pickingInfo?.pickToQty.toString());


        sku_desp.setOnClickListener {

            displayPopupWindow(it, pickingInfo?.description.toString())
        }

        buttonYes.setOnClickListener {

            if (dataQtyListFinal.size == 0) {
                ToastUtils.showToast(
                    this@ReturnPickingDetailsActivity,
                    "Please scan the Bin"
                )
            } else {
                val myIntent =
                    Intent(this@ReturnPickingDetailsActivity, HeNoCustomDialog::class.java)
                startActivity(myIntent)
            }

        }

        buttonNo.setOnClickListener {
            finish()
        }

        addNewBIN.setOnClickListener {

            if (remain_qty.text.toString().toInt() == 0) {

                ToastUtils.showToast(applicationContext, "Remaining Qty is 0")
            } else {

                loadAddBinData()
            }
        }


    }

    fun loadAddBinData() {
        WMSSharedPref.getAppPrefs(WMSApplication.getInstance().context)
            .clearSharedPrefs("PICKING_QUANTITY_INFO_LIST")

        if (dataQtyListFinal.size > 0) {
            WMSSharedPref.getAppPrefs(WMSApplication.getInstance().context)
                .savePickingQTYInfo(dataQtyListFinal[0])
        } else {
            dataQtyListFinal.add(
                PickingQuantityConfirm(
                    0,
                    pickingInfo.inventoryQuantity,
                    pickingInfo.proposedPackBarCode,
                    pickingInfo.proposedStorageBin,
                    pickingInfo.outboundOrderTypeId,
                    "",
                    pickingInfo.itemCode,
                    false
                )
            )
            WMSSharedPref.getAppPrefs(WMSApplication.getInstance().context)
                .savePickingQTYInfo(dataQtyListFinal[0])
        }


        val myIntent =
            Intent(this@ReturnPickingDetailsActivity, ReturnPickingBinDetailsActivity::class.java)
        myIntent.putExtra("REMAIN_QTY", remain_qty.text.toString())
        myIntent.putExtra("TOTAL_QTY", pickingInfo?.inventoryQuantity.toString())
        startActivity(myIntent)


    }


    companion object {
        private var instances2: ReturnPickingDetailsActivity? = null
        fun getInstance(): ReturnPickingDetailsActivity? {
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

        if (remain_qty.text.toString().toInt() == 0) {
            ToastUtils.showToast(applicationContext, "Remaining Qty is 0")
        } else {
            if (pickingInfo?.proposedStorageBin?.length == decodedData?.length) {

                if (decodedData.toString() == pickingInfo?.proposedStorageBin) {
                    isScanned =
                        WMSSharedPref.getAppPrefs(WMSApplication.getInstance().context)
                            .getBooleanValue(PrefConstant.CASE_CODE_SCANNED)
                    WMSSharedPref.getAppPrefs(WMSApplication.getInstance().context)
                        .saveStringValue("REMAINING_QTY", remain_qty.text.toString())

                    val find = dataQtyList.find {
                        it.binLocation.equals(decodedData.toString())
                    }
                    WMSSharedPref.getAppPrefs(
                        WMSApplication.getInstance().context
                    ).savePickingQTYInfo(find)!!
                    val cdd =
                        BinCustomDialog(
                            this@ReturnPickingDetailsActivity,
                            "Bin Number Scanned",
                            decodedData.toString()
                        )
                    cdd.show()
                } else {
                    val cdd = ScannerErrorCustomDialog(
                        this@ReturnPickingDetailsActivity,
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
            Intent(this@ReturnPickingDetailsActivity, PalletIDCustomDialog::class.java)
        myIntent.putExtra("EXTRA_PALLET_CODE",findList!!.palletId)
        startActivity(myIntent)
    }


    fun verifyItemCode() {


        val stringValue = WMSSharedPref.getAppPrefs(WMSApplication.getInstance().context)
            .getStringValue("OLD_BIN_VAL")

        var findList = dataQtyList.find {
            it.binLocation.equals(stringValue)
        }

        val myIntent =
            Intent(this@ReturnPickingDetailsActivity, ItemCodeCustomDialog::class.java)
        myIntent.putExtra("EXTRA_ITEM_CODE",findList!!.itemCode)
        startActivity(myIntent)
    }


    override fun onResume() {
        loadFromCache()
        super.onResume()
        registerReceivers()

    }


    fun loadFromCache() {
        Log.d("ON RESUME", "Calling")

        try {
            val qtyInfo = WMSSharedPref.getAppPrefs(
                WMSApplication.getInstance().context
            ).listPickingQTYInfo
            val qtyInfo2 = ArrayList<PickingQuantityConfirm>()
            if (qtyInfo != null) {
                var isAvailable = false
                var isSelctedFrom = false

                dataQtyList.clear()
                dataQtyList.add(
                    PickingQuantityConfirm(
                        0,
                        pickingInfo?.inventoryQuantity,
                        pickingInfo?.proposedPackBarCode,
                        pickingInfo?.proposedStorageBin,
                        pickingInfo?.outboundOrderTypeId,
                        "",
                        pickingInfo.itemCode,
                        false
                    )
                )


                for (i in dataQtyList?.indices!!) {

                    for (j in qtyInfo?.indices!!) {
                        if (dataQtyList[i].binLocation == qtyInfo[j].binLocation && dataQtyList[i].palletId == qtyInfo[j].palletId) {
                            val putawayConfirmedQty = dataQtyList[i].pickedQty!!
                            val putawayConfirmedQty1 = qtyInfo[j].pickedQty!!
                            isSelctedFrom = qtyInfo[j].isSelected!!;
                            val sumCount = putawayConfirmedQty + putawayConfirmedQty1
                            dataQtyList.removeAt(i)
                            dataQtyList.add(
                                PickingQuantityConfirm(
                                    qtyInfo[j].pickedQty,
                                    qtyInfo[j].inventoryQty,
                                    qtyInfo[j].palletId,
                                    qtyInfo[j].binLocation,
                                    qtyInfo[j].outboundOrderTypeId,
                                    qtyInfo[j].heNumber,
                                    qtyInfo[j].itemCode,
                                    isSelctedFrom
                                )
                            )
                            isAvailable = true
                        }
                        else {
                            qtyInfo2.add(qtyInfo[j])
                        }
                    }

                }
                if (!isAvailable) {
                    dataQtyList.addAll(qtyInfo)
                } else {
                    dataQtyList.addAll(qtyInfo2)
                }
                getCurrentSum()
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



        remain_qty_header.text = "Rem Qty : "
        remain_qty.text = balanceQty1.toString()

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

    fun checkHandlingEquipmentID(stringHe: String) {
        pd.show()
        val model = ViewModelProvider(this)[PickingLiveDataModel::class.java]
        model.getEquipment(stringHe).observe(this) { HEstatus ->

            if ((HEstatus == true)) {
                createPickingRecords(stringHe)
            } else {
                if (pd != null) pd.dismiss()

                ToastUtils.showToast(applicationContext, "HE Number not Available")
                finish()
                ReturnPickingListActivity.getInstance()?.reload()

            }
        }
    }


    fun createPickingRecords(stringHe: String) {

        // pd.show()


        val currentDate = DateUtil.getCurrentDate();
        var loginID =
            WMSSharedPref.getAppPrefs(WMSApplication.getInstance().context)
                .getStringValue(PrefConstant.LOGIN_ID)
        var strBoolean =
            WMSSharedPref.getAppPrefs(WMSApplication.getInstance().context)
                .getBooleanValue("REMAINING_QTY_BOOL")

        for (i in dataQtyList?.indices!!) {
            var statusID = 0
            var allocQty = 0
            allocQty = if (dataQtyList[i].binLocation.equals(proposedBin)) {
                pickingInfo.pickToQty!!
            } else {
                0
            }

            statusID = if (dataQtyList[i].isSelected == false)
                50
            else {
                if (dataQtyList[i].pickedQty == 0)
                    51
                else
                    50
            }

            request.add(
                PickingSubmitRequest(1,
                    stringHe,
                    loginID,
                    "",
                    pickingInfo.companyCodeId!!.toInt(),
                    0,
                    pickingInfo.description,
                    pickingInfo.itemCode,
                    pickingInfo.languageId,
                    pickingInfo.lineNumber,
                    pickingInfo.manufacturerPartNo,
                    pickingInfo.partnerCode,
                    "",
                    dataQtyList[i].pickedQty,
                    allocQty,
                    "",
                    pickingInfo.pickToQty.toString(),
                    pickingInfo.pickUom,
                    dataQtyList[i].palletId,
                    dataQtyList[i].binLocation,
                    loginID,
                    currentDate,
                    loginID,
                    currentDate,
                    pickingInfo.pickupNumber,
                    "",
                    "",
                    "",
                    "",
                    pickingInfo.plantId,
                    pickingInfo.preOutboundNo,
                    pickingInfo.refDocNumber,
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    pickingInfo.specialStockIndicatorId,
                    statusID,
                    pickingInfo.stockTypeId,
                    0,
                    "",
                    pickingInfo.warehouseId
                )
            )
        }

        if (!NetworkUtils().haveNetworkConnection(applicationContext)) {
            ToastUtils.showToast(applicationContext, getString(R.string.internet_check_msg_store_offline))

            WMSApplication.getInstance().submitLocalRepository
                    ?.insertPickingListVo(request)
        } else {

            verifyToken()
        }
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
        val model = ViewModelProvider(this)[LoginLiveData::class.java]
        model.getLoginStatus(request).observe(this) { asnList ->
            // update UI
            if (asnList.equals("ERROR")) {
                ToastUtils.showToast(applicationContext, "User not Available")
            } else {
                if (asnList.equals("FAILED")) {
                    ToastUtils.showToast(applicationContext, "No Data Available")
                } else {

                    createRecords()

                }
            }

        }


    }

    private fun createRecords() {
        val model = ViewModelProvider(this)[PickingLiveDataModel::class.java]



        model.confirmPicking(request).observe(this) { vDataList ->
            // update UI
            if (pd != null) pd.dismiss()

            processRequest(vDataList)


        }
    }

    private fun processRequest(vDataList: List<PickingSubmitResponse>?) {

        if (vDataList != null && vDataList.isNotEmpty()) {
            ToastUtils.showToast(
                applicationContext,
                "Picking Submitted Successfully "
            )

            finish()

            ReturnPickingListActivity.getInstance()?.reload()
        } else {
            ToastUtils.showToast(
                applicationContext,
                "Unable to Submit Picking Details"
            )
        }


    }

    private fun displayPopupWindow(anchorView: View, msg: String) {
        val popup: PopupWindow = PopupWindow(this@ReturnPickingDetailsActivity)
        val layout: View = layoutInflater.inflate(R.layout.popup_content, null)
        popup.contentView = layout
        val txt = layout.findViewById<TextView>(R.id.text_display)
        txt.text = msg
        popup.height = WindowManager.LayoutParams.WRAP_CONTENT
        popup.width = WindowManager.LayoutParams.WRAP_CONTENT
        popup.isOutsideTouchable = true
        popup.isFocusable = true
        popup.setBackgroundDrawable(BitmapDrawable())
        popup.showAsDropDown(anchorView)
    }


}
