package com.tvhht.myapplication.cases

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.tvhht.myapplication.R
import com.tvhht.myapplication.cases.adapter.CaseASNAdapter
import com.tvhht.myapplication.cases.model.AsnList
import com.tvhht.myapplication.cases.model.CaseList
import com.tvhht.myapplication.cases.viewmodel.CasesLiveDataModel
import com.tvhht.myapplication.home.HomePageActivity
import com.tvhht.myapplication.login.LoginLiveData
import com.tvhht.myapplication.login.model.LoginModel
import com.tvhht.myapplication.utils.*
import kotlinx.android.synthetic.main.activity_asn_no.*
import kotlinx.android.synthetic.main.tool_bar.*
import kotlinx.android.synthetic.main.tool_bar.view.*

class ASNNumberListActivity : AppCompatActivity() {

    var asn_dataList: List<AsnList> = ArrayList()

    //    var filteredFinalArray: List<AsnList> = ArrayList()
    var distinctByArray: MutableList<AsnList> = ArrayList()
    var caseFilteredArray: MutableSet<CaseList> = HashSet()

    lateinit var adapter: CaseASNAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_asn_no)
        Recycler_view.layoutManager = LinearLayoutManager(
            this
        )
        Recycler_view.addItemDecoration(
            DividerItemDecoration(
                Recycler_view.context,
                DividerItemDecoration.VERTICAL
            )
        )
        instances2=this

        toolbar.sign_out.setOnClickListener {
            callLogout()
        }



        search_bar.setOnClickListener(View.OnClickListener {
            search_bar.isIconified = false
            search_bar.clearFocus()
        })

        search_bar.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                // inside on query text change method we are
                // calling a method to filter our recycler view.
                filter(newText!!)
                return false
            }
        })


        try {
            val imm: InputMethodManager =
                getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun processAccounts(listVOS: List<AsnList>?) {
        val loginID: String =
            WMSSharedPref.getAppPrefs(WMSApplication.getInstance().context)
                .getStringValue(PrefConstant.LOGIN_ID)

        progress.visibility = View.GONE

        if (listVOS != null) {
            asn_dataList = listVOS
            distinctByArray =
                asn_dataList.distinctBy { it.refDocNumber } as MutableList<AsnList>
            adapter = CaseASNAdapter(this, distinctByArray)
            Recycler_view.adapter = adapter

        }
    }


    private fun loginSuccess() {

        var request: LoginModel = LoginModel(
            ApiConstant.apiName_transaction,
            ApiConstant.clientId,
            ApiConstant.clientSecretKey,
            ApiConstant.grantType,
            ApiConstant.apiName_name,
            ApiConstant.apiName_pass_key
        )
        val model = ViewModelProviders.of(this)[LoginLiveData::class.java]


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

            model.getLoginStatus(request).observe(this) { asnList ->
                // update UI
                if (asnList.equals("ERROR")) {
                    ToastUtils.showToast(applicationContext, "User not Available")
                } else {
                    if (asnList.equals("FAILED")) {
                        ToastUtils.showToast(applicationContext, "No Data Available")
                    } else {
                        var repo = WMSApplication.getInstance().casesLocalRepository

                        var model = ViewModelProviders.of(this)[CasesLiveDataModel::class.java]
                        model.getStatus().observe(this) {
                        }
                        repo.fetchListData().observe(this) { listVOS: List<AsnList>? ->
                            processAccounts(listVOS)
                        }
                    }
                }

            }
        }

    }


    private fun filter(text: String) {
        // creating a new array list to filter our data.
        val filteredlist: ArrayList<AsnList> = ArrayList()
        // running a for loop to compare elements.
        for (item in distinctByArray) {
            // checking if the entered string matched with any item of our recycler view.
            if (item.refDocNumber?.lowercase()?.contains(text.lowercase())!!) {
                // if the item is matched we are
                // adding it to our filtered list.
                filteredlist.add(item)
            }
        }
        if (filteredlist.isEmpty()) {
            ToastUtils.showToast(this, "No Data Found..")
        }

        adapter.filterList(filteredlist)

    }


    override fun onResume() {
        super.onResume()
        loginSuccess()
    }

    fun callLogout() {
        val cdd = LogoutCustomDialog(
            this@ASNNumberListActivity
        )
        if (cdd.isShowing) {
            cdd.dismiss()
        }
        cdd.show()

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

    companion object {
        private var instances2: ASNNumberListActivity? = null
        fun getInstance(): ASNNumberListActivity? {
            return instances2
        }
    }

}

