package com.clara.clientportal.ui.invoice

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.clara.clientportal.BaseViewModel
import com.clara.clientportal.model.InvoiceResponse
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
class InvoiceViewModel @Inject constructor(
    private val repository: MainRepository,
    private val preferenceHelper: PreferenceHelper,
    private val commonUtils: CommonUtils
) : BaseViewModel() {
    val invoiceMutableLiveData: MutableLiveData<List<InvoiceResponse>> = MutableLiveData()
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
                            getInvoice()
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

    private fun getInvoice() {
        viewModelScope.launch {
            repository.findInVoice(
                authTokenMutableLiveData.value?.accessToken ?: "",
                invoiceRequest()
            )
                .onCompletion { loaderMutableLiveData.value = false }
                .collectLatest {
                    when (it) {
                        is NetworkResult.Success -> {
                            it.result?.let { result ->
                                invoiceMutableLiveData.value = result
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

    private fun invoiceRequest(): MatterRequest {
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