package com.tvhht.myapplication.quality.model




import com.google.gson.annotations.SerializedName
import java.io.Serializable


data class QualityDetailsModel(

    @SerializedName("languageId"              ) var languageId: String? = null,
    @SerializedName("companyCodeId"           ) var companyCodeId: String? = null,
    @SerializedName("plantId"                 ) var plantId: String? = null,
    @SerializedName("warehouseId"             ) var warehouseId: String? = null,
    @SerializedName("preOutboundNo"           ) var preOutboundNo: String? = null,
    @SerializedName("refDocNumber"            ) var refDocNumber: String? = null,
    @SerializedName("partnerCode"             ) var partnerCode: String? = null,
    @SerializedName("lineNumber"              ) var lineNumber: Int?    = null,
    @SerializedName("pickupNumber"            ) var pickupNumber: String? = null,
    @SerializedName("pickConfirmQty"          ) var pickConfirmQty: Int? = null,
    @SerializedName("itemCode"                ) var itemCode: String? = null,
    @SerializedName("actualHeNo"              ) var actualHeNo: String? = null,
    @SerializedName("pickedStorageBin"        ) var pickedStorageBin: String? = null,
    @SerializedName("pickedPackCode"          ) var pickedPackCode: String? = null,
    @SerializedName("variantCode"             ) var variantCode: Int?    = null,
    @SerializedName("variantSubCode"          ) var variantSubCode: String? = null,
    @SerializedName("batchSerialNumber"       ) var batchSerialNumber: String? = null,
    @SerializedName("pickQty"                 ) var pickQty: String? = null,
    @SerializedName("pickUom"                 ) var pickUom: String? = null,
    @SerializedName("stockTypeId"             ) var stockTypeId: Int?    = null,
    @SerializedName("specialStockIndicatorId" ) var specialStockIndicatorId: Int?    = null,
    @SerializedName("description"             ) var description: String? = null,
    @SerializedName("manufacturerPartNo"      ) var manufacturerPartNo: String? = null,
    @SerializedName("assignedPickerId"        ) var assignedPickerId: String? = null,
    @SerializedName("pickPalletCode"          ) var pickPalletCode: String? = null,
    @SerializedName("pickCaseCode"            ) var pickCaseCode: String? = null,
    @SerializedName("statusId"                ) var statusId: Int?    = null,
    @SerializedName("referenceField1"         ) var referenceField1: String? = null,
    @SerializedName("referenceField2"         ) var referenceField2: String? = null,
    @SerializedName("referenceField3"         ) var referenceField3: String? = null,
    @SerializedName("referenceField4"         ) var referenceField4: String? = null,
    @SerializedName("referenceField5"         ) var referenceField5: String? = null,
    @SerializedName("referenceField6"         ) var referenceField6: String? = null,
    @SerializedName("referenceField7"         ) var referenceField7: String? = null,
    @SerializedName("referenceField8"         ) var referenceField8: String? = null,
    @SerializedName("referenceField9"         ) var referenceField9: String? = null,
    @SerializedName("referenceField10"        ) var referenceField10: String? = null,
    @SerializedName("deletionIndicator"       ) var deletionIndicator: Int?    = null,
    @SerializedName("pickupCreatedBy"         ) var pickupCreatedBy: String? = null,
    @SerializedName("pickupCreatedOn"         ) var pickupCreatedOn: String? = null,
    @SerializedName("pickupConfirmedBy"       ) var pickupConfirmedBy: String? = null,
    @SerializedName("pickupConfirmedOn"       ) var pickupConfirmedOn: String? = null,
    @SerializedName("pickUpUpdatedBy"         ) var pickUpUpdatedBy: String? = null,
    @SerializedName("pickupUpdatedOn"         ) var pickupUpdatedOn: String? = null,
    @SerializedName("pickupReversedBy"        ) var pickupReversedBy: String? = null,
    @SerializedName("pickupReversedOn"        ) var pickupReversedOn: String? = null,
    @SerializedName("qualityInspectionNo"        ) var qualityInspectionNo: String? = null,
    @SerializedName("manufacturerName"        ) var manufacturerName: String? = null,
    @SerializedName("isSelected"        ) var isSelected: Boolean = false

):Serializable