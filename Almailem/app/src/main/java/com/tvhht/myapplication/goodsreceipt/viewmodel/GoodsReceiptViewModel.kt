package com.tvhht.myapplication.goodsreceipt.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.tvhht.myapplication.api.ApiServices
import com.tvhht.myapplication.api.RetrofitBuilder
import com.tvhht.myapplication.goodsreceipt.model.GoodsReceiptRequest
import com.tvhht.myapplication.goodsreceipt.model.GoodsReceiptResponse
import com.tvhht.myapplication.goodsreceipt.model.SelectedDocumentResponse
import com.tvhht.myapplication.utils.ApiConstant
import com.tvhht.myapplication.utils.PrefConstant
import com.tvhht.myapplication.utils.WMSApplication
import com.tvhht.myapplication.utils.WMSSharedPref
import rx.Observable
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

class GoodsReceiptViewModel : ViewModel() {
    private var goodsReceiptList: MutableLiveData<List<GoodsReceiptResponse>>? = null
    var selectedDocument: SelectedDocumentResponse? = null
    var partNoList: List<SelectedDocumentResponse>? = null
    var grHeaderList: List<GoodsReceiptResponse> = listOf()
    internal fun getGoodsReceiptList(): MutableLiveData<List<GoodsReceiptResponse>> {
        if (goodsReceiptList == null) {
            goodsReceiptList = MutableLiveData()
        }
        loadGoodsReceipt()
        return goodsReceiptList as MutableLiveData<List<GoodsReceiptResponse>>
    }

    private fun loadGoodsReceipt() {
        // do async operation to fetch users
        val apiService: ApiServices = RetrofitBuilder.apiService

        val apiKey: String =
            WMSSharedPref.getAppPrefs(WMSApplication.getInstance().context)
                .getStringValue(PrefConstant.SECRET_TOKEN)

        val warehouseIdList: MutableList<String> = ArrayList()
        val companyCodeIdList: MutableList<String> = ArrayList()
        val languageIdList: MutableList<String> = ArrayList()
        val plantIdList: MutableList<String> = ArrayList()
        val statusIdList: MutableList<Int> = ArrayList()
        val stagingNoList: MutableList<String> = ArrayList()
        val inboundOrderTypeIdList: MutableList<Int> = ArrayList()
        val refDocNumberList: MutableList<String> = ArrayList()

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

        warehouseIdList.add(wareId)
        companyCodeIdList.add(companyCodeId)
        languageIdList.add(languageId)
        plantIdList.add(plantId)
        statusIdList.add(16)
       // stagingNoList.add(selectedDocument?.stagingNo ?: "")
        partNoList?.forEach { selectedDocument ->
            selectedDocument.inboundOrderTypeId?.let {
                inboundOrderTypeIdList.add(it)
            }
            selectedDocument.refDocNumber?.let {
                refDocNumberList.add(it)
            }
        }

        val goodsRequest =
            GoodsReceiptRequest(
                if (wareId == ApiConstant.WAREHOUSE_ID_200) null else companyCodeIdList,
                languageIdList,
                if (wareId == ApiConstant.WAREHOUSE_ID_200) null else plantIdList,
                warehouseIdList,
                statusIdList,
                inboundOrderTypeIdList,
                refDocNumberList
            )
        apiService.getGoodsReceipt(apiKey, goodsRequest)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .onErrorResumeNext { throwable -> Observable.error(throwable) }
            .subscribe(object : Subscriber<List<GoodsReceiptResponse>>() {
                override fun onCompleted() {}
                override fun onError(e: Throwable) {
                    e.printStackTrace()
                    Log.e("EEEEE", e.cause.toString())
                }

                override fun onNext(response: List<GoodsReceiptResponse>) {
                   goodsReceiptList?.postValue(response)
                }
            })
    }
}