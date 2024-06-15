package com.tvhht.myapplication

import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.tvhht.myapplication.home.HomePageActivity
import com.tvhht.myapplication.home.NewScannerPage
import com.tvhht.myapplication.login.LoginActivity
import com.tvhht.myapplication.utils.PrefConstant
import com.tvhht.myapplication.utils.WMSApplication
import com.tvhht.myapplication.utils.WMSSharedPref


class SplashActivity : AppCompatActivity() {

    var checkForSession: Boolean = false
    private val EXTRA_PROFILENAME = "WMS_DATA2"

    // DataWedge Extras
    private val EXTRA_GET_VERSION_INFO = "com.symbol.datawedge.api.GET_VERSION_INFO"
    private val EXTRA_CREATE_PROFILE = "com.symbol.datawedge.api.CREATE_PROFILE"
    private val EXTRA_KEY_APPLICATION_NAME = "com.symbol.datawedge.api.APPLICATION_NAME"
    private val EXTRA_KEY_NOTIFICATION_TYPE = "com.symbol.datawedge.api.NOTIFICATION_TYPE"
    private val EXTRA_REGISTER_NOTIFICATION = "com.symbol.datawedge.api.REGISTER_FOR_NOTIFICATION"
    private val EXTRA_SET_CONFIG = "com.symbol.datawedge.api.SET_CONFIG"
    private val EXTRA_SEND_RESULT = "SEND_RESULT"
    private val EXTRA_EMPTY = ""

    // DataWedge Actions
    private val ACTION_DATAWEDGE = "com.symbol.datawedge.api.ACTION"

    // private variables
    private val bRequestSendResult = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
//        window.setFlags(
//            WindowManager.LayoutParams.FLAG_FULLSCREEN,
//            WindowManager.LayoutParams.FLAG_FULLSCREEN);
        // Validate checkForSession


        CreateProfile()


