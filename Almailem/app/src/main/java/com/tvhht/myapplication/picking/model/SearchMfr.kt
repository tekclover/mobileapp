package com.tvhht.myapplication.picking.model

import com.google.gson.annotations.SerializedName


data class SearchMfr (
    @SerializedName("itemCode") val itemCode : List<String>)
