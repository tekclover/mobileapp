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
import com.tvhht.myapplication.picking.model.PickingQuantityConfirm
import com.tvhht.myapplication.utils.ToastUtils

import com.tvhht.myapplication.utils.WMSApplication
import com.tvhht.myapplication.utils.WMSSharedPref

class PickingQuantityActivity : AppCompatActivity() {
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
        no = findViewById<View>(R.id.buttonNo) as Button
        val qtyInfo = WMSSharedPref.getAppPrefs(
            WMSApplication.getInstance().context
        ).pickingQTYInfo
        yes!!.setOnClickListener { view: View? ->

            val s = qty_two!!.text.toString()

            if (s.isEmpty()) {
                ToastUtils.showToast(
                    applicationContext,
                    "Enter valid Quantity")
            } else {

                qtyInfo?.pickedQty = s.toInt()
                qtyInfo?.isSelected = true
                WMSSharedPref.getAppPrefs(
                    WMSApplication.getInstance().context
                ).savePickingQTYInfo(qtyInfo)
                    var list = ArrayList<PickingQuantityConfirm?>()
                    list.add(qtyInfo)
                    WMSSharedPref.getAppPrefs(
                        WMSApplication.getInstance().context
                    ).saveListPickingQTYInfo(list)
                finish()

            }


        }
        no!!.setOnClickListener { view: View? ->

            WMSSharedPref.getAppPrefs(WMSApplication.getInstance().context)
                .clearSharedPrefs("PICKING_QUANTITY_INFO_LIST")

            finish()
        }

        val stringValue = WMSSharedPref.getAppPrefs(applicationContext).getStringValue("ALLOC_QTY");
        val stringValue1 = WMSSharedPref.getAppPrefs(WMSApplication.getInstance().context)
            .getStringValue("REMAINING_QTY")
        textTitle!!.text = "Remaining Qty :$stringValue1"
        textMessage!!.text = title
        qty_one!!.setText(stringValue)
        qty_two!!.setText(stringValue)

        qty_two!!.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun afterTextChanged(editable: Editable) {

                try {
                    if (editable.toString().toInt() > stringValue1.toInt()!!) {
                        ToastUtils.showToast(
                            applicationContext,
                            "Enter Quantity is Greater than Total Quantity")
                        qty_two!!.setText(qtyInfo!!.inventoryQty.toString())
                    }
                } catch (nfe: Exception) { //or whatever exception you get
                    //do some handling if you need to
                }

            }
        })
    }
}