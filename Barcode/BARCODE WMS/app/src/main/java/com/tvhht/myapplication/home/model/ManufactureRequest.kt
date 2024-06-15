package com.tvhht.myapplication.home.model

import com.google.gson.annotations.SerializedName


data class ManufactureRequest(
    @SerializedName("itemCode") val itemCode: List<String>,
   )
