package com.clara.timekeeping

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.clara.timekeeping.ui.logout.FragmentLogout
import com.clara.timekeeping.ui.notification.NotificationActivity
import com.clara.timekeeping.utils.CommonUtils
import com.clara.timekeeping.utils.PreferenceHelper
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

    fun navigateToNotification() {
        val intent = Intent(this, NotificationActivity::class.java)
        startActivity(intent)
    }

    fun logout() {
        if (supportFragmentManager.findFragmentByTag("logout_fragment") == null) {
            val logoutFragment = FragmentLogout()
            supportFragmentManager.beginTransaction().add(logoutFragment, "logout_fragment")
                .commitAllowingStateLoss()
        }
    }
}