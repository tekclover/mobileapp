package com.clara.clientportal.ui.paymentplandetails

import android.content.pm.ActivityInfo
import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.activity.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.clara.clientportal.BaseActivity
import com.clara.clientportal.R
import com.clara.clientportal.databinding.ActivityPaymentPlanDetailsBinding
import com.clara.clientportal.ui.paymentplandetails.adapter.PaymentPlanDetailsAdapter
import com.clara.clientportal.utils.Constants
import com.clara.clientportal.utils.toDollar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PaymentPlanDetailsActivity : BaseActivity() {
    private lateinit var binding: ActivityPaymentPlanDetailsBinding
    private val viewModel: PaymentPlanDetailsViewModel by viewModels()
    private lateinit var frameAnimation: AnimationDrawable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPaymentPlanDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setToolBar(binding.lytToolbar.toolbar)
        initIntentValue()
        setObservers()
    }

    private fun initIntentValue() {
        if (intent != null && intent.extras != null) {
            val intent = intent
            if (intent.hasExtra(Constants.PAYMENT_PLAN_NO)) {
                viewModel.paymentPlanNo = intent.getStringExtra(Constants.PAYMENT_PLAN_NO) ?: ""
            }
            if (intent.hasExtra(Constants.PAYMENT_PLAN_REVISION_NO)) {
                viewModel.paymentPlanRevisionNo =
                    intent.getIntExtra(Constants.PAYMENT_PLAN_REVISION_NO, -1)
            }
        }
    }

    private fun setObservers() {
        viewModel.paymentPlanDetailsMutableLiveData.observe(this) {
            if (it != null) {
                with(it) {
                    binding.textMatterNo.text = this.matterNumber ?: ""
                    binding.textPaymentPlanNo.text = this.paymentPlanNumber ?: ""
                    binding.textPaymentPlanAmount.text =
                        this.paymentPlanTotalAmount?.toDouble()?.toDollar()
                    binding.textInstallmentAmount.text = this.dueAmount?.toDouble()?.toDollar()
                    binding.textNoOfInstallments.text = this.noOfInstallment?.toString()
                    var totalPaidAmount = 0.0
                    it.paymentPlanLines?.let { lineItemList ->
                        for (lineItem in lineItemList) {
                            totalPaidAmount += lineItem.paidAmount?.toDouble() ?: 0.0
                        }
                    }
                    binding.textBalanceAmount.text = ((this.paymentPlanTotalAmount?.toDouble() ?: 0.0) - (totalPaidAmount)).toDollar()
                }
                val linearLayoutManager = LinearLayoutManager(this)
                binding.paymentPlanDetailsList.layoutManager = linearLayoutManager
                binding.paymentPlanDetailsList.addItemDecoration(
                    DividerItemDecoration(
                        this,
                        DividerItemDecoration.VERTICAL
                    )
                )
                it.paymentPlanLines?.let { lineList ->
/*                    val sortedList = lineList.sortedBy { date ->
                        date.dueDate?.let { it1 ->
                            commonUtils.formatDate(Constants.MM_DD_YYYY).parse(
                                it1
                            )
                        }
                    } */
/*
                    val sortedList = lineList.sortedByDescending { line ->
                        line.itemNumber
                    }
*/
                    val paymentPlanDetailsAdapter =
                        PaymentPlanDetailsAdapter(lineList,commonUtils)
                    binding.paymentPlanDetailsList.adapter = paymentPlanDetailsAdapter
                    setHeaders()
                }
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
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        val notificationView = menu.findItem(R.id.action_notification)?.actionView
        val notificationCount = notificationView?.findViewById(R.id.notification_count) as? TextView
        notificationCount?.text = preferenceHelper.getNotificationCount().toString()
        notificationView?.setOnClickListener {
            navigateToNotification()
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }

            R.id.action_logout -> {
                logout()
                true
            }

            else -> false
        }
    }

    private fun setHeaders() {
        if (preferenceHelper.isTablet()) {
            with(binding) {
                lytHeaderView?.textTitle1?.text =
                    this@PaymentPlanDetailsActivity.resources.getString(R.string.installment_no)
                lytHeaderView?.textTitle2?.text =
                    this@PaymentPlanDetailsActivity.resources.getString(R.string.due_date)
                lytHeaderView?.textTitle3?.text =
                    this@PaymentPlanDetailsActivity.resources.getString(R.string.installment_amount)
                lytHeaderView?.textTitle4?.text =
                    this@PaymentPlanDetailsActivity.resources.getString(R.string.paid_amount)
                lytHeaderView?.textTitle5?.text =
                    this@PaymentPlanDetailsActivity.resources.getString(R.string.balance_amount)
                binding.lytHeaderView?.lytHeader5Parent?.visibility = View.VISIBLE
            }
        } else {
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }
    }
}