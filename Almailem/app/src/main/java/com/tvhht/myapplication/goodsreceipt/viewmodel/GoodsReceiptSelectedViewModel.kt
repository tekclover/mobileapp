package com.tvhht.myapplication.goodsreceipt.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.tvhht.myapplication.api.ApiServices
import com.tvhht.myapplication.api.RetrofitBuilder
import com.tvhht.myapplication.goodsreceipt.model.CreateImPartnerRequest
import com.tvhht.myapplication.goodsreceipt.model.CreateImPartnerResponse
import com.tvhht.myapplication.goodsreceipt.model.ErrorResponse
import com.tvhht.myapplication.goodsreceipt.model.FindImPartnerRequest
import com.tvhht.myapplication.goodsreceipt.model.FindImPartnerResponse
import com.tvhht.myapplication.goodsreceipt.model.GoodsReceiptRequest
import com.tvhht.myapplication.goodsreceipt.model.SelectedDocumentResponse
import com.tvhht.myapplication.goodsreceipt.model.StagingLineUpdateResponse
import com.tvhht.myapplication.login.model.LoginModel
import com.tvhht.myapplication.login.model.LoginResponse
import com.tvhht.myapplication.picking.viewmodel.SingleLiveData
import com.tvhht.myapplication.utils.ApiConstant
import com.tvhht.myapplication.utils.PrefConstant
import com.tvhht.myapplication.utils.WMSApplication
import com.tvhht.myapplication.utils.WMSSharedPref
import retrofit2.HttpException
import rx.Observable
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers


class GoodsReceiptSelectedViewModel : ViewModel() {
    var selectedDocumentList: MutableLiveData<List<SelectedDocumentResponse>> = MutableLiveData()
    var findImPartner: MutableLiveData<List<FindImPartnerResponse>> = MutableLiveData()
    var loaderLiveData: MutableLiveData<Boolean> = MutableLiveData()
    var authToken: MutableLiveData<String> = MutableLiveData()
    var createImPartner: MutableLiveData<List<CreateImPartnerResponse>> = MutableLiveData()
    var stagingLineUpdate: MutableLiveData<StagingLineUpdateResponse> = MutableLiveData()
    var errorMessage: SingleLiveData<Boolean> = SingleLiveData()
    var itemCode = ""
    val caseCodeList: MutableList<String> = mutableListOf()
    val stagingNoList: MutableList<String> = mutableListOf()
    var barcode = ""
    var businessPartnerCode = ""
    var selectedDocument: SelectedDocumentResponse? = null
    var partNoList: List<SelectedDocumentResponse>? = null
    private fun loadSelectedDocument() {
        val apiService: ApiServices = RetrofitBuilder.apiService
        val warehouseIdList: MutableList<String> = ArrayList()
        val companyCodeIdList: MutableList<String> = ArrayList()
        val languageIdList: MutableList<String> = ArrayList()
        val plantIdList: MutableList<String> = ArrayList()
        val statusIdList: MutableList<Int> = ArrayList()

        val wareId: String =
            WMSSharedPref.getAppPrefs(WMSApplication.getInstance().context)
                .getStringValue(PrefConstant.WARE_HOUSE_ID)
        val companyCodeId: String =
            WMSSharedPref.getAppPrefs(WMSApplication.getInstance().context)
                .getStringValue(PrefConstant.COMPANY_CODE_ID)
        val languageId: String =
            WMSSharedPref.getAppPrefs(WMSApplication.getInstance().context)
                .getStringValue(PrefConstant.LANGUAGE_ID)
        val plantId: String =
            WMSSharedPref.getAppPrefs(WMSApplication.getInstance().context)
                .getStringValue(PrefConstant.PLANT_ID)

        warehouseIdList.add(wareId)
        companyCodeIdList.add(companyCodeId)
        languageIdList.add(languageId)
        plantIdList.add(plantId)
        statusIdList.add(14)
        statusIdList.add(15)
        statusIdList.add(16)

        val goodsRequest =
            GoodsReceiptRequest(
                if (wareId == ApiConstant.WAREHOUSE_ID_200) null else companyCodeIdList,
                languageIdList,
                if (wareId == ApiConstant.WAREHOUSE_ID_200) null else plantIdList,
                warehouseIdList,
                statusIdList
            )
        //val request = SelectedDocumentRequest(caseCodeList, stagingNoList)
        apiService.getSelectedDocument(authToken.value ?: "", goodsRequest)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .onErrorResumeNext { throwable -> Observable.error(throwable) }
            .subscribe(object : Subscriber<List<SelectedDocumentResponse>>() {
                override fun onCompleted() {
                    loaderLiveData.value = false
                }

                override fun onError(e: Throwable) {
                    loaderLiveData.value = false
                }

                override fun onNext(response: List<SelectedDocumentResponse>) {
                    selectedDocumentList.postValue(response)
                }
            })
    }

