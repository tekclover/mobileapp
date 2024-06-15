package com.clara.timekeeping.ui.search

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.clara.timekeeping.BaseViewModel
import com.clara.timekeeping.model.AuthTokenResponse
import com.clara.timekeeping.model.ErrorResponse
import com.clara.timekeeping.model.MatterIdResponse
import com.clara.timekeeping.model.SearchExecuteRequest
import com.clara.timekeeping.model.SearchResult
import com.clara.timekeeping.model.SearchStatus
import com.clara.timekeeping.model.TimeKeeperCode
import com.clara.timekeeping.model.TimeTicketSummaryResponse
import com.clara.timekeeping.network.APIConstant
import com.clara.timekeeping.network.NetworkResult
import com.clara.timekeeping.repository.TimeTicketRepository
import com.clara.timekeeping.utils.CommonUtils
import com.clara.timekeeping.utils.Constants
import com.clara.timekeeping.utils.FilterHelperClass
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val repository: TimeTicketRepository,
    private val commonUtils: CommonUtils
) : BaseViewModel() {
    var matterIdMutableLiveDat: MutableLiveData<MatterIdResponse> = MutableLiveData()
    var timekeeperCodeMutableLiveDat: MutableLiveData<List<TimeKeeperCode>> = MutableLiveData()
    var statusMutableLiveDat: MutableLiveData<List<SearchStatus>> = MutableLiveData()
    var searchExecuteMutableLiveDat: MutableLiveData<List<TimeTicketSummaryResponse>> =
        MutableLiveData()
    var networkMutableLiveDat: MutableLiveData<Boolean> = MutableLiveData()
    val loaderMutableLiveData: MutableLiveData<Boolean> = MutableLiveData()
    var apiFailureMutableLiveData: MutableLiveData<String> = MutableLiveData()
    var matterNoList: MutableList<SearchResult> = mutableListOf()
    var statusList: MutableList<SearchResult> = mutableListOf()
    var billTypeList: MutableList<SearchResult> = mutableListOf()
    var startDate = ""
    var endDate = ""
    var timekeeperCode = ""

    init {
        getAuthToken(
            APIConstant.MATTER_NUMBER_CHECKED_ID,
            Constants.MANAGEMENT_SERVICE,
            APIConstant.DROP_DOWN_MATTER_ID
        )
    }

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
                                APIConstant.MATTER_NUMBER_CHECKED_ID -> getMatterNumber(url, it.id)
                                APIConstant.SEARCH_TIME_KEEPER_CODE_ID, APIConstant.SEARCH_STATUS_ID -> getTimeKeeperAndStatusDetails()
                                APIConstant.SEARCH_EXECUTE_ID -> searchExecute()
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
                            networkMutableLiveDat.value = true
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
                    getAuthToken(
                        APIConstant.SEARCH_TIME_KEEPER_CODE_ID,
                        Constants.SETUP_SERVICE,
                        ""
                    )
                }
                .collectLatest {
                    when (it) {
                        is NetworkResult.Success -> {
                            it.result?.let { result ->
                                matterIdMutableLiveDat.value = result as MatterIdResponse
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
                            networkMutableLiveDat.value = true
                            loaderMutableLiveData.value = false
                        }
                    }
                }
        }
    }

    private fun getTimeKeeperAndStatusDetails() {
        viewModelScope.launch {
            repository.timeTicketSearchTimekeeperAndStatusDetails(
                authTokenMutableLiveData.value?.accessToken ?: "",
                timekeeperCode.isEmpty()
            )
                .onCompletion { loaderMutableLiveData.value = false }
                .collectLatest {
                    when (it) {
                        is NetworkResult.Success -> {
                            when (it.id) {
                                APIConstant.SEARCH_TIME_KEEPER_CODE_ID -> {
                                    it.result?.let { result ->
                                        timekeeperCodeMutableLiveDat.value =
                                            result as List<TimeKeeperCode>
                                    }
                                }

                                APIConstant.SEARCH_STATUS_ID -> {
                                    it.result?.let { result ->
                                        statusMutableLiveDat.value = result as List<SearchStatus>
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
                            networkMutableLiveDat.value = true
                            loaderMutableLiveData.value = false
                        }
                    }
                }
        }
    }

    private fun searchExecute() {
        viewModelScope.launch {
            repository.searchExecute(
                authTokenMutableLiveData.value?.accessToken ?: "",
                searchExecuteRequest()
            )
                .onCompletion { loaderMutableLiveData.value = false }
                .collectLatest {
                    when (it) {
                        is NetworkResult.Success -> {
                            it.result?.let { result ->
                                searchExecuteMutableLiveDat.value =
                                    result as List<TimeTicketSummaryResponse>
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
                            networkMutableLiveDat.value = true
                            loaderMutableLiveData.value = false
                        }
                    }
                }
        }
    }

    private fun searchExecuteRequest(): SearchExecuteRequest {
        val matterNumberList: ArrayList<String> = arrayListOf()
        val billTypeList: ArrayList<String> = arrayListOf()
        val statusList: ArrayList<Int> = arrayListOf()
        val timekeeperList: ArrayList<String> = arrayListOf()
        if (FilterHelperClass.selectedFilterList.isNotEmpty()) {
            for (searchMap in FilterHelperClass.selectedFilterList) {
                for (key in searchMap.keys) {
                    val filterOptionList = searchMap[key]
                    filterOptionList?.let {
                        when (key) {
                            Constants.MATTER_NO -> {
                                for (searchItem in filterOptionList) {
                                    searchItem.id?.let { it1 -> matterNumberList.add(it1) }
                                }
                            }

                            Constants.BILL_TYPE -> {
                                for (searchItem in filterOptionList) {
                                    searchItem.id?.let { it1 -> billTypeList.add(it1) }
                                }
                            }

                            Constants.STATUS -> {
                                for (searchItem in filterOptionList) {
                                    searchItem.id?.toInt()?.let { it1 -> statusList.add(it1) }
                                }
                            }

                            Constants.TIMEKEEPER_CODE -> {
                                for (searchItem in filterOptionList) {
                                    searchItem.id?.let { it1 -> timekeeperList.add(it1) }
                                }
                            }

                            else -> {}
                        }
                    }
                }
            }
        }
        return SearchExecuteRequest(
            statusList.ifEmpty { null },
            timekeeperList.ifEmpty { null },
            matterNumberList.ifEmpty { null },
            billTypeList.ifEmpty { null },
            startDate.ifEmpty { null },
            endDate.ifEmpty { null }
        )
    }
}