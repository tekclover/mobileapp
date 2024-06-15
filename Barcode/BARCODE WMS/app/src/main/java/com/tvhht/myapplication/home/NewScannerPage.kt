package com.tvhht.myapplication.home


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
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.snackbar.Snackbar
import com.tvhht.myapplication.R
import com.tvhht.myapplication.home.model.BarcodeRequestBin
import com.tvhht.myapplication.home.model.BarcodeResponse
import com.tvhht.myapplication.home.model.ManufactureRequest
import com.tvhht.myapplication.login.LoginLiveData
import com.tvhht.myapplication.login.model.LoginModel
import com.tvhht.myapplication.utils.*
import kotlinx.android.synthetic.main.activity_new_scanner.*
import kotlinx.android.synthetic.main.tool_bar.*
import kotlinx.android.synthetic.main.tool_bar.view.*


class NewScannerPage : AppCompatActivity() {

    var isScanned: Boolean = false
    var reportList_data: List<BarcodeResponse> = ArrayList()
    lateinit var modelReport: ReportLiveDataModel
    lateinit var adapter: ArrayAdapter<String>
    lateinit var adapterDetails: ArrayAdapter<String>

    var itemCode = ArrayList<String>()
    var itemDescription = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_scanner)

        instances2 = this
        modelReport = ViewModelProviders.of(this)[ReportLiveDataModel::class.java]
        toolbar.sign_out.setOnClickListener {
            val cdd = LogoutCustomDialog(
                this@NewScannerPage
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


        var loginID =
            WMSSharedPref.getAppPrefs(WMSApplication.getInstance().context)
                .getStringValue(PrefConstant.LOGIN_ID)
        putaway_txt_user.text = loginID
        getToken()

        buttonYes.setOnClickListener {

            if (palletIDEnter.text.toString() == "") {
                ToastUtils.showToast(applicationContext, "Please scan Barcode")
            } else {

                if (manufacturerTxt.selectedItem == null || itemCodetxt.text == null) {
                    ToastUtils.showToast(applicationContext, "Please enter valid Item Code")
                } else {
                    verifyToken()
                }
            }
        }

        buttonNo.setOnClickListener {

            val intent = intent
            overridePendingTransition(0, 0)
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
            finish()
            overridePendingTransition(0, 0)
            startActivity(intent)
        }

        scanReports1.setOnClickListener {

            if (!NetworkUtils().haveNetworkConnection(applicationContext)) {
                Handler().postDelayed({
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

                val myIntent =
                    Intent(this@NewScannerPage, ScanBinCustomDialog::class.java)
                startActivity(myIntent)
            }
        }

        reloadReports.setOnClickListener {

            val intent = intent
            overridePendingTransition(0, 0)
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
            finish()
            overridePendingTransition(0, 0)
            startActivity(intent)


        }

        loadItemCode()
    }


    fun getBarcode() {

        var reportsID =
            WMSSharedPref.getAppPrefs(WMSApplication.getInstance().context)
                .getStringValue("BARCODE_ID_VALUE")

        palletIDEnter.text = reportsID
    }


    fun getToken() {

        var request = LoginModel(
            ApiConstant.apiName_masters,
            ApiConstant.clientId,
            ApiConstant.clientSecretKey,
            ApiConstant.grantType,
            ApiConstant.apiName_name,
            ApiConstant.apiName_pass_key
        )
        progress.visibility = View.GONE
        val model = ViewModelProviders.of(this)[LoginLiveData::class.java]
        model.getLoginStatus(request).observe(this) { asnList ->
            // update UI
            if (asnList.equals("ERROR")) {
                progress.visibility = View.GONE
                ToastUtils.showToast(applicationContext, "User not Available")
            } else {
                if (asnList.equals("FAILED")) {
                    progress.visibility = View.GONE
                    ToastUtils.showToast(applicationContext, "No Data Available")
                } else {

                    // getReportList()

                }
            }

        }


    }

    fun verifyToken() {

        var request = LoginModel(
            ApiConstant.apiName_id_master,
            ApiConstant.clientId,
            ApiConstant.clientSecretKey,
            ApiConstant.grantType,
            ApiConstant.apiName_name,
            ApiConstant.apiName_pass_key
        )
        progress.visibility = View.VISIBLE
        val model = ViewModelProviders.of(this)[LoginLiveData::class.java]
        model.getAuthStatus(request).observe(this) { asnList ->
            // update UI
            if (asnList.equals("ERROR")) {
                progress.visibility = View.GONE
                ToastUtils.showToast(applicationContext, "User not Available")
            } else {
                if (asnList.equals("FAILED")) {
                    progress.visibility = View.GONE
                    ToastUtils.showToast(applicationContext, "No Data Available")
                } else {

                    getReportList()

                }
            }

        }


    }

    private fun getItemCode(s: String) {

        if (modelReport == null)
            modelReport = ViewModelProviders.of(this)[ReportLiveDataModel::class.java]
        modelReport.getItemCode(s).observe(this) { vDataList ->
            // update UI
            progress.visibility = View.GONE
            itemCode.clear()
            if (vDataList != null && vDataList.isNotEmpty()) {
                itemCode.addAll(vDataList)
                adapter =
                    ArrayAdapter<String>(
                        this,
                        android.R.layout.simple_spinner_dropdown_item,
                        itemCode
                    )
                itemCodetxt.threshold = 1
                itemCodetxt.setAdapter(adapter)
                adapter.notifyDataSetChanged()
                itemCodetxt.setOnItemClickListener() { parent, _, position, id ->
                    val selectedPoi = parent.adapter.getItem(position)
                    itemCodetxt.setText(selectedPoi.toString())

                    var manufactList = ArrayList<String>()
                    manufactList.add(itemCodetxt.text.toString())
                    this.currentFocus?.let { view ->
                        val imm =
                            getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
                        imm?.hideSoftInputFromWindow(view.windowToken, 0)
                    }

                    val requestData = ManufactureRequest(
                        manufactList
                    )

                    getItemDescription(requestData)
                }
            } else {
                // ToastUtils.showToast(applicationContext, "Request Failed")
            }
        }
    }

    private fun getItemDescription(s: ManufactureRequest) {

        if (modelReport == null)
            modelReport = ViewModelProviders.of(this)[ReportLiveDataModel::class.java]
        modelReport.getItemDescription(s).observe(this) { vDataList ->
            // update UI
            progress.visibility = View.GONE
            itemDescription.clear()
            if (vDataList != null && vDataList.isNotEmpty()) {
                itemDescription.addAll(vDataList)

                adapterDetails = ArrayAdapter<String>(
                    this, android.R.layout.simple_spinner_item,
                    itemDescription
                ) //selected item will look like a spinner set from XML

                adapterDetails.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                manufacturerTxt.adapter = adapterDetails


            } else {
                //  ToastUtils.showToast(applicationContext, "Request Failed")
            }
        }
    }


    private fun getReportList() {
        val model = ViewModelProviders.of(this)[ReportLiveDataModel::class.java]

        var reportsID =
            WMSSharedPref.getAppPrefs(WMSApplication.getInstance().context)
                .getStringValue("BARCODE_ID_VALUE")

        var loginID =
            WMSSharedPref.getAppPrefs(WMSApplication.getInstance().context)
                .getStringValue(PrefConstant.LOGIN_ID)

        val requestData = BarcodeRequestBin(
            reportsID,
            manufacturerTxt.selectedItem.toString(),
            loginID,
            "",
            0,
            itemCodetxt.text.toString(),
            binNo.text.toString(),
            loginID,
            ""
        )

        model.getBarcodeStatus(requestData).observe(this) { vDataList ->
            // update UI
            progress.visibility = View.GONE
            if (vDataList != null && vDataList == "success") {
                ToastUtils.showToast(applicationContext, "Barcode Submitted Successfully")
                val intent = intent
                overridePendingTransition(0, 0)
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                finish()
                overridePendingTransition(0, 0)
                startActivity(intent)

            } else {
                if(vDataList != null&&vDataList != "success")
                    ToastUtils.showToast(applicationContext, vDataList)
                else
                ToastUtils.showToast(applicationContext, "Request Failed")
            }
        }
    }

    override fun onResume() {
        super.onResume()
        registerReceivers()
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(myBroadcastReceiver)
        unRegisterScannerStatus()
    }

    fun loadItemCode() {
        itemCodetxt.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                //retrieve data s
            }

            override fun afterTextChanged(s: Editable) {
                getItemCode(s.toString())
//                itemDescription.clear()
//                adapterDetails.notifyDataSetChanged()

            }
        })
    }

    companion object {
        private var instances2: NewScannerPage? = null
        fun getInstance(): NewScannerPage? {
            return instances2
        }
    }


    private fun registerReceivers() {
        Log.d("LOG", "registerReceivers()")
        val filter = IntentFilter()
        filter.addAction("com.symbol.datawedge.api.NOTIFICATION_ACTION") // for notification result
        filter.addAction("com.symbol.datawedge.api.ACTION") // for error code result
        filter.addCategory(Intent.CATEGORY_DEFAULT) // needed to get version info

        // register to received broadcasts via DataWedge scanning
        filter.addAction("com.tvhht.myapplication.ACTION")
        filter.addAction("com.tvhht.myapplication.service.ACTION")
        registerReceiver(myBroadcastReceiver, filter)
    }

    // Unregister scanner status notification
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


    private val myBroadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action
            val b = intent.extras
            Log.d("LOG", "DataWedge Action:$action")

            // Get DataWedge version info
            if (intent.hasExtra("com.symbol.datawedge.api.RESULT_GET_VERSION_INFO")) {
                val versionInfo =
                    intent.getBundleExtra("com.symbol.datawedge.api.RESULT_GET_VERSION_INFO")
                val DWVersion = versionInfo!!.getString("DATAWEDGE")
            }
            if (action == "com.tvhht.myapplication.ACTION") {
                //  Received a barcode scan
                try {
                    displayScanResult(intent, "via Broadcast")
                } catch (e: Exception) {
                    //  Catch error if the UI does not exist when we receive the broadcast...
                }
            }
        }
    }

    private fun displayScanResult(initiatingIntent: Intent, howDataReceived: String) {
        // store decoded data
        val decodedData = initiatingIntent.getStringExtra("com.symbol.datawedge.data_string")
        // store decoder type
        val decodedLabelType = initiatingIntent.getStringExtra("com.symbol.datawedge.label_type")
        if (decodedData.toString().length > 1) {
            getItemCode(decodedData.toString())
        }
    }

}




