package com.clara.clientportal.ui.notification

import android.annotation.SuppressLint
import android.content.Intent
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
import com.clara.clientportal.BaseActivity
import com.clara.clientportal.R
import com.clara.clientportal.databinding.ActivityNotificationBinding
import com.clara.clientportal.enums.NotificationOrderType
import com.clara.clientportal.model.NotificationResponse
import com.clara.clientportal.network.APIConstant
import com.clara.clientportal.ui.checklist.CheckListActivity
import com.clara.clientportal.ui.documentupload.DocumentUploadActivity
import com.clara.clientportal.ui.invoice.InvoiceActivity
import com.clara.clientportal.ui.matter.MatterActivity
import com.clara.clientportal.ui.notification.adapter.NotificationAdapter
import com.clara.clientportal.ui.paymentplan.PaymentPlanActivity
import com.clara.clientportal.ui.quotation.InitialRetainerActivity
import com.clara.clientportal.ui.receiptno.ReceiptNoActivity
import com.clara.clientportal.utils.Constants
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
        initIntentValue()
        setObservers()
        setListener()
        onBackPressedDispatcher.addCallback(this, backPressedCallback)
    }
    override fun onResume() {
        super.onResume()
        viewModel.getAuthToken(APIConstant.NOTIFICATION_MESSAGE_ID)
    }

    private fun initIntentValue() {
        intent?.extras?.let {
            viewModel.orderType = it.getString(Constants.ORDER_TYPE, "")
            viewModel.isFromMenu = it.getBoolean(Constants.IS_FROM_MENU, false)
            viewModel.isFromTab = it.getBoolean(Constants.IS_FROM_TAB, false)
        }
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
                val sortedList = it.sortedByDescending { date ->
                    date.createdOn?.let { it1 ->
                        commonUtils.formatDate(Constants.YYYY_MM_DD_T_HH_MM_SS_SSS_XXX).parse(
                            it1
                        )
                    }
                }

                val adapter = NotificationAdapter(
                    sortedList,
                    viewModel.isFromMenu,
                    viewModel.isFromTab,
                    ::onNotificationClick
                )
                binding.notificationList.adapter = adapter
            }
        }
        viewModel.loaderMutableLiveData.observe(this) {
            if (it) {
                binding.lytProgressParent.lytProgress.visibility = View.VISIBLE
                binding.lytProgressParent.lytProgress.isEnabled = false
                binding.lytProgressParent.lytProgress.isClickable = false
                binding.lytProgressParent.imgProgress.setBackgroundResource(R.drawable.progress_frame_animation)
                frameAnimation =
                    binding.lytProgressParent.imgProgress.background as AnimationDrawable
                frameAnimation.start()
            } else {
                binding.lytProgressParent.lytProgress.visibility = View.GONE
                if (::frameAnimation.isInitialized && frameAnimation.isRunning) {
                    frameAnimation.stop()
                }
            }
        }
        viewModel.networkMutableLiveData.observe(this) {
            if (it) {
                commonUtils.showToastMessage(
                    this,
                    this.resources.getString(R.string.no_network)
                )
            }
        }
        viewModel.apiFailureMutableLiveData.observe(this) {
            if (it) {
                commonUtils.showToastMessage(
                    this,
                    this.resources.getString(R.string.api_failure_message)
                )
            }
        }
        viewModel.notificationUpdateLiveData.observe(this) {
            backPressed()
        }
    }

    private fun onNotificationClick(orderType: String) {
        viewModel.selectedOrder = orderType
        notificationUpdateAPICall()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        menu.findItem(R.id.action_notification).isVisible = false
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                viewModel.selectedOrder = ""
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
                viewModel.selectedOrder = ""
                notificationUpdateAPICall()
            }
        }
    }

    private fun notificationUpdateAPICall() {
        if (isNotificationHighLighted()) {
            viewModel.getAuthToken(APIConstant.NOTIFICATION_MESSAGE_UPDATE_ID)
        } else {
            backPressed()
        }
    }
    private fun isNotificationHighLighted(): Boolean {
        var notificationList: List<NotificationResponse>? = null
        when {
            viewModel.isFromMenu -> {
                notificationList =
                    viewModel.notificationLiveData.value?.filter { it.menu?.not() == true }
            }
            viewModel.isFromTab -> {
                notificationList =
                    viewModel.notificationLiveData.value?.filter { it.tab?.not() == true }
            }
        }
        return notificationList.isNullOrEmpty().not()
    }
    private fun backPressed() {
        when (viewModel.selectedOrder) {
            NotificationOrderType.MATTER.orderType -> startActivity(
                Intent(
                    this,
                    MatterActivity::class.java
                )
            )

            NotificationOrderType.INITIAL.orderType -> startActivity(
                Intent(
                    this,
                    InitialRetainerActivity::class.java
                )
            )

            NotificationOrderType.INVOICE.orderType -> startActivity(
                Intent(
                    this,
                    InvoiceActivity::class.java
                )
            )

            NotificationOrderType.PAYMENT_PLAN.orderType -> startActivity(
                Intent(
                    this,
                    PaymentPlanActivity::class.java
                )
            )

            NotificationOrderType.CHECKLIST.orderType -> startActivity(
                Intent(
                    this,
                    CheckListActivity::class.java
                )
            )

            NotificationOrderType.DOCUMENT_UPLOAD.orderType -> startActivity(
                Intent(
                    this,
                    DocumentUploadActivity::class.java
                )
            )

            NotificationOrderType.RECEIPT.orderType -> startActivity(
                Intent(
                    this,
                    ReceiptNoActivity::class.java
                )
            )

            else -> {
                finish()
            }
        }
    }
}