package com.tvhht.myapplication.reports.model

import com.google.gson.annotations.SerializedName


data class ReportRequestItemCode(
    @SerializedName("warehouseId") val warehouseId: List<String>,
    @SerializedName("itemCode") val itemCode: List<String>,
    @SerializedName("assignedUserId") val assignedUserId: String? = null
)
