package com.tvhht.myapplication.quality.model

import com.google.gson.annotations.SerializedName

data class OutboundLineRequest(
    @SerializedName("companyCodeId") var companyCodeIdList: List<String>? = null,
    @SerializedName("languageId") var languageIdList: List<String>? = null,
    @SerializedName("plantId") var plantIdList: List<String>? = null,
    @SerializedName("warehouseId") var warehouseIdList: List<String>? = null,
    @SerializedName("statusId") var statusIdList: List<Int>? = null
)

data class OutboundLineResponse(
    @SerializedName("languageId") var languageId: String? = null,
    @SerializedName("companyCodeId") var companyCodeId: Int? = null,
    @SerializedName("plantId") var plantId: String? = null,
    @SerializedName("warehouseId") var warehouseId: String? = null,
    @SerializedName("preOutboundNo") var preOutboundNo: String? = null,
    @SerializedName("refDocNumber") var refDocNumber: String? = null,
    @SerializedName("partnerCode") var partnerCode: String? = null,
    @SerializedName("lineNumber") var lineNumber: Int? = null,
    @SerializedName("itemCode") var itemCode: String? = null,
    @SerializedName("deliveryOrderNo") var deliveryOrderNo: String? = null,
    @SerializedName("batchSerialNumber") var batchSerialNumber: String? = null,
    @SerializedName("variantCode") var variantCode: String? = null,
    @SerializedName("variantSubCode") var variantSubCode: String? = null,
    @SerializedName("outboundOrderTypeId") var outboundOrderTypeId: Int? = null,
    @SerializedName("statusId") var statusId: Int? = null,
    @SerializedName("stockTypeId") var stockTypeId: Int? = null,
    @SerializedName("specialStockIndicatorId") var specialStockIndicatorId: Int? = null,
    @SerializedName("description") var description: String? = null,
    @SerializedName("orderQty") var orderQty: Int? = null,
    @SerializedName("orderUom") var orderUom: String? = null,
    @SerializedName("deliveryQty") var deliveryQty: Int? = null,
    @SerializedName("deliveryUom") var deliveryUom: String? = null,
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
    @SerializedName("deliveryConfirmedBy") var deliveryConfirmedBy: String? = null,
    @SerializedName("deliveryConfirmedOn") var deliveryConfirmedOn: String? = null,
    @SerializedName("updatedBy") var updatedBy: String? = null,
    @SerializedName("updatedOn") var updatedOn: String? = null,
    @SerializedName("reversedBy") var reversedBy: String? = null,
    @SerializedName("reversedOn") var reversedOn: String? = null,
    @SerializedName("itemText") var itemText: String? = null,
    @SerializedName("mfrPartNumber") var mfrPartNumber: String? = null,
    @SerializedName("companyDescription") var companyDescription: String? = null,
    @SerializedName("plantDescription") var plantDescription: String? = null,
    @SerializedName("warehouseDescription") var warehouseDescription: String? = null,
    @SerializedName("statusDescription") var statusDescription: String? = null,
    @SerializedName("salesInvoiceNumber") var salesInvoiceNumber: String? = null,
    @SerializedName("pickListNumber") var pickListNumber: String? = null,
    @SerializedName("invoiceDate") var invoiceDate: String? = null,
    @SerializedName("deliveryType") var deliveryType: String? = null,
    @SerializedName("customerId") var customerId: String? = null,
    @SerializedName("customerName") var customerName: String? = null,
    @SerializedName("address") var address: String? = null,
    @SerializedName("phoneNumber") var phoneNumber: String? = null,
    @SerializedName("alternateNo") var alternateNo: String? = null,
    @SerializedName("status") var status: String? = null,
    @SerializedName("manufacturerName") var manufacturerName: String? = null,
    @SerializedName("middlewareId") var middlewareId: String? = null,
    @SerializedName("middlewareTable") var middlewareTable: String? = null,
    @SerializedName("middlewareHeaderId") var middlewareHeaderId: String? = null,
    @SerializedName("referenceDocumentType") var referenceDocumentType: String? = null,
    @SerializedName("manufactureFullName") var manufactureFullName: String? = null,
    @SerializedName("salesOrderNumber") var salesOrderNumber: String? = null,
    @SerializedName("tokenNumber") var tokenNumber: String? = null,
    @SerializedName("targetBranchCode") var targetBranchCode: String? = null,
    @SerializedName("transferOrderNo") var transferOrderNo: String? = null,
    @SerializedName("returnOrderNo") var returnOrderNo: String? = null,
    @SerializedName("isCompleted") var isCompleted: String? = null,
    @SerializedName("isCancelled") var isCancelled: String? = null,
    @SerializedName("barcodeId") var barcodeId: String? = null,
    @SerializedName("handlingEquipment") var handlingEquipment: String? = null,
    @SerializedName("customerType") var customerType: String? = null,
    @SerializedName("assignedPickerId") var assignedPickerId: String? = null
)