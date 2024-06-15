package com.tvhht.myapplication.putaway.model

import com.google.gson.annotations.SerializedName

data class BinQuantityConfirm(
    @SerializedName("putawayConfirmedQty") var putawayConfirmedQty: Int?=null,
    @SerializedName("putawayTotalQty") var putawayTotalQty: Int?=null,
    @SerializedName("binLocation") var binLocation: String?=null,
    @SerializedName("isSelected") var isSelected: Boolean?=null
)
