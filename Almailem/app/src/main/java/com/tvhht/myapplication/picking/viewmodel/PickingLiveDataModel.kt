package com.tvhht.myapplication.picking.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.tvhht.myapplication.picking.model.BinResponse
import com.tvhht.myapplication.api.ApiServices
import com.tvhht.myapplication.api.RetrofitBuilder
import com.tvhht.myapplication.api.error.NetworkState
import com.tvhht.myapplication.goodsreceipt.model.ErrorResponse
import com.tvhht.myapplication.login.model.LoginModel
import com.tvhht.myapplication.login.model.LoginResponse
import com.tvhht.myapplication.picking.model.*
import com.tvhht.myapplication.putaway.model.DescriptionModel
import com.tvhht.myapplication.putaway.model.SearchDescription
import com.tvhht.myapplication.transfers.model.InventoryModel
import com.tvhht.myapplication.utils.ApiConstant

import com.tvhht.myapplication.utils.PrefConstant
import com.tvhht.myapplication.utils.WMSApplication
import com.tvhht.myapplication.utils.WMSSharedPref
import retrofit2.HttpException
import rx.Observable
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers


class PickingLiveDataModel : ViewModel() {
    private var strPickingList: MutableLiveData<List<PickingListResponse?>>? = null
    private var strPickingBin: MutableLiveData<List<BinResponse?>>? = null
    private var strPickingRecords: SingleLiveData<List<PickingSubmitResponse>>? = null
    private var strHandlingEqupiment: MutableLiveData<Boolean>? = null
    private var globalInventoryList: MutableLiveData<List<InventoryModel>>? = null
    private val mState = MutableLiveData<NetworkState>()
    var errorMessage: SingleLiveData<Boolean> = SingleLiveData()

    internal fun getPickingList(dataToApi: PickUpHeader): MutableLiveData<List<PickingListResponse?>> {
        if (strPickingList == null) {
            strPickingList = MutableLiveData()
        }
        loadListData(dataToApi)
        return strPickingList as MutableLiveData<List<PickingListResponse?>>
    }

    internal fun getEquipFeatures(): MutableLiveData<Boolean>? {
        return strHandlingEqupiment;
    }


    internal fun getEquipment(handlingId: String): MutableLiveData<Boolean> {
        if (strHandlingEqupiment == null) {
            strHandlingEqupiment = MutableLiveData()
        }
        vaidateHandlingEquipment(handlingId)
        return strHandlingEqupiment as MutableLiveData<Boolean>
    }


    internal fun getPickingBin(
        dataToApi: String,
        dataQty: PickingQuantityConfirm,manufacturerName:String,packBarCode:String
    ): MutableLiveData<List<BinResponse?>> {
        if (strPickingBin == null) {
            strPickingBin = MutableLiveData()
        }
        loadBinData(dataToApi, dataQty,manufacturerName,packBarCode)
        return strPickingBin as MutableLiveData<List<BinResponse?>>
    }


    internal fun confirmPicking(dataToApi: List<PickingSubmitRequest>): SingleLiveData<List<PickingSubmitResponse>> {
        if (strPickingRecords == null) {
            strPickingRecords = SingleLiveData()
            createPickingData(dataToApi)
        }
        return strPickingRecords as SingleLiveData<List<PickingSubmitResponse>>
    }
    internal fun getGlobalInventoryList(pickingInventory: PickingInventory): MutableLiveData<List<InventoryModel>> {
        if (globalInventoryList == null) {
            globalInventoryList = MutableLiveData()
        }
        getGlobalInventory(pickingInventory)
        return globalInventoryList as MutableLiveData<List<InventoryModel>>
    }


    private fun loadListData(dataToApi: PickUpHeader) {
        val apiService: ApiServices = RetrofitBuilder.apiService

        val apiKey: String =
            WMSSharedPref.getAppPrefs(WMSApplication.getInstance().context)
                .getStringValue(PrefConstant.SECRET_TOKEN)

        apiService.findPickUpList(apiKey, dataToApi)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .onErrorResumeNext { throwable -> Observable.error(throwable) }
            .subscribe(object : Subscriber<List<PickingListResponse?>>() {
                override fun onCompleted() {}
                override fun onError(e: Throwable) {
                    e.printStackTrace()
                    Log.e("EEEEE", e.cause.toString())
                }

                override fun onNext(response: List<PickingListResponse?>) {
                    if (response.isNotEmpty()) {
                        strPickingList?.postValue(response)
                    } else {
                        strPickingList?.postValue(emptyList())
                    }
                }
            })
    }


