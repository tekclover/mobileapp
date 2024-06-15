package com.clara.clientportal.ui.matter

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.clara.clientportal.model.MatterPopupDetailsResponse
import com.clara.clientportal.model.SetupServiceAuthResponse
import com.clara.clientportal.model.request.SetupServiceAuthTokenRequest
import com.clara.clientportal.network.APIConstant
import com.clara.clientportal.network.NetworkResult
import com.clara.clientportal.repository.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MatterPopupViewModel @Inject constructor(private val repository: MainRepository) :
    ViewModel() {
    var matterNo: String = ""
    private val setupServiceAuthTokenMutableLiveData: MutableLiveData<SetupServiceAuthResponse> =
        MutableLiveData()
    val matterPopupMutableLiveData: MutableLiveData<MatterPopupDetailsResponse> =
        MutableLiveData()

    init {
        setupServiceAuthToken()
    }

    private fun setupServiceAuthToken() {
        viewModelScope.launch {
            repository.getSetupServiceAuthToken(setupServiceAuthTokenRequest(), 0)
                .collectLatest {
                    when (it) {
                        is NetworkResult.Success -> {
                            it.result?.let { result ->
                                setupServiceAuthTokenMutableLiveData.value = result
                                getMatterPopupDetails()
                            }
                        }

                        is NetworkResult.Failure -> {

                        }

                        else -> {}
                    }
                }
        }
    }

    private fun setupServiceAuthTokenRequest(): SetupServiceAuthTokenRequest {
        return SetupServiceAuthTokenRequest(
            "pixeltrice",
            "pixeltrice-secret-key",
            "password",
            "test",
            "test",
            "mnr-management-service"
        )
    }


    private fun getMatterPopupDetails() {
        viewModelScope.launch {
            repository.matterPopupDetails(
                APIConstant.MATTER_POPUP_DETAILS + matterNo,
                setupServiceAuthTokenMutableLiveData.value?.accessToken ?: ""
            )
                .collectLatest {
                    when (it) {
                        is NetworkResult.Success -> {
                            it.result?.let { result ->
                                matterPopupMutableLiveData.value = result
                            }
                        }

                        is NetworkResult.Failure -> {

                        }

                        else -> {}
                    }

                }
        }
    }
}