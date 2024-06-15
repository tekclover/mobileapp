package com.tvhht.myapplication.putaway.model

import com.google.gson.annotations.SerializedName

data class BinQuantityConfirm(
    @SerializedName("putawayConfirmedQty") var putawayConfirmedQty: Double? = null,
    @SerializedName("putawayTotalQty") var putawayTotalQty: Double? = null,
    @SerializedName("binLocation") var binLocation: String? = null,
    @SerializedName("isSelected") var isSelected: Boolean? = null,
    @SerializedName("remarks") var remarks: String? = null
)
