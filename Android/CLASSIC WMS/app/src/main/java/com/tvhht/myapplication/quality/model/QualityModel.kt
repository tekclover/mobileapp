package com.tvhht.myapplication.quality.model


import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity
data class QualityModel(@PrimaryKey(autoGenerate = true) val qualityID: Int,
    @SerializedName("actualHeNo"              ) var actualHeNo              : String? = null,
    @SerializedName("batchSerialNumber"       ) var batchSerialNumber       : String? = null,
    @SerializedName("companyCodeId"           ) var companyCodeId           : String? = null,
    @SerializedName("deletionIndicator"       ) var deletionIndicator       : Int?    = null,
    @SerializedName("description"             ) var description             : String? = null,
    @SerializedName("itemCode"                ) var itemCode                : String? = null,
    @SerializedName("languageId"              ) var languageId              : String? = null,
    @SerializedName("lineNumber"              ) var lineNumber              : Int?    = null,
    @SerializedName("manufacturerPartNo"      ) var manufacturerPartNo      : String? = null,
    @SerializedName("outboundOrderTypeId"     ) var outboundOrderTypeId     : Int?    = null,
    @SerializedName("packingMaterialNo"       ) var packingMaterialNo       : String? = null,
    @SerializedName("partnerCode"             ) var partnerCode             : String? = null,
    @SerializedName("pickConfirmQty"          ) var pickConfirmQty          : Int?    = null,
    @SerializedName("pickPackBarCode"         ) var pickPackBarCode         : String? = null,
    @SerializedName("plantId"                 ) var plantId                 : String? = null,
    @SerializedName("preOutboundNo"           ) var preOutboundNo           : String? = null,
    @SerializedName("qualityConfirmUom"       ) var qualityConfirmUom       : String? = null,
    @SerializedName("qualityConfirmedBy"      ) var qualityConfirmedBy      : String? = null,
    @SerializedName("qualityConfirmedOn"      ) var qualityConfirmedOn      : String? = null,
    @SerializedName("qualityCreatedBy"        ) var qualityCreatedBy        : String? = null,
    @SerializedName("qualityCreatedOn"        ) var qualityCreatedOn        : String? = null,
    @SerializedName("qualityInspectionNo"     ) var qualityInspectionNo     : String? = null,
    @SerializedName("qualityQty"              ) var qualityQty              : Int?    = null,
    @SerializedName("qualityReversedBy"       ) var qualityReversedBy       : String? = null,
    @SerializedName("qualityReversedOn"       ) var qualityReversedOn       : String? = null,
    @SerializedName("qualityUpdatedBy"        ) var qualityUpdatedBy        : String? = null,
    @SerializedName("qualityUpdatedOn"        ) var qualityUpdatedOn        : String? = null,
    @SerializedName("refDocNumber"            ) var refDocNumber            : String? = null,
    @SerializedName("referenceField1"         ) var referenceField1         : String? = null,
    @SerializedName("referenceField10"        ) var referenceField10        : String? = null,
    @SerializedName("referenceField2"         ) var referenceField2         : String? = null,
    @SerializedName("referenceField3"         ) var referenceField3         : String? = null,
    @SerializedName("referenceField4"         ) var referenceField4         : String? = null,
    @SerializedName("referenceField5"         ) var referenceField5         : String? = null,
    @SerializedName("referenceField6"         ) var referenceField6         : String? = null,
    @SerializedName("referenceField7"         ) var referenceField7         : String? = null,
    @SerializedName("referenceField8"         ) var referenceField8         : String? = null,
    @SerializedName("referenceField9"         ) var referenceField9         : String? = null,
    @SerializedName("rejectQty"               ) var rejectQty               : String? = null,
    @SerializedName("rejectUom"               ) var rejectUom               : String? = null,
    @SerializedName("specialStockIndicatorId" ) var specialStockIndicatorId : Int?    = null,
    @SerializedName("statusId"                ) var statusId                : Int?    = null,
    @SerializedName("stockTypeId"             ) var stockTypeId             : Int?    = null,
    @SerializedName("variantCode"             ) var variantCode             : Int?    = null,
    @SerializedName("variantSubCode"          ) var variantSubCode          : String? = null,
    @SerializedName("warehouseId"             ) var warehouseId             : String? = null

)