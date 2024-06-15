package com.clara.clientportal.ui.paymentplan

import android.content.Intent
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
import com.clara.clientportal.databinding.ActivityPaymentPlanBinding
import com.clara.clientportal.ui.paymentplan.adapter.PaymentPlanAdapter
import com.clara.clientportal.ui.paymentplandetails.PaymentPlanDetailsActivity
import com.clara.clientportal.utils.Constants
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PaymentPlanActivity : BaseActivity() {
    private lateinit var binding: ActivityPaymentPlanBinding
    private val viewModel: PaymentPlanViewModel by viewModels()
    private lateinit var frameAnimation: AnimationDrawable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPaymentPlanBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setToolBar(binding.lytToolbar.toolbar)
        setObservers()
    }

    private fun setObservers() {
        viewModel.paymentPlanMutableLiveData.observe(this) { it ->
            if (it.isNullOrEmpty().not()) {
                val linearLayoutManager = LinearLayoutManager(this)
                binding.paymentPlanList.layoutManager = linearLayoutManager
                binding.paymentPlanList.addItemDecoration(
                    DividerItemDecoration(
                        this,
                        DividerItemDecoration.VERTICAL
                    )
                )
                val filterList = it.filter { it.clientId == preferenceHelper.getClientId() }
/*
                val sortedList = filterList.sortedByDescending { date ->
                    date.paymentPlanDate?.let { it1 ->
                        commonUtils.formatDate(Constants.MM_DD_YYYY).parse(
                            it1
                        )
                    }
                }
*/
                val sortedList = filterList.sortedByDescending {
                    it.paymentPlanNumber
                }
                val paymentPlanAdapter =
                    PaymentPlanAdapter(
                        sortedList,
                        commonUtils,
                        preferenceHelper,
                        ::onPaymentDetails
                    )
                binding.paymentPlanList.adapter = paymentPlanAdapter
                setHeaders()
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

    private fun onPaymentDetails(paymentPlanNo: String, paymentPlanRevisionNo: Int) {
        val intent = Intent(this, PaymentPlanDetailsActivity::class.java)
        intent.putExtra(Constants.PAYMENT_PLAN_NO, paymentPlanNo)
        intent.putExtra(Constants.PAYMENT_PLAN_REVISION_NO, paymentPlanRevisionNo)
        startActivity(intent)
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
                    this@PaymentPlanActivity.resources.getString(R.string.matter_no)
                lytHeaderView?.textTitle2?.text =
                    this@PaymentPlanActivity.resources.getString(R.string.status)
                lytHeaderView?.textTitle3?.text =
                    this@PaymentPlanActivity.resources.getString(R.string.payment_plan_no)
                lytHeaderView?.textTitle4?.text =
                    this@PaymentPlanActivity.resources.getString(R.string.date)
                lytHeaderView?.textTitle5?.text =
                    this@PaymentPlanActivity.resources.getString(R.string.amount)
                lytHeaderView?.textTitle6?.text =
                    this@PaymentPlanActivity.resources.getString(R.string.details)
                binding.lytHeaderView?.lytHeader6Parent?.visibility = View.VISIBLE
            }
        } else {
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }
    }
}