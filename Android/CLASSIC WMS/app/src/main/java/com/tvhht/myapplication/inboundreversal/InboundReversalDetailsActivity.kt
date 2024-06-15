package com.tvhht.myapplication.inboundreversal

import android.app.ProgressDialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.tvhht.myapplication.R
import com.tvhht.myapplication.cases.ScannerErrorCustomDialog
import com.tvhht.myapplication.inboundreversal.adapter.ReversalConfirmAdapter
import com.tvhht.myapplication.inboundreversal.utils.ConfirmRevWarningDialog
import com.tvhht.myapplication.inboundreversal.utils.ReversalAddBinCustomDialog
import com.tvhht.myapplication.inboundreversal.utils.ReversalBinCustomDialog
import com.tvhht.myapplication.inboundreversal.utils.ReversalChangeBinCustomDialog
import com.tvhht.myapplication.inboundreversal.viewmodel.ReversalLiveData
import com.tvhht.myapplication.putaway.model.BinQuantityConfirm
import com.tvhht.myapplication.putaway.model.PutAwayCombineModel
import com.tvhht.myapplication.putaway.model.PutAwayConfirmResponse
import com.tvhht.myapplication.putaway.model.PutAwaySubmit
import com.tvhht.myapplication.utils.*
import kotlinx.android.synthetic.main.activity_case_details.Recycler_view
import kotlinx.android.synthetic.main.activity_putaway_details_confirm.*
import kotlinx.android.synthetic.main.tool_bar.*
import kotlinx.android.synthetic.main.tool_bar.view.*

class InboundReversalDetailsActivity : AppCompatActivity() {

    lateinit var pd: ProgressDialog

    var dataList = MutableLiveData<List<PutAwayConfirmResponse>>()
    lateinit var duplicate_dataList: List<PutAwayConfirmResponse>
    lateinit var putAwayInfo: PutAwayCombineModel
    var dataBinQtyList: MutableList<BinQuantityConfirm> = ArrayList<BinQuantityConfirm>()
    var dataBinQtyListfinal: MutableList<BinQuantityConfirm> = ArrayList<BinQuantityConfirm>()

