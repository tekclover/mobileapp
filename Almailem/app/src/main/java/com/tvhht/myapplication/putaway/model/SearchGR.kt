package com.tvhht.myapplication.putaway.model

import com.google.gson.annotations.SerializedName


data class SearchGR (
    @SerializedName("packBarcodes") val packBarcodes : List<String>,
    @SerializedName("preInboundNo") val preInboundNo : List<String>,
    @SerializedName("refDocNumber") val refDOcNumber : List<String>)
