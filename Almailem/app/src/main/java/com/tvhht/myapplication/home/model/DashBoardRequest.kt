package com.tvhht.myapplication.home.model

import com.google.gson.annotations.SerializedName

data class DashBoardRequest(
    @SerializedName("companyCode") val companyCodeList: List<String>? = null,
    @SerializedName("languageId") val languageIdList: List<String>? = null,
    @SerializedName("plantId") val plantIdList: List<String>? = null,
    @SerializedName("warehouseId") val warehouseIdList: List<String>? = null,
    @SerializedName("userID") val loginUserIdList: List<String>? = null
)
