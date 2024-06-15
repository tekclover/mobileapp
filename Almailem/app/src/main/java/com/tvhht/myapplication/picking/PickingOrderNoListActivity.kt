package com.tvhht.myapplication.picking

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
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.tvhht.myapplication.R
import com.tvhht.myapplication.cases.ScannerErrorCustomDialog
import com.tvhht.myapplication.databinding.ActivityPickingOrderNoListBinding
import com.tvhht.myapplication.home.HomePageActivity
import com.tvhht.myapplication.outboundreturns.ReturnPickingDetailsActivity
import com.tvhht.myapplication.outboundreturns.ReturnPickingListActivity
import com.tvhht.myapplication.picking.AddNewBarcodeActivity.Companion.setUpdateBarcodeListener
import com.tvhht.myapplication.picking.adapter.PickingOrderNoListAdapter
import com.tvhht.myapplication.picking.model.AddNewBarcodeResponse
import com.tvhht.myapplication.picking.model.PickingListResponse
import com.tvhht.myapplication.picking.utils.BinNoScanDialog
import com.tvhht.myapplication.picking.utils.UpdateNewBarcodeListener
import com.tvhht.myapplication.picking.viewmodel.PickingOrderNoViewModel
import com.tvhht.myapplication.transfers.model.InventoryModel
import com.tvhht.myapplication.utils.ApiConstant
import com.tvhht.myapplication.utils.LogoutFragment
import com.tvhht.myapplication.utils.NetworkUtils
import com.tvhht.myapplication.utils.PrefConstant
import com.tvhht.myapplication.utils.WMSApplication
import com.tvhht.myapplication.utils.WMSSharedPref
import kotlinx.android.synthetic.main.activity_quality_details.progress
import kotlinx.android.synthetic.main.tool_bar.toolbar
import kotlinx.android.synthetic.main.tool_bar.view.sign_out

