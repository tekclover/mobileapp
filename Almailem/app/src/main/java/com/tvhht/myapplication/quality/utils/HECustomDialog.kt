package com.tvhht.myapplication.quality.utils

import android.app.Activity
import android.app.Dialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import android.view.View
import android.view.Window
import androidx.appcompat.app.AppCompatActivity
import com.tvhht.myapplication.R
import com.tvhht.myapplication.cases.ScannerErrorCustomDialog
import com.tvhht.myapplication.databinding.DialogViewOneActivityBinding
import com.tvhht.myapplication.quality.model.QualityDetailsModel
import com.tvhht.myapplication.utils.ToastUtils.Companion.showToast
import com.tvhht.myapplication.utils.WMSApplication
import com.tvhht.myapplication.utils.WMSSharedPref

class HECustomDialog : AppCompatActivity(), View.OnClickListener {
    var c: Activity? = null
    var value: String? = ""
    var d: Dialog? = null

    private var dataList: ArrayList<QualityDetailsModel>? = ArrayList()
    private lateinit var binding: DialogViewOneActivityBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        binding = DialogViewOneActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.buttonYes.setOnClickListener(this)
        binding.buttonNo.setOnClickListener(this)
        value = intent.getStringExtra("EXTRA_CODE_1")
        intent?.getStringExtra("EXTRA_CODE_4")?.let {
            binding.textMessage.text = it
        }
        binding.textTitle.text = value
        if (intent != null && intent.hasExtra("EXTRA_CODE_5")) {
            dataList =
                intent.getSerializableExtra("EXTRA_CODE_5") as ArrayList<QualityDetailsModel>?
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
        if (decodedData.isNullOrEmpty().not() && isHeNoAvailable(decodedData.toString())) {
            value = decodedData.toString()
            binding.textTitle.text = value
            binding.textMessage.text = "HE Number Scanned"
            binding.imageIcon.setBackgroundResource(R.drawable.icon_tick_success)
        } else {
            val cdd = ScannerErrorCustomDialog(
                this@HECustomDialog,
                "Invalid HE Number",
                decodedData.toString(), 1,
            )
            cdd.show()
        }
    }

    private fun isHeNoAvailable(caseId: String): Boolean {
        val find = dataList?.find { it.actualHeNo == caseId }
        return find != null
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

    override fun onClick(v: View) {
        when (v.id) {
            R.id.buttonYes -> {
                finish()
                if (value?.isNotEmpty() == true) {
                    /* Intent myIntent = new Intent(getApplicationContext(), QualityDetailsActivity.class);
                    myIntent.putExtra("HE_INT_CODE", value);
                    myIntent.putExtra("HE_INT_CODE_NO", valueCode);
                    myIntent.putExtra("QUALITY_INSPECTION_NO", qualityInspectionNo);
                    startActivity(myIntent);*/
                    val filterList =
                        dataList?.filter { it.actualHeNo == value && it.isSelected.not() }
                    val barcodeList: ArrayList<String> = ArrayList()
                    if (filterList.isNullOrEmpty().not()) {
                        filterList?.let { list ->
                            for (data in list) {
                                data.referenceField6?.let { barcodeList.add(it) }
                            }
                            val myIntent =
                                Intent(applicationContext, BarcodeIdNewCustomDialog::class.java)
                            myIntent.putStringArrayListExtra("BARCODE_ID", barcodeList)
                            startActivity(myIntent)
                        }
                    } else {
                        showToast(applicationContext, "No records")
                    }
                } else {
                    showToast(applicationContext, "Please Scan HE Number")
                }
            }

            R.id.buttonNo -> {
                WMSSharedPref.getAppPrefs(
                    WMSApplication.getInstance().context
                ).saveBooleanValue(
                    "CASE_CODE_UPDATED",
                    false
                )
                finish()
            }

            else -> {}
        }
        finish()
    }
}
