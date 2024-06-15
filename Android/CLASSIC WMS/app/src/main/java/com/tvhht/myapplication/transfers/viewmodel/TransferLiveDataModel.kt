package com.tvhht.myapplication.transfers.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.tvhht.myapplication.api.ApiServices
import com.tvhht.myapplication.api.RetrofitBuilder
import com.tvhht.myapplication.api.error.NetworkState
import com.tvhht.myapplication.login.model.LoginModel
import com.tvhht.myapplication.login.model.LoginResponse
import com.tvhht.myapplication.transfers.model.*
import com.tvhht.myapplication.utils.ApiConstant
import com.tvhht.myapplication.utils.PrefConstant
import com.tvhht.myapplication.utils.WMSApplication
import com.tvhht.myapplication.utils.WMSSharedPref
import rx.Observable
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers


class TransferLiveDataModel : ViewModel() {
    private var strInventory: MutableLiveData<List<InventoryModel?>>? = null
    private var createTransfer: MutableLiveData<CreateTransferRequest?>? = null
    private var binTransfer: MutableLiveData<List<TargetBinResponse?>>? = null
    private val mState = MutableLiveData<NetworkState>()

    internal fun getInventoryList(listBin: List<String>): MutableLiveData<List<InventoryModel?>> {
        if (strInventory == null) {
            strInventory = MutableLiveData()
            loadInventoryDetails(listBin)
        }
        return strInventory as MutableLiveData<List<InventoryModel?>>
    }


    fun createDataToUpload(dataToApi: CreateTransferRequest): MutableLiveData<CreateTransferRequest?> {
        if (createTransfer == null) {
            createTransfer = MutableLiveData()
            createTransferRecords(dataToApi)
        }
        return createTransfer as MutableLiveData<CreateTransferRequest?>
    }


    fun getTargetBinData(dataToApi: SearchTargetBin): MutableLiveData<List<TargetBinResponse?>> {
        if (binTransfer == null) {
            binTransfer = MutableLiveData()
            checkTokenForTargetBin(dataToApi)
        }
        return binTransfer as MutableLiveData<List<TargetBinResponse?>>
    }


    private fun loadInventoryDetails(listBin: List<String>) {


        val apiService: ApiServices = RetrofitBuilder.apiService
        val apiKey: String =
            WMSSharedPref.getAppPrefs(WMSApplication.getInstance().context)
                .getStringValue(PrefConstant.SECRET_TOKEN)
        val stringValue = WMSSharedPref.getAppPrefs(
            WMSApplication.getInstance().context
        ).getStringValue(PrefConstant.WARE_HOUSE_ID)

        val requestList: MutableList<String> = ArrayList()
        requestList.add(stringValue)

        val searchgr = SearchInventory(listBin,requestList)
        apiService.findInventoryDetails(apiKey, searchgr)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .onErrorResumeNext { throwable -> Observable.error(throwable) }
            .subscribe(object : Subscriber<List<InventoryModel?>>() {
                override fun onCompleted() {}
                override fun onError(e: Throwable) {
//                    strAuthenticator?.postValue("ERROR")
                    e.printStackTrace()

                    Log.e("EEEEE", e.cause.toString())
                }

                override fun onNext(response: List<InventoryModel?>) {
                    if (response != null) {

                        strInventory?.postValue(response)
                    }
                }


            })
    }


    private fun createTransferRecords(dataToApi: CreateTransferRequest) {
        val apiService: ApiServices = RetrofitBuilder.apiService

        val apiKey: String =
            WMSSharedPref.getAppPrefs(WMSApplication.getInstance().context)
                .getStringValue(PrefConstant.SECRET_TOKEN)

        val loginID: String =
            WMSSharedPref.getAppPrefs(WMSApplication.getInstance().context)
                .getStringValue(PrefConstant.LOGIN_ID)

        apiService.createTransferInventory(
            apiKey,
            loginID,
            dataToApi
        )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .onErrorResumeNext { throwable -> Observable.error(throwable) }
            .subscribe(object : Subscriber<CreateTransferRequest?>() {
                override fun onCompleted() {}
                override fun onError(e: Throwable) {
//                    strAuthenticator?.postValue("ERROR")
                    e.printStackTrace()

                    Log.e("EEEEE", e.cause.toString())
                    createTransfer?.postValue(null)
                }

                override fun onNext(response: CreateTransferRequest?) {
                    if (response != null) {
//                        WMSApplication.getInstance().casesLocalRepository
//                            ?.insertListVo(response)

                        createTransfer?.postValue(response)
                    }
                }


            })
    }


    private fun checkTokenForTargetBin(
        mfrRequest: SearchTargetBin) {
        var request = LoginModel(
            ApiConstant.apiName_masters,
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
                override fun onCompleted() {}
                override fun onError(e: Throwable) {

                }

                override fun onNext(response: LoginResponse?) {
                    if (response != null) {
                        Log.d("Token==>", response.access_token)
                        WMSSharedPref.getAppPrefs(
                            WMSApplication.getInstance().context
                        ).saveStringValue(
                            PrefConstant.SECRET_TOKEN2,
                            response.access_token
                        )
                        checkTargetBin(mfrRequest)
                    }

                }


            })

    }



    private fun checkTargetBin(
        mfrRequest: SearchTargetBin) {

        val apiService: ApiServices = RetrofitBuilder.apiService

        val stringValue = WMSSharedPref.getAppPrefs(
            WMSApplication.getInstance().context
        ).getStringValue(
            PrefConstant.SECRET_TOKEN2
        )

        apiService.findTargetBin(stringValue,mfrRequest)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .onErrorResumeNext { throwable -> Observable.error(throwable) }
            .subscribe(object : Subscriber<List<TargetBinResponse?>>() {
                override fun onCompleted() {}
                override fun onError(e: Throwable) {

                    binTransfer!!.postValue(emptyList())

                }

                override fun onNext(response: List<TargetBinResponse?>) {
                    if (response != null) {
                        binTransfer!!.postValue(response)

                    }
                    else
                        binTransfer!!.postValue(emptyList())

                }


            })

    }

}