package com.tvhht.myapplication.reports.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.tvhht.myapplication.api.ApiServices
import com.tvhht.myapplication.api.RetrofitBuilder
import com.tvhht.myapplication.api.error.NetworkState
import com.tvhht.myapplication.reports.model.ReportRequestBin
import com.tvhht.myapplication.reports.model.ReportRequestItemCode
import com.tvhht.myapplication.reports.model.ReportRequestPalletID
import com.tvhht.myapplication.reports.model.ReportResponse
import com.tvhht.myapplication.utils.PrefConstant
import com.tvhht.myapplication.utils.WMSApplication
import com.tvhht.myapplication.utils.WMSSharedPref
import rx.Observable
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers


class ReportLiveDataModel : ViewModel() {
    private var strReportList: MutableLiveData<List<ReportResponse?>>? = null
    private val mState = MutableLiveData<NetworkState>()

    internal fun getReportsFromPalletID(dataToApi: ReportRequestPalletID): MutableLiveData<List<ReportResponse?>> {
        if (strReportList == null) {
            strReportList = MutableLiveData()
        }
        loadPalletReports(dataToApi)
        return strReportList as MutableLiveData<List<ReportResponse?>>
    }


    private fun loadPalletReports(dataToApi: ReportRequestPalletID) {
        val apiService: ApiServices = RetrofitBuilder.apiService

        val apiKey: String =
            WMSSharedPref.getAppPrefs(WMSApplication.getInstance().context)
                .getStringValue(PrefConstant.SECRET_TOKEN)
        apiService.getReportsFromPalletID(apiKey, dataToApi)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .onErrorResumeNext { throwable -> Observable.error(throwable) }
            .subscribe(object : Subscriber<List<ReportResponse?>>() {
                override fun onCompleted() {}
                override fun onError(e: Throwable) {
//                    strAuthenticator?.postValue("ERROR")
                    e.printStackTrace()

                    Log.e("EEEEE", e.cause.toString())
                }

                override fun onNext(response: List<ReportResponse?>) {
                    if (response != null) {
                        strReportList?.postValue(response)
                    }
                }


            })
    }



    internal fun getReportsFromBin(dataToApi: ReportRequestBin): MutableLiveData<List<ReportResponse?>> {
        if (strReportList == null) {
            strReportList = MutableLiveData()
        }
        loadBinReports(dataToApi)
        return strReportList as MutableLiveData<List<ReportResponse?>>
    }


    private fun loadBinReports(dataToApi: ReportRequestBin) {
        val apiService: ApiServices = RetrofitBuilder.apiService

        val apiKey: String =
            WMSSharedPref.getAppPrefs(WMSApplication.getInstance().context)
                .getStringValue(PrefConstant.SECRET_TOKEN)
        apiService.getReportsFromBinID(apiKey, dataToApi)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .onErrorResumeNext { throwable -> Observable.error(throwable) }
            .subscribe(object : Subscriber<List<ReportResponse?>>() {
                override fun onCompleted() {}
                override fun onError(e: Throwable) {
//                    strAuthenticator?.postValue("ERROR")
                    e.printStackTrace()

                    Log.e("EEEEE", e.cause.toString())
                }

                override fun onNext(response: List<ReportResponse?>) {
                    if (response != null) {
                        strReportList?.postValue(response)
                    }
                }


            })
    }




    internal fun getReportsFromItemCode(dataToApi: ReportRequestItemCode): MutableLiveData<List<ReportResponse?>> {
        if (strReportList == null) {
            strReportList = MutableLiveData()
        }
        loadItemCodeReports(dataToApi)
        return strReportList as MutableLiveData<List<ReportResponse?>>
    }


    private fun loadItemCodeReports(dataToApi: ReportRequestItemCode) {
        val apiService: ApiServices = RetrofitBuilder.apiService

        val apiKey: String =
            WMSSharedPref.getAppPrefs(WMSApplication.getInstance().context)
                .getStringValue(PrefConstant.SECRET_TOKEN)
        apiService.getReportsItemCode(apiKey, dataToApi)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .onErrorResumeNext { throwable -> Observable.error(throwable) }
            .subscribe(object : Subscriber<List<ReportResponse?>>() {
                override fun onCompleted() {}
                override fun onError(e: Throwable) {
//                    strAuthenticator?.postValue("ERROR")
                    e.printStackTrace()

                    Log.e("EEEEE", e.cause.toString())
                }

                override fun onNext(response: List<ReportResponse?>) {
                    if (response != null) {
                        strReportList?.postValue(response)
                    }
                }


            })
    }


}