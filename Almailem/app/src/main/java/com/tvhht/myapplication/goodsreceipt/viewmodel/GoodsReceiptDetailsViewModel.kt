package com.tvhht.myapplication.goodsreceipt.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.tvhht.myapplication.api.ApiServices
import com.tvhht.myapplication.api.RetrofitBuilder
import com.tvhht.myapplication.goodsreceipt.model.CBMRequest
import com.tvhht.myapplication.goodsreceipt.model.CBMResponse
import com.tvhht.myapplication.goodsreceipt.model.GRLineSubmitResponse
import com.tvhht.myapplication.goodsreceipt.model.HHTUserRequest
import com.tvhht.myapplication.goodsreceipt.model.PackBarcodeResponse
import com.tvhht.myapplication.goodsreceipt.model.SelectedDocumentResponse
import com.tvhht.myapplication.login.model.LoginModel
import com.tvhht.myapplication.login.model.LoginResponse
import com.tvhht.myapplication.login.model.UserDetails
import com.tvhht.myapplication.utils.ApiConstant
import com.tvhht.myapplication.utils.PrefConstant
import com.tvhht.myapplication.utils.WMSApplication
import com.tvhht.myapplication.utils.WMSSharedPref
import rx.Observable
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

class GoodsReceiptDetailsViewModel : ViewModel() {
    var cbmDetails: MutableLiveData<List<CBMResponse>> = MutableLiveData()
    var submitGRLine: MutableLiveData<List<GRLineSubmitResponse>> = MutableLiveData()
    var userList: MutableLiveData<List<UserDetails>> = MutableLiveData()
    var authToken: MutableLiveData<String> = MutableLiveData()
    var packBarcodeLiveData: MutableLiveData<List<PackBarcodeResponse>> = MutableLiveData()
    var loaderLiveData: MutableLiveData<Boolean> = MutableLiveData()

    var selectedDocumentResponse: SelectedDocumentResponse? = null
    var acceptedQty: Int? = null
    var damagedQty: Int? = null
    private fun getCBMDetails() {
        // do async operation to fetch users
        val apiService: ApiServices = RetrofitBuilder.apiService
        val warehouseIdList: MutableList<String> = ArrayList()
        val companyCodeIdList: MutableList<String> = ArrayList()
        val languageIdList: MutableList<String> = ArrayList()
        val plantIdList: MutableList<String> = ArrayList()
        val itemCodeList: MutableList<String> = ArrayList()
        val manufacturePartNoList: MutableList<String> = ArrayList()

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
        selectedDocumentResponse?.itemCode?.let { itemCodeList.add(it) }
        selectedDocumentResponse?.manufacturerPartNo?.let { manufacturePartNoList.add(it) }
        val request = CBMRequest(
            if (wareId == ApiConstant.WAREHOUSE_ID_200) null else companyCodeIdList,
            languageIdList,
            if (wareId == ApiConstant.WAREHOUSE_ID_200) null else plantIdList,
            warehouseIdList,
            itemCodeList,
            manufacturePartNoList
        )
        apiService.getCbmDetails(authToken.value ?: "", request)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .onErrorResumeNext { throwable -> Observable.error(throwable) }
            .subscribe(object : Subscriber<List<CBMResponse>>() {
                override fun onCompleted() {
                    loaderLiveData.value = false
                }

                override fun onError(e: Throwable) {
                    loaderLiveData.value = false
                    cbmDetails.postValue(emptyList())
                }

                override fun onNext(response: List<CBMResponse>) {
                    cbmDetails.postValue(response)
                }
            })
    }

    private fun submitGRLine() {
        val apiService: ApiServices = RetrofitBuilder.apiService
        val userId: String =
            WMSSharedPref.getAppPrefs(WMSApplication.getInstance().context)
                .getStringValue(PrefConstant.LOGIN_ID)

        selectedDocumentResponse?.let {
            selectedDocumentResponse?.acceptedQty = (acceptedQty ?: 0)
            selectedDocumentResponse?.damageQty = (damagedQty ?: 0)
            selectedDocumentResponse?.barcodeId = selectedDocumentResponse?.partnerItemBarcode
            selectedDocumentResponse?.goodReceiptQty = 1
            selectedDocumentResponse?.varianceQty =
                (((selectedDocumentResponse?.orderQty ?: 0) - ((damagedQty ?: 0) + (acceptedQty ?: 0))))
            val grLineList: MutableList<SelectedDocumentResponse> = mutableListOf()
            grLineList.add(it)
            apiService.grLineSubmit(authToken.value ?: "", userId, grLineList)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .onErrorResumeNext { throwable -> Observable.error(throwable) }
                .subscribe(object : Subscriber<List<GRLineSubmitResponse>>() {
                    override fun onCompleted() {
                        loaderLiveData.value = false
                    }

                    override fun onError(e: Throwable) {
                        loaderLiveData.value = false
                        submitGRLine.postValue(emptyList())
                    }

                    override fun onNext(response: List<GRLineSubmitResponse>) {
                        submitGRLine.postValue(response)
                    }
                })
        }
    }

