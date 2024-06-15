package com.clara.clientportal.ui.logout

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.clara.clientportal.BaseViewModel
import com.clara.clientportal.model.ErrorResponse
import com.clara.clientportal.model.PushNotificationRequest
import com.clara.clientportal.model.PushNotificationResponse
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
class LogoutViewModel @Inject constructor(
    private val mainRepository: MainRepository,
    private val commonUtils: CommonUtils,
    private val preferenceHelper: PreferenceHelper
) : BaseViewModel() {
    val loaderMutableLiveData: MutableLiveData<Boolean> = MutableLiveData()
    var networkMutableLiveData: MutableLiveData<Boolean> = MutableLiveData()
    var apiErrorMutableLiveData: MutableLiveData<Boolean> = MutableLiveData()
    var apiFailureMutableLiveData: MutableLiveData<ErrorResponse> = MutableLiveData()
    var pushNotificationLiveData: MutableLiveData<PushNotificationResponse?> = MutableLiveData()

    fun getAuthToken(apiName: String, transactionId: Int) {
        viewModelScope.launch {
            mainRepository.getSetupServiceAuthToken(
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
                                    APIConstant.CREATE_PUSH_NOTIFICATION_ID -> {
                                        sendFCMTokenToServer()
                                    }
                                }
                            }
                        }

                        is NetworkResult.Failure -> {
                            loaderMutableLiveData.value = false
                            apiErrorMutableLiveData.value = true
                        }

                        NetworkResult.NoNetwork -> {
                            networkMutableLiveData.value = true
                        }

                        else -> {
                            loaderMutableLiveData.value = false
                            apiErrorMutableLiveData.value = true
                        }
                    }
                }
        }
    }

    private fun sendFCMTokenToServer() {
        viewModelScope.launch {
            mainRepository.createPushNotification(
                authTokenMutableLiveData.value?.accessToken ?: "",
                preferenceHelper.getClientUserId(),
                pushNotificationRequest()
            )
                .onCompletion { loaderMutableLiveData.value = false }
                .collectLatest {
                    when (it) {
                        is NetworkResult.Success -> {
                            it.result?.let { result ->
                                pushNotificationLiveData.value = result
                            }
                        }

                        NetworkResult.NoNetwork -> {
                            networkMutableLiveData.value = true
                            loaderMutableLiveData.value = false
                        }

                        else -> {
                            loaderMutableLiveData.value = false
                            pushNotificationLiveData.value = null
                        }
                    }
                }
        }
    }

    private fun pushNotificationRequest(): PushNotificationRequest {
        return PushNotificationRequest(
            preferenceHelper.getClassId().toString(),
            preferenceHelper.getClientId(),
            preferenceHelper.getClientUserId(),
            preferenceHelper.getFCMToken(),
            false,
            preferenceHelper.getFCMToken()
        )
    }
}