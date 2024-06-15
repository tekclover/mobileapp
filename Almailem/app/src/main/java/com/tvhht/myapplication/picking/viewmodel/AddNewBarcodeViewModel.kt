package com.tvhht.myapplication.picking.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.tvhht.myapplication.api.ApiServices
import com.tvhht.myapplication.api.RetrofitBuilder
import com.tvhht.myapplication.goodsreceipt.model.ErrorResponse
import com.tvhht.myapplication.login.model.LoginModel
import com.tvhht.myapplication.login.model.LoginResponse
import com.tvhht.myapplication.picking.model.AddNewBarcodeRequest
import com.tvhht.myapplication.picking.model.AddNewBarcodeResponse
import com.tvhht.myapplication.utils.ApiConstant
import com.tvhht.myapplication.utils.PrefConstant
import com.tvhht.myapplication.utils.WMSApplication
import com.tvhht.myapplication.utils.WMSSharedPref
import retrofit2.HttpException
import rx.Observable
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

class AddNewBarcodeViewModel : ViewModel() {
    var authToken: MutableLiveData<String> = MutableLiveData()
    var updateBarcodeLiveData: MutableLiveData<AddNewBarcodeResponse> = MutableLiveData()
    var loaderLiveData: MutableLiveData<Boolean> = MutableLiveData()
    var errorMessage: SingleLiveData<Boolean> = SingleLiveData()

    var barcode = ""

    fun getAuthToken(apiName: String) {
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
                        updateNewBarcode()
                    }
                }
            })
    }

    private fun updateNewBarcode() {
        val apiService: ApiServices = RetrofitBuilder.apiService
        val pickingInfo = WMSSharedPref.getAppPrefs(
            WMSApplication.getInstance().context
        ).pickingInfo!!

        val userId: String =
            WMSSharedPref.getAppPrefs(WMSApplication.getInstance().context)
                .getStringValue(PrefConstant.LOGIN_ID)

        val request = AddNewBarcodeRequest(
            barcode,
            pickingInfo.companyCodeId ?: "",
            pickingInfo.itemCode ?: "",
            pickingInfo.languageId ?: "",
            userId?:"",
            pickingInfo.manufacturerName ?: "",
            pickingInfo.plantId ?: "",
            pickingInfo.warehouseId ?: ""
        )

        apiService.addNewBarcode(authToken.value ?: "", request)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .onErrorResumeNext { throwable -> Observable.error(throwable) }
            .subscribe(object : Subscriber<AddNewBarcodeResponse>() {
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

                override fun onNext(response: AddNewBarcodeResponse) {
                    response.let {
                        updateBarcodeLiveData.value = it
                    }
                }
            })
    }
}