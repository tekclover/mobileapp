package com.tvhht.myapplication.reports


import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.tvhht.myapplication.R
import com.tvhht.myapplication.login.LoginLiveData
import com.tvhht.myapplication.login.model.LoginModel
import com.tvhht.myapplication.reports.adapter.ReportItemCodeAdapter
import com.tvhht.myapplication.reports.model.ReportRequestItemCode
import com.tvhht.myapplication.reports.model.ReportResponse
import com.tvhht.myapplication.reports.viewmodel.ReportLiveDataModel
import com.tvhht.myapplication.utils.*
import kotlinx.android.synthetic.main.activity_quality_details_confirm.*
import kotlinx.android.synthetic.main.activity_report_item_code.*
import kotlinx.android.synthetic.main.activity_report_item_code.Recycler_view
import kotlinx.android.synthetic.main.activity_report_item_code.progress
import kotlinx.android.synthetic.main.tool_bar.*
import kotlinx.android.synthetic.main.tool_bar.view.*
import java.util.*
import kotlin.collections.ArrayList

class ReportListFromItemCodeActivity : AppCompatActivity() {

    var isScanned: Boolean = false
    lateinit var reportsID: String
    var reportList_data: List<ReportResponse> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_report_item_code)
        Recycler_view.layoutManager = LinearLayoutManager(
            this
        )
        instances2 = this

        toolbar.sign_out.setOnClickListener {
            val cdd = LogoutCustomDialog(
                this@ReportListFromItemCodeActivity
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

        progress.visibility = View.GONE


        scanReports.setOnClickListener {

            try {
                val imm: InputMethodManager =
                    getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
            } catch (e: Exception) {
                e.printStackTrace()
            }

            if (palletIDEnter.text.toString().isNotEmpty()) {
                reportsID = palletIDEnter.text.toString()
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
                    verifyToken()
                }
            } else {
                ToastUtils.showToast(
                    applicationContext,
                    "Please enter Item Code"
                )
            }
        }

        reloadReports.setOnClickListener {

            try {
                val imm: InputMethodManager =
                    getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
            } catch (e: Exception) {
                e.printStackTrace()
            }

            val intent = intent
            overridePendingTransition(0, 0)
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
            finish()
            overridePendingTransition(0, 0)
            startActivity(intent)


        }


    }


    fun verifyToken() {


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


        val binID: MutableList<String> = ArrayList()
        val wareID: MutableList<String> = ArrayList()
        val binClass: MutableList<Int> = ArrayList()


        binID.add(reportsID)
        wareID.add(ware_id)
        binClass.add(1)

        val requestData = ReportRequestItemCode(wareID, binID)

        model.getReportsFromItemCode(requestData).observe(this) { vDataList ->
            // update UI
            progress.visibility = View.GONE
            if (vDataList != null)
                reportList_data = (vDataList as List<ReportResponse>?)!!


            var sizeList = reportList_data.size.toString()



            Recycler_view.adapter = ReportItemCodeAdapter(this, reportList_data)


        }
    }

    override fun onResume() {
        super.onResume()

    }


    companion object {
        private var instances2: ReportListFromItemCodeActivity? = null
        fun getInstance(): ReportListFromItemCodeActivity? {
            return instances2
        }
    }
}


