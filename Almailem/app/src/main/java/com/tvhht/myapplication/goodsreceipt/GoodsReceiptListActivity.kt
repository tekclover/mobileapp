package com.tvhht.myapplication.goodsreceipt

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
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
import com.tvhht.myapplication.databinding.ActivityGoodsReceiptListBinding
import com.tvhht.myapplication.goodsreceipt.adapter.GoodsReceiptAdapter
import com.tvhht.myapplication.goodsreceipt.model.GoodsReceiptResponse
import com.tvhht.myapplication.goodsreceipt.model.SelectedDocumentResponse
import com.tvhht.myapplication.goodsreceipt.utils.DocumentNoSelectedDialog
import com.tvhht.myapplication.goodsreceipt.viewmodel.GoodsReceiptViewModel
import com.tvhht.myapplication.home.HomePageActivity
import com.tvhht.myapplication.login.LoginLiveData
import com.tvhht.myapplication.login.model.LoginModel
import com.tvhht.myapplication.utils.ApiConstant
import com.tvhht.myapplication.utils.DateUtil
import com.tvhht.myapplication.utils.LogoutFragment
import com.tvhht.myapplication.utils.NetworkUtils
import com.tvhht.myapplication.utils.PrefConstant
import com.tvhht.myapplication.utils.ToastUtils
import com.tvhht.myapplication.utils.WMSApplication
import com.tvhht.myapplication.utils.WMSSharedPref
import kotlinx.android.synthetic.main.activity_quality_details.progress
import kotlinx.android.synthetic.main.tool_bar.toolbar
import kotlinx.android.synthetic.main.tool_bar.view.sign_out

class GoodsReceiptListActivity : AppCompatActivity() {
    private lateinit var binding: ActivityGoodsReceiptListBinding
    private lateinit var viewModel: GoodsReceiptViewModel
    private lateinit var goodsReceiptAdapter: GoodsReceiptAdapter
    private var filterList: List<SelectedDocumentResponse> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGoodsReceiptListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        instances = this
        initViewModel()
        initIntentValue()
        val loginUserId = WMSSharedPref.getAppPrefs(
            WMSApplication.getInstance().context
        ).getStringValue(PrefConstant.LOGIN_ID)
        binding.grUser.text = loginUserId

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

        toolbar.sign_out.setOnClickListener {
            if (supportFragmentManager.findFragmentByTag("logout_fragment") == null) {
                val logoutFragment = LogoutFragment()
                supportFragmentManager.beginTransaction().add(logoutFragment, "logout_fragment")
                    .commitAllowingStateLoss()
            }
        }
        setListener()
    }

    private fun initViewModel() {
        viewModel = ViewModelProvider(this)[GoodsReceiptViewModel::class.java]
    }

    private fun verifyToken() {
        binding.progress.visibility = View.VISIBLE
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
                    getGoodsReceiptList()
                }
            }
        }
    }

    private fun getGoodsReceiptList() {
        viewModel.getGoodsReceiptList().observe(this) { goodsList ->
            if (goodsList.isNullOrEmpty().not()) {
                viewModel.grHeaderList = goodsList
                with(binding) {
                    val layoutManager = LinearLayoutManager(this@GoodsReceiptListActivity)
                    goodsReceiptList.layoutManager = layoutManager
                    goodsReceiptList.addItemDecoration(
                        DividerItemDecoration(
                            this@GoodsReceiptListActivity,
                            DividerItemDecoration.VERTICAL
                        )
                    )
                    goodsReceiptAdapter = GoodsReceiptAdapter(viewModel.partNoList?: listOf()) { selectedDocument ->
                        if (supportFragmentManager.findFragmentByTag("document_no_selected_fragment") == null) {
                            val documentNoSelectedDialog = DocumentNoSelectedDialog()
                            val bundle = Bundle()
                            val findList = viewModel.grHeaderList.find { it.refDocNumber == selectedDocument.refDocNumber }
                            selectedDocument.goodsReceiptNo = findList?.goodsReceiptNo ?: ""
                            selectedDocument.partnerItemBarcode = viewModel.selectedDocument?.partnerItemBarcode ?: ""
                            //viewModel.selectedDocument?.goodsReceiptNo = selectedDocument.goodsReceiptNo ?: ""
                            bundle.putString("Good_receipt", Gson().toJson(selectedDocument))
                            documentNoSelectedDialog.arguments = bundle
                            supportFragmentManager.beginTransaction()
                                .add(documentNoSelectedDialog, "document_no_selected_fragment")
                                .commitAllowingStateLoss()
                        }
                    }
                    goodsReceiptList.adapter = goodsReceiptAdapter
                    /*grCount.text = sortedList.size.toString()
                    filterList = sortedList*/
                    grCount.text = viewModel.partNoList?.size.toString()
                    filterList = viewModel.partNoList?: listOf()
                }
                initAutocompleteTextView(filterList)
            }
            binding.progress.visibility = View.GONE
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
        private var instances: GoodsReceiptListActivity? = null
        fun getInstance(): GoodsReceiptListActivity? {
            return instances
        }
    }

    private fun initAutocompleteTextView(goodsReceiptList: List<SelectedDocumentResponse>) {
        val orderNoList: MutableList<String> = mutableListOf()
        orderNoList.add("All")
        for (goodReceipt in goodsReceiptList) {
            goodReceipt.refDocNumber?.let { orderNoList.add(it) }
        }
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, orderNoList)
        binding.autoCompleteViewOrderNumber.threshold = 1
        binding.autoCompleteViewOrderNumber.setAdapter(adapter)
    }
    private fun setListener() {
        with(binding) {
            imgFilter.setOnClickListener {
                if (autoCompleteViewOrderNumber.text.toString().isNotEmpty()) {
                    hideKeyboard()
                    val filteredList: List<SelectedDocumentResponse> =
                        if (autoCompleteViewOrderNumber.text.toString() == "All") filterList else filterList.filter { it.refDocNumber == autoCompleteViewOrderNumber.text.toString() }
                    setAdapter(filteredList)
                    grCount.text = filteredList.size.toString()
                }
            }
            imgClear.setOnClickListener {
                hideKeyboard()
                autoCompleteViewOrderNumber.text = null
                setAdapter(filterList)
                grCount.text = filterList.size.toString()
            }
        }
    }
    private fun setAdapter(updateList: List<SelectedDocumentResponse>) {
        if (::goodsReceiptAdapter.isInitialized) {
            goodsReceiptAdapter.updateAdapter(updateList)
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
    private fun initIntentValue() {
        intent?.extras?.let {
            val document = it.getString("SELECTED_DOCUMENT")
            viewModel.selectedDocument =
                Gson().fromJson(document, SelectedDocumentResponse::class.java)
            viewModel.partNoList = intent.getParcelableArrayListExtra("PART_NO_LIST")
        }
    }
}