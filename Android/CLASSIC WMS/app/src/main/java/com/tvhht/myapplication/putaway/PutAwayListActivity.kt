package com.tvhht.myapplication.putaway

import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
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
import com.tvhht.myapplication.cases.ScannerErrorCustomDialog
import com.tvhht.myapplication.home.HomePageActivity
import com.tvhht.myapplication.login.LoginLiveData
import com.tvhht.myapplication.login.model.LoginModel
import com.tvhht.myapplication.putaway.adapter.PutAwayAdapter
import com.tvhht.myapplication.putaway.model.PutAwayCombineModel
import com.tvhht.myapplication.putaway.utils.PalletIDCustomDialog
import com.tvhht.myapplication.putaway.viewmodel.PutAwayLiveData
import com.tvhht.myapplication.utils.*
import kotlinx.android.synthetic.main.activity_putaway_details.*
import kotlinx.android.synthetic.main.tool_bar.*
import kotlinx.android.synthetic.main.tool_bar.view.*
import java.text.SimpleDateFormat
import java.util.*


class PutAwayListActivity : AppCompatActivity() {
    val myCalendar = Calendar.getInstance()
    var isScanned: Boolean = false
    var duplicate_dataList: List<PutAwayCombineModel> = ArrayList()
    var filter_list: List<PutAwayCombineModel> = ArrayList()
    var str_list: MutableList<String> = ArrayList()
    var listGroup: MutableList<String> = ArrayList()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_putaway_details)
        Recycler_view.layoutManager = LinearLayoutManager(
            this
        )

        instances2 = this

        toolbar.sign_out.setOnClickListener {
            val cdd = LogoutCustomDialog(
                this@PutAwayListActivity
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


        // loginSuccess()


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


        val date =
            OnDateSetListener { view, year, month, day ->
                myCalendar[Calendar.YEAR] = year
                myCalendar[Calendar.MONTH] = month
                myCalendar[Calendar.DAY_OF_MONTH] = day
                updateLabel()
            }

        datePickerView.setOnClickListener {

            DatePickerDialog(
                this@PutAwayListActivity, date,
                myCalendar[Calendar.YEAR],
                myCalendar[Calendar.MONTH], myCalendar[Calendar.DAY_OF_MONTH]
            ).show()

        }

        listGroup.add("All")
        setDateValue.text = "All"


        filterByDate.setOnClickListener {

            filterByDate(setDateValue.text.toString())
        }


        clearFilter.setOnClickListener {

            reload()
        }
    }

    private fun zoneSelection() {

        if (listGroup != null) {

            var listGroup1 = listGroup.toSet().toList()

            var adapter: ArrayAdapter<String> =
                ArrayAdapter<String>(
                    applicationContext,
                    R.layout.spinner_item, R.id.txt_cell,
                    listGroup1
                );
            selection_value.adapter = adapter
        }
    }


    fun updateLabel() {
        val myFormat = "dd-MM-yyyy"
        val dateFormat = SimpleDateFormat(myFormat, Locale.US)
        setDateValue.text = dateFormat.format(myCalendar.time)
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
            isScanned =
                WMSSharedPref.getAppPrefs(WMSApplication.getInstance().context)
                    .getBooleanValue(PrefConstant.CASE_CODE_SCANNED)
            if (!isScanned && isScanned((decodedData.toString()))) {
                WMSSharedPref.getAppPrefs(
                    WMSApplication.getInstance().context
                ).saveBooleanValue(
                    PrefConstant.CASE_CODE_SCANNED,
                    false
                )
                val cdd = PalletIDCustomDialog(
                    this@PutAwayListActivity,
                    "Barcode ID Scanned",
                    decodedData.toString()
                )
                cdd.show()
            } else {
                val cdd = ScannerErrorCustomDialog(
                    this@PutAwayListActivity,
                    "Invalid Barcode ID",
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

    public fun loginSuccess() {


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

                    loadPutAwayList()

                }
            }

        }


    }


    fun loadPutAwayList() {
        val model = ViewModelProviders.of(this)[PutAwayLiveData::class.java]
        model.getPutAwayList().observe(this) { vDataList ->
            // update UI

            val ware_id = WMSSharedPref.getAppPrefs(
                WMSApplication.getInstance().context
            ).getStringValue(PrefConstant.WARE_HOUSE_ID)


            if (vDataList != null && vDataList.isNotEmpty()) {
                progress.visibility = View.GONE

                duplicate_dataList = vDataList!!
                filter_list =
                    duplicate_dataList.filter { (it.statusId == 19 || it.statusId == 21) && (it.inboundOrderTypeId == 1 || it.inboundOrderTypeId == 3) && it.warehouseId == ware_id.toInt() }

                // val filter2 = filter.filter {  }


                for (cases in filter_list?.indices!!) {
                    str_list.add(filter_list[cases].barcodeId.toString())
                    listGroup.add(filter_list[cases].proposedHandlingEquipment.toString())
                }

                var sizeList = filter_list.size.toString()

                putaway_txt_count_val.text = sizeList

                Recycler_view.adapter = PutAwayAdapter(this, filter_list)

                registerReceivers()
                zoneSelection()
            } else {
                progress.visibility = View.GONE
                ToastUtils.showToast(applicationContext, "No Records Available")
            }
        }
    }

    override fun onResume() {
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
            loginSuccess()
            registerReceivers()
        }
        super.onResume()


    }


    private fun isScanned(caseID: String): Boolean {
        var returnState = false;
        for (csModel: PutAwayCombineModel in filter_list!!) {

            val toString = csModel.barcodeId.toString()
            if (toString == caseID) {
                val csDetails: PutAwayCombineModel? =
                    filter_list.find {
                        it.barcodeId == (caseID)
                    }

                WMSSharedPref.getAppPrefs(
                    WMSApplication.getInstance().context
                ).savePutAwayInfo(csDetails)
                returnState = true
                break
            }

        }


        return returnState
    }


    fun filterByDate(date: String) {

        val toIndexValue = selection_value.selectedItem;

        if (date != "All" && toIndexValue == "All") {
            val filterDetList =
                filter_list.filter { (DateUtil.getDateYYYYMMDD(it.createdOn) == date) }

            for (cases in filterDetList?.indices!!) {
                str_list.add(filterDetList[cases].barcodeId.toString())
            }

            var sizeList = filterDetList.size.toString()

            putaway_txt_count_val.text = sizeList

            Recycler_view.adapter = PutAwayAdapter(this, filterDetList)
        } else if (date == "All" && toIndexValue != "All") {
            val filterDetList =
                filter_list.filter { it.proposedHandlingEquipment == toIndexValue }

            for (cases in filterDetList?.indices!!) {
                str_list.add(filterDetList[cases].barcodeId.toString())
            }

            var sizeList = filterDetList.size.toString()

            putaway_txt_count_val.text = sizeList

            Recycler_view.adapter = PutAwayAdapter(this, filterDetList)
        } else if (date != "All" && toIndexValue != "All") {
            val filterDetList =
                filter_list.filter {
                    it.proposedHandlingEquipment == toIndexValue && (DateUtil.getDateYYYYMMDD(
                        it.createdOn
                    ) == date)
                }

            for (cases in filterDetList?.indices!!) {
                str_list.add(filterDetList[cases].barcodeId.toString())
            }

            var sizeList = filterDetList.size.toString()

            putaway_txt_count_val.text = sizeList

            Recycler_view.adapter = PutAwayAdapter(this, filterDetList)
        } else {

            for (cases in filter_list?.indices!!) {
                str_list.add(filter_list[cases].barcodeId.toString())
            }

            var sizeList = filter_list.size.toString()

            putaway_txt_count_val.text = sizeList

            Recycler_view.adapter = PutAwayAdapter(this, filter_list)

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

    companion object {
        private var instances2: PutAwayListActivity? = null
        fun getInstance(): PutAwayListActivity? {
            return instances2
        }
    }

    override fun onBackPressed() {
        HomePageActivity.getInstance()?.reload()
        super.onBackPressed()
    }

}


