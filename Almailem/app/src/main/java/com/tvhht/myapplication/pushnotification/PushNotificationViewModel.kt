package com.tvhht.myapplication.pushnotification

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.tvhht.myapplication.api.ApiServices
import com.tvhht.myapplication.api.RetrofitBuilder
import com.tvhht.myapplication.login.model.LoginModel
import com.tvhht.myapplication.login.model.LoginResponse
import com.tvhht.myapplication.picking.viewmodel.SingleLiveData
import com.tvhht.myapplication.utils.ApiConstant
import com.tvhht.myapplication.utils.PrefConstant
import com.tvhht.myapplication.utils.WMSApplication
import com.tvhht.myapplication.utils.WMSSharedPref
import rx.Observable
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

class PushNotificationViewModel : ViewModel() {
    private var authTokenLiveData: MutableLiveData<String> = MutableLiveData()
    var fcmTokenLiveData: SingleLiveData<FCMTokenResponse> = SingleLiveData()
    fun getAuthToken(isUserLogin: Boolean) {
        val request = LoginModel(
            ApiConstant.apiName_id_master,
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
                }

                override fun onCompleted() {}
                override fun onError(e: Throwable) {
                }

                override fun onNext(response: LoginResponse?) {
                    response?.access_token?.let {
                        authTokenLiveData.value = it
                        sendFCMTokenToServer(isUserLogin)
                    }
                }
            })
    }

    fun sendFCMTokenToServer(isUserLogin: Boolean) {
        val apiService: ApiServices = RetrofitBuilder.apiService

        val loginId: String =
            WMSSharedPref.getAppPrefs(WMSApplication.getInstance().context)
                .getStringValue(PrefConstant.LOGIN_ID)

        val warehouseId: String =
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
        val fcmToken: String =
            WMSSharedPref.getAppPrefs(WMSApplication.getInstance().context)
                .getStringValue(PrefConstant.FCM_TOKEN)

        val request = FCMTokenRequest(
            companyCodeId,
            fcmToken,
            0,
            languageId,
            plantId,
            fcmToken,
            loginId,
            warehouseId,
            isUserLogin
        )

        apiService.sendFCMTokenToServer(authTokenLiveData.value ?:"", loginId, request)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .onErrorResumeNext { throwable -> Observable.error(throwable) }
            .subscribe(object : Subscriber<FCMTokenResponse>() {
                override fun onCompleted() {}
                override fun onError(e: Throwable) {

                }

                override fun onNext(response: FCMTokenResponse) {
                    fcmTokenLiveData.value = response
                }
            })
    }
}