package com.tvhht.myapplication.quality

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
import com.tvhht.myapplication.cases.ScannerErrorCustomDialog
import com.tvhht.myapplication.home.HomePageActivity
import com.tvhht.myapplication.login.LoginLiveData
import com.tvhht.myapplication.login.model.LoginModel
import com.tvhht.myapplication.quality.adapter.QualityDetailsAdapter
import com.tvhht.myapplication.quality.model.QualityDetailRequestModel
import com.tvhht.myapplication.quality.model.QualityDetailsModel
import com.tvhht.myapplication.quality.model.QualityListResponse
import com.tvhht.myapplication.quality.model.QualityModel
import com.tvhht.myapplication.quality.utils.PalletIDCustomDialog
import com.tvhht.myapplication.quality.viewmodel.QualityLiveData
import com.tvhht.myapplication.utils.*
import kotlinx.android.synthetic.main.activity_perpetual_list.*
import kotlinx.android.synthetic.main.activity_quality_details_confirm.*
import kotlinx.android.synthetic.main.activity_quality_details_confirm.Recycler_view
import kotlinx.android.synthetic.main.activity_quality_details_confirm.progress
import kotlinx.android.synthetic.main.tool_bar.*
import kotlinx.android.synthetic.main.tool_bar.view.*

class QualityDetailsActivity : AppCompatActivity() {
    lateinit var pd: ProgressDialog
    var dataList = ArrayList<QualityDetailsModel>()
    var duplicate_dataList: MutableList<QualityDetailsModel> = ArrayList()
    var isScanned: Boolean = false
    lateinit var he_no: String
    lateinit var pickNO: String
    lateinit var quality_inspection_no: String
    var request: MutableList<QualityModel> = ArrayList()
    var listQualityInfo: List<QualityListResponse> = ArrayList();
    var str_list: MutableList<String> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quality_details_confirm)
        Recycler_view.layoutManager = LinearLayoutManager(
            this
        )


        instances2 = this

        WMSSharedPref.getAppPrefs(WMSApplication.getInstance().context)
            .clearSharedPrefs("QUALITY_INFO_LIST")

