package com.tvhht.myapplication.transfers.model

import com.google.gson.annotations.SerializedName


data class InhouseTransferLine (

  @SerializedName("caseCode"                ) var caseCode                : String? = null,
  @SerializedName("companyCodeId"           ) var companyCodeId           : String? = null,
  @SerializedName("confirmedBy"             ) var confirmedBy             : String? = null,
  @SerializedName("confirmedOn"             ) var confirmedOn             : String? = null,
  @SerializedName("createdOn"               ) var createdOn               : String? = null,
  @SerializedName("createdby"               ) var createdby               : String? = null,
  @SerializedName("languageId"              ) var languageId              : String? = null,
  @SerializedName("packBarcodes"            ) var packBarcodes            : String? = null,
  @SerializedName("palletCode"              ) var palletCode              : String? = null,
  @SerializedName("plantId"                 ) var plantId                 : String? = null,
  @SerializedName("sourceItemCode"          ) var sourceItemCode          : String? = null,
  @SerializedName("sourceStockTypeId"       ) var sourceStockTypeId       : Int?    = null,
  @SerializedName("sourceStorageBin"        ) var sourceStorageBin        : String? = null,
  @SerializedName("specialStockIndicatorId" ) var specialStockIndicatorId : Int?    = null,
  @SerializedName("targetStockTypeId"       ) var targetStockTypeId       : Int?    = null,
  @SerializedName("targetItemCode"          ) var targetItemCode          : String? = null,
  @SerializedName("targetStorageBin"        ) var targetStorageBin        : String? = null,
  @SerializedName("transferConfirmedQty"    ) var transferConfirmedQty    : Int?    = null,
  @SerializedName("transferOrderQty"        ) var transferOrderQty        : Int?    = null,
  @SerializedName("transferUom"             ) var transferUom             : String? = null,
  @SerializedName("warehouseId"             ) var warehouseId             : String? = null,
  @SerializedName("manufacturerName"        ) var manufacturerName        : String? = null
)