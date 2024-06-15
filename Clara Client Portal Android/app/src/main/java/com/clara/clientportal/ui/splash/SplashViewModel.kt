package com.clara.clientportal.ui.splash

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.clara.clientportal.BaseViewModel
import com.clara.clientportal.model.StatusResponse
import com.clara.clientportal.network.NetworkResult
import com.clara.clientportal.repository.MainRepository
import com.clara.clientportal.utils.CommonUtils
import com.clara.clientportal.utils.Constants
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val repository: MainRepository,
    private val commonUtils: CommonUtils
) : BaseViewModel() {
    val statusMutableLiveData: MutableLiveData<List<StatusResponse>> =
        MutableLiveData()
    var networkMutableLiveDat: MutableLiveData<Boolean> = MutableLiveData()

    init {
        getAuthToken()
    }

    private fun getAuthToken() {
        viewModelScope.launch {
            repository.getSetupServiceAuthToken(
                commonUtils.authTokenRequest(Constants.SETUP_SERVICE),
                0
            )
                .collectLatest {
                    when (it) {
                        is NetworkResult.Success -> {
                            it.result?.let { result ->
                                authTokenMutableLiveData.value = result
                                getStatus()
                            }
                        }

                        is NetworkResult.Failure -> {

                        }

                        NetworkResult.NoNetwork -> {
                            networkMutableLiveDat.value = true
                        }

                        else -> {}
                    }
                }
        }
    }

    private fun getStatus() {
        viewModelScope.launch {
            repository.status(authTokenMutableLiveData.value?.accessToken ?: "")
                .collectLatest {
                    when (it) {
                        is NetworkResult.Success -> {
                            it.result?.let { result ->
                                statusMutableLiveData.value = result
                            }
                        }

                        is NetworkResult.Failure -> {

                        }

                        NetworkResult.NoNetwork -> {
                            networkMutableLiveDat.value = true
                        }

                        else -> {}
                    }
                }
        }
    }
}