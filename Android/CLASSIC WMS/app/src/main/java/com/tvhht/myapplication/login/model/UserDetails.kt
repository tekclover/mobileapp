package com.tvhht.myapplication.login.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class UserDetails(
    @SerializedName("languageId") val languageId : String?,
    @SerializedName("companyCodeId") val companyCodeId : String?,
    @SerializedName("plantId") val plantId : String?,
    @SerializedName("warehouseId") val warehouseId : String?,
    @SerializedName("userId") val userId : String?,
    @SerializedName("password") val password : String?,
    @SerializedName("userName") val userName : String?,
    @SerializedName("statusId") val statusId : Int?,
    @SerializedName("caseReceipts") val caseReceipts : Boolean,
    @SerializedName("itemReceipts") val itemReceipts : Boolean,
    @SerializedName("putaway") val putaway : Boolean,
    @SerializedName("transfer") val transfer : Boolean,
    @SerializedName("picking") val picking : Boolean,
    @SerializedName("quality") val quality : Boolean,
    @SerializedName("inventory") val inventory : Boolean,
    @SerializedName("customerReturn") val customerReturn : Boolean,
    @SerializedName("supplierReturn") val supplierReturn : Boolean,
    @SerializedName("referenceField1") val referenceField1 : String?,
    @SerializedName("referenceField2") val referenceField2 : String?,
    @SerializedName("referenceField3") val referenceField3 : String?,
    @SerializedName("referenceField4") val referenceField4 : String?,
    @SerializedName("referenceField5") val referenceField5 : String?,
    @SerializedName("referenceField6") val referenceField6 : String?,
    @SerializedName("referenceField7") val referenceField7 : String?,
    @SerializedName("referenceField8") val referenceField8 : String?,
    @SerializedName("referenceField9") val referenceField9 : String?,
    @SerializedName("referenceField10") val referenceField10 : String?,
    @SerializedName("deletionIndicator") val deletionIndicator : Int?,
    @SerializedName("createdBy") val createdBy : String?,
    @SerializedName("createdOn") val createdOn : String?,
    @SerializedName("updatedBy") val updatedBy : String?,
    @SerializedName("updatedOn") val updatedOn : String?
) : Parcelable
