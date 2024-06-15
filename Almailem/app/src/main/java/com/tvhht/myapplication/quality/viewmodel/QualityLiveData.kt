package com.tvhht.myapplication.quality.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tvhht.myapplication.api.ApiServices
import com.tvhht.myapplication.api.RetrofitBuilder
import com.tvhht.myapplication.login.model.LoginModel
import com.tvhht.myapplication.login.model.LoginResponse
import com.tvhht.myapplication.picking.viewmodel.SingleLiveData
import com.tvhht.myapplication.putaway.model.DescriptionModel
import com.tvhht.myapplication.putaway.model.SearchDescription
import com.tvhht.myapplication.quality.model.*
import com.tvhht.myapplication.utils.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import rx.Observable
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

class QualityLiveData : ViewModel() {
    private var qualityList: MutableLiveData<List<QualityListResponse>>? = null
    private var qtyList: MutableLiveData<List<QualityDetailsModel>>? = null
    private var qtySubmit: SingleLiveData<List<QualityResponse>>? = null

    internal fun getQualityList(): MutableLiveData<List<QualityListResponse>> {
        if (qualityList == null) {
            qualityList = MutableLiveData()
        }
        loadDetails()
        return qualityList as MutableLiveData<List<QualityListResponse>>
    }

    internal fun getQualityListNew(heNo: String,orderNo:String): MutableLiveData<List<QualityListResponse>> {
        if (qualityList == null) {
            qualityList = MutableLiveData()
        }
        getQA(heNo,orderNo)
        return qualityList as MutableLiveData<List<QualityListResponse>>
    }

    internal fun createData(dataToAPi: List<QualityModel>): SingleLiveData<List<QualityResponse>> {
        if (qtySubmit == null) {
            qtySubmit = SingleLiveData()
        }
        createDetails(dataToAPi)
        return qtySubmit as SingleLiveData<List<QualityResponse>>
    }


    internal fun getQtyList(dataToAPi: QualityDetailRequestModel): MutableLiveData<List<QualityDetailsModel>> {
        if (qtyList == null) {
            qtyList = MutableLiveData()
            loadQtyDetails(dataToAPi)
        }
        return qtyList as MutableLiveData<List<QualityDetailsModel>>
    }

    private fun loadDetails() {
        // do async operation to fetch users
        val apiService: ApiServices = RetrofitBuilder.apiService

        val apiKey: String =
            WMSSharedPref.getAppPrefs(WMSApplication.getInstance().context)
                .getStringValue(PrefConstant.SECRET_TOKEN)

        val companyCodeIdList: MutableList<String> = ArrayList()
        val languageIdList: MutableList<String> = ArrayList()
        val plantIdList: MutableList<String> = ArrayList()
        val warehouseIdList: MutableList<String> = ArrayList()
        val statusID: MutableList<Int> = ArrayList()

        val wareID: String =
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

        warehouseIdList.add(wareID)
        companyCodeIdList.add(companyCodeId)
        languageIdList.add(languageId)
        plantIdList.add(plantId)
        statusID.add(54)

        val userId = WMSSharedPref.getAppPrefs(
            WMSApplication.getInstance().context
        ).getStringValue(PrefConstant.LOGIN_ID)

        val requestData = QualityRequest(
            if (wareID == ApiConstant.WAREHOUSE_ID_200) null else companyCodeIdList,
            languageIdList,
            if (wareID == ApiConstant.WAREHOUSE_ID_200) null else plantIdList,
            warehouseIdList,
            statusID,
            userId
        )
        apiService.getQualityList(apiKey, requestData)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .onErrorResumeNext { throwable -> Observable.error(throwable) }
            .subscribe(object : Subscriber<List<QualityListResponse>>() {
                override fun onCompleted() {}
                override fun onError(e: Throwable) {
//                    strAuthenticator?.postValue("ERROR")
                    e.printStackTrace()

                    Log.e("EEEEE", e.cause.toString())
                }

                override fun onNext(response: List<QualityListResponse>) {
                    if (response != null) {

                        val wareID: String =
                            WMSSharedPref.getAppPrefs(WMSApplication.getInstance().context)
                                .getStringValue(PrefConstant.WARE_HOUSE_ID)
                        val arrayList2: List<QualityListResponse> =
                            response.filter {
                                (it.statusId == 54) && (it.warehouseId == wareID)
                            }
                        insertQA(arrayList2)
                        qualityList!!.postValue(response)
                    }
                }


            })

    }


    private fun insertQA(list: List<QualityListResponse>) {
        viewModelScope.launch {

            try {
                val dbHelper =
                    DatabaseHelperImpl(DatabaseBuilder.getInstance(WMSApplication.getInstance().applicationContext))
                dbHelper.deleteALL()
                dbHelper.insertAll(list)


            } catch (e: Exception) {

                e.printStackTrace()

            }
        }
    }


