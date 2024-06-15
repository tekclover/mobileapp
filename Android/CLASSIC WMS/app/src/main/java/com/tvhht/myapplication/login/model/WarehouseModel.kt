package com.tvhht.myapplication.login.model

import com.google.gson.annotations.SerializedName

data class WarehouseModel(

    @SerializedName("languageId"        ) var languageId        : String? = null,
    @SerializedName("companyCode"       ) var companyCode       : String? = null,
    @SerializedName("plantId"           ) var plantId           : String? = null,
    @SerializedName("warehouseId"       ) var warehouseId       : String? = null,
    @SerializedName("userId"            ) var userId            : String? = null,
    @SerializedName("userRoleId"        ) var userRoleId        : Int?    = null,
    @SerializedName("userTypeId"        ) var userTypeId        : Int?    = null,
    @SerializedName("userName"          ) var userName          : String? = null,
    @SerializedName("firstName"         ) var firstName         : String? = null,
    @SerializedName("lastName"          ) var lastName          : String? = null,
    @SerializedName("statusId"          ) var statusId          : Int?    = null,
    @SerializedName("dateFormatId"      ) var dateFormatId      : Int?    = null,
    @SerializedName("currencyDecimal"   ) var currencyDecimal   : Int?    = null,
    @SerializedName("decimalNotationId" ) var decimalNotationId : String? = null,
    @SerializedName("timeZone"          ) var timeZone          : String? = null,
    @SerializedName("emailId"           ) var emailId           : String? = null,
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