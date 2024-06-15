package com.clara.clientportal.ui.notification

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.clara.clientportal.BaseViewModel
import com.clara.clientportal.model.NotificationRequest
import com.clara.clientportal.model.NotificationResponse
import com.clara.clientportal.network.APIConstant
import com.clara.clientportal.network.NetworkResult
import com.clara.clientportal.repository.MainRepository
import com.clara.clientportal.utils.CommonUtils
import com.clara.clientportal.utils.Constants
import com.clara.clientportal.utils.PreferenceHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotificationViewModel @Inject constructor(
    private val repository: MainRepository,
    private val commonUtils: CommonUtils,
    private val preferenceHelper: PreferenceHelper
) : BaseViewModel() {
    val loaderMutableLiveData: MutableLiveData<Boolean> = MutableLiveData()
    val networkMutableLiveData: MutableLiveData<Boolean> = MutableLiveData()
    val apiFailureMutableLiveData: MutableLiveData<Boolean> = MutableLiveData()
    val notificationLiveData: MutableLiveData<List<NotificationResponse>> = MutableLiveData()
    val notificationUpdateLiveData: MutableLiveData<List<NotificationResponse>?> = MutableLiveData()
    var orderType = ""
    var isFromMenu = false
    var isFromTab = false
    var selectedOrder = ""

    fun getAuthToken(transactionId: Int) {
        viewModelScope.launch {
            repository.getSetupServiceAuthToken(
                commonUtils.authTokenRequest(Constants.MANAGEMENT_SERVICE),
                transactionId
            )
                .onStart { loaderMutableLiveData.value = true }
                .collectLatest {
                    when (it) {
                        is NetworkResult.Success -> {
                            it.result?.let { result ->
                                authTokenMutableLiveData.value = result
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
                            loaderMutableLiveData.value = false
                            apiFailureMutableLiveData.value = true
                        }

                        NetworkResult.NoNetwork -> {
                            networkMutableLiveData.value = true
                        }

                        else -> {
                            loaderMutableLiveData.value = false
                            apiFailureMutableLiveData.value = true
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
                                notificationLiveData.value = result
                            }
                        }

                        is NetworkResult.Failure -> {
                            loaderMutableLiveData.value = false
                            apiFailureMutableLiveData.value = true
                        }

                        NetworkResult.NoNetwork -> {
                            networkMutableLiveData.value = true
                        }

                        else -> {
                            loaderMutableLiveData.value = false
                            apiFailureMutableLiveData.value = true
                        }
                    }
                }
        }
    }

    private fun notificationMessageRequest(): NotificationRequest {
        val clientIdList: ArrayList<String> = arrayListOf()
        val orderTypeList: ArrayList<String> = arrayListOf()
        clientIdList.add(preferenceHelper.getClientId())
        if (orderType.isNotEmpty()) {
            orderTypeList.add(orderType)
            return NotificationRequest(clientIdList, orderTypeList)
        }
        return NotificationRequest(clientIdList, commonUtils.getOrderType())
    }

    private fun notificationsUpdate() {
        viewModelScope.launch {
            repository.notificationMessageUpdate(
                authTokenMutableLiveData.value?.accessToken ?: "",
                preferenceHelper.getClientUserId() ?: "",
                notificationUpdateRequest()
            )
                .onCompletion { loaderMutableLiveData.value = false }
                .collectLatest {
                    when (it) {
                        is NetworkResult.Success -> {
                            it.result?.let { result ->
                                notificationUpdateLiveData.value = result
                            }
                        }

                        is NetworkResult.Failure -> {
                            loaderMutableLiveData.value = false
                            apiFailureMutableLiveData.value = true
                            notificationUpdateLiveData.value = null
                        }

                        NetworkResult.NoNetwork -> {
                            networkMutableLiveData.value = true
                        }

                        else -> {
                            loaderMutableLiveData.value = false
                            apiFailureMutableLiveData.value = true
                            notificationUpdateLiveData.value = null
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
                notification.tab = true
                notificationUpdateList.add(notification)
            }
        }
        return notificationUpdateList
    }
}