package com.tvhht.myapplication.quality.utils

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.provider.ContactsContract
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.Window
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.tvhht.myapplication.cases.ScannerErrorCustomDialog
import com.tvhht.myapplication.databinding.DialogQualityBarcodeBinding
import com.tvhht.myapplication.quality.QualityDetailsActivity.Companion.getInstance
import com.tvhht.myapplication.quality.adapter.QualityBarcodeAdapter
import com.tvhht.myapplication.quality.model.QualityBarcode
import com.tvhht.myapplication.utils.WMSApplication
import com.tvhht.myapplication.utils.WMSSharedPref

class BarcodeIdNewCustomDialog : AppCompatActivity() {
    private lateinit var binding: DialogQualityBarcodeBinding
    private var barcodeList: ArrayList<String> = ArrayList()
    private val barcodeIdList: ArrayList<QualityBarcode> = ArrayList()
    private lateinit var barcodeAdapter: QualityBarcodeAdapter
    private var selectedBarcode = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        binding = DialogQualityBarcodeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        if (intent != null) {
            if (intent.hasExtra("BARCODE_LIST")) {
                barcodeList = intent.getStringArrayListExtra("BARCODE_LIST") ?: arrayListOf()
            }
            if (intent.hasExtra("BARCODE_ID")) {
                selectedBarcode = intent.getStringExtra("BARCODE_ID") ?: ""
                if (selectedBarcode.isNotEmpty()) {
                    binding.imageIcon.visibility = View.VISIBLE
                }
            }
        }
        for (barcode in barcodeList) {
            barcodeIdList.add(QualityBarcode(barcode, barcode == selectedBarcode))
        }
        setListener()
        initAdapter()
    }

    private fun initAdapter() {
        val layoutManager = LinearLayoutManager(this)
        layoutManager.orientation = LinearLayoutManager.VERTICAL
        binding.qualityBarcodeList.layoutManager = layoutManager
        binding.qualityBarcodeList.addItemDecoration(
            DividerItemDecoration(
                this,
                DividerItemDecoration.VERTICAL
            )
        )
        if (::barcodeAdapter.isInitialized.not()) {
            barcodeAdapter = QualityBarcodeAdapter(barcodeIdList, ::verifiedBarcode)
            binding.qualityBarcodeList.adapter = barcodeAdapter
        } else {
            barcodeAdapter.updateAdapter(barcodeIdList)
        }
    }

    private fun setListener() {
        binding.edtTextQualityBarcode.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (::barcodeAdapter.isInitialized) {
                    barcodeAdapter.filter.filter(s.toString())
                }
            }

            override fun afterTextChanged(s: Editable?) {
            }

        })
        binding.btnConfirm.setOnClickListener {
            if (selectedBarcode.isNotEmpty()) {
                getInstance()?.saveSelectedRecord(selectedBarcode)
                val myIntent = Intent(applicationContext, QualityCustomDialogActivity::class.java)
                startActivity(myIntent)
                finish()
            }
        }
        binding.btnCancel.setOnClickListener {
            WMSSharedPref.getAppPrefs(
                WMSApplication.getInstance().context
            ).saveBooleanValue(
                "CASE_CODE_UPDATED",
                false
            )
            finish()
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
        i.setAction(ContactsContract.Intents.Insert.ACTION)
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
        if (!TextUtils.isEmpty(decodedData) && isBarcodeAvailable(decodedData.toString())) {
            selectedBarcode = decodedData.toString()
            binding.imageIcon.visibility = View.VISIBLE
        } else {
            val errorDialog =
                ScannerErrorCustomDialog(this, "Invalid Barcode", decodedData.toString(), 1)
            if (!errorDialog.isShowing) {
                errorDialog.show()
            }
        }
        binding.edtTextQualityBarcode.text = null
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

    private fun verifiedBarcode(barcodeId: String) {
        selectedBarcode = barcodeId
        binding.imageIcon.visibility = if (barcodeId.isEmpty()) View.GONE else View.VISIBLE
    }

    private fun isBarcodeAvailable(code: String): Boolean {
        for (item in barcodeIdList) {
            item.isChecked = item.barcodeId == code
        }
        if (::barcodeAdapter.isInitialized) {
            val sortedList = barcodeIdList.sortedBy { it.isChecked }
            barcodeAdapter.updateAdapter(sortedList)
        }
        val qualityBarcode: QualityBarcode? =
            barcodeIdList.find { it.isChecked }

        qualityBarcode?.let {
            return true
        }
        return false
    }
}