class PickingOrderNoListActivity : AppCompatActivity(),
    BinNoScanDialog.BinNumberScanDialogListener {
    private lateinit var binding: ActivityPickingOrderNoListBinding
    private lateinit var viewModel: PickingOrderNoViewModel
    private lateinit var adapter: PickingOrderNoListAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPickingOrderNoListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initViewModel()
        initIntentValue()
        instances = this
        val loginId =
            WMSSharedPref.getAppPrefs(WMSApplication.getInstance().context)
                .getStringValue(PrefConstant.LOGIN_ID)
        binding.pickingUser.text = loginId
        binding.headerTt.text = this.resources.getString(
            when (viewModel.intentFrom) {
                "PICKING_LIST" -> R.string.picking_txt
                "RETURN_PICKING_LIST" -> R.string.picking_returns
                else -> R.string.picking_txt
            }
        )
        toolbar.sign_out.setOnClickListener {
            if (supportFragmentManager.findFragmentByTag("logout_fragment") == null) {
                val logoutFragment = LogoutFragment()
                supportFragmentManager.beginTransaction().add(logoutFragment, "logout_fragment")
                    .commitAllowingStateLoss()
            }
        }
        setAdapter()
        inventoryObserver()

        setUpdateBarcodeListener(object : UpdateNewBarcodeListener {
            override fun onBarcodeUpdate(response: AddNewBarcodeResponse) {
                viewModel.orderNoList?.find { it.itemCode == response.itemCode }?.apply {
                    barcodeId = response.partnerItemBarcode ?: ""
                }
            }
        })
    }

    private fun initViewModel() {
        viewModel = ViewModelProvider(this)[PickingOrderNoViewModel::class.java]
    }

    private fun initIntentValue() {
        if (intent != null) {
            val intent = intent
            viewModel.orderNoList = intent.getParcelableArrayListExtra("ORDER_NO_LIST")
            if (intent.hasExtra("INTENT_FROM_ORDER_NO")) {
                viewModel.intentFrom = intent.getStringExtra("INTENT_FROM_ORDER_NO") ?: ""
            }
        }
    }

    private fun setAdapter() {
        with(binding) {
            viewModel.orderNoList?.let { orderList ->
                val sortedList = orderList.sortedBy { it.proposedStorageBin }
                if (::adapter.isInitialized.not()) {
                    val layoutManager = LinearLayoutManager(this@PickingOrderNoListActivity)
                    pickingOrderNoList.layoutManager = layoutManager
                    pickingOrderNoList.addItemDecoration(
                        DividerItemDecoration(
                            this@PickingOrderNoListActivity,
                            DividerItemDecoration.VERTICAL
                        )
                    )
                    adapter = PickingOrderNoListAdapter(sortedList) {
                        viewModel.selectedPicking = it
                        verifyToken(ApiConstant.PICKING_INVENTORY_ID)
                    }
                    pickingOrderNoList.adapter = adapter
                } else {
                    adapter.updateAdapter(sortedList)
                }
                pickingCount.text = orderList.size.toString()
            }
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
        viewModel.binNo = decodedData.toString()
        val pickingCombineResponse: PickingListResponse? =
            viewModel.orderNoList?.find { it.proposedStorageBin == viewModel.binNo }

        if (decodedData.isNullOrEmpty().not() && pickingCombineResponse != null) {
            if (supportFragmentManager.findFragmentByTag("bin_no_scan_fragment") == null) {
                val binNoScan =
                    BinNoScanDialog(decodedData.toString(), pickingCombineResponse, this)
                binNoScan.let { it1 ->
                    supportFragmentManager.beginTransaction()
                        .add(it1, "bin_no_scan_fragment")
                        .commitAllowingStateLoss()
                }
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

    private fun invalidBarcodeDialog() {
        val cdd = ScannerErrorCustomDialog(
            this@PickingOrderNoListActivity,
            "Invalid Bin Number",
            viewModel.binNo, 1,
        )
        cdd.show()
    }

    companion object {
        private var instances: PickingOrderNoListActivity? = null
        fun getInstance(): PickingOrderNoListActivity? {
            return instances
        }
    }

    fun reload(storageBin: String, isFrom: String) {
        viewModel.intentFrom = isFrom
        val filterList = viewModel.orderNoList?.filter { it.proposedStorageBin != storageBin }
        viewModel.orderNoList = filterList
        if (viewModel.orderNoList.isNullOrEmpty().not()) {
            setAdapter()
        } else {
            finish()
            navigateToListPage()
        }
    }

    private fun navigateToListPage() {
        when (viewModel.intentFrom) {
            "PICKING_LIST" -> {
                PickingListActivity.getInstance()?.reload()
            }

            "RETURN_PICKING_LIST" -> {
                ReturnPickingListActivity.getInstance()?.reload()
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
            viewModel.getAuthToken(transactionId, ApiConstant.apiName_transaction)
        }
    }

    private fun inventoryObserver() {
        viewModel.inventoryLiveData.observe(this) { inventoryList ->
            inventoryList?.let { inventoryModelList ->
                val inventoryModel: InventoryModel? = inventoryModelList.find {
                    it.itemCode == (viewModel.selectedPicking?.itemCode ?: "")
                }
                inventoryModel?.let {
                    viewModel.selectedPicking?.inventoryQuantity = it.inventoryQuantity
                    viewModel.selectedPicking?.transferQuantity = it.inventoryQuantity
                    viewModel.selectedPicking?.description = it.description ?: ""
                }

                WMSSharedPref.getAppPrefs(
                    WMSApplication.getInstance().context
                ).savePickingInfo(viewModel.selectedPicking)
                var intent: Intent? = null
                when (viewModel.intentFrom) {
                    "PICKING_LIST" -> {
                        intent = Intent(
                            this@PickingOrderNoListActivity,
                            PickingDetailsActivity::class.java
                        )
                    }

                    "RETURN_PICKING_LIST" -> {
                        intent = Intent(
                            this@PickingOrderNoListActivity,
                            ReturnPickingDetailsActivity::class.java
                        )
                    }
                }
                intent?.let {
                    it.putExtra(
                        "DEFAULT_BIN_NO",
                        viewModel.selectedPicking?.proposedStorageBin ?: ""
                    )
                    it.putExtra(
                        "DEFAULT_PALLET_NO",
                        viewModel.selectedPicking?.proposedPackBarCode ?: ""
                    )
                    startActivity(it)
                }
            }
        }
        viewModel.loaderLiveData.observe(this) {
            binding.progress.visibility = if (it) View.VISIBLE else View.GONE
        }
    }

    override fun onPickingSelected(picking: PickingListResponse) {
        viewModel.selectedPicking = picking
        verifyToken(ApiConstant.PICKING_INVENTORY_ID)
    }

    override fun onBackPressed() {
        navigateToListPage()
        super.onBackPressed()
    }
}