package com.clara.clientportal.ui.quotation

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.clara.clientportal.BaseViewModel
import com.clara.clientportal.model.QuotationResponse
import com.clara.clientportal.model.request.MatterRequest
import com.clara.clientportal.network.NetworkResult
import com.clara.clientportal.repository.MainRepository
import com.clara.clientportal.utils.CommonUtils
import com.clara.clientportal.utils.Constants
import com.clara.clientportal.utils.FileUtils
import com.clara.clientportal.utils.PreferenceHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class QuotationViewModel @Inject constructor(
    private val repository: MainRepository,
    private val preferenceHelper: PreferenceHelper,
    private val commonUtils: CommonUtils
) : BaseViewModel() {
    val quotationMutableLiveData: MutableLiveData<List<QuotationResponse>> = MutableLiveData()
    val loaderMutableLiveData: MutableLiveData<Boolean> = MutableLiveData()
    val networkMutableLiveData: MutableLiveData<Boolean> = MutableLiveData()
    val apiFailureMutableLiveData: MutableLiveData<Boolean> = MutableLiveData()
    val downloadMutableLiveData: MutableLiveData<String> = MutableLiveData()
    var documentName = ""

    init {
        getAuthToken()
    }

    private fun getAuthToken() {
        viewModelScope.launch {
            repository.getSetupServiceAuthToken(
                commonUtils.authTokenRequest(Constants.ACCOUNT_SERVICE),
                0
            )
                .onStart { loaderMutableLiveData.value = true }
                .collectLatest {
                    when (it) {
                        is NetworkResult.Success -> {
                            it.result?.let { result ->
                                authTokenMutableLiveData.value = result
                            }
                            getQuotation()
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

    private fun getQuotation() {
        viewModelScope.launch {
            repository.findQuotation(
                authTokenMutableLiveData.value?.accessToken ?: "",
                matterRequest()
            )
                .onCompletion { loaderMutableLiveData.value = false }
                .collectLatest {
                    when (it) {
                        is NetworkResult.Success -> {
                            it.result?.let { result ->
                                quotationMutableLiveData.value = result
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

    fun downloadDocument(context: Context, url: String, docName: String) {
        FileUtils.downloadDocument(context, url, docName) {
            downloadMutableLiveData.value = it
        }
    }
}