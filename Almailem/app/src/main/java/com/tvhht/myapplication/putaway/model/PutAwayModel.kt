
package com.tvhht.myapplication.putaway.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Entity(tableName = "PutAwayList")
@Parcelize
data class PutAwayModel (
    @PrimaryKey(autoGenerate = true)
    var id: Int?=null,
    @SerializedName("languageId") val languageId : String?=null,
    @SerializedName("companyCodeId") val companyCodeId : String?=null,
    @SerializedName("plantId") val plantId : String?=null,
    @SerializedName("warehouseId") val warehouseId : String?=null,
    @SerializedName("preInboundNo") val preInboundNo : String?=null,
    @SerializedName("refDocNumber") val refDocNumber : String?=null,
    @SerializedName("goodsReceiptNo") val goodsReceiptNo : String?=null,
    @SerializedName("palletCode") val palletCode : String?=null,
    @SerializedName("caseCode") val caseCode : String?=null,
    @SerializedName("packBarcodes") val packBarcodes : String?=null,
    @SerializedName("barcodeId") val barcodeId : String?=null,
    @SerializedName("inboundOrderTypeId") val inboundOrderTypeId : Int?=null,
    @SerializedName("putAwayNumber") val putAwayNumber : String?=null,
    @SerializedName("proposedStorageBin") val proposedStorageBin : String?=null,
    @SerializedName("putAwayQuantity") val putAwayQuantity : Double?=null,
    @SerializedName("putAwayUom") val putAwayUom : String?=null,
    @SerializedName("strategyTypeId") val strategyTypeId : Int?=null,
    @SerializedName("strategyNo") val strategyNo : Int?=null,
    @SerializedName("proposedHandlingEquipment") val proposedHandlingEquipment : String?=null,
    @SerializedName("assignedUserId") val assignedUserId : String?=null,
    @SerializedName("statusId") val statusId : Int?=null,
    @SerializedName("quantityType") val quantityType : String?=null,
    @SerializedName("referenceField1") val referenceField1 : String?=null,
    @SerializedName("referenceField2") val referenceField2 : String?=null,
    @SerializedName("referenceField3") val referenceField3 : String?=null,
    @SerializedName("referenceField4") val referenceField4 : String?=null,
    @SerializedName("referenceField5") val referenceField5 : String?=null,
    @SerializedName("referenceField6") val referenceField6 : String?=null,
    @SerializedName("referenceField7") val referenceField7 : String?=null,
    @SerializedName("referenceField8") val referenceField8 : String?=null,
    @SerializedName("referenceField9") val referenceField9 : String?=null,
    @SerializedName("referenceField10") val referenceField10 : String?=null,
    @SerializedName("deletionIndicator") val deletionIndicator : Int?=null,
    @SerializedName("createdBy") val createdBy : String?=null,
    @SerializedName("createdOn") val createdOn : String?=null,
    @SerializedName("confirmedBy") val confirmedBy : String?=null,
    @SerializedName("confirmedOn") val confirmedOn : String?=null,
    @SerializedName("updatedBy") val updatedBy : String?=null,
    @SerializedName("updatedOn") val updatedOn : String?=null,
    @SerializedName("inventoryQuantity") val inventoryQuantity : Double?=null
) :Parcelable

