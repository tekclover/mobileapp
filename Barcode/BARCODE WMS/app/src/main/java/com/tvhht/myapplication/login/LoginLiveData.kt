package com.tvhht.myapplication.login

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.tvhht.myapplication.R
import com.tvhht.myapplication.api.ApiServices
import com.tvhht.myapplication.api.RetrofitBuilder
import com.tvhht.myapplication.api.error.NetworkState
import com.tvhht.myapplication.home.model.DashBoardReportCount
import com.tvhht.myapplication.login.model.*
import com.tvhht.myapplication.utils.PrefConstant
import com.tvhht.myapplication.utils.WMSApplication
import com.tvhht.myapplication.utils.WMSSharedPref

import rx.Observable
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import java.util.concurrent.TimeUnit


class LoginLiveData : ViewModel() {
    private var strAuthenticator: MutableLiveData<String>? = null
    private var strAuthenticator2: MutableLiveData<String>? = null
    private var userStatus: MutableLiveData<List<UserDetails>>? = null
    private var warehoueList: MutableLiveData<List<WarehouseModel>>? = null
    private var homeCount: MutableLiveData<DashBoardReportCount>? = null
    private val mState = MutableLiveData<NetworkState>()

    internal fun getLoginStatus(request: LoginModel): MutableLiveData<String> {
        if (strAuthenticator == null) {
            strAuthenticator = MutableLiveData()
            loadStatus(request)
        }

        return strAuthenticator as MutableLiveData<String>
    }

    internal fun getAuthStatus(request: LoginModel): MutableLiveData<String> {
        if (strAuthenticator2 == null) {
            strAuthenticator2 = MutableLiveData()
            loadStatusNew(request)
        }

        return strAuthenticator2 as MutableLiveData<String>
    }


    internal fun getLoginHome(
        request: LoginModel,
        id: String,
        key: String
    ): MutableLiveData<String> {
        if (strAuthenticator == null) {
            strAuthenticator = MutableLiveData()
            loadStatusHome(request, id, key)
        }
        return strAuthenticator as MutableLiveData<String>
    }


    internal fun getLoginHomeNew(
        request: LoginModel,
        id: String,
        key: String
    ): MutableLiveData<String> {
        if (strAuthenticator == null) {
            strAuthenticator = MutableLiveData()
            loadLogin(id, key)
        }
        return strAuthenticator as MutableLiveData<String>
    }


    internal fun getUserHomeCount(): MutableLiveData<DashBoardReportCount> {
        if (homeCount == null) {
            homeCount = MutableLiveData()
            loadHomeCount()
        }
        return homeCount as MutableLiveData<DashBoardReportCount>
    }


    internal fun getUserStatus(): MutableLiveData<List<UserDetails>> {
        if (userStatus == null) {
            userStatus = MutableLiveData()
            loadUserDetails()
        }
        return userStatus as MutableLiveData<List<UserDetails>>
    }


    internal fun getWarehouseList(warehouseId: String): MutableLiveData<List<WarehouseModel>> {
        if (warehoueList == null) {
            warehoueList = MutableLiveData()
            loadWareHouseID(warehouseId)
        }
        return warehoueList as MutableLiveData<List<WarehouseModel>>
    }


    private fun loadStatusHome(request: LoginModel, id: String, key: String) {

        val apiService: ApiServices = RetrofitBuilder.apiService

        apiService.checkUser(request)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .onErrorResumeNext { throwable -> Observable.error(throwable) }
            .subscribe(object : Subscriber<LoginResponse?>() {
                override fun onCompleted() {}
                override fun onError(e: Throwable) {
                    strAuthenticator?.postValue("ERROR")
                }

                override fun onNext(response: LoginResponse?) {
                    if (response != null) {//

                        Log.d("Token==>", response.access_token)
                        WMSSharedPref.getAppPrefs(
                            WMSApplication.getInstance().context
                        ).saveStringValue(
                            PrefConstant.SECRET_TOKEN,
                            response.access_token
                        )

                        WMSSharedPref.getAppPrefs(
                            WMSApplication.getInstance().context
                        ).saveStringValue(
                            PrefConstant.REFRESH_TOKEN,
                            response.refresh_token
                        )

                        loadLogin(id, key)


                    } else {
                        WMSSharedPref.getAppPrefs(
                            WMSApplication.getInstance().context
                        ).saveBooleanValue(
                            PrefConstant.LOGIN_STATUS,
                            false
                        )

                        strAuthenticator?.postValue("FAILED")
                    }

                }


            })
    }


