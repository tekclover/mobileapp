package com.tvhht.myapplication.stock.model

import com.google.gson.annotations.SerializedName


data class PerpetualHeader(
    @SerializedName("warehouseId") val warehouseId: String,
    @SerializedName("cycleCounterId") val cycleCounterId: String,
    @SerializedName("lineStatusId") val lineStatusId: List<Int>
)
