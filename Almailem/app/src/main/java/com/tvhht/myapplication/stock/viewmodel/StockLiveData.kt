package com.tvhht.myapplication.stock.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.tvhht.myapplication.annual.model.AnnualDetailHeader
import com.tvhht.myapplication.annual.model.AnnualDetailHeader1
import com.tvhht.myapplication.annual.model.AnnualHeader
import com.tvhht.myapplication.api.ApiServices
import com.tvhht.myapplication.api.RetrofitBuilder
import com.tvhht.myapplication.login.model.LoginModel
import com.tvhht.myapplication.login.model.LoginResponse
import com.tvhht.myapplication.putaway.model.DescriptionModel
import com.tvhht.myapplication.putaway.model.SearchDescription
import com.tvhht.myapplication.quality.model.*
import com.tvhht.myapplication.stock.model.*
import com.tvhht.myapplication.utils.ApiConstant
import com.tvhht.myapplication.utils.PrefConstant
import com.tvhht.myapplication.utils.WMSApplication
import com.tvhht.myapplication.utils.WMSSharedPref
import retrofit2.Response
import rx.Observable
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

class StockLiveData : ViewModel() {
    private var stockList: MutableLiveData<List<PerpetualResponse?>>? = null
    private var createList: MutableLiveData<String?>? = null
    private var updatedstockList: MutableLiveData<List<PerpetualLine>?>? = null


    internal fun getPerpetualList(dataToApi: AnnualHeader): MutableLiveData<List<PerpetualResponse>> {
        if (stockList == null) {
            stockList = MutableLiveData()
            loadDetails(dataToApi)
        }
        return stockList as MutableLiveData<List<PerpetualResponse>>
    }


    internal fun getUpdated(dataToApi: AnnualDetailHeader): MutableLiveData<List<PerpetualLine>> {
        if (updatedstockList == null) {
            updatedstockList = MutableLiveData()
            vmloadDetails(dataToApi)
        }
        return updatedstockList as MutableLiveData<List<PerpetualLine>>
    }


    internal fun createRecords(dataToApi: StockPerpetualRequest): MutableLiveData<String?> {
        if (createList == null) {
            createList = MutableLiveData()
            createRecord(dataToApi)
        }
        return createList as MutableLiveData<String?>
    }



