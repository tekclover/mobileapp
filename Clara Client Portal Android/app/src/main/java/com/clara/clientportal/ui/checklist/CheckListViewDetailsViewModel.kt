package com.clara.clientportal.ui.checklist

import android.content.Context
import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.clara.clientportal.BaseViewModel
import com.clara.clientportal.model.CheckListDocumentSubmitResponse
import com.clara.clientportal.model.CheckListResponse
import com.clara.clientportal.model.DocumentUploadUpdateResponse
import com.clara.clientportal.model.MatterDocLists
import com.clara.clientportal.model.UploadResponse
import com.clara.clientportal.model.request.CheckListDocumentUploadUpdateRequest
import com.clara.clientportal.model.request.CheckListViewDetailsRequest
import com.clara.clientportal.model.request.MatterDocListRequest
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
class CheckListViewDetailsViewModel @Inject constructor(
    private val repository: MainRepository,
    private val preferenceHelper: PreferenceHelper,
    private val commonUtils: CommonUtils
) : BaseViewModel() {
    val checkListMutableLiveData: MutableLiveData<List<CheckListResponse>> = MutableLiveData()
    val loaderMutableLiveData: MutableLiveData<Boolean> = MutableLiveData()
    var uploadList: MutableLiveData<File> = MutableLiveData()
    val uploadMutableLiveData: MutableLiveData<UploadResponse> =
        MutableLiveData()
    val documentUploadUpdateMutableLiveData: MutableLiveData<DocumentUploadUpdateResponse> =
        MutableLiveData()
    val documentSubmitMutableLiveData: MutableLiveData<CheckListDocumentSubmitResponse> =
        MutableLiveData()
    val networkMutableLiveData: MutableLiveData<Boolean> = MutableLiveData()
    val apiFailureMutableLiveData: MutableLiveData<Boolean> = MutableLiveData()
    val downloadMutableLiveData: MutableLiveData<String> = MutableLiveData()
    var tempCameraUri: Uri? = null
    var matterNo: String = ""
    var checkListNo: Int = -1
    var documentName = ""
    var matterHeaderId = -1
    var matterDocLists: MatterDocLists? = null


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
                                APIConstant.CHECK_LIST_Id -> {
                                    getCheckListViewDetails()
                                }

                                APIConstant.UPLOAD -> {
                                    uploadDocument()
                                }

                                APIConstant.DOCUMENT_UPLOAD_UPDATE_ID -> {
                                    checkListUploadDocumentUpdate()
                                }
                                APIConstant.CHECK_LIST_DOCUMENT_SUBMIT_ID -> {
                                    checkListDocumentSubmit()
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

    private fun getCheckListViewDetails() {
        viewModelScope.launch {
            repository.checkListViewDetails(
                authTokenMutableLiveData.value?.accessToken ?: "",
                checkListViewDetailsRequest()
            )
                .onCompletion { loaderMutableLiveData.value = false }
                .collectLatest {
                    when (it) {
                        is NetworkResult.Success -> {
                            it.result?.let { result ->
                                checkListMutableLiveData.value = result
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
            repository.uploadCheckList(
                Constants.PARAM_CLIENT_PORTAL + Constants.SLASH_SYMBOL + Constants.TEMP + Constants.SLASH_SYMBOL + matterDocLists?.clientId + Constants.SLASH_SYMBOL + matterDocLists?.matterNumber + Constants.SLASH_SYMBOL + matterDocLists?.checkListNo,
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

    private fun checkListUploadDocumentUpdate() {
        viewModelScope.launch {
            repository.checkListDocumentUploadUpdate(
                APIConstant.CHECKLIST_DOCUMENT_UPLOAD_UPDATE + matterDocLists?.matterHeaderId,
                authTokenMutableLiveData.value?.accessToken ?: "",
                preferenceHelper.getClientUserId(),
                checkListUploadDocumentUpdateRequest()
            )
                .onCompletion { loaderMutableLiveData.value = false }
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
    private fun checkListDocumentSubmit() {
        viewModelScope.launch {
            repository.checkListDocumentSubmit(
                documentUploadUpdateMutableLiveData.value?.matterNumber ?: "",
                authTokenMutableLiveData.value?.accessToken ?: "",
                matterDocLists?.checkListNo ?: -1,
                documentUploadUpdateMutableLiveData.value?.clientId ?: "",
                preferenceHelper.getClientUserId(),
                matterDocLists?.matterHeaderId ?: -1
            )
                .onCompletion { loaderMutableLiveData.value = false }
                .collectLatest {
                    when (it) {
                        is NetworkResult.Success -> {
                            it.result?.let { result ->
                                documentSubmitMutableLiveData.value = result
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

    private fun checkListViewDetailsRequest(): CheckListViewDetailsRequest {
        return CheckListViewDetailsRequest(
            arrayOf(checkListNo).asList(),
            arrayOf(matterHeaderId).asList(),
            arrayOf(matterNo).asList()
        )
    }

    private fun getMultiPartRequestBody(): MultipartBody {
        val builder = MultipartBody.Builder()
        builder.setType(MultipartBody.FORM)
        uploadList.value?.let {
/*            builder.addFormDataPart(
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

    private fun checkListUploadDocumentUpdateRequest(): CheckListDocumentUploadUpdateRequest {
        val matterDocList: MutableList<MatterDocListRequest> = mutableListOf()
        val matterDocListRequest = MatterDocListRequest()
        matterDocListRequest.checkListNo = matterDocLists?.checkListNo
        matterDocListRequest.documentName = matterDocLists?.documentName
        matterDocListRequest.documentUrl = uploadMutableLiveData.value?.file?:""
        matterDocListRequest.matterText = matterDocLists?.matterText
        matterDocListRequest.caseCategoryId = matterDocLists?.caseCategoryId
        matterDocListRequest.caseSubCategoryId = matterDocLists?.caseSubCategoryId
        matterDocListRequest.sequenceNumber = matterDocLists?.sequenceNumber
        matterDocListRequest.classId = matterDocLists?.classId
        matterDocListRequest.clientId = matterDocLists?.clientId
        matterDocListRequest.languageId = matterDocLists?.languageId
        matterDocListRequest.matterNumber = matterDocLists?.matterNumber
        matterDocListRequest.statusId = getStatusId((matterDocLists?.statusId ?: -1))
        matterDocList.add(matterDocListRequest)
        return CheckListDocumentUploadUpdateRequest(
            matterDocLists?.caseSubCategoryId,
            matterDocLists?.checkListNo,
            matterDocLists?.classId,
            matterDocLists?.clientId,
            matterDocLists?.languageId,
            matterDocLists?.matterNumber,
            getStatusId((matterDocLists?.statusId ?: -1)),
            matterDocList
        )
    }
    fun downloadDocument(context: Context, url: String, docName: String) {
        FileUtils.downloadDocument(context, url, docName) {
            downloadMutableLiveData.value = it
        }
    }
    private fun getStatusId(statusId: Int): Number {
        return when (statusId) {
            63 -> 63
            60 -> 63
            else -> 60
        }
    }
}