    private fun loadStatus(request: LoginModel) {

        val apiService: ApiServices = RetrofitBuilder.apiService

        apiService.checkUser(request)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .onErrorResumeNext { throwable -> Observable.error(throwable) }
            .subscribe(object : Subscriber<LoginResponse?>() {
                override fun onCompleted() {}
                override fun onError(e: Throwable) {
                    strAuthenticator?.postValue("ERROR")
                }

                override fun onNext(response: LoginResponse?) {
                    if (response != null) {//
                        strAuthenticator?.postValue(response.access_token)
                        Log.d("Token==>", response.access_token)
                        WMSSharedPref.getAppPrefs(
                            WMSApplication.getInstance().context
                        ).saveStringValue(
                            PrefConstant.SECRET_TOKEN,
                            response.access_token
                        )

                        WMSSharedPref.getAppPrefs(
                            WMSApplication.getInstance().context
                        ).saveStringValue(
                            PrefConstant.REFRESH_TOKEN,
                            response.refresh_token
                        )


                    } else {
                        WMSSharedPref.getAppPrefs(
                            WMSApplication.getInstance().context
                        ).saveBooleanValue(
                            PrefConstant.LOGIN_STATUS,
                            false
                        )

                        strAuthenticator?.postValue("FAILED")
                    }

                }


            })
    }


    private fun loadStatusNew(request: LoginModel) {

        val apiService: ApiServices = RetrofitBuilder.apiService

        apiService.checkUser(request)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .onErrorResumeNext { throwable -> Observable.error(throwable) }
            .subscribe(object : Subscriber<LoginResponse?>() {
                override fun onCompleted() {}
                override fun onError(e: Throwable) {
                    strAuthenticator2?.postValue("ERROR")
                }

                override fun onNext(response: LoginResponse?) {
                    if (response != null) {//
                        strAuthenticator2?.postValue(response.access_token)
                        Log.d("Token==>", response.access_token)
                        WMSSharedPref.getAppPrefs(
                            WMSApplication.getInstance().context
                        ).saveStringValue(
                            PrefConstant.SECRET_TOKEN2,
                            response.access_token
                        )

                        WMSSharedPref.getAppPrefs(
                            WMSApplication.getInstance().context
                        ).saveStringValue(
                            PrefConstant.REFRESH_TOKEN,
                            response.refresh_token
                        )


                    } else {
                        WMSSharedPref.getAppPrefs(
                            WMSApplication.getInstance().context
                        ).saveBooleanValue(
                            PrefConstant.LOGIN_STATUS,
                            false
                        )

                        strAuthenticator2?.postValue("FAILED")
                    }

                }


            })
    }


    private fun loadLogin(username: String, passkey: String) {
        val apiService: ApiServices = RetrofitBuilder.apiService
        val apiKey: String =
            WMSSharedPref.getAppPrefs(WMSApplication.getInstance().context)
                .getStringValue(PrefConstant.SECRET_TOKEN)
        val version= WMSApplication.getInstance().context.resources.getString(R.string.app_version);
        apiService.getLoginDetails(apiKey, username, passkey,"")
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .onErrorResumeNext { throwable -> Observable.error(throwable) }
            .subscribe(object : Subscriber<UserLoginResponse>() {
                override fun onCompleted() {}
                override fun onError(e: Throwable) {
//                    strAuthenticator?.postValue("ERROR")
                    e.printStackTrace()
                    Log.e("EEEEE", e.cause.toString())
                    strAuthenticator?.postValue("ERROR")
                }

                override fun onNext(response: UserLoginResponse) {
                    if (response != null) {

                        WMSSharedPref.getAppPrefs(
                            WMSApplication.getInstance().context
                        ).saveBooleanValue(
                            PrefConstant.LOGIN_STATUS,
                            true
                        )

                        WMSSharedPref.getAppPrefs(
                            WMSApplication.getInstance().context
                        ).saveStringValue(PrefConstant.LOGIN_ID, response?.userId)

                        WMSSharedPref.getAppPrefs(
                            WMSApplication.getInstance().context
                        ).saveStringValue(PrefConstant.PLANT_ID, response?.plantId)

                        WMSSharedPref.getAppPrefs(
                            WMSApplication.getInstance().context
                        ).saveStringValue(PrefConstant.LANGUAGE_ID, response?.languageId)

                        WMSSharedPref.getAppPrefs(
                            WMSApplication.getInstance().context
                        ).saveStringValue(PrefConstant.COMPANY_ID, response?.companyCode)

                        WMSSharedPref.getAppPrefs(
                            WMSApplication.getInstance().context
                        ).saveStringValue(PrefConstant.WARE_HOUSE_ID, response?.warehouseId)

                        strAuthenticator?.postValue(apiKey)
                    }
                }

            })
    }


