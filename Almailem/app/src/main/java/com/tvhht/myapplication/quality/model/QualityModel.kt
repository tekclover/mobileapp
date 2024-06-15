package com.tvhht.myapplication.quality.model


import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity
data class QualityModel(
    @PrimaryKey(autoGenerate = true) val qualityID: Int,
    @SerializedName("actualHeNo") var actualHeNo: String? = null,
    @SerializedName("companyCodeId") var companyCodeId: String? = null,
    @SerializedName("itemCode") var itemCode: String? = null,
    @SerializedName("languageId") var languageId: String? = null,
    @SerializedName("lineNumber") var lineNumber: Int? = null,
    @SerializedName("outboundOrderTypeId") var outboundOrderTypeId: String? = null,
    @SerializedName("partnerCode") var partnerCode: String? = null,
    @SerializedName("plantId") var plantId: String? = null,
    @SerializedName("pickConfirmQty") var pickConfirmQty: Int? = null,
    @SerializedName("preOutboundNo") var preOutboundNo: String? = null,
    @SerializedName("qualityInspectionNo") var qualityInspectionNo: String? = null,
    @SerializedName("pickPackBarCode") var pickPackBarCode: String? = null,
    @SerializedName("qualityQty") var qualityQty: Int? = null,
    @SerializedName("refDocNumber") var refDocNumber: String? = null,
    @SerializedName("warehouseId") var warehouseId: String? = null
)