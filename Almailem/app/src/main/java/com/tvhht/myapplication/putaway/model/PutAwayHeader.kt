package com.tvhht.myapplication.putaway.model


import com.google.gson.annotations.SerializedName


data class PutAwayHeader(
    @SerializedName("companyCodeId") val companyCodeIdList: List<String>? = null,
    @SerializedName("languageId") val languageIdList: List<String>? = null,
    @SerializedName("plantId") val plantIdList: List<String>? = null,
    @SerializedName("warehouseId") val warehouseIdList: List<String>? = null,
    @SerializedName("statusId") val statusId: List<Int>,
    @SerializedName("assignedUserId") val assignedUserId: String? = null
)