        @Suppress("DEPRECATION")
        Handler().postDelayed(Runnable {
            val sCache =
                WMSSharedPref.getAppPrefs(WMSApplication.getInstance().context)
                    .getBooleanValue(PrefConstant.LOGIN_STATUS)
            WMSSharedPref.getAppPrefs(
                WMSApplication.getInstance().context
            ).saveIntValue(PrefConstant.HOME_PAGE_INDEX, 0)


            if (sCache)
                loginfromSession()
            else
                newLogin()

        }, 3000)
    }


    fun CreateProfile() {
        val profileName: String = EXTRA_PROFILENAME

        sendDataWedgeIntentWithExtra(
            ACTION_DATAWEDGE,
            EXTRA_CREATE_PROFILE,
            profileName
        )

        // Configure created profile to apply to this app
        val profileConfig = Bundle()
        profileConfig.putString("PROFILE_NAME", EXTRA_PROFILENAME)
        profileConfig.putString("PROFILE_ENABLED", "true")
        profileConfig.putString(
            "CONFIG_MODE",
            "CREATE_IF_NOT_EXIST"
        )

        val barcodeConfig = Bundle()
        barcodeConfig.putString("PLUGIN_NAME", "BARCODE")
        barcodeConfig.putString("RESET_CONFIG", "true") //  This is the default
        val barcodeProps = Bundle()
        barcodeConfig.putBundle("PARAM_LIST", barcodeProps)
        profileConfig.putBundle("PLUGIN_CONFIG", barcodeConfig)

        // Associate profile with this app
        val appConfig = Bundle()
        appConfig.putString("PACKAGE_NAME", packageName)
        appConfig.putStringArray("ACTIVITY_LIST", arrayOf("*"))
        profileConfig.putParcelableArray("APP_LIST", arrayOf(appConfig))
        profileConfig.remove("PLUGIN_CONFIG")

        sendDataWedgeIntentWithExtra(
            ACTION_DATAWEDGE,
            EXTRA_SET_CONFIG,
            profileConfig
        )

        // Configure intent output for captured data to be sent to this app
        val intentConfig = Bundle()
        intentConfig.putString("PLUGIN_NAME", "INTENT")
        intentConfig.putString("RESET_CONFIG", "true")
        val intentProps = Bundle()
        intentProps.putString("intent_output_enabled", "true")
        intentProps.putString("intent_action", "com.tvhht.myapplication.ACTION")
        intentProps.putString("intent_delivery", "2")
        intentConfig.putBundle("PARAM_LIST", intentProps)
        profileConfig.putBundle("PLUGIN_CONFIG", intentConfig)
        sendDataWedgeIntentWithExtra(
            ACTION_DATAWEDGE,
            EXTRA_SET_CONFIG,
            profileConfig
        )
        scanSettings()
    }

    fun scanSettings() {
        // Main bundle properties
        val profileConfig = Bundle()
        profileConfig.putString("PROFILE_NAME", EXTRA_PROFILENAME)
        profileConfig.putString("PROFILE_ENABLED", "true")
        profileConfig.putString("CONFIG_MODE", "UPDATE") // Update specified settings in profile

        val barcodeConfig = Bundle()
        barcodeConfig.putString("PLUGIN_NAME", "BARCODE")
        barcodeConfig.putString("RESET_CONFIG", "true")

        val barcodeProps = Bundle()
        barcodeProps.putString("scanner_selection", "auto")
        barcodeProps.putString("scanner_input_enabled", "true")
        barcodeProps.putString("decoder_code128", "true")
        barcodeProps.putString("decoder_code39", "true")
        barcodeProps.putString("decoder_ean13", "true")
        barcodeProps.putString("decoder_upca", "true")

        barcodeConfig.putBundle("PARAM_LIST", barcodeProps)
        profileConfig.putBundle("PLUGIN_CONFIG", barcodeConfig)

        val appConfig = Bundle()
        appConfig.putString("PACKAGE_NAME", packageName)
        appConfig.putStringArray("ACTIVITY_LIST", arrayOf("*"))
        profileConfig.putParcelableArray("APP_LIST", arrayOf(appConfig))
        sendDataWedgeIntentWithExtra(
            ACTION_DATAWEDGE,
            EXTRA_SET_CONFIG,
            profileConfig
        )


        val b = Bundle()
        b.putString(EXTRA_KEY_APPLICATION_NAME, packageName)
        b.putString(
            EXTRA_KEY_NOTIFICATION_TYPE,
            "SCANNER_STATUS"
        ) // register for changes in scanner status

        sendDataWedgeIntentWithExtra(
            ACTION_DATAWEDGE,
            EXTRA_REGISTER_NOTIFICATION,
            b
        )
       sendDataWedgeIntentWithExtra(
            ACTION_DATAWEDGE,
            EXTRA_GET_VERSION_INFO,
            EXTRA_EMPTY
        )

    }


    private fun sendDataWedgeIntentWithExtra(action: String, extraKey: String, extras: Bundle) {
        val dwIntent = Intent()
        dwIntent.action = action
        dwIntent.putExtra(extraKey, extras)
        if (bRequestSendResult) dwIntent.putExtra(EXTRA_SEND_RESULT, "true")
        this.sendBroadcast(dwIntent)
    }

    private fun sendDataWedgeIntentWithExtra(action: String, extraKey: String, extras: String) {
        val dwIntent = Intent()
        dwIntent.action = action
        dwIntent.putExtra(extraKey, extras)
        if (bRequestSendResult) dwIntent.putExtra(EXTRA_SEND_RESULT, "true")
        this.sendBroadcast(dwIntent)
    }

    private fun loginfromSession() {
        val i = Intent(this@SplashActivity, NewScannerPage::class.java)
        startActivity(i)
        finish()
    }


    private fun newLogin() {
        val i = Intent(this@SplashActivity, LoginActivity::class.java)
        startActivity(i)
        finish()
    }


}