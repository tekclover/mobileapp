package com.tvhht.myapplication.picking.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.tvhht.myapplication.api.ApiServices
import com.tvhht.myapplication.api.RetrofitBuilder
import com.tvhht.myapplication.goodsreceipt.model.FindImPartnerRequest
import com.tvhht.myapplication.goodsreceipt.model.FindImPartnerResponse
import com.tvhht.myapplication.login.model.LoginModel
import com.tvhht.myapplication.login.model.LoginResponse
import com.tvhht.myapplication.utils.ApiConstant
import com.tvhht.myapplication.utils.PrefConstant
import com.tvhht.myapplication.utils.WMSApplication
import com.tvhht.myapplication.utils.WMSSharedPref
import rx.Observable
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

class FindImPartnerViewModel : ViewModel() {
    var findImPartner: SingleLiveData<List<FindImPartnerResponse>> = SingleLiveData()
    var loaderLiveData: MutableLiveData<Boolean> = MutableLiveData()
    private var authTokenLiveData: MutableLiveData<String> = MutableLiveData()

    var itemCode: String = ""
    var businessPartnerCode: String = ""
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
                        authTokenLiveData.value = it
                        when (transactionId) {
                            ApiConstant.GR_FIND_IM_PARTNER_ID -> {
                                getIMPartner()
                            }
                        }
                    }
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
        apiService.findImPartner(authTokenLiveData.value ?: "", request)
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
}