    fun getHhtUserList() {
        val apiService: ApiServices = RetrofitBuilder.apiService

        val companyCodeId: String =
            WMSSharedPref.getAppPrefs(WMSApplication.getInstance().context)
                .getStringValue(PrefConstant.COMPANY_CODE_ID)
        val languageId: String =
            WMSSharedPref.getAppPrefs(WMSApplication.getInstance().context)
                .getStringValue(PrefConstant.LANGUAGE_ID)
        val wareId: String =
            WMSSharedPref.getAppPrefs(WMSApplication.getInstance().context)
                .getStringValue(PrefConstant.WARE_HOUSE_ID)
        val plantId: String =
            WMSSharedPref.getAppPrefs(WMSApplication.getInstance().context)
                .getStringValue(PrefConstant.PLANT_ID)
        val userId: String =
            WMSSharedPref.getAppPrefs(WMSApplication.getInstance().context)
                .getStringValue(PrefConstant.LOGIN_ID)

        val companyCodeIdList: MutableList<String> = mutableListOf()
        companyCodeIdList.add(companyCodeId)
        val languageIdList: MutableList<String> = mutableListOf()
        languageIdList.add(languageId)
        val warehouseIdList: MutableList<String> = mutableListOf()
        warehouseIdList.add(wareId)
        val plantIdList: MutableList<String> = mutableListOf()
        plantIdList.add(plantId)
        val userIdList: MutableList<String> = mutableListOf()
        userIdList.add(userId)
        apiService.getHHTUser(
            authToken.value ?: "",
            HHTUserRequest(
                if (wareId == ApiConstant.WAREHOUSE_ID_200) null else companyCodeIdList,
                languageIdList,
                warehouseIdList,
                if (wareId == ApiConstant.WAREHOUSE_ID_200) null else plantIdList
            )
        )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .onErrorResumeNext { throwable -> Observable.error(throwable) }
            .subscribe(object : Subscriber<List<UserDetails?>>() {
                override fun onCompleted() {
                    loaderLiveData.value = false
                }

                override fun onError(e: Throwable) {
                    loaderLiveData.value = false
                    userList.postValue(emptyList())
                }

                override fun onNext(response: List<UserDetails?>) {
                    userList.postValue(response as List<UserDetails>?)
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
                            ApiConstant.GR_CBM_ID -> {
                                getCBMDetails()
                            }

                            ApiConstant.GR_PACK_BARCODE_ID -> {
                                getPackBarcodes()
                            }

                            ApiConstant.GR_HHT_USER_ID -> {
                                getHhtUserList()
                            }

                            ApiConstant.GR_LINE_ID -> {
                                submitGRLine()
                            }
                        }
                    }
                }
            })
    }

    private fun getPackBarcodes() {
        // do async operation to fetch users
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
        val userId: String =
            WMSSharedPref.getAppPrefs(WMSApplication.getInstance().context)
                .getStringValue(PrefConstant.LOGIN_ID)
        apiService.getPackBarCode(
            authToken.value ?: "",
            acceptedQty ?: 0,
            damagedQty ?: 0,
            wareId,
            companyCodeId,
            plantId,
            languageId,
            userId
        )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .onErrorResumeNext { throwable -> Observable.error(throwable) }
            .subscribe(object : Subscriber<List<PackBarcodeResponse>>() {
                override fun onCompleted() {}
                override fun onError(e: Throwable) {
                    loaderLiveData.value = false
                    packBarcodeLiveData.postValue(emptyList())
                }

                override fun onNext(response: List<PackBarcodeResponse>) {
                    packBarcodeLiveData.postValue(response)
                }
            })
    }
}