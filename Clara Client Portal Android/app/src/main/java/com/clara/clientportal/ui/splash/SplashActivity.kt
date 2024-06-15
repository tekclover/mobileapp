package com.clara.clientportal.ui.splash

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import com.clara.clientportal.BaseActivity
import com.clara.clientportal.R
import com.clara.clientportal.databinding.ActivitySplashBinding
import com.clara.clientportal.ui.home.HomeActivity
import com.clara.clientportal.ui.signin.SignInActivity
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SplashActivity : BaseActivity() {
    private lateinit var binding: ActivitySplashBinding
    private val viewModel: SplashViewModel by viewModels()

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.lytSplashParent.tag?.let {
            preferenceHelper.setTablet(commonUtils.isTablet(it.toString()))
        }
        if (preferenceHelper.isTablet().not()) {
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }
        viewModel.statusMutableLiveData.observe(this) {
            if (it.isNullOrEmpty().not()) {
                preferenceHelper.setStatus(Gson().toJson(it))
            }
            askNotificationPermission()
        }
        viewModel.networkMutableLiveDat.observe(this) {
            if (it) {
                commonUtils.showToastMessage(this, this.resources.getString(R.string.no_network))
            }
        }
    }

    private fun askNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                navigateToHomePage()
            } else {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        } else {
            navigateToHomePage()
        }
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { isGranted: Boolean ->
        if (isGranted.not()) {
            commonUtils.showToastMessage(
                this,
                this.resources.getString(R.string.notification_denied_message)
            )
        }
        navigateToHomePage()
    }

    private fun navigateToHomePage() {
        if (preferenceHelper.getClientDetails().isNotEmpty()) {
            startActivity(Intent(this, HomeActivity::class.java))
        } else {
            startActivity(Intent(this, SignInActivity::class.java))
        }
        finish()
    }
}