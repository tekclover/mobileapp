package com.tvhht.myapplication.annual


import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.tvhht.myapplication.R
import com.tvhht.myapplication.annual.adapter.AnnualListAdapter
import com.tvhht.myapplication.annual.model.AnnualHeader
import com.tvhht.myapplication.annual.model.AnnualStockResponse
import com.tvhht.myapplication.annual.viewmodel.AnnualLiveData
import com.tvhht.myapplication.home.HomePageActivity
import com.tvhht.myapplication.login.LoginLiveData
import com.tvhht.myapplication.login.model.LoginModel
import com.tvhht.myapplication.utils.ApiConstant
import com.tvhht.myapplication.utils.LogoutFragment
import com.tvhht.myapplication.utils.NetworkUtils
import com.tvhht.myapplication.utils.PrefConstant
import com.tvhht.myapplication.utils.ToastUtils
import com.tvhht.myapplication.utils.WMSApplication
import com.tvhht.myapplication.utils.WMSSharedPref
import kotlinx.android.synthetic.main.activity_perpetual_list.Recycler_view
import kotlinx.android.synthetic.main.activity_perpetual_list.header_tt
import kotlinx.android.synthetic.main.activity_perpetual_list.progress
import kotlinx.android.synthetic.main.activity_perpetual_list.putaway_txt_count_val
import kotlinx.android.synthetic.main.activity_perpetual_list.putaway_txt_user
import kotlinx.android.synthetic.main.tool_bar.toolbar
import kotlinx.android.synthetic.main.tool_bar.view.sign_out

class AnnualListActivity : AppCompatActivity() {

    var stockData: MutableList<AnnualStockResponse> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_annual_list)
        Recycler_view.layoutManager = LinearLayoutManager(
            this
        )
        header_tt.text = getString(R.string.annual_txt)
        toolbar.sign_out.setOnClickListener {
            if (supportFragmentManager.findFragmentByTag("logout_fragment") == null) {
                val logoutFragment = LogoutFragment()
                supportFragmentManager.beginTransaction().add(logoutFragment, "logout_fragment")
                    .commitAllowingStateLoss()
            }
        }
        instances2 = this

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
        val model = ViewModelProvider(this)[LoginLiveData::class.java]
        model.getLoginStatus(request).observe(this) { asnList ->
            // update UI
            if (asnList.equals("ERROR")) {
                ToastUtils.showToast(applicationContext, "User not Available")
            } else {
                if (asnList.equals("FAILED")) {
                    ToastUtils.showToast(applicationContext, "No Data Available")
                } else {

                    getStockList()

                }
            }

        }


    }


    private fun getStockList() {
        val model = ViewModelProvider(this)[AnnualLiveData::class.java]

        val warehouseIdList: MutableList<String> = ArrayList()
        val companyCodeIdList: MutableList<String> = ArrayList()
        val languageIdList: MutableList<String> = ArrayList()
        val plantIdList: MutableList<String> = ArrayList()
        val headerStatusIdList: MutableList<Int> = ArrayList()
        val lineStatusIdList: MutableList<Int> = ArrayList()
        val cycleCounterIdList: MutableList<String> = ArrayList()

        val wareId: String =
            WMSSharedPref.getAppPrefs(WMSApplication.getInstance().context)
                .getStringValue(PrefConstant.WARE_HOUSE_ID)
        val companyCodeId: String =
            WMSSharedPref.getAppPrefs(WMSApplication.getInstance().context)
                .getStringValue(PrefConstant.COMPANY_CODE_ID)
        val languageId: String =
            WMSSharedPref.getAppPrefs(WMSApplication.getInstance().context)
                .getStringValue(PrefConstant.LANGUAGE_ID)
        val plantId: String =
            WMSSharedPref.getAppPrefs(WMSApplication.getInstance().context)
                .getStringValue(PrefConstant.PLANT_ID)
        var loginId =
            WMSSharedPref.getAppPrefs(WMSApplication.getInstance().context)
                .getStringValue(PrefConstant.LOGIN_ID)

        cycleCounterIdList.add(loginId)
        warehouseIdList.add(wareId)
        companyCodeIdList.add(companyCodeId)
        languageIdList.add(languageId)
        plantIdList.add(plantId)
        headerStatusIdList.add(70)
        headerStatusIdList.add(73)
        lineStatusIdList.add(72)
        lineStatusIdList.add(75)

        val requestData = AnnualHeader(
            cycleCounterIdList,
            headerStatusIdList,
            lineStatusIdList,
            warehouseIdList,
            if (wareId == ApiConstant.WAREHOUSE_ID_200) null else companyCodeIdList,
            if (wareId == ApiConstant.WAREHOUSE_ID_200) null else plantIdList,
            languageIdList
        )
        model.getAnnualList(requestData).observe(this) { vDataList ->
            // update UI
            progress.visibility = View.GONE

            if (vDataList != null && vDataList.isNotEmpty()) {
                var stockData1 = (vDataList as MutableList<AnnualStockResponse>?)!!
                for (iValue in stockData1) {
                    if (iValue.periodicLine.size > 0) {
                        stockData.add(iValue)
                    }
                }
                val distinctList = vDataList.distinctBy { it.cycleCountNo }
                stockData = (distinctList as MutableList<AnnualStockResponse>?)!!
            }


            var sizeList = stockData.size.toString()

            putaway_txt_count_val.text = sizeList
            val sortedList = stockData.sortedByDescending { it.cycleCountNo }
            Recycler_view.adapter = AnnualListAdapter(this, sortedList)


        }
    }

    override fun onResume() {

        super.onResume()

    }

    companion object {
        private var instances2: AnnualListActivity? = null
        fun getInstance(): AnnualListActivity? {
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


