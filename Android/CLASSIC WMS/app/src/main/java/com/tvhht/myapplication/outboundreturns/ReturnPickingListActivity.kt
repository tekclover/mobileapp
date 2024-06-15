package com.tvhht.myapplication.outboundreturns


import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.tvhht.myapplication.R
import com.tvhht.myapplication.home.HomePageActivity
import com.tvhht.myapplication.inboundreversal.InboundReversalDetailsActivity
import com.tvhht.myapplication.login.LoginLiveData
import com.tvhht.myapplication.login.model.LoginModel
import com.tvhht.myapplication.outboundreturns.adapter.PickingListAdapter
import com.tvhht.myapplication.picking.model.PickUpHeader
import com.tvhht.myapplication.picking.model.PickingCombineResponse
import com.tvhht.myapplication.picking.viewmodel.PickingLiveDataModel

import com.tvhht.myapplication.utils.*
import kotlinx.android.synthetic.main.activity_picking_details.*


import kotlinx.android.synthetic.main.tool_bar.*
import kotlinx.android.synthetic.main.tool_bar.view.*
import java.util.*
import kotlin.collections.ArrayList

class ReturnPickingListActivity : AppCompatActivity() {

    var isScanned: Boolean = false
    var pickingList_data: List<PickingCombineResponse> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_picking_details)
        Recycler_view.layoutManager = LinearLayoutManager(
                this
        )
        header_tt.text = getString(R.string.picking_returns)
        instances2 = this
        toolbar.sign_out.setOnClickListener {
            val cdd = LogoutCustomDialog(
                    this@ReturnPickingListActivity
            )
            if (cdd.isShowing) {
                cdd.dismiss()
            }
            cdd.show()
        }

        WMSSharedPref.getAppPrefs(
                WMSApplication.getInstance().context
        ).saveBooleanValue(
                PrefConstant.CASE_CODE_SCANNED,
                false
        )



        Recycler_view.addItemDecoration(
                DividerItemDecoration(
                        Recycler_view.context,
                        DividerItemDecoration.VERTICAL
                )
        )

        var loginID =
                WMSSharedPref.getAppPrefs(WMSApplication.getInstance().context)
                        .getStringValue(PrefConstant.LOGIN_ID)
        putaway_txt_user.text = loginID

       if (!NetworkUtils().haveNetworkConnection(applicationContext)) {
            Handler().postDelayed({
                progress.visibility = View.GONE
                val parentLayout = findViewById<View>(android.R.id.content)
                val snackbar = Snackbar.make(
                    parentLayout, getString(R.string.internet_check_msg),
                    Snackbar.LENGTH_INDEFINITE
                )

                snackbar.setAction("Dismiss") {
                    snackbar.dismiss()
                    HomePageActivity.getInstance()?.reload()
                }
                snackbar.setActionTextColor(Color.BLUE)
                val snackbarView = snackbar.view
                snackbarView.setBackgroundColor(Color.LTGRAY)
                val textView =
                    snackbarView.findViewById(com.google.android.material.R.id.snackbar_text) as TextView
                textView.setTextColor(Color.RED)
                textView.textSize = 16f
                snackbar.show()
            }, 1000)
        } else {
            verifyToken()
        }
    }


    private fun verifyToken() {
        var request = LoginModel(
                ApiConstant.apiName_transaction,
                ApiConstant.clientId,
                ApiConstant.clientSecretKey,
                ApiConstant.grantType,
                ApiConstant.apiName_name,
                ApiConstant.apiName_pass_key
        )
        val model = ViewModelProviders.of(this)[LoginLiveData::class.java]
        model.getLoginStatus(request).observe(this) { asnList ->
            // update UI
            if (asnList.equals("ERROR")) {
                ToastUtils.showToast(applicationContext, "User not Available")
            } else {
                if (asnList.equals("FAILED")) {
                    ToastUtils.showToast(applicationContext, "No Data Available")
                } else {

                    getPickingList()

                }
            }

        }


    }


    private fun getPickingList() {
        val model = ViewModelProviders.of(this)[PickingLiveDataModel::class.java]

        val ware_id = WMSSharedPref.getAppPrefs(
                WMSApplication.getInstance().context
        ).getStringValue(PrefConstant.WARE_HOUSE_ID)

        var loginID =
                WMSSharedPref.getAppPrefs(WMSApplication.getInstance().context)
                        .getStringValue(PrefConstant.LOGIN_ID)

        val assUser: MutableList<String> = ArrayList()
        val wareID: MutableList<String> = ArrayList()
        val statusID: MutableList<Int> = ArrayList()

        assUser.add(loginID)
        wareID.add(ware_id)
        statusID.add(48)

        val requestData = PickUpHeader(wareID, assUser, statusID)

        model.getPickingList(requestData).observe(this) { vDataList ->
            // update UI
            progress.visibility = View.GONE
            var pickingList_data2: List<PickingCombineResponse> = ArrayList()
            if (vDataList != null && vDataList.isNotEmpty()) {
                pickingList_data2 = (vDataList as List<PickingCombineResponse>?)!!

                var pickingList_data3 =
                        pickingList_data2.filter { (it.outboundOrderTypeId == 2) }

                pickingList_data =
                        pickingList_data3.sortedBy { it.proposedStorageBin }

            } else {
                ToastUtils.showToast(applicationContext, "No Records Available")
            }


            var sizeList = pickingList_data.size.toString()

            putaway_txt_count_val.text = sizeList

            Recycler_view.adapter = PickingListAdapter(this, pickingList_data)


        }
    }

    override fun onResume() {

        super.onResume()

    }


    companion object {
        private var instances2: ReturnPickingListActivity? = null
        fun getInstance(): ReturnPickingListActivity? {
            return instances2
        }
    }

    fun reload() {
        val intent = intent
        overridePendingTransition(0, 0)
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
        finish()
        overridePendingTransition(0, 0)
        startActivity(intent)
    }

    override fun onBackPressed() {
        HomePageActivity.getInstance()?.reload()
        super.onBackPressed()
    }


}


