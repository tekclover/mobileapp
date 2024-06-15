package com.tvhht.myapplication.annual.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.tvhht.myapplication.annual.model.*
import com.tvhht.myapplication.api.ApiServices
import com.tvhht.myapplication.api.RetrofitBuilder
import com.tvhht.myapplication.login.model.LoginModel
import com.tvhht.myapplication.login.model.LoginResponse
import com.tvhht.myapplication.putaway.model.DescriptionModel
import com.tvhht.myapplication.putaway.model.SearchDescription
import com.tvhht.myapplication.stock.model.*
import com.tvhht.myapplication.utils.ApiConstant
import com.tvhht.myapplication.utils.PrefConstant
import com.tvhht.myapplication.utils.WMSApplication
import com.tvhht.myapplication.utils.WMSSharedPref
import rx.Observable
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

class AnnualLiveData : ViewModel() {
    private var stockList: MutableLiveData<List<AnnualStockResponse?>>? = null
    private var stockListDetail: MutableLiveData<List<AnnualStockResponse?>>? = null
    private var createList: MutableLiveData<AnnualStockResponse?>? = null
    private var updatedstockList: MutableLiveData<List<PeriodicLine>?>? = null


    internal fun getAnnualList(dataToApi: AnnualHeader): MutableLiveData<List<AnnualStockResponse>> {
        if (stockList == null) {
            stockList = MutableLiveData()
            loadDetails(dataToApi)
        }
        return stockList as MutableLiveData<List<AnnualStockResponse>>
    }


    internal fun getAnnualListDetails(dataToApi: AnnualDetailHeader1): MutableLiveData<List<PeriodicLine>> {
        if (updatedstockList == null) {
            updatedstockList = MutableLiveData()
        }

        vdloadDetails(dataToApi)
        return updatedstockList as MutableLiveData<List<PeriodicLine>>
    }


    internal fun createRecords(dataToApi: AnnualStockRequest): MutableLiveData<AnnualStockResponse> {
        if (createList == null) {
            createList = MutableLiveData()
            createRecord(dataToApi)
        }
        return createList as MutableLiveData<AnnualStockResponse>
    }


    private fun loadDetails(dataToApi: AnnualHeader) {
        // do async operation to fetch users
        val apiService: ApiServices = RetrofitBuilder.apiService

        val apiKey: String =
            WMSSharedPref.getAppPrefs(WMSApplication.getInstance().context)
                .getStringValue(PrefConstant.SECRET_TOKEN)
        apiService.findAnnualStockList(apiKey, dataToApi)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .onErrorResumeNext { throwable -> Observable.error(throwable) }
            .subscribe(object : Subscriber<List<AnnualStockResponse?>>() {
                override fun onCompleted() {}
                override fun onError(e: Throwable) {
//                    strAuthenticator?.postValue("ERROR")
                    e.printStackTrace()

                    Log.e("EEEEE", e.cause.toString())
                    stockList!!.postValue(emptyList())
                }

                override fun onNext(response: List<AnnualStockResponse?>) {

                    try {
                        if (response != null) {
                            stockList!!.postValue(response)
                        }
                    } catch (excep: Exception) {
                        excep.printStackTrace()
                    }
                }


            })

    }


    private fun vdloadDetails(dataToApi: AnnualDetailHeader1) {
        // do async operation to fetch users
        val apiService: ApiServices = RetrofitBuilder.apiService

        val apiKey: String =
            WMSSharedPref.getAppPrefs(WMSApplication.getInstance().context)
                .getStringValue(PrefConstant.SECRET_TOKEN)
        apiService.findAnnualStockListDetails(apiKey, dataToApi)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .onErrorResumeNext { throwable -> Observable.error(throwable) }
            .subscribe(object : Subscriber<List<PeriodicLine>>() {
                override fun onCompleted() {}
                override fun onError(e: Throwable) {
//                    strAuthenticator?.postValue("ERROR")
                    e.printStackTrace()
                    updatedstockList?.postValue(emptyList())
                    Log.e("EEEEE", e.cause.toString())
                }

                override fun onNext(response: List<PeriodicLine>?) {

                    try {
                        if (response != null) {


                            if (response.isNotEmpty()) {


                                updatedstockList?.postValue(response)
                            } else {
                                updatedstockList?.postValue(emptyList())
                            }


                        }
                    } catch (excep: Exception) {
                        excep.printStackTrace()
                    }
                }

            })

    }


    private fun createRecord(dataToApi: AnnualStockRequest) {
        // do async operation to fetch users
        val apiService: ApiServices = RetrofitBuilder.apiService
        val loginID: String =
            WMSSharedPref.getAppPrefs(WMSApplication.getInstance().context)
                .getStringValue(PrefConstant.LOGIN_ID)
        val apiKey: String =
            WMSSharedPref.getAppPrefs(WMSApplication.getInstance().context)
                .getStringValue(PrefConstant.SECRET_TOKEN)
        apiService.updateAnnualStockList(
            dataToApi.cycleCountNo,
            apiKey,
            1,
            loginID,
            dataToApi.warehouseId,
            dataToApi
        )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .onErrorResumeNext { throwable -> Observable.error(throwable) }
            .subscribe(object : Subscriber<AnnualStockResponse?>() {
                override fun onCompleted() {}
                override fun onError(e: Throwable) {
//                    strAuthenticator?.postValue("ERROR")
                    e.printStackTrace()
                    createList!!.postValue(null)
                    Log.e("EEEEE", e.cause.toString())
                }

                override fun onNext(response: AnnualStockResponse?) {
                    if (response != null) {
                        createList!!.postValue(response)
                    } else {
                        createList!!.postValue(null)

                    }

                }


            })

    }




    private fun isDesAvailable(
        itemCode: String,
        mfrList: List<DescriptionModel>?
    ): DescriptionModel? {
        val find = mfrList!!.find { it.itemCode == itemCode }
        if (find != null) {
            return find
        } else {
            return null
        }


    }


}