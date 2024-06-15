package com.clara.clientportal.ui.matter

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.clara.clientportal.BaseViewModel
import com.clara.clientportal.model.MatterResponse
import com.clara.clientportal.model.request.MatterRequest
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
class MatterViewModel @Inject constructor(
    private val repository: MainRepository,
    private val preferenceHelper: PreferenceHelper,
    private val commonUtils: CommonUtils
) : BaseViewModel() {
    val matterMutableLiveData: MutableLiveData<List<MatterResponse>> = MutableLiveData()
    val loaderMutableLiveData: MutableLiveData<Boolean> = MutableLiveData()
    val networkMutableLiveData: MutableLiveData<Boolean> = MutableLiveData()
    val apiFailureMutableLiveData: MutableLiveData<Boolean> = MutableLiveData()

    init {
        getAuthToken()
    }

    private fun getAuthToken() {
        viewModelScope.launch {
            repository.getSetupServiceAuthToken(
                commonUtils.authTokenRequest(Constants.MANAGEMENT_SERVICE),
                0
            )
                .onStart { loaderMutableLiveData.value = true }
                .collectLatest {
                    when (it) {
                        is NetworkResult.Success -> {
                            it.result?.let { result ->
                                authTokenMutableLiveData.value = result
                            }
                            getMatterDetails()
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

    private fun getMatterDetails() {
        viewModelScope.launch {
            repository.findMatter(
                authTokenMutableLiveData.value?.accessToken ?: "",
                matterRequest()
            )
                .onCompletion { loaderMutableLiveData.value = false }
                .collectLatest {
                    when (it) {
                        is NetworkResult.Success -> {
                            it.result?.let { result ->
                                matterMutableLiveData.value = result
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

    private fun matterRequest(): MatterRequest {
        val clientIdList: MutableList<String> = mutableListOf()
        clientIdList.add(preferenceHelper.getClientId())
        return MatterRequest(
            clientIdList
        )
    }
}