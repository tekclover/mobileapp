package com.clara.clientportal.ui.matter

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
import com.clara.clientportal.databinding.ActivityMatterBinding
import com.clara.clientportal.ui.matter.adapter.MatterAdapter
import com.clara.clientportal.utils.Constants
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class  MatterActivity : BaseActivity() {
    private lateinit var binding: ActivityMatterBinding
    private val viewModel: MatterViewModel by viewModels()
    private lateinit var frameAnimation: AnimationDrawable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMatterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setToolBar(binding.lytToolbar.toolbar)
        setObserver()
    }

    private fun setObserver() {
        viewModel.matterMutableLiveData.observe(this) {
            if (it.isNullOrEmpty().not()) {
                val linearLayoutManager = LinearLayoutManager(this)
                binding.lytMatter.matterList.layoutManager = linearLayoutManager
                binding.lytMatter.matterList.addItemDecoration(
                    DividerItemDecoration(
                        this,
                        DividerItemDecoration.VERTICAL
                    )
                )
               /* val sortedList = it.sortedByDescending { date ->
                    date.caseOpenedDate?.let { it1 ->
                        commonUtils.formatDate(Constants.MM_DD_YYYY).parse(
                            it1
                        )
                    }
                }*/
                val sortedList = it.sortedByDescending { it1 ->
                    it1.matterNumber
                }
                val matterAdapter = MatterAdapter(
                    sortedList, commonUtils,
                    preferenceHelper.isTablet(), ::onViewClick
                )
                binding.lytMatter.matterList.adapter = matterAdapter
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

    fun onViewClick(matterNo: String) {
        val viewMatterFragment = FragmentMatterDetails()
        val bundle = Bundle()
        bundle.putString(Constants.MATTER_NO, matterNo)
        viewMatterFragment.arguments = bundle
        viewMatterFragment.show(supportFragmentManager, "matter_popup_fragment")
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
                lytMatter.lytHeaderView?.textTitle1?.text =
                    this@MatterActivity.resources.getString(R.string.matter_no)
                lytMatter.lytHeaderView?.textTitle2?.text =
                    this@MatterActivity.resources.getString(R.string.date)
                lytMatter.lytHeaderView?.textTitle3?.text =
                    this@MatterActivity.resources.getString(R.string.status)
                lytMatter.lytHeaderView?.textTitle4?.text =
                    this@MatterActivity.resources.getString(R.string.details)
                binding.lytMatter.lytHeaderView?.lytHeader4Parent?.visibility = View.VISIBLE
            }
        } else {
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }
    }
}