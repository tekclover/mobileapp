package com.clara.clientportal.ui.receiptno

import android.content.ActivityNotFoundException
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
import com.clara.clientportal.databinding.ActivityReceiptNoBinding
import com.clara.clientportal.network.APIConstant
import com.clara.clientportal.ui.receiptno.adapter.ReceiptNoAdapter
import com.clara.clientportal.utils.Constants
import com.clara.clientportal.utils.FileUtils
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ReceiptNoActivity : BaseActivity() {
    private lateinit var binding: ActivityReceiptNoBinding
    private val viewModel: ReceiptNoViewModel by viewModels()
    private lateinit var frameAnimation: AnimationDrawable


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReceiptNoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setToolBar(binding.lytToolbar.toolbar)
        setObservers()
    }

    private fun setObservers() {
        viewModel.receiptNoMutableLiveData.observe(this) { it ->
            if (it.isNullOrEmpty().not()) {
                val linearLayoutManager = LinearLayoutManager(this)
                binding.receiptNoList.layoutManager = linearLayoutManager
                binding.receiptNoList.addItemDecoration(
                    DividerItemDecoration(
                        this,
                        DividerItemDecoration.VERTICAL
                    )
                )
                val filterList = it.filter { it.clientId == preferenceHelper.getClientId() }
                val sortedList = filterList.sortedByDescending { date ->
                    date.receiptDate?.let { it1 ->
                        commonUtils.formatDate(Constants.YYYY_MM_DD).parse(
                            it1
                        )
                    }
                }
               /* val sortedList = filterList.sortedByDescending { it1 ->
                    it1.receiptNo
                }*/
                val receiptNoAdapter =
                    ReceiptNoAdapter(sortedList, commonUtils, preferenceHelper, ::onClickDownload)
                binding.receiptNoList.adapter = receiptNoAdapter
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

    private fun onClickDownload(clientId: String, matterNo: String, downloadDocument: String) {
        viewModel.documentName = downloadDocument
        if (viewModel.documentName.isNotEmpty()) {
            initDownload(clientId, matterNo)
        } else {
            commonUtils.showToastMessage(
                this,
                this.resources.getString(R.string.no_document_to_display)
            )
        }
    }

    private fun initDownload(clientId: String, matterNo: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU || isExternalStoragePermissionGranted(
                this
            )
        ) {
            val url =
                APIConstant.BASE_URL + APIConstant.DOWNLOAD + Constants.QUESTION_MARK_SYMBOL + Constants.FILE_NAME + Constants.ASSIGNMENT_SYMBOL + viewModel.documentName + Constants.AMPERSAND_SYMBOL + Constants.LOCATION + Constants.ASSIGNMENT_SYMBOL + Constants.RECEIPT + Constants.SLASH_SYMBOL + clientId + Constants.SLASH_SYMBOL + matterNo
            viewModel.loaderMutableLiveData.value = true
            viewModel.downloadDocument(this, url, viewModel.documentName)
        } else {
            requestPermission(Constants.DOWNLOAD_PERMISSION)
        }
    }

    private fun setHeaders() {
        if (preferenceHelper.isTablet()) {
            with(binding) {
                lytHeaderView?.textTitle1?.text =
                    this@ReceiptNoActivity.resources.getString(R.string.document_type)
                lytHeaderView?.textTitle2?.text =
                    this@ReceiptNoActivity.resources.getString(R.string.receipt_no)
                lytHeaderView?.textTitle3?.text =
                    this@ReceiptNoActivity.resources.getString(R.string.type)
                lytHeaderView?.textTitle4?.text =
                    this@ReceiptNoActivity.resources.getString(R.string.date)
                lytHeaderView?.textTitle5?.text =
                    this@ReceiptNoActivity.resources.getString(R.string.status)
                lytHeaderView?.textTitle6?.text =
                    this@ReceiptNoActivity.resources.getString(R.string.doc)
                binding.lytHeaderView?.lytHeader6Parent?.visibility = View.VISIBLE
            }
        } else {
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }
    }
}