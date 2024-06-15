package com.clara.timekeeping.ui.login

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.clara.timekeeping.BaseViewModel
import com.clara.timekeeping.model.AuthTokenResponse
import com.clara.timekeeping.model.ErrorResponse
import com.clara.timekeeping.model.LoginResponse
import com.clara.timekeeping.network.NetworkResult
import com.clara.timekeeping.repository.TimeTicketRepository
import com.clara.timekeeping.utils.CommonUtils
import com.clara.timekeeping.utils.Constants
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val repository: TimeTicketRepository,
    private val commonUtils: CommonUtils
) : BaseViewModel() {
    var loginMutableLiveDat: MutableLiveData<LoginResponse> = MutableLiveData()
    var networkMutableLiveDat: MutableLiveData<Boolean> = MutableLiveData()
    val loaderMutableLiveData: MutableLiveData<Boolean> = MutableLiveData()
    var apiFailureMutableLiveData: MutableLiveData<String> = MutableLiveData()

    var userId: String = ""
    var password: String = ""

    fun getAuthToken() {
        viewModelScope.launch {
            repository.getAuthToken(
                commonUtils.authTokenRequest(Constants.SETUP_SERVICE),
                0
            )
                .onStart { loaderMutableLiveData.value = true }
                .collectLatest {
                    when (it) {
                        is NetworkResult.Success -> {
                            it.result?.let { result ->
                                authTokenMutableLiveData.value = result as AuthTokenResponse
                            }
                            login()
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

    private fun login() {
        viewModelScope.launch {
            repository.login(authTokenMutableLiveData.value?.accessToken ?: "", userId, password)
                .onCompletion { loaderMutableLiveData.value = false }
                .collectLatest {
                    when (it) {
                        is NetworkResult.Success -> {
                            it.result?.let { result ->
                                loginMutableLiveDat.value = result as LoginResponse
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
}