package com.tvhht.myapplication.annual.model

import com.google.gson.annotations.SerializedName


data class AnnualHeader(
    @SerializedName("cycleCounterId") val cycleCounterIdList: List<String>? = null,
    @SerializedName("headerStatusId") val headerStatusIdList: List<Int>? = null,
    @SerializedName("lineStatusId") val lineStatusIdList: List<Int>? = null,
    @SerializedName("warehouseId") val warehouseIdList: List<String>? = null,
    @SerializedName("companyCode") val companyCodeIdList: List<String>? = null,
    @SerializedName("plantId") val plantIdList: List<String>? = null,
    @SerializedName("languageId") val languageIdList: List<String>? = null
)
