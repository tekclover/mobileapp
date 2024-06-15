package com.tvhht.myapplication.annual.model

import com.google.gson.annotations.SerializedName


data class AnnualDetailHeader1(
    @SerializedName("cycleCountNo") val cycleCountNoList: List<String>? = null,
    @SerializedName("cycleCounterId") val cycleCounterIdList: List<String>? = null,
    @SerializedName("lineStatusId") val lineStatusIdList: List<Int>? = null,
    @SerializedName("warehouseId") val warehouseIdList: List<String>? = null,
    @SerializedName("companyCode") val companyCodeIdList: List<String>? = null,
    @SerializedName("plantId") val plantIdList: List<String>? = null,
    @SerializedName("languageId") val languageIdList: List<String>? = null
)
