package com.clara.timekeeping.ui.logout

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
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LogoutViewModel @Inject constructor(
    private val repository: TimeTicketRepository,
    private val commonUtils: CommonUtils,
    private val preferenceHelper: PreferenceHelper,
    private val gson: Gson
) : BaseViewModel() {
    var networkMutableLiveData: MutableLiveData<Boolean> = MutableLiveData()
    val loaderMutableLiveData: MutableLiveData<Boolean> = MutableLiveData()
    var apiFailureMutableLiveData: MutableLiveData<String> = MutableLiveData()
    var pushNotificationLiveData: MutableLiveData<PushNotificationResponse?> = MutableLiveData()

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

    private fun sendFCMTokenToServer() {
        viewModelScope.launch {
            repository.createPushNotification(
                authTokenMutableLiveData.value?.accessToken ?: "",
                preferenceHelper.getUserId(),
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
            getClassId().toString(),
            preferenceHelper.getUserId(),
            preferenceHelper.getFCMToken(),
            false,
            preferenceHelper.getFCMToken()
        )
    }

    private fun getClassId(): Int {
        return try {
            val loginDetails =
                gson.fromJson(preferenceHelper.getLoginDetails(), LoginResponse::class.java)
            loginDetails?.classId ?: -1
        } catch (e: Exception) {
            -1
        }
    }
}