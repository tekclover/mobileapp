package com.tvhht.myapplication.picking.utils

import android.app.Activity
import android.app.Dialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.provider.ContactsContract
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.tvhht.myapplication.R
import com.tvhht.myapplication.goodsreceipt.model.FindImPartnerResponse
import com.tvhht.myapplication.picking.AddNewBarcodeActivity
import com.tvhht.myapplication.picking.PickingDetailsActivity.Companion.getInstance
import com.tvhht.myapplication.picking.model.PickingQuantityConfirm
import com.tvhht.myapplication.picking.viewmodel.FindImPartnerViewModel
import com.tvhht.myapplication.utils.ApiConstant
import com.tvhht.myapplication.utils.ToastUtils.Companion.showToast
import com.tvhht.myapplication.utils.WMSApplication
import com.tvhht.myapplication.utils.WMSSharedPref
import kotlinx.android.synthetic.main.dialog_view_one_activity.btnAdd

class PalletIDCustomDialog : AppCompatActivity(), View.OnClickListener {
    var c: Activity? = null
    var title = "Verify Barcode"
    var value: String? = ""
    var value2: String? = ""
    var d: Dialog? = null
    var yes: Button? = null
    var no: Button? = null
    var add: Button? = null
    var textMessage: TextView? = null
    var textValue: TextView? = null
    var imageIcon: ImageView? = null
    var barcode_value: EditText? = null
    var progress:ProgressBar?=null
    private var isValidBarcode = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.dialog_view_one_activity)
        yes = findViewById<View>(R.id.buttonYes) as Button
        textMessage = findViewById<View>(R.id.textMessage) as TextView
        textValue = findViewById<View>(R.id.textTitle) as TextView
        imageIcon = findViewById<View>(R.id.imageIcon) as ImageView
        no = findViewById<View>(R.id.buttonNo) as Button
        add = findViewById<View>(R.id.btnAdd) as Button
        barcode_value = findViewById<View>(R.id.barcode_value) as EditText
        progress = findViewById<View>(R.id.progress) as ProgressBar
        yes?.setOnClickListener(this)
        no?.setOnClickListener(this)
        add?.setOnClickListener(this)
        textMessage!!.text = title
        value = intent.getStringExtra("EXTRA_PALLET_CODE")
        value2 = value
        textValue!!.text = value
        imageIcon!!.setBackgroundResource(R.drawable.icon_alert_failure)
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
        if (decodedData.isNullOrEmpty().not()) {
            value = decodedData.toString()
            textValue?.text = value
/*
            if (value.equals(value2, ignoreCase = true)) {
                textMessage!!.text = "Barcode Scanned"
                imageIcon!!.setBackgroundResource(R.drawable.icon_tick_success)
            } else {
                textMessage!!.text = "Invalid Barcode"
                imageIcon!!.setBackgroundResource(R.drawable.icon_alert_failure)
            }
*/
            barcodeValidation()
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

    override fun onClick(v: View) {
        when (v.id) {
            R.id.buttonYes -> {
                if (isValidBarcode) {
                    val qtyInfo = WMSSharedPref.getAppPrefs(
                        WMSApplication.getInstance().context
                    ).pickingQTYInfo
                    qtyInfo?.barcodeId = value
                    WMSSharedPref.getAppPrefs(
                        WMSApplication.getInstance().context
                    ).savePickingQTYInfo(qtyInfo)
                    val list = ArrayList<PickingQuantityConfirm?>()
                    list.add(qtyInfo)
                    WMSSharedPref.getAppPrefs(
                        WMSApplication.getInstance().context
                    ).saveListPickingQTYInfo(list)
                    finish()
                    getInstance()?.verifyItemCode()
                } else {
                    showToast(applicationContext, "Invalid Barcode")
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
            R.id.btnAdd -> {
                startActivity(Intent(this, AddNewBarcodeActivity::class.java))
                finish()
            }

            else -> {}
        }
        finish()
    }

    private fun barcodeValidation() {
        val viewModel = ViewModelProvider(this)[FindImPartnerViewModel::class.java]

        intent?.extras?.let {
            viewModel.itemCode = it.getString("EXTRA_ITEM_CODE") ?: ""
            viewModel.businessPartnerCode = it.getString("EXTRA_MANUFACTURER_NAME") ?: ""
        }
        viewModel.getAuthToken(ApiConstant.GR_FIND_IM_PARTNER_ID, ApiConstant.apiName_masters)

        viewModel.findImPartner.observe(this) { imPartnerList ->
            if (imPartnerList.isNullOrEmpty().not()) {
                val imPartnerResponse: FindImPartnerResponse? =
                    imPartnerList.find { it.partnerItemBarcode == value }
                if (imPartnerResponse != null) {
                    isValidBarcode = true
                    textMessage?.text = "Barcode Scanned"
                    imageIcon?.setBackgroundResource(R.drawable.icon_tick_success)
                    btnAdd.visibility = View.GONE
                } else {
                    isValidBarcode = false
                    textMessage?.text = "Invalid Barcode"
                    imageIcon?.setBackgroundResource(R.drawable.icon_alert_failure)
                    btnAdd.visibility = View.VISIBLE
                }
            } else {
                isValidBarcode = false
                textMessage?.text = "Invalid Barcode"
                imageIcon?.setBackgroundResource(R.drawable.icon_alert_failure)
                btnAdd.visibility = View.VISIBLE
            }
        }
        viewModel.loaderLiveData.observe(this) {
            progress?.visibility = if (it) View.VISIBLE else View.GONE
        }
    }
}
