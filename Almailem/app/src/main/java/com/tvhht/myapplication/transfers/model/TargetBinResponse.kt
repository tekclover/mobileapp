package com.tvhht.myapplication.transfers.model


import com.google.gson.annotations.SerializedName


data class TargetBinResponse(
    @SerializedName("languageId"        ) var languageId        : String? = null,
    @SerializedName("companyCodeId"     ) var companyCodeId     : String? = null,
    @SerializedName("plantId"           ) var plantId           : String? = null,
    @SerializedName("warehouseId"       ) var warehouseId       : String? = null,
    @SerializedName("storageBin"        ) var storageBin        : String? = null,
    @SerializedName("floorId"           ) var floorId           : Int?    = null,
    @SerializedName("storageSectionId"  ) var storageSectionId  : String? = null,
    @SerializedName("rowId"             ) var rowId             : String? = null,
    @SerializedName("aisleNumber"       ) var aisleNumber       : String? = null,
    @SerializedName("spanId"            ) var spanId            : String? = null,
    @SerializedName("shelfId"           ) var shelfId           : String? = null,
    @SerializedName("binSectionId"      ) var binSectionId      : Int?    = null,
    @SerializedName("storageTypeId"     ) var storageTypeId     : Int?    = null,
    @SerializedName("binClassId"        ) var binClassId        : Int?    = null,
    @SerializedName("description"       ) var description       : String? = null,
    @SerializedName("binBarcode"        ) var binBarcode        : String? = null,
    @SerializedName("putawayBlock"      ) var putawayBlock      : Int?    = null,
    @SerializedName("pickingBlock"      ) var pickingBlock      : Int?    = null,
    @SerializedName("blockReason"       ) var blockReason       : String? = null,
    @SerializedName("statusId"          ) var statusId          : Int?    = null,
    @SerializedName("referenceField1"   ) var referenceField1   : String? = null,
    @SerializedName("referenceField2"   ) var referenceField2   : String? = null,
    @SerializedName("referenceField3"   ) var referenceField3   : String? = null,
    @SerializedName("referenceField4"   ) var referenceField4   : String? = null,
    @SerializedName("referenceField5"   ) var referenceField5   : String? = null,
    @SerializedName("referenceField6"   ) var referenceField6   : String? = null,
    @SerializedName("referenceField7"   ) var referenceField7   : String? = null,
    @SerializedName("referenceField8"   ) var referenceField8   : String? = null,
    @SerializedName("referenceField9"   ) var referenceField9   : String? = null,
    @SerializedName("referenceField10"  ) var referenceField10  : String? = null,
    @SerializedName("deletionIndicator" ) var deletionIndicator : Int?    = null,
    @SerializedName("createdBy"         ) var createdBy         : String? = null,
    @SerializedName("createdOn"         ) var createdOn         : String? = null,
    @SerializedName("updatedBy"         ) var updatedBy         : String? = null,
    @SerializedName("updatedOn"         ) var updatedOn         : String? = null

)