package com.tvhht.myapplication.picking


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
import android.text.method.ScrollingMovementMethod
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.PopupWindow
import android.widget.TextView
import android.widget.Toast
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
import com.tvhht.myapplication.picking.adapter.PickingConfirmAdapter
import com.tvhht.myapplication.picking.model.PickingInventory
import com.tvhht.myapplication.picking.model.PickingListResponse
import com.tvhht.myapplication.picking.model.PickingQuantityConfirm
import com.tvhht.myapplication.picking.model.PickingSubmitRequest
import com.tvhht.myapplication.picking.model.PickingSubmitResponse
import com.tvhht.myapplication.picking.utils.BinCustomDialog
import com.tvhht.myapplication.picking.utils.GlobalInventoryDialog
import com.tvhht.myapplication.picking.utils.HeNoCustomDialog
import com.tvhht.myapplication.picking.utils.ItemCodeCustomDialog
import com.tvhht.myapplication.picking.utils.PalletIDCustomDialog
import com.tvhht.myapplication.picking.viewmodel.PickingLiveDataModel
import com.tvhht.myapplication.utils.ApiConstant
import com.tvhht.myapplication.utils.DateUtil
import com.tvhht.myapplication.utils.LogoutFragment
import com.tvhht.myapplication.utils.NetworkUtils
import com.tvhht.myapplication.utils.PrefConstant
import com.tvhht.myapplication.utils.ToastUtils
import com.tvhht.myapplication.utils.WMSApplication
import com.tvhht.myapplication.utils.WMSSharedPref
import kotlinx.android.synthetic.main.activity_picking_details.Recycler_view
import kotlinx.android.synthetic.main.activity_picking_details.progress
import kotlinx.android.synthetic.main.activity_picking_details_confirm.addNewBIN
import kotlinx.android.synthetic.main.activity_picking_details_confirm.buttonNo
import kotlinx.android.synthetic.main.activity_picking_details_confirm.buttonYes
import kotlinx.android.synthetic.main.activity_picking_details_confirm.globalInventory
import kotlinx.android.synthetic.main.activity_picking_details_confirm.header_tt_details
import kotlinx.android.synthetic.main.activity_picking_details_confirm.itemCode
import kotlinx.android.synthetic.main.activity_picking_details_confirm.mfr
import kotlinx.android.synthetic.main.activity_picking_details_confirm.putaway_detail_txt_user
import kotlinx.android.synthetic.main.activity_picking_details_confirm.remain_qty
import kotlinx.android.synthetic.main.activity_picking_details_confirm.remain_qty_header
import kotlinx.android.synthetic.main.activity_picking_details_confirm.sku_desp
import kotlinx.android.synthetic.main.activity_picking_details_confirm.totalQTY
import kotlinx.android.synthetic.main.tool_bar.toolbar
import kotlinx.android.synthetic.main.tool_bar.view.sign_out

class PickingDetailsActivity : AppCompatActivity() {

    lateinit var pd: ProgressDialog

