package com.tvhht.myapplication.picking.model

import android.os.Parcel
import android.os.Parcelable
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
    @SerializedName("manufacturerName") var manufacturerName: String? = null,
    @SerializedName("barcodeId") var barcodeId: String? = null,
    @SerializedName("referenceDocumentType") var referenceDocumentType: String? = null,
    @SerializedName("assignedPickerId") var assignedPickerId: String? = null,
    @SerializedName("salesOrderNumber") var salesOrderNumber: String? = null
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
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readString(),
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readValue(Int::class.java.classLoader) as? Int,
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
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(languageId)
        parcel.writeString(companyCodeId)
        parcel.writeString(plantId)
        parcel.writeString(warehouseId)
        parcel.writeString(preOutboundNo)
        parcel.writeString(refDocNumber)
        parcel.writeString(partnerCode)
        parcel.writeString(pickupNumber)
        parcel.writeValue(lineNumber)
        parcel.writeString(itemCode)
        parcel.writeString(proposedStorageBin)
        parcel.writeString(proposedPackBarCode)
        parcel.writeValue(outboundOrderTypeId)
        parcel.writeValue(pickToQty)
        parcel.writeString(pickUom)
        parcel.writeValue(stockTypeId)
        parcel.writeValue(specialStockIndicatorId)
        parcel.writeString(manufacturerPartNo)
        parcel.writeValue(statusId)
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
        parcel.writeString(remarks)
        parcel.writeString(pickupCreatedBy)
        parcel.writeString(pickupCreatedOn)
        parcel.writeString(pickConfimedBy)
        parcel.writeString(pickConfimedOn)
        parcel.writeString(pickUpdatedBy)
        parcel.writeString(pickUpdatedOn)
        parcel.writeString(pickupReversedBy)
        parcel.writeString(pickupReversedOn)
        parcel.writeString(description)
        parcel.writeString(createdBy)
        parcel.writeString(createdOn)
        parcel.writeString(grUom)
        parcel.writeString(hsnCode)
        parcel.writeString(updatedBy)
        parcel.writeString(updatedOn)
        parcel.writeValue(inventoryQuantity)
        parcel.writeValue(transferQuantity)
        parcel.writeString(manufacturerName)
        parcel.writeString(barcodeId)
        parcel.writeString(referenceDocumentType)
        parcel.writeString(assignedPickerId)
        parcel.writeString(salesOrderNumber)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<PickingCombineResponse> {
        override fun createFromParcel(parcel: Parcel): PickingCombineResponse {
            return PickingCombineResponse(parcel)
        }

        override fun newArray(size: Int): Array<PickingCombineResponse?> {
            return arrayOfNulls(size)
        }
    }
}