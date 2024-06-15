package com.tvhht.myapplication.reports


import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView.OnItemClickListener
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.tvhht.myapplication.R
import com.tvhht.myapplication.login.model.LoginModel
import com.tvhht.myapplication.reports.adapter.FindItemCodeAdapter
import com.tvhht.myapplication.reports.adapter.ReportItemCodeAdapter
import com.tvhht.myapplication.reports.model.FindItemCodeRequest
import com.tvhht.myapplication.reports.model.ReportRequestItemCode
import com.tvhht.myapplication.reports.model.ReportResponse
import com.tvhht.myapplication.reports.viewmodel.ReportLiveDataModel
import com.tvhht.myapplication.utils.ApiConstant
import com.tvhht.myapplication.utils.LogoutFragment
import com.tvhht.myapplication.utils.NetworkUtils
import com.tvhht.myapplication.utils.PrefConstant
import com.tvhht.myapplication.utils.ToastUtils
import com.tvhht.myapplication.utils.WMSApplication
import com.tvhht.myapplication.utils.WMSSharedPref
import kotlinx.android.synthetic.main.activity_report_item_code.Recycler_view
import kotlinx.android.synthetic.main.activity_report_item_code.auto_complete_edit_text
import kotlinx.android.synthetic.main.activity_report_item_code.progress
import kotlinx.android.synthetic.main.activity_report_item_code.putaway_txt_user
import kotlinx.android.synthetic.main.activity_report_item_code.reloadReports
import kotlinx.android.synthetic.main.activity_report_item_code.scanReports
import kotlinx.android.synthetic.main.tool_bar.toolbar
import kotlinx.android.synthetic.main.tool_bar.view.sign_out


class ReportListFromItemCodeActivity : AppCompatActivity() {

