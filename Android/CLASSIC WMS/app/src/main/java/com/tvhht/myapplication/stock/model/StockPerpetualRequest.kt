package com.tvhht.myapplication.stock.model

import com.google.gson.annotations.SerializedName


data class StockPerpetualRequest (

  @SerializedName("companyCodeId"     ) var companyCodeId     : String?                  = null,
  @SerializedName("plantId"           ) var plantId           : String?                  = null,
  @SerializedName("warehouseId"       ) var warehouseId       : String?                  = null,
  @SerializedName("cycleCountTypeId"  ) var cycleCountTypeId  : Int?                     = null,
  @SerializedName("cycleCountNo"      ) var cycleCountNo      : String?                  = null,
  @SerializedName("movementTypeId"    ) var movementTypeId    : Int?                     = null,
  @SerializedName("subMovementTypeId" ) var subMovementTypeId : Int?                     = null,
  @SerializedName("statusId"          ) var statusId          : String?                  = null,
  @SerializedName("referenceField1"   ) var referenceField1   : String?                  = null,
  @SerializedName("referenceField2"   ) var referenceField2   : String?                  = null,
  @SerializedName("referenceField3"   ) var referenceField3   : String?                  = null,
  @SerializedName("referenceField4"   ) var referenceField4   : String?                  = null,
  @SerializedName("referenceField5"   ) var referenceField5   : String?                  = null,
  @SerializedName("referenceField6"   ) var referenceField6   : String?                  = null,
  @SerializedName("referenceField7"   ) var referenceField7   : String?                  = null,
  @SerializedName("referenceField8"   ) var referenceField8   : String?                  = null,
  @SerializedName("referenceField9"   ) var referenceField9   : String?                  = null,
  @SerializedName("referenceField10"  ) var referenceField10  : String?                  = null,
  @SerializedName("deletionIndicator" ) var deletionIndicator : Int?                     = null,
  @SerializedName("createdBy"         ) var createdBy         : String?                  = null,
  @SerializedName("createdOn"         ) var createdOn         : String?                  = null,
  @SerializedName("countedBy"         ) var countedBy         : String?                  = null,
  @SerializedName("countedOn"         ) var countedOn         : String?                  = null,
  @SerializedName("confirmedBy"       ) var confirmedBy       : String?                  = null,
  @SerializedName("confirmedOn"       ) var confirmedOn       : String?                  = null,
  @SerializedName("updatePerpetualLine"     ) var perpetualLine     : ArrayList<PerpetualLine> = arrayListOf()

)