    var dataList = MutableLiveData<List<PickingListResponse>>()
    lateinit var duplicate_dataList: List<PickingListResponse>
    var dataQtyList: MutableList<PickingQuantityConfirm> = ArrayList()
    var dataQtyListFinal: MutableList<PickingQuantityConfirm> = ArrayList()
    lateinit var pickingInfo: PickingListResponse
    var request: MutableList<PickingSubmitRequest> = ArrayList()
    private var proposedBin = ""
    private var proposedPalletID = ""
    var isScanned: Boolean = false
    private var isError: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_picking_details_confirm)
        Recycler_view.layoutManager = LinearLayoutManager(
            this
        )

        instances2 = this
        header_tt_details.text = getString(R.string.picking_txt)
        proposedBin = intent?.getStringExtra("DEFAULT_BIN_NO") ?: ""
        proposedPalletID = intent?.getStringExtra("DEFAULT_PALLET_NO") ?: ""
        toolbar.sign_out.setOnClickListener {
            if (supportFragmentManager.findFragmentByTag("logout_fragment") == null) {
                val logoutFragment = LogoutFragment()
                supportFragmentManager.beginTransaction().add(logoutFragment, "logout_fragment")
                    .commitAllowingStateLoss()
            }
        }

        WMSSharedPref.getAppPrefs(
            WMSApplication.getInstance().context
        ).saveBooleanValue(
            PrefConstant.CASE_CODE_SCANNED,
            false
        )

        WMSSharedPref.getAppPrefs(
            WMSApplication.getInstance().context
        ).clearSharedPrefs("PICKING_QUANTITY_INFO_LIST_MUL")


        WMSSharedPref.getAppPrefs(WMSApplication.getInstance().context)
            .saveBooleanValue("REMAINING_QTY_BOOL", false)

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


        pd = ProgressDialog(this@PickingDetailsActivity)
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
                pickingInfo?.inventoryQuantity,
                pickingInfo?.proposedPackBarCode,
                pickingInfo?.proposedStorageBin,
                pickingInfo?.outboundOrderTypeId,
                "",
                pickingInfo.itemCode,
                false,
                "",
                pickingInfo.barcodeId
            )
        )


        itemCode.text = pickingInfo.itemCode.toString()
        mfr.text = pickingInfo.manufacturerName?:""
        totalQTY.text = pickingInfo.pickToQty.toString()
        sku_desp.text = pickingInfo.description.toString()



        WMSSharedPref.getAppPrefs(applicationContext)
            .saveStringValue("ALLOC_QTY", pickingInfo.pickToQty.toString());

        sku_desp.setOnClickListener {
            displayPopupWindow(it, pickingInfo.description.toString())
        }
        remain_qty_header.text = "Rem Qty : "
        remain_qty.text = pickingInfo.pickToQty.toString()
        Recycler_view.adapter = PickingConfirmAdapter(this, dataQtyList)

        buttonYes.setOnClickListener {

            if (dataQtyListFinal.size == 0) {
                ToastUtils.showToast(
                    this@PickingDetailsActivity,
                    "Please scan the Bin"
                )
            } else {

                if (dataQtyList.size == 1 && dataQtyList[0].isSelected == false) {
                    ToastUtils.showToast(
                        this@PickingDetailsActivity,
                        "Please scan the Bin"
                    )

                } else {
                    val myIntent =
                        Intent(this@PickingDetailsActivity, HeNoCustomDialog::class.java)
                    startActivity(myIntent)
                }


            }

        }

        buttonNo.setOnClickListener {
           navigateToHome()
        }

        addNewBIN.setOnClickListener {

            if (remain_qty.text.toString().toInt() == 0) {

                ToastUtils.showToast(applicationContext, "Remaining Qty is 0")
            } else {

                loadAddBinData()
            }
        }
        globalInventory.setOnClickListener {
            getGlobalInventoryList()
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
                    false,
                    "",
                    pickingInfo.barcodeId
                )
            )
            WMSSharedPref.getAppPrefs(WMSApplication.getInstance().context)
                .savePickingQTYInfo(dataQtyListFinal[0])
        }


        val myIntent = Intent(this@PickingDetailsActivity, PickingBinDetailsActivity::class.java)
        myIntent.putExtra("REMAIN_QTY", remain_qty.text.toString())
        myIntent.putExtra("TOTAL_QTY", pickingInfo?.inventoryQuantity.toString())
        startActivity(myIntent)


    }


    companion object {
        private var instances2: PickingDetailsActivity? = null
        fun getInstance(): PickingDetailsActivity? {
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
            initiatingIntent.getStringExtra("com.symbol.datawedge.data_string")?.replace(" ", "")
        val decodedLabelType =
            initiatingIntent.getStringExtra("com.symbol.datawedge.label_type")
        Log.i("LOG", "DataWedge Scanned Code: $decodedData")
        Log.i("LOG", "DataWedge decodedLabelType: $decodedLabelType")

        when {
            remain_qty.text.toString().toInt() == 0 -> {
                ToastUtils.showToast(applicationContext, "Remaining Qty is 0")
            }

            (pickingInfo.proposedStorageBin ?: "") == (decodedData ?: "") -> {
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
                        this@PickingDetailsActivity,
                        "Bin Number Scanned",
                        decodedData.toString()
                    )
                cdd.show()
            }

            else -> {
                val cdd = ScannerErrorCustomDialog(
                    this@PickingDetailsActivity,
                    "Invalid Bin Number",
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


    fun loadPalletID(string: String) {

        var findList = dataQtyList.find {
            it.binLocation.equals(string)
        }
        val myIntent =
            Intent(this@PickingDetailsActivity, PalletIDCustomDialog::class.java)
        myIntent.putExtra("EXTRA_PALLET_CODE", findList!!.barcodeId)
        findList.itemCode?.let {
            myIntent.putExtra("EXTRA_ITEM_CODE", it)
        }
        pickingInfo.manufacturerName?.let {
            myIntent.putExtra("EXTRA_MANUFACTURER_NAME", it)
        }
        startActivity(myIntent)
    }


    fun verifyItemCode() {


        val stringValue = WMSSharedPref.getAppPrefs(WMSApplication.getInstance().context)
            .getStringValue("OLD_BIN_VAL")

        var findList = dataQtyList.find {
            it.binLocation.equals(stringValue)
        }

        val myIntent =
            Intent(this@PickingDetailsActivity, ItemCodeCustomDialog::class.java)
        myIntent.putExtra("EXTRA_ITEM_CODE", findList!!.itemCode)
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
                        false,
                        pickingInfo.remarks,
                        pickingInfo.barcodeId
                    )
                )

                for (i in dataQtyList?.indices!!) {

                    for (j in qtyInfo?.indices!!) {
                        if (dataQtyList[i].binLocation == qtyInfo[j].binLocation && dataQtyList[i].palletId == qtyInfo[j].palletId && dataQtyList[i].itemCode == qtyInfo[j].itemCode) {
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
                                    isSelctedFrom,
                                    qtyInfo[j].remark,
                                    qtyInfo[j].barcodeId
                                )
                            )

                            isAvailable = true
                        } else {
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
        var isSel = false
        isSel = sum > 0

        dataQtyListFinal.add(
            PickingQuantityConfirm(
                sum,
                pickingInfo.inventoryQuantity,
                pickingInfo.proposedPackBarCode,
                pickingInfo.proposedStorageBin,
                pickingInfo.outboundOrderTypeId,
                "",
                pickingInfo.itemCode,
                isSel,
                pickingInfo.barcodeId
            )
        )

    }


    fun checkHandlingEquipmentID(stringHe: String) {
        pd.show()
        val model11 = ViewModelProvider(this)[PickingLiveDataModel::class.java]



        model11.getEquipment(stringHe).observe(this) { HEstatus ->
            if ((HEstatus == true)) {
                createPickingRecords(stringHe)
            } else {
                if (pd != null) pd.dismiss()

                ToastUtils.showToast(applicationContext, "HE Number not Available")
                finish()
                PickingListActivity.getInstance()?.reload()
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
            allocQty =
                if (dataQtyList[i].binLocation.equals(proposedBin) && dataQtyList[i].palletId.equals(
                        proposedPalletID
                    )
                ) {
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
                PickingSubmitRequest(
                    2,
                    stringHe,
                    loginID,
                    "",
                    pickingInfo.companyCodeId?.toInt(),
                    0,
                    pickingInfo.description?:"",
                    pickingInfo.itemCode,
                    pickingInfo.languageId,
                    pickingInfo.lineNumber,
                    pickingInfo.manufacturerName?:"",
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
                    dataQtyList[i].remark,
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
                    pickingInfo.warehouseId,
                    dataQtyList[i].barcodeId
                )
            )

        }

        if (!NetworkUtils().haveNetworkConnection(applicationContext)) {
            ToastUtils.showToast(
                applicationContext,
                getString(R.string.internet_check_msg_store_offline)
            )

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
        model.errorMessage.observe(this) {
            if (::pd.isInitialized) {
                pd.dismiss()
            }
            if (it) {
                isError = it
                Toast.makeText(
                    this,
                    this.resources.getString(R.string.pick_up_line_record_is_getting_duplicated),
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun processRequest(vDataList: List<PickingSubmitResponse>?) {

        if (vDataList != null && vDataList.isNotEmpty()) {
            ToastUtils.showToast(
                applicationContext,
                "Picking Submitted Successfully "
            )

           /* val tempList = dataQtyList.filter { it.isSelected?.not() == true }
            dataQtyList = tempList as MutableList<PickingQuantityConfirm>
            if (dataQtyList.isEmpty()) {

            } else {
                Recycler_view.adapter = PickingConfirmAdapter(this, dataQtyList)
            }*/
            finish()
            vDataList[0].pickedStorageBin?.let {
                PickingOrderNoListActivity.getInstance()?.reload(
                    it,"PICKING_LIST"
                )
            }
        } else {
            ToastUtils.showToast(
                applicationContext,
                "Unable to Submit Picking Details"
            )
        }
    }

    private fun displayPopupWindow(anchorView: View, msg: String) {
        val popup: PopupWindow = PopupWindow(this@PickingDetailsActivity)
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

    private fun getGlobalInventoryList() {
        val model = ViewModelProvider(this)[PickingLiveDataModel::class.java]
        val itemCodeList: MutableList<String> = ArrayList()
        itemCodeList.add(pickingInfo.itemCode.toString())
        val request = PickingInventory(itemCodeList, emptyList(), emptyList(), emptyList(), emptyList())

        model.getGlobalInventoryList(request).observe(this) { globalInventoryList ->
            // update UI
            //progress.visibility = View.GONE
            if (globalInventoryList != null && globalInventoryList.isNotEmpty()) {
                val companyCodeId: String =
                    WMSSharedPref.getAppPrefs(WMSApplication.getInstance().context)
                        .getStringValue(PrefConstant.COMPANY_CODE_ID)

                val filterList = globalInventoryList.filter { ((it.inventoryQuantity ?: 0) > 0) && (it.binClassId == 1 || it.binClassId == 2) && (it.stockTypeId == 1) && (it.companyCodeId != companyCodeId)}
                if (filterList.isNotEmpty()) {
                    if (supportFragmentManager.findFragmentByTag("global_inventory_fragment") == null) {
                        val globalInventoryDialog = GlobalInventoryDialog(filterList)
                        supportFragmentManager.beginTransaction()
                            .add(globalInventoryDialog, "global_inventory_fragment")
                            .commitAllowingStateLoss()
                    }
                } else {
                    ToastUtils.showToast(applicationContext, "Inventory is not found in any branch")
                }
            } else {
                ToastUtils.showToast(applicationContext, "Inventory is not found in any branch")
            }
        }
    }
    private fun navigateToHome() {
        if (isError) {
            val intent = Intent(this@PickingDetailsActivity, HomePageActivity::class.java)
            startActivity(intent)
            finishAffinity()
        } else {
            finish()
        }
    }

    override fun onBackPressed() {
        navigateToHome()
        super.onBackPressed()
    }
}
