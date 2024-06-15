package com.clara.timekeeping.ui.newticket

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.ActivityInfo
import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.clara.timekeeping.BaseActivity
import com.clara.timekeeping.R
import com.clara.timekeeping.databinding.ActivityNewTicketBinding
import com.clara.timekeeping.ui.filter.FilterActivity
import com.clara.timekeeping.model.FilterData
import com.clara.timekeeping.model.LoginResponse
import com.clara.timekeeping.model.NewTicketResponse
import com.clara.timekeeping.model.SearchResult
import com.clara.timekeeping.model.TicketRequest
import com.clara.timekeeping.network.APIConstant
import com.clara.timekeeping.ui.search.SearchActivity
import com.clara.timekeeping.utils.Constants
import com.clara.timekeeping.utils.Constants.TIMER_INTERVAL
import com.clara.timekeeping.utils.CustomResultLauncher
import com.clara.timekeeping.utils.FilterHelperClass
import com.clara.timekeeping.utils.setEnable
import com.clara.timekeeping.utils.toDollar
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointBackward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import org.greenrobot.eventbus.EventBus
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@AndroidEntryPoint
class NewTicketActivity : BaseActivity() {
    lateinit var binding: ActivityNewTicketBinding
    private val viewModel: NewTicketViewModel by viewModels()
    private lateinit var frameAnimation: AnimationDrawable
    private lateinit var backPressedCallback: OnBackPressedCallback

