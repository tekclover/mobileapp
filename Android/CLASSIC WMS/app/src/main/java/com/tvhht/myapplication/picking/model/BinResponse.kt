package com.tvhht.myapplication.picking.model

import com.google.gson.annotations.SerializedName


data class BinResponse (

  @SerializedName("languageId"              ) var languageId              : String? = null,
  @SerializedName("companyCodeId"           ) var companyCodeId           : String? = null,
  @SerializedName("plantId"                 ) var plantId                 : String? = null,
  @SerializedName("warehouseId"             ) var warehouseId             : String? = null,
  @SerializedName("palletCode"              ) var palletCode              : String? = null,
  @SerializedName("caseCode"                ) var caseCode                : String? = null,
  @SerializedName("packBarcodes"            ) var packBarcodes            : String? = null,
  @SerializedName("itemCode"                ) var itemCode                : String? = null,
  @SerializedName("variantCode"             ) var variantCode             : Int?    = null,
  @SerializedName("variantSubCode"          ) var variantSubCode          : String? = null,
  @SerializedName("batchSerialNumber"       ) var batchSerialNumber       : String? = null,
  @SerializedName("storageBin"              ) var storageBin              : String? = null,
  @SerializedName("stockTypeId"             ) var stockTypeId             : Int?    = null,
  @SerializedName("specialStockIndicatorId" ) var specialStockIndicatorId : Int?    = null,
  @SerializedName("referenceOrderNo"        ) var referenceOrderNo        : String? = null,
  @SerializedName("storageMethod"           ) var storageMethod           : String? = null,
  @SerializedName("binClassId"              ) var binClassId              : Int?    = null,
  @SerializedName("description"             ) var description             : String? = null,
  @SerializedName("inventoryQuantity"       ) var inventoryQuantity       : Int?    = null,
  @SerializedName("allocatedQuantity"       ) var allocatedQuantity       : Int?    = null,
  @SerializedName("inventoryUom"            ) var inventoryUom            : String? = null,
  @SerializedName("manufacturerDate"        ) var manufacturerDate        : String? = null,
  @SerializedName("expiryDate"              ) var expiryDate              : String? = null,
  @SerializedName("deletionIndicator"       ) var deletionIndicator       : Int?    = null,
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
  @SerializedName("createdBy"               ) var createdBy               : String? = null,
  @SerializedName("createdOn"               ) var createdOn               : String? = null

)