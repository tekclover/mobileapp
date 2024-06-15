package com.clara.timekeeping.ui.summary

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.clara.timekeeping.BaseActivity
import com.clara.timekeeping.R
import com.clara.timekeeping.databinding.ActivityTimeTicketSummaryBinding
import com.clara.timekeeping.model.TimeTicketSummaryResponse
import com.clara.timekeeping.network.APIConstant
import com.clara.timekeeping.ui.newticket.NewTicketActivity
import com.clara.timekeeping.ui.search.SearchActivity
import com.clara.timekeeping.ui.summary.adapter.SummaryAdapter
import com.clara.timekeeping.utils.Constants
import com.clara.timekeeping.utils.CustomResultLauncher
import com.clara.timekeeping.utils.FilterHelperClass
import com.clara.timekeeping.utils.setEnable
import dagger.hilt.android.AndroidEntryPoint
import java.io.Serializable


@AndroidEntryPoint
class TimeTicketSummaryActivity : BaseActivity() {
    lateinit var binding: ActivityTimeTicketSummaryBinding
    private val viewModel: SummaryViewModel by viewModels()
    private lateinit var frameAnimation: AnimationDrawable
    private lateinit var summaryAdapter: SummaryAdapter

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTimeTicketSummaryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setToolBar(binding.lytToolbar.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(false)
        if (preferenceHelper.isTablet().not()) {
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }
        setListener()
        observeObservers()
        viewModel.loaderMutableLiveData.value = true
        viewModel.getAuthToken(APIConstant.FIND_MATTER_TIME_TICKET_ID)
    }

    override fun onResume() {
        super.onResume()
        viewModel.getAuthToken(APIConstant.NOTIFICATION_COUNT_ID)
    }

