package com.tvhht.myapplication.cases.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize


@Entity(tableName = "AsnList")
@Parcelize
data class AsnList(
    @PrimaryKey(autoGenerate = true)
    var id: Int? ,
    @SerializedName("languageId"              ) var languageId              : String? = null,
    @SerializedName("companyCode"             ) var companyCode             : String? = null,
    @SerializedName("plantId"                 ) var plantId                 : String? = null,
    @SerializedName("warehouseId"             ) var warehouseId             : String? = null,
    @SerializedName("preInboundNo"            ) var preInboundNo            : String? = null,
    @SerializedName("refDocNumber"            ) var refDocNumber            : String? = null,
    @SerializedName("stagingNo"               ) var stagingNo               : String? = null,
    @SerializedName("palletCode"              ) var palletCode              : String? = null,
    @SerializedName("caseCode"                ) var caseCode                : String? = null,
    @SerializedName("lineNo"                  ) var lineNo                  : Int?    = null,
    @SerializedName("itemCode"                ) var itemCode                : String? = null,
    @SerializedName("inboundOrderTypeId"      ) var inboundOrderTypeId      : Int?    = null,
    @SerializedName("variantCode"             ) var variantCode             : Int?    = null,
    @SerializedName("variantSubCode"          ) var variantSubCode          : String? = null,
    @SerializedName("batchSerialNumber"       ) var batchSerialNumber       : String? = null,
    @SerializedName("stockTypeId"             ) var stockTypeId             : Int?    = null,
    @SerializedName("specialStockIndicatorId" ) var specialStockIndicatorId : Int?    = null,
    @SerializedName("storageMethod"           ) var storageMethod           : String? = null,
    @SerializedName("statusId"                ) var statusId                : Int?    = null,
    @SerializedName("businessPartnerCode"     ) var businessPartnerCode     : String? = null,
    @SerializedName("containerNo"             ) var containerNo             : String? = null,
    @SerializedName("invoiceNo"               ) var invoiceNo               : String? = null,
    @SerializedName("orderQty"                ) var orderQty                : Int?    = null,
    @SerializedName("orderUom"                ) var orderUom                : String? = null,
    @SerializedName("itemQtyPerPallet"        ) var itemQtyPerPallet        : Int?    = null,
    @SerializedName("itemQtyPerCase"          ) var itemQtyPerCase          : Int?    = null,
    @SerializedName("assignedUserId"          ) var assignedUserId          : String? = null,
    @SerializedName("itemDescription"         ) var itemDescription         : String? = null,
    @SerializedName("manufacturerPartNo"      ) var manufacturerPartNo      : String? = null,
    @SerializedName("hsnCode"                 ) var hsnCode                 : String? = null,
    @SerializedName("variantType"             ) var variantType             : String? = null,
    @SerializedName("specificationActual"     ) var specificationActual     : String? = null,
    @SerializedName("itemBarcode"             ) var itemBarcode             : String? = null,
    @SerializedName("referenceOrderNo"        ) var referenceOrderNo        : String? = null,
    @SerializedName("referenceOrderQty"       ) var referenceOrderQty       : Int?    = null,
    @SerializedName("crossDockAllocationQty"  ) var crossDockAllocationQty  : Int?    = null,
    @SerializedName("remarks"                 ) var remarks                 : String? = null,
    @SerializedName("referenceField1"         ) var referenceField1         : String? = null,
    @SerializedName("referenceField2"         ) var referenceField2         : String? = null,
    @SerializedName("referenceField3"         ) var referenceField3         : String? = null,
    @SerializedName("referenceField4"         ) var referenceField4         : String? = null,
    @SerializedName("referenceField5"         ) var referenceField5         : String? = null,
    @SerializedName("referenceField6"         ) var referenceField6         : String? = null,
    @SerializedName("referenceField7"         ) var referenceField7         : String? = null,
    @SerializedName("referenceField8"         ) var referenceField8         : String? = null,
    @SerializedName("referenceField9"         ) var referenceField9         : String? = null,
    @SerializedName("referenceField10"        ) var referenceField10        : String? = null,
    @SerializedName("deletionIndicator"       ) var deletionIndicator       : Int?    = null,
    @SerializedName("createdBy"               ) var createdBy               : String? = null,
    @SerializedName("createdOn"               ) var createdOn               : String? = null,
    @SerializedName("updatedBy"               ) var updatedBy               : String? = null,
    @SerializedName("updatedOn"               ) var updatedOn               : String? = null,
    @SerializedName("confirmedBy"             ) var confirmedBy             : String? = null,
    @SerializedName("confirmedOn"             ) var confirmedOn             : String? = null
) : Parcelable
