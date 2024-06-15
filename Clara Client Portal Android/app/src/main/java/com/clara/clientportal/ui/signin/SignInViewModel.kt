package com.clara.clientportal.ui.signin

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.clara.clientportal.BaseViewModel
import com.clara.clientportal.model.ErrorResponse
import com.clara.clientportal.model.FindClientGeneralResponse
import com.clara.clientportal.model.PushNotificationRequest
import com.clara.clientportal.model.PushNotificationResponse
import com.clara.clientportal.model.VerifyOtpResponse
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
import org.json.JSONObject
import javax.inject.Inject

@HiltViewModel
class SignInViewModel @Inject constructor(
    private val mainRepository: MainRepository,
    private val commonUtils: CommonUtils,
    private val preferenceHelper: PreferenceHelper
) :
    BaseViewModel() {

    val sendOtpMutableLiveData: MutableLiveData<Boolean> = MutableLiveData()
    val verifyOtpMutableLiveData: MutableLiveData<VerifyOtpResponse> = MutableLiveData()
    val findClientGeneralMutableLiveData: MutableLiveData<List<FindClientGeneralResponse>> =
        MutableLiveData()
    val loaderMutableLiveData: MutableLiveData<Boolean> = MutableLiveData()
    var networkMutableLiveData: MutableLiveData<Boolean> = MutableLiveData()
    var apiErrorMutableLiveData: MutableLiveData<Boolean> = MutableLiveData()
    var apiFailureMutableLiveData: MutableLiveData<ErrorResponse> = MutableLiveData()
    var pushNotificationLiveData: MutableLiveData<PushNotificationResponse?> = MutableLiveData()
    var mobileOrEmail: String = ""
    var otp: String = ""
    var isMobileOtp: Boolean = true

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
                                    APIConstant.SENT_EMAIL_OTP_ID -> {
                                        sendEmailOtp(mobileOrEmail)
                                    }

                                    APIConstant.VERIFY_EMAIL_OTP_ID -> {
                                        verifyEmailOtp(mobileOrEmail, otp)
                                    }

                                    APIConstant.SENT_MOBILE_OTP_ID -> {
                                        sendMobileOtp(mobileOrEmail)
                                    }

                                    APIConstant.VERIFY_MOBILE_OTP_ID -> {
                                        verifyMobileOtp(mobileOrEmail, otp)
                                    }

                                    APIConstant.FIND_CLIENT_GENERAL_ID -> {
                                        findClientGeneral(mobileOrEmail)
                                    }

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

    private fun sendEmailOtp(email: String) {
        viewModelScope.launch {
            mainRepository.sendEmailOtp(
                authTokenMutableLiveData.value?.accessToken ?: "", email
            )
                .onCompletion { loaderMutableLiveData.value = false }
                .collectLatest {
                    when (it) {
                        is NetworkResult.Success -> {
                            it.result?.let { result ->
                                sendOtpMutableLiveData.value = result as Boolean
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

    private fun verifyEmailOtp(email: String, otp: String) {
        viewModelScope.launch {
            mainRepository.verifyEmailOtp(
                authTokenMutableLiveData.value?.accessToken ?: "", email, otp
            )
                .collectLatest {
                    when (it) {
                        is NetworkResult.Success -> {
                            it.result?.let { result ->
                                verifyOtpMutableLiveData.value = result
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

    private fun sendMobileOtp(contactNo: String) {
        viewModelScope.launch {
            mainRepository.sendMobileOtp(
                authTokenMutableLiveData.value?.accessToken ?: "", contactNo
            )
                .onCompletion { loaderMutableLiveData.value = false }
                .collectLatest {
                    when (it) {
                        is NetworkResult.Success -> {
                            it.result?.let { result ->
                                sendOtpMutableLiveData.value = result as Boolean
                            }
                        }

                        is NetworkResult.Failure -> {
                            it.result?.let { error ->
                                apiFailureMutableLiveData.value = error as ErrorResponse
                            }
                            loaderMutableLiveData.value = false
                            // apiErrorMutableLiveData.value = true
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

    private fun verifyMobileOtp(contactNo: String, otp: String) {
        viewModelScope.launch {
            mainRepository.verifyMobileOtp(
                authTokenMutableLiveData.value?.accessToken ?: "", contactNo, otp
            )
                .collectLatest {
                    when (it) {
                        is NetworkResult.Success -> {
                            it.result?.let { result ->
                                verifyOtpMutableLiveData.value = result
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


    private fun findClientGeneral(mobileOrEmail: String) {
        viewModelScope.launch {
            mainRepository.findClientGeneral(
                authTokenMutableLiveData.value?.accessToken ?: "",
                findClientGeneralRequest(
                    if (isMobileOtp) Constants.CONTACT_NUMBER else Constants.EMAIL_ID,
                    mobileOrEmail
                )
            )
                .onCompletion { }
                .collectLatest {
                    when (it) {
                        is NetworkResult.Success -> {
                            it.result?.let { result ->
                                findClientGeneralMutableLiveData.value = result
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

    private fun findClientGeneralRequest(key: String, value: String): JSONObject {
        val jsonObject = JSONObject()
        jsonObject.put(key, value)
        return JSONObject().apply {
            key to value
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
            true,
            preferenceHelper.getFCMToken()
        )
    }
}