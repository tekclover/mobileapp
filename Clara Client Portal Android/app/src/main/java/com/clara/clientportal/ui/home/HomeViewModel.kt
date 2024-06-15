package com.clara.clientportal.ui.home

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.clara.clientportal.BaseViewModel
import com.clara.clientportal.model.FindClientGeneralResponse
import com.clara.clientportal.model.NotificationCount
import com.clara.clientportal.model.request.FindClientUserNewRequest
import com.clara.clientportal.network.APIConstant
import com.clara.clientportal.network.NetworkResult
import com.clara.clientportal.repository.MainRepository
import com.clara.clientportal.utils.CommonUtils
import com.clara.clientportal.utils.PreferenceHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: MainRepository,
    private val commonUtils: CommonUtils,
    private val preferenceHelper: PreferenceHelper
) : BaseViewModel() {

    val clientUserMutableLiveData: MutableLiveData<List<FindClientGeneralResponse>> =
        MutableLiveData()
    val loaderMutableLiveData: MutableLiveData<Boolean> = MutableLiveData()
    var networkMutableLiveDat: MutableLiveData<Boolean> = MutableLiveData()
    var apiFailureMutableLiveData: MutableLiveData<Boolean> = MutableLiveData()
    var notificationCountLiveData: MutableLiveData<NotificationCount> = MutableLiveData()
    var emailId = ""


    fun getAuthToken(apiName: String, transactionId: Int) {
        viewModelScope.launch {
            repository.getSetupServiceAuthToken(
                commonUtils.authTokenRequest(apiName),
                transactionId
            )
                .onStart { loaderMutableLiveData.value = true }
                .collectLatest {
                    when (it) {
                        is NetworkResult.Success -> {
                            it.result?.let { result ->
                                authTokenMutableLiveData.value = result
                                when (it.id) {
                                    APIConstant.FIND_CLIENT_USER_NEW_ID -> {
                                        findClientUserNew()
                                    }

                                    APIConstant.NOTIFICATION_COUNT_ID -> {
                                        getNotificationCount()
                                    }
                                }
                            }
                        }

                        is NetworkResult.Failure -> {
                            loaderMutableLiveData.value = false
                            apiFailureMutableLiveData.value = true
                        }

                        NetworkResult.NoNetwork -> {
                            networkMutableLiveDat.value = true
                        }

                        else -> {
                            loaderMutableLiveData.value = false
                            apiFailureMutableLiveData.value = true
                        }
                    }
                }
        }
    }

    private fun findClientUserNew() {
        viewModelScope.launch {
            repository.findClientUser(
                authTokenMutableLiveData.value?.accessToken ?: "",
                findClientGeneralRequest()
            )
                .onCompletion { loaderMutableLiveData.value = false }
                .collectLatest {
                    when (it) {
                        is NetworkResult.Success -> {
                            it.result?.let { result ->
                                clientUserMutableLiveData.value = result
                            }
                        }

                        is NetworkResult.Failure -> {
                            loaderMutableLiveData.value = false
                            apiFailureMutableLiveData.value = true
                        }

                        NetworkResult.NoNetwork -> {
                            networkMutableLiveDat.value = true
                        }

                        else -> {
                            loaderMutableLiveData.value = false
                            apiFailureMutableLiveData.value = true
                        }
                    }
                }
        }
    }

    private fun findClientGeneralRequest(): FindClientUserNewRequest {
        val emailIdList: MutableList<String> = mutableListOf()
        emailIdList.add(emailId)
        return FindClientUserNewRequest(
            emailIdList
        )
    }

    private fun getNotificationCount() {
        viewModelScope.launch {
            repository.notificationCount(
                preferenceHelper.getClientId(),
                authTokenMutableLiveData.value?.accessToken ?: ""
            )
                .collectLatest {
                    when (it) {
                        is NetworkResult.Success -> {
                            it.result?.let { result ->
                                notificationCountLiveData.value = result
                            }
                        }

                        is NetworkResult.Failure -> {
                            loaderMutableLiveData.value = false
                            apiFailureMutableLiveData.value = true
                        }

                        NetworkResult.NoNetwork -> {
                            networkMutableLiveDat.value = true
                        }

                        else -> {
                            loaderMutableLiveData.value = false
                            apiFailureMutableLiveData.value = true
                        }
                    }
                }
        }
    }
}