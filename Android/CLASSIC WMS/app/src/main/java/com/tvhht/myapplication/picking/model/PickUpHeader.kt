package com.tvhht.myapplication.picking.model

import com.google.gson.annotations.SerializedName


data class PickUpHeader(
    @SerializedName("warehouseId") val warehouseId: List<String>,
    @SerializedName("assignedPickerId") val assignedPicker: List<String>,
    @SerializedName("statusId") val statusId: List<Int>
)
