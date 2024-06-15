package com.tvhht.myapplication.picking.model

import com.google.gson.annotations.SerializedName


data class PickingMfrModel (
    @SerializedName("acceptedQty") val acceptedQty : Int?,
    @SerializedName("assignedUserId") val assignedUserId : String?,
    @SerializedName("batchSerialNumber") val batchSerialNumber : String?,
    @SerializedName("businessPartnerCode") val businessPartnerCode : String?,
    @SerializedName("caseCode") val caseCode : String?,
    @SerializedName("companyCodeId") val companyCodeId : String?,
    @SerializedName("confirmedBy") val confirmedBy : String?,
    @SerializedName("confirmedOn") val confirmedOn : String?,
    @SerializedName("confirmedQty") val confirmedQty : Int?,
    @SerializedName("containerNo") val containerNo : String?,
    @SerializedName("createdBy") val createdBy : String?,
    @SerializedName("createdOn") val createdOn : String?,
    @SerializedName("crossDockAllocationQty") val crossDockAllocationQty : Int?,
    @SerializedName("damageQty") val damageQty : Int?,
    @SerializedName("deletionIndicator") val deletionIndicator : Int?,
    @SerializedName("expiryDate") val expiryDate : String?,
    @SerializedName("goodsReceiptNo") val goodsReceiptNo : String?,
    @SerializedName("grUom") val grUom : String?,
    @SerializedName("hsnCode") val hsnCode : String?,
    @SerializedName("inboundOrderTypeId") val inboundOrderTypeId : Int?,
    @SerializedName("invoiceNo") val invoiceNo : String?,
    @SerializedName("itemBarcode") val itemBarcode : String?,
    @SerializedName("itemCode") val itemCode : String?,
    @SerializedName("itemDescription") val itemDescription : String?,
    @SerializedName("languageId") val languageId : String?,
    @SerializedName("lineNo") val lineNo : Int?,
    @SerializedName("manufacturerDate") val manufacturerDate : String?,
    @SerializedName("manufacturerPartNo") val manufacturerPartNo : String?,
    @SerializedName("orderQty") val orderQty : Int?,
    @SerializedName("orderUom") val orderUom : String?,
    @SerializedName("packBarcodes") val packBarcodes : String?,
    @SerializedName("palletCode") val palletCode : String?,
    @SerializedName("plantId") val plantId : String?,
    @SerializedName("preInboundNo") val preInboundNo : String?,
    @SerializedName("putAwayHandlingEquipment") val putAwayHandlingEquipment : String?,
    @SerializedName("quantityType") val quantityType : String?,
    @SerializedName("receiptQty") val receiptQty : Int?,
    @SerializedName("refDocNumber") val refDocNumber : String?,
    @SerializedName("referenceField1") val referenceField1 : String?,
    @SerializedName("referenceField10") val referenceField10 : String?,
    @SerializedName("referenceField2") val referenceField2 : String?,
    @SerializedName("referenceField3") val referenceField3 : String?,
    @SerializedName("referenceField4") val referenceField4 : String?,
    @SerializedName("referenceField5") val referenceField5 : String?,
    @SerializedName("referenceField6") val referenceField6 : String?,
    @SerializedName("referenceField7") val referenceField7 : String?,
    @SerializedName("referenceField8") val referenceField8 : String?,
    @SerializedName("referenceField9") val referenceField9 : String?,
    @SerializedName("referenceOrderNo") val referenceOrderNo : String?,
    @SerializedName("referenceOrderQty") val referenceOrderQty : Int?,
    @SerializedName("remainingQty") val remainingQty : Int?,
    @SerializedName("remarks") val remarks : String?,
    @SerializedName("specialStockIndicatorId") val specialStockIndicatorId : Int?,
    @SerializedName("specificationActual") val specificationActual : String?,
    @SerializedName("statusId") val statusId : Int?,
    @SerializedName("stockTypeId") val stockTypeId : Int?,
    @SerializedName("storageMethod") val storageMethod : String?,
    @SerializedName("storageQty") val storageQty : Int?,
    @SerializedName("updatedBy") val updatedBy : String?,
    @SerializedName("updatedOn") val updatedOn : String?,
    @SerializedName("variantCode") val variantCode : Int?,
    @SerializedName("variantSubCode") val variantSubCode : String?,
    @SerializedName("variantType") val variantType : String?,
    @SerializedName("warehouseId") val warehouseId : String?
)