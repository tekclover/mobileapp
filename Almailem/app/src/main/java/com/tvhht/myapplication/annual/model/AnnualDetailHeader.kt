package com.tvhht.myapplication.annual.model

import com.google.gson.annotations.SerializedName


data class AnnualDetailHeader(
    @SerializedName("warehouseId") val warehouseId: String,
    @SerializedName("cycleCounterId") val cycleCounterId: String,
    @SerializedName("lineStatusId") val lineStatusId: List<Int>,
    @SerializedName("cycleCountNo") val cycleCountNo: List<String>,
    @SerializedName("companyCodeId") val companyCodeIdList: List<String>? = null,
    @SerializedName("plantId") val plantIdList: List<String>? = null,
    @SerializedName("languageId") val languageIdList: List<String>? = null
)
