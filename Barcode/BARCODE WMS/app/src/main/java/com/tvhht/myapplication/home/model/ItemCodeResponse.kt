package com.tvhht.myapplication.home.model

import com.google.gson.annotations.SerializedName


data class ItemCodeResponse (

    @SerializedName("itemCode"    ) var itemCode    : String? = null,
    @SerializedName("description" ) var description : String? = null

)