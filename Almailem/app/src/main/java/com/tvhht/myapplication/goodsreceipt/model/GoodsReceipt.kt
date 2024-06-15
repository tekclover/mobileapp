package com.tvhht.myapplication.goodsreceipt.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

data class GoodsReceiptRequest(
    @SerializedName("companyCodeId") val companyCodeIdList: List<String>? = null,
    @SerializedName("languageId") val languageIdList: List<String>? = null,
    @SerializedName("plantId") val plantIdList: List<String>? = null,
    @SerializedName("warehouseId") val warehouseIdList: List<String>? = null,
    @SerializedName("statusId") val statusIdList: List<Int>? = null,
    @SerializedName("inboundOrderTypeId") val inboundOrderTypeIdList: List<Int>? = null,
    @SerializedName("refDocNumber") val refDocNumberList: List<String>? = null
)

data class GoodsReceiptResponse(
    @SerializedName("languageId") var languageId: String? = null,
    @SerializedName("companyCodeId") var companyCodeId: String? = null,
    @SerializedName("plantId") var plantId: String? = null,
    @SerializedName("warehouseId") var warehouseId: String? = null,
    @SerializedName("preInboundNo") var preInboundNo: String? = null,
    @SerializedName("refDocNumber") var refDocNumber: String? = null,
    @SerializedName("stagingNo") var stagingNo: String? = null,
    @SerializedName("goodsReceiptNo") var goodsReceiptNo: String? = null,
    @SerializedName("palletCode") var palletCode: String? = null,
    @SerializedName("caseCode") var caseCode: String? = null,
    @SerializedName("inboundOrderTypeId") var inboundOrderTypeId: Int? = null,
    @SerializedName("statusId") var statusId: Int? = null,
    @SerializedName("grMethod") var grMethod: String? = null,
    @SerializedName("containerReceiptNo") var containerReceiptNo: String? = null,
    @SerializedName("dockAllocationNo") var dockAllocationNo: String? = null,
    @SerializedName("containerNo") var containerNo: String? = null,
    @SerializedName("vechicleNo") var vechicleNo: String? = null,
    @SerializedName("expectedArrivalDate") var expectedArrivalDate: String? = null,
    @SerializedName("goodsReceiptDate") var goodsReceiptDate: String? = null,
    @SerializedName("deletionIndicator") var deletionIndicator: Int? = null,
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
    @SerializedName("createdBy") var createdBy: String? = null,
    @SerializedName("createdOn") var createdOn: String? = null,
    @SerializedName("updatedBy") var updatedBy: String? = null,
    @SerializedName("updatedOn") var updatedOn: String? = null,
    @SerializedName("confirmedBy") var confirmedBy: String? = null,
    @SerializedName("confirmedOn") var confirmedOn: String? = null,
    @SerializedName("acceptedQuantity") var acceptedQuantity: String? = null,
    @SerializedName("damagedQuantity") var damagedQuantity: String? = null,
    @SerializedName("companyDescription") var companyDescription: String? = null,
    @SerializedName("plantDescription") var plantDescription: String? = null,
    @SerializedName("warehouseDescription") var warehouseDescription: String? = null,
    @SerializedName("statusDescription") var statusDescription: String? = null,
    @SerializedName("middlewareId") var middlewareId: String? = null,
    @SerializedName("middlewareTable") var middlewareTable: String? = null,
    @SerializedName("manufactureFullName") var manufactureFullName: String? = null,
    @SerializedName("referenceDocumentType") var referenceDocumentType: String? = null
)

data class SelectedDocumentRequest(
    @SerializedName("caseCode") val caseCodeList: List<String>? = null,
    @SerializedName("stagingNo") val stagingNoList: List<String>? = null
)