//        listQualityInfo =
//            WMSSharedPref.getAppPrefs(WMSApplication.getInstance().context).listQualityInfo!!

        pd = ProgressDialog(this@QualityDetailsActivity)
        pd.setMessage("Loading...")

        he_no = intent.getStringExtra("HE_INT_CODE").toString()
        pickNO = intent.getStringExtra("HE_INT_CODE_NO").toString()
        quality_inspection_no = intent.getStringExtra("QUALITY_INSPECTION_NO").toString()


        val login_id = WMSSharedPref.getAppPrefs(
            WMSApplication.getInstance().context
        ).getStringValue(PrefConstant.LOGIN_ID)

        txt_user.text = login_id
        txt_asn_val.text = he_no

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

        Recycler_view.addItemDecoration(
            DividerItemDecoration(
                Recycler_view.context,
                DividerItemDecoration.VERTICAL
            )
        )

        toolbar.sign_out.setOnClickListener {
            val cdd = LogoutCustomDialog(
                this@QualityDetailsActivity
            )
            if (cdd.isShowing) {
                cdd.dismiss()
            }
            cdd.show()
        }

        buttonYes.setOnClickListener {

            if (duplicate_dataList.isEmpty()) {
                ToastUtils.showToast(
                    applicationContext,
                    "Please Verify Pallet ID"
                )
            } else {
                createQuality()
            }

        }

        buttonNo.setOnClickListener {
            finish()
        }


    }


    private fun verifyTokenNew() {

        progress.visibility = View.VISIBLE

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

                    createNewRecord()

                }
            }

        }


    }


    private fun verifyToken() {


        val model = ViewModelProvider(this)[QualityLiveData::class.java]

        model.getQualityListNew("%$he_no%").observe(this) { vDataList ->
            // update UI
            progress.visibility = View.GONE

            listQualityInfo = (vDataList as ArrayList<QualityListResponse>?)!!

            val list2 = (listQualityInfo as ArrayList<QualityListResponse>?)!!

            var list = list2.filter { it.actualHeNo == he_no }

            val qtList: MutableList<QualityDetailsModel> = ArrayList()
            for (i in list?.indices!!) {

                try {
                    var lineValue = 1
                    if (!list[i].referenceField5.isNullOrEmpty() && !list[i].referenceField5.equals(
                            "null"
                        )
                    ) {
                        lineValue = list[i].referenceField5.toString().toInt()
                    }

                    qtList.add(
                        QualityDetailsModel(
                            list[i].languageId,
                            list[i].companyCodeId,
                            list[i].plantId,
                            list[i].warehouseId,
                            list[i].preOutboundNo,
                            list[i].refDocNumber,
                            list[i].partnerCode,
                            lineValue,
                            list[i].pickupNumber,
                            list[i].qcToQty!!.toDouble().toInt(),
                            list[i].referenceField4,
                            list[i].actualHeNo,
                            list[i].referenceField1,
                            list[i].referenceField2,
                            0,
                            "",
                            "",
                            "",
                            list[i].qcUom,
                            0,
                            list[i].deletionIndicator,
                            list[i].referenceField3,
                            list[i].manufacturerPartNo,
                            "",
                            list[i].referenceField2,
                            "",
                            list[i].statusId,
                            list[i].referenceField1,
                            list[i].referenceField2,
                            list[i].referenceField3,
                            list[i].referenceField4,
                            list[i].referenceField5,
                            list[i].referenceField6,
                            list[i].referenceField7,
                            list[i].referenceField8,
                            list[i].referenceField9,
                            list[i].referenceField10,
                            list[i].deletionIndicator,
                            list[i].qualityCreatedBy,
                            list[i].qualityCreatedOn,
                            list[i].qualityConfirmedBy,
                            list[i].qualityConfirmedOn,
                            list[i].qualityUpdatedBy,
                            list[i].qualityUpdatedOn,
                            list[i].qualityReversedBy,
                            list[i].qualityReversedOn,
                            list[i].qualityInspectionNo,
                            list[i].isSelected


                        )
                    )
                } catch (e: Exception) {
                    e.printStackTrace()
                }

            }

            dataList = qtList.filter { it.statusId == 54 } as ArrayList<QualityDetailsModel>

            for (i in dataList?.indices!!) {
                str_list.add(dataList[i].pickedPackCode!!)
            }
            val size = dataList.size
            txt_count_val.text = size.toString()
            dataList.sortBy { !it.isSelected }

            if (dataList.size > 500) {
                dataList = ArrayList(dataList.subList(0, 500))
            } else {
                dataList = dataList
            }

            Log.d("Sizee==", ">>" + dataList.size)

            Recycler_view.adapter = QualityDetailsAdapter(this, dataList)
        }


    }

    private fun createNewRecord() {
        val model = ViewModelProvider(this)[QualityLiveData::class.java]

        model.createData(request).observe(this) { vDataList ->
            // update UI
            if (pd != null) pd.dismiss()

            if (vDataList != null && vDataList.isNotEmpty()) {
                ToastUtils.showToast(
                    applicationContext,
                    "Record Submitted Successfully "
                )
            } else {
                ToastUtils.showToast(
                    applicationContext,
                    "Unable to Submit the Details"
                )
            }
            finish()
            QualityListActivity.getInstance()?.reload()


        }
    }

    private fun getQualityDetails() {
        val ware_id = WMSSharedPref.getAppPrefs(
            WMSApplication.getInstance().context
        ).getStringValue(PrefConstant.WARE_HOUSE_ID)


        val heNumber: MutableList<String> = ArrayList()
        val wareID: MutableList<String> = ArrayList()
        val pickNumber: MutableList<String> = ArrayList()


        for (i in listQualityInfo.indices) {
            if (listQualityInfo[i].actualHeNo.equals(he_no)) {
                heNumber.add(he_no)
                wareID.add(ware_id)
                pickNumber.add(listQualityInfo[i].pickupNumber.toString())

            }

        }

//        heNumber.add(he_no)
//        wareID.add(ware_id)
//        pickNumber.add(pickNO)

        val requestData = QualityDetailRequestModel(wareID, heNumber, pickNumber)

        progress.visibility = View.GONE

        val model = ViewModelProviders.of(this)[QualityLiveData::class.java]
        model.getQtyList(requestData).observe(this) { vDataList ->
            // update UI


        }
    }


    fun createQuality() {

        request.clear()
        pd.show()
        val currentDate = DateUtil.getCurrentDate();
        var loginID =
            WMSSharedPref.getAppPrefs(WMSApplication.getInstance().context)
                .getStringValue(PrefConstant.LOGIN_ID)
        for (i in duplicate_dataList?.indices!!) {
            request.add(
                QualityModel(
                    1,
                    he_no,
                    "",
                    duplicate_dataList[i].companyCodeId.toString(),
                    0,
                    duplicate_dataList[i].description,
                    duplicate_dataList[i].itemCode,
                    duplicate_dataList[i].languageId,
                    duplicate_dataList[i].lineNumber,
                    duplicate_dataList[i].manufacturerPartNo,
                    0,
                    "",
                    duplicate_dataList[i].partnerCode,
                    duplicate_dataList[i].pickQty!!.toInt(),
                    duplicate_dataList[i].pickedPackCode,
                    duplicate_dataList[i].plantId,
                    duplicate_dataList[i].preOutboundNo,
                    duplicate_dataList[i].pickUom,
                    loginID,
                    currentDate,
                    loginID,
                    currentDate,
                    duplicate_dataList[i].qualityInspectionNo,
                    duplicate_dataList[i].pickConfirmQty,
                    loginID,
                    currentDate,
                    loginID,
                    currentDate,
                    duplicate_dataList[i].refDocNumber,
                    duplicate_dataList[i].referenceField1,
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
                    "",
                    duplicate_dataList[i].specialStockIndicatorId,
                    0,
                    duplicate_dataList[i].stockTypeId,
                    0,
                    "",
                    duplicate_dataList[i].warehouseId

                )

            )
        }

//        if (!NetworkUtils().haveNetworkConnection(applicationContext)) {
//
//            Handler().postDelayed({
//                progress.visibility = View.GONE
//                if(pd!=null)
//                    pd.dismiss()
//                ToastUtils.showToast(applicationContext, getString(R.string.internet_check_msg_store_offline))
//
//            }, 2000)
//
//
//            WMSApplication.getInstance().submitLocalRepository
//                ?.insertQuality(request)
//
//        } else {

        //        verifyTokenNew()
        //    }

        verifyTokenNew()


    }


    fun submitQuantity() {

        try {
            val qualityInfo = WMSSharedPref.getAppPrefs(
                WMSApplication.getInstance().applicationContext
            ).qualityInfo



            if (qualityInfo != null) {

                var isAvailable = false

                for (i in dataList?.indices!!) {
                    if (dataList[i].pickedPackCode == qualityInfo.pickedPackCode && dataList[i].refDocNumber == qualityInfo.refDocNumber && dataList[i].itemCode == qualityInfo.itemCode && dataList[i].qualityInspectionNo == qualityInfo.qualityInspectionNo) {
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
                    if (dataList[i].isSelected) {
                        duplicate_dataList.add(dataList[i])
                    }
                }


                dataList.sortBy { !it.isSelected }


                Recycler_view.adapter = QualityDetailsAdapter(this, dataList)
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


    companion object {
        private var instances2: QualityDetailsActivity? = null
        fun getInstance(): QualityDetailsActivity? {
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

        var shortestLength = 0
        if (str_list.size > 0) {
            shortestLength = str_list.minByOrNull { it.length }!!.length
        }
        if (shortestLength <= decodedData?.length!!) {
            if (!isScanned && isScanned(decodedData.toString())) {
                val qtModel: QualityDetailsModel =
                    dataList.find { it.pickedPackCode == decodedData.toString() }!!

                val cdd = PalletIDCustomDialog(
                    this@QualityDetailsActivity,
                    "Pallet ID Scanned",
                    qtModel
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

    private fun isScanned(caseID: String): Boolean {
        var returnState = false;

        val find = dataList.find { it.pickedPackCode == caseID }

        if (find != null)
            return true
        Recycler_view.adapter = QualityDetailsAdapter(this, dataList)
        Recycler_view.adapter!!.notifyDataSetChanged()

        return returnState
    }


    fun getInspectionNo(pickupNumber: String): String {
        var returnState = "";

        val find = listQualityInfo.find { it.pickupNumber == pickupNumber }

        returnState = find!!.qualityInspectionNo.toString()

        return returnState
    }


}


