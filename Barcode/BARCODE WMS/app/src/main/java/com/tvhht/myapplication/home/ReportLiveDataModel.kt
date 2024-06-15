package com.tvhht.myapplication.home

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.tvhht.myapplication.api.ApiServices
import com.tvhht.myapplication.api.RetrofitBuilder
import com.tvhht.myapplication.api.error.NetworkState
import com.tvhht.myapplication.home.model.*
import com.tvhht.myapplication.utils.PrefConstant
import com.tvhht.myapplication.utils.WMSApplication
import com.tvhht.myapplication.utils.WMSSharedPref
import org.json.JSONObject
import retrofit2.HttpException
import rx.Observable
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers


class ReportLiveDataModel : ViewModel() {

    private var strBarcodeResponser: MutableLiveData<String?>? = null
    private var strItemCodeResponse: MutableLiveData<List<String>?>? = null
    private var strItemDescriptionResponse: MutableLiveData<List<String>?>? = null
    private val mState = MutableLiveData<NetworkState>()


    internal fun getBarcodeStatus(dataToApi: BarcodeRequestBin): MutableLiveData<String?> {
        if (strBarcodeResponser == null) {
            strBarcodeResponser = MutableLiveData()
        }
        loadStatusBarcode(dataToApi)
        return strBarcodeResponser as MutableLiveData<String?>
    }

    internal fun getItemCode(dataToApi: String): MutableLiveData<List<String>?> {
        if (strItemCodeResponse == null) {
            strItemCodeResponse = MutableLiveData()
        }
        loadItemCode(dataToApi)
        return strItemCodeResponse as MutableLiveData<List<String>?>
    }

    internal fun getItemDescription(dataToApi: ManufactureRequest): MutableLiveData<List<String>?> {
        if (strItemDescriptionResponse == null) {
            strItemDescriptionResponse = MutableLiveData()
        }
        loadItemDescription(dataToApi)
        return strItemDescriptionResponse as MutableLiveData<List<String>?>
    }

    private fun loadStatusBarcode(dataToApi: BarcodeRequestBin) {
        val apiService: ApiServices = RetrofitBuilder.apiService

        val apiKey: String =
            WMSSharedPref.getAppPrefs(WMSApplication.getInstance().context)
                .getStringValue(PrefConstant.SECRET_TOKEN2)
        var loginID =
            WMSSharedPref.getAppPrefs(WMSApplication.getInstance().context)
                .getStringValue(PrefConstant.LOGIN_ID)
        apiService.getBarcodeStatus(apiKey, loginID, dataToApi)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .onErrorResumeNext { throwable -> Observable.error(throwable) }
            .subscribe(object : Subscriber<BarcodeResponse?>() {
                override fun onCompleted() {}
                override fun onError(throwable: Throwable) {
//                    strAuthenticator?.postValue("ERROR")
                    if (throwable is HttpException && (throwable!!.code() == 400 || throwable!!.code() == 404)) {
                        var responseBody = throwable!!.response()?.errorBody()?.string()
                        val jsonObject = JSONObject(responseBody!!.trim())
                        var message = jsonObject.getString("error")
                        strBarcodeResponser?.postValue(message)
                    } else {
                        strBarcodeResponser?.postValue("Unknown Server Exception")
                    }
                }

                override fun onNext(response: BarcodeResponse?) {
                    if (response != null) {
                        strBarcodeResponser?.postValue("success")
                    } else {
                        strBarcodeResponser?.postValue("Request Failed")
                    }
                }


            })
    }

    private fun loadItemCode(dataToApi: String) {
        val apiService: ApiServices = RetrofitBuilder.apiService

        val apiKey: String =
            WMSSharedPref.getAppPrefs(WMSApplication.getInstance().context)
                .getStringValue(PrefConstant.SECRET_TOKEN)
        var loginID =
            WMSSharedPref.getAppPrefs(WMSApplication.getInstance().context)
                .getStringValue(PrefConstant.LOGIN_ID)

        var plantID =
            WMSSharedPref.getAppPrefs(WMSApplication.getInstance().context)
                .getStringValue(PrefConstant.PLANT_ID)

        var langID =
            WMSSharedPref.getAppPrefs(WMSApplication.getInstance().context)
                .getStringValue(PrefConstant.LANGUAGE_ID)

        var companyCode =
            WMSSharedPref.getAppPrefs(WMSApplication.getInstance().context)
                .getStringValue(PrefConstant.COMPANY_ID)

        var wareID =
            WMSSharedPref.getAppPrefs(WMSApplication.getInstance().context)
                .getStringValue(PrefConstant.WARE_HOUSE_ID)
        apiService.getItemCodeLikes(apiKey, companyCode, langID, dataToApi, plantID, wareID)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .onErrorResumeNext { throwable -> Observable.error(throwable) }
            .subscribe(object : Subscriber<List<ItemCodeResponse>?>() {
                override fun onCompleted() {}
                override fun onError(e: Throwable) {
//                    strAuthenticator?.postValue("ERROR")
                    //strItemCodeResponse?.postValue(em)
                    strItemCodeResponse?.postValue(emptyList())
                    Log.e("EEEEE", e.cause.toString())
                }

                override fun onNext(response: List<ItemCodeResponse>?) {
                    var list = ArrayList<String>()
                    if (response != null) {
                        for (i in response) {
                            list.add(i.itemCode.toString())
                        }
                        strItemCodeResponse?.postValue(list)
                    } else {
                        strItemCodeResponse?.postValue(emptyList())
                    }
                }


            })
    }

    private fun loadItemDescription(dataToApi: ManufactureRequest) {
        val apiService: ApiServices = RetrofitBuilder.apiService

        val apiKey: String =
            WMSSharedPref.getAppPrefs(WMSApplication.getInstance().context)
                .getStringValue(PrefConstant.SECRET_TOKEN)

        apiService.getManufactureDetails(apiKey, dataToApi)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .onErrorResumeNext { throwable -> Observable.error(throwable) }
            .subscribe(object : Subscriber<List<ManufactureResponse>?>() {
                override fun onCompleted() {}
                override fun onError(e: Throwable) {
//                    strAuthenticator?.postValue("ERROR")
                    //strItemCodeResponse?.postValue(em)
                    strItemDescriptionResponse?.postValue(emptyList())
                    Log.e("EEEEE", e.cause.toString())
                }

                override fun onNext(response: List<ManufactureResponse>?) {
                    var list = ArrayList<String>()
                    if (response != null) {
                        for (i in response) {
                            list.add(i.manufacturerPartNo.toString())
                        }
                        strItemDescriptionResponse?.postValue(list)
                    } else {
                        strItemDescriptionResponse?.postValue(emptyList())
                    }
                }


            })
    }

}