data class SelectedDocumentResponse(
    @SerializedName("languageId") var languageId: String? = null,
    @SerializedName("companyCode") var companyCode: String? = null,
    @SerializedName("plantId") var plantId: String? = null,
    @SerializedName("warehouseId") var warehouseId: String? = null,
    @SerializedName("preInboundNo") var preInboundNo: String? = null,
    @SerializedName("refDocNumber") var refDocNumber: String? = null,
    @SerializedName("stagingNo") var stagingNo: String? = null,
    @SerializedName("palletCode") var palletCode: String? = null,
    @SerializedName("caseCode") var caseCode: String? = null,
    @SerializedName("lineNo") var lineNo: Int? = null,
    @SerializedName("itemCode") var itemCode: String? = null,
    @SerializedName("inboundOrderTypeId") var inboundOrderTypeId: Int? = null,
    @SerializedName("variantCode") var variantCode: String? = null,
    @SerializedName("variantSubCode") var variantSubCode: String? = null,
    @SerializedName("batchSerialNumber") var batchSerialNumber: String? = null,
    @SerializedName("stockTypeId") var stockTypeId: Int? = null,
    @SerializedName("specialStockIndicatorId") var specialStockIndicatorId: Int? = null,
    @SerializedName("storageMethod") var storageMethod: String? = null,
    @SerializedName("statusId") var statusId: Int? = null,
    @SerializedName("businessPartnerCode") var businessPartnerCode: String? = null,
    @SerializedName("containerNo") var containerNo: String? = null,
    @SerializedName("invoiceNo") var invoiceNo: String? = null,
    @SerializedName("orderQty") var orderQty: Int? = null,
    @SerializedName("orderUom") var orderUom: String? = null,
    @SerializedName("itemQtyPerPallet") var itemQtyPerPallet: String? = null,
    @SerializedName("itemQtyPerCase") var itemQtyPerCase: String? = null,
    @SerializedName("assignedUserId") var assignedUserId: String? = null,
    @SerializedName("itemDescription") var itemDescription: String? = null,
    @SerializedName("manufacturerPartNo") var manufacturerPartNo: String? = null,
    @SerializedName("hsnCode") var hsnCode: String? = null,
    @SerializedName("variantType") var variantType: String? = null,
    @SerializedName("specificationActual") var specificationActual: String? = null,
    @SerializedName("itemBarcode") var itemBarcode: String? = null,
    @SerializedName("referenceOrderNo") var referenceOrderNo: String? = null,
    @SerializedName("referenceOrderQty") var referenceOrderQty: String? = null,
    @SerializedName("crossDockAllocationQty") var crossDockAllocationQty: String? = null,
    @SerializedName("remark") var remarks: String? = null,
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
    @SerializedName("updatedBy") var updatedBy: String? = null,
    @SerializedName("updatedOn") var updatedOn: String? = null,
    @SerializedName("confirmedBy") var confirmedBy: String? = null,
    @SerializedName("confirmedOn") var confirmedOn: String? = null,
    @SerializedName("middlewareId") var middlewareId: String? = null,
    @SerializedName("middlewareTable") var middlewareTable: String? = null,
    @SerializedName("companyDescription") var companyDescription: String? = null,
    @SerializedName("plantDescription") var plantDescription: String? = null,
    @SerializedName("warehouseDescription") var warehouseDescription: String? = null,
    @SerializedName("statusDescription") var statusDescription: String? = null,
    @SerializedName("inventoryQuantity") var inventoryQuantity: Double? = null,
    @SerializedName("manufacturerCode") var manufacturerCode: String? = null,
    @SerializedName("manufacturerName") var manufacturerName: String? = null,
    @SerializedName("origin") var origin: String? = null,
    @SerializedName("brand") var brand: String? = null,
    @SerializedName("partner_item_barcode") var partnerItemBarcode: String? = null,
    @SerializedName("rec_accept_qty") var recAcceptQty: Int? = null,
    @SerializedName("rec_damage_qty") var recDamageQty: Int? = null,
    @SerializedName("middlewareHeaderId") var middlewareHeaderId: String? = null,
    @SerializedName("purchaseOrderNumber") var purchaseOrderNumber: String? = null,
    @SerializedName("referenceDocumentType") var referenceDocumentType: String? = null,
    @SerializedName("varianceQty") var varianceQty: Int? = null,
    @SerializedName("putAwayHandlingEquipment") var putAwayHandlingEquipment: String? = null,
    @SerializedName("packBarcodes") var packBarcodes: ArrayList<PackBarcodeResponse>? = ArrayList(),
    @SerializedName("goodsReceiptNo") var goodsReceiptNo: String? = null,
    @SerializedName("damageQty") var damageQty: Int? = null,
    @SerializedName("acceptedQty") var acceptedQty: Int? = null,
    @SerializedName("barCodeType") var barCodeType: Int? = null,
    @SerializedName("length") var length: String? = null,
    @SerializedName("width") var width: String? = null,
    @SerializedName("height") var height: String? = null,
    @SerializedName("cbmQty") var cbmQty: Int? = null,
    @SerializedName("acceptedCbmQty") var acceptedCbmQty: Int? = null,
    @SerializedName("rejectedCbmQty") var rejectedCbmQty: Int? = null,
    @SerializedName("barcodeId") var barcodeId: String? = null,
    @SerializedName("goodReceiptQty") var goodReceiptQty: Int? = null,
    @SerializedName("noOfDamageLabel") var noOfDamageLabel: Int? = null,
    @SerializedName("noOfAcceptedLabel") var noOfAcceptedLabel: Int? = null,
    @SerializedName("totalLabel") var totalLabel: Int? = null,
    @SerializedName("packBarcodesState") var packBarcodesState: Boolean? = null,
    var isSelected: Boolean = false
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readString(),
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readString(),
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readValue(Double::class.java.classLoader) as? Double,
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readString(),
        parcel.createTypedArrayList(PackBarcodeResponse),
        parcel.readString(),
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readString(),
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readValue(Boolean::class.java.classLoader) as? Boolean,
        parcel.readByte() != 0.toByte()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(languageId)
        parcel.writeString(companyCode)
        parcel.writeString(plantId)
        parcel.writeString(warehouseId)
        parcel.writeString(preInboundNo)
        parcel.writeString(refDocNumber)
        parcel.writeString(stagingNo)
        parcel.writeString(palletCode)
        parcel.writeString(caseCode)
        parcel.writeValue(lineNo)
        parcel.writeString(itemCode)
        parcel.writeValue(inboundOrderTypeId)
        parcel.writeString(variantCode)
        parcel.writeString(variantSubCode)
        parcel.writeString(batchSerialNumber)
        parcel.writeValue(stockTypeId)
        parcel.writeValue(specialStockIndicatorId)
        parcel.writeString(storageMethod)
        parcel.writeValue(statusId)
        parcel.writeString(businessPartnerCode)
        parcel.writeString(containerNo)
        parcel.writeString(invoiceNo)
        parcel.writeValue(orderQty)
        parcel.writeString(orderUom)
        parcel.writeString(itemQtyPerPallet)
        parcel.writeString(itemQtyPerCase)
        parcel.writeString(assignedUserId)
        parcel.writeString(itemDescription)
        parcel.writeString(manufacturerPartNo)
        parcel.writeString(hsnCode)
        parcel.writeString(variantType)
        parcel.writeString(specificationActual)
        parcel.writeString(itemBarcode)
        parcel.writeString(referenceOrderNo)
        parcel.writeString(referenceOrderQty)
        parcel.writeString(crossDockAllocationQty)
        parcel.writeString(remarks)
        parcel.writeString(referenceField1)
        parcel.writeString(referenceField2)
        parcel.writeString(referenceField3)
        parcel.writeString(referenceField4)
        parcel.writeString(referenceField5)
        parcel.writeString(referenceField6)
        parcel.writeString(referenceField7)
        parcel.writeString(referenceField8)
        parcel.writeString(referenceField9)
        parcel.writeString(referenceField10)
        parcel.writeValue(deletionIndicator)
        parcel.writeString(createdBy)
        parcel.writeString(createdOn)
        parcel.writeString(updatedBy)
        parcel.writeString(updatedOn)
        parcel.writeString(confirmedBy)
        parcel.writeString(confirmedOn)
        parcel.writeString(middlewareId)
        parcel.writeString(middlewareTable)
        parcel.writeString(companyDescription)
        parcel.writeString(plantDescription)
        parcel.writeString(warehouseDescription)
        parcel.writeString(statusDescription)
        parcel.writeValue(inventoryQuantity)
        parcel.writeString(manufacturerCode)
        parcel.writeString(manufacturerName)
        parcel.writeString(origin)
        parcel.writeString(brand)
        parcel.writeString(partnerItemBarcode)
        parcel.writeValue(recAcceptQty)
        parcel.writeValue(recDamageQty)
        parcel.writeString(middlewareHeaderId)
        parcel.writeString(purchaseOrderNumber)
        parcel.writeString(referenceDocumentType)
        parcel.writeValue(varianceQty)
        parcel.writeString(putAwayHandlingEquipment)
        parcel.writeTypedList(packBarcodes)
        parcel.writeString(goodsReceiptNo)
        parcel.writeValue(damageQty)
        parcel.writeValue(acceptedQty)
        parcel.writeValue(barCodeType)
        parcel.writeString(length)
        parcel.writeString(width)
        parcel.writeString(height)
        parcel.writeValue(cbmQty)
        parcel.writeValue(acceptedCbmQty)
        parcel.writeValue(rejectedCbmQty)
        parcel.writeString(barcodeId)
        parcel.writeValue(goodReceiptQty)
        parcel.writeValue(noOfDamageLabel)
        parcel.writeValue(noOfAcceptedLabel)
        parcel.writeValue(totalLabel)
        parcel.writeValue(packBarcodesState)
        parcel.writeByte(if (isSelected) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<SelectedDocumentResponse> {
        override fun createFromParcel(parcel: Parcel): SelectedDocumentResponse {
            return SelectedDocumentResponse(parcel)
        }

        override fun newArray(size: Int): Array<SelectedDocumentResponse?> {
            return arrayOfNulls(size)
        }
    }
}

data class CBMRequest(
    @SerializedName("companyCodeId") val companyCodeIdList: List<String>? = null,
    @SerializedName("languageId") val languageIdList: List<String>? = null,
    @SerializedName("plantId") val plantIdList: List<String>? = null,
    @SerializedName("warehouseId") val warehouseIdList: List<String>? = null,
    @SerializedName("itemCode") val itemCodeList: List<String>? = null,
    @SerializedName("manufacturerPartNo") val manufacturerPartNoList: List<String>? = null
)

data class CBMResponse(
    @SerializedName("length") var length: String? = null,
    @SerializedName("width") var width: String? = null,
    @SerializedName("height") var height: String? = null
)

data class GRLineSubmitResponse(
    @SerializedName("languageId") var languageId: String? = null,
    @SerializedName("companyCode") var companyCode: String? = null,
    @SerializedName("plantId") var plantId: String? = null,
    @SerializedName("warehouseId") var warehouseId: String? = null,
    @SerializedName("preInboundNo") var preInboundNo: String? = null,
    @SerializedName("refDocNumber") var refDocNumber: String? = null,
    @SerializedName("goodsReceiptNo") var goodsReceiptNo: String? = null,
    @SerializedName("palletCode") var palletCode: String? = null,
    @SerializedName("caseCode") var caseCode: String? = null,
    @SerializedName("packBarcodes") var packBarcodes: String? = null,
    @SerializedName("lineNo") var lineNo: Int? = null,
    @SerializedName("itemCode") var itemCode: String? = null,
    @SerializedName("inboundOrderTypeId") var inboundOrderTypeId: Int? = null,
    @SerializedName("variantCode") var variantCode: String? = null,
    @SerializedName("variantSubCode") var variantSubCode: String? = null,
    @SerializedName("batchSerialNumber") var batchSerialNumber: String? = null,
    @SerializedName("stockTypeId") var stockTypeId: Int? = null,
    @SerializedName("specialStockIndicatorId") var specialStockIndicatorId: Int? = null,
    @SerializedName("storageMethod") var storageMethod: String? = null,
    @SerializedName("statusId") var statusId: Int? = null,
    @SerializedName("businessPartnerCode") var businessPartnerCode: String? = null,
    @SerializedName("containerNo") var containerNo: String? = null,
    @SerializedName("invoiceNo") var invoiceNo: String? = null,
    @SerializedName("itemDescription") var itemDescription: String? = null,
    @SerializedName("manufacturerPartNo") var manufacturerPartNo: String? = null,
    @SerializedName("hsnCode") var hsnCode: String? = null,
    @SerializedName("variantType") var variantType: String? = null,
    @SerializedName("specificationActual") var specificationActual: String? = null,
    @SerializedName("itemBarcode") var itemBarcode: String? = null,
    @SerializedName("orderQty") var orderQty: Int? = null,
    @SerializedName("orderUom") var orderUom: String? = null,
    @SerializedName("receiptQty") var receiptQty: String? = null,
    @SerializedName("grUom") var grUom: String? = null,
    @SerializedName("acceptedQty") var acceptedQty: Int? = null,
    @SerializedName("damageQty") var damageQty: Int? = null,
    @SerializedName("quantityType") var quantityType: String? = null,
    @SerializedName("assignedUserId") var assignedUserId: String? = null,
    @SerializedName("putAwayHandlingEquipment") var putAwayHandlingEquipment: String? = null,
    @SerializedName("confirmedQty") var confirmedQty: String? = null,
    @SerializedName("remainingQty") var remainingQty: String? = null,
    @SerializedName("referenceOrderNo") var referenceOrderNo: String? = null,
    @SerializedName("referenceOrderQty") var referenceOrderQty: String? = null,
    @SerializedName("crossDockAllocationQty") var crossDockAllocationQty: String? = null,
    @SerializedName("manufacturerDate") var manufacturerDate: String? = null,
    @SerializedName("expiryDate") var expiryDate: String? = null,
    @SerializedName("storageQty") var storageQty: String? = null,
    @SerializedName("remarks") var remarks: String? = null,
    @SerializedName("cbmUnit") var cbmUnit: String? = null,
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
    @SerializedName("updatedBy") var updatedBy: String? = null,
    @SerializedName("updatedOn") var updatedOn: String? = null,
    @SerializedName("confirmedBy") var confirmedBy: String? = null,
    @SerializedName("confirmedOn") var confirmedOn: String? = null,
    @SerializedName("inventoryQuantity") var inventoryQuantity: Double? = null,
    @SerializedName("barcodeId") var barcodeId: String? = null,
    @SerializedName("cbm") var cbm: Int? = null,
    @SerializedName("manufacturerCode") var manufacturerCode: String? = null,
    @SerializedName("manufacturerName") var manufacturerName: String? = null,
    @SerializedName("origin") var origin: String? = null,
    @SerializedName("brand") var brand: String? = null,
    @SerializedName("rejectType") var rejectType: String? = null,
    @SerializedName("rejectReason") var rejectReason: String? = null,
    @SerializedName("cbmQuantity") var cbmQuantity: Int? = null,
    @SerializedName("companyDescription") var companyDescription: String? = null,
    @SerializedName("plantDescription") var plantDescription: String? = null,
    @SerializedName("warehouseDescription") var warehouseDescription: String? = null,
    @SerializedName("statusDescription") var statusDescription: String? = null,
    @SerializedName("interimStorageBin") var interimStorageBin: String? = null,
    @SerializedName("middlewareId") var middlewareId: String? = null,
    @SerializedName("middlewareTable") var middlewareTable: String? = null,
    @SerializedName("manufactureFullName") var manufactureFullName: String? = null,
    @SerializedName("middlewareHeaderId") var middlewareHeaderId: String? = null,
    @SerializedName("purchaseOrderNumber") var purchaseOrderNumber: String? = null,
    @SerializedName("referenceDocumentType") var referenceDocumentType: String? = null
)

data class HHTUserRequest(
    @SerializedName("companyCodeId") var companyCodeIdList: List<String>? = null,
    @SerializedName("languageId") var languageIdList: List<String>? = null,
    @SerializedName("warehouseId") var warehouseIdList: List<String>? = null,
    @SerializedName("plantId") var plantIdList: List<String>? = null
)

data class HHTUser(
    @SerializedName("userId") val userId: String? = null,
    @SerializedName("userName") val userName: String? = null,
    var isChecked: Boolean = false
)

data class PackBarcodeResponse(
    @SerializedName("quantityType") var quantityType: String? = null,
    @SerializedName("barcode") var barcode: String? = null,
    @SerializedName("cbm") var cbm: Int? = null,
    @SerializedName("cbmQuantity") var cbmQuantity: Int? = null
):Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readValue(Int::class.java.classLoader) as? Int
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(quantityType)
        parcel.writeString(barcode)
        parcel.writeValue(cbm)
        parcel.writeValue(cbmQuantity)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<PackBarcodeResponse> {
        override fun createFromParcel(parcel: Parcel): PackBarcodeResponse {
            return PackBarcodeResponse(parcel)
        }

        override fun newArray(size: Int): Array<PackBarcodeResponse?> {
            return arrayOfNulls(size)
        }
    }
}

