package com.tvhht.myapplication.cases.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.tvhht.myapplication.api.ApiServices
import com.tvhht.myapplication.api.RetrofitBuilder
import com.tvhht.myapplication.api.error.NetworkState
import com.tvhht.myapplication.cases.model.AsnList
import com.tvhht.myapplication.cases.model.CaseConfirmResponse
import com.tvhht.myapplication.cases.model.CaseDetailModel
import com.tvhht.myapplication.cases.model.CasesConfirmRequest
import com.tvhht.myapplication.utils.PrefConstant
import com.tvhht.myapplication.utils.WMSApplication
import com.tvhht.myapplication.utils.WMSSharedPref
import rx.Observable
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers


class CasesLiveDataModel : ViewModel() {
    private var strAuthenticator: MutableLiveData<List<AsnList?>>? = null
    private var caseSubmit: MutableLiveData<CaseConfirmResponse?>? = null
    private val mState = MutableLiveData<NetworkState>()

    internal fun getStatus(): MutableLiveData<List<AsnList?>> {
        if (strAuthenticator == null) {
            strAuthenticator = MutableLiveData()
            loadStatus()
        }
        return strAuthenticator as MutableLiveData<List<AsnList?>>
    }


    fun createCaseToUpload(dataToApi: CaseDetailModel,dataToApi1: List<CaseDetailModel>): MutableLiveData<CaseConfirmResponse?> {
        if (caseSubmit == null) {
            caseSubmit = MutableLiveData()
            submitCase(dataToApi,dataToApi1)
        }
        return caseSubmit as MutableLiveData<CaseConfirmResponse?>
    }


    private fun loadStatus() {
        val apiService: ApiServices = RetrofitBuilder.apiService

        val apiKey: String =
            WMSSharedPref.getAppPrefs(WMSApplication.getInstance().context)
                .getStringValue(PrefConstant.SECRET_TOKEN)
        apiService.getAsnList(apiKey)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .onErrorResumeNext { throwable -> Observable.error(throwable) }
            .subscribe(object : Subscriber<List<AsnList?>>() {
                override fun onCompleted() {}
                override fun onError(e: Throwable) {
//                    strAuthenticator?.postValue("ERROR")
                    e.printStackTrace()

                    Log.e("EEEEE", e.cause.toString())
                }

                override fun onNext(response: List<AsnList?>) {
                    if (response != null) {

                        val loginID: String =
                            WMSSharedPref.getAppPrefs(WMSApplication.getInstance().context)
                                .getStringValue(PrefConstant.LOGIN_ID)


                        val ware_id = WMSSharedPref.getAppPrefs(
                            WMSApplication.getInstance().context
                        ).getStringValue(PrefConstant.WARE_HOUSE_ID)


                        var filteredFinalArray =
                            response.filter { it?.statusId == 13 && it?.assignedUserId == loginID && it.warehouseId == ware_id }

                        WMSApplication.getInstance().casesLocalRepository
                            ?.delete()
                        WMSApplication.getInstance().casesLocalRepository
                            ?.insertListVo(filteredFinalArray)

                        strAuthenticator?.postValue(filteredFinalArray)
                    }
                }


            })
    }


    private fun submitCase(dataToApi: CaseDetailModel,dataToApi1: List<CaseDetailModel>) {
        val apiService: ApiServices = RetrofitBuilder.apiService

        val apiKey: String =
            WMSSharedPref.getAppPrefs(WMSApplication.getInstance().context)
                .getStringValue(PrefConstant.SECRET_TOKEN)

        val loginID: String =
            WMSSharedPref.getAppPrefs(WMSApplication.getInstance().context)
                .getStringValue(PrefConstant.LOGIN_ID)


        var listData: MutableList<CasesConfirmRequest> = ArrayList();


        for(i in dataToApi1.indices) {

            listData.add(
                CasesConfirmRequest(
                    dataToApi1[i].caseCode,
                    dataToApi1[i].itemCode,
                    dataToApi1[i].lineNo,
                    dataToApi1[i].palletCode,
                    dataToApi1[i].preInboundNo,
                    dataToApi1[i].refDocNumber,
                    dataToApi1[i].stagingNo,
                    dataToApi1[i].wareHouseID
                )
            )
        }

        apiService.updateCases(
            apiKey,
            dataToApi.caseCode,
            loginID,
            listData
        ).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .onErrorResumeNext { throwable -> Observable.error(throwable) }
            .subscribe(object : Subscriber<List<CaseConfirmResponse?>>() {
                override fun onCompleted() {}
                override fun onError(e: Throwable) {
//                    strAuthenticator?.postValue("ERROR")
                    e.printStackTrace()

                    Log.e("EEEEE", e.cause.toString())
                }

                override fun onNext(response: List<CaseConfirmResponse?>) {
                    if (response != null) {
//                        WMSApplication.getInstance().casesLocalRepository
//                            ?.insertListVo(response)
                        caseSubmit?.postValue(response[0])
                    }
                }


            })
    }


}