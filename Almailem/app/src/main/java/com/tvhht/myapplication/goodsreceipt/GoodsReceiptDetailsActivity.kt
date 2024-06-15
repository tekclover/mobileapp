package com.tvhht.myapplication.goodsreceipt

import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.tvhht.myapplication.R
import com.tvhht.myapplication.databinding.ActivityGoodsReceiptDetailsBinding
import com.tvhht.myapplication.goodsreceipt.model.HHTUser
import com.tvhht.myapplication.goodsreceipt.model.SelectedDocumentResponse
import com.tvhht.myapplication.goodsreceipt.utils.AssignBinDialog
import com.tvhht.myapplication.goodsreceipt.utils.CBMDialog
import com.tvhht.myapplication.goodsreceipt.viewmodel.GoodsReceiptDetailsViewModel
import com.tvhht.myapplication.home.HomePageActivity
import com.tvhht.myapplication.putaway.utils.ReasonDialog
import com.tvhht.myapplication.utils.ApiConstant
import com.tvhht.myapplication.utils.LogoutFragment
import com.tvhht.myapplication.utils.NetworkUtils
import com.tvhht.myapplication.utils.PrefConstant
import com.tvhht.myapplication.utils.ToastUtils
import com.tvhht.myapplication.utils.WMSApplication
import com.tvhht.myapplication.utils.WMSSharedPref
import kotlinx.android.synthetic.main.activity_quality_details.progress
import kotlinx.android.synthetic.main.tool_bar.toolbar
import kotlinx.android.synthetic.main.tool_bar.view.sign_out