data class FindImPartnerRequest(
    @SerializedName("companyCodeId") val companyCodeIdList: List<String>? = null,
    @SerializedName("languageId") val languageIdList: List<String>? = null,
    @SerializedName("plantId") val plantIdList: List<String>? = null,
    @SerializedName("warehouseId") val warehouseIdList: List<String>? = null,
    @SerializedName("itemCode") val itemCodeList: List<String>? = null,
    @SerializedName("businessPartnerCode") val businessPartnerCode: String? = null
)

data class FindImPartnerResponse(
    @SerializedName("languageId") var languageId: String? = null,
    @SerializedName("companyCodeId") var companyCodeId: String? = null,
    @SerializedName("plantId") var plantId: String? = null,
    @SerializedName("warehouseId") var warehouseId: String? = null,
    @SerializedName("itemCode") var itemCode: String? = null,
    @SerializedName("businessPartnerType") var businessPartnerType: String? = null,
    @SerializedName("businessPartnerCode") var businessPartnerCode: String? = null,
    @SerializedName("partnerItemBarcode") var partnerItemBarcode: String? = null,
    @SerializedName("manufacturerCode") var manufacturerCode: String? = null,
    @SerializedName("manufacturerName") var manufacturerName: String? = null,
    @SerializedName("partnerName") var partnerName: String? = null,
    @SerializedName("partnerItemNo") var partnerItemNo: String? = null,
    @SerializedName("vendorItemBarcode") var vendorItemBarcode: String? = null,
    @SerializedName("mfrBarcode") var mfrBarcode: String? = null,
    @SerializedName("brandName") var brandName: String? = null,
    @SerializedName("stock") var stock: String? = null,
    @SerializedName("stockUom") var stockUom: String? = null,
    @SerializedName("statusId") var statusId: String? = null,
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
    @SerializedName("updatedBy") var updatedBy: String? = null,
    @SerializedName("updatedOn") var updatedOn: String? = null
)

