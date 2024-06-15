package com.tvhht.myapplication.home.model

import androidx.annotation.NonNull
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity
data class BarcodeResponse (
    @PrimaryKey
    @NonNull
    @SerializedName("interimBarcodeId"  ) var interimBarcodeId  : Int?    = null,
    @SerializedName("storageBin"        ) var storageBin        : String? = null,
    @SerializedName("itemCode"          ) var itemCode          : String? = null,
    @SerializedName("barcode"           ) var barcode           : String? = null,
    @SerializedName("deletionIndicator" ) var deletionIndicator : Int?    = null,
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
    @SerializedName("createdBy"         ) var createdBy         : String? = null,
    @SerializedName("createdOn"         ) var createdOn         : String? = null,
    @SerializedName("updatedBy"         ) var updatedBy         : String? = null,
    @SerializedName("updatedOn"         ) var updatedOn         : String? = null

)