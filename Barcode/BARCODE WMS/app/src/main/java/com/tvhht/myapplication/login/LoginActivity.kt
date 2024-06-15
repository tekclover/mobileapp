package com.tvhht.myapplication.login

import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.snackbar.Snackbar
import com.tvhht.myapplication.BuildConfig
import com.tvhht.myapplication.R
import com.tvhht.myapplication.home.HomePageActivity
import com.tvhht.myapplication.home.NewScannerPage
import com.tvhht.myapplication.login.model.LoginModel
import com.tvhht.myapplication.utils.*
import kotlinx.android.synthetic.main.activity_login.*


class LoginActivity : AppCompatActivity() {
    lateinit var pd: ProgressDialog
    lateinit var toUser: String
    lateinit var tPassword: String
    var listGroup: MutableList<String> = ArrayList()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        var loginEnter = findViewById<FrameLayout>(R.id.btn_login)
        var switchLogin = findViewById<SwitchCompat>(R.id.switchLogin)
        var imgTrueLogo = findViewById<ImageView>(R.id.imgTrueLogo)

        if(BuildConfig.DEBUG)
            imgTrueLogo.visibility=View.INVISIBLE
        else
            imgTrueLogo.visibility=View.VISIBLE
        instances2 = this;

        pd = ProgressDialog(this@LoginActivity)
        pd.setMessage("Loading...")



        loginEnter.setOnClickListener {
            toUser = username.text.toString()
            tPassword = password.text.toString()
            if (tPassword != null && tPassword.isNotEmpty() && toUser != null && toUser.isNotEmpty()) {
                loginSuccess(toUser, tPassword)
            } else {
                ToastUtils.showToast(applicationContext, "Please enter Username and Password")
            }

        }

        switchLogin.setOnTouchListener { v, event -> event.getActionMasked() === MotionEvent.ACTION_MOVE }
        switchLogin.setOnClickListener { v ->

            if (switchLogin.isChecked) {

                username.isEnabled=false
                password.isEnabled=false
                btn_login.isEnabled=false

                val myIntent =
                    Intent(this@LoginActivity, LoginCustomDialog::class.java)
                startActivity(myIntent)
            } else {

            }

        }


    }

    fun loginCancel() {
        var switchLogin = findViewById<SwitchCompat>(R.id.switchLogin)

        switchLogin.isChecked = false
        username.isEnabled=true
        password.isEnabled=true
        btn_login.isEnabled=true

    }

    fun loginSuccess(name: String, password: String) {
        try {
            val imm: InputMethodManager =
                getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        pd.show()
        var request: LoginModel = LoginModel(
            ApiConstant.apiName_id_master,
            ApiConstant.clientId,
            ApiConstant.clientSecretKey,
            ApiConstant.grantType,
            ApiConstant.apiName_name,
            ApiConstant.apiName_pass_key
        )
        val model = ViewModelProviders.of(this)[LoginLiveData::class.java]

        WMSSharedPref.getAppPrefs(
            WMSApplication.getInstance().context
        ).saveStringValue(
            PrefConstant.LOGIN_USER_ID,
            name
        )

        WMSSharedPref.getAppPrefs(
            WMSApplication.getInstance().context
        ).saveStringValue(
            PrefConstant.LOGIN_USER_KEY,
            password
        )

        if (!NetworkUtils().haveNetworkConnection(applicationContext)) {

            Handler().postDelayed({
                if (pd != null)
                    pd.dismiss()
                val parentLayout = findViewById<View>(android.R.id.content)
                val snackbar = Snackbar.make(
                    parentLayout, getString(R.string.internet_check_msg),
                    Snackbar.LENGTH_INDEFINITE
                )

                snackbar.setAction("Retry") {

                    snackbar.dismiss()
                    reload()
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

            model.getLoginHome(request, name, password)
                    ?.observe(this, Observer { this.setPasswordPolicy(it) })

        }
    }

    private fun setPasswordPolicy(asnList: String?) {
        if (asnList.equals("ERROR")) {
            if (pd != null)
                pd.dismiss()
            reload()
            ToastUtils.showToast(applicationContext, "User not Available")

        } else {
            if (asnList.equals("FAILED")) {
                if (pd != null)
                    pd.dismiss()
                reload()
                ToastUtils.showToast(applicationContext, "No Data Available")

            } else {
                loadUserDetails()
            }
        }
    }

    fun loadUserDetails() {
        val model = ViewModelProviders.of(this)[LoginLiveData::class.java]
        model.getUserStatus().observe(this) { userList ->
            // update UI
            if (pd != null)
                pd.dismiss()
            if (userList.equals("ERROR")) {
                ToastUtils.showToast(applicationContext, "User not Available")
            } else {
                if (userList.equals("FAILED")) {
                    ToastUtils.showToast(applicationContext, "No Data Available")
                } else {

                    val stringUserID = WMSSharedPref.getAppPrefs(
                        WMSApplication.getInstance().context
                    ).getStringValue(PrefConstant.LOGIN_ID)

                    val filter = userList?.filter { it.userId == stringUserID }

                    WMSSharedPref.getAppPrefs(
                        WMSApplication.getInstance().context
                    ).saveUserInfoVO(filter)

                    for (i in filter?.indices!!) {
                        listGroup.add(filter?.get(i)?.warehouseId.toString())
                    }

                    if (listGroup != null && listGroup.size == 1) {
                        WMSSharedPref.getAppPrefs(
                            WMSApplication.getInstance().context
                        ).saveStringValue(PrefConstant.USER_DETAILS_INDEX, listGroup[0])

                        val i = Intent(this@LoginActivity, NewScannerPage::class.java)
                        startActivity(i)
                        finish()
                    } else {
                        val i = Intent(this@LoginActivity, ConfirmGroupActivity::class.java)
                        i.putStringArrayListExtra(
                            "GROUP_LIST",
                            listGroup as java.util.ArrayList<String>
                        )
                        startActivity(i)
                        finish()
                    }

                }
            }

        }
    }


    fun loadGroupDetails() {

        val stringValue = WMSSharedPref.getAppPrefs(
            WMSApplication.getInstance().context
        ).getStringValue(PrefConstant.WARE_HOUSE_ID)
        val model = ViewModelProviders.of(this)[LoginLiveData::class.java]
        model.getWarehouseList(stringValue).observe(this) { warelist ->
            // update UI
            if (pd != null)
                pd.dismiss()
            if (warelist.equals("ERROR")) {
                ToastUtils.showToast(applicationContext, "Warehouse not Available")
            } else {
                if (warelist.equals("FAILED")) {
                    ToastUtils.showToast(applicationContext, "No Data Available")
                } else {
//
//                                        WMSSharedPref.getAppPrefs(
//                        WMSApplication.getInstance().context
//                    ).saveUserInfoVO(userList)
//                    loadGroupDetails()


                }
            }

        }
    }

    fun reload() {
        val intent = intent
        finish()
        overridePendingTransition(0, 0)
        startActivity(intent)
    }


    companion object {
        private var instances2: LoginActivity? = null
        fun getInstance(): LoginActivity? {
            return instances2
        }
    }

}

