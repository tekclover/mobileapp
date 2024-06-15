package com.clara.timekeeping.ui.verifyotp

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.clara.timekeeping.BaseViewModel
import com.clara.timekeeping.model.AuthTokenResponse
import com.clara.timekeeping.model.ErrorResponse
import com.clara.timekeeping.model.LoginResponse
import com.clara.timekeeping.model.PushNotificationRequest
import com.clara.timekeeping.model.PushNotificationResponse
import com.clara.timekeeping.network.APIConstant
import com.clara.timekeeping.network.NetworkResult
import com.clara.timekeeping.repository.TimeTicketRepository
import com.clara.timekeeping.utils.CommonUtils
import com.clara.timekeeping.utils.PreferenceHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VerifyOtpViewModel @Inject constructor(
    private val repository: TimeTicketRepository,
    private val commonUtils: CommonUtils,
    private val preferenceHelper: PreferenceHelper
) : BaseViewModel() {
    var verifyOtpMutableLiveData: MutableLiveData<LoginResponse> = MutableLiveData()
    var resendOtpMutableLiveData: MutableLiveData<Boolean> = MutableLiveData()
    var networkMutableLiveData: MutableLiveData<Boolean> = MutableLiveData()
    val loaderMutableLiveData: MutableLiveData<Boolean> = MutableLiveData()
    var apiFailureMutableLiveData: MutableLiveData<String> = MutableLiveData()
    var pushNotificationLiveData: MutableLiveData<PushNotificationResponse?> = MutableLiveData()

    var userId: String = ""
    var otp: String = ""

    fun getAuthToken(apiName: String, apiId: Int) {
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
                                APIConstant.VERIFY_EMAIL_OTP_ID -> verifyOtp()
                                APIConstant.RESEND_OTP_ID -> resendOtp()
                                APIConstant.CREATE_PUSH_NOTIFICATION_ID -> sendFCMTokenToServer()
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

    private fun verifyOtp() {
        viewModelScope.launch {
            repository.verifyOtp(authTokenMutableLiveData.value?.accessToken ?: "", userId, otp)
                .collectLatest {
                    when (it) {
                        is NetworkResult.Success -> {
                            it.result?.let { result ->
                                verifyOtpMutableLiveData.value = result as LoginResponse
                            }
                        }

                        is NetworkResult.Failure -> {
                            apiFailureMutableLiveData.value =
                                if (it.result != null) (it.result as ErrorResponse).error else ""
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

    private fun resendOtp() {
        viewModelScope.launch {
            repository.resendOtp(authTokenMutableLiveData.value?.accessToken ?: "", userId)
                .onStart { loaderMutableLiveData.value = true }
                .onCompletion { loaderMutableLiveData.value = false }
                .collectLatest {
                    when (it) {
                        is NetworkResult.Success -> {
                            it.result?.let { result ->
                                resendOtpMutableLiveData.value = result as Boolean
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

    private fun sendFCMTokenToServer() {
        viewModelScope.launch {
            repository.createPushNotification(
                authTokenMutableLiveData.value?.accessToken ?: "",
                verifyOtpMutableLiveData.value?.userId ?: "",
                pushNotificationRequest()
            )
                .onCompletion { loaderMutableLiveData.value = false }
                .collectLatest {
                    when (it) {
                        is NetworkResult.Success -> {
                            it.result?.let { result ->
                                pushNotificationLiveData.value = result as PushNotificationResponse
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
            verifyOtpMutableLiveData.value?.classId?.toString(),
            verifyOtpMutableLiveData.value?.userId ?: "",
            preferenceHelper.getFCMToken(),
            true,
            preferenceHelper.getFCMToken()
        )
    }
}