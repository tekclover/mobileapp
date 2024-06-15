package com.tvhht.myapplication.quality.model


import com.google.gson.annotations.SerializedName


data class QualityRequest(
    @SerializedName("companyCodeId") val companyCodeIdList: List<String>?=null,
    @SerializedName("languageId") val languageIdList: List<String>,
    @SerializedName("plantId") val plantIdList: List<String>?=null,
    @SerializedName("warehouseId") val warehouseId: List<String>,
    @SerializedName("statusId") val statusId: List<Int>,
    @SerializedName("assignedUserId") val assignedUserId: String? = null
)
