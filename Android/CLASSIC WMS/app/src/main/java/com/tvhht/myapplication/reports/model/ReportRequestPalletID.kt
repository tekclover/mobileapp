package com.tvhht.myapplication.reports.model

import com.google.gson.annotations.SerializedName


data class ReportRequestPalletID(
    @SerializedName("warehouseId") val warehouseId: List<String>,
    @SerializedName("packBarcodes") val packBarcodes: List<String>

)
