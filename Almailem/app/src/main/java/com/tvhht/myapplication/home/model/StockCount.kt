package com.tvhht.myapplication.home.model

import com.google.gson.annotations.SerializedName


data class StockCount(
    @SerializedName("perpertual") var perpertual: Int? = null,
    @SerializedName("periodic") var periodic: Int? = null
)