package com.tvhht.myapplication.quality

import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.tvhht.myapplication.R
import com.tvhht.myapplication.databinding.ActivityQualityOrderTrackingReportBinding
import com.tvhht.myapplication.home.HomePageActivity
import com.tvhht.myapplication.quality.adapter.QualityOrderTrackingAdapter
import com.tvhht.myapplication.quality.model.OutboundLineResponse
import com.tvhht.myapplication.quality.viewmodel.QualityOrderTrackingReportViewModel
import com.tvhht.myapplication.utils.DateUtil
import com.tvhht.myapplication.utils.LogoutFragment
import com.tvhht.myapplication.utils.NetworkUtils
import com.tvhht.myapplication.utils.PrefConstant
import com.tvhht.myapplication.utils.WMSApplication
import com.tvhht.myapplication.utils.WMSSharedPref
import kotlinx.android.synthetic.main.activity_quality_details.progress
import kotlinx.android.synthetic.main.tool_bar.toolbar
import kotlinx.android.synthetic.main.tool_bar.view.sign_out

class QualityOrderTrackingReportActivity : AppCompatActivity() {
    lateinit var binding: ActivityQualityOrderTrackingReportBinding
    lateinit var viewModel: QualityOrderTrackingReportViewModel
    lateinit var adapter: QualityOrderTrackingAdapter
    private lateinit var handler: Handler
    private lateinit var runnable: Runnable
    private var outboundLineList: List<OutboundLineResponse> = listOf()
    private var filterList: List<OutboundLineResponse> = listOf()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityQualityOrderTrackingReportBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initViewModel()
        setObserve()
        setListener()
        WMSSharedPref.getAppPrefs(WMSApplication.getInstance().context)
            .getStringValue(PrefConstant.LOGIN_ID)?.let {
                binding.textUser.text = it
            }
        DateUtil.getCurrentDateOnly()?.let {
            binding.textDate.text = it
        }
        toolbar.sign_out.setOnClickListener {
            if (supportFragmentManager.findFragmentByTag("logout_fragment") == null) {
                val logoutFragment = LogoutFragment()
                supportFragmentManager.beginTransaction().add(logoutFragment, "logout_fragment")
                    .commitAllowingStateLoss()
            }
        }
       // verifyToken()
        handler = Handler(Looper.getMainLooper())
        runnable = object : Runnable {
            override fun run() {
                viewModel.selectedSalesOrderNo = ""
                binding.salesOrderNoAutoCompleteView.text = null
                verifyToken()
                handler.postDelayed(this, 60000)
            }
        }
    }

    private fun initViewModel() {
        viewModel = ViewModelProvider(this)[QualityOrderTrackingReportViewModel::class.java]
    }

    private fun setObserve() {
        viewModel.outboundLineLiveData.observe(this) { response ->
            response?.let {
                outboundLineList = it
                filterList = it
                setAdapter(outboundLineList)
                initFilter(outboundLineList)
            }
        }
        viewModel.loaderLiveData.observe(this) {
            binding.progress.visibility = if (it) View.VISIBLE else View.GONE
        }
    }

    private fun setListener() {
        with(binding) {
            lytClearFilter.setOnClickListener {
                viewModel.selectedSalesOrderNo = ""
                hideKeyboard()
                salesOrderNoAutoCompleteView.text = null
                setAdapter(outboundLineList)
            }
            lytFilter.setOnClickListener {
                if (viewModel.selectedSalesOrderNo.isNotEmpty()) {
                    hideKeyboard()
                    val filteredList =
                        filterList.filter { it.salesOrderNumber == viewModel.selectedSalesOrderNo }
                    setAdapter(filteredList)
                }
            }
        }
    }

    private fun verifyToken() {
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
            viewModel.getAuthToken()
        }
    }

    private fun setAdapter(outboundLineList: List<OutboundLineResponse>) {
        if (outboundLineList.isNotEmpty()) {
            val sortedList = outboundLineList.sortedByDescending { it.refDocNumber }
            with(binding) {
                if (::adapter.isInitialized.not()) {
                    val layoutManager = LinearLayoutManager(this@QualityOrderTrackingReportActivity)
                    orderTrackingReportList.layoutManager = layoutManager
                    orderTrackingReportList.addItemDecoration(
                        DividerItemDecoration(
                            this@QualityOrderTrackingReportActivity,
                            DividerItemDecoration.VERTICAL
                        )
                    )
                    adapter = QualityOrderTrackingAdapter(sortedList)
                    orderTrackingReportList.adapter = adapter
                } else {
                    adapter.updateAdapter(sortedList)
                }
            }
        }
    }

    private fun initFilter(outboundLineResponses: List<OutboundLineResponse>) {
        val salesOrderNoList: MutableList<String> = mutableListOf()
        for (outbound in outboundLineResponses) {
            outbound.salesOrderNumber?.let { salesOrderNoList.add(it) }
        }
        val distinctList = salesOrderNoList.distinctBy { it }.sortedBy { it }
        if (distinctList.isNotEmpty()) {
            val salesOrderNoAdapter =
                ArrayAdapter(this, R.layout.adapter_spinner_view, distinctList)
            binding.salesOrderNoAutoCompleteView.setAdapter(salesOrderNoAdapter)
            binding.salesOrderNoAutoCompleteView.threshold = 1
            binding.salesOrderNoAutoCompleteView.onItemClickListener =
                AdapterView.OnItemClickListener { parent, _, position, _ ->
                    viewModel.selectedSalesOrderNo = parent.getItemAtPosition(position).toString()
                }
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

    override fun onResume() {
        super.onResume()
        handler.post(runnable)
    }

    override fun onPause() {
        super.onPause()
        handler.removeCallbacks(runnable)
    }
}