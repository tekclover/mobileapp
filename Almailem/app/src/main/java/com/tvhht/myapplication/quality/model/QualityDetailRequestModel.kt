package com.tvhht.myapplication.quality.model

import com.google.gson.annotations.SerializedName

data class QualityDetailRequestModel(
    @SerializedName("warehouseId") val warehouseId: List<String>,
    @SerializedName("actualHeNo") val actualHeNo: List<String>,
    @SerializedName("pickupNumber") val pickupNumber: List<String>
)