class GoodsReceiptDetailsActivity : AppCompatActivity(), ReasonDialog.ReasonListener,
    CBMDialog.CBMListener {
    private lateinit var binding: ActivityGoodsReceiptDetailsBinding
    private lateinit var viewModel: GoodsReceiptDetailsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGoodsReceiptDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initViewModel()
        setListener()
        val loginUserId = WMSSharedPref.getAppPrefs(
            WMSApplication.getInstance().context
        ).getStringValue(PrefConstant.LOGIN_ID)
        binding.grUser.text = loginUserId
        initIntentValue()
        setObservers()
    }

    private fun initViewModel() {
        viewModel = ViewModelProvider(this)[GoodsReceiptDetailsViewModel::class.java]
    }

    private fun initIntentValue() {
        intent?.extras?.let {
            val document = it.getString("SELECTED_DOCUMENT")
            viewModel.selectedDocumentResponse =
                Gson().fromJson(document, SelectedDocumentResponse::class.java)

            with(binding) {
                invoiceQty.text = (viewModel.selectedDocumentResponse?.orderQty ?: 0).toString()
                releasedAccQty.text =
                    (viewModel.selectedDocumentResponse?.recAcceptQty ?: 0).toString()
                releasedRgjQty.text =
                    (viewModel.selectedDocumentResponse?.recDamageQty ?: 0).toString()
                acceptedQty.setText(
                    (viewModel.selectedDocumentResponse?.acceptedQty ?: "").toString()
                )
            }
        }
    }

    private fun setListener() {
        binding.cbm.setOnClickListener {
            verifyToken(ApiConstant.GR_CBM_ID)
        }
/*
        binding.rejectedQty.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun afterTextChanged(editable: Editable) {
                if (binding.rejectedQty.text.isNullOrEmpty().not()) {
                    Handler(Looper.getMainLooper()).postDelayed({
                        if (supportFragmentManager.findFragmentByTag("reason_fragment") == null) {
                            val reasonFragment = ReasonDialog()
                            val bundle = Bundle()
                            bundle.putString("IS_REASON_FROM", "GoodsReceipt")
                            reasonFragment.arguments = bundle
                            supportFragmentManager.beginTransaction()
                                .add(reasonFragment, "reason_fragment")
                                .commitAllowingStateLoss()
                        }
                    }, 2000)
                }
            }
        })
*/
        binding.acceptedQty.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun afterTextChanged(editable: Editable) {
                try {
                    if (editable.toString().toInt() > (viewModel.selectedDocumentResponse?.orderQty
                            ?: 0)
                    ) {
                        ToastUtils.showToast(
                            applicationContext,
                            this@GoodsReceiptDetailsActivity.resources?.getString(R.string.gr_excess_qty_message)
                                ?.let {
                                    String.format(
                                        it,
                                        (viewModel.selectedDocumentResponse?.orderQty ?: 0)
                                    )
                                }
                        )
                        binding.acceptedQty.setText((viewModel.selectedDocumentResponse?.orderQty ?: "").toString())
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        })
        binding.buttonYes.setOnClickListener {
            val acceptedQty = binding.acceptedQty.text.toString()
            val damagedQty = binding.rejectedQty.text.toString()
            val totalAcceptedQty = binding.releasedAccQty.text.toString()
            val totalRejectedQty = binding.releasedRgjQty.text.toString()
            val invoiceQty = binding.invoiceQty.text.toString()
            viewModel.acceptedQty = if (acceptedQty.isEmpty()) null else acceptedQty.toInt()
            viewModel.damagedQty = if (damagedQty.isEmpty()) null else damagedQty.toInt()
            val totalAcceptedQuantity=if (totalAcceptedQty.isEmpty()) 0 else totalAcceptedQty.toInt()
            val totalRejectedQuantity=if (totalRejectedQty.isEmpty()) 0 else totalRejectedQty.toInt()
            val invoiceQuantity=if (invoiceQty.isEmpty()) 0 else invoiceQty.toInt()
            val sumOfQty = (totalAcceptedQuantity + totalRejectedQuantity + (viewModel.acceptedQty ?: 0) + (viewModel.damagedQty ?: 0))

            when {
                acceptedQty.isEmpty() && damagedQty.isEmpty() -> {
                    ToastUtils.showToast(
                        applicationContext,
                        "Please enter the accepted or rejected qty"
                    )
                }
                sumOfQty > invoiceQuantity -> {
                    ToastUtils.showToast(
                        applicationContext,
                        this@GoodsReceiptDetailsActivity.resources?.getString(R.string.gr_excess_qty_message)
                            ?.let {
                                String.format(
                                    it,
                                    invoiceQuantity
                                )
                            }
                    )
                }

                ((viewModel.damagedQty ?: 0) > 0) -> {
                    if (supportFragmentManager.findFragmentByTag("reason_fragment") == null) {
                        val reasonFragment = ReasonDialog()
                        val bundle = Bundle()
                        bundle.putString("IS_REASON_FROM", "GoodsReceipt")
                        reasonFragment.arguments = bundle
                        supportFragmentManager.beginTransaction()
                            .add(reasonFragment, "reason_fragment")
                            .commitAllowingStateLoss()
                    }
                }

                else -> {
                    verifyToken(ApiConstant.GR_PACK_BARCODE_ID)
                }
            }
        }
        binding.buttonNo.setOnClickListener {
            finish()
           // GoodsReceiptListActivity.getInstance()?.reload()
        }
        toolbar.sign_out.setOnClickListener {
            if (supportFragmentManager.findFragmentByTag("logout_fragment") == null) {
                val logoutFragment = LogoutFragment()
                supportFragmentManager.beginTransaction().add(logoutFragment, "logout_fragment")
                    .commitAllowingStateLoss()
            }
        }
    }

    private fun verifyToken(transactionId: Int) {
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
            val apiServiceName = when (transactionId) {
                ApiConstant.GR_HHT_USER_ID -> ApiConstant.apiName_id_master
                ApiConstant.GR_CBM_ID -> ApiConstant.apiName_masters
                else -> ApiConstant.apiName_transaction
            }
            viewModel.getAuthToken(transactionId, apiServiceName)
        }
    }

    override fun selectedReason(reason: String) {
        viewModel.selectedDocumentResponse?.remarks = reason
        verifyToken(ApiConstant.GR_PACK_BARCODE_ID)
    }

    private fun onUserSelected(user: HHTUser) {
        viewModel.selectedDocumentResponse?.assignedUserId = user.userId
        if (binding.rejectedQty.text.toString().isEmpty() || (binding.rejectedQty.text.toString()
                .toInt() == 0)
        ) {
            viewModel.selectedDocumentResponse?.remarks = ""
        }
        verifyToken(ApiConstant.GR_LINE_ID)
    }

    private fun setObservers() {
        viewModel.packBarcodeLiveData.observe(this) {
            it?.let {
                for (pack in it) {
                    pack.cbm = 0
                    pack.cbmQuantity = 0
                }
                viewModel.selectedDocumentResponse?.packBarcodes = ArrayList(it)
                //verifyToken(ApiConstant.GR_HHT_USER_ID)
                verifyToken(ApiConstant.GR_LINE_ID)
            }
        }

        viewModel.userList.observe(this) { userList ->
            userList?.let {
                if (supportFragmentManager.findFragmentByTag("assign_bin_fragment") == null) {
                    val assignBinDialog = AssignBinDialog(userList, ::onUserSelected)
                    supportFragmentManager.beginTransaction()
                        .add(assignBinDialog, "assign_bin_fragment").commitAllowingStateLoss()
                }
            }
        }
        viewModel.submitGRLine.observe(this) {
            if (it.isNullOrEmpty().not()) {
                ToastUtils.showToast(
                    WMSApplication.getInstance().context,
                    "Goods Receipt Submitted successfully"
                )
                finish()
                GoodsReceiptSelectedActivity.getInstance()?.reload()
            } else {
                ToastUtils.showToast(
                    WMSApplication.getInstance().context,
                    "Unable to update Goods Receipt"
                )
            }
        }
        viewModel.cbmDetails.observe(this) {
            it?.let {
                val cbmResponse = it[0]
                if (supportFragmentManager.findFragmentByTag("cbm_fragment") == null) {
                    val cbmDialog = CBMDialog()
                    val bundle = Bundle()
                    bundle.putString("CBM_RESPONSE", Gson().toJson(cbmResponse))
                    cbmDialog.arguments = bundle
                    supportFragmentManager.beginTransaction()
                        .add(cbmDialog, "cbm_fragment").commitAllowingStateLoss()
                }
            }
        }
        viewModel.loaderLiveData.observe(this) {
            binding.progress.visibility = if (it) View.VISIBLE else View.GONE
        }
    }

    override fun onCBM(length: String, width: String, height: String) {
        viewModel.selectedDocumentResponse?.length = length
        viewModel.selectedDocumentResponse?.width = width
        viewModel.selectedDocumentResponse?.height = height
    }

    override fun onBackPressed() {
      //  GoodsReceiptListActivity.getInstance()?.reload()
        super.onBackPressed()
    }
}