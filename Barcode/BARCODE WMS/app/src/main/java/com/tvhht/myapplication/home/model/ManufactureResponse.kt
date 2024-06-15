package com.tvhht.myapplication.home.model

import com.google.gson.annotations.SerializedName


data class ManufactureResponse (

    @SerializedName("languageId"         ) var languageId         : String? = null,
    @SerializedName("companyCodeId"      ) var companyCodeId      : String? = null,
    @SerializedName("plantId"            ) var plantId            : String? = null,
    @SerializedName("warehouseId"        ) var warehouseId        : String? = null,
    @SerializedName("itemCode"           ) var itemCode           : String? = null,
    @SerializedName("uomId"              ) var uomId              : String? = null,
    @SerializedName("description"        ) var description        : String? = null,
    @SerializedName("model"              ) var model              : String? = null,
    @SerializedName("specifications1"    ) var specifications1    : String? = null,
    @SerializedName("specifications2"    ) var specifications2    : String? = null,
    @SerializedName("eanUpcNo"           ) var eanUpcNo           : String? = null,
    @SerializedName("manufacturerPartNo" ) var manufacturerPartNo : String? = null,
    @SerializedName("hsnCode"            ) var hsnCode            : String? = null,
    @SerializedName("itemType"           ) var itemType           : Int?    = null,
    @SerializedName("itemGroup"          ) var itemGroup          : Int?    = null,
    @SerializedName("subItemGroup"       ) var subItemGroup       : Int?    = null,
    @SerializedName("storageSectionId"   ) var storageSectionId   : String? = null,
    @SerializedName("totalStock"         ) var totalStock         : String? = null,
    @SerializedName("capacityCheck"      ) var capacityCheck      : String? = null,
    @SerializedName("minimumStock"       ) var minimumStock       : String? = null,
    @SerializedName("maximumStock"       ) var maximumStock       : String? = null,
    @SerializedName("reorderLevel"       ) var reorderLevel       : String? = null,
    @SerializedName("replenishmentQty"   ) var replenishmentQty   : String? = null,
    @SerializedName("safetyStock"        ) var safetyStock        : String? = null,
    @SerializedName("statusId"           ) var statusId           : Int?    = null,
    @SerializedName("referenceField1"    ) var referenceField1    : String? = null,
    @SerializedName("referenceField2"    ) var referenceField2    : String? = null,
    @SerializedName("referenceField3"    ) var referenceField3    : String? = null,
    @SerializedName("referenceField4"    ) var referenceField4    : String? = null,
    @SerializedName("referenceField5"    ) var referenceField5    : String? = null,
    @SerializedName("referenceField6"    ) var referenceField6    : String? = null,
    @SerializedName("referenceField7"    ) var referenceField7    : String? = null,
    @SerializedName("referenceField8"    ) var referenceField8    : String? = null,
    @SerializedName("referenceField9"    ) var referenceField9    : String? = null,
    @SerializedName("referenceField10"   ) var referenceField10   : String? = null,
    @SerializedName("deletionIndicator"  ) var deletionIndicator  : Int?    = null,
    @SerializedName("createdBy"          ) var createdBy          : String? = null,
    @SerializedName("createdOn"          ) var createdOn          : String? = null,
    @SerializedName("updatedBy"          ) var updatedBy          : String? = null,
    @SerializedName("updatedOn"          ) var updatedOn          : String? = null

)