    private fun loadDetails(dataToApi: AnnualHeader) {
        // do async operation to fetch users
        val apiService: ApiServices = RetrofitBuilder.apiService

        val apiKey: String =
            WMSSharedPref.getAppPrefs(WMSApplication.getInstance().context)
                .getStringValue(PrefConstant.SECRET_TOKEN)
        apiService.findPerpetualStockList(apiKey, dataToApi)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .onErrorResumeNext { throwable -> Observable.error(throwable) }
            .subscribe(object : Subscriber<List<PerpetualResponse?>>() {
                override fun onCompleted() {}
                override fun onError(e: Throwable) {
//                    strAuthenticator?.postValue("ERROR")
                    e.printStackTrace()

                    Log.e("EEEEE", e.cause.toString())
                    stockList!!.postValue(emptyList())
                }

                override fun onNext(response: List<PerpetualResponse?>) {

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


    private fun vmloadDetails(dataToApi: AnnualDetailHeader) {
        // do async operation to fetch users
        val apiService: ApiServices = RetrofitBuilder.apiService

        val apiKey: String =
            WMSSharedPref.getAppPrefs(WMSApplication.getInstance().context)
                .getStringValue(PrefConstant.SECRET_TOKEN)
        apiService.findPerpetualStockDetailList(apiKey, dataToApi)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .onErrorResumeNext { throwable -> Observable.error(throwable) }
            .subscribe(object : Subscriber<List<PerpetualLine>>() {
                override fun onCompleted() {}
                override fun onError(e: Throwable) {
//                    strAuthenticator?.postValue("ERROR")
                    e.printStackTrace()

                    Log.e("EEEEE", e.cause.toString())
                    updatedstockList?.postValue(emptyList())
                }

                override fun onNext(response: List<PerpetualLine>) {

                    try {
                        if (response != null) {
                            if (response.isNotEmpty()) {

                                val requestList: MutableList<String> = ArrayList()
                                val requestList2: MutableList<String> = ArrayList()
                                val requestList3: MutableList<String> = ArrayList()
                                val stringValue = WMSSharedPref.getAppPrefs(
                                    WMSApplication.getInstance().context
                                ).getStringValue(PrefConstant.WARE_HOUSE_ID)

                                val loginID = WMSSharedPref.getAppPrefs(
                                    WMSApplication.getInstance().context
                                ).getStringValue(PrefConstant.LOGIN_ID)

                                for (i in response?.indices!!) {
                                    requestList.add(response[i]?.itemCode.toString())
                                    requestList2.add(stringValue)
                                    requestList3.add(loginID)
                                }

                                val searchgr =
                                    SearchDescription(requestList, requestList2, requestList3)
                                checkMfrDesp(searchgr, response)
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


    private fun createRecord(dataToApi: StockPerpetualRequest) {
        // do async operation to fetch users
        val apiService: ApiServices = RetrofitBuilder.apiService
        val loginID: String =
            WMSSharedPref.getAppPrefs(WMSApplication.getInstance().context)
                .getStringValue(PrefConstant.LOGIN_ID)
        val apiKey: String =
            WMSSharedPref.getAppPrefs(WMSApplication.getInstance().context)
                .getStringValue(PrefConstant.SECRET_TOKEN)

        val companyCodeId = WMSSharedPref.getAppPrefs(
            WMSApplication.getInstance().context
        ).getStringValue(PrefConstant.COMPANY_CODE_ID)
        val plantId = WMSSharedPref.getAppPrefs(
            WMSApplication.getInstance().context
        ).getStringValue(PrefConstant.PLANT_ID)
        val languageId = WMSSharedPref.getAppPrefs(
            WMSApplication.getInstance().context
        ).getStringValue(PrefConstant.LANGUAGE_ID)

        apiService.updatePerpetualStockList(
            dataToApi.cycleCountNo,
            apiKey,
            1,
            loginID,
            1,
            1,
            dataToApi.warehouseId,
            companyCodeId,
            plantId,
            languageId,
            dataToApi
        )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .onErrorResumeNext { throwable -> Observable.error(throwable) }
            .subscribe(object : Subscriber<Response<Unit>??>() {
                override fun onCompleted() {}
                override fun onError(e: Throwable) {
//                    strAuthenticator?.postValue("ERROR")
                    e.printStackTrace()
                    createList!!.postValue(null)
                    Log.e("EEEEE", e.cause.toString())
                }

                override fun onNext(response: Response<Unit>??) {
                    if (response != null&& response.code()==200) {
                        createList!!.postValue("YES")
                    } else {
                        createList!!.postValue(null)

                    }

                }


            })

    }


    private fun checkMfrDesp(mfrRequest: SearchDescription, list: List<PerpetualLine>) {
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
                        loadMfrDesp(mfrRequest, list!!)
                    }

                }


            })

    }


    private fun loadMfrDesp(mfrRequest: SearchDescription, list: List<PerpetualLine>) {
        val apiService: ApiServices = RetrofitBuilder.apiService

        val apiKey: String =
            WMSSharedPref.getAppPrefs(WMSApplication.getInstance().context)
                .getStringValue(PrefConstant.SECRET_TOKEN2)


        apiService.findItemSescription(apiKey, mfrRequest)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .onErrorResumeNext { throwable -> Observable.error(throwable) }
            .subscribe(object : Subscriber<List<DescriptionModel?>>() {
                override fun onCompleted() {}
                override fun onError(e: Throwable) {
//                    strAuthenticator?.postValue("ERROR")
                    e.printStackTrace()
                    Log.e("EEEEE", e.cause.toString())
                }

                override fun onNext(response: List<DescriptionModel?>) {


                    if (response != null) {

                        val mfrlist = response as List<DescriptionModel>?
                        val updatedList: MutableList<PerpetualLine> = ArrayList()


                        for (i in list?.indices!!) {

                            try {
                                val desAvailable = isDesAvailable(list[i].itemCode!!, mfrlist)!!

                                list[i].itemDesc= desAvailable.description
                                list[i].manufacturerPartNo=  desAvailable!!.manufacturerPartNo

                                updatedList.add(list[i]);
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }

                        }
                        updatedstockList?.postValue(updatedList)


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