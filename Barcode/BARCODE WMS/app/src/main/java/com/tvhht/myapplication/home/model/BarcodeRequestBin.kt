package com.tvhht.myapplication.home.model

import com.google.gson.annotations.SerializedName


data class BarcodeRequestBin(
    @SerializedName("barcode") val barcode: String,
    @SerializedName("referenceField1") val referenceField1: String,
    @SerializedName("createdBy") val createdBy: String,
    @SerializedName("createdOn") val createdOn: String,
    @SerializedName("deletionIndicator") val deletionIndicator: Int,
    @SerializedName("itemCode") val itemCode: String,
    @SerializedName("storageBin") val storageBin: String,
    @SerializedName("updatedBy") val updatedBy: String,
    @SerializedName("updatedOn") val updatedOn: String


)
