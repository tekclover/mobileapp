package com.tvhht.myapplication.quality

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.tvhht.myapplication.R
import com.tvhht.myapplication.cases.ScannerErrorCustomDialog
import com.tvhht.myapplication.databinding.ActivityQualityHeNoBinding
import com.tvhht.myapplication.quality.adapter.QualityHeNumberListAdapter
import com.tvhht.myapplication.quality.model.QualityListResponse
import com.tvhht.myapplication.quality.viewmodel.QualityHeNumberViewModel
import com.tvhht.myapplication.utils.LogoutFragment
import com.tvhht.myapplication.utils.PrefConstant
import com.tvhht.myapplication.utils.WMSApplication
import com.tvhht.myapplication.utils.WMSSharedPref
import kotlinx.android.synthetic.main.tool_bar.toolbar
import kotlinx.android.synthetic.main.tool_bar.view.sign_out

class QualityHeNumberActivity : AppCompatActivity() {
    private lateinit var binding: ActivityQualityHeNoBinding
    private lateinit var viewModel: QualityHeNumberViewModel
    private lateinit var adapter: QualityHeNumberListAdapter
    private var heNoList: List<QualityListResponse> = ArrayList()
    private var distinctHeNoList: List<QualityListResponse> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityQualityHeNoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initViewModel()
        initIntentValue()
        heNumberObserver()
        WMSSharedPref.getAppPrefs(WMSApplication.getInstance().context)
            .getStringValue(PrefConstant.LOGIN_ID)?.let {
                binding.qualityUser.text = it
            }

        toolbar.sign_out.setOnClickListener {
            if (supportFragmentManager.findFragmentByTag("logout_fragment") == null) {
                val logoutFragment = LogoutFragment()
                supportFragmentManager.beginTransaction().add(logoutFragment, "logout_fragment")
                    .commitAllowingStateLoss()
            }
        }
    }

    private fun initViewModel() {
        viewModel = ViewModelProvider(this)[QualityHeNumberViewModel::class.java]
    }

    private fun initIntentValue() {
        if (intent != null) {
            viewModel.orderNo = intent.getStringExtra("ORDER_NO") ?: ""
            viewModel.getHeNumberList()
        }
    }

    private fun heNumberObserver() {
        viewModel.heNumberLiveData.observe(this) { response ->
            if (response.isNullOrEmpty().not()) {
                heNoList = response
                distinctHeNoList = heNoList.distinctBy { it.actualHeNo }
                setAdapter(distinctHeNoList)
            } else {
                finish()
                QualityListActivity.getInstance()?.reload()
            }
        }
        viewModel.deleteLiveData.observe(this) {
            if (it) {
                viewModel.getHeNumberList()
            }
        }
    }

    private fun setAdapter(heNoList: List<QualityListResponse>) {
        with(binding) {
            if (::adapter.isInitialized.not()) {
                val layoutManager = LinearLayoutManager(this@QualityHeNumberActivity)
                qualityHeNoList.layoutManager = layoutManager
                qualityHeNoList.addItemDecoration(
                    DividerItemDecoration(
                        this@QualityHeNumberActivity,
                        DividerItemDecoration.VERTICAL
                    )
                )
                adapter = QualityHeNumberListAdapter(heNoList) { heNumber ->
                    // val groupByHeNo = heNoList.groupBy { it.actualHeNo }
                    //val orderNoList = groupByHeNo[heNumber]
                    viewModel.selectedHeNumber = heNumber
                    navigateToQualityDetailsPage()
                }
                qualityHeNoList.adapter = adapter
            } else {
                adapter.updateAdapter(heNoList)
            }
            qualityHeNoCount.text = heNoList.size.toString()
        }
    }

    private fun navigateToQualityDetailsPage() {
        val myIntent = Intent(this@QualityHeNumberActivity, QualityDetailsActivity::class.java)
        myIntent.putExtra("ACTUAL_HE_NUMBER", viewModel.selectedHeNumber)
        myIntent.putExtra("ORDER_NO", viewModel.orderNo)
        activityLauncher.launch(myIntent)
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

        if (decodedData.isNullOrEmpty().not() && isHeNumberAvailable(decodedData.toString())) {
            viewModel.selectedHeNumber = decodedData.toString()
            navigateToQualityDetailsPage()
        } else {
            invalidBarcodeDialog(decodedData.toString())
        }
    }

    private fun isHeNumberAvailable(scanResult: String): Boolean {
        val findHeNo: QualityListResponse? = distinctHeNoList.find { it.actualHeNo == scanResult }
        findHeNo?.let {
            return true
        }
        return false
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

    private fun invalidBarcodeDialog(scanResult: String) {
        val cdd = ScannerErrorCustomDialog(
            this@QualityHeNumberActivity,
            "Invalid HE Number",
            scanResult, 1,
        )
        cdd.show()
    }
    private val activityLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            val resultCode = result.resultCode
            val data = result.data
            if (resultCode == Activity.RESULT_OK && data != null) {
                val deleteHeNumber = data.getStringExtra("DELETE_HE_NO")
                deleteHeNumber?.let { viewModel.deleteRecord(it) }
            }
        }

    override fun onBackPressed() {
        finish()
        QualityListActivity.getInstance()?.reload()
        super.onBackPressed()
    }
}