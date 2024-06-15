package com.tvhht.myapplication.picking.model


import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity
data class PickingSubmitRequest(@PrimaryKey(autoGenerate = true) val pickingID: Int,
    @SerializedName("actualHeNo") var actualHeNo: String? = null,
    @SerializedName("assignedPickerId") var assignedPickerId: String? = null,
    @SerializedName("batchSerialNumber") var batchSerialNumber: String? = null,
    @SerializedName("companyCodeId") var companyCodeId: Int? = null,
    @SerializedName("deletionIndicator") var deletionIndicator: Int? = null,
    @SerializedName("description") var description: String? = null,
    @SerializedName("itemCode") var itemCode: String? = null,
    @SerializedName("languageId") var languageId: String? = null,
    @SerializedName("lineNumber") var lineNumber: Int? = null,
    @SerializedName("manufacturerPartNo") var manufacturerPartNo: String? = null,
    @SerializedName("partnerCode") var partnerCode: String? = null,
    @SerializedName("pickCaseCode") var pickCaseCode: String? = null,
    @SerializedName("pickConfirmQty") var pickConfirmQty: Int? = null,
    @SerializedName("allocatedQty") var allocatedQty: Int? = null,
    @SerializedName("pickPalletCode") var pickPalletCode: String? = null,
    @SerializedName("pickQty") var pickQty: String? = null,
    @SerializedName("pickUom") var pickUom: String? = null,
    @SerializedName("pickedPackCode") var pickedPackCode: String? = null,
    @SerializedName("pickedStorageBin") var pickedStorageBin: String? = null,
    @SerializedName("pickupConfirmedBy") var pickupConfirmedBy: String? = null,
    @SerializedName("pickupConfirmedOn") var pickupConfirmedOn: String? = null,
    @SerializedName("pickupCreatedBy") var pickupCreatedBy: String? = null,
    @SerializedName("pickupCreatedOn") var pickupCreatedOn: String? = null,
    @SerializedName("pickupNumber") var pickupNumber: String? = null,
    @SerializedName("pickupReversedBy") var pickupReversedBy: String? = null,
    @SerializedName("pickupReversedOn") var pickupReversedOn: String? = null,
    @SerializedName("pickupUpdatedBy") var pickupUpdatedBy: String? = null,
    @SerializedName("pickupupdatedOn") var pickupupdatedOn: String? = null,
    @SerializedName("plantId") var plantId: String? = null,
    @SerializedName("preOutboundNo") var preOutboundNo: String? = null,
    @SerializedName("refDocNumber") var refDocNumber: String? = null,
    @SerializedName("referenceField1") var referenceField1: String? = null,
    @SerializedName("referenceField10") var referenceField10: String? = null,
    @SerializedName("referenceField2") var referenceField2: String? = null,
    @SerializedName("referenceField3") var referenceField3: String? = null,
    @SerializedName("referenceField4") var referenceField4: String? = null,
    @SerializedName("referenceField5") var referenceField5: String? = null,
    @SerializedName("referenceField6") var referenceField6: String? = null,
    @SerializedName("referenceField7") var referenceField7: String? = null,
    @SerializedName("referenceField8") var referenceField8: String? = null,
    @SerializedName("referenceField9") var referenceField9: String? = null,
    @SerializedName("specialStockIndicatorId") var specialStockIndicatorId: Int? = null,
    @SerializedName("statusId") var statusId: Int? = null,
    @SerializedName("stockTypeId") var stockTypeId: Int? = null,
    @SerializedName("variantCode") var variantCode: Int? = null,
    @SerializedName("variantSubCode") var variantSubCode: String? = null,
    @SerializedName("warehouseId") var warehouseId: String? = null

)
