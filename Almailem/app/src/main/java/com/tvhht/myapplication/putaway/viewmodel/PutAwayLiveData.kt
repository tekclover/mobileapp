package com.tvhht.myapplication.putaway.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.tvhht.myapplication.api.ApiServices
import com.tvhht.myapplication.api.RetrofitBuilder
import com.tvhht.myapplication.login.model.LoginModel
import com.tvhht.myapplication.login.model.LoginResponse
import com.tvhht.myapplication.picking.viewmodel.SingleLiveData
import com.tvhht.myapplication.putaway.model.*
import com.tvhht.myapplication.quality.model.QualityRequest
import com.tvhht.myapplication.utils.ApiConstant
import com.tvhht.myapplication.utils.PrefConstant
import com.tvhht.myapplication.utils.WMSApplication
import com.tvhht.myapplication.utils.WMSSharedPref
import rx.Observable
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

class PutAwayLiveData : ViewModel() {
    private var putawayListData: MutableLiveData<List<PutAwayCombineModel>>? = null
    private var strAuthenticator: SingleLiveData<PutAwayConfirmResponse?>? = null
    private var storageBinLiveData: MutableLiveData<StorageBinResponse?>? = null
    private var authTokenLiveData: MutableLiveData<String>? = null


    internal fun getPutAwayList(): MutableLiveData<List<PutAwayCombineModel>> {
        if (putawayListData == null) {
            putawayListData = MutableLiveData()
        }
        loadPutAwayList()
        return putawayListData as MutableLiveData<List<PutAwayCombineModel>>
    }


    internal fun submitPutAwayDetails(request: List<PutAwaySubmit>): SingleLiveData<PutAwayConfirmResponse> {
        if (strAuthenticator == null) {
            strAuthenticator = SingleLiveData()
            submitPutAway(request)
        }
        return strAuthenticator as SingleLiveData<PutAwayConfirmResponse>
    }
    internal fun authToken(): MutableLiveData<String> {
        if (authTokenLiveData == null) {
            authTokenLiveData = MutableLiveData()
        }
        getAuthToken()
        return authTokenLiveData as MutableLiveData<String>
    }

    internal fun binValidation(
        binStorage: String,
        apiKey: String
    ): MutableLiveData<StorageBinResponse?> {
        if (storageBinLiveData == null) {
            storageBinLiveData = MutableLiveData()
        }
        binStorage(binStorage, apiKey)
        return storageBinLiveData as MutableLiveData<StorageBinResponse?>
    }


    private fun loadPutAwayList() {

        val apiService: ApiServices = RetrofitBuilder.apiService

        val apiKey: String =
            WMSSharedPref.getAppPrefs(WMSApplication.getInstance().context)
                .getStringValue(PrefConstant.SECRET_TOKEN)
        val inboundID: MutableList<Int> = ArrayList()
        val warehouseIdList: MutableList<String> = ArrayList()
        val companyCodeIdList: MutableList<String> = ArrayList()
        val languageIdList: MutableList<String> = ArrayList()
        val plantIdList: MutableList<String> = ArrayList()
        val statusIdList: MutableList<Int> = ArrayList()

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
        statusIdList.add(19)
        statusIdList.add(21)
        /*inboundID.add(1)
        inboundID.add(3)
        inboundID.add(4)
        inboundID.add(5)*/
        val userId = WMSSharedPref.getAppPrefs(
            WMSApplication.getInstance().context
        ).getStringValue(PrefConstant.LOGIN_ID)

        val requestData = PutAwayHeader(
            if (wareID == ApiConstant.WAREHOUSE_ID_200) null else companyCodeIdList,
            languageIdList,
            if (wareID == ApiConstant.WAREHOUSE_ID_200) null else plantIdList,
            warehouseIdList,
            statusIdList,
            userId
        )

        apiService.getAllPutAwayNew(apiKey,requestData)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .onErrorResumeNext { throwable -> Observable.error(throwable) }
            .subscribe(object : Subscriber<List<PutAwayModel?>>() {
                override fun onCompleted() {}
                override fun onError(e: Throwable) {
//                    strAuthenticator?.postValue("ERROR")
                    e.printStackTrace()

                    Log.e("EEEEE", e.cause.toString())
                    putawayListData?.postValue(emptyList())
                }

                override fun onNext(response: List<PutAwayModel?>) {
                    if (response != null && response.isNotEmpty()) {
//                        putawayList?.postValue(response as List<PutAwayModel>?)
                        val list = response as List<PutAwayModel>?
                        val requestList: MutableList<String> = ArrayList()
                        val requestList1: MutableList<String> = ArrayList()
                        val requestList2: MutableList<String> = ArrayList()
//
//                        val ware_id = WMSSharedPref.getAppPrefs(
//                            WMSApplication.getInstance().context
//                        ).getStringValue(PrefConstant.WARE_HOUSE_ID)
//
//                        var filter_list1 =
//                            list!!.filter { (it.statusId == 19 || it.statusId == 21) && (it.inboundOrderTypeId == 1 || it.inboundOrderTypeId == 3) && it.warehouseId == ware_id.toInt() }

                        var filter_list =
                            list!!.distinctBy { it.packBarcodes}


                        for (i in filter_list?.indices!!) {
                            requestList.add(filter_list[i].packBarcodes.toString())
                            requestList1.add(filter_list[i].preInboundNo.toString())
                            requestList2.add(filter_list[i].refDocNumber.toString())
                        }

                        val searchgr = SearchGR(requestList, requestList1, requestList2);
                        loadMfr(searchgr, filter_list!!)

                    } else {
                        putawayListData?.postValue(emptyList())
                    }
                }


            })


    }


