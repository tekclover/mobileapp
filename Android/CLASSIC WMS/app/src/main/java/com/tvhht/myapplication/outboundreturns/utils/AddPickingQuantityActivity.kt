package com.tvhht.myapplication.outboundreturns.utils

import android.app.Activity
import android.app.Dialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.tvhht.myapplication.R
import com.tvhht.myapplication.outboundreturns.ReturnPickingBinDetailsActivity.Companion.getInstance
import com.tvhht.myapplication.utils.ToastUtils
import com.tvhht.myapplication.utils.WMSApplication
import com.tvhht.myapplication.utils.WMSSharedPref

class AddPickingQuantityActivity : AppCompatActivity() {
    var c: Activity? = null
    var title = ""
    var value = ""
    var d: Dialog? = null
    var yes: Button? = null
    var no: Button? = null
    var textMessage: TextView? = null
    var textTitle: TextView? = null
    var textQty: TextView? = null
    var imageIcon: ImageView? = null
    private var qty_one: EditText? = null
    private var qty_two: EditText? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_view_two_inventory)
        setFinishOnTouchOutside(false)
        yes = findViewById<View>(R.id.buttonYes) as Button
        textMessage = findViewById<View>(R.id.textMessage) as TextView
        textTitle = findViewById<View>(R.id.textTitle) as TextView
        imageIcon = findViewById<View>(R.id.imageIcon) as ImageView
        qty_one = findViewById<View>(R.id.qty_one) as EditText
        qty_two = findViewById<View>(R.id.qty_two) as EditText
        textQty = findViewById<View>(R.id.invqty) as TextView
        no = findViewById<View>(R.id.buttonNo) as Button
        val qtyInfo = WMSSharedPref.getAppPrefs(
            WMSApplication.getInstance().context
        ).pickingQTYInfo

        textQty!!.text = "Inventory Qty"
        val stringValue = WMSSharedPref.getAppPrefs(WMSApplication.getInstance().context)
            .getStringValue("REMAINING_QTY")
        val strInt =stringValue.toInt()
        yes!!.setOnClickListener { view: View? ->

            val s = qty_two!!.text.toString()

            if (s.toInt() == 0) {
                ToastUtils.showToast(
                    applicationContext,
                    "Enter Quantity Should be Greater than 0")
            } else {

                if (s.toInt() > strInt) {

                    ToastUtils.showToast(
                        applicationContext,
                        "Enter Quantity Should be less than or equal $stringValue")
                } else {

                    var textOne = qtyInfo!!.inventoryQty

                    if (textOne!! >= s.toInt()) {
                    qtyInfo?.pickedQty = s.toInt()
                    qtyInfo?.isSelected = true
                    WMSSharedPref.getAppPrefs(
                        WMSApplication.getInstance().context
                    ).savePickingQTYInfo(qtyInfo)
                    finish()
                    getInstance()!!.loadFromCache()
                    } else {
                        ToastUtils.showToast(
                            applicationContext,
                            "Enter Quantity Should be less than or equal $textOne"
                        )
                    }
                }
            }


        }
        no!!.setOnClickListener { view: View? ->

            WMSSharedPref.getAppPrefs(WMSApplication.getInstance().context)
                .clearSharedPrefs("PICKING_QUANTITY_INFO_LIST")

            finish()
        }

        val stringValue1 = WMSSharedPref.getAppPrefs(applicationContext).getStringValue("ALLOC_QTY");


        var textOne = qtyInfo!!.inventoryQty
        textTitle!!.text = "Remaining Qty :$stringValue"
        textMessage!!.text = title
        qty_one!!.setText(""+textOne)
        qty_two!!.setText(stringValue1)

        qty_two!!.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun afterTextChanged(editable: Editable) {

                try {
                    if (editable.toString().toInt() > strInt) {
                        ToastUtils.showToast(
                            applicationContext,
                            "Enter Quantity Should be less than or equal $stringValue")
                        qty_two!!.setText(strInt)
                    }
                } catch (nfe: Exception) { //or whatever exception you get
                    //do some handling if you need to
                }

            }
        })
    }
}