    private fun getIMPartner() {
        val apiService: ApiServices = RetrofitBuilder.apiService

        val warehouseIdList: MutableList<String> = ArrayList()
        val companyCodeIdList: MutableList<String> = ArrayList()
        val languageIdList: MutableList<String> = ArrayList()
        val plantIdList: MutableList<String> = ArrayList()
        val itemCodeList: MutableList<String> = ArrayList()

        val wareId: String =
            WMSSharedPref.getAppPrefs(WMSApplication.getInstance().context)
                .getStringValue(PrefConstant.WARE_HOUSE_ID)
        val companyCodeId: String =
            WMSSharedPref.getAppPrefs(WMSApplication.getInstance().context)
                .getStringValue(PrefConstant.COMPANY_CODE_ID)
        val languageId: String =
            WMSSharedPref.getAppPrefs(WMSApplication.getInstance().context)
                .getStringValue(PrefConstant.LANGUAGE_ID)
        val plantId: String =
            WMSSharedPref.getAppPrefs(WMSApplication.getInstance().context)
                .getStringValue(PrefConstant.PLANT_ID)

        warehouseIdList.add(wareId)
        companyCodeIdList.add(companyCodeId)
        languageIdList.add(languageId)
        plantIdList.add(plantId)
        itemCodeList.add(itemCode)

        val request = FindImPartnerRequest(
            if (wareId == ApiConstant.WAREHOUSE_ID_200) null else companyCodeIdList,
            languageIdList,
            if (wareId == ApiConstant.WAREHOUSE_ID_200) null else plantIdList,
            warehouseIdList,
            itemCodeList,
            businessPartnerCode
        )
        apiService.findImPartner(authToken.value ?: "", request)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .onErrorResumeNext { throwable -> Observable.error(throwable) }
            .subscribe(object : Subscriber<List<FindImPartnerResponse>>() {
                override fun onCompleted() {
                    loaderLiveData.value = false
                }

                override fun onError(e: Throwable) {
                    loaderLiveData.value = false
                }

                override fun onNext(response: List<FindImPartnerResponse>) {
                    findImPartner.postValue(response)
                }
            })
    }

