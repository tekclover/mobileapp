package com.tvhht.myapplication.cases.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class CaseDetailModel(val caseCode:String?, val itemCode:String?, val lineNo:Int, val loginUserID :String?, val palletCode:String?, val preInboundNo:String?, val refDocNumber:String?, val stagingNo:String?,val wareHouseID:String?,
                           var isSelected :Boolean):Parcelable



