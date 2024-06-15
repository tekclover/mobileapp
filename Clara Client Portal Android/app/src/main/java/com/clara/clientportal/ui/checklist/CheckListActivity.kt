package com.clara.clientportal.ui.checklist

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
import com.clara.clientportal.databinding.ActivityCheckListBinding
import com.clara.clientportal.model.CheckListResponse
import com.clara.clientportal.ui.checklist.adapter.CheckListAdapter
import com.clara.clientportal.utils.Constants
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CheckListActivity : BaseActivity() {
    private lateinit var binding: ActivityCheckListBinding
    private val viewModel: CheckListViewModel by viewModels()
    private lateinit var frameAnimation: AnimationDrawable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCheckListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setToolBar(binding.lytToolbar.toolbar)
        setObserver()
    }

    private fun setObserver() {
        viewModel.checkListMutableLiveData.observe(this) {
            if (it.isNullOrEmpty().not()) {
                val linearLayoutManager = LinearLayoutManager(this)
                binding.checkList.layoutManager = linearLayoutManager
                binding.checkList.addItemDecoration(
                    DividerItemDecoration(
                        this,
                        DividerItemDecoration.VERTICAL
                    )
                )
              /*  val sortedList = it.sortedByDescending { date ->
                    date.updatedOn?.let { it1 ->
                        commonUtils.formatDate(Constants.YYYY_MM_DD_T_HH_MM_SS_SSS_Z).parse(
                            it1
                        )
                    }
                }*/
                val sortedList = it.sortedByDescending { it1 ->
                    it1.matterNumber
                }
                val checkListAdapter =
                    CheckListAdapter(sortedList, commonUtils, preferenceHelper, ::onViewClick)
                binding.checkList.adapter = checkListAdapter
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

    private fun onViewClick(checkListResponse: CheckListResponse) {
        if (checkListResponse.checkListNo != null && checkListResponse.matterHeaderId != null && checkListResponse.matterNumber.isNullOrEmpty()
                .not()
        ) {
            val intent = Intent(this, CheckListViewDetailsActivity::class.java)
            intent.putExtra(Constants.CHECK_LIST_NO, checkListResponse.checkListNo ?: -1)
            intent.putExtra(Constants.MATTER_HEADER_ID, checkListResponse.matterHeaderId ?: -1)
            intent.putExtra(Constants.MATTER_NO, checkListResponse.matterNumber ?: "")
            startActivity(intent)
        } else {
            commonUtils.showToastMessage(
                this,
                this.resources.getString(R.string.no_document_to_display)
            )
        }
    }

    private fun setHeaders() {
        if (preferenceHelper.isTablet()) {
            with(binding) {
                lytHeaderView?.textTitle1?.text =
                    this@CheckListActivity.resources.getString(R.string.matter_no)
                lytHeaderView?.textTitle2?.text =
                    this@CheckListActivity.resources.getString(R.string.date)
                lytHeaderView?.textTitle3?.text =
                    this@CheckListActivity.resources.getString(R.string.status)
                lytHeaderView?.textTitle4?.text =
                    this@CheckListActivity.resources.getString(R.string.details)
                lytHeaderView?.lytHeader4Parent?.visibility = View.VISIBLE
            }
        } else {
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }
    }
}