package com.tvhht.myapplication.transfers.model

import com.google.gson.annotations.SerializedName


 data class SearchTargetBin (
    @SerializedName("storageBin") var packBarcodes : ArrayList<String> = arrayListOf(),
    @SerializedName("warehouseId") var warehouseId : ArrayList<String> = arrayListOf())