    fun getAuthToken(transactionId: Int, apiName: String) {
        val request = LoginModel(
            apiName,
            ApiConstant.clientId,
            ApiConstant.clientSecretKey,
            ApiConstant.grantType,
            ApiConstant.apiName_name,
            ApiConstant.apiName_pass_key
        )
        val apiService: ApiServices = RetrofitBuilder.apiService
        apiService.checkUser(request)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .onErrorResumeNext { throwable -> Observable.error(throwable) }
            .subscribe(object : Subscriber<LoginResponse?>() {
                override fun onStart() {
                    loaderLiveData.value = true
                }

                override fun onCompleted() {}
                override fun onError(e: Throwable) {
                    loaderLiveData.value = false
                }

                override fun onNext(response: LoginResponse?) {
                    response?.access_token?.let {
                        authToken.value = it
                        when (transactionId) {
                            ApiConstant.GR_FIND_IM_PARTNER_ID -> {
                                getIMPartner()
                            }

                            ApiConstant.GR_SELECTED_DOCUMENT_ID -> {
                                loadSelectedDocument()
                            }
                            ApiConstant.GR_CREATE_IM_PARTNER_ID -> {
                                createIMPartner()
                            }
                            ApiConstant.GR_STAGING_LINE_UPDATE_ID -> {
                                stagingLineUpdate()
                            }
                        }
                    }
                }
            })
    }
    private fun createIMPartner() {
        val apiService: ApiServices = RetrofitBuilder.apiService
        val wareId: String =
            WMSSharedPref.getAppPrefs(WMSApplication.getInstance().context)
                .getStringValue(PrefConstant.WARE_HOUSE_ID)
        val companyCodeId: String =
            WMSSharedPref.getAppPrefs(WMSApplication.getInstance().context)
                .getStringValue(PrefConstant.COMPANY_CODE_ID)
        val languageId: String =
            WMSSharedPref.getAppPrefs(WMSApplication.getInstance().context)
                .getStringValue(PrefConstant.LANGUAGE_ID)
        val plantId: String =
            WMSSharedPref.getAppPrefs(WMSApplication.getInstance().context)
                .getStringValue(PrefConstant.PLANT_ID)
        val loginId: String =
            WMSSharedPref.getAppPrefs(WMSApplication.getInstance().context)
                .getStringValue(PrefConstant.LOGIN_ID)

        val imPartnerList: ArrayList<CreateImPartnerRequest> = ArrayList()
        imPartnerList.add(
            CreateImPartnerRequest(
                selectedDocument?.companyCode ?: "",
                selectedDocument?.languageId ?: "",
                selectedDocument?.plantId ?: "",
                selectedDocument?.warehouseId ?: "",
                "1",
                selectedDocument?.manufacturerCode ?: "",
                selectedDocument?.itemCode ?: "",
                barcode,
                selectedDocument?.manufacturerCode ?: "",
                selectedDocument?.manufacturerName ?: "",
                selectedDocument?.brand ?: ""
            )
        )
        apiService.createImPartner(authToken.value ?: "",loginId, imPartnerList)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .onErrorResumeNext { throwable -> Observable.error(throwable) }
            .subscribe(object : Subscriber<List<CreateImPartnerResponse>>() {
                override fun onCompleted() {
                    loaderLiveData.value = false
                }

                override fun onError(e: Throwable) {
                    loaderLiveData.value = false
                    try {
                        val httpError = e as HttpException
                        val errorBody = httpError.response()?.errorBody()?.string()
                        val response = Gson().fromJson(errorBody, ErrorResponse::class.java)
                        if (response?.error.isNullOrEmpty()
                                .not() && response.error?.contains("BarcodeId already exist") == true
                        ) {
                            errorMessage.value = true
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                override fun onNext(response: List<CreateImPartnerResponse>) {
                    createImPartner.postValue(response)
                }
            })
    }
    private fun stagingLineUpdate() {
        val apiService: ApiServices = RetrofitBuilder.apiService
        val loginId: String =
            WMSSharedPref.getAppPrefs(WMSApplication.getInstance().context)
                .getStringValue(PrefConstant.LOGIN_ID)

        selectedDocument?.let {
            apiService.updateStagingLine(
                it.lineNo, authToken.value ?: "",
                it.caseCode ?: "",
                it.refDocNumber ?: "",
                it.stagingNo ?: "",
                it.itemCode ?: "",
                it.palletCode ?: "",
                it.preInboundNo ?: "",
                it.warehouseId ?: "",
                it.companyCode ?: "",
                it.languageId ?: "",
                it.plantId ?: "",
                loginId,
                it
            )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .onErrorResumeNext { throwable -> Observable.error(throwable) }
                .subscribe(object : Subscriber<StagingLineUpdateResponse>() {
                    override fun onCompleted() {
                        loaderLiveData.value = false
                    }

                    override fun onError(e: Throwable) {
                        loaderLiveData.value = false
                        try {
                            val httpError = e as HttpException
                            val errorBody = httpError.response()?.errorBody()?.string()
                            val response = Gson().fromJson(errorBody, ErrorResponse::class.java)
                            if (response?.error.isNullOrEmpty()
                                    .not() && response.error?.contains("Barcode already Assigned") == true
                            ) {
                                errorMessage.value = true
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }

                    override fun onNext(response: StagingLineUpdateResponse) {
                        stagingLineUpdate.postValue(response)
                    }
                })
        }
    }
}