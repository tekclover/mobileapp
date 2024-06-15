package com.tvhht.myapplication.reports


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
import com.tvhht.myapplication.login.LoginLiveData
import com.tvhht.myapplication.login.model.LoginModel
import com.tvhht.myapplication.reports.adapter.ReportBinAdapter
import com.tvhht.myapplication.reports.model.ReportRequestBin
import com.tvhht.myapplication.reports.model.ReportResponse
import com.tvhht.myapplication.reports.utils.ScanBinCustomDialog
import com.tvhht.myapplication.reports.viewmodel.ReportLiveDataModel
import com.tvhht.myapplication.utils.ApiConstant
import com.tvhht.myapplication.utils.LogoutFragment
import com.tvhht.myapplication.utils.NetworkUtils
import com.tvhht.myapplication.utils.PrefConstant
import com.tvhht.myapplication.utils.ToastUtils
import com.tvhht.myapplication.utils.WMSApplication
import com.tvhht.myapplication.utils.WMSSharedPref
import kotlinx.android.synthetic.main.activity_report_storage_bin.Recycler_view
import kotlinx.android.synthetic.main.activity_report_storage_bin.palletIDEnter
import kotlinx.android.synthetic.main.activity_report_storage_bin.progress
import kotlinx.android.synthetic.main.activity_report_storage_bin.putaway_txt_user
import kotlinx.android.synthetic.main.activity_report_storage_bin.reloadReports
import kotlinx.android.synthetic.main.activity_report_storage_bin.scanReports
import kotlinx.android.synthetic.main.tool_bar.toolbar
import kotlinx.android.synthetic.main.tool_bar.view.sign_out

class ReportListFromBinNoActivity : AppCompatActivity() {

    var isScanned: Boolean = false
    var reportList_data: List<ReportResponse> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_report_storage_bin)
        Recycler_view.layoutManager = LinearLayoutManager(
            this
        )
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

        progress.visibility = View.GONE


        scanReports.setOnClickListener {

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
                        onBackPressed()
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

                val myIntent =
                    Intent(this@ReportListFromBinNoActivity, ScanBinCustomDialog::class.java)
                startActivity(myIntent)
            }
        }

        reloadReports.setOnClickListener {

            val intent = intent
            overridePendingTransition(0, 0)
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
            finish()
            overridePendingTransition(0, 0)
            startActivity(intent)


        }


    }


    fun verifyToken() {

        var reportsID =
            WMSSharedPref.getAppPrefs(WMSApplication.getInstance().context)
                .getStringValue(PrefConstant.REPORTS_ID_VALUE)

        palletIDEnter.text = reportsID

        progress.visibility = View.VISIBLE
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

                    getReportList()

                }
            }

        }


    }


    private fun getReportList() {
        val model = ViewModelProviders.of(this)[ReportLiveDataModel::class.java]

        val ware_id = WMSSharedPref.getAppPrefs(
            WMSApplication.getInstance().context
        ).getStringValue(PrefConstant.WARE_HOUSE_ID)

        var reportsID =
            WMSSharedPref.getAppPrefs(WMSApplication.getInstance().context)
                .getStringValue(PrefConstant.REPORTS_ID_VALUE)

        val binID: MutableList<String> = ArrayList()
        val wareID: MutableList<String> = ArrayList()
        val binClass: MutableList<Int> = ArrayList()


        binID.add(reportsID)
        wareID.add(ware_id)
        binClass.add(1)

        val userId = WMSSharedPref.getAppPrefs(
            WMSApplication.getInstance().context
        ).getStringValue(PrefConstant.LOGIN_ID)

        val requestData = ReportRequestBin(wareID, binID,userId)

        model.getReportsFromBin(requestData).observe(this) { vDataList ->
            // update UI
            progress.visibility = View.GONE
            if (vDataList != null)
                reportList_data = (vDataList as List<ReportResponse>?)!!


            var sizeList = reportList_data.size.toString()



            Recycler_view.adapter = ReportBinAdapter(this, reportList_data)


        }
    }

    override fun onResume() {
        super.onResume()

    }


    companion object {
        private var instances2: ReportListFromBinNoActivity? = null
        fun getInstance(): ReportListFromBinNoActivity? {
            return instances2
        }
    }
}


