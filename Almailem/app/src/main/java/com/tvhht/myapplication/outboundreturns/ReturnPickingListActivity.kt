package com.tvhht.myapplication.outboundreturns


import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.tvhht.myapplication.R
import com.tvhht.myapplication.home.HomePageActivity
import com.tvhht.myapplication.login.LoginLiveData
import com.tvhht.myapplication.login.model.LoginModel
import com.tvhht.myapplication.outboundreturns.adapter.PickingListAdapter
import com.tvhht.myapplication.picking.PickingOrderNoListActivity
import com.tvhht.myapplication.picking.model.PickUpHeader
import com.tvhht.myapplication.picking.model.PickingListResponse
import com.tvhht.myapplication.picking.viewmodel.PickingLiveDataModel
import com.tvhht.myapplication.utils.ApiConstant
import com.tvhht.myapplication.utils.LogoutFragment
import com.tvhht.myapplication.utils.NetworkUtils
import com.tvhht.myapplication.utils.PrefConstant
import com.tvhht.myapplication.utils.ToastUtils
import com.tvhht.myapplication.utils.WMSApplication
import com.tvhht.myapplication.utils.WMSSharedPref
import kotlinx.android.synthetic.main.activity_picking_details.Recycler_view
import kotlinx.android.synthetic.main.activity_picking_details.header_tt
import kotlinx.android.synthetic.main.activity_picking_details.progress
import kotlinx.android.synthetic.main.activity_picking_details.putaway_txt_count_val
import kotlinx.android.synthetic.main.activity_picking_details.putaway_txt_user
import kotlinx.android.synthetic.main.tool_bar.toolbar
import kotlinx.android.synthetic.main.tool_bar.view.sign_out

class ReturnPickingListActivity : AppCompatActivity() {

    var isScanned: Boolean = false
    var pickingList_data: List<PickingListResponse> = ArrayList()
    var originalList: List<PickingListResponse> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_picking_details)
        Recycler_view.layoutManager = LinearLayoutManager(
                this
        )
        header_tt.text = getString(R.string.picking_returns)
        instances2 = this
        toolbar.sign_out.setOnClickListener {
            if (supportFragmentManager.findFragmentByTag("logout_fragment") == null) {
                val logoutFragment = LogoutFragment()
                supportFragmentManager.beginTransaction().add(logoutFragment, "logout_fragment")
                    .commitAllowingStateLoss()
            }
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

        val loginID =
                WMSSharedPref.getAppPrefs(WMSApplication.getInstance().context)
                        .getStringValue(PrefConstant.LOGIN_ID)

        val userInfo = WMSSharedPref.getAppPrefs(
            WMSApplication.getInstance().context
        ).userInfoSingle

        val levelIdList: MutableList<Int> = ArrayList()
        val warehouseIdList: MutableList<String> = ArrayList()
        val statusID: MutableList<Int> = ArrayList()
        val outboundOrderTypeID: MutableList<Int> = ArrayList()
        val companyCodeIdList: MutableList<String> = ArrayList()
        val languageIdList: MutableList<String> = ArrayList()
        val plantIdList: MutableList<String> = ArrayList()
        val assignedPickerIdList: MutableList<String> = ArrayList()

        userInfo?.levelId?.let {
            levelIdList.add(it)
        }
        userInfo?.companyCodeId?.let {
            companyCodeIdList.add(it)
        }
        userInfo?.languageId?.let {
            languageIdList.add(it)
        }
        userInfo?.plantId?.let {
            plantIdList.add(it)
        }
        userInfo?.warehouseId?.let {
            warehouseIdList.add(it)
        }
        statusID.add(48)
        outboundOrderTypeID.add(2)
        assignedPickerIdList.add(loginID)

        val requestData = PickUpHeader(
            warehouseIdList,
            if (userInfo?.warehouseId == ApiConstant.WAREHOUSE_ID_200) null else companyCodeIdList,
            if (userInfo?.warehouseId == ApiConstant.WAREHOUSE_ID_200) null else plantIdList,
            languageIdList,
            null,
            statusID,
            assignedPickerIdList,
            outboundOrderTypeID
        )

        model.getPickingList(requestData).observe(this) { vDataList ->
            // update UI
            progress.visibility = View.GONE
            var pickingList_data2: List<PickingListResponse> = ArrayList()
            if (vDataList != null && vDataList.isNotEmpty()) {
                pickingList_data2 = (vDataList as List<PickingListResponse>?)!!

                originalList = pickingList_data2.filter { (it.outboundOrderTypeId == 2) }
                val distinctByOrderNo = originalList.distinctBy { Pair(it.refDocNumber, it.referenceDocumentType) }
                pickingList_data = distinctByOrderNo.sortedBy { it.refDocNumber }
            } else {
                ToastUtils.showToast(applicationContext, "No Records Available")
            }

            val sizeList = pickingList_data.size.toString()
            putaway_txt_count_val.text = sizeList

            Recycler_view.adapter = PickingListAdapter(this, pickingList_data) { orderNumber,orderType ->
                val groupByOrderNo = originalList.groupBy { Pair(it.refDocNumber, it.referenceDocumentType) }
                val orderNoList = groupByOrderNo[Pair(orderNumber, orderType)]
                val myIntent = Intent(this, PickingOrderNoListActivity::class.java)
                myIntent.putParcelableArrayListExtra(
                    "ORDER_NO_LIST",
                    orderNoList?.let { it1 -> ArrayList(it1) })
                myIntent.putExtra("INTENT_FROM_ORDER_NO", "RETURN_PICKING_LIST")
                startActivity(myIntent)
            }
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


