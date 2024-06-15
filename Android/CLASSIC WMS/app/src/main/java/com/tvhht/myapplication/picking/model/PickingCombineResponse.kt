package com.tvhht.myapplication.picking.model

import com.google.gson.annotations.SerializedName


data class PickingCombineResponse(
    @SerializedName("languageId") var languageId: String? = null,
    @SerializedName("companyCodeId") var companyCodeId: String? = null,
    @SerializedName("plantId") var plantId: String? = null,
    @SerializedName("warehouseId") var warehouseId: String? = null,
    @SerializedName("preOutboundNo") var preOutboundNo: String? = null,
    @SerializedName("refDocNumber") var refDocNumber: String? = null,
    @SerializedName("partnerCode") var partnerCode: String? = null,
    @SerializedName("pickupNumber") var pickupNumber: String? = null,
    @SerializedName("lineNumber") var lineNumber: Int? = null,
    @SerializedName("itemCode") var itemCode: String? = null,
    @SerializedName("proposedStorageBin") var proposedStorageBin: String? = null,
    @SerializedName("proposedPackBarCode") var proposedPackBarCode: String? = null,
    @SerializedName("outboundOrderTypeId") var outboundOrderTypeId: Int? = null,
    @SerializedName("pickToQty") var pickToQty: Int? = null,
    @SerializedName("pickUom") var pickUom: String? = null,
    @SerializedName("stockTypeId") var stockTypeId: Int? = null,
    @SerializedName("specialStockIndicatorId") var specialStockIndicatorId: Int? = null,
    @SerializedName("manufacturerPartNo") var manufacturerPartNo: String? = null,
    @SerializedName("statusId") var statusId: Int? = null,
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
    @SerializedName("remarks") var remarks: String? = null,
    @SerializedName("pickupCreatedBy") var pickupCreatedBy: String? = null,
    @SerializedName("pickupCreatedOn") var pickupCreatedOn: String? = null,
    @SerializedName("pickConfimedBy") var pickConfimedBy: String? = null,
    @SerializedName("pickConfimedOn") var pickConfimedOn: String? = null,
    @SerializedName("pickUpdatedBy") var pickUpdatedBy: String? = null,
    @SerializedName("pickUpdatedOn") var pickUpdatedOn: String? = null,
    @SerializedName("pickupReversedBy") var pickupReversedBy: String? = null,
    @SerializedName("pickupReversedOn") var pickupReversedOn: String? = null,
    @SerializedName("description") val description: String?,
    @SerializedName("createdBy") val createdBy: String?,
    @SerializedName("createdOn") val createdOn: String?,

    @SerializedName("grUom") val grUom: String?,
    @SerializedName("hsnCode") val hsnCode: String?,

    @SerializedName("updatedBy") val updatedBy: String?,
    @SerializedName("updatedOn") val updatedOn: String?,
    @SerializedName("inventoryQuantity") var inventoryQuantity: Int? = null,
    @SerializedName("transferQuantity") var transferQuantity: Int? = null,


    )