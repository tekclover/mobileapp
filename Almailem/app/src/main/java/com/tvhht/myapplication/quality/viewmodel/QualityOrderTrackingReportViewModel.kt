package com.tvhht.myapplication.quality.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.tvhht.myapplication.api.ApiServices
import com.tvhht.myapplication.api.RetrofitBuilder
import com.tvhht.myapplication.login.model.LoginModel
import com.tvhht.myapplication.login.model.LoginResponse
import com.tvhht.myapplication.quality.model.OutboundLineRequest
import com.tvhht.myapplication.quality.model.OutboundLineResponse
import com.tvhht.myapplication.utils.ApiConstant
import com.tvhht.myapplication.utils.PrefConstant
import com.tvhht.myapplication.utils.WMSApplication
import com.tvhht.myapplication.utils.WMSSharedPref
import rx.Observable
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

class QualityOrderTrackingReportViewModel : ViewModel() {
    var authToken: MutableLiveData<String> = MutableLiveData()
    var loaderLiveData: MutableLiveData<Boolean> = MutableLiveData()
    var outboundLineLiveData: MutableLiveData<List<OutboundLineResponse>> = MutableLiveData()
    var selectedSalesOrderNo = ""
    fun getAuthToken() {
        val request = LoginModel(
            ApiConstant.apiName_transaction,
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
                        getOutboundLines()
                    }
                }
            })
    }

    fun getOutboundLines() {
        val apiService: ApiServices = RetrofitBuilder.apiService

        val companyCodeId: String =
            WMSSharedPref.getAppPrefs(WMSApplication.getInstance().context)
                .getStringValue(PrefConstant.COMPANY_CODE_ID)
        val languageId: String =
            WMSSharedPref.getAppPrefs(WMSApplication.getInstance().context)
                .getStringValue(PrefConstant.LANGUAGE_ID)
        val warehouseId: String =
            WMSSharedPref.getAppPrefs(WMSApplication.getInstance().context)
                .getStringValue(PrefConstant.WARE_HOUSE_ID)
        val plantId: String =
            WMSSharedPref.getAppPrefs(WMSApplication.getInstance().context)
                .getStringValue(PrefConstant.PLANT_ID)


        val companyCodeIdList: MutableList<String> = mutableListOf()
        val languageIdList: MutableList<String> = mutableListOf()
        val warehouseIdList: MutableList<String> = mutableListOf()
        val plantIdList: MutableList<String> = mutableListOf()
        val statusIdList: MutableList<Int> = mutableListOf()

        companyCodeIdList.add(companyCodeId)
        languageIdList.add(languageId)
        warehouseIdList.add(warehouseId)
        plantIdList.add(plantId)
        statusIdList.add(48)
        statusIdList.add(50)
        statusIdList.add(57)

        val request = OutboundLineRequest(
            if (warehouseId == ApiConstant.WAREHOUSE_ID_200) null else companyCodeIdList,
            languageIdList,
            if (warehouseId == ApiConstant.WAREHOUSE_ID_200) null else plantIdList,
            warehouseIdList,
            statusIdList
        )
        apiService.findOutboundLine(
            authToken.value ?: "",
            request
        )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .onErrorResumeNext { throwable -> Observable.error(throwable) }
            .subscribe(object : Subscriber<List<OutboundLineResponse>>() {
                override fun onCompleted() {
                    loaderLiveData.value = false
                }

                override fun onError(e: Throwable) {
                    loaderLiveData.value = false
                }

                override fun onNext(response: List<OutboundLineResponse>) {
                    outboundLineLiveData.value = response
                }
            })
    }

}