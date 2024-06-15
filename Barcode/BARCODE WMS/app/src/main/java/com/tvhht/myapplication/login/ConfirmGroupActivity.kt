package com.tvhht.myapplication.login

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.FrameLayout
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import com.tvhht.myapplication.R
import com.tvhht.myapplication.home.NewScannerPage
import com.tvhht.myapplication.utils.PrefConstant
import com.tvhht.myapplication.utils.WMSApplication
import com.tvhht.myapplication.utils.WMSSharedPref

class ConfirmGroupActivity : AppCompatActivity() {

    lateinit var pd: ProgressDialog
    var listGroup: MutableList<String> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_group)

        pd = ProgressDialog(this@ConfirmGroupActivity)
        pd.setMessage("Loading...")

        val userInfo = WMSSharedPref.getAppPrefs(
            WMSApplication.getInstance().context
        ).userInfo

        var dataList1 = intent?.getStringArrayListExtra("GROUP_LIST")
        if (dataList1 != null) {
            listGroup.addAll(dataList1)
        }

        warehouseSelection()


    }


    private fun warehouseSelection() {
        var groupConfirm = findViewById<Spinner>(R.id.selection_value)
        var groupSubmit = findViewById<FrameLayout>(R.id.btn_confirm)

        if (listGroup != null) {
            var adapter: ArrayAdapter<String> =
                ArrayAdapter<String>(
                    applicationContext,
                    R.layout.spinner_item, R.id.txt_cell,
                    listGroup
                );
            groupConfirm.adapter = adapter
        }

        groupSubmit.setOnClickListener {

            val toIndex = groupConfirm.selectedItem;
            WMSSharedPref.getAppPrefs(
                WMSApplication.getInstance().context
            ).saveStringValue(PrefConstant.USER_DETAILS_INDEX, toIndex.toString())

            loginSuccess()
        }


    }

    private fun loginSuccess() {
        val i = Intent(this@ConfirmGroupActivity, NewScannerPage::class.java)
        startActivity(i)
        finish()
    }

}