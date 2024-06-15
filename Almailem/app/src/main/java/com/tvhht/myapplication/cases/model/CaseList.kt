package com.tvhht.myapplication.cases.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class CaseList(var refDocNumber:String?, var caseList : List<String>?):Parcelable



