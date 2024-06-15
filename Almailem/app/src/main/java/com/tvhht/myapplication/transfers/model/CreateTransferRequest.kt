package com.tvhht.myapplication.transfers.model

import com.google.gson.annotations.SerializedName


data class CreateTransferRequest  (

  @SerializedName("companyCodeId"       ) var companyCodeId       : String?                        = null,
  @SerializedName("createdOn"           ) var createdOn           : String?                        = null,
  @SerializedName("createdby"           ) var createdby           : String?                        = null,
  @SerializedName("inhouseTransferLine" ) var inhouseTransferLine : ArrayList<InhouseTransferLine> = arrayListOf(),
  @SerializedName("languageId"          ) var languageId          : String?                        = null,
  @SerializedName("plantId"             ) var plantId             : String?                        = null,
  @SerializedName("transferMethod"      ) var transferMethod      : String?                        = null,
  @SerializedName("transferTypeId"      ) var transferTypeId      : Int?                           = null,
  @SerializedName("warehouseId"         ) var warehouseId         : String?                        = null

)