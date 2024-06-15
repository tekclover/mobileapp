package com.tvhht.myapplication.transfers.utils

import android.app.Activity
import android.app.Dialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.tvhht.myapplication.R
import com.tvhht.myapplication.utils.ToastUtils
import com.tvhht.myapplication.utils.WMSApplication
import com.tvhht.myapplication.utils.WMSSharedPref

class InvQuantityCustomDialogActivity : AppCompatActivity() {
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
        val transferQtyLabel = findViewById<View>(R.id.text_picked_qty) as TextView
        transferQtyLabel.text = this.resources.getString(R.string.transfer_qty)
        val qtyInfo = WMSSharedPref.getAppPrefs(
            WMSApplication.getInstance().context
        ).inventoryInfo
        yes!!.setOnClickListener { view: View? ->
            val s = qty_two!!.text.toString().trim()
            qtyInfo?.transferQuantity = s.toInt()
            qtyInfo?.isSelected = true
            WMSSharedPref.getAppPrefs(
                WMSApplication.getInstance().context
            ).saveInventoryInfo(qtyInfo)
            finish()
        }
        no!!.setOnClickListener { view: View? -> finish() }
        imageIcon!!.setOnClickListener { view: View? ->


        }
        textMessage!!.text = title
        qty_one!!.setText(qtyInfo?.inventoryQuantity.toString())
        qty_two!!.setText(qtyInfo?.inventoryQuantity.toString())

        qty_two!!.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun afterTextChanged(editable: Editable) {
                try {
                    if (editable.toString().toInt() > qtyInfo?.inventoryQuantity!!) {
                        ToastUtils.showToast(
                            applicationContext,
                            "Enter Quantity is Greater than Total Quantity")
                        qty_two!!.setText(qtyInfo.inventoryQuantity.toString())
                    }


                } catch (nfe: Exception) { //or whatever exception you get
                    //do some handling if you need to
                }
            }
        })
    }
}