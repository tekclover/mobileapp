package com.tvhht.myapplication.picking.model

import com.google.gson.annotations.SerializedName


data class PickUpHeader(
    @SerializedName("warehouseId") val warehouseId: List<String>? = null,
    @SerializedName("companyCodeId") val companyCodeIdList: List<String>? = null,
    @SerializedName("plantId") val plantIdList: List<String>? = null,
    @SerializedName("languageId") val languageIdList: List<String>? = null,
    @SerializedName("levelId") val levelIdList: List<Int>? = null,
    @SerializedName("statusId") val statusId: List<Int>? = null,
    @SerializedName("assignedPickerId") val assignedPickerIdList: List<String>? = null,
    @SerializedName("OutboundOrderTypeId") val outboundOrderTypeId: List<Int>? = null
)
