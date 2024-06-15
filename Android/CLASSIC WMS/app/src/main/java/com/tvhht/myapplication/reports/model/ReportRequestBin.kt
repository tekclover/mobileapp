package com.tvhht.myapplication.reports.model

import com.google.gson.annotations.SerializedName


data class ReportRequestBin(
    @SerializedName("warehouseId") val warehouseId: List<String>,
    @SerializedName("storageBin") val storageBin: List<String>

)