    var isScanned: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_putaway_details_confirm)
        Recycler_view.layoutManager = LinearLayoutManager(
            this
        )

        instances2 = this
        header_tt_details.text = "Returns"
        itemCodeHeader.text = "Pallet ID"
        toolbar.sign_out.setOnClickListener {
            val cdd = LogoutCustomDialog(
                this@InboundReversalDetailsActivity
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

        WMSSharedPref.getAppPrefs(
            WMSApplication.getInstance().context
        ).saveBooleanValue(
            "CASE_CODE_UPDATED",
            false
        )


        WMSSharedPref.getAppPrefs(WMSApplication.getInstance().context)
            .clearSharedPrefs("QUANTITY_INFO_LIST")

        loadData()

        Recycler_view.addItemDecoration(
            DividerItemDecoration(
                Recycler_view.context,
                DividerItemDecoration.VERTICAL
            )
        )


        addNewBIN.setOnClickListener {


            if (dataBinQtyListfinal.size == 0) {
                val cdd = ConfirmRevWarningDialog(
                    this@InboundReversalDetailsActivity
                )
                cdd.show()
            } else {

                if (balanceQty.text.toString().toInt() == 0) {

                    ToastUtils.showToast(applicationContext, "Remaining Qty is 0")
                } else {

                    loadAddBinData()
                }
            }

        }


    }


    fun loadAddBinData() {
        WMSSharedPref.getAppPrefs(WMSApplication.getInstance().context)
            .clearSharedPrefs("QUANTITY_INFO_LIST")

        if (dataBinQtyListfinal.size > 0) {
            WMSSharedPref.getAppPrefs(WMSApplication.getInstance().context)
                .saveQTYInfo(dataBinQtyListfinal[0])
        } else {
            dataBinQtyListfinal.add(BinQuantityConfirm(0, putAwayInfo?.putAwayQuantity, "", false))

            WMSSharedPref.getAppPrefs(WMSApplication.getInstance().context)
                .saveQTYInfo(dataBinQtyListfinal[0])
        }

        val myIntent =
            Intent(this@InboundReversalDetailsActivity, ReversalAddBinCustomDialog::class.java)
        startActivity(myIntent)
    }


    private fun loadData() {


        pd = ProgressDialog(this@InboundReversalDetailsActivity)
        pd.setMessage("Loading...")

        var loginID =
            WMSSharedPref.getAppPrefs(WMSApplication.getInstance().context)
                .getStringValue(PrefConstant.LOGIN_ID)
        putaway_detail_txt_user.text = loginID

        putAwayInfo = WMSSharedPref.getAppPrefs(
            WMSApplication.getInstance().context
        ).putAwayInfo!!

        palletID.text = putAwayInfo?.packBarcodes.toString()
        mfr.text = putAwayInfo?.manufacturerPartNo
        totalQTY.text = putAwayInfo?.putAwayQuantity.toString()
        balanceQty.text = putAwayInfo?.putAwayQuantity.toString()

        dataBinQtyList.add(
            BinQuantityConfirm(
                0,
                putAwayInfo?.putAwayQuantity,
                putAwayInfo.proposedStorageBin,
                false
            )
        )

        Recycler_view.adapter = ReversalConfirmAdapter(this, dataBinQtyList)


        buttonYes.setOnClickListener {

            if (dataBinQtyListfinal.size == 0) {
                ToastUtils.showToast(
                    this@InboundReversalDetailsActivity,
                    "Please scan the Bin"
                )
            } else {
                submitPutaway()

            }

        }

        buttonNo.setOnClickListener {
            finish()
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

        if (balanceQty.text.toString().toInt() == 0) {
            ToastUtils.showToast(applicationContext, "Remaining Qty is 0")
        } else {
            if (putAwayInfo?.proposedStorageBin?.length == decodedData?.length) {

                if (decodedData.toString() == putAwayInfo?.proposedStorageBin) {
                    isScanned =
                        WMSSharedPref.getAppPrefs(WMSApplication.getInstance().context)
                            .getBooleanValue(PrefConstant.CASE_CODE_SCANNED)

                    val find = dataBinQtyList.find {
                        it.binLocation.equals(decodedData.toString())
                    }
                    val toString = balanceQty.text.toString()
                    find?.putawayTotalQty = toString.toInt()
                    WMSSharedPref.getAppPrefs(
                        WMSApplication.getInstance().context
                    ).saveQTYInfo(find)!!
                    val cdd =
                        ReversalBinCustomDialog(
                            this@InboundReversalDetailsActivity,
                            "Bin Number Scanned",
                            decodedData.toString()
                        )
                    cdd.show()
                } else {
                    val cdd = ScannerErrorCustomDialog(
                        this@InboundReversalDetailsActivity,
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

    override fun onResume() {
        super.onResume()
        registerReceivers()
    }


    fun loadFromCache() {
        Log.d("ON RESUME", "Calling")

        try {
            val qtyInfo = WMSSharedPref.getAppPrefs(
                WMSApplication.getInstance().context
            ).qtyInfo!!



            if (qtyInfo != null) {

                var isAvailable = false

                for (i in dataBinQtyList?.indices!!) {
                    if (dataBinQtyList[i].binLocation == qtyInfo.binLocation) {
                        val putawayConfirmedQty = dataBinQtyList[i].putawayConfirmedQty!!
                        val putawayConfirmedQty1 = qtyInfo.putawayConfirmedQty!!
                        val sumCount = putawayConfirmedQty1
                        dataBinQtyList.removeAt(i)
                        dataBinQtyList.add(
                            BinQuantityConfirm(
                                sumCount,
                                qtyInfo.putawayTotalQty,
                                qtyInfo.binLocation,
                                qtyInfo.isSelected
                            )
                        )

                        isAvailable = true
                    }

                }
                if (!isAvailable) {
                    dataBinQtyList.add(qtyInfo)
                }
                getCurrentSum()
                Recycler_view.adapter = ReversalConfirmAdapter(this, dataBinQtyList)
                Recycler_view.adapter!!.notifyDataSetChanged()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }


    }


    fun submitPutaway() {
        var request: MutableList<PutAwaySubmit> = ArrayList<PutAwaySubmit>()
        val currentDate = DateUtil.getCurrentDate();
        var loginID =
            WMSSharedPref.getAppPrefs(WMSApplication.getInstance().context)
                .getStringValue(PrefConstant.LOGIN_ID)
        for (i in dataBinQtyList?.indices!!) {

            if (dataBinQtyList[i].isSelected == true && dataBinQtyList[i].putawayConfirmedQty!! > 0) {
                request.add(
                    PutAwaySubmit(
                        1,
                        putAwayInfo.assignedUserId.toString(),
                        putAwayInfo.companyCodeId.toString(),
                        loginID,
                        currentDate,
                        dataBinQtyList[i].binLocation.toString(),
                        loginID,
                        currentDate,
                        0,
                        putAwayInfo.expiryDate.toString(),
                        putAwayInfo.goodsReceiptNo.toString(),
                        putAwayInfo.itemCode,
                        putAwayInfo.languageId.toString(),
                        putAwayInfo.lineNo!!,
                        putAwayInfo.manufacturerDate.toString(),
                        1,
                        putAwayInfo.packBarcodes.toString(),
                        putAwayInfo.plantId.toString(),
                        putAwayInfo.preInboundNo.toString(),
                        putAwayInfo.proposedStorageBin.toString(),
                        putAwayInfo.putAwayNumber.toString(),
                        putAwayInfo.putAwayQuantity,
                        putAwayInfo.putAwayUom.toString(),
                        dataBinQtyList[i].putawayConfirmedQty,
                        putAwayInfo.quantityType,
                        putAwayInfo.refDocNumber.toString(),
                        1,
                        putAwayInfo.stockTypeId,
                        putAwayInfo.warehouseId.toString(),
                        putAwayInfo.itemDescription.toString()
                    )
                )
            }
        }

        if (!NetworkUtils().haveNetworkConnection(applicationContext)) {
            ToastUtils.showToast(
                applicationContext,
                getString(R.string.internet_check_msg_store_offline)
            )

            WMSApplication.getInstance().submitLocalRepository
                ?.insertPutawayListVo(request)
        } else {
            val model = ViewModelProviders.of(this)[ReversalLiveData::class.java]

            pd.show()
            model.submitPutAwayDetails(request).observe(this) { vDataList ->
                if (pd != null) {
                    pd.dismiss()
                }

                processRequest(vDataList)
            }

        }
    }

    private fun processRequest(vDataList: PutAwayConfirmResponse?) {
        if (vDataList != null) {
            ToastUtils.showToast(
                WMSApplication.getInstance().context,
                "Reversal Details Submitted successfully"
            )
            finish()
            InboundReversalListActivity.getInstance()?.reload()
        } else {
            ToastUtils.showToast(
                WMSApplication.getInstance().context,
                "Unable to update PutAway details"
            )
        }
    }

    fun getCurrentSum() {
        var myIntList: MutableList<Int> = ArrayList<Int>()
        for (i in dataBinQtyList.indices) {
            myIntList.add(dataBinQtyList[i].putawayConfirmedQty!!)
        }

        val sum = myIntList.sum()

        var balanceQty1 = putAwayInfo?.putAwayQuantity?.minus(sum)

        totalQTY.text = putAwayInfo?.putAwayQuantity.toString()
        balanceQty.text = balanceQty1.toString()

        dataBinQtyListfinal.clear()
        dataBinQtyListfinal.add(BinQuantityConfirm(balanceQty1, balanceQty1, "", false))

    }


    fun launchChangeBin() {
        val myIntent =
            Intent(this@InboundReversalDetailsActivity, ReversalChangeBinCustomDialog::class.java)
        startActivity(myIntent)
    }


    companion object {
        private var instances2: InboundReversalDetailsActivity? = null
        fun getInstance(): InboundReversalDetailsActivity? {
            return instances2
        }
    }

    override fun onBackPressed() {
        InboundReversalListActivity.getInstance()?.reload()
        super.onBackPressed()
    }


}

