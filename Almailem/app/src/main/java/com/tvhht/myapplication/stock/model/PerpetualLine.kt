package com.tvhht.myapplication.stock.model

import com.google.gson.annotations.SerializedName




data class PerpetualLine (

   @SerializedName("languageId"            ) var languageId            : String? = null,
    @SerializedName("companyCodeId"         ) var companyCodeId         : String? = null,
    @SerializedName("plantId"               ) var plantId               : String? = null,
    @SerializedName("warehouseId"           ) var warehouseId           : String? = null,
    @SerializedName("cycleCountNo"          ) var cycleCountNo          : String? = null,
    @SerializedName("storageBin"            ) var storageBin            : String? = null,
    @SerializedName("itemCode"              ) var itemCode              : String? = null,
    @SerializedName("itemDesc"              ) var itemDesc              : String? = null,
    @SerializedName("packBarcodes"          ) var packBarcodes          : String? = null,
    @SerializedName("manufacturerPartNo"    ) var manufacturerPartNo    : String? = null,
    @SerializedName("variantCode"           ) var variantCode           : String? = null,
    @SerializedName("variantSubCode"        ) var variantSubCode        : String? = null,
    @SerializedName("batchSerialNumber"     ) var batchSerialNumber     : String? = null,
    @SerializedName("stockTypeId"           ) var stockTypeId           : Int?    = null,
    @SerializedName("specialStockIndicator" ) var specialStockIndicator : String? = null,
    @SerializedName("storageSectionId"      ) var storageSectionId      : String? = null,
    @SerializedName("inventoryQuantity"     ) var inventoryQuantity     : Int?    = null,
    @SerializedName("inventoryUom"          ) var inventoryUom          : String? = null,
    @SerializedName("countedQty"            ) var countedQty            : Int? = null,
    @SerializedName("varianceQty"           ) var varianceQty           : Int? = null,
    @SerializedName("cycleCounterId"        ) var cycleCounterId        : String? = null,
    @SerializedName("cycleCounterName"      ) var cycleCounterName      : String? = null,
    @SerializedName("statusId"              ) var statusId              : Int?    = null,
    @SerializedName("cycleCountAction"      ) var cycleCountAction      : String? = null,
    @SerializedName("referenceNo"           ) var referenceNo           : String? = null,
    @SerializedName("approvalProcessId"     ) var approvalProcessId     : String? = null,
    @SerializedName("approvalLevel"         ) var approvalLevel         : String? = null,
    @SerializedName("approverCode"          ) var approverCode          : String? = null,
    @SerializedName("approvalStatus"        ) var approvalStatus        : String? = null,
    @SerializedName("remarks"               ) var remarks               : String? = null,
    @SerializedName("referenceField1"       ) var referenceField1       : String? = null,
    @SerializedName("referenceField2"       ) var referenceField2       : String? = null,
    @SerializedName("referenceField3"       ) var referenceField3       : String? = null,
    @SerializedName("referenceField4"       ) var referenceField4       : String? = null,
    @SerializedName("referenceField5"       ) var referenceField5       : String? = null,
    @SerializedName("referenceField6"       ) var referenceField6       : String? = null,
    @SerializedName("referenceField7"       ) var referenceField7       : String? = null,
    @SerializedName("referenceField8"       ) var referenceField8       : String? = null,
    @SerializedName("referenceField9"       ) var referenceField9       : String? = null,
    @SerializedName("referenceField10"      ) var referenceField10      : String? = null,
    @SerializedName("deletionIndicator"     ) var deletionIndicator     : Int?    = null,
    @SerializedName("createdBy"             ) var createdBy             : String? = null,
    @SerializedName("createdOn"             ) var createdOn             : String? = null,
    @SerializedName("confirmedBy"           ) var confirmedBy           : String? = null,
    @SerializedName("confirmedOn"           ) var confirmedOn           : String? = null,
    @SerializedName("countedBy"             ) var countedBy             : String? = null,
    @SerializedName("countedOn"             ) var countedOn             : String? = null,
    @SerializedName("manufacturerName"      ) var manufacturerName      : String? = null,
    @SerializedName("barcodeId"             ) val barcodeId             : String? = null
)