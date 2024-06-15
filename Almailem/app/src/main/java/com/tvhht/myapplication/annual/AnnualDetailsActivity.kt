package com.tvhht.myapplication.annual


import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.provider.ContactsContract
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.tvhht.myapplication.R
import com.tvhht.myapplication.annual.adapter.AnnualDetailAdapter
import com.tvhht.myapplication.annual.model.AnnualDetailHeader1
import com.tvhht.myapplication.annual.model.PeriodicLine
import com.tvhht.myapplication.annual.utils.BinCustomDialog
import com.tvhht.myapplication.annual.viewmodel.AnnualLiveData
import com.tvhht.myapplication.cases.ScannerErrorCustomDialog
import com.tvhht.myapplication.home.HomePageActivity
import com.tvhht.myapplication.utils.ApiConstant
import com.tvhht.myapplication.utils.LogoutFragment
import com.tvhht.myapplication.utils.NetworkUtils
import com.tvhht.myapplication.utils.PrefConstant
import com.tvhht.myapplication.utils.ToastUtils
import com.tvhht.myapplication.utils.WMSApplication
import com.tvhht.myapplication.utils.WMSSharedPref
import kotlinx.android.synthetic.main.activity_annual_detail.Recycler_view
import kotlinx.android.synthetic.main.activity_annual_detail.header_tt
import kotlinx.android.synthetic.main.activity_annual_detail.progress
import kotlinx.android.synthetic.main.activity_annual_detail.putaway_txt_count_val
import kotlinx.android.synthetic.main.activity_annual_detail.putaway_txt_user
import kotlinx.android.synthetic.main.tool_bar.toolbar
import kotlinx.android.synthetic.main.tool_bar.view.sign_out

class AnnualDetailsActivity : AppCompatActivity() {