    private fun getQA(heNo: String,orderNo:String) {
        viewModelScope.launch {
            try {
                val dbHelper =
                    DatabaseHelperImpl(DatabaseBuilder.getInstance(WMSApplication.getInstance().applicationContext))
                val qaList = dbHelper.getQAList(heNo, orderNo)
                qualityList?.postValue(qaList)

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }


    private fun createDetails(dataToAPi: List<QualityModel>) {
        val apiService: ApiServices = RetrofitBuilder.apiService

        val apiKey: String =
            WMSSharedPref.getAppPrefs(WMSApplication.getInstance().context)
                .getStringValue(PrefConstant.SECRET_TOKEN)
        val loginID: String =
            WMSSharedPref.getAppPrefs(WMSApplication.getInstance().context)
                .getStringValue(PrefConstant.LOGIN_ID)

        apiService.createQuality(apiKey, loginID, dataToAPi).take(1)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .onErrorResumeNext { throwable -> Observable.error(throwable) }
            .subscribe(object : Subscriber<List<QualityResponse>>() {
                override fun onCompleted() {}
                override fun onError(e: Throwable) {
//                    strAuthenticator?.postValue("ERROR")
                    e.printStackTrace()

                    qtySubmit!!.postValue(emptyList())

                    Log.e("EEEEE", e.cause.toString())
                }

                override fun onNext(response: List<QualityResponse>) {
                    if (response != null) {
                        qtySubmit!!.postValue(response)
                    }
                }


            })
    }


    private fun loadQtyDetails(dataToAPi: QualityDetailRequestModel) {

        val apiService: ApiServices = RetrofitBuilder.apiService

        val apiKey: String =
            WMSSharedPref.getAppPrefs(WMSApplication.getInstance().context)
                .getStringValue(PrefConstant.SECRET_TOKEN)
        apiService.getQualityListDetails(apiKey, dataToAPi)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .onErrorResumeNext { throwable -> Observable.error(throwable) }
            .subscribe(object : Subscriber<List<QualityDetailsModel>>() {
                override fun onCompleted() {}
                override fun onError(e: Throwable) {
//                    strAuthenticator?.postValue("ERROR")
                    e.printStackTrace()

                    qtyList!!.postValue(emptyList())
                    Log.e("EEEEE", e.cause.toString())
                }

                override fun onNext(response: List<QualityDetailsModel>) {
                    if (response != null && response.isNotEmpty()) {
                        //qtyList!!.postValue(response)
                        val list = response as List<QualityDetailsModel>?

                        val requestList: MutableList<String> = ArrayList()
                        val requestList2: MutableList<String> = ArrayList()
                        val requestList3: MutableList<String> = ArrayList()

                        val stringValue = WMSSharedPref.getAppPrefs(
                            WMSApplication.getInstance().context
                        ).getStringValue(PrefConstant.WARE_HOUSE_ID)

                        val loginID: String =
                            WMSSharedPref.getAppPrefs(WMSApplication.getInstance().context)
                                .getStringValue(PrefConstant.LOGIN_ID)

                        for (i in list?.indices!!) {
                            requestList.add(list[i].itemCode.toString())
                            requestList2.add(stringValue)
                            requestList3.add(loginID)

                        }
                        val searchgr =
                            SearchDescription(requestList, requestList2, requestList3)
                        checkMfrDesp(searchgr, list)

                    } else {
                        qtyList?.postValue(emptyList())
                    }
                }


            })

    }


    private fun checkMfrDesp(
        mfrRequest: SearchDescription,
        list: List<QualityDetailsModel>
    ) {
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
                        loadMfrDesp(mfrRequest, list)
                    }

                }


            })

    }


    private fun loadMfrDesp(
        mfrRequest: SearchDescription,
        list: List<QualityDetailsModel>
    ) {
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


                    if (response != null && response.isNotEmpty()) {

                        val mfrlist = response as List<DescriptionModel>?
                        val qtList: MutableList<QualityDetailsModel> = ArrayList()



                        for (i in list?.indices!!) {

                            try {
                                val desAvailable = isDesAvailable(list[i].itemCode!!, mfrlist)!!
                                qtList.add(
                                    QualityDetailsModel(
                                        list[i].languageId,
                                        list[i].companyCodeId,
                                        list[i].plantId,
                                        list[i].warehouseId,
                                        list[i].preOutboundNo,
                                        list[i].refDocNumber,
                                        list[i].partnerCode,
                                        list[i].lineNumber,
                                        list[i].pickupNumber,
                                        list[i].pickConfirmQty,
                                        list[i].itemCode,
                                        list[i].actualHeNo,
                                        list[i].pickedStorageBin,
                                        list[i].pickedPackCode,
                                        list[i].variantCode,
                                        list[i].variantSubCode,
                                        list[i].batchSerialNumber,
                                        list[i].pickQty,
                                        list[i].pickUom,
                                        list[i].stockTypeId,
                                        list[i].specialStockIndicatorId,
                                        desAvailable.description,
                                        desAvailable.manufacturerPartNo,
                                        list[i].assignedPickerId,
                                        list[i].pickPalletCode,
                                        list[i].pickCaseCode,
                                        list[i].statusId,
                                        list[i].referenceField1,
                                        list[i].referenceField2,
                                        list[i].referenceField3,
                                        list[i].referenceField4,
                                        list[i].referenceField5,
                                        list[i].referenceField6,
                                        list[i].referenceField7,
                                        list[i].referenceField8,
                                        list[i].referenceField9,
                                        list[i].referenceField10,
                                        list[i].deletionIndicator,
                                        list[i].pickupCreatedBy,
                                        list[i].pickupCreatedOn,
                                        list[i].pickupConfirmedBy,
                                        list[i].pickupConfirmedOn,
                                        list[i].pickUpUpdatedBy,
                                        list[i].pickupUpdatedOn,
                                        list[i].pickupReversedBy,
                                        list[i].pickupReversedOn,
                                        list[i].pickedPackCode + "_" + i,
                                        "",
                                        list[i].isSelected


                                    )
                                )
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }

                        }
                        qtyList?.postValue(qtList)


                    } else {
                        qtyList?.postValue(emptyList())
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
    fun deleteSingleHeNumber(heNumber: String, orderNo: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val dbHelper =
                    DatabaseHelperImpl(DatabaseBuilder.getInstance(WMSApplication.getInstance().applicationContext))
                dbHelper.deleteSingleHeNumber(heNumber, orderNo)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}