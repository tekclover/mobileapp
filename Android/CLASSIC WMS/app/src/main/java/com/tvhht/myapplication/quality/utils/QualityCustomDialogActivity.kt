package com.tvhht.myapplication.quality.utils

import android.app.Activity
import android.app.Dialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.tvhht.myapplication.R
import com.tvhht.myapplication.quality.QualityDetailsActivity
import com.tvhht.myapplication.utils.ToastUtils
import com.tvhht.myapplication.utils.WMSApplication
import com.tvhht.myapplication.utils.WMSSharedPref

class QualityCustomDialogActivity : AppCompatActivity() {
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
        setContentView(R.layout.dialog_view_two_cc)
        setFinishOnTouchOutside(false)
        yes = findViewById<View>(R.id.buttonYes) as Button
        textMessage = findViewById<View>(R.id.textMessage) as TextView
        qty_one = findViewById<View>(R.id.qty_one) as EditText
        qty_two = findViewById<View>(R.id.qty_two) as EditText
        no = findViewById<View>(R.id.buttonNo) as Button
        val qualityInfo = WMSSharedPref.getAppPrefs(
            WMSApplication.getInstance().applicationContext
        ).qualityInfo
        yes!!.setOnClickListener {


            val s = qty_two!!.text.toString()

            if (s.toInt() == 0) {
                ToastUtils.showToast(
                    applicationContext,
                    "Enter Quantity Should be Greater than 0")
            } else {
                qualityInfo?.pickQty = s.toInt().toString()
                qualityInfo?.isSelected = true
                WMSSharedPref.getAppPrefs(
                    WMSApplication.getInstance().context
                ).saveQualityInfo(qualityInfo)
                finish()

                QualityDetailsActivity.getInstance()!!.submitQuantity()
            }
        }
        no!!.setOnClickListener { finish() }
        textMessage!!.text = title
        qty_one!!.setText(qualityInfo!!.pickConfirmQty.toString())
        qty_two!!.setText(qualityInfo!!.pickConfirmQty.toString())

        qty_two!!.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun afterTextChanged(editable: Editable) {

                try {
                    if (editable.toString().toInt() > qualityInfo!!.pickConfirmQty!!) {
                        ToastUtils.showToast(
                            applicationContext,
                            "Inspected Quantity cannot be greater than Picked Quantity")
                        qty_two!!.setText(qualityInfo!!.pickConfirmQty.toString())
                    }
                } catch (nfe: Exception) { //or whatever exception you get
                    //do some handling if you need to
                }

            }
        })
    }
}