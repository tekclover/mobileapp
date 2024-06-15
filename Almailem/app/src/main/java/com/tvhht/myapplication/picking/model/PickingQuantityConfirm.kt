package com.tvhht.myapplication.picking.model

import com.google.gson.annotations.SerializedName

data class PickingQuantityConfirm(
    @SerializedName("pickedQty") var pickedQty: Int? = null,
    @SerializedName("inventoryQty") var inventoryQty: Int? = null,
    @SerializedName("palletId") var palletId: String? = null,
    @SerializedName("binLocation") var binLocation: String? = null,
    @SerializedName("outboundOrderTypeId") var outboundOrderTypeId: Int? = null,
    @SerializedName("heNumber") var heNumber: String? = null,
    @SerializedName("itemCode") var itemCode: String? = null,
    @SerializedName("isSelected") var isSelected: Boolean? = null,
    @SerializedName("remark") var remark: String? = null,
    @SerializedName("barcodeId") var barcodeId: String? = null
)

