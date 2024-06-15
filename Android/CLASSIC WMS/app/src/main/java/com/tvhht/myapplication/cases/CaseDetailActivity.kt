package com.tvhht.myapplication.cases

import android.app.ProgressDialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.provider.ContactsContract
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.tvhht.myapplication.R
import com.tvhht.myapplication.cases.adapter.CaseDetailAdapter
import com.tvhht.myapplication.cases.model.AsnList
import com.tvhht.myapplication.cases.model.CaseDetailModel
import com.tvhht.myapplication.cases.viewmodel.CasesLiveDataModel
import com.tvhht.myapplication.putaway.utils.PalletIDCustomDialog
import com.tvhht.myapplication.utils.*

import kotlinx.android.synthetic.main.activity_case_details.*
import kotlinx.android.synthetic.main.activity_case_details.Recycler_view
import kotlinx.android.synthetic.main.activity_case_details.progress
import kotlinx.android.synthetic.main.tool_bar.*
import kotlinx.android.synthetic.main.tool_bar.view.*
import java.util.*
import kotlin.collections.ArrayList


class CaseDetailActivity : AppCompatActivity() {

    var strList: MutableList<CaseDetailModel> = ArrayList()
    var case_dataList: MutableList<CaseDetailModel> = ArrayList()
    var case_dataListFinal: MutableList<CaseDetailModel> = ArrayList()
    var isScanned: Boolean = false
    var split_caseCode: MutableList<String> = ArrayList()
    var caseCodeList: MutableList<String> = ArrayList()
    var caseCodeList2: MutableList<AsnList> = ArrayList()
    lateinit var loginID: String
    var pd: ProgressDialog? = null

