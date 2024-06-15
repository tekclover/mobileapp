package com.tvhht.myapplication.stock


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
import com.tvhht.myapplication.home.HomePageActivity
import com.tvhht.myapplication.login.LoginLiveData
import com.tvhht.myapplication.login.model.LoginModel
import com.tvhht.myapplication.stock.adapter.PerpetualListAdapter
import com.tvhht.myapplication.stock.model.PerpetualHeader
import com.tvhht.myapplication.stock.model.PerpetualResponse
import com.tvhht.myapplication.stock.viewmodel.StockLiveData
import com.tvhht.myapplication.utils.*
import kotlinx.android.synthetic.main.activity_perpetual_list.*
import kotlinx.android.synthetic.main.activity_perpetual_list.Recycler_view
import kotlinx.android.synthetic.main.activity_perpetual_list.progress
import kotlinx.android.synthetic.main.activity_quality_details_confirm.*

import kotlinx.android.synthetic.main.tool_bar.*
import kotlinx.android.synthetic.main.tool_bar.view.*

class PerpetualListActivity : AppCompatActivity() {

    var stockData: List<PerpetualResponse> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_perpetual_list)
        Recycler_view.layoutManager = LinearLayoutManager(
            this
        )
        header_tt.text = getString(R.string.perpetual_txt)
        toolbar.sign_out.setOnClickListener {
            val cdd = LogoutCustomDialog(
                this@PerpetualListActivity
            )
            if (cdd.isShowing) {
                cdd.dismiss()
            }
            cdd.show()
        }
        instances2=this

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
        val model = ViewModelProvider(this)[StockLiveData::class.java]

        val wareId = WMSSharedPref.getAppPrefs(
            WMSApplication.getInstance().context
        ).getStringValue(PrefConstant.WARE_HOUSE_ID)

        var loginID =
            WMSSharedPref.getAppPrefs(WMSApplication.getInstance().context)
                .getStringValue(PrefConstant.LOGIN_ID)

        val assUser: MutableList<String> = ArrayList()
        val wareID: MutableList<String> = ArrayList()
        val statusID: MutableList<Int> = ArrayList()
        val statusID1: MutableList<Int> = ArrayList()

        assUser.add(loginID)
        wareID.add(wareId)
        statusID.add(72)
//        statusID1.add(70)
//        statusID1.add(73)

        val requestData = PerpetualHeader(wareId, loginID, statusID)

        model.getPerpetualList(requestData).observe(this) { vDataList ->
            // update UI
            progress.visibility = View.GONE

            if (vDataList != null) {
                val distinctList = vDataList.distinctBy { it.cycleCountNo }
                stockData = (distinctList as List<PerpetualResponse>?)!!
            }


            var sizeList = stockData.size.toString()

            putaway_txt_count_val.text = sizeList

            Recycler_view.adapter = PerpetualListAdapter(this, stockData)


        }
    }

    override fun onResume() {

        super.onResume()

    }

    companion object {
        private var instances2: PerpetualListActivity? = null
        fun getInstance(): PerpetualListActivity? {
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


