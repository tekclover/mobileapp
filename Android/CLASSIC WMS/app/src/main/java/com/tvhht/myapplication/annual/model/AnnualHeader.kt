package com.tvhht.myapplication.annual.model

import com.google.gson.annotations.SerializedName


data class AnnualHeader(
    @SerializedName("warehouseId") val warehouseId: String,
    @SerializedName("cycleCounterId") val cycleCounterId: List<String>,
    @SerializedName("headerStatusId") val headerStatusId: List<Int>,
    @SerializedName("lineStatusId") val lineStatusID: List<Int>
)
