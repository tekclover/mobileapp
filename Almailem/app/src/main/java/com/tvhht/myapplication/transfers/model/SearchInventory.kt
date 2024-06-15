package com.tvhht.myapplication.transfers.model

import com.google.gson.annotations.SerializedName


data class SearchInventory (
    @SerializedName("storageBin") val packBarcodes : List<String>, @SerializedName("warehouseId") val warehouseId : List<String>)
