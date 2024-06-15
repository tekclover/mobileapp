package com.clara.timekeeping.ui.search

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.ActivityInfo
import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import android.text.InputType
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.util.Pair
import com.clara.timekeeping.BaseActivity
import com.clara.timekeeping.R
import com.clara.timekeeping.databinding.ActivitySearchBinding
import com.clara.timekeeping.ui.filter.FilterActivity
import com.clara.timekeeping.model.FilterData
import com.clara.timekeeping.model.SearchResult
import com.clara.timekeeping.utils.Constants
import com.clara.timekeeping.utils.CustomResultLauncher
import com.clara.timekeeping.utils.FilterHelperClass
import com.clara.timekeeping.utils.setEnable
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import org.greenrobot.eventbus.EventBus
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import javax.inject.Inject


@AndroidEntryPoint
class SearchActivity : BaseActivity() {
    lateinit var binding: ActivitySearchBinding
    private val viewModel: SearchViewModel by viewModels()
    private lateinit var frameAnimation: AnimationDrawable
    private val statusBuilder = StringBuilder()
    private lateinit var backPressedCallback: OnBackPressedCallback

    @Inject
    lateinit var gson: Gson

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setToolBar(binding.lytToolbar.toolbar)
        if (preferenceHelper.isTablet().not()) {
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }
        setListener()
        observeObservers()
        setBillType()
        setSelectedSearchOptions()
        onBackPressedDispatcher.addCallback(this, backPressedCallback)
    }

    private fun setListener() {
        with(binding) {
            btnExecute.setOnClickListener {
                /*viewModel.getAuthToken(
                    APIConstant.SEARCH_EXECUTE_ID,
                    Constants.MANAGEMENT_SERVICE, ""
                )*/

                if (viewModel.startDate.isNotEmpty()) {
                    FilterHelperClass.removeDate(Constants.START_DATE)
                    FilterHelperClass.addSelectedSearch(
                        SearchResult(
                            viewModel.startDate,
                            viewModel.startDate
                        ), Constants.START_DATE
                    )
                }
                if (viewModel.endDate.isNotEmpty()) {
                    FilterHelperClass.removeDate(Constants.END_DATE)
                    FilterHelperClass.addSelectedSearch(
                        SearchResult(
                            viewModel.endDate,
                            viewModel.endDate
                        ), Constants.END_DATE
                    )
                }
                setResult(RESULT_OK)
                finish()
            }
            btnReset.setOnClickListener {
                edtTxtMatterId.text = null
                edtTxtDate.text = null
                edtTxtStatus.text = null
                edtTxtBillType.text = null
                viewModel.startDate = ""
                viewModel.endDate = ""
                FilterHelperClass.clearSearch()
            }
            inputLayoutDate.setEndIconOnClickListener {
                showDatePicker()
            }
            edtTxtDate.setOnClickListener {
                showDatePicker()
            }

            edtTxtMatterId.setOnClickListener {
                edtTxtMatterId.inputType = InputType.TYPE_NULL
                edtTxtMatterId.showSoftInputOnFocus = false
                try {
                    for (item in viewModel.matterNoList) {
                        item.isChecked = false
                    }
                    EventBus.getDefault()
                        .postSticky(FilterData(viewModel.matterNoList, false, Constants.MATTER_NO))
                    val intent = Intent(this@SearchActivity, FilterActivity::class.java)
                    activityResultLauncher.launch(intent,
                        object : CustomResultLauncher.ActivityResultListener<ActivityResult> {
                            override fun onActivityResult(result: ActivityResult) {
                                if (result.resultCode == RESULT_OK) {
                                    val matterNumberBuilder = StringBuilder()
                                    if (FilterHelperClass.selectedFilterList.size > 0) {
                                        for (searchMap in FilterHelperClass.selectedFilterList) {
                                            if (searchMap.containsKey(Constants.MATTER_NO)) {
                                                val itemList = searchMap[Constants.MATTER_NO]
                                                itemList?.forEach { searchResult ->
                                                    if (matterNumberBuilder.isNotEmpty()) {
                                                        matterNumberBuilder.append(",")
                                                            .append(searchResult.id)
                                                    } else {
                                                        matterNumberBuilder.append(searchResult.id)
                                                    }
                                                }
                                                break
                                            }
                                        }
                                        edtTxtMatterId.setText(matterNumberBuilder.toString())
                                    } else {
                                        edtTxtMatterId.text = null
                                    }
                                }
                            }
                        })
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            edtTxtStatus.setOnClickListener {
                edtTxtStatus.inputType = InputType.TYPE_NULL
                edtTxtStatus.showSoftInputOnFocus = false
                try {
                    for (item in viewModel.statusList) {
                        item.isChecked = false
                    }
                    EventBus.getDefault()
                        .postSticky(FilterData(viewModel.statusList, false, Constants.STATUS))
                    val intent = Intent(this@SearchActivity, FilterActivity::class.java)
                    activityResultLauncher.launch(intent,
                        object : CustomResultLauncher.ActivityResultListener<ActivityResult> {
                            override fun onActivityResult(result: ActivityResult) {
                                if (result.resultCode == RESULT_OK) {
                                    val matterNumberBuilder = StringBuilder()
                                    if (FilterHelperClass.selectedFilterList.size > 0) {
                                        for (searchMap in FilterHelperClass.selectedFilterList) {
                                            if (searchMap.containsKey(Constants.STATUS)) {
                                                val itemList = searchMap[Constants.STATUS]
                                                itemList?.forEach { searchResult ->
                                                    if (matterNumberBuilder.isNotEmpty()) {
                                                        matterNumberBuilder.append(",")
                                                            .append(searchResult.name)
                                                    } else {
                                                        matterNumberBuilder.append(searchResult.name)
                                                    }
                                                }
                                                break
                                            }
                                        }
                                        edtTxtStatus.setText(matterNumberBuilder.toString())
                                    } else {
                                        edtTxtStatus.text = null
                                    }
                                }
                            }
                        })
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            edtTxtBillType.setOnClickListener {
                edtTxtBillType.inputType = InputType.TYPE_NULL
                edtTxtBillType.showSoftInputOnFocus = false
                try {
                    for (item in viewModel.billTypeList) {
                        item.isChecked = false
                    }
                    EventBus.getDefault()
                        .postSticky(FilterData(viewModel.billTypeList, false, Constants.BILL_TYPE))
                    val intent = Intent(this@SearchActivity, FilterActivity::class.java)
                    activityResultLauncher.launch(intent,
                        object : CustomResultLauncher.ActivityResultListener<ActivityResult> {
                            override fun onActivityResult(result: ActivityResult) {
                                if (result.resultCode == RESULT_OK) {
                                    val matterNumberBuilder = StringBuilder()
                                    if (FilterHelperClass.selectedFilterList.size > 0) {
                                        for (searchMap in FilterHelperClass.selectedFilterList) {
                                            if (searchMap.containsKey(Constants.BILL_TYPE)) {
                                                val itemList = searchMap[Constants.BILL_TYPE]
                                                itemList?.forEach { searchResult ->
                                                    if (matterNumberBuilder.isNotEmpty()) {
                                                        matterNumberBuilder.append(",")
                                                            .append(searchResult.id)
                                                    } else {
                                                        matterNumberBuilder.append(searchResult.id)
                                                    }
                                                }
                                                break
                                            }
                                        }
                                        edtTxtBillType.setText(matterNumberBuilder.toString())
                                    } else {
                                        edtTxtBillType.text = null
                                    }
                                }
                            }
                        })
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            lytToolbar.imgHeaderLogo.setOnClickListener {
                backPressed()
            }
        }

        backPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                backPressed()
            }
        }
    }

    private fun observeObservers() {
        viewModel.matterIdMutableLiveDat.observe(this) {
            it.matterDropDownList?.let { matterIdList ->
                for (matter in matterIdList) {
                    viewModel.matterNoList.add(
                        SearchResult(
                            matter.matterNumber,
                            matter.matterNumber + " - " + matter.matterDescription
                        )
                    )
                }
            }
        }
        viewModel.timekeeperCodeMutableLiveDat.observe(this) { it ->
            it?.let { list ->
                val filterList = list.filter { it.timekeeperCode == preferenceHelper.getUserId() }
                filterList.firstOrNull().let { timeKeeper ->
                    binding.edtTxtTimeKeeperCode.setText(
                        String.format(
                            Locale.getDefault(),
                            "%s - %s",
                            (timeKeeper?.timekeeperCode ?: ""),
                            (timeKeeper?.timekeeperName ?: "")
                        )
                    )
                    FilterHelperClass.addSelectedSearch(
                        SearchResult(
                            timeKeeper?.timekeeperCode ?: "",
                            ((timeKeeper?.timekeeperCode
                                ?: "") + " - " + (timeKeeper?.timekeeperName ?: ""))
                        ),
                        Constants.TIMEKEEPER_CODE
                    )
                }
            }
        }
        viewModel.statusMutableLiveDat.observe(this) {
            it?.let { list ->
                if (statusBuilder.isEmpty()) {
                    val filterList = list.filter { it1 -> it1.statusId == Constants.STATUS_ID }
                    filterList.firstOrNull()?.let { status ->
                        binding.edtTxtStatus.setText(status.statusDesc ?: "")
                        FilterHelperClass.addSelectedSearch(
                            SearchResult(
                                status.statusId.toString(),
                                status.statusDesc
                            ), Constants.STATUS
                        )
                    }
                }
                for (status in list) {
                    viewModel.statusList.add(
                        SearchResult(
                            status.statusId?.toString(),
                            status.statusDesc
                        )
                    )
                }
            }
        }

        viewModel.searchExecuteMutableLiveDat.observe(this) {
            if (it.isNullOrEmpty()) {
                commonUtils.showToastMessage(
                    this,
                    this.resources.getString(R.string.search_empty_result_message)
                )
            } else {
                if (viewModel.startDate.isNotEmpty()) {
                    FilterHelperClass.removeDate(Constants.START_DATE)
                    FilterHelperClass.addSelectedSearch(
                        SearchResult(
                            viewModel.startDate,
                            viewModel.startDate
                        ), Constants.START_DATE
                    )
                }
                if (viewModel.endDate.isNotEmpty()) {
                    FilterHelperClass.removeDate(Constants.END_DATE)
                    FilterHelperClass.addSelectedSearch(
                        SearchResult(
                            viewModel.endDate,
                            viewModel.endDate
                        ), Constants.END_DATE
                    )
                }
                setResult(RESULT_OK)
                finish()
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
                    commonUtils.enableDisableViews(lytSearchParent, false)
                    frameAnimation.start()
                } else {
                    lytProgressParent.lytProgress.visibility = View.GONE
                    stopAnimation()
                    commonUtils.enableDisableViews(lytSearchParent, true)
                }
            }
        }
        viewModel.networkMutableLiveDat.observe(this) {
            if (it) {
                commonUtils.showToastMessage(this, this.resources.getString(R.string.no_network))
            }
        }
    }

    private fun stopAnimation() {
        if (::frameAnimation.isInitialized && frameAnimation.isRunning) {
            frameAnimation.stop()
        }
    }

    private fun setBillType() {
        val billTypeList = resources?.getStringArray(R.array.bill_type)?.asList()
        billTypeList?.forEach {
            viewModel.billTypeList.add(SearchResult(it, it))
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
                backPressed()
                true
            }

            R.id.action_logout -> {
                stopAnimation()
                logout()
                true
            }

            else -> false
        }
    }

    private val activityResultLauncher: CustomResultLauncher<Intent, ActivityResult> =
        CustomResultLauncher.registerForActivityResult(
            this,
            ActivityResultContracts.StartActivityForResult()
        )

    private fun showDatePicker() {
        val builder = MaterialDatePicker.Builder.dateRangePicker().setTheme(R.style.DatePickerStyle)
            .setTitleText(this.resources.getString(R.string.select_dates))
        val datePicker = builder.build()
        datePicker.addOnPositiveButtonClickListener { selection: Pair<Long, Long> ->
            val startDate = selection.first
            val endDate = selection.second
            val backendDateFormat =
                SimpleDateFormat(Constants.MM_DD_YYYY_HH_MM_SS, Locale.getDefault()).apply {
                    timeZone = TimeZone.getTimeZone("UTC")
                }
            viewModel.startDate = backendDateFormat.format(Date(startDate))
            viewModel.endDate = backendDateFormat.format(Date(endDate))
            val displayDateFormat =
                SimpleDateFormat(Constants.MM_DD_YYYY, Locale.getDefault()).apply {
                    timeZone = TimeZone.getTimeZone("UTC")
                }
            val startDateDisplay = displayDateFormat.format(Date(startDate))
            val endDateDisplay = displayDateFormat.format(Date(endDate))
            val selectedDateRange = "$startDateDisplay - $endDateDisplay"
            binding.edtTxtDate.setText(selectedDateRange)
        }
        datePicker.show(supportFragmentManager, TAG_DATE_PICKER)
    }

    companion object {
        const val TAG_DATE_PICKER = "DATE_PICKER"
    }

    private fun backPressed() {
        stopAnimation()
        setResult(RESULT_OK)
        finish()
    }

    private fun setSelectedSearchOptions() {
        val matterNumberBuilder = StringBuilder()
        val billTypeBuilder = StringBuilder()
        if (FilterHelperClass.selectedFilterList.size > 0) {
            for (searchMap in FilterHelperClass.selectedFilterList) {
                when {
                    searchMap.containsKey(Constants.MATTER_NO) -> {
                        val itemList = searchMap[Constants.MATTER_NO]
                        itemList?.forEach { searchResult ->
                            if (matterNumberBuilder.isNotEmpty()) {
                                matterNumberBuilder.append(",").append(searchResult.id)
                            } else {
                                matterNumberBuilder.append(searchResult.id)
                            }
                        }
                    }

                    searchMap.containsKey(Constants.BILL_TYPE) -> {
                        val itemList = searchMap[Constants.BILL_TYPE]
                        itemList?.forEach { searchResult ->
                            if (billTypeBuilder.isNotEmpty()) {
                                billTypeBuilder.append(",").append(searchResult.id)
                            } else {
                                billTypeBuilder.append(searchResult.id)
                            }
                        }
                    }

                    searchMap.containsKey(Constants.STATUS) -> {
                        val itemList = searchMap[Constants.STATUS]
                        itemList?.forEach { searchResult ->
                            if (statusBuilder.isNotEmpty()) {
                                statusBuilder.append(",").append(searchResult.name)
                            } else {
                                statusBuilder.append(searchResult.name)
                            }
                        }
                    }

                    searchMap.containsKey(Constants.TIMEKEEPER_CODE) -> {
                        val itemList = searchMap[Constants.TIMEKEEPER_CODE]
                        itemList?.forEach { searchResult ->
                            viewModel.timekeeperCode = searchResult.name ?: ""
                        }
                    }

                    searchMap.containsKey(Constants.START_DATE) -> {
                        val itemList = searchMap[Constants.START_DATE]
                        itemList?.forEach { searchResult ->
                            viewModel.startDate = searchResult.id ?: ""
                        }
                    }

                    searchMap.containsKey(Constants.END_DATE) -> {
                        val itemList = searchMap[Constants.END_DATE]
                        itemList?.forEach { searchResult ->
                            viewModel.endDate = searchResult.id ?: ""
                        }
                    }
                }
            }
        }
        with(binding) {
            edtTxtMatterId.setText(matterNumberBuilder.toString())
            edtTxtBillType.setText(billTypeBuilder.toString())
            edtTxtStatus.setText(statusBuilder.toString())
            edtTxtTimeKeeperCode.setText(viewModel.timekeeperCode)
            if (viewModel.startDate.isNotEmpty() && viewModel.endDate.isNotEmpty()) {
                edtTxtDate.setText(
                    String.format(
                        Locale.getDefault(),
                        "%s - %s",
                        commonUtils.formatDate(
                            viewModel.startDate,
                            Constants.MM_DD_YYYY_HH_MM_SS,
                            Constants.MM_DD_YYYY
                        ),
                        commonUtils.formatDate(
                            viewModel.endDate,
                            Constants.MM_DD_YYYY_HH_MM_SS,
                            Constants.MM_DD_YYYY
                        )
                    )
                )
            }
        }
    }
}