    private fun loadMfr(mfrRequest: SearchGR, putawayList1: List<PutAwayModel>) {
        val apiService: ApiServices = RetrofitBuilder.apiService

        val apiKey: String =
            WMSSharedPref.getAppPrefs(WMSApplication.getInstance().context)
                .getStringValue(PrefConstant.SECRET_TOKEN)


        apiService.findSearch(apiKey, mfrRequest)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .onErrorResumeNext { throwable -> Observable.error(throwable) }
            .subscribe(object : Subscriber<List<PutAwayMfrModel?>>() {
                override fun onCompleted() {}
                override fun onError(e: Throwable) {
//                    strAuthenticator?.postValue("ERROR")
                    e.printStackTrace()
                    Log.e("EEEEE", e.cause.toString())
                    putawayListData?.postValue(emptyList())
                }

                override fun onNext(response: List<PutAwayMfrModel?>) {


                    if (response != null) {

                        val mfrlist1 = response!! as List<PutAwayMfrModel>?
                        val putawayList: MutableList<PutAwayCombineModel> = ArrayList()
                        var mfrlist: MutableList<PutAwayMfrModel> = ArrayList()
                        var list: MutableList<PutAwayModel> = ArrayList()

                        mfrlist =
                            mfrlist1!!.sortedBy { it.packBarcodes } as MutableList<PutAwayMfrModel>
                        list =
                            putawayList1!!.sortedBy { it.packBarcodes } as MutableList<PutAwayModel>


                        for (i in list?.indices!!) {

                            try {

                                var mfrlist2=mfrlist.find { it.packBarcodes==list[i].packBarcodes.toString()&& it.preInboundNo==list[i].preInboundNo.toString()&&it.refDocNumber==list[i].refDocNumber.toString() }

                                if (mfrlist2==null) {
                                    putawayList.add(
                                        PutAwayCombineModel(
                                            list[i].languageId,
                                            list[i].companyCodeId,
                                            list[i].plantId,
                                            list[i].warehouseId,
                                            list[i].preInboundNo,
                                            list[i].refDocNumber,
                                            list[i].goodsReceiptNo,
                                            list[i].palletCode,
                                            list[i].caseCode,
                                            list[i].packBarcodes,list[i].barcodeId,
                                            list[i].inboundOrderTypeId,
                                            list[i].putAwayNumber,
                                            list[i].proposedStorageBin,
                                            list[i].putAwayQuantity,
                                            list[i].putAwayUom,
                                            list[i].strategyTypeId,
                                            list[i].strategyNo,
                                            list[i].proposedHandlingEquipment,
                                            list[i].assignedUserId,
                                            list[i].statusId,
                                            list[i].quantityType,
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
                                            list[i].createdBy,
                                            list[i].createdOn,
                                            list[i].confirmedBy,
                                            list[i].confirmedOn,
                                            list[i].updatedBy,
                                            list[i].updatedOn,
                                            0,
                                            "",
                                            "",
                                            0,
                                            "",
                                            0,
                                            0,
                                            "",
                                            "",
                                            "",
                                            "",
                                            "",
                                            "",
                                            "",
                                            0,
                                            "",
                                            "",
                                            0.0,
                                            "",
                                            "",
                                            0,
                                            "",
                                            0,
                                            0,
                                            "",
                                            0,
                                            "",
                                            0,
                                            "",
                                            0,
                                            0,
                                            "",
                                            "",
                                            list[i].inventoryQuantity,
                                            list[i].proposedStorageBin,
                                            0,
                                            list[i].companyCodeId.toString(),
                                            null,
                                            null,
                                            0.0,
                                            "",
                                            "",
                                            null,
                                            null,
                                            null,
                                            null,
                                            0.0,
                                            "",
                                            "",
                                            "",
                                            "",
                                            null,
                                            null,
                                            null,
                                            null,
                                            null,
                                            null,
                                            null
                                        )
                                    )
                                } else {
                                    putawayList.add(
                                        PutAwayCombineModel(
                                            list[i].languageId,
                                            list[i].companyCodeId,
                                            list[i].plantId,
                                            list[i].warehouseId,
                                            list[i].preInboundNo,
                                            list[i].refDocNumber,
                                            list[i].goodsReceiptNo,
                                            list[i].palletCode,
                                            list[i].caseCode,
                                            list[i].packBarcodes, list[i].barcodeId,
                                            list[i].inboundOrderTypeId,
                                            list[i].putAwayNumber,
                                            list[i].proposedStorageBin,
                                            list[i].putAwayQuantity,
                                            list[i].putAwayUom,
                                            list[i].strategyTypeId,
                                            list[i].strategyNo,
                                            list[i].proposedHandlingEquipment,
                                            list[i].assignedUserId,
                                            list[i].statusId,
                                            list[i].quantityType,
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
                                            list[i].createdBy,
                                            list[i].createdOn,
                                            list[i].confirmedBy,
                                            list[i].confirmedOn,
                                            list[i].updatedBy,
                                            list[i].updatedOn,
                                            mfrlist2.acceptedQty,
                                            mfrlist2.batchSerialNumber,
                                            mfrlist2.businessPartnerCode,
                                            mfrlist2.confirmedQty,
                                            mfrlist2.containerNo,
                                            mfrlist2.crossDockAllocationQty,
                                            mfrlist2.damageQty,
                                            mfrlist2.expiryDate,
                                            mfrlist2.grUom,
                                            mfrlist2.hsnCode,
                                            mfrlist2.invoiceNo,
                                            mfrlist2.itemBarcode,
                                            mfrlist2.itemCode,
                                            mfrlist2.itemDescription,
                                            mfrlist2.lineNo,
                                            mfrlist2.manufacturerDate,
                                            mfrlist2.manufacturerPartNo,
                                            mfrlist2.orderQty,
                                            mfrlist2.orderUom,
                                            mfrlist2.putAwayHandlingEquipment,
                                            mfrlist2.receiptQty,
                                            mfrlist2.referenceOrderNo,
                                            mfrlist2.referenceOrderQty,
                                            mfrlist2.remainingQty,
                                            mfrlist2.remarks,
                                            mfrlist2.specialStockIndicatorId,
                                            mfrlist2.specificationActual,
                                            mfrlist2.stockTypeId,
                                            mfrlist2.storageMethod,
                                            mfrlist2.storageQty,
                                            mfrlist2.variantCode,
                                            mfrlist2.variantSubCode,
                                            mfrlist2.variantType,
                                            list[i].inventoryQuantity,
                                            list[i].proposedStorageBin,
                                            0,
                                            list[i].companyCodeId.toString(),
                                            null,
                                            null,
                                            0.0,
                                            mfrlist2.manufacturerCode,
                                            mfrlist2.manufacturerName,
                                            null,
                                            null,
                                            null,
                                            null,
                                            0.0,
                                            mfrlist2.companyDescription,
                                            mfrlist2.plantDescription,
                                            mfrlist2.warehouseDescription,
                                            mfrlist2.statusDescription,
                                            null,
                                            null,
                                            null,
                                            null,
                                            null,
                                            null,
                                            null
                                        )
                                    )
                                }


                            } catch (e: Exception) {
                                e.printStackTrace()
                            }

                        }
                        putawayListData?.postValue(putawayList)


                    }

                }


            })

    }


