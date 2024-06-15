package com.clara.timekeeping.ui.notification

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.clara.timekeeping.BaseActivity
import com.clara.timekeeping.R
import com.clara.timekeeping.databinding.ActivityNotificationBinding
import com.clara.timekeeping.network.APIConstant
import com.clara.timekeeping.ui.notification.adapter.NotificationAdapter
import com.clara.timekeeping.utils.setEnable
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NotificationActivity : BaseActivity() {
    private lateinit var binding: ActivityNotificationBinding
    private val viewModel: NotificationViewModel by viewModels()
    private lateinit var frameAnimation: AnimationDrawable
    private lateinit var backPressedCallback: OnBackPressedCallback

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNotificationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setToolBar(binding.lytToolbar.toolbar)
        if (preferenceHelper.isTablet().not()) {
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }
        setObservers()
        setListener()
        onBackPressedDispatcher.addCallback(this, backPressedCallback)
    }

    override fun onResume() {
        super.onResume()
        viewModel.getAuthToken(APIConstant.NOTIFICATION_MESSAGE_ID)
    }

    private fun setObservers() {
        viewModel.notificationLiveData.observe(this) {
            if (it.isNullOrEmpty().not()) {
                val linearLayoutManager = LinearLayoutManager(this)
                binding.notificationList.layoutManager = linearLayoutManager
                binding.notificationList.addItemDecoration(
                    DividerItemDecoration(
                        this,
                        DividerItemDecoration.VERTICAL
                    )
                )
                val adapter = NotificationAdapter(
                    it,commonUtils
                )
                binding.notificationList.adapter = adapter
            }
        }
        viewModel.loaderMutableLiveData.observe(this) {
            with(binding) {
                if (it) {
                    lytProgressParent.lytProgress.visibility = View.VISIBLE
                    lytProgressParent.lytProgress.setEnable(false)
                    lytProgressParent.imgProgress.setBackgroundResource(R.drawable.progress_frame_animation)
                    frameAnimation =
                        lytProgressParent.imgProgress.background as AnimationDrawable
                    commonUtils.enableDisableViews(lytNotificationRoot, false)
                    frameAnimation.start()
                } else {
                    lytProgressParent.lytProgress.visibility = View.GONE
                    stopAnimation()
                    commonUtils.enableDisableViews(lytNotificationRoot, true)
                }
            }
        }
        viewModel.networkMutableLiveData.observe(this) {
            if (it) {
                commonUtils.showToastMessage(this, this.resources.getString(R.string.no_network))
            }
        }
        viewModel.apiFailureMutableLiveData.observe(this) {
            commonUtils.showToastMessage(
                this,
                if (it.isNullOrEmpty()
                        .not()
                ) it else this.resources.getString(R.string.api_failure_message)
            )
        }
        viewModel.notificationUpdateLiveData.observe(this) {
            finish()
        }
    }

    private fun stopAnimation() {
        if (::frameAnimation.isInitialized && frameAnimation.isRunning) {
            frameAnimation.stop()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        menu.findItem(R.id.action_notification).isVisible = false
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                notificationUpdateAPICall()
                true
            }

            R.id.action_logout -> {
                logout()
                true
            }

            else -> false
        }
    }

    private fun setListener() {
        backPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                notificationUpdateAPICall()
            }
        }
    }

    private fun notificationUpdateAPICall() {
        val notificationList =
            viewModel.notificationLiveData.value?.filter { it.menu?.not() == true }
        if (notificationList.isNullOrEmpty().not()) {
            viewModel.getAuthToken(APIConstant.NOTIFICATION_MESSAGE_UPDATE_ID)
        } else {
            finish()
        }
    }
}