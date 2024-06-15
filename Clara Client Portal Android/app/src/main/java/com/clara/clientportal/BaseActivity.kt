package com.clara.clientportal

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.webkit.WebSettings
import android.webkit.WebView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.clara.clientportal.ui.logout.FragmentLogout
import com.clara.clientportal.ui.notification.NotificationActivity
import com.clara.clientportal.utils.CommonUtils
import com.clara.clientportal.utils.Constants
import com.clara.clientportal.utils.PreferenceHelper
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
open class BaseActivity : AppCompatActivity() {
    @Inject
    lateinit var commonUtils: CommonUtils

    @Inject
    lateinit var preferenceHelper: PreferenceHelper
    open fun setToolBar(toolbar: Toolbar?) {
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_arrow_left)
    }

    fun setFragment(fragment: Fragment?) {
        fragment?.let {
            if (isSimilarFragment(it).not()) {
                val fragmentTransaction = supportFragmentManager.beginTransaction()
                fragmentTransaction.replace(R.id.content_view, it, it.javaClass.name)
                fragmentTransaction.addToBackStack(it.javaClass.name)
                if (!isFinishing) {
                    fragmentTransaction.commitAllowingStateLoss()
                }
            }
        }
    }

    open fun isSimilarFragment(fragment: Fragment?): Boolean {
        return getActiveFragment() != null && fragment?.javaClass?.name.equals(getActiveFragment()?.javaClass?.name)
    }

    open fun getActiveFragment(): Fragment? {
        return when (supportFragmentManager.backStackEntryCount) {
            0 -> {
                null
            }

            else -> {
                val fragmentTag =
                    supportFragmentManager.getBackStackEntryAt(supportFragmentManager.backStackEntryCount - 1).name
                supportFragmentManager.findFragmentByTag(fragmentTag)
            }
        }
    }

    @SuppressLint("SetJavaScriptEnabled", "ObsoleteSdkInt")
    open fun setWebViewSettings(webView: WebView) {
        val webViewSetting = webView.settings
        webViewSetting.javaScriptEnabled = true
        webViewSetting.loadWithOverviewMode = true
        webViewSetting.useWideViewPort = true
        webViewSetting.setSupportZoom(true)
        webViewSetting.builtInZoomControls = false
        webViewSetting.cacheMode = WebSettings.LOAD_NO_CACHE
        webViewSetting.setRenderPriority(WebSettings.RenderPriority.HIGH)
        webViewSetting.allowContentAccess = true
        webViewSetting.allowFileAccess = true
        webViewSetting.domStorageEnabled = true
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            webViewSetting.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
        }
    }

    open fun isCameraPermissionGranted(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    }

    open fun isExternalStoragePermissionGranted(context: Context): Boolean {
        val isWritePermission: Boolean = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            true
        } else {
            ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        }
        return ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED &&
                isWritePermission
    }

    open fun requestPermission(permissionCode: Int) {
        when (permissionCode) {
            Constants.DOWNLOAD_PERMISSION -> {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    ),
                    permissionCode
                )
            }

            Constants.CAMERA_PERMISSION -> {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.CAMERA),
                    permissionCode
                )
            }
        }
    }

    open fun isPermissionGranted(grantResults: IntArray): Boolean {
        var isPermissionDenied = true
        if (grantResults.isNotEmpty()) {
            for (result in grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) isPermissionDenied = false
            }
        }
        return isPermissionDenied
    }
    fun logout() {
        if (supportFragmentManager.findFragmentByTag("logout_fragment") == null) {
            val logoutFragment = FragmentLogout()
            supportFragmentManager.beginTransaction().add(logoutFragment, "logout_fragment")
                .commitAllowingStateLoss()
        }
    }
    fun navigateToNotification() {
        val intent = Intent(this, NotificationActivity::class.java)
        intent.putExtra(Constants.IS_FROM_MENU, true)
        startActivity(intent)
    }
}