data class CreateImPartnerRequest(
    @SerializedName("companyCodeId") val companyCodeId: String? = null,
    @SerializedName("languageId") val languageId: String? = null,
    @SerializedName("plantId") val plantId: String? = null,
    @SerializedName("warehouseId") val warehouseId: String? = null,
    @SerializedName("businessPartnerType") val businessPartnerType: String? = null,
    @SerializedName("businessPartnerCode") val businessPartnerCode: String? = null,
    @SerializedName("itemCode") val itemCode: String? = null,
    @SerializedName("partnerItemBarcode") val partnerItemBarcode: String? = null,
    @SerializedName("manufacturerCode") val manufacturerCode: String? = null,
    @SerializedName("manufacturerName") val manufacturerName: String? = null,
    @SerializedName("brand") val brand: String? = null
)

data class CreateImPartnerResponse(
    @SerializedName("languageId") var languageId: String? = null,
    @SerializedName("companyCodeId") var companyCodeId: String? = null,
    @SerializedName("plantId") var plantId: String? = null,
    @SerializedName("warehouseId") var warehouseId: String? = null,
    @SerializedName("itemCode") var itemCode: String? = null,
    @SerializedName("businessPartnerType") var businessPartnerType: String? = null,
    @SerializedName("businessPartnerCode") var businessPartnerCode: String? = null,
    @SerializedName("partnerItemBarcode") var partnerItemBarcode: String? = null,
    @SerializedName("manufacturerCode") var manufacturerCode: String? = null,
    @SerializedName("manufacturerName") var manufacturerName: String? = null,
    @SerializedName("partnerName") var partnerName: String? = null,
    @SerializedName("partnerItemNo") var partnerItemNo: String? = null,
    @SerializedName("vendorItemBarcode") var vendorItemBarcode: String? = null,
    @SerializedName("mfrBarcode") var mfrBarcode: String? = null,
    @SerializedName("brandName") var brandName: String? = null,
    @SerializedName("stock") var stock: String? = null,
    @SerializedName("stockUom") var stockUom: String? = null,
    @SerializedName("statusId") var statusId: String? = null,
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
    @SerializedName("updatedBy") var updatedBy: String? = null,
    @SerializedName("updatedOn") var updatedOn: String? = null
)