    var stockData: MutableList<PeriodicLine> = ArrayList()
    var isScanned: Boolean = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_annual_detail)
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

        getStockList()
      registerReceivers()

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
            getStockList()
            registerReceivers()
        }

    }




    private val myBroadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent) {
            val action = intent.action
            val b = intent.extras
            if (intent.hasExtra("com.symbol.datawedge.api.RESULT_GET_VERSION_INFO")) {
                val versionInfo =
                    intent.getBundleExtra("com.symbol.datawedge.api.RESULT_GET_VERSION_INFO")
                val DWVersion = versionInfo!!.getString("DATAWEDGE")
                Log.d("LOG", "DataWedge Version: $DWVersion")
            }
            if (action == "com.tvhht.myapplication.ACTION") {
                try {
                    displayScanResult(intent, "via Broadcast")

                } catch (e: Exception) {
                    //  Catch error if the UI does not exist when we receive the broadcast...
                }
            }
        }
    }

    private fun displayScanResult(initiatingIntent: Intent, howDataReceived: String) {
        val decodedData =
            initiatingIntent.getStringExtra("com.symbol.datawedge.data_string")?.replace(" ", "")
        val decodedLabelType =
            initiatingIntent.getStringExtra("com.symbol.datawedge.label_type")
        Log.i("LOG", "DataWedge Scanned Code: $decodedData")
        Log.i("LOG", "DataWedge decodedLabelType: $decodedLabelType")

        if (decodedData.isNullOrEmpty().not() && isScanned((decodedData.toString()))) {
            val cdd =
                BinCustomDialog(
                    this@AnnualDetailsActivity,
                    "Bin Number Scanned",
                    decodedData.toString()
                )
            if (cdd.isShowing) {
                cdd.dismiss()
            }
            cdd.show()
        } else {
            val cdd = ScannerErrorCustomDialog(
                this@AnnualDetailsActivity,
                "Invalid Bin Number",
                decodedData.toString(), 1,
            )
            cdd.show()
        }
    }

    private fun registerReceivers() {
        Log.d("LOG", "registerReceivers()")
        val filter = IntentFilter()
        filter.addAction("com.symbol.datawedge.api.NOTIFICATION_ACTION") // for notification result
        filter.addAction("com.symbol.datawedge.api.ACTION") // for error code result
        filter.addCategory(Intent.CATEGORY_DEFAULT)
        filter.addAction(resources.getString(R.string.activity_intent_filter_action))
        filter.addAction(resources.getString(R.string.activity_action_from_service))
        registerReceiver(myBroadcastReceiver, filter)
    }

    fun unRegisterScannerStatus() {
        Log.d("LOG", "unRegisterScannerStatus()")
        val b = Bundle()
        b.putString("com.symbol.datawedge.api.APPLICATION_NAME", packageName)
        b.putString(
            "com.symbol.datawedge.api.NOTIFICATION_TYPE",
            "SCANNER_STATUS"
        )
        val i = Intent()
        i.action = ContactsContract.Intents.Insert.ACTION
        i.putExtra("com.symbol.datawedge.api.UNREGISTER_FOR_NOTIFICATION", b)
        this.sendBroadcast(i)
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(myBroadcastReceiver)
        unRegisterScannerStatus()
    }


    private fun getStockList() {


        val model = ViewModelProvider(this)[AnnualLiveData::class.java]

        progress.visibility = View.VISIBLE
       /* val wareId = WMSSharedPref.getAppPrefs(
            WMSApplication.getInstance().context
        ).getStringValue(PrefConstant.WARE_HOUSE_ID)

        var loginID =
            WMSSharedPref.getAppPrefs(WMSApplication.getInstance().context)
                .getStringValue(PrefConstant.LOGIN_ID)

        var stock_sel_index =
            WMSSharedPref.getAppPrefs(WMSApplication.getInstance().context)
                .getStringValue(PrefConstant.STOCK_INDEX_SEL)

        val assUser: MutableList<String> = ArrayList()
        val wareID: MutableList<String> = ArrayList()
        val headerstatusID: MutableList<Int> = ArrayList()
        val statusID: MutableList<Int> = ArrayList()
        val stock_sel: MutableList<String> = ArrayList()

        assUser.add(loginID)
        statusID.add(72)
        stock_sel.add(stock_sel_index)

        val requestData = AnnualDetailHeader1(wareId, assUser, statusID, stock_sel_index)*/

        val warehouseIdList: MutableList<String> = ArrayList()
        val companyCodeIdList: MutableList<String> = ArrayList()
        val languageIdList: MutableList<String> = ArrayList()
        val plantIdList: MutableList<String> = ArrayList()
        val cycleCountNoList: MutableList<String> = ArrayList()
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
        var stock_sel_index =
            WMSSharedPref.getAppPrefs(WMSApplication.getInstance().context)
                .getStringValue(PrefConstant.STOCK_INDEX_SEL)

        cycleCounterIdList.add(loginId)
        warehouseIdList.add(wareId)
        companyCodeIdList.add(companyCodeId)
        languageIdList.add(languageId)
        plantIdList.add(plantId)
        //headerStatusIdList.add(70)
        //headerStatusIdList.add(73)
        lineStatusIdList.add(72)
        lineStatusIdList.add(75)
        cycleCountNoList.add(stock_sel_index)

        val requestData = AnnualDetailHeader1(
            cycleCountNoList,
            cycleCounterIdList,
            lineStatusIdList,
            warehouseIdList,
            if (wareId == ApiConstant.WAREHOUSE_ID_200) null else companyCodeIdList,
            if (wareId == ApiConstant.WAREHOUSE_ID_200) null else plantIdList,
            languageIdList
        )

        model.getAnnualListDetails(requestData).observe(this) { vDataList ->
            // update UI
            progress.visibility = View.GONE


            if (vDataList != null && vDataList.isNotEmpty()) {
               // stockData.clear()
                stockData =
                    vDataList.sortedBy { (it.storageBin) } as MutableList<PeriodicLine>

                WMSSharedPref.getAppPrefs(
                    WMSApplication.getInstance().context
                ).saveAnnualStockInfo(stockData)
                var sizeList = stockData.size.toString()

                putaway_txt_count_val.text = sizeList

                Recycler_view.adapter = AnnualDetailAdapter(this, stockData)

            }

            else
            {
                finish()
                AnnualListActivity.getInstance()?.reload()
                ToastUtils.showToast(applicationContext, "No record found")
            }




        }
    }

    override fun onResume() {
        super.onResume()
        registerReceivers()

    }

    companion object {
        private var instances2: AnnualDetailsActivity? = null
        fun getInstance(): AnnualDetailsActivity? {
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
        AnnualListActivity.getInstance()?.reload()
        super.onBackPressed()
    }

    private fun isScanned(binId: String): Boolean {
        val find = stockData.find { it.storageBin == binId }
        if (find != null) {
            WMSSharedPref.getAppPrefs(WMSApplication.getInstance().context)
                .saveSingleStockAnnual(find)
            return true
        }
        return false
    }
}


