package com.clara.timekeeping.ui.notification

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.clara.timekeeping.BaseViewModel
import com.clara.timekeeping.model.AuthTokenResponse
import com.clara.timekeeping.model.ErrorResponse
import com.clara.timekeeping.model.NotificationRequest
import com.clara.timekeeping.model.NotificationResponse
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
class NotificationViewModel @Inject constructor(
    private val repository: TimeTicketRepository,
    private val commonUtils: CommonUtils,
    private val preferenceHelper: PreferenceHelper
) : BaseViewModel() {
    var networkMutableLiveData: MutableLiveData<Boolean> = MutableLiveData()
    val loaderMutableLiveData: MutableLiveData<Boolean> = MutableLiveData()
    var apiFailureMutableLiveData: MutableLiveData<String> = MutableLiveData()
    val notificationLiveData: MutableLiveData<List<NotificationResponse>> = MutableLiveData()
    val notificationUpdateLiveData: MutableLiveData<List<NotificationResponse>?> = MutableLiveData()

    fun getAuthToken(transactionId: Int) {
        viewModelScope.launch {
            repository.getAuthToken(
                commonUtils.authTokenRequest(Constants.MANAGEMENT_SERVICE),
                transactionId
            )
                .onStart { loaderMutableLiveData.value = true }
                .collectLatest {
                    when (it) {
                        is NetworkResult.Success -> {
                            it.result?.let { result ->
                                authTokenMutableLiveData.value = result as AuthTokenResponse
                            }
                            when (it.id) {
                                APIConstant.NOTIFICATION_MESSAGE_ID -> {
                                    fetchNotifications()
                                }

                                APIConstant.NOTIFICATION_MESSAGE_UPDATE_ID -> {
                                    notificationsUpdate()
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


    private fun fetchNotifications() {
        viewModelScope.launch {
            repository.getNotificationMessages(
                authTokenMutableLiveData.value?.accessToken ?: "",
                notificationMessageRequest()
            )
                .onCompletion { loaderMutableLiveData.value = false }
                .collectLatest {
                    when (it) {
                        is NetworkResult.Success -> {
                            it.result?.let { result ->
                                notificationLiveData.value = result as List<NotificationResponse>
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

    private fun notificationMessageRequest(): NotificationRequest {
        val clientIdList: ArrayList<String> = arrayListOf()
        val orderTypeList: ArrayList<String> = arrayListOf()
        clientIdList.add(preferenceHelper.getUserId())
        orderTypeList.add(Constants.NOTIFICATION_ORDER_TYPE)
        return NotificationRequest(clientIdList, orderTypeList)
    }

    private fun notificationsUpdate() {
        viewModelScope.launch {
            repository.notificationMessageUpdate(
                authTokenMutableLiveData.value?.accessToken ?: "",
                preferenceHelper.getUserId(),
                notificationUpdateRequest()
            )
                .onCompletion { loaderMutableLiveData.value = false }
                .collectLatest {
                    when (it) {
                        is NetworkResult.Success -> {
                            it.result?.let { result ->
                                notificationUpdateLiveData.value =
                                    result as List<NotificationResponse>
                            }
                        }

                        is NetworkResult.Failure -> {
                            it.result?.let { error ->
                                apiFailureMutableLiveData.value =
                                    (error as ErrorResponse).error ?: ""
                            }
                            loaderMutableLiveData.value = false
                            notificationUpdateLiveData.value = null
                        }

                        is NetworkResult.Error -> {
                            it.exception?.let { error ->
                                apiFailureMutableLiveData.value = (error as Exception).message
                            }
                            loaderMutableLiveData.value = false
                            notificationUpdateLiveData.value = null
                        }

                        NetworkResult.NoNetwork -> {
                            networkMutableLiveData.value = true
                            loaderMutableLiveData.value = false
                        }
                    }
                }
        }
    }

    private fun notificationUpdateRequest(): List<NotificationResponse> {
        val notificationUpdateList: ArrayList<NotificationResponse> = ArrayList()
        notificationLiveData.value?.let {
            for (notification in it) {
                notification.menu = true
                notificationUpdateList.add(notification)
            }
        }
        return notificationUpdateList
    }
}