    @Inject
    lateinit var gson: Gson
    private val mInterval = TIMER_INTERVAL
    private var mHandler: Handler? = null
    private var timeInSeconds = 0L
    private var bookedHoursIncrementValue: Long = 6L
    private var bookedHours = 0.1
    private var startButtonClicked = false

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewTicketBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setToolBar(binding.lytToolbar.toolbar)
        if (preferenceHelper.isTablet().not()) {
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }
        FilterHelperClass.clearSingleSelectionSearch()
        initIntentValue()
        setBillType()
        setListener()
        observeObservers()
        onBackPressedDispatcher.addCallback(this, backPressedCallback)
        binding.textTitle.text = when {
            viewModel.isFromEdit -> this.resources.getString(R.string.time_ticket_edit)
            viewModel.isFromCopy -> this.resources.getString(R.string.time_ticket_copy)
            else -> this.resources.getString(R.string.time_ticket_new)
        }
        binding.edtTxtAssignedRateHour.setText(viewModel.assignedRatePerHour.toDollar())
        binding.edtTxtDefaultRateHour.setText(viewModel.defaultRatePerHour.toDollar())
        binding.edtTxtTimeTicketAmount.setText(viewModel.timeTicketAmount.toDollar())
    }

    private fun initIntentValue() {
        intent?.extras?.let {
            viewModel.isFromEdit = it.getBoolean(Constants.IS_FROM_EDIT, false) == true
            viewModel.isFromCopy = it.getBoolean(Constants.IS_FROM_COPY, false) == true
            viewModel.timeTicketNumber = it.getString(Constants.TIME_TICKET_NUMBER, "")
        }
        when {
            viewModel.isFromEdit || viewModel.isFromCopy -> {
                viewModel.getAuthToken(
                    APIConstant.TIME_TICKET_DETAILS_ID,
                    Constants.MANAGEMENT_SERVICE,
                    ""
                )
            }

            else -> {
                viewModel.getAuthToken(
                    APIConstant.MATTER_NUMBER_CHECKED_ID,
                    Constants.MANAGEMENT_SERVICE,
                    APIConstant.DROP_DOWN_MATTER_ID
                )
                setCurrentDate()
            }
        }
    }

    private fun setTicketDetails(ticketDetails: NewTicketResponse) {
        ticketDetails.let {
            with(binding) {
                viewModel.timeTicketNumber = it.timeTicketNumber ?: ""
                viewModel.matterNumber = it.matterNumber ?: ""
                viewModel.clientId = it.clientId ?: ""
                viewModel.timekeeperCode = it.timeKeeperCode ?: ""
                viewModel.timeTicketAmount = it.timeTicketAmount ?: 0.00
                viewModel.assignedRatePerHour = it.assignedRatePerHourNew ?: 0.00
                viewModel.defaultRatePerHour = it.defaultRatePerHourNew ?: 0.00
                viewModel.taskBasedCode = it.taskCode ?: ""
                viewModel.activityCode = it.activityCode ?: ""
                viewModel.selectedDate = it.sTimeTicketDate ?: ""
                viewModel.classIdEdit = it.classId ?: -1
                edtTxtMatterId.setText(it.matterIdDesc ?: "")
                edtTxtTaskBasedCode.setText(viewModel.taskBasedCode)
                edtTxtActivityCode.setText(viewModel.activityCode)
                edtTxtClientName.setText(it.clientName ?: "")
                edtTxtTimeKeeperCode.setText(viewModel.timekeeperCode)
                edtTxtTimer.setText("")
                edtTxtBookedHours.setText(
                    String.format(
                        Locale.getDefault(),
                        "%.1f",
                        it.timeTicketHours ?: 0.0
                    )
                )
                edtTxtBillType.setText(it.billType ?: "")
                edtTxtDate.setText(viewModel.selectedDate)
                edtTxtAssignedRateHour.setText(viewModel.assignedRatePerHour.toDollar())
                edtTxtDefaultRateHour.setText(viewModel.defaultRatePerHour.toDollar())
                edtTxtTimeTicketAmount.setText(viewModel.timeTicketAmount.toDollar())
                edtTxtDescription.setText(it.timeTicketDescription ?: "")

                textActivityCodeLabel.visibility =
                    if (it.activityCode.isNullOrEmpty()) View.GONE else View.VISIBLE
                inputLayoutActivityCode.visibility =
                    if (it.activityCode.isNullOrEmpty()) View.GONE else View.VISIBLE
                edtTxtActivityCode.visibility =
                    if (it.activityCode.isNullOrEmpty()) View.GONE else View.VISIBLE
                textTaskBasedCodeLabel.visibility =
                    if (it.taskCode.isNullOrEmpty()) View.GONE else View.VISIBLE
                inputLayoutTaskBasedCode.visibility =
                    if (it.taskCode.isNullOrEmpty()) View.GONE else View.VISIBLE
                edtTxtTaskBasedCode.visibility =
                    if (it.taskCode.isNullOrEmpty()) View.GONE else View.VISIBLE


                FilterHelperClass.addSingleSearchOption(
                    SearchResult(
                        it.matterNumber,
                        it.matterIdDesc
                    ), Constants.MATTER_NO
                )
                FilterHelperClass.addSingleSearchOption(
                    SearchResult(it.taskCode, it.taskCode),
                    Constants.TASK_BASED_CODE
                )
                FilterHelperClass.addSingleSearchOption(
                    SearchResult(
                        it.activityCode,
                        it.activityCode
                    ), Constants.ACTIVITY_CODE
                )
                FilterHelperClass.addSingleSearchOption(
                    SearchResult(it.billType, it.billType),
                    Constants.BILL_TYPE
                )
            }

            when {
                it.activityCode.isNullOrEmpty().not() || it.taskCode.isNullOrEmpty()
                    .not() -> {
                    viewModel.getAuthToken(
                        APIConstant.TASK_BASED_CODE_ID,
                        Constants.SETUP_SERVICE,
                        ""
                    )
                }

                viewModel.isFromCopy -> {
                    viewModel.getAuthToken(
                        APIConstant.MATTER_NUMBER_CHECKED_ID,
                        Constants.MANAGEMENT_SERVICE,
                        APIConstant.DROP_DOWN_MATTER_ID
                    )
                }

                else -> {
                    viewModel.loaderMutableLiveData.value = false
                }
            }
        }
    }

    private fun setListener() {
        with(binding) {
            btnTimerStart.setOnClickListener {
                if (edtTxtBillType.text.toString().isNotEmpty()) {
                    if (!startButtonClicked) {
                        startTimer()
                        startTimerView()
                    } else {
                        stopTimer()
                        stopTimerView()
                    }
                } else {
                    commonUtils.showToastMessage(
                        this@NewTicketActivity,
                        this@NewTicketActivity.resources.getString(R.string.bill_type_empty_message)
                    )
                }
            }
            btnTimerReset.setOnClickListener {
                stopTimer()
                resetTimerView()
            }
            edtTxtDate.setOnClickListener {
                showDatePicker()
            }
            inputLayoutDate.setEndIconOnClickListener {
                showDatePicker()
            }
            allMattersCheckbox.setOnCheckedChangeListener { _, isChecked ->
                edtTxtMatterId.text = null
                edtTxtActivityCode.text = null
                edtTxtTaskBasedCode.text = null
                edtTxtClientName.text = null
               // edtTxtAssignedRateHour.text = null
                edtTxtTimer.text = null
                edtTxtBookedHours.text = null
               // edtTxtTimeTicketAmount.text = null
                binding.edtTxtAssignedRateHour.setText(viewModel.assignedRatePerHour.toDollar())
                binding.edtTxtTimeTicketAmount.setText(viewModel.timeTicketAmount.toDollar())
                FilterHelperClass.clearSingleSelectionSearch()
                stopTimer()
                resetTimerView()
                viewModel.allMatters = isChecked
                if (isChecked) {
                    viewModel.getAuthToken(
                        APIConstant.MATTER_NUMBER_CHECKED_ID,
                        Constants.MANAGEMENT_SERVICE,
                        APIConstant.DROP_DOWN_MATTER_ID
                    )
                } else {
                    viewModel.getAuthToken(
                        APIConstant.MATTER_NUMBER_UNCHECKED_ID,
                        Constants.MANAGEMENT_SERVICE,
                        APIConstant.DROP_DOWN_MATTER_ID_OPEN
                    )
                }
            }
            edtTxtMatterId.setOnClickListener {
                edtTxtMatterId.inputType = InputType.TYPE_NULL
                edtTxtMatterId.showSoftInputOnFocus = false
                try {
                    for (item in viewModel.matterNoList) {
                        item.isChecked = false
                    }
                    EventBus.getDefault()
                        .postSticky(FilterData(viewModel.matterNoList, true, Constants.MATTER_NO))
                    val intent = Intent(this@NewTicketActivity, FilterActivity::class.java)
                    activityResultLauncher.launch(intent,
                        object : CustomResultLauncher.ActivityResultListener<ActivityResult> {
                            override fun onActivityResult(result: ActivityResult) {
                                if (result.resultCode == RESULT_OK) {
                                    if (FilterHelperClass.singleSelectionSearchList.size > 0) {
                                        for (searchMap in FilterHelperClass.singleSelectionSearchList) {
                                            if (searchMap.containsKey(Constants.MATTER_NO)) {
                                                val itemList = searchMap[Constants.MATTER_NO]
                                                itemList?.firstOrNull()?.let { searchResult ->
                                                    edtTxtMatterId.setText(searchResult.name)
                                                    val filterList =
                                                        viewModel.matterIdMutableLiveData.value?.matterDropDownList?.filter { it.matterNumber == searchResult.id }
                                                    filterList?.firstOrNull()?.let {
                                                        edtTxtClientName.setText(
                                                            it.clientName ?: ""
                                                        )
                                                        viewModel.clientId = it.clientId ?: ""
                                                        viewModel.matterNumber =
                                                            it.matterNumber ?: ""
                                                    }
                                                    viewModel.getAuthToken(
                                                        APIConstant.MATTER_DETAILS_ID,
                                                        Constants.MANAGEMENT_SERVICE,
                                                        ""
                                                    )
                                                }
                                            }
                                        }
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
            edtTxtActivityCode.setOnClickListener {
                edtTxtActivityCode.inputType = InputType.TYPE_NULL
                edtTxtActivityCode.showSoftInputOnFocus = false
                try {
                    for (item in viewModel.activityCodeList) {
                        item.isChecked = false
                    }
                    EventBus.getDefault().postSticky(
                        FilterData(
                            viewModel.activityCodeList,
                            true,
                            Constants.ACTIVITY_CODE
                        )
                    )
                    val intent = Intent(this@NewTicketActivity, FilterActivity::class.java)
                    activityResultLauncher.launch(intent,
                        object : CustomResultLauncher.ActivityResultListener<ActivityResult> {
                            override fun onActivityResult(result: ActivityResult) {
                                if (result.resultCode == RESULT_OK) {
                                    if (FilterHelperClass.singleSelectionSearchList.size > 0) {
                                        for (searchMap in FilterHelperClass.singleSelectionSearchList) {
                                            if (searchMap.containsKey(Constants.ACTIVITY_CODE)) {
                                                val itemList = searchMap[Constants.ACTIVITY_CODE]
                                                itemList?.firstOrNull()?.let { searchResult ->
                                                    edtTxtActivityCode.setText(searchResult.name)
                                                    viewModel.activityCode = searchResult.id ?: ""
                                                }
                                            }
                                        }
                                    } else {
                                        edtTxtActivityCode.text = null
                                    }
                                }

                            }
                        })
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            edtTxtTaskBasedCode.setOnClickListener {
                edtTxtTaskBasedCode.inputType = InputType.TYPE_NULL
                edtTxtTaskBasedCode.showSoftInputOnFocus = false
                try {
                    for (item in viewModel.taskBasedCodeList) {
                        item.isChecked = false
                    }
                    EventBus.getDefault().postSticky(
                        FilterData(
                            viewModel.taskBasedCodeList,
                            true,
                            Constants.TASK_BASED_CODE
                        )
                    )
                    val intent = Intent(this@NewTicketActivity, FilterActivity::class.java)
                    activityResultLauncher.launch(intent,
                        object : CustomResultLauncher.ActivityResultListener<ActivityResult> {
                            override fun onActivityResult(result: ActivityResult) {
                                if (result.resultCode == RESULT_OK) {
                                    if (FilterHelperClass.singleSelectionSearchList.size > 0) {
                                        for (searchMap in FilterHelperClass.singleSelectionSearchList) {
                                            if (searchMap.containsKey(Constants.TASK_BASED_CODE)) {
                                                val itemList = searchMap[Constants.TASK_BASED_CODE]
                                                itemList?.firstOrNull()?.let { searchResult ->
                                                    edtTxtTaskBasedCode.setText(searchResult.name)
                                                    viewModel.taskBasedCode = searchResult.id ?: ""
                                                }
                                            }
                                        }
                                    } else {
                                        edtTxtTaskBasedCode.text = null
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
                        .postSticky(FilterData(viewModel.billTypeList, true, Constants.BILL_TYPE))
                    val intent = Intent(this@NewTicketActivity, FilterActivity::class.java)
                    activityResultLauncher.launch(intent,
                        object : CustomResultLauncher.ActivityResultListener<ActivityResult> {
                            override fun onActivityResult(result: ActivityResult) {
                                if (result.resultCode == RESULT_OK) {
                                    if (FilterHelperClass.singleSelectionSearchList.size > 0) {
                                        for (searchMap in FilterHelperClass.singleSelectionSearchList) {
                                            if (searchMap.containsKey(Constants.BILL_TYPE)) {
                                                val itemList = searchMap[Constants.BILL_TYPE]
                                                itemList?.firstOrNull()?.let { searchResult ->
                                                    edtTxtBillType.setText(searchResult.id)
                                                }
                                            }
                                        }
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
            edtTxtBillType.addTextChangedListener(billTypeWatcher())
            edtTxtBookedHours.addTextChangedListener(bookedHourWatcher())
            btnCancel.setOnClickListener {
                handlingBackPress()
            }
            btnSave.setOnClickListener {
                if (isValid()) {
                    val request = TicketRequest()
                    request.allMatters = viewModel.allMatters
                    request.matterNumber = viewModel.matterNumber
                    request.activityCode = viewModel.activityCode
                    request.taskCode = viewModel.taskBasedCode
                    request.clientId = viewModel.clientId
                    request.clientName = edtTxtClientName.text.toString()
                    request.timeKeeperCode = viewModel.timekeeperCode
                    request.timer = edtTxtTimer.text.toString()
                    request.timeTicketHours = edtTxtBookedHours.text.toString()
                    request.billType = edtTxtBillType.text.toString()
                    request.sTimeTicketDate = viewModel.selectedDate
                    request.defaultRatePerHour = viewModel.defaultRatePerHour.toString()
                    request.defaultRate = viewModel.assignedRatePerHour.toString()
                    request.timeTicketAmount =
                        String.format(Locale.getDefault(), "%.2f", viewModel.timeTicketAmount)
                    request.timeTicketDescription = edtTxtDescription.text.toString()
                    request.classId = getClassId()
                    request.languageId = Constants.LANGUAGE_ID
                    request.statusId = Constants.STATUS_ID
                    viewModel.timeTicketRequest = request
                    viewModel.getAuthToken(
                        if (viewModel.isFromEdit) APIConstant.EDIT_TICKET_ID else APIConstant.NEW_TIME_TICKET_ID,
                        Constants.MANAGEMENT_SERVICE,
                        ""
                    )
                }
            }
            lytToolbar.imgHeaderLogo.setOnClickListener {
                handlingBackPress()
            }
        }

        backPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                handlingBackPress()
            }
        }
    }

    private fun setBillType() {
        binding.textBillTypeLabel.text = String.format(
            Locale.getDefault(),
            "%s%s",
            this.resources.getString(R.string.bill_type),
            "*"
        )
        val billTypeList = resources?.getStringArray(R.array.bill_type)?.asList()
        billTypeList?.forEach {
            viewModel.billTypeList.add(SearchResult(it, it))
        }
        viewModel.billTypeList.firstOrNull()?.let {
            binding.edtTxtBillType.setText(it.name ?: "")
            FilterHelperClass.addSingleSearchOption(
                SearchResult(it.name, it.name),
                Constants.BILL_TYPE
            )
        }
    }

    private fun observeObservers() {
        viewModel.matterIdMutableLiveData.observe(this) {
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
        viewModel.timekeeperCodeMutableLiveData.observe(this) { it ->
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
                    viewModel.defaultRatePerHour = timeKeeper?.defaultRate?.toDouble() ?: 0.00
                    binding.edtTxtDefaultRateHour.setText(viewModel.defaultRatePerHour.toDollar())
                    viewModel.timekeeperCode = timeKeeper?.timekeeperCode ?: ""
                }
                for (timekeeper in filterList) {
                    timekeeper.timekeeperCode?.let { it1 -> viewModel.selectedTimekeeperList.add(it1) }
                }
            }
        }
        viewModel.activityCodeMutableLiveData.observe(this) {
            it?.let { list ->
                if (viewModel.isFromEdit || viewModel.isFromCopy) {
                    val filterList =
                        list.filter { it1 -> it1.activityCode == viewModel.activityCode }
                    filterList.firstOrNull()?.let { activityCode ->
                        binding.edtTxtActivityCode.setText(
                            String.format(
                                Locale.getDefault(),
                                "%s - %s",
                                activityCode.activityCode,
                                activityCode.activityCodeDescription
                            )
                        )
                    }
                }
                for (activityCode in list) {
                    viewModel.activityCodeList.add(
                        SearchResult(
                            activityCode.activityCode,
                            activityCode.activityCode + " - " + activityCode.activityCodeDescription
                        )
                    )
                }
            }
        }
        viewModel.taskBasedCodeMutableLiveData.observe(this) {
            it?.let { list ->
                if (viewModel.isFromEdit || viewModel.isFromCopy) {
                    val filterList =
                        list.filter { it1 -> it1.taskCode == viewModel.taskBasedCode }
                    filterList.firstOrNull()?.let { taskBasedCode ->
                        binding.edtTxtTaskBasedCode.setText(
                            String.format(
                                Locale.getDefault(),
                                "%s - %s",
                                taskBasedCode.taskCode,
                                taskBasedCode.taskcodeDescription
                            )
                        )
                    }
                }
                for (taskBasedCode in list) {
                    viewModel.taskBasedCodeList.add(
                        SearchResult(
                            taskBasedCode.taskCode,
                            taskBasedCode.taskCode + " - " + taskBasedCode.taskcodeDescription
                        )
                    )
                }
            }
        }

        viewModel.matterDetailsMutableLiveData.observe(this) {
            it?.let { matterDetails ->
                if (matterDetails.billingFormatId == Constants.BILLING_FORMAT_ID_10 || matterDetails.billingFormatId == Constants.BILLING_FORMAT_ID_12) {
                    binding.textActivityCodeLabel.visibility = View.VISIBLE
                    binding.textTaskBasedCodeLabel.visibility = View.VISIBLE
                    binding.inputLayoutActivityCode.visibility = View.VISIBLE
                    binding.inputLayoutTaskBasedCode.visibility = View.VISIBLE
                    binding.edtTxtActivityCode.visibility = View.VISIBLE
                    binding.edtTxtTaskBasedCode.visibility = View.VISIBLE
                } else {
                    binding.textActivityCodeLabel.visibility = View.GONE
                    binding.textTaskBasedCodeLabel.visibility = View.GONE
                    binding.inputLayoutActivityCode.visibility = View.GONE
                    binding.inputLayoutTaskBasedCode.visibility = View.GONE
                    binding.edtTxtActivityCode.visibility = View.GONE
                    binding.edtTxtTaskBasedCode.visibility = View.GONE
                }
            }
        }
        viewModel.matterRateMutableLiveData.observe(this) {
            it?.let { matterRate ->
                viewModel.assignedRatePerHour = matterRate.assignedRatePerHour?.toDouble() ?: 0.00
                binding.edtTxtAssignedRateHour.setText(viewModel.assignedRatePerHour.toDollar())
            }
        }
        viewModel.ticketMutableLiveData.observe(this) {
            it?.let {
                commonUtils.showToastMessage(
                    this,
                    this.resources.getString(R.string.ticket_save_success_message)
                )
                setResult(RESULT_OK)
                finish()
            }
        }
        viewModel.ticketDetailsMutableLiveData.observe(this) {
            it?.let {
                setTicketDetails(it)
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
                    commonUtils.enableDisableViews(lytNewTicketParent, false)
                    frameAnimation.start()
                } else {
                    lytProgressParent.lytProgress.visibility = View.GONE
                    stopAnimation()
                    commonUtils.enableDisableViews(lytNewTicketParent, true)
                    if (viewModel.isFromEdit) {
                        if (preferenceHelper.isTablet().not()) {
                            lytMatter?.setEnable(false)
                        } else {
                            lytRow1?.setEnable(false)
                        }
                        edtTxtMatterId.setEnable(false)
                        allMattersCheckbox.setEnable(false)
                        edtTxtMatterId.background = ContextCompat.getDrawable(
                            this@NewTicketActivity,
                            R.drawable.timekeeper_code_background
                        )
                    }
                }
            }
        }
        viewModel.networkMutableLiveData.observe(this) {
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

    private fun getClassId(): Int {
        return try {
            if (viewModel.isFromEdit) {
                viewModel.classIdEdit
            } else {
                val loginDetails =
                    gson.fromJson(preferenceHelper.getLoginDetails(), LoginResponse::class.java)
                loginDetails?.classId ?: -1
            }
        } catch (e: Exception) {
            -1
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
                handlingBackPress()
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

    private fun startTimer() {
        mHandler = Handler(Looper.getMainLooper())
        mStatusChecker.run()
    }

    private fun stopTimer() {
        mHandler?.removeCallbacks(mStatusChecker)
    }

    private fun stopTimerView() {
        startButtonClicked = false
        Glide.with(this).load(R.drawable.ic_arrow_right).into(binding.btnTimerStart)
    }

    private fun startTimerView() {
        binding.edtTxtBookedHours.setText(String.format(Locale.getDefault(), "%.1f", bookedHours))
        startButtonClicked = true
        Glide.with(this).load(R.drawable.ic_pause).into(binding.btnTimerStart)
    }

    private var mStatusChecker: Runnable = object : Runnable {
        override fun run() {
            try {
                timeInSeconds += 1
                updateStopWatchView(timeInSeconds)
            } finally {
                mHandler?.postDelayed(this, mInterval.toLong())
            }
        }
    }

    private fun updateStopWatchView(timeInSeconds: Long) {
        val formattedTime = commonUtils.getFormattedStopWatch((timeInSeconds * 1000))
        binding.edtTxtTimer.setText(formattedTime)
        var milliseconds = (timeInSeconds * 1000)
        val hours = TimeUnit.MILLISECONDS.toHours(milliseconds)
        milliseconds -= TimeUnit.HOURS.toMillis(hours)
        val minutes = TimeUnit.MILLISECONDS.toMinutes(milliseconds)
        if (minutes == bookedHoursIncrementValue) {
            bookedHours += 0.1
            binding.edtTxtBookedHours.setText(
                String.format(
                    Locale.getDefault(),
                    "%.1f",
                    bookedHours
                )
            )
            bookedHoursIncrementValue += 6
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        stopTimer()
    }

    private fun resetTimerView() {
        timeInSeconds = 0
        startButtonClicked = false
        binding.edtTxtTimer.text = null
        bookedHours = 0.1
        bookedHoursIncrementValue = 6L
        binding.edtTxtBookedHours.text = null
        Glide.with(this).load(R.drawable.ic_arrow_right).into(binding.btnTimerStart)
    }

    private val activityResultLauncher: CustomResultLauncher<Intent, ActivityResult> =
        CustomResultLauncher.registerForActivityResult(
            this,
            ActivityResultContracts.StartActivityForResult()
        )

    private fun isValid(): Boolean {
        when {
            startButtonClicked -> {
                commonUtils.showToastMessage(
                    this@NewTicketActivity,
                    this@NewTicketActivity.resources.getString(R.string.stop_timer_message)
                )
                return false
            }

            binding.edtTxtMatterId.text.toString().isEmpty() -> {
                commonUtils.showToastMessage(
                    this@NewTicketActivity,
                    this@NewTicketActivity.resources.getString(R.string.matter_number_empty_message)
                )
                return false
            }

            binding.edtTxtActivityCode.visibility == View.VISIBLE && binding.edtTxtActivityCode.text.toString()
                .isEmpty() -> {
                commonUtils.showToastMessage(
                    this@NewTicketActivity,
                    this@NewTicketActivity.resources.getString(R.string.activity_code_empty_message)
                )
                return false
            }

            binding.edtTxtTaskBasedCode.visibility == View.VISIBLE && binding.edtTxtTaskBasedCode.text.toString()
                .isEmpty() -> {
                commonUtils.showToastMessage(
                    this@NewTicketActivity,
                    this@NewTicketActivity.resources.getString(R.string.task_based_code_empty_message)
                )
                return false
            }

            binding.edtTxtClientName.text.toString().isEmpty() -> {
                commonUtils.showToastMessage(
                    this@NewTicketActivity,
                    this@NewTicketActivity.resources.getString(R.string.client_name_empty_message)
                )
                return false
            }

            binding.edtTxtTimeKeeperCode.text.toString().isEmpty() -> {
                commonUtils.showToastMessage(
                    this@NewTicketActivity,
                    this@NewTicketActivity.resources.getString(R.string.timekeeper_code_empty_message)
                )
                return false
            }

            /*
                        viewModel.isFromEdit.not() && viewModel.isFromCopy.not() && binding.edtTxtTimer.text.toString()
                            .isEmpty() -> {
                            commonUtils.showToastMessage(
                                this@NewTicketActivity,
                                this@NewTicketActivity.resources.getString(R.string.timer_empty_message)
                            )
                            return false
                        }
            */

            binding.edtTxtBookedHours.text.toString()
                .isEmpty() || binding.edtTxtBookedHours.text.toString().toDouble() <= 0 -> {
                commonUtils.showToastMessage(
                    this@NewTicketActivity,
                    this@NewTicketActivity.resources.getString(R.string.booked_hours_empty_message)
                )
                return false
            }

            binding.edtTxtBillType.text.toString().isEmpty() -> {
                commonUtils.showToastMessage(
                    this@NewTicketActivity,
                    this@NewTicketActivity.resources.getString(R.string.bill_type_empty_message)
                )
                return false
            }

            binding.edtTxtDate.text.toString().isEmpty() -> {
                commonUtils.showToastMessage(
                    this@NewTicketActivity,
                    this@NewTicketActivity.resources.getString(R.string.date_empty_message)
                )
                return false
            }

            binding.edtTxtAssignedRateHour.text.toString().isEmpty() -> {
                commonUtils.showToastMessage(
                    this@NewTicketActivity,
                    this@NewTicketActivity.resources.getString(R.string.assigned_rate_hours_empty_message)
                )
                return false
            }

            binding.edtTxtDefaultRateHour.text.toString().isEmpty() -> {
                commonUtils.showToastMessage(
                    this@NewTicketActivity,
                    this@NewTicketActivity.resources.getString(R.string.default_rate_hour_empty_message)
                )
                return false
            }

            binding.edtTxtTimeTicketAmount.text.toString()
                .isEmpty() || viewModel.timeTicketAmount <= 0 -> {
                commonUtils.showToastMessage(
                    this@NewTicketActivity,
                    this@NewTicketActivity.resources.getString(R.string.time_ticket_amount_empty_message)
                )
                return false
            }

            binding.edtTxtDescription.text.toString().isEmpty() -> {
                commonUtils.showToastMessage(
                    this@NewTicketActivity,
                    this@NewTicketActivity.resources.getString(R.string.description_empty_message)
                )
                return false
            }

            else -> {
                return true
            }
        }
    }

    private fun showDatePicker() {
        val constraintsBuilder =
            CalendarConstraints.Builder()
                .setValidator(
                    DateValidatorPointBackward.now()
                )
        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setTheme(R.style.DatePickerStyle)
            .setTitleText(this.resources.getString(R.string.select_date))
            .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
            .setCalendarConstraints(constraintsBuilder.build())
            .build()
        datePicker.addOnPositiveButtonClickListener {
            try {
                val date = Date(it)
                val backendDateFormat =
                    SimpleDateFormat(Constants.MM_DD_YYYY_HH_MM_SS, Locale.getDefault()).apply {
                        timeZone = TimeZone.getTimeZone("UTC")
                    }
                viewModel.selectedDate = backendDateFormat.format(date)
                val displayDateFormat =
                    SimpleDateFormat(Constants.MM_DD_YYYY, Locale.getDefault()).apply {
                        timeZone = TimeZone.getTimeZone("UTC")
                    }
                binding.edtTxtDate.setText(displayDateFormat.format(date))
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        datePicker.show(supportFragmentManager, SearchActivity.TAG_DATE_PICKER)
    }

    /*
        override fun onBackPressed() {
            if (timerIsRunning().not()) {
                finish()
            }
            super.onBackPressed()
        }
    */

    private fun timerIsRunning(): Boolean {
        if (startButtonClicked) {
            commonUtils.showToastMessage(
                this@NewTicketActivity,
                this@NewTicketActivity.resources.getString(R.string.stop_timer_message)
            )
            return true
        }
        return false
    }

    private fun bookedHourWatcher(): TextWatcher {
        return object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                timeTicketAmountCalculation(binding.edtTxtBillType.text.toString())
            }
        }
    }

    private fun billTypeWatcher(): TextWatcher {
        return object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                timeTicketAmountCalculation(s.toString())
            }
        }
    }

    private fun timeTicketAmountCalculation(billType: String) {
        val hours = if (binding.edtTxtBookedHours.text.toString()
                .isEmpty()
        ) 0.0 else binding.edtTxtBookedHours.text.toString().toDouble()
        if (billType == Constants.BILLABLE) {
            viewModel.timeTicketAmount = (viewModel.assignedRatePerHour * hours)
        } else {
            viewModel.timeTicketAmount = (viewModel.defaultRatePerHour * hours)
        }
        binding.edtTxtTimeTicketAmount.setText(viewModel.timeTicketAmount.toDollar())
    }

    private fun setCurrentDate() {
        viewModel.selectedDate = commonUtils.getDateTime(Constants.MM_DD_YYYY_HH_MM_SS) ?: ""
        binding.edtTxtDate.setText(commonUtils.getDateTime(Constants.MM_DD_YYYY) ?: "")
    }

    private fun handlingBackPress() {
        if (timerIsRunning().not()) {
            stopAnimation()
            setResult(RESULT_CANCELED)
            finish()
        }
    }
}