    private fun setListener() {
        with(binding) {
            btnNew.setOnClickListener {
                summaryFilter.text = null
                val intent = Intent(this@TimeTicketSummaryActivity, NewTicketActivity::class.java)
                activityResultLauncher.launch(intent,
                    object : CustomResultLauncher.ActivityResultListener<ActivityResult> {
                        override fun onActivityResult(result: ActivityResult) {
                            if (result.resultCode == RESULT_OK) {
                                viewModel.loaderMutableLiveData.value = true
                                viewModel.getAuthToken(APIConstant.FIND_MATTER_TIME_TICKET_ID)
                            }
                        }
                    })
            }
            btnSearch.setOnClickListener {
                summaryFilter.text = null
                val intent = Intent(this@TimeTicketSummaryActivity, SearchActivity::class.java)
                activityResultLauncher.launch(intent,
                    object : CustomResultLauncher.ActivityResultListener<ActivityResult> {
                        override fun onActivityResult(result: ActivityResult) {
                            if (result.resultCode == RESULT_OK) {
                                viewModel.loaderMutableLiveData.value = true
                                viewModel.getAuthToken(APIConstant.FIND_MATTER_TIME_TICKET_ID)
                            }
                        }
                    })
            }

            lytSwipe.setOnRefreshListener {
                summaryFilter.text = null
                viewModel.loaderMutableLiveData.value = true
                viewModel.getAuthToken(APIConstant.FIND_MATTER_TIME_TICKET_ID)
                binding.lytSwipe.isRefreshing = false
            }
            btnReset.setOnClickListener {
                summaryFilter.text = null
                FilterHelperClass.clearSearch()
                viewModel.loaderMutableLiveData.value = true
                viewModel.getAuthToken(APIConstant.FIND_MATTER_TIME_TICKET_ID)
            }
            summaryFilter.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                    binding.imgSearchClear.visibility =
                        if (s.isNotEmpty()) View.VISIBLE else View.GONE
                    if (::summaryAdapter.isInitialized) summaryAdapter.filter.filter(s.toString())
                }

                override fun afterTextChanged(s: Editable) {}
            })
            imgSearchClear.setOnClickListener {
                summaryFilter.text = null
            }
        }
    }

    private fun observeObservers() {
        viewModel.timeTicketSummaryMutableLiveData.observe(this) {
            with(binding) {
                if (it.isNullOrEmpty().not()) {
                    val sortedList = it.sortedByDescending {
                        it.sTimeTicketDate?.let { it1 ->
                            commonUtils.convertStringDateToDate(
                                it1,
                                Constants.SUMMARY_DATE_FORMAT_MM_DD_YYYY
                            )
                        }
                    }
                    val layoutManager = LinearLayoutManager(this@TimeTicketSummaryActivity)
                    summaryList.layoutManager = layoutManager
                    if (::summaryAdapter.isInitialized.not()) {
                        summaryAdapter =
                            SummaryAdapter(sortedList, commonUtils, ::onViewClick)
                        summaryList.adapter = summaryAdapter
                    } else {
                        summaryAdapter.updateAdapter(sortedList)
                    }
                    summaryList.visibility = View.VISIBLE
                } else {
                    summaryList.visibility = View.GONE
                    commonUtils.showToastMessage(
                        this@TimeTicketSummaryActivity,
                        this@TimeTicketSummaryActivity.resources.getString(R.string.no_record_found)
                    )
                }
            }
        }
        viewModel.deleteTicketMutableLiveData.observe(this) {
            it?.status?.let { status ->
                if (status == Constants.SUCCESS) {
                    commonUtils.showToastMessage(
                        this,
                        this.resources.getString(R.string.delete_success_message)
                    )
                    viewModel.loaderMutableLiveData.value = true
                    viewModel.getAuthToken(APIConstant.FIND_MATTER_TIME_TICKET_ID)
                }
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
        viewModel.loaderMutableLiveData.observe(this) {
            with(binding) {
                if (it) {
                    lytProgressParent.lytProgress.visibility = View.VISIBLE
                    lytProgressParent.lytProgress.setEnable(false)
                    lytProgressParent.imgProgress.setBackgroundResource(R.drawable.progress_frame_animation)
                    frameAnimation =
                        lytProgressParent.imgProgress.background as AnimationDrawable
                    commonUtils.enableDisableViews(lytSummaryParent, true)
                    frameAnimation.start()
                } else {
                    lytProgressParent.lytProgress.visibility = View.GONE
                    if (::frameAnimation.isInitialized && frameAnimation.isRunning) {
                        frameAnimation.stop()
                    }
                    commonUtils.enableDisableViews(lytSummaryParent, true)
                }
            }
        }
        viewModel.networkMutableLiveData.observe(this) {
            if (it) {
                commonUtils.showToastMessage(this, this.resources.getString(R.string.no_network))
            }
        }
        viewModel.notificationCountLiveData.observe(this) {
            it?.let { notificationCount ->
                preferenceHelper.setNotificationCount(notificationCount.overAllCount ?: 0)
                invalidateOptionsMenu()
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
        if (item.itemId == R.id.action_logout) {
            logout()
            return true
        }
        return false
    }

    private val activityResultLauncher: CustomResultLauncher<Intent, ActivityResult> =
        CustomResultLauncher.registerForActivityResult(
            this,
            ActivityResultContracts.StartActivityForResult()
        )

    private fun onViewClick(timeTicketSummary: TimeTicketSummaryResponse, actionId: Int) {
        viewModel.timeTicketNumber = timeTicketSummary.timeTicketNumber ?: ""
        when (actionId) {
            Constants.ACTION_EDIT -> {
                val intent = Intent(this@TimeTicketSummaryActivity, NewTicketActivity::class.java)
                intent.putExtra(Constants.TICKET_SUMMARY, timeTicketSummary as Serializable)
                intent.putExtra(Constants.TIME_TICKET_NUMBER, viewModel.timeTicketNumber)
                intent.putExtra(Constants.IS_FROM_EDIT, true)
                activityResultLauncher.launch(intent,
                    object : CustomResultLauncher.ActivityResultListener<ActivityResult> {
                        override fun onActivityResult(result: ActivityResult) {
                            if (result.resultCode == RESULT_OK) {
                                viewModel.loaderMutableLiveData.value = true
                                viewModel.getAuthToken(APIConstant.FIND_MATTER_TIME_TICKET_ID)
                            }
                        }
                    })
            }

            Constants.ACTION_COPY -> {
                val intent = Intent(this@TimeTicketSummaryActivity, NewTicketActivity::class.java)
                intent.putExtra(Constants.TICKET_SUMMARY, timeTicketSummary as Serializable)
                intent.putExtra(Constants.TIME_TICKET_NUMBER, viewModel.timeTicketNumber)
                intent.putExtra(Constants.IS_FROM_COPY, true)
                activityResultLauncher.launch(intent,
                    object : CustomResultLauncher.ActivityResultListener<ActivityResult> {
                        override fun onActivityResult(result: ActivityResult) {
                            if (result.resultCode == RESULT_OK) {
                                viewModel.loaderMutableLiveData.value = true
                                viewModel.getAuthToken(APIConstant.FIND_MATTER_TIME_TICKET_ID)
                            }
                        }
                    })
            }

            Constants.ACTION_DELETE -> {
                if (timeTicketSummary.statusId == Constants.STATUS_ID) {
                    deleteConfirmationDialog(this)
                } else {
                    commonUtils.showToastMessage(
                        this,
                        this.resources.getString(R.string.delete_error_message)
                    )
                }
            }
        }
    }

    private fun deleteConfirmationDialog(context: Context) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle(context.resources.getString(R.string.delete))
        builder.setMessage(context.resources.getString(R.string.delete_confirmation_message))

        builder.setPositiveButton(context.resources.getString(R.string.yes)) { dialogInterface, _ ->
            dialogInterface.dismiss()
            viewModel.loaderMutableLiveData.value = true
            viewModel.getAuthToken(APIConstant.DELETE_TICKET_ID)
        }

        builder.setNegativeButton(context.resources.getString(R.string.no)) { dialogInterface, _ ->
            dialogInterface.dismiss()
        }
        val alertDialog: AlertDialog = builder.create()
        alertDialog.setCancelable(false)
        alertDialog.show()
        alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE)
            .setTextColor(ContextCompat.getColor(context, R.color.color_devil_blue))
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE)
            .setTextColor(ContextCompat.getColor(context, R.color.color_devil_blue))
    }
}