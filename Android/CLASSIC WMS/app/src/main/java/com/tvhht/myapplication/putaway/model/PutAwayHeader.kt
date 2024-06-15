package com.tvhht.myapplication.putaway.model


import com.google.gson.annotations.SerializedName


data class PutAwayHeader(
    @SerializedName("warehouseId") val warehouseId: List<String>,
    @SerializedName("inboundOrderTypeId") val inboundOrderTypeId: List<Int>,
    @SerializedName("statusId") val statusId: List<Int>
)
