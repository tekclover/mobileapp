package com.tvhht.myapplication.reports.model

import com.google.gson.annotations.SerializedName


data class ReportRequestPalletID(
    @SerializedName("companyCodeId") val companyCodeIdList: List<String>? = null,
    @SerializedName("languageId") val languageIdList: List<String>? = null,
    @SerializedName("plantId") val plantIdList: List<String>? = null,
    @SerializedName("warehouseId") val warehouseId: List<String>,
    @SerializedName("barcodeId") val packBarcodes: List<String>,
    @SerializedName("assignedUserId") val assignedUserId: String? = null
)