    private fun loadInventory(
        mfrRequest: SearchDescription,
        invRequest: PickingInventory,
        list: List<PickingListResponse>
    ) {
        val apiService: ApiServices = RetrofitBuilder.apiService
        val apiKey: String =
            WMSSharedPref.getAppPrefs(WMSApplication.getInstance().context)
                .getStringValue(PrefConstant.SECRET_TOKEN)

        apiService.getInventoryQty(apiKey, invRequest)
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
                    if (response != null && response.isNotEmpty()) {

                        val listInv = response as List<InventoryModel>?

                        checkMfrDesp(mfrRequest, list!!, listInv!!)

                    } else {
                        strPickingList?.postValue(emptyList())
                    }


                }


            })
    }


    private fun checkMfrDesp(
        mfrRequest: SearchDescription,
        list: List<PickingListResponse>,
        listInvQty: List<InventoryModel>
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
                        loadMfrDesp(mfrRequest, list!!, listInvQty)
                    }

                }


            })

    }


    private fun loadMfrDesp(
        mfrRequest: SearchDescription,
        list: List<PickingListResponse>,
        listInvQty: List<InventoryModel>
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
                        val pickingList: MutableList<PickingCombineResponse> = ArrayList()


                        for (i in list?.indices!!) {

                            try {
                                val desAvailable = isDesAvailable(list[i].itemCode!!, mfrlist)!!
                                val inventoryQty = isInvenQty(
                                    list[i].itemCode!!,
                                    list[i].proposedPackBarCode!!,
                                    list[i].proposedStorageBin!!,
                                    listInvQty
                                )!!
                                val barcodeId = getBarcodeId(list[i].itemCode?:"",list[i].manufacturerName?:"",listInvQty)
                                pickingList.add(
                                    PickingCombineResponse(
                                        list[i].languageId,
                                        list[i].companyCodeId,
                                        list[i].plantId,
                                        list[i].warehouseId,
                                        list[i].preOutboundNo,
                                        list[i].refDocNumber,
                                        list[i].partnerCode,
                                        list[i].pickupNumber,
                                        list[i].lineNumber,
                                        list[i].itemCode,
                                        list[i].proposedStorageBin,
                                        list[i].proposedPackBarCode,
                                        list[i].outboundOrderTypeId,
                                        list[i].pickToQty,
                                        list[i].pickUom,
                                        list[i].stockTypeId,
                                        list[i].specialStockIndicatorId,
                                        desAvailable!!.manufacturerPartNo,
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
                                        list[i].remarks,
                                        list[i].pickupCreatedBy,
                                        list[i].pickupCreatedOn,
                                        list[i].pickConfimedBy,
                                        list[i].pickConfimedOn,
                                        list[i].pickUpdatedBy,
                                        list[i].pickUpdatedOn,
                                        list[i].pickupReversedBy,
                                        list[i].pickupReversedOn,

                                        desAvailable!!.description,
                                        desAvailable!!.createdBy,
                                        desAvailable!!.createdOn,

                                        desAvailable!!.uomID,
                                        desAvailable!!.hsnCode,

                                        desAvailable!!.updatedBy,
                                        desAvailable!!.updatedOn,
                                        inventoryQty, inventoryQty,
                                        list[i].manufacturerName,
                                        barcodeId,
                                        list[i].referenceDocumentType,
                                        list[i].assignedPickerId,
                                        list[i].salesOrderNumber ?: ""
                                    )
                                )
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }

                        }
                        //strPickingList?.postValue(pickingList)


                    } else {
                        strPickingList?.postValue(emptyList())
                    }

                }


            })

    }


    private fun loadBinData(dataToApi: String, dataQty: PickingQuantityConfirm,manufacturerName:String,packBarCode:String) {
        val apiService: ApiServices = RetrofitBuilder.apiService

        val apiKey: String =
            WMSSharedPref.getAppPrefs(WMSApplication.getInstance().context)
                .getStringValue(PrefConstant.SECRET_TOKEN)

        val wareID: String =
            WMSSharedPref.getAppPrefs(WMSApplication.getInstance().context)
                .getStringValue(PrefConstant.WARE_HOUSE_ID)
        val companyCodeId = WMSSharedPref.getAppPrefs(
            WMSApplication.getInstance().context
        ).getStringValue(PrefConstant.COMPANY_CODE_ID)
        val plantId = WMSSharedPref.getAppPrefs(
            WMSApplication.getInstance().context
        ).getStringValue(PrefConstant.PLANT_ID)
        val languageId = WMSSharedPref.getAppPrefs(
            WMSApplication.getInstance().context
        ).getStringValue(PrefConstant.LANGUAGE_ID)

        apiService.getAdditionalBin(
            apiKey,
            dataToApi,
            dataQty.outboundOrderTypeId,
            packBarCode,
            dataQty.binLocation,
            wareID,
            companyCodeId,
            plantId,
            languageId,
            manufacturerName
        )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .onErrorResumeNext { throwable -> Observable.error(throwable) }
            .subscribe(object : Subscriber<List<BinResponse?>>() {
                override fun onCompleted() {}
                override fun onError(e: Throwable) {
//                    strAuthenticator?.postValue("ERROR")
                    e.printStackTrace()

                    Log.e("EEEEE", e.cause.toString())
                    strPickingBin?.postValue(emptyList())
                }

                override fun onNext(response: List<BinResponse?>) {

                    if (response != null && response.isNotEmpty()) {

                        val requestList1: MutableList<String> = ArrayList()
                        val requestList2: MutableList<String> = ArrayList()
                        val requestList3: MutableList<String> = ArrayList()

                        val requestList4: MutableList<String> = ArrayList()
                        val requestList5: MutableList<String> = ArrayList()
                        requestList5.add(manufacturerName)
                        val stringValue = WMSSharedPref.getAppPrefs(
                            WMSApplication.getInstance().context
                        ).getStringValue(PrefConstant.WARE_HOUSE_ID)
                        for (i in response?.indices!!) {

                            requestList1.add(response[i]!!.itemCode.toString())
                            requestList4.add(response[i]!!.packBarcodes.toString())
                            requestList3.add(response[i]!!.storageBin.toString())
                            requestList2.add(stringValue)
                        }


                        val searchInventory =
                            PickingInventory(requestList1, requestList2, requestList3, requestList4,requestList5)

                        getInventoryCount(searchInventory, response)
                    } else {
                        strPickingBin?.postValue(emptyList())
                    }
                }


            })
    }

    private fun getInventoryCount(
        invRequest: PickingInventory,
        list: List<BinResponse?>
    ) {
        val apiService: ApiServices = RetrofitBuilder.apiService
        val apiKey: String =
            WMSSharedPref.getAppPrefs(WMSApplication.getInstance().context)
                .getStringValue(PrefConstant.SECRET_TOKEN)

        apiService.getInventoryQty(apiKey, invRequest)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .onErrorResumeNext { throwable -> Observable.error(throwable) }
            .subscribe(object : Subscriber<List<InventoryModel?>>() {
                override fun onCompleted() {}
                override fun onError(e: Throwable) {
//                    strAuthenticator?.postValue("ERROR")
                    e.printStackTrace()

                    Log.e("EEEEE", e.cause.toString())
                    strPickingBin?.postValue(emptyList())
                }

                override fun onNext(response: List<InventoryModel?>) {
                    if (response != null && response.isNotEmpty()) {

                        val listInv = response as List<InventoryModel>?

                        for (i in list?.indices!!) {
                            val invenQty = isInvenQty(
                                list[i]!!.itemCode!!,
                                list[i]!!.packBarcodes!!,
                                list[i]!!.storageBin!!,
                                listInv
                            )
                            list[i]!!.inventoryQuantity = invenQty
                        }

                        val filter = list.filter { it?.inventoryQuantity!! > 0 }

                        strPickingBin?.postValue(filter)
                    } else {
                        strPickingBin?.postValue(emptyList())
                    }

                }
            })


    }


    private fun createPickingData(dataToApi: List<PickingSubmitRequest>) {
        val apiService: ApiServices = RetrofitBuilder.apiService

        val apiKey: String =
            WMSSharedPref.getAppPrefs(WMSApplication.getInstance().context)
                .getStringValue(PrefConstant.SECRET_TOKEN)

        val login_id: String =
            WMSSharedPref.getAppPrefs(WMSApplication.getInstance().context)
                .getStringValue(PrefConstant.LOGIN_ID)

        apiService.createPickingRecords(apiKey, login_id, dataToApi).take(1)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .onErrorResumeNext { throwable -> Observable.error(throwable) }
            .subscribe(object : Subscriber<List<PickingSubmitResponse>>() {
                override fun onCompleted() {}
                override fun onError(e: Throwable) {
                    try {
                        val httpError = e as HttpException
                        val errorBody = httpError.response()?.errorBody()?.string()
                        val response = Gson().fromJson(errorBody, ErrorResponse::class.java)
                        if (response?.error.isNullOrEmpty()
                                .not() && response.error?.contains("PickupLine Record is getting duplicated. Given data already exists in the Database") == true
                        ) {
                            errorMessage.value = true
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                override fun onNext(response: List<PickingSubmitResponse>) {

                    if (response != null) {
                        strPickingRecords?.postValue(response)
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


    private fun isInvenQty(
        itemCode: String, palletCode: String, BinNo: String,
        mfrList: List<InventoryModel>?
    ): Int? {
        val find =
            mfrList!!.find { it.itemCode == itemCode && it.packBarcodes == palletCode && it.storageBin == BinNo }
        if (find != null) {
            return find.inventoryQuantity
        } else {
            return 0
        }
    }
    private fun getBarcodeId(
        itemCode: String,
        manufacturerName: String,
        inventoryList: List<InventoryModel>?
    ): String? {
        val find =
            inventoryList!!.find { it.itemCode == itemCode && it.manufacturerName == manufacturerName }
        return if (find != null) {
            find.barcodeId
        } else {
            ""
        }
    }


    private fun vaidateHandlingEquipment(handlingId: String) {
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
                    strHandlingEqupiment!!.postValue(false)
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
                        loadEquipmentDetails(handlingId)
                    } else {
                        strHandlingEqupiment!!.postValue(false)
                    }

                }


            })

    }


    private fun loadEquipmentDetails(handlingId: String) {
        val apiService: ApiServices = RetrofitBuilder.apiService

        val apiKey: String =
            WMSSharedPref.getAppPrefs(WMSApplication.getInstance().context)
                .getStringValue(PrefConstant.SECRET_TOKEN2)

        val warehouseID: String =
            WMSSharedPref.getAppPrefs(WMSApplication.getInstance().context)
                .getStringValue(PrefConstant.WARE_HOUSE_ID)
        val companyCodeId = WMSSharedPref.getAppPrefs(
            WMSApplication.getInstance().context
        ).getStringValue(PrefConstant.COMPANY_CODE_ID)
        val plantId = WMSSharedPref.getAppPrefs(
            WMSApplication.getInstance().context
        ).getStringValue(PrefConstant.PLANT_ID)
        val languageId = WMSSharedPref.getAppPrefs(
            WMSApplication.getInstance().context
        ).getStringValue(PrefConstant.LANGUAGE_ID)

        apiService.findHandlingEquipment(
            handlingId,
            apiKey,
            warehouseID,
            companyCodeId,
            plantId,
            languageId
        )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .onErrorResumeNext { throwable -> Observable.error(throwable) }
            .subscribe(object : Subscriber<HEResponse>() {
                override fun onCompleted() {}
                override fun onError(e: Throwable) {
//                    strAuthenticator?.postValue("ERROR")
                    e.printStackTrace()
                    Log.e("EEEEE", e.cause.toString())
                    strHandlingEqupiment!!.postValue(false)
                }

                override fun onNext(response: HEResponse) {
                    if (response != null) {
                        strHandlingEqupiment!!.postValue(true)
                    } else {
                        strHandlingEqupiment!!.postValue(false)
                    }
                }

            })
    }

    private fun getGlobalInventory(request: PickingInventory) {
        val apiService: ApiServices = RetrofitBuilder.apiService
        val apiKey: String =
            WMSSharedPref.getAppPrefs(WMSApplication.getInstance().context)
                .getStringValue(PrefConstant.SECRET_TOKEN)

        apiService.getInventoryQty(apiKey, request)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .onErrorResumeNext { throwable -> Observable.error(throwable) }
            .subscribe(object : Subscriber<List<InventoryModel?>>() {
                override fun onCompleted() {}
                override fun onError(e: Throwable) {
                    e.printStackTrace()
                    Log.e("EEEEE", e.cause.toString())
                }

                override fun onNext(response: List<InventoryModel?>) {
                    if (response.isNotEmpty()) {
                        val inventoryList = response as List<InventoryModel>
                        globalInventoryList?.postValue(inventoryList)
                    } else {
                        globalInventoryList?.postValue(emptyList())
                    }
                }
            })
    }

}