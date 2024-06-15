package com.clara.timekeeping.ui.filter

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.recyclerview.widget.LinearLayoutManager
import com.clara.timekeeping.BaseActivity
import com.clara.timekeeping.R
import com.clara.timekeeping.databinding.ActivityFilterBinding
import com.clara.timekeeping.ui.filter.adapter.FilterAdapter
import com.clara.timekeeping.model.FilterData
import com.clara.timekeeping.model.SearchResult
import com.clara.timekeeping.ui.summary.TimeTicketSummaryActivity
import com.clara.timekeeping.utils.FilterHelperClass
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import javax.inject.Inject

@AndroidEntryPoint
class FilterActivity : BaseActivity() {
    lateinit var binding: ActivityFilterBinding
    lateinit var filterAdapter: FilterAdapter
    private lateinit var backPressedCallback: OnBackPressedCallback

    @Inject
    lateinit var gson: Gson
    private var isSingleSelection: Boolean = false
    private var searchList: MutableList<SearchResult> = mutableListOf()
    private var isFromSearch = ""
    private var isAllChecked = false

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFilterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setToolBar(binding.lytToolbar.toolbar)
        if (preferenceHelper.isTablet().not()) {
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }
        setListener()
        onBackPressedDispatcher.addCallback(this, backPressedCallback)
    }

    private fun setListener() {
        with(binding) {
            edtTxtFilter.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                    if (::filterAdapter.isInitialized) filterAdapter.filter.filter(s.toString())
                }

                override fun afterTextChanged(s: Editable) {}
            })

            checkboxAll.setOnClickListener {
                if (isAllChecked) {
                    for (searchItem in searchList) {
                        searchItem.isChecked = false
                    }
                    filterAdapter.updateAdapter(searchList)
                    isAllChecked = false
                    FilterHelperClass.clearSearch()
                } else {
                    for (searchItem in searchList) {
                        searchItem.isChecked = true
                        FilterHelperClass.addSelectedSearch(searchItem, isFromSearch)
                    }
                    filterAdapter.updateAdapter(searchList)
                    isAllChecked = true
                }
            }

            btnConfirm.setOnClickListener {
                commonUtils.hideKeyboard(this@FilterActivity, binding.edtTxtFilter)
                setResult(RESULT_OK)
                finish()
            }
            lytToolbar.imgHeaderLogo.setOnClickListener {
                startActivity(Intent(this@FilterActivity, TimeTicketSummaryActivity::class.java))
                finishAffinity()
            }
        }
        backPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                handingBackPress()
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
                handingBackPress()
                true
            }

            R.id.action_logout -> {
                logout()
                true
            }

            else -> false
        }
    }

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    override fun onStop() {
        super.onStop()
        EventBus.getDefault().unregister(this)
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    fun onEvent(filterData: FilterData) {
        EventBus.getDefault().removeStickyEvent(filterData)
        isSingleSelection = filterData.isSingleSelect
        isFromSearch = filterData.searchOption
        filterData.filterList?.let {
            searchList = it
            for (item in searchList) {
                val selectedList =
                    if (isSingleSelection) FilterHelperClass.singleSelectionSearchList else FilterHelperClass.selectedFilterList
                for (selectedSearchItem in selectedList) {
                    if (selectedSearchItem.containsKey(isFromSearch)) {
                        val itemList = selectedSearchItem[isFromSearch]
                        for (selectedItem in itemList!!) {
                            if (selectedItem.id == item.id) {
                                item.isChecked = true
                            }
                        }
                    }
                }
            }
            val layoutManager = LinearLayoutManager(this)
            binding.filterList.layoutManager = layoutManager
            filterAdapter = FilterAdapter(
                searchList,
                filterData.isSingleSelect,
                ::addOrRemoveItem,
                ::filteredList
            )
            binding.filterList.adapter = filterAdapter
        }
        binding.checkboxAll.visibility = if (isSingleSelection) View.GONE else View.VISIBLE
        binding.checkboxAll.isChecked = searchList.none { it.isChecked.not() }
    }

    private fun addOrRemoveItem(searchResult: SearchResult, isAddItem: Boolean) {
        when {
            isSingleSelection && isAddItem -> {
                FilterHelperClass.addSingleSearchOption(searchResult, isFromSearch)
            }

            isAddItem -> {
                FilterHelperClass.addSelectedSearch(searchResult, isFromSearch)
                binding.checkboxAll.isChecked = filterUnCheckedList()
                isAllChecked = filterUnCheckedList()
            }

            else -> {
                searchResult.id?.let {
                    FilterHelperClass.removeSearchSelection(
                        it,
                        isFromSearch,
                        FilterHelperClass.selectedFilterList
                    )
                }
                binding.checkboxAll.isChecked = filterUnCheckedList()
                isAllChecked = filterUnCheckedList()
            }
        }
    }

    private fun filteredList(filterList: MutableList<SearchResult>) {
        this.searchList = filterList
    }

    private fun filterUnCheckedList(): Boolean {
        return searchList.none { it.isChecked.not() }
    }

    private fun handingBackPress() {
        commonUtils.hideKeyboard(this, binding.edtTxtFilter)
        setResult(RESULT_OK)
        finish()
    }
}