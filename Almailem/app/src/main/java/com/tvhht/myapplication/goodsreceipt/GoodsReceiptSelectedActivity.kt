package com.tvhht.myapplication.goodsreceipt

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
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.tvhht.myapplication.R
import com.tvhht.myapplication.cases.ScannerErrorCustomDialog
import com.tvhht.myapplication.databinding.ActivityGoodsReceiptSelectedBinding
import com.tvhht.myapplication.goodsreceipt.ScanBarcodeActivity.Companion.setBarcodeListener
import com.tvhht.myapplication.goodsreceipt.adapter.SelectedDocumentAdapter
import com.tvhht.myapplication.goodsreceipt.model.SelectedDocumentResponse
import com.tvhht.myapplication.goodsreceipt.utils.BarcodeScanDialog
import com.tvhht.myapplication.goodsreceipt.viewmodel.GoodsReceiptSelectedViewModel
import com.tvhht.myapplication.home.HomePageActivity
import com.tvhht.myapplication.utils.ApiConstant
import com.tvhht.myapplication.utils.LogoutFragment
import com.tvhht.myapplication.utils.NetworkUtils
import com.tvhht.myapplication.utils.PrefConstant
import com.tvhht.myapplication.utils.WMSApplication
import com.tvhht.myapplication.utils.WMSSharedPref
import kotlinx.android.synthetic.main.tool_bar.toolbar
import kotlinx.android.synthetic.main.tool_bar.view.sign_out

