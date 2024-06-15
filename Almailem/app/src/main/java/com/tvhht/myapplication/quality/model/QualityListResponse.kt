package com.tvhht.myapplication.quality.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "QualityListResponse")
data class QualityListResponse(
    @PrimaryKey(autoGenerate = true)
    var id: Int?,
    @SerializedName("languageId") var languageId: String? = null,
    @SerializedName("companyCodeId") var companyCodeId: String? = null,
    @SerializedName("plantId") var plantId: String? = null,
    @SerializedName("warehouseId") var warehouseId: String? = null,
    @SerializedName("preOutboundNo") var preOutboundNo: String? = null,
    @SerializedName("refDocNumber") var refDocNumber: String? = null,
    @SerializedName("partnerCode") var partnerCode: String? = null,
    @SerializedName("pickupNumber") var pickupNumber: String? = null,
    @SerializedName("qualityInspectionNo") var qualityInspectionNo: String? = null,
    @SerializedName("actualHeNo") var actualHeNo: String? = null,
    @SerializedName("outboundOrderTypeId") var outboundOrderTypeId: String? = null,
    @SerializedName("statusId") var statusId: Int? = null,
    @SerializedName("qcToQty") var qcToQty: String? = null,
    @SerializedName("qcUom") var qcUom: String? = null,
    @SerializedName("manufacturerPartNo") var manufacturerPartNo: String? = null,
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
    @SerializedName("qualityCreatedBy") var qualityCreatedBy: String? = null,
    @SerializedName("qualityCreatedOn") var qualityCreatedOn: String? = null,
    @SerializedName("qualityConfirmedBy") var qualityConfirmedBy: String? = null,
    @SerializedName("qualityConfirmedOn") var qualityConfirmedOn: String? = null,
    @SerializedName("qualityUpdatedBy") var qualityUpdatedBy: String? = null,
    @SerializedName("qualityUpdatedOn") var qualityUpdatedOn: String? = null,
    @SerializedName("qualityReversedBy") var qualityReversedBy: String? = null,
    @SerializedName("qualityReversedOn") var qualityReversedOn: String? = null,
    @SerializedName("referenceDocumentType") var referenceDocumentType: String? = null,
    @SerializedName("salesOrderNumber") var salesOrderNumber: String? = null,
    @SerializedName("manufacturerName") var manufacturerName: String? = null,
    @SerializedName("isSelected") var isSelected: Boolean = false
)