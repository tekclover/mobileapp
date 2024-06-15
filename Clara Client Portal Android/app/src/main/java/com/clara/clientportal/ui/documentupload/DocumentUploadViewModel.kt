package com.clara.clientportal.ui.documentupload

import android.content.Context
import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.clara.clientportal.BaseViewModel
import com.clara.clientportal.model.DocumentUploadUpdateResponse
import com.clara.clientportal.model.DocumentUploadedResponse
import com.clara.clientportal.model.MatterDetails
import com.clara.clientportal.model.MatterIdResponse
import com.clara.clientportal.model.UploadResponse
import com.clara.clientportal.model.request.DocumentUploadUpdateRequest
import com.clara.clientportal.model.request.MatterRequest
import com.clara.clientportal.network.APIConstant
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
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import javax.inject.Inject

@HiltViewModel
class DocumentUploadViewModel @Inject constructor(
    private val repository: MainRepository,
    private val commonUtils: CommonUtils,
    private val preferenceHelper: PreferenceHelper
) : BaseViewModel() {
    val uploadedDocumentMutableLiveData: MutableLiveData<List<DocumentUploadedResponse>> =
        MutableLiveData()
    val loaderMutableLiveData: MutableLiveData<Boolean> = MutableLiveData()
    val matterIdMutableLiveData: MutableLiveData<List<MatterIdResponse>> =
        MutableLiveData()
    val uploadMutableLiveData: MutableLiveData<UploadResponse> =
        MutableLiveData()
    val documentUploadUpdateMutableLiveData: MutableLiveData<DocumentUploadUpdateResponse> =
        MutableLiveData()
    private val matterDetailsMutableLiveData: MutableLiveData<MatterDetails> =
        MutableLiveData()
    val networkMutableLiveData: MutableLiveData<Boolean> = MutableLiveData()
    val apiFailureMutableLiveData: MutableLiveData<Boolean> = MutableLiveData()
    val downloadMutableLiveData: MutableLiveData<String> = MutableLiveData()
    var documentName = ""
    var matterId: String? = null
    var tempCameraUri: Uri? = null
    var uploadList: MutableLiveData<File> = MutableLiveData()
    var matterNoList: ArrayList<String>? = null
    init {
        getAuthToken(APIConstant.UPLOAD_DOCUMENT)
    }

    fun getAuthToken(apiId: Int) {
        viewModelScope.launch {
            repository.getSetupServiceAuthToken(
                commonUtils.authTokenRequest(Constants.MANAGEMENT_SERVICE),
                apiId
            )
                .onStart { loaderMutableLiveData.value = true }
                .collectLatest {
                    when (it) {
                        is NetworkResult.Success -> {
                            it.result?.let { result ->
                                authTokenMutableLiveData.value = result
                            }
                            when (it.id) {
                                APIConstant.UPLOAD_DOCUMENT -> {
                                    getUploadedDocument()
                                }

                                APIConstant.MATTER_IDS -> {
                                    matterId()
                                }

                                APIConstant.UPLOAD -> {
                                    uploadDocument()
                                }

                                APIConstant.DOCUMENT_UPLOAD_MATTER_ID -> {
                                    getUploadDocumentMatterDetails()
                                }

                                APIConstant.DOCUMENT_UPLOAD_UPDATE_ID -> {
                                    uploadDocumentUpdate()
                                }
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

    private fun getUploadedDocument() {
        viewModelScope.launch {
            repository.uploadedDocument(
                authTokenMutableLiveData.value?.accessToken ?: "",
                uploadedDocumentRequest()
            )
                .onCompletion { loaderMutableLiveData.value = false }
                .collectLatest {
                    when (it) {
                        is NetworkResult.Success -> {
                            it.result?.let { result ->
                                uploadedDocumentMutableLiveData.value = result
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

    private fun matterId() {
        viewModelScope.launch {
            repository.matterId(
                authTokenMutableLiveData.value?.accessToken ?: "",
                uploadedDocumentRequest()
            )
                .onCompletion { loaderMutableLiveData.value = false }
                .collectLatest {
                    when (it) {
                        is NetworkResult.Success -> {
                            it.result?.let { result ->
                                matterIdMutableLiveData.value = result
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

    private fun uploadDocument() {
        viewModelScope.launch {
            repository.uploadDocument(
                authTokenMutableLiveData.value?.accessToken ?: "",
                Constants.PARAM_CLIENT_PORTAL + Constants.SLASH_SYMBOL + matterDetailsMutableLiveData.value?.clientId + Constants.SLASH_SYMBOL + matterDetailsMutableLiveData.value?.matterNumber,
                matterDetailsMutableLiveData.value?.classId ?: -1,
                getMultiPartRequestBody()
            )
                .collectLatest {
                    when (it) {
                        is NetworkResult.Success -> {
                            it.result?.let { result ->
                                uploadMutableLiveData.value = result
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

    private fun uploadDocumentUpdate() {
        viewModelScope.launch {
            repository.documentUploadUpdate(
                authTokenMutableLiveData.value?.accessToken ?: "",
                preferenceHelper.getClientUserId(),
                uploadDocumentUpdateRequest()
            )
                .collectLatest {
                    when (it) {
                        is NetworkResult.Success -> {
                            it.result?.let { result ->
                                documentUploadUpdateMutableLiveData.value = result
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

    private fun getUploadDocumentMatterDetails() {
        viewModelScope.launch {
            repository.uploadDocumentMatterDetails(
                APIConstant.DOCUMENT_UPLOAD_MATTER + Constants.SLASH_SYMBOL + matterId,
                authTokenMutableLiveData.value?.accessToken ?: "",
            )
                .onStart { loaderMutableLiveData.value = true }
                .onCompletion { loaderMutableLiveData.value = false }
                .collectLatest {
                    when (it) {
                        is NetworkResult.Success -> {
                            it.result?.let { result ->
                                matterDetailsMutableLiveData.value = result
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

    private fun uploadedDocumentRequest(): MatterRequest {
        val clientIdList: MutableList<String> = mutableListOf()
        clientIdList.add(preferenceHelper.getClientId())
        return MatterRequest(
            clientIdList
        )
    }

    private fun getMultiPartRequestBody(): MultipartBody {
        val builder = MultipartBody.Builder()
        builder.setType(MultipartBody.FORM)
        uploadList.value?.let {
           /* builder.addFormDataPart(
                Constants.FILE, it.name, RequestBody.create(
                    MediaType.parse(FileUtils.MULTI_PART_TYPE), it
                )
            )*/
            builder.addFormDataPart(
                Constants.FILE, it.name, RequestBody.create(
                    FileUtils.MULTI_PART_TYPE.toMediaType(), it
                )
            )
        }
        return builder.build()
    }

    private fun uploadDocumentUpdateRequest(): DocumentUploadUpdateRequest {
        return DocumentUploadUpdateRequest(
            matterDetailsMutableLiveData.value?.classId,
            matterDetailsMutableLiveData.value?.clientId,
            matterDetailsMutableLiveData.value?.caseCategoryId,
            matterDetailsMutableLiveData.value?.caseSubCategoryId,
            matterDetailsMutableLiveData.value?.matterNumber,
            uploadMutableLiveData.value?.file,
            matterDetailsMutableLiveData.value?.deletionIndicator,
            preferenceHelper.getClientUserId(),
            matterDetailsMutableLiveData.value?.languageId,
            23
        )
    }

    fun downloadDocument(context: Context, url: String, docName: String) {
        FileUtils.downloadDocument(context, url, docName) {
            downloadMutableLiveData.value = it
        }
    }
}