class GoodsReceiptSelectedActivity : AppCompatActivity() {
    private lateinit var binding: ActivityGoodsReceiptSelectedBinding
    private lateinit var viewModel: GoodsReceiptSelectedViewModel
    private lateinit var selectedDocumentAdapter: SelectedDocumentAdapter
    private var selectedList: MutableList<SelectedDocumentResponse> = ArrayList()
    private var originalList: MutableList<SelectedDocumentResponse> = ArrayList()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGoodsReceiptSelectedBinding.inflate(layoutInflater)
        setContentView(binding.root)
        instances = this
        initViewModel()
        val loginUserId = WMSSharedPref.getAppPrefs(
            WMSApplication.getInstance().context
        ).getStringValue(PrefConstant.LOGIN_ID)
        binding.grUser.text = loginUserId
        setObservers()
        verifyToken(ApiConstant.GR_SELECTED_DOCUMENT_ID)
        toolbar.sign_out.setOnClickListener {
            if (supportFragmentManager.findFragmentByTag("logout_fragment") == null) {
                val logoutFragment = LogoutFragment()
                supportFragmentManager.beginTransaction().add(logoutFragment, "logout_fragment")
                    .commitAllowingStateLoss()
            }
        }
        setBarcodeListener(object : ScanBarcodeListener {
            override fun onReload(document: SelectedDocumentResponse) {
                verifyToken(ApiConstant.GR_SELECTED_DOCUMENT_ID)
            }
        })
        setListener()
    }

    private fun initViewModel() {
        viewModel = ViewModelProvider(this)[GoodsReceiptSelectedViewModel::class.java]
    }

    private fun verifyToken(transactionId: Int) {
        /* binding.progress.visibility = View.VISIBLE
         val request = LoginModel(
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
                     getSelectedDocumentList()
                 }
             }
         }*/

        if (!NetworkUtils().haveNetworkConnection(applicationContext)) {
            Handler().postDelayed({
                // progress.visibility = View.GONE
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
            val apiServiceName = when (transactionId) {
                ApiConstant.GR_FIND_IM_PARTNER_ID -> ApiConstant.apiName_masters
                else -> ApiConstant.apiName_transaction
            }
            viewModel.getAuthToken(transactionId, apiServiceName)
        }
    }

    private fun initIntentValue() {
        intent?.extras?.let {
            val caseCode = it.getString("CaseCode")
            val stagingNo = it.getString("StagingNo")
            caseCode?.let { it1 -> viewModel.caseCodeList.add(it1) }
            stagingNo?.let { it1 -> viewModel.stagingNoList.add(it1) }
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
        viewModel.barcode = decodedData.toString()

        if (decodedData.isNullOrEmpty().not() && isBarcodeAvailable(decodedData.toString())) {
            val documentResponse =
                selectedList.find { it.partnerItemBarcode == decodedData.toString() }
            if (documentResponse != null) {
                if (supportFragmentManager.findFragmentByTag("barcode_scan_fragment") == null) {
                    val barcodeScan =
                        BarcodeScanDialog(decodedData.toString(), documentResponse, originalList)
                    barcodeScan.let { it1 ->
                        supportFragmentManager.beginTransaction()
                            .add(it1, "barcode_scan_fragment")
                            .commitAllowingStateLoss()
                    }
                }
                /*documentResponse.itemCode?.let { code ->
                    viewModel.itemCode = code
                }
                documentResponse.manufacturerCode?.let { partnerCode ->
                    viewModel.businessPartnerCode = partnerCode
                }
                verifyToken(ApiConstant.GR_FIND_IM_PARTNER_ID)*/
            } else {
                invalidBarcodeDialog()
            }
        } else {
            invalidBarcodeDialog()
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

    private fun isBarcodeAvailable(code: String): Boolean {
        val documentResponse: SelectedDocumentResponse? =
            selectedList.find { it.partnerItemBarcode == code }
        documentResponse?.let {
            it.isSelected = true
            if (::selectedDocumentAdapter.isInitialized) {
                selectedDocumentAdapter.updateAdapter(selectedList)
            }
            return true
        }
        return false
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
        private var instances: GoodsReceiptSelectedActivity? = null
        fun getInstance(): GoodsReceiptSelectedActivity? {
            return instances
        }
    }

    private fun setObservers() {
        viewModel.loaderLiveData.observe(this) {
            binding.progress.visibility = if (it) View.VISIBLE else View.GONE
        }
        viewModel.selectedDocumentList.observe(this) { list ->
            if (list.isNullOrEmpty().not()) {
                with(binding) {
                    originalList = list.toMutableList()
                    val distinctList = list.distinctBy { Pair(it.itemCode, it.manufacturerName) }
                    selectedList = distinctList.toMutableList()
                    grCount.text = selectedList.size.toString()

                    if (::selectedDocumentAdapter.isInitialized.not()) {
                        val layoutManager = LinearLayoutManager(this@GoodsReceiptSelectedActivity)
                        selectedDocumentList.layoutManager = layoutManager
                        selectedDocumentList.addItemDecoration(
                            DividerItemDecoration(
                                this@GoodsReceiptSelectedActivity,
                                DividerItemDecoration.VERTICAL
                            )
                        )
                        selectedDocumentAdapter =
                            SelectedDocumentAdapter(selectedList) { selectedDocument ->
                                val groupList =
                                    originalList.groupBy { it.itemCode }[selectedDocument.itemCode]?.filter { it.manufacturerName == selectedDocument.manufacturerName }
                                        ?: emptyList()
                                val intent = Intent(
                                    this@GoodsReceiptSelectedActivity,
                                    ScanBarcodeActivity::class.java
                                )
                                intent.putExtra(
                                    "SELECTED_DOCUMENT",
                                    Gson().toJson(selectedDocument)
                                )
                                intent.putParcelableArrayListExtra(
                                    "PART_NO_LIST",
                                    ArrayList(groupList)
                                )
                                startActivity(intent)
                            }
                        selectedDocumentList.adapter = selectedDocumentAdapter
                    } else {
                        selectedDocumentAdapter.updateAdapter(selectedList)
                    }
                    initAutocompleteTextView()
                }
            }
            // binding.progress.visibility = View.GONE
        }
        viewModel.findImPartner.observe(this) { imPartnerList ->
            if (imPartnerList.isNullOrEmpty().not()) {
                val filterBarcode =
                    imPartnerList.filter { it.partnerItemBarcode == viewModel.barcode }
                if (filterBarcode.isNotEmpty()) {
                    val documentResponse =
                        selectedList.find { it.partnerItemBarcode == viewModel.barcode }
                    documentResponse?.let {
                        if (supportFragmentManager.findFragmentByTag("barcode_scan_fragment") == null) {
                            val barcodeScan = BarcodeScanDialog(viewModel.barcode, it, originalList)
                            barcodeScan.let { it1 ->
                                supportFragmentManager.beginTransaction()
                                    .add(it1, "barcode_scan_fragment")
                                    .commitAllowingStateLoss()
                            }
                        }
                    }
                } else {
                    invalidBarcodeDialog()
                }
            } else {
                invalidBarcodeDialog()
            }
        }
    }

    private fun invalidBarcodeDialog() {
        val cdd = ScannerErrorCustomDialog(
            this@GoodsReceiptSelectedActivity,
            "Invalid Barcode",
            viewModel.barcode, 1,
        )
        cdd.show()
    }

    override fun onBackPressed() {
        HomePageActivity.getInstance()?.reload()
        super.onBackPressed()
    }

    private fun initAutocompleteTextView() {
        val distinctList = originalList.distinctBy { it.itemCode }
        val partNoList: MutableList<String> = mutableListOf()
        partNoList.add("All")
        for (goodReceipt in distinctList) {
            goodReceipt.itemCode?.let { partNoList.add(it) }
        }
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, partNoList)
        binding.partNumberAutoCompleteView.threshold = 1
        binding.partNumberAutoCompleteView.setAdapter(adapter)
    }

    private fun setListener() {
        with(binding) {
            imgFilter.setOnClickListener {
                if (partNumberAutoCompleteView.text.toString().isNotEmpty()) {
                    hideKeyboard()
                    val filteredList: List<SelectedDocumentResponse> =
                        if (partNumberAutoCompleteView.text.toString() == "All") selectedList else selectedList.filter { it.itemCode == partNumberAutoCompleteView.text.toString() }
                    setAdapter(filteredList)
                    grCount.text = filteredList.size.toString()
                }
            }
            imgClear.setOnClickListener {
                hideKeyboard()
                partNumberAutoCompleteView.text = null
                setAdapter(selectedList)
                grCount.text = selectedList.size.toString()
            }
        }
    }

    private fun setAdapter(updateList: List<SelectedDocumentResponse>) {
        if (::selectedDocumentAdapter.isInitialized) {
            selectedDocumentAdapter.updateAdapter(updateList)
        }
    }

    private fun hideKeyboard() {
        try {
            val imm: InputMethodManager =
                getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}