package com.tvhht.myapplication.annual.utils

import android.app.Activity
import android.app.Dialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.tvhht.myapplication.R
import com.tvhht.myapplication.annual.AnnualConfirmActivity

import com.tvhht.myapplication.utils.ToastUtils
import com.tvhht.myapplication.utils.WMSApplication
import com.tvhht.myapplication.utils.WMSSharedPref

class QualityCustomDialog2Activity : AppCompatActivity() {
    var c: Activity? = null
    var title = ""
    var value = ""
    var d: Dialog? = null
    var yes: Button? = null
    var no: Button? = null
    var textMessage: TextView? = null
    private var qty_one: EditText? = null
    private var qty_two: EditText? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_view_cc_stock)
        setFinishOnTouchOutside(false)
        yes = findViewById<View>(R.id.buttonYes) as Button
        textMessage = findViewById<View>(R.id.textMessage) as TextView
        qty_one = findViewById<View>(R.id.qty_one) as EditText
        qty_two = findViewById<View>(R.id.qty_two) as EditText
        no = findViewById<View>(R.id.buttonNo) as Button
        val qualityInfo = WMSSharedPref.getAppPrefs(
            WMSApplication.getInstance().applicationContext
        ).stocksQualityInfo2
        yes!!.setOnClickListener {


            val s = qty_two!!.text.toString()

            if (s.isEmpty()) {
                ToastUtils.showToast(
                    applicationContext,
                    "Enter Quantity")
            } else {
                qualityInfo?.countedQty = s.toInt()
                qualityInfo?.referenceField5 = "YES"

                WMSSharedPref.getAppPrefs(
                    WMSApplication.getInstance().context
                ).saveStocksQualityInfo2(qualityInfo)
                finish()

                AnnualConfirmActivity.getInstance()!!.submitQuantity2()
            }
        }
        no!!.setOnClickListener { finish() }
        textMessage!!.text = title
       // qty_two!!.setText("0")

        qty_two!!.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun afterTextChanged(editable: Editable) {


            }
        })
    }
}