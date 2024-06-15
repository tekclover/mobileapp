package com.tvhht.myapplication.picking.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.tvhht.myapplication.api.ApiServices
import com.tvhht.myapplication.api.RetrofitBuilder
import com.tvhht.myapplication.login.model.LoginModel
import com.tvhht.myapplication.login.model.LoginResponse
import com.tvhht.myapplication.picking.model.PickingInventory
import com.tvhht.myapplication.picking.model.PickingListResponse
import com.tvhht.myapplication.transfers.model.InventoryModel
import com.tvhht.myapplication.utils.ApiConstant
import rx.Observable
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

class PickingOrderNoViewModel : ViewModel() {
    var authToken: MutableLiveData<String> = MutableLiveData()
    var inventoryLiveData: MutableLiveData<List<InventoryModel>> = MutableLiveData()
    var loaderLiveData: MutableLiveData<Boolean> = MutableLiveData()
    var orderNoList: List<PickingListResponse>? = null
    var binNo = ""
    var selectedPicking: PickingListResponse? = null
    var intentFrom: String = ""
    fun getAuthToken(transactionId: Int, apiName: String) {
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
                        when (transactionId) {
                            ApiConstant.PICKING_INVENTORY_ID -> {
                                getInventory()
                            }
                        }
                    }
                }
            })
    }

    private fun getInventory() {
        val apiService: ApiServices = RetrofitBuilder.apiService

        val itemCodeList: MutableList<String> = ArrayList()
        val proposedPackBarCodeList: MutableList<String> = ArrayList()
        val proposedStorageBinList: MutableList<String> = ArrayList()
        val warehouseIdList: MutableList<String> = ArrayList()
        val manufacturerNameList: MutableList<String> = ArrayList()

        itemCodeList.add(selectedPicking?.itemCode ?: "")
        warehouseIdList.add(selectedPicking?.warehouseId ?: "")
        proposedStorageBinList.add(selectedPicking?.proposedStorageBin ?: "")
        proposedPackBarCodeList.add(selectedPicking?.proposedPackBarCode ?: "")
        manufacturerNameList.add(selectedPicking?.manufacturerName ?: "")

        val request = PickingInventory(
            itemCodeList,
            warehouseIdList,
            proposedStorageBinList,
            proposedPackBarCodeList,
            manufacturerNameList
        )

        apiService.getInventoryQty(authToken.value ?: "", request)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .onErrorResumeNext { throwable -> Observable.error(throwable) }
            .subscribe(object : Subscriber<List<InventoryModel>>() {
                override fun onCompleted() {
                    loaderLiveData.value = false
                }
                override fun onError(e: Throwable) {
                    loaderLiveData.value = false
                }

                override fun onNext(response: List<InventoryModel>) {
                    if (response.isNotEmpty()) {
                        inventoryLiveData.value = response
                    }
                }
            })
    }
}