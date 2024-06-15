package com.tvhht.myapplication.inboundreversal.utils


import android.app.Activity
import android.app.Dialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.tvhht.myapplication.R
import com.tvhht.myapplication.inboundreversal.InboundReversalDetailsActivity
import com.tvhht.myapplication.putaway.PutAwayDetailsActivity
import com.tvhht.myapplication.putaway.utils.ReasonDialog
import com.tvhht.myapplication.utils.ToastUtils
import com.tvhht.myapplication.utils.WMSApplication
import com.tvhht.myapplication.utils.WMSSharedPref
import java.util.Locale

class ReversalQuantityCustomDialogActivity : AppCompatActivity(),ReasonDialog.ReasonListener {
    var c: Activity? = null
    var title = ""
    var value = ""
    var d: Dialog? = null
    var yes: Button? = null
    var no: Button? = null
    var textMessage: TextView? = null
    var textTitle: TextView? = null
    var imageIcon: ImageView? = null
    private var qty_one: EditText? = null
    private var qty_two: EditText? = null
    private var confirmQty: Double = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_view_two)
        setFinishOnTouchOutside(false)
        yes = findViewById<View>(R.id.buttonYes) as Button
        textMessage = findViewById<View>(R.id.textMessage) as TextView
        textTitle = findViewById<View>(R.id.textTitle) as TextView
        imageIcon = findViewById<View>(R.id.imageIcon) as ImageView
        qty_one = findViewById<View>(R.id.qty_one) as EditText
        qty_two = findViewById<View>(R.id.qty_two) as EditText
        no = findViewById<View>(R.id.buttonNo) as Button
        val qtyInfo = WMSSharedPref.getAppPrefs(
            WMSApplication.getInstance().context
        ).qtyInfo
        yes!!.setOnClickListener { view: View? ->
            val s = qty_two!!.text.toString().trim()
            if (s.isNotEmpty() && s.toInt() > 0) {
                when {
                    s.toInt() == 0 -> {
                        ToastUtils.showToast(
                            applicationContext,
                            "Enter Quantity Should be Greater than 0"
                        )
                    }

/*
                    (qty_two?.text.toString().toDouble() != qtyInfo?.putawayTotalQty!!) -> {
                        confirmQty = s.toDouble()
                        val reasonFragment = ReasonDialog()
                        val bundle = Bundle()
                        bundle.putString("IS_REASON_FROM", "Outbound")
                        reasonFragment.arguments = bundle
                        supportFragmentManager.beginTransaction()
                            .add(reasonFragment, "reason_fragment")
                            .commitAllowingStateLoss()
                    }
*/

                    else -> {
                        qtyInfo?.putawayConfirmedQty = s.toDouble()
                        qtyInfo?.isSelected = true
                        WMSSharedPref.getAppPrefs(
                            WMSApplication.getInstance().context
                        ).saveQTYInfo(qtyInfo)
                        finish()
                        InboundReversalDetailsActivity.getInstance()?.loadFromCache();
                    }
                }
            }
        }
        no!!.setOnClickListener { view: View? ->

            WMSSharedPref.getAppPrefs(WMSApplication.getInstance().context)
                .clearSharedPrefs("QUANTITY_INFO_LIST")

            finish()
        }
        imageIcon!!.setOnClickListener { view: View? ->
            val s = qty_two!!.text.toString().trim()
            qtyInfo?.putawayConfirmedQty = s.toDouble()
            qtyInfo?.isSelected = true
            WMSSharedPref.getAppPrefs(
                WMSApplication.getInstance().context
            ).saveQTYInfo(qtyInfo)

            WMSSharedPref.getAppPrefs(
                WMSApplication.getInstance().context
            ).saveBooleanValue(
                "CASE_CODE_UPDATED",
                false
            )

            finish()
            InboundReversalDetailsActivity.getInstance()?.launchChangeBin();
        }
        textTitle!!.text = "Bin Number: " + qtyInfo!!.binLocation
        textMessage!!.text = title
        qty_one!!.setText(String.format(Locale.getDefault(),"%d",(qtyInfo.putawayTotalQty ?:0).toInt()))
        qty_two!!.setText(String.format(Locale.getDefault(),"%d",(qtyInfo.putawayTotalQty ?:0).toInt()))

        qty_two!!.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun afterTextChanged(editable: Editable) {

                try {
                    if (editable.toString().toInt() > qtyInfo.putawayTotalQty!!) {
                        ToastUtils.showToast(
                            applicationContext,
                            "Enter Quantity is Greater than Total Quantity")
                        qty_two!!.setText(String.format(Locale.getDefault(),"%d",(qtyInfo.putawayTotalQty ?:0).toInt()))
                    }
                } catch (nfe: Exception) { //or whatever exception you get
                    //do some handling if you need to
                }

            }
        })
    }

    override fun selectedReason(reason: String) {
        val qtyInfo = WMSSharedPref.getAppPrefs(
            WMSApplication.getInstance().context
        ).qtyInfo
        qtyInfo?.putawayConfirmedQty = confirmQty
        qtyInfo?.isSelected = true
        qtyInfo?.remarks = reason
        WMSSharedPref.getAppPrefs(
            WMSApplication.getInstance().context
        ).saveQTYInfo(qtyInfo)
        finish()
        InboundReversalDetailsActivity.getInstance()?.loadFromCache()
    }
}