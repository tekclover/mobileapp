package com.tvhht.myapplication.putaway.model

import com.google.gson.annotations.SerializedName

data class FindHhtUserRequest(
   /* @SerializedName("companyCodeId") var companyCodeIdList: List<String>? = null,
    @SerializedName("languageId") var languageIdList: List<String>? = null,*/
    @SerializedName("userId") var userIdList: List<String>? = null
)
