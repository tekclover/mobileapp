package com.tvhht.myapplication.putaway.model

import com.google.gson.annotations.SerializedName


data class PutAwayConfirmResponse(
        @SerializedName("languageId") var languageId: String? = null,
        @SerializedName("companyCode") var companyCode: String? = null,
        @SerializedName("plantId") var plantId: String? = null,
        @SerializedName("warehouseId") var warehouseId: String? = null,
        @SerializedName("goodsReceiptNo") var goodsReceiptNo: String? = null,
        @SerializedName("preInboundNo") var preInboundNo: String? = null,
        @SerializedName("refDocNumber") var refDocNumber: String? = null,
        @SerializedName("putAwayNumber") var putAwayNumber: String? = null,
        @SerializedName("lineNo") var lineNo: Int? = null,
        @SerializedName("itemCode") var itemCode: String? = null,
        @SerializedName("proposedStorageBin") var proposedStorageBin: String? = null,
        @SerializedName("confirmedStorageBin") var confirmedStorageBin: String? = null,
        @SerializedName("putAwayQuantity") var putAwayQuantity: Int? = null,
        @SerializedName("putAwayUom") var putAwayUom: String? = null,
        @SerializedName("putawayConfirmedQty") var putawayConfirmedQty: Int? = null,
        @SerializedName("variantCode") var variantCode: String? = null,
        @SerializedName("variantSubCode") var variantSubCode: String? = null,
        @SerializedName("storageMethod") var storageMethod: String? = null,
        @SerializedName("batchSerialNumber") var batchSerialNumber: String? = null,
        @SerializedName("outboundOrderTypeId") var outboundOrderTypeId: String? = null,
        @SerializedName("stockTypeId") var stockTypeId: Int? = null,
        @SerializedName("specialStockIndicatorId") var specialStockIndicatorId: Int? = null,
        @SerializedName("referenceOrderNo") var referenceOrderNo: String? = null,
        @SerializedName("statusId") var statusId: Int? = null,
        @SerializedName("description") var description: String? = null,
        @SerializedName("specificationActual") var specificationActual: String? = null,
        @SerializedName("vendorCode") var vendorCode: String? = null,
        @SerializedName("manufacturerPartNo") var manufacturerPartNo: String? = null,
        @SerializedName("hsnCode") var hsnCode: String? = null,
        @SerializedName("itemBarcode") var itemBarcode: String? = null,
        @SerializedName("manufacturerDate") var manufacturerDate: String? = null,
        @SerializedName("expiryDate") var expiryDate: String? = null,
        @SerializedName("storageQty") var storageQty: String? = null,
        @SerializedName("storageTemperature") var storageTemperature: String? = null,
        @SerializedName("storageUom") var storageUom: String? = null,
        @SerializedName("quantityType") var quantityType: String? = null,
        @SerializedName("proposedHandlingEquipment") var proposedHandlingEquipment: String? = null,
        @SerializedName("assignedUserId") var assignedUserId: String? = null,
        @SerializedName("workCenterId") var workCenterId: String? = null,
        @SerializedName("putAwayHandlingEquipment") var putAwayHandlingEquipment: String? = null,
        @SerializedName("putAwayEmployeeId") var putAwayEmployeeId: String? = null,
        @SerializedName("remarks") var remarks: String? = null,
        @SerializedName("referenceField1") var referenceField1: String? = null,
        @SerializedName("referenceField2") var referenceField2: String? = null,
        @SerializedName("referenceField3") var referenceField3: String? = null,
        @SerializedName("referenceField4") var referenceField4: String? = null,
        @SerializedName("referenceField5") var referenceField5: String? = null,
        @SerializedName("referenceField6") var referenceField6: String? = null,
        @SerializedName("referenceField7") var referenceField7: String? = null,
        @SerializedName("referenceField8") var referenceField8: String? = null,
        @SerializedName("referenceField9") var referenceField9: String? = null,
        @SerializedName("referenceField10") var referenceField10: String? = null,
        @SerializedName("deletionIndicator") var deletionIndicator: Int? = null,
        @SerializedName("createdBy") var createdBy: String? = null,
        @SerializedName("createdOn") var createdOn: String? = null,
        @SerializedName("confirmedBy") var confirmedBy: String? = null,
        @SerializedName("confirmedOn") var confirmedOn: String? = null,
        @SerializedName("updatedBy") var updatedBy: String? = null,
        @SerializedName("updatedOn") var updatedOn: String? = null

)
