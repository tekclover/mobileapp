package com.tvhht.myapplication.picking.model

import com.google.gson.annotations.SerializedName

data class PickingInventory(
@SerializedName("itemCode") val itemCode : List<String>,
@SerializedName("warehouseId") val warehouseId : List<String>,
@SerializedName("storageBin") val storageBin : List<String>,
@SerializedName("packBarcodes") val packBarcodes : List<String>,
@SerializedName("manufacturerName") val manufacturerName : List<String>)