data class StagingLineUpdateResponse(
    @SerializedName("languageId") var languageId: String? = null,
    @SerializedName("companyCode") var companyCode: String? = null,
    @SerializedName("plantId") var plantId: String? = null,
    @SerializedName("warehouseId") var warehouseId: String? = null,
    @SerializedName("preInboundNo") var preInboundNo: String? = null,
    @SerializedName("refDocNumber") var refDocNumber: String? = null,
    @SerializedName("stagingNo") var stagingNo: String? = null,
    @SerializedName("palletCode") var palletCode: String? = null,
    @SerializedName("caseCode") var caseCode: String? = null,
    @SerializedName("lineNo") var lineNo: Int? = null,
    @SerializedName("itemCode") var itemCode: String? = null,
    @SerializedName("inboundOrderTypeId") var inboundOrderTypeId: Int? = null,
    @SerializedName("variantCode") var variantCode: String? = null,
    @SerializedName("variantSubCode") var variantSubCode: String? = null,
    @SerializedName("batchSerialNumber") var batchSerialNumber: String? = null,
    @SerializedName("stockTypeId") var stockTypeId: Int? = null,
    @SerializedName("specialStockIndicatorId") var specialStockIndicatorId: Int? = null,
    @SerializedName("storageMethod") var storageMethod: String? = null,
    @SerializedName("statusId") var statusId: Int? = null,
    @SerializedName("businessPartnerCode") var businessPartnerCode: String? = null,
    @SerializedName("containerNo") var containerNo: String? = null,
    @SerializedName("invoiceNo") var invoiceNo: String? = null,
    @SerializedName("orderQty") var orderQty: Int? = null,
    @SerializedName("orderUom") var orderUom: String? = null,
    @SerializedName("itemQtyPerPallet") var itemQtyPerPallet: String? = null,
    @SerializedName("itemQtyPerCase") var itemQtyPerCase: String? = null,
    @SerializedName("assignedUserId") var assignedUserId: String? = null,
    @SerializedName("itemDescription") var itemDescription: String? = null,
    @SerializedName("manufacturerPartNo") var manufacturerPartNo: String? = null,
    @SerializedName("hsnCode") var hsnCode: String? = null,
    @SerializedName("variantType") var variantType: String? = null,
    @SerializedName("specificationActual") var specificationActual: String? = null,
    @SerializedName("itemBarcode") var itemBarcode: String? = null,
    @SerializedName("referenceOrderNo") var referenceOrderNo: String? = null,
    @SerializedName("referenceOrderQty") var referenceOrderQty: String? = null,
    @SerializedName("crossDockAllocationQty") var crossDockAllocationQty: String? = null,
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
    @SerializedName("updatedBy") var updatedBy: String? = null,
    @SerializedName("updatedOn") var updatedOn: String? = null,
    @SerializedName("confirmedBy") var confirmedBy: String? = null,
    @SerializedName("confirmedOn") var confirmedOn: String? = null,
    @SerializedName("middlewareId") var middlewareId: String? = null,
    @SerializedName("middlewareTable") var middlewareTable: String? = null,
    @SerializedName("companyDescription") var companyDescription: String? = null,
    @SerializedName("plantDescription") var plantDescription: String? = null,
    @SerializedName("warehouseDescription") var warehouseDescription: String? = null,
    @SerializedName("statusDescription") var statusDescription: String? = null,
    @SerializedName("inventoryQuantity") var inventoryQuantity: Int? = null,
    @SerializedName("manufacturerCode") var manufacturerCode: String? = null,
    @SerializedName("manufacturerName") var manufacturerName: String? = null,
    @SerializedName("manufacturerFullName") var manufacturerFullName: String? = null,
    @SerializedName("origin") var origin: String? = null,
    @SerializedName("brand") var brand: String? = null,
    @SerializedName("partner_item_barcode") var partnerItemBarcode: String? = null,
    @SerializedName("rec_accept_qty") var recAcceptQty: Int? = null,
    @SerializedName("rec_damage_qty") var recDamageQty: Int? = null,
    @SerializedName("middlewareHeaderId") var middlewareHeaderId: String? = null,
    @SerializedName("purchaseOrderNumber") var purchaseOrderNumber: String? = null,
    @SerializedName("referenceDocumentType") var referenceDocumentType: String? = null,
    @SerializedName("branchCode") var branchCode: String? = null,
    @SerializedName("transferOrderNo") var transferOrderNo: String? = null,
    @SerializedName("isCompleted") var isCompleted: String? = null
)
data class ErrorResponse(
    @SerializedName("timestamp") var timestamp: String? = null,
    @SerializedName("status") var status: Int? = null,
    @SerializedName("error") var error: String? = null
)

