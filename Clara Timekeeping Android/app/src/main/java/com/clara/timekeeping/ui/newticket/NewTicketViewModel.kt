package com.clara.timekeeping.ui.newticket

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.clara.timekeeping.BaseViewModel
import com.clara.timekeeping.model.ActivityCodeResponse
import com.clara.timekeeping.model.AuthTokenResponse
import com.clara.timekeeping.model.ErrorResponse
import com.clara.timekeeping.model.MatterDetails
import com.clara.timekeeping.model.MatterIdResponse
import com.clara.timekeeping.model.MatterRate
import com.clara.timekeeping.model.NewTicketResponse
import com.clara.timekeeping.model.SearchResult
import com.clara.timekeeping.model.TaskBasedCodeResponse
import com.clara.timekeeping.model.TicketRequest
import com.clara.timekeeping.model.TimeKeeperCode
import com.clara.timekeeping.model.TimeTicketSummaryResponse
import com.clara.timekeeping.network.APIConstant
import com.clara.timekeeping.network.NetworkResult
import com.clara.timekeeping.repository.TimeTicketRepository
import com.clara.timekeeping.utils.CommonUtils
import com.clara.timekeeping.utils.Constants
import com.clara.timekeeping.utils.PreferenceHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NewTicketViewModel @Inject constructor(
    private val repository: TimeTicketRepository,
    private val commonUtils: CommonUtils,
    private val preferenceHelper: PreferenceHelper
) : BaseViewModel() {
    var matterIdMutableLiveData: MutableLiveData<MatterIdResponse> = MutableLiveData()
    var timekeeperCodeMutableLiveData: MutableLiveData<List<TimeKeeperCode>> = MutableLiveData()
    var matterRateMutableLiveData: MutableLiveData<MatterRate> = MutableLiveData()
    var matterDetailsMutableLiveData: MutableLiveData<MatterDetails> = MutableLiveData()
    var activityCodeMutableLiveData: MutableLiveData<List<ActivityCodeResponse>> = MutableLiveData()
    var taskBasedCodeMutableLiveData: MutableLiveData<List<TaskBasedCodeResponse>> =
        MutableLiveData()
    var ticketMutableLiveData: MutableLiveData<NewTicketResponse> = MutableLiveData()
    var ticketDetailsMutableLiveData: MutableLiveData<NewTicketResponse> = MutableLiveData()
    var networkMutableLiveData: MutableLiveData<Boolean> = MutableLiveData()
    val loaderMutableLiveData: MutableLiveData<Boolean> = MutableLiveData()
    var apiFailureMutableLiveData: MutableLiveData<String> = MutableLiveData()
    var matterNoList: MutableList<SearchResult> = mutableListOf()
    var activityCodeList: MutableList<SearchResult> = mutableListOf()
    var taskBasedCodeList: MutableList<SearchResult> = mutableListOf()
    var billTypeList: MutableList<SearchResult> = mutableListOf()
    var selectedTimekeeperList: MutableList<String> = mutableListOf()
    var isFromEdit = false
    var isFromCopy = false
    var ticketSummary: TimeTicketSummaryResponse? = null
    var assignedRatePerHour = 0.0
    var defaultRatePerHour = 0.0
    var timeTicketAmount = 0.0
    var allMatters: Boolean = false
    var clientId = ""
    var matterNumber = ""
    var timekeeperCode = ""
    var timeTicketRequest: TicketRequest? = null
    var timeTicketNumber = ""
    var selectedDate = ""
    var activityCode = ""
    var taskBasedCode = ""
    var classIdEdit = -1
    fun getAuthToken(apiId: Int, apiName: String, url: String) {
        viewModelScope.launch {
            repository.getAuthToken(
                commonUtils.authTokenRequest(apiName),
                apiId
            )
                .onStart { loaderMutableLiveData.value = true }
                .collectLatest {
                    when (it) {
                        is NetworkResult.Success -> {
                            it.result?.let { result ->
                                authTokenMutableLiveData.value = result as AuthTokenResponse
                            }
                            when (it.id) {
                                APIConstant.MATTER_NUMBER_CHECKED_ID, APIConstant.MATTER_NUMBER_UNCHECKED_ID -> getMatterNumber(
                                    url,
                                    it.id
                                )

                                APIConstant.SEARCH_TIME_KEEPER_CODE_ID, APIConstant.TASK_BASED_CODE_ID, APIConstant.ACTIVITY_CODE_ID -> getDropDownDetails()
                                APIConstant.NEW_TIME_TICKET_ID, APIConstant.EDIT_TICKET_ID -> submitTicket()
                                APIConstant.MATTER_DETAILS_ID, APIConstant.MATTER_RATE_ID -> getMatterDetailsAndRate()
                                APIConstant.TIME_TICKET_DETAILS_ID -> getTimeTicketDetails()
                            }
                        }

                        is NetworkResult.Failure -> {
                            it.result?.let { error ->
                                apiFailureMutableLiveData.value =
                                    (error as ErrorResponse).error ?: ""
                            }
                            loaderMutableLiveData.value = false
                        }

                        is NetworkResult.Error -> {
                            it.exception?.let { error ->
                                apiFailureMutableLiveData.value = (error as Exception).message
                            }
                            loaderMutableLiveData.value = false
                        }

                        NetworkResult.NoNetwork -> {
                            networkMutableLiveData.value = true
                            loaderMutableLiveData.value = false
                        }
                    }
                }
        }
    }

    private fun getMatterNumber(url: String, transactionId: Int) {
        viewModelScope.launch {
            repository.searchMatterId(
                url,
                authTokenMutableLiveData.value?.accessToken ?: "",
                transactionId
            )
                .onCompletion {
                    if (isFromCopy.not()) {
                        getAuthToken(
                            APIConstant.SEARCH_TIME_KEEPER_CODE_ID,
                            Constants.SETUP_SERVICE,
                            ""
                        )
                    } else {
                        loaderMutableLiveData.value = false
                    }
                }
                .collectLatest {
                    when (it) {
                        is NetworkResult.Success -> {
                            it.result?.let { result ->
                                matterIdMutableLiveData.value = result as MatterIdResponse
                            }
                        }

                        is NetworkResult.Failure -> {
                            it.result?.let { error ->
                                apiFailureMutableLiveData.value =
                                    (error as ErrorResponse).error ?: ""
                            }
                            loaderMutableLiveData.value = false
                        }

                        is NetworkResult.Error -> {
                            it.exception?.let { error ->
                                apiFailureMutableLiveData.value = (error as Exception).message
                            }
                            loaderMutableLiveData.value = false
                        }

                        NetworkResult.NoNetwork -> {
                            networkMutableLiveData.value = true
                            loaderMutableLiveData.value = false
                        }
                    }
                }
        }
    }

    private fun getDropDownDetails() {
        viewModelScope.launch {
            repository.newTicketDropdownDetails(
                authTokenMutableLiveData.value?.accessToken ?: "",
                isFromEdit
            )
                .onCompletion {
                    if (isFromCopy) {
                        getAuthToken(
                            APIConstant.MATTER_NUMBER_CHECKED_ID,
                            Constants.MANAGEMENT_SERVICE,
                            APIConstant.DROP_DOWN_MATTER_ID
                        )
                    } else {
                        loaderMutableLiveData.value = false
                    }
                }
                .collectLatest {
                    when (it) {
                        is NetworkResult.Success -> {
                            when (it.id) {
                                APIConstant.SEARCH_TIME_KEEPER_CODE_ID -> {
                                    it.result?.let { result ->
                                        timekeeperCodeMutableLiveData.value =
                                            result as List<TimeKeeperCode>
                                    }
                                }

                                APIConstant.ACTIVITY_CODE_ID -> {
                                    it.result?.let { result ->
                                        activityCodeMutableLiveData.value =
                                            result as List<ActivityCodeResponse>
                                    }
                                }

                                APIConstant.TASK_BASED_CODE_ID -> {
                                    it.result?.let { result ->
                                        taskBasedCodeMutableLiveData.value =
                                            result as List<TaskBasedCodeResponse>
                                    }
                                }
                            }
                        }

                        is NetworkResult.Failure -> {
                            it.result?.let { error ->
                                apiFailureMutableLiveData.value =
                                    (error as ErrorResponse).error ?: ""
                            }
                            loaderMutableLiveData.value = false
                        }

                        is NetworkResult.Error -> {
                            it.exception?.let { error ->
                                apiFailureMutableLiveData.value = (error as Exception).message
                            }
                            loaderMutableLiveData.value = false
                        }

                        NetworkResult.NoNetwork -> {
                            networkMutableLiveData.value = true
                            loaderMutableLiveData.value = false
                        }
                    }
                }
        }
    }

    private fun submitTicket() {
        viewModelScope.launch {
            timeTicketRequest?.let { it ->
                if (isFromEdit) {
                    repository.editTicket(
                        timeTicketNumber,
                        authTokenMutableLiveData.value?.accessToken ?: "",
                        preferenceHelper.getUserId(),
                        it
                    )
                } else {
                    repository.newTimeTicket(
                        authTokenMutableLiveData.value?.accessToken ?: "",
                        preferenceHelper.getUserId(),
                        it
                    )
                }
                    .onCompletion { loaderMutableLiveData.value = false }
                    .collectLatest {
                        when (it) {
                            is NetworkResult.Success -> {
                                it.result?.let { result ->
                                    ticketMutableLiveData.value =
                                        result as NewTicketResponse
                                }
                            }

                            is NetworkResult.Failure -> {
                                it.result?.let { error ->
                                    apiFailureMutableLiveData.value =
                                        (error as ErrorResponse).error ?: ""
                                }
                                loaderMutableLiveData.value = false
                            }

                            is NetworkResult.Error -> {
                                it.exception?.let { error ->
                                    apiFailureMutableLiveData.value = (error as Exception).message
                                }
                                loaderMutableLiveData.value = false
                            }

                            NetworkResult.NoNetwork -> {
                                networkMutableLiveData.value = true
                                loaderMutableLiveData.value = false
                            }
                        }
                    }
            }
        }
    }

    private fun getMatterDetailsAndRate() {
        viewModelScope.launch {
            repository.matterDetailsAndRate(
                matterNumber,
                authTokenMutableLiveData.value?.accessToken ?: "",
                timekeeperCode
            )
                .onStart { loaderMutableLiveData.value = true }
                .onCompletion { loaderMutableLiveData.value = false }
                .collectLatest {
                    when (it) {
                        is NetworkResult.Success -> {
                            when (it.id) {
                                APIConstant.MATTER_DETAILS_ID -> {
                                    it.result?.let { result ->
                                        matterDetailsMutableLiveData.value =
                                            result as MatterDetails
                                    }
                                }

                                APIConstant.MATTER_RATE_ID -> {
                                    it.result?.let { result ->
                                        matterRateMutableLiveData.value =
                                            result as MatterRate
                                    }
                                }
                            }
                        }

                        is NetworkResult.Failure -> {
                            it.result?.let { error ->
                                apiFailureMutableLiveData.value =
                                    (error as ErrorResponse).error ?: ""
                            }
                            loaderMutableLiveData.value = false
                        }

                        is NetworkResult.Error -> {
                            it.exception?.let { error ->
                                apiFailureMutableLiveData.value = (error as Exception).message
                            }
                            loaderMutableLiveData.value = false
                        }

                        NetworkResult.NoNetwork -> {
                            networkMutableLiveData.value = true
                            loaderMutableLiveData.value = false
                        }
                    }
                }
        }
    }

    private fun getTimeTicketDetails() {
        viewModelScope.launch {
            repository.timeTicketDetails(
                timeTicketNumber,
                authTokenMutableLiveData.value?.accessToken ?: ""
            )
                .onCompletion {  }
                .collectLatest {
                    when (it) {
                        is NetworkResult.Success -> {
                            it.result?.let { result ->
                                ticketDetailsMutableLiveData.value =
                                    result as NewTicketResponse
                            }
                        }

                        is NetworkResult.Failure -> {
                            it.result?.let { error ->
                                apiFailureMutableLiveData.value =
                                    (error as ErrorResponse).error ?: ""
                            }
                            loaderMutableLiveData.value = false
                        }

                        is NetworkResult.Error -> {
                            it.exception?.let { error ->
                                apiFailureMutableLiveData.value = (error as Exception).message
                            }
                            loaderMutableLiveData.value = false
                        }

                        NetworkResult.NoNetwork -> {
                            networkMutableLiveData.value = true
                            loaderMutableLiveData.value = false
                        }
                    }
                }

        }
    }
}