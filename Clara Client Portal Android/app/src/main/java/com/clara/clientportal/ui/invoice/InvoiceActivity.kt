package com.clara.clientportal.ui.invoice

import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.ActivityInfo
import android.graphics.drawable.AnimationDrawable
import android.os.Build
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
import com.clara.clientportal.databinding.ActivityInvoiceBinding
import com.clara.clientportal.model.InvoiceResponse
import com.clara.clientportal.network.APIConstant
import com.clara.clientportal.ui.invoice.adapter.InvoiceAdapter
import com.clara.clientportal.ui.webview.WebViewActivity
import com.clara.clientportal.utils.Constants
import com.clara.clientportal.utils.FileUtils
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class InvoiceActivity : BaseActivity() {
    private lateinit var binding: ActivityInvoiceBinding
    private val viewModel: InvoiceViewModel by viewModels()
    private lateinit var frameAnimation: AnimationDrawable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInvoiceBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setToolBar(binding.lytToolbar.toolbar)
        setObservers()
    }

    private fun setObservers() {
        viewModel.invoiceMutableLiveData.observe(this) {
            if (it.isNullOrEmpty().not()) {
                val linearLayoutManager = LinearLayoutManager(this)
                binding.invoiceList.layoutManager = linearLayoutManager
                binding.invoiceList.addItemDecoration(
                    DividerItemDecoration(
                        this,
                        DividerItemDecoration.VERTICAL
                    )
                )
                /*val sortedList = it.sortedByDescending { date ->
                    date.invoiceDate?.let { it1 ->
                        commonUtils.formatDate(Constants.MM_DD_YYYY).parse(
                            it1
                        )
                    }
                }*/
                val sortedList = it.filter { invoiceList -> invoiceList.referenceField8 == Constants.SENT }
                                 it.sortedByDescending { it1 ->
                                 it1.invoiceNumber }
                val invoiceAdapter =
                    InvoiceAdapter(sortedList, commonUtils, preferenceHelper, ::onViewClick)
                binding.invoiceList.adapter = invoiceAdapter
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
        viewModel.downloadMutableLiveData.observe(this) {
            viewModel.loaderMutableLiveData.value = false
            if (it.isNotEmpty()) {
                try {
                    startActivity(FileUtils.getActionViewIntent(this, viewModel.documentName, it))
                } catch (e: ActivityNotFoundException) {
                    e.printStackTrace()
                }
            }
        }

    }

    private fun onViewClick(invoice: InvoiceResponse, isPayment: Boolean) {
        if (isPayment) {
            if (invoice.referenceField10.isNullOrEmpty().not()) {
                val intent = Intent(this, WebViewActivity::class.java)
                intent.putExtra(Constants.WEB_VIEW_URL, invoice.referenceField10)
                startActivity(intent)
            } else {
                commonUtils.showToastMessage(
                    this,
                    this.resources.getString(R.string.no_payment_to_display)
                )
            }
        } else {
            if (invoice.referenceField1.isNullOrEmpty().not()) {
                invoice.matterNumber?.let {
                    viewModel.documentName = invoice.referenceField1 ?: ""
                    initDownload(it)
                }
            } else {
                commonUtils.showToastMessage(
                    this,
                    this.resources.getString(R.string.no_document_to_display)
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


    private fun initDownload(matterNo: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU || isExternalStoragePermissionGranted(
                this
            )
        ) {
            val url =
                APIConstant.BASE_URL + APIConstant.DOWNLOAD + Constants.QUESTION_MARK_SYMBOL + Constants.FILE_NAME + Constants.ASSIGNMENT_SYMBOL + viewModel.documentName + Constants.AMPERSAND_SYMBOL + Constants.LOCATION + Constants.ASSIGNMENT_SYMBOL + Constants.INVOICE + Constants.SLASH_SYMBOL + matterNo
            viewModel.loaderMutableLiveData.value = true
            viewModel.downloadDocument(this, url, viewModel.documentName)
        } else {
            requestPermission(Constants.DOWNLOAD_PERMISSION)
        }
    }

    private fun setHeaders() {
        with(binding) {
            if (preferenceHelper.isTablet()) {
                lytHeaderView?.textTitle1?.text =
                    this@InvoiceActivity.resources.getString(R.string.matter_no)
                lytHeaderView?.textTitle2?.text =
                    this@InvoiceActivity.resources.getString(R.string.invoice_no)
                lytHeaderView?.textTitle3?.text =
                    this@InvoiceActivity.resources.getString(R.string.bill_amount)
                lytHeaderView?.textTitle4?.text =
                    this@InvoiceActivity.resources.getString(R.string.date)
                lytHeaderView?.textTitle5?.text =
                    this@InvoiceActivity.resources.getString(R.string.status)
                lytHeaderView?.textTitle6?.text =
                    this@InvoiceActivity.resources.getString(R.string.doc)
                lytHeaderView?.textTitle7?.text =
                    this@InvoiceActivity.resources.getString(R.string.actions)
                binding.lytHeaderView?.lytHeader7Parent?.visibility = View.VISIBLE
            } else {
                requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            }
        }
    }
}