    private fun submitPutAway(dataToApi: List<PutAwaySubmit>) {
        val apiService: ApiServices = RetrofitBuilder.apiService

        val apiKey: String =
            WMSSharedPref.getAppPrefs(WMSApplication.getInstance().context)
                .getStringValue(PrefConstant.SECRET_TOKEN)

        val loginID: String =
            WMSSharedPref.getAppPrefs(WMSApplication.getInstance().context)
                .getStringValue(PrefConstant.LOGIN_ID)

        apiService.createPutAway(apiKey, loginID, dataToApi).take(1)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .onErrorResumeNext { throwable -> Observable.error(throwable) }
            .subscribe(object : Subscriber<List<PutAwayConfirmResponse>>() {
                override fun onCompleted() {}
                override fun onError(e: Throwable) {
                    //    strAuthenticator?.postValue(emptyArray<PutAwayConfirmResponse>()[0])
                    e.printStackTrace()

                    Log.e("EEEEE", e.cause.toString())
                    strAuthenticator?.postValue(null)
                }

                override fun onNext(response: List<PutAwayConfirmResponse>) {
                    if (response != null) {
                        strAuthenticator?.postValue(response[0])
                    } else
                        strAuthenticator?.postValue(null)
                }


            })
    }
    private fun binStorage(binStorage: String,apiKey:String) {
        val apiService: ApiServices = RetrofitBuilder.apiService

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
        apiService.findStorageBin(
            binStorage,
            apiKey,
            companyCodeId,
            languageId,
            wareId,
            plantId
        )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .onErrorResumeNext { throwable -> Observable.error(throwable) }
            .subscribe(object : Subscriber<StorageBinResponse>() {
                override fun onCompleted() {}
                override fun onError(e: Throwable) {
                    storageBinLiveData?.postValue(null)
                }

                override fun onNext(response: StorageBinResponse) {
                    storageBinLiveData?.postValue(response)
                }
            })
    }
    private fun getAuthToken() {
        val request = LoginModel(
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
                    authTokenLiveData?.postValue("")
                }

                override fun onNext(response: LoginResponse?) {
                    response?.access_token?.let {
                       authTokenLiveData?.postValue(it)
                    }
                }
            })
    }
}

