package com.tvhht.myapplication.putaway.model

import com.google.gson.annotations.SerializedName


data class SearchDescription (
    @SerializedName("itemCode") val itemCode : List<String>,
    @SerializedName("warehouseId") val warehouseId : List<String>,
    @SerializedName("loginUserID") val loginUserID : List<String>)
