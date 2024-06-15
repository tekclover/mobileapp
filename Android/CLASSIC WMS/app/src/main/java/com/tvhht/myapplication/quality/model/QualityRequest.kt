package com.tvhht.myapplication.quality.model


import com.google.gson.annotations.SerializedName


data class QualityRequest(
    @SerializedName("warehouseId") val warehouseId: List<String>,
    @SerializedName("statusId") val statusId: List<Int>
)
