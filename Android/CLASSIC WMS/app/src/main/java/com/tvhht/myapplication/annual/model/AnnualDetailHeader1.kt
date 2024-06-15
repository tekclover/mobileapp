package com.tvhht.myapplication.annual.model

import com.google.gson.annotations.SerializedName


data class AnnualDetailHeader1(
    @SerializedName("warehouseId") val warehouseId: String,
    @SerializedName("cycleCounterId") val cycleCounterId: List<String>,
    @SerializedName("lineStatusId") val lineStatusId: List<Int>,
    @SerializedName("cycleCountNo") val cycleCountNo: String

)
