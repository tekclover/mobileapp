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
import android.view.Window
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
import com.tvhht.myapplication.R
import com.tvhht.myapplication.cases.ScannerErrorCustomDialog
import com.tvhht.myapplication.databinding.GrScanBarcodeActivityBinding
import com.tvhht.myapplication.home.HomePageActivity
import com.tvhht.myapplication.picking.model.PickingQuantityConfirm
import com.tvhht.myapplication.picking.utils.UpdateNewBarcodeListener
import com.tvhht.myapplication.picking.viewmodel.AddNewBarcodeViewModel
import com.tvhht.myapplication.utils.ApiConstant
import com.tvhht.myapplication.utils.NetworkUtils
import com.tvhht.myapplication.utils.WMSApplication
import com.tvhht.myapplication.utils.WMSSharedPref

class AddNewBarcodeActivity : AppCompatActivity() {
    private lateinit var binding: GrScanBarcodeActivityBinding
    private lateinit var viewModel: AddNewBarcodeViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        binding = GrScanBarcodeActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initViewModel()
        setObservers()
        setListener()
    }

    private fun initViewModel() {
        viewModel = ViewModelProvider(this)[AddNewBarcodeViewModel::class.java]
    }

    private fun setListener() {
        binding.buttonYes.setOnClickListener {
            verifyToken()
        }
        binding.buttonNo.setOnClickListener {
            finish()
        }
    }

    private fun verifyToken() {
        if (!NetworkUtils().haveNetworkConnection(applicationContext)) {
            Handler().postDelayed({
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
            viewModel.getAuthToken(ApiConstant.apiName_transaction)
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
        if (decodedData.isNullOrEmpty().not()) {
            binding.textBarcode.text = decodedData
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

    private fun setObservers() {
        viewModel.loaderLiveData.observe(this) {
            binding.progress.visibility = if (it) View.VISIBLE else View.GONE
        }
        viewModel.updateBarcodeLiveData.observe(this) { barcodeResponse ->
            barcodeResponse?.let {
                val qtyInfo = WMSSharedPref.getAppPrefs(
                    WMSApplication.getInstance().context
                ).pickingQTYInfo
                qtyInfo?.barcodeId = it.partnerItemBarcode ?: ""
                WMSSharedPref.getAppPrefs(
                    WMSApplication.getInstance().context
                ).savePickingQTYInfo(qtyInfo)
                val list = ArrayList<PickingQuantityConfirm?>()
                list.add(qtyInfo)
                WMSSharedPref.getAppPrefs(
                    WMSApplication.getInstance().context
                ).saveListPickingQTYInfo(list)
                val pickingInfo = WMSSharedPref.getAppPrefs(
                    WMSApplication.getInstance().context
                ).pickingInfo!!
                pickingInfo.barcodeId = it.partnerItemBarcode ?: ""
                WMSSharedPref.getAppPrefs(
                    WMSApplication.getInstance().context
                ).savePickingInfo(pickingInfo)
                updateBarcodeListener?.onBarcodeUpdate(it)
                finish()
            }
        }
        viewModel.errorMessage.observe(this) {
            if (it) {
                Toast.makeText(this, this.resources.getString(R.string.barcode_exists_message), Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun invalidBarcodeDialog() {
        val cdd = ScannerErrorCustomDialog(
            this@AddNewBarcodeActivity,
            "Invalid Barcode",
            viewModel.barcode, 1,
        )
        cdd.show()
    }

    companion object {
        private var updateBarcodeListener: UpdateNewBarcodeListener? = null
        fun setUpdateBarcodeListener(barcodeListener: UpdateNewBarcodeListener?) {
            updateBarcodeListener = barcodeListener
        }
    }
}