package com.tvhht.myapplication.picking.model

import com.google.gson.annotations.SerializedName


data class HEResponse (

  @SerializedName("acquistionDate"      ) var acquistionDate      : String? = null,
  @SerializedName("acquistionValue"     ) var acquistionValue     : Int?    = null,
  @SerializedName("category"            ) var category            : String? = null,
  @SerializedName("companyCodeId"       ) var companyCodeId       : String? = null,
  @SerializedName("countryOfOrigin"     ) var countryOfOrigin     : String? = null,
  @SerializedName("createdby"           ) var createdby           : String? = null,
  @SerializedName("createdon"           ) var createdon           : String? = null,
  @SerializedName("currencyId"          ) var currencyId          : Int?    = null,
  @SerializedName("deletionIndicator"   ) var deletionIndicator   : Int?    = null,
  @SerializedName("handlingEquipmentId" ) var handlingEquipmentId : String? = null,
  @SerializedName("handlingUnit"        ) var handlingUnit        : String? = null,
  @SerializedName("heBarcode"           ) var heBarcode           : String? = null,
  @SerializedName("languageId"          ) var languageId          : String? = null,
  @SerializedName("manufacturerPartNo"  ) var manufacturerPartNo  : String? = null,
  @SerializedName("modelNo"             ) var modelNo             : String? = null,
  @SerializedName("plantId"             ) var plantId             : String? = null,
  @SerializedName("referenceField1"     ) var referenceField1     : String? = null,
  @SerializedName("referenceField10"    ) var referenceField10    : String? = null,
  @SerializedName("referenceField2"     ) var referenceField2     : String? = null,
  @SerializedName("referenceField3"     ) var referenceField3     : String? = null,
  @SerializedName("referenceField4"     ) var referenceField4     : String? = null,
  @SerializedName("referenceField5"     ) var referenceField5     : String? = null,
  @SerializedName("referenceField6"     ) var referenceField6     : String? = null,
  @SerializedName("referenceField7"     ) var referenceField7     : String? = null,
  @SerializedName("referenceField8"     ) var referenceField8     : String? = null,
  @SerializedName("referenceField9"     ) var referenceField9     : String? = null,
  @SerializedName("statusId"            ) var statusId            : Int?    = null,
  @SerializedName("updatedby"           ) var updatedby           : String? = null,
  @SerializedName("updatedon"           ) var updatedon           : String? = null,
  @SerializedName("warehouseId"         ) var warehouseId         : String? = null

)