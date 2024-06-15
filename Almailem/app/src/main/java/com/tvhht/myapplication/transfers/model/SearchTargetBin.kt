package com.tvhht.myapplication.transfers.model

import com.google.gson.annotations.SerializedName


data class SearchTargetBin(
    @SerializedName("storageBin") var packBarcodes: ArrayList<String> = arrayListOf(),
    @SerializedName("warehouseId") var warehouseId: ArrayList<String> = arrayListOf(),
    @SerializedName("companyCodeId") var companyCodeIdList: ArrayList<String> = arrayListOf(),
    @SerializedName("languageId") var languageIdList: ArrayList<String> = arrayListOf(),
    @SerializedName("plantId") var plantIdList: ArrayList<String> = arrayListOf()
)

