package com.tvhht.myapplication.reports.model

import com.google.gson.annotations.SerializedName


data class FindItemCodeRequest(
    @SerializedName("companyCodeId") val companyCodeId: String,
    @SerializedName("languageId") val languageId: String,
    @SerializedName("plantId") val plantId: String,
    @SerializedName("warehouseId") val warehouseId: String,
    @SerializedName("likeSearchByDesc") val likeSearchByDesc: String,
)

data class FindItemCodeResponse(
    @SerializedName("itemCode") var itemCode: String? = null,
    @SerializedName("description") var description: String? = null
)