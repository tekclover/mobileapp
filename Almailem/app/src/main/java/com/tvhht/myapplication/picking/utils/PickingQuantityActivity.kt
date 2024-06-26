package com.tvhht.myapplication.picking.utils

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
import com.tvhht.myapplication.putaway.utils.ReasonDialog
import com.tvhht.myapplication.utils.ToastUtils


import com.tvhht.myapplication.utils.WMSApplication
import com.tvhht.myapplication.utils.WMSSharedPref

class PickingQuantityActivity : AppCompatActivity(),ReasonDialog.ReasonListener {
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
    private var pickingQty: Int = 0
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
        val stringValue = WMSSharedPref.getAppPrefs(applicationContext).getStringValue("ALLOC_QTY");
        val stringValue1 = WMSSharedPref.getAppPrefs(WMSApplication.getInstance().context)
            .getStringValue("REMAINING_QTY")
        yes!!.setOnClickListener { view: View? ->

            val s = qty_two!!.text.toString()

            when {
                s.isEmpty() -> {
                    ToastUtils.showToast(
                        applicationContext,
                        "Enter valid Quantity"
                    )
                }

                ((qty_two?.text.toString().toInt() == 0) || (qty_two?.text.toString().toInt() < stringValue1.toInt())) -> {
                    pickingQty=s.toInt()
                    val reasonFragment = ReasonDialog()
                    val bundle = Bundle()
                    bundle.putString("IS_REASON_FROM", "Inbound")
                    reasonFragment.arguments = bundle
                    supportFragmentManager.beginTransaction().add(reasonFragment, "reason_fragment")
                        .commitAllowingStateLoss()
                }

                else -> {
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
        }
        no!!.setOnClickListener { view: View? ->

            WMSSharedPref.getAppPrefs(WMSApplication.getInstance().context)
                .clearSharedPrefs("PICKING_QUANTITY_INFO_LIST")

            finish()
        }

        textTitle!!.text = "Remaining Qty :$stringValue1"
        textMessage!!.text = title

        qty_one!!.setText(stringValue)
        qty_two!!.setText(stringValue1)

        qty_two!!.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun afterTextChanged(editable: Editable) {

                try {
                    if (editable.toString().toInt() > stringValue1.toInt()!!) {
                        ToastUtils.showToast(
                            applicationContext,
                            "Enter Quantity Should be less than or equal $stringValue1"
                        )
                        qty_two!!.setText(stringValue1)
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
        ).pickingQTYInfo
        qtyInfo?.pickedQty = pickingQty
        qtyInfo?.isSelected = true
        qtyInfo?.remark = reason
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