    private fun loadWareHouseID(warehouseId: String) {
        val apiService: ApiServices = RetrofitBuilder.apiService

        val apiKey: String =
            WMSSharedPref.getAppPrefs(WMSApplication.getInstance().context)
                .getStringValue(PrefConstant.SECRET_TOKEN)
        val stringUserID = WMSSharedPref.getAppPrefs(
            WMSApplication.getInstance().context
        ).getStringValue(PrefConstant.LOGIN_ID)
        apiService.getWarehousseDetails(stringUserID, apiKey, warehouseId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .onErrorResumeNext { throwable -> Observable.error(throwable) }
            .subscribe(object : Subscriber<WarehouseModel?>() {
                override fun onCompleted() {}
                override fun onError(e: Throwable) {
//                    strAuthenticator?.postValue("ERROR")
                    e.printStackTrace()
                    Log.e("EEEEE", e.cause.toString())
                }

                override fun onNext(response: WarehouseModel?) {
                    if (response != null) {

                        var responseList: MutableList<WarehouseModel> = ArrayList();
                        responseList.add(response)

                        warehoueList?.postValue(responseList)
                    }
                }

            })
    }


    private fun loadHomeCount() {
        val apiService: ApiServices = RetrofitBuilder.apiService
        val apiKey: String =
            WMSSharedPref.getAppPrefs(WMSApplication.getInstance().context)
                .getStringValue(PrefConstant.SECRET_TOKEN)
        val warehouseId = WMSSharedPref.getAppPrefs(
            WMSApplication.getInstance().context
        ).getStringValue(PrefConstant.WARE_HOUSE_ID)
        apiService.getDasboardCount(apiKey, warehouseId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .onErrorResumeNext { throwable -> Observable.error(throwable) }
            .subscribe(object : Subscriber<DashBoardReportCount>() {
                override fun onCompleted() {}
                override fun onError(e: Throwable) {
//                    strAuthenticator?.postValue("ERROR")
                    e.printStackTrace()
                    Log.e("EEEEE", e.cause.toString())
                }

                override fun onNext(response: DashBoardReportCount) {
                    if (response != null) {
                        homeCount?.postValue(response)
                    }
                }

            })
    }


    private fun loadUserDetails() {
        val apiService: ApiServices = RetrofitBuilder.apiService

        val apiKey: String =
            WMSSharedPref.getAppPrefs(WMSApplication.getInstance().context)
                .getStringValue(PrefConstant.SECRET_TOKEN)
        apiService.getUserDetails(apiKey)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .onErrorResumeNext { throwable -> Observable.error(throwable) }
            .subscribe(object : Subscriber<List<UserDetails?>>() {
                override fun onCompleted() {}
                override fun onError(e: Throwable) {
//                    strAuthenticator?.postValue("ERROR")
                    e.printStackTrace()
                    Log.e("EEEEE", e.cause.toString())
                }

                override fun onNext(response: List<UserDetails?>) {
                    if (response != null) {
                        userStatus?.postValue(response as List<UserDetails>?)
                    }
                }

            })
    }

}