    var isScanned: Boolean = false
    private var reportDataList: List<ReportResponse> = ArrayList()
    private var handler: Handler? = null
    private lateinit var autocompleteAdapter: FindItemCodeAdapter
    private lateinit var viewModel: ReportLiveDataModel
    private var selectedItemCode = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_report_item_code)
        initViewModel()
        Recycler_view.layoutManager = LinearLayoutManager(
            this
        )
        instances2 = this

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

        Recycler_view.addItemDecoration(
            DividerItemDecoration(
                Recycler_view.context,
                DividerItemDecoration.VERTICAL
            )
        )

        val loginID =
            WMSSharedPref.getAppPrefs(WMSApplication.getInstance().context)
                .getStringValue(PrefConstant.LOGIN_ID)
        putaway_txt_user.text = loginID

        progress.visibility = View.GONE


        scanReports.setOnClickListener {
            hideKeyboard()
            if (selectedItemCode.isNotEmpty()) {
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
                    viewModel.transactionId = ApiConstant.REPORT_FROM_ITEM_CODE_ID
                    viewModel.getAuthToken(ApiConstant.apiName_transaction)
                }
            } else {
                ToastUtils.showToast(
                    applicationContext,
                    "Please enter Item Code"
                )
            }
        }

        reloadReports.setOnClickListener {
           hideKeyboard()
            val intent = intent
            overridePendingTransition(0, 0)
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
            finish()
            overridePendingTransition(0, 0)
            startActivity(intent)
        }
        initAutocompleteAdapter()
        setObserver()
    }

    private fun initViewModel(){
        viewModel = ViewModelProvider(this)[ReportLiveDataModel::class.java]
    }

    private fun initAutocompleteAdapter(){
        autocompleteAdapter = FindItemCodeAdapter(this, R.layout.adapter_spinner_view)
        auto_complete_edit_text.threshold = 1
        auto_complete_edit_text.setAdapter(autocompleteAdapter)
        auto_complete_edit_text.onItemClickListener =
            OnItemClickListener { parent, _, position, _ ->
                selectedItemCode = parent.getItemAtPosition(position).toString()
            }

        auto_complete_edit_text.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(
                s: CharSequence, start: Int, before: Int,
                count: Int
            ) {
                handler?.removeMessages(TRIGGER_AUTO_COMPLETE)
                handler?.sendEmptyMessageDelayed(
                    TRIGGER_AUTO_COMPLETE,
                    AUTO_COMPLETE_DELAY
                )
            }

            override fun afterTextChanged(s: Editable) {}
        })

        handler = Handler(Looper.getMainLooper()) { handlerMessage ->
            if (handlerMessage.what == TRIGGER_AUTO_COMPLETE) {
                if (auto_complete_edit_text.text.isNullOrEmpty().not()) {
                    val request = LoginModel(
                        ApiConstant.apiName_masters,
                        ApiConstant.clientId,
                        ApiConstant.clientSecretKey,
                        ApiConstant.grantType,
                        ApiConstant.apiName_name,
                        ApiConstant.apiName_pass_key
                    )
                    viewModel.transactionId = ApiConstant.FIND_ITEM_CODE_ID
                    viewModel.getAuthToken(ApiConstant.apiName_masters)
                }
            }
            false
        }
    }

    private fun getReportList() {

        val wareId = WMSSharedPref.getAppPrefs(
            WMSApplication.getInstance().context
        ).getStringValue(PrefConstant.WARE_HOUSE_ID)


        val binID: MutableList<String> = ArrayList()
        val wareID: MutableList<String> = ArrayList()
        val binClass: MutableList<Int> = ArrayList()


        binID.add(selectedItemCode)
        wareID.add(wareId)
        binClass.add(1)
        val userId = WMSSharedPref.getAppPrefs(
            WMSApplication.getInstance().context
        ).getStringValue(PrefConstant.LOGIN_ID)

        val requestData = ReportRequestItemCode(wareID, binID,userId)

        viewModel.getReportsFromItemCode(requestData).observe(this) { vDataList ->
            // update UI
            viewModel.loaderLiveData.value = false
            if (vDataList != null)
                reportDataList = (vDataList as List<ReportResponse>?)!!

            var sizeList = reportDataList.size.toString()
            Recycler_view.adapter = ReportItemCodeAdapter(this, reportDataList)
        }
    }

    override fun onResume() {
        super.onResume()

    }


    companion object {
        const val TRIGGER_AUTO_COMPLETE = 100
        const val AUTO_COMPLETE_DELAY: Long = 300
        private var instances2: ReportListFromItemCodeActivity? = null
        fun getInstance(): ReportListFromItemCodeActivity? {
            return instances2
        }
    }

    private fun getItemCode() {
        val wareId: String =
            WMSSharedPref.getAppPrefs(WMSApplication.getInstance().context)
                .getStringValue(PrefConstant.WARE_HOUSE_ID)
        val companyCodeId: String =
            WMSSharedPref.getAppPrefs(WMSApplication.getInstance().context)
                .getStringValue(PrefConstant.COMPANY_CODE_ID)
        val languageId: String =
            WMSSharedPref.getAppPrefs(WMSApplication.getInstance().context)
                .getStringValue(PrefConstant.LANGUAGE_ID)
        val plantId: String =
            WMSSharedPref.getAppPrefs(WMSApplication.getInstance().context)
                .getStringValue(PrefConstant.PLANT_ID)
        val request = FindItemCodeRequest(
            companyCodeId, languageId, plantId, wareId, auto_complete_edit_text.text.toString()
        )
        viewModel.findItemCode(request)
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

    private fun setObserver() {
        viewModel.loaderLiveData.observe(this) {
            progress.visibility = if (it) View.VISIBLE else View.GONE
        }
        viewModel.authToken.observe(this) {
            when {
                it.equals("ERROR") -> {
                    ToastUtils.showToast(applicationContext, "User not Available")
                }

                it.equals("FAILED") -> {
                    ToastUtils.showToast(applicationContext, "No Data Available")
                }

                viewModel.transactionId == ApiConstant.REPORT_FROM_ITEM_CODE_ID -> {
                    getReportList()
                }

                viewModel.transactionId == ApiConstant.FIND_ITEM_CODE_ID -> {
                    getItemCode()
                }
            }
        }
        viewModel.findItemCodeLiveData.observe(this) {
            it?.let {
                val itemCodeList: MutableList<String> = mutableListOf()
                for (findItemCode in it) {
                    findItemCode.itemCode?.let { it1 -> itemCodeList.add(it1) }
                }
                if (::autocompleteAdapter.isInitialized) {
                    autocompleteAdapter.updateAdapter(itemCodeList)
                    autocompleteAdapter.notifyDataSetChanged()
                }
                hideKeyboard()
            }
        }
    }
}