    lateinit var dataList: AsnList
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_case_details)

        instance = this

        toolbar.sign_out.setOnClickListener {
            callLogout()
        }

        dataList = intent?.getParcelableExtra<AsnList>("CASE_LIST_SEL")!!
        var dataList1 = intent?.getStringExtra("CASE_LIST_SEL_ID")

        WMSSharedPref.getAppPrefs(
            WMSApplication.getInstance().context
        ).saveBooleanValue(
            "CASE_CODE_UPDATED",
            false
        )

        pd = ProgressDialog(this@CaseDetailActivity)
        pd!!.setMessage("Loading...")


        loginID = WMSSharedPref.getAppPrefs(
            WMSApplication.getInstance().context
        ).getStringValue(PrefConstant.LOGIN_ID)


        var repo = WMSApplication.getInstance().casesLocalRepository
        repo.fetchData(dataList1).observe(this, { listIDs: List<AsnList>? ->
            processRequest(listIDs)
        })


    }


    private fun processRequest(listIDs: List<AsnList>?) {
        try {
            caseCodeList2.addAll(listIDs!!)
            progress.visibility = View.GONE
            for (cases in caseCodeList2?.indices!!) {
                split_caseCode.add(caseCodeList2[cases].caseCode.toString())
                case_dataListFinal.add(
                    CaseDetailModel(
                        caseCodeList2[cases].caseCode,
                        caseCodeList2[cases].itemCode.toString(),
                        caseCodeList2[cases].lineNo!!,
                        loginID,
                        caseCodeList2[cases].palletCode,
                        caseCodeList2[cases].preInboundNo?.toString(),
                        caseCodeList2[cases].refDocNumber,
                        caseCodeList2[cases].stagingNo.toString(),
                        caseCodeList2[cases].warehouseId.toString(),
                        false
                    )
                )

            }

            case_dataList =
                case_dataListFinal.distinctBy { it.caseCode } as MutableList<CaseDetailModel>

            Recycler_view.layoutManager = LinearLayoutManager(
                this
            )
            Recycler_view.addItemDecoration(
                DividerItemDecoration(
                    Recycler_view.context,
                    DividerItemDecoration.VERTICAL
                )
            )
            Recycler_view.adapter = CaseDetailAdapter(this, case_dataList)

            getUpdateHeaderInfo(
                case_dataList.size.toString(),
                dataList?.refDocNumber.toString(),
                loginID
            )


        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    private fun getUpdateHeaderInfo(count: String, asnNo: String, loginID: String) {
        txt_count_val.text = count
        txt_asn_val.text = asnNo
        txt_user.text = loginID
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

        val shortestLength = split_caseCode.minByOrNull { it.length }?.length
        if (shortestLength!! <= decodedData?.length!!) {

            if (!isScanned && isScanned(decodedData.toString())) {
                WMSSharedPref.getAppPrefs(
                    WMSApplication.getInstance().context
                ).saveBooleanValue(
                    PrefConstant.CASE_CODE_SCANNED,
                    false
                )

                WMSSharedPref.getAppPrefs(
                    WMSApplication.getInstance().context
                ).saveStringValue(
                    "MY_CASE_CODE",
                    decodedData.toString()
                )

                val caseDetails: CaseDetailModel =
                    case_dataList.find { it.caseCode == decodedData.toString() }!!
                val cdd = ScannerCustomDialog(
                    this@CaseDetailActivity,
                    "Case Number Scanned",
                    decodedData.toString(), 1, caseDetails
                )
                if (cdd.isShowing) {
                    cdd.dismiss()
                }
                cdd.show()
            } else {
                val cdd = ScannerErrorCustomDialog(
                    this@CaseDetailActivity,
                    "Invalid Case Number",
                    decodedData.toString(), 1,
                )
                if (cdd.isShowing) {
                    cdd.dismiss()
                }
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


    override fun onResume() {
        val booleanValue = WMSSharedPref.getAppPrefs(
            WMSApplication.getInstance().context
        ).getBooleanValue(
            "CASE_CODE_UPDATED"
        )

        if (booleanValue)
            isSuccess()
        super.onResume()
        registerReceivers()

    }


    private fun isScanned(caseID: String): Boolean {
        var returnState = false;
        val caseDetails: CaseDetailModel? =
            case_dataList.find { it.caseCode == caseID }

        if (caseDetails != null)
            return true
        Recycler_view.adapter = CaseDetailAdapter(this, case_dataList)
        Recycler_view.adapter!!.notifyDataSetChanged()

        return returnState
    }


    private fun isSuccess() {

        val caseID = WMSSharedPref.getAppPrefs(
            WMSApplication.getInstance().context
        ).getStringValue(
            "MY_CASE_CODE"
        )

        for (caseModel: CaseDetailModel in case_dataList!!) {
            if (caseModel.caseCode == caseID) {
                case_dataList.remove(caseModel)

                break
            }

        }


        txt_count_val.text = case_dataList.size.toString()


        Recycler_view.adapter = CaseDetailAdapter(this, case_dataList)
        Recycler_view.adapter!!.notifyDataSetChanged()

    }


    protected fun updateDataList(casedID: String) {
        val caseDetails: CaseDetailModel? =
            case_dataList.find { it.caseCode == casedID }
        case_dataList.remove(caseDetails)
        split_caseCode.remove(casedID)
        Recycler_view.adapter = CaseDetailAdapter(this, case_dataList)

        if (case_dataList.size == 0) {
            finish()
            ASNNumberListActivity.getInstance()?.reload()
        }

        getUpdateHeaderInfo(
            case_dataList.size.toString(),
            dataList?.refDocNumber.toString(),
            loginID
        )



    }

    fun callLogout() {
        val cdd = LogoutCustomDialog(
            this@CaseDetailActivity
        )
        if (cdd.isShowing) {
            cdd.dismiss()
        }
        cdd.show()

    }

    companion object {
        private var instance: CaseDetailActivity? = null
        fun getInstance(): CaseDetailActivity? {
            return instance
        }
    }

    fun createCase(caseDetailModel: CaseDetailModel) {


        pd!!.show()
        val filterList = case_dataListFinal.filter { it.caseCode == caseDetailModel.caseCode }

        val model = ViewModelProviders.of(this)[CasesLiveDataModel::class.java]

        if (!NetworkUtils().haveNetworkConnection(applicationContext)) {
            ToastUtils.showToast(applicationContext, getString(R.string.internet_check_msg))
            if (pd != null)
                pd!!.dismiss()
        } else {
            model.createCaseToUpload(caseDetailModel, filterList).observe(this) { listVO ->

                if (pd != null)
                    pd!!.dismiss()
                if (listVO != null) {
                    ToastUtils.showToast(applicationContext, "Case Updated Successfully")
                    WMSSharedPref.getAppPrefs(
                            WMSApplication.getInstance().context
                    ).saveBooleanValue(
                            "CASE_CODE_UPDATED",
                            true
                    )
                    updateDataList(listVO.caseCode!!)
                } else {
                    WMSSharedPref.getAppPrefs(
                            WMSApplication.getInstance().context
                    ).saveBooleanValue(
                            "CASE_CODE_UPDATED",
                            false
                    )
                    ToastUtils.showToast(
                            applicationContext,
                            "Unable to update Case Details"
                    )
                }


            }
        }
    }


}

