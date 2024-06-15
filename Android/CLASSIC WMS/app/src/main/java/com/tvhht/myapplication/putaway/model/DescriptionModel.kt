package com.tvhht.myapplication.putaway.model

import com.google.gson.annotations.SerializedName


data class DescriptionModel (
    @SerializedName("languageId")
    val languageID: String,
    @SerializedName("companyCodeId")
    val companyCodeID: String,
    @SerializedName("plantId")
    val plantID: String,
    @SerializedName("warehouseId")
    val warehouseID: String,
    @SerializedName("itemCode")
    val itemCode: String,
    @SerializedName("uomId")
    val uomID: String,
    @SerializedName("description")
    val description: String,
    @SerializedName("model")
    val model: Any? = null,
    @SerializedName("specifications1")
    val specifications1: Any? = null,
    @SerializedName("specifications2")
    val specifications2: Any? = null,
    @SerializedName("eanUpcNo")
    val eanUpcNo: Any? = null,
    @SerializedName("manufacturerPartNo")
    val manufacturerPartNo: String,
    @SerializedName("hsnCode")
    val hsnCode: String? = null,
    @SerializedName("itemType")
    val itemType: Any? = null,
    @SerializedName("itemGroup")
    val itemGroup: Any? = null,
    @SerializedName("subItemGroup")
    val subItemGroup: Any? = null,
    @SerializedName("storageSectionId")
    val storageSectionID: Any? = null,
    @SerializedName("totalStock")
    val totalStock: Any? = null,
    @SerializedName("minimumStock")
    val minimumStock: Any? = null,
    @SerializedName("maximumStock")
    val maximumStock: Any? = null,
    @SerializedName("reorderLevel")
    val reorderLevel: Any? = null,
    @SerializedName("replenishmentQty")
    val replenishmentQty: Any? = null,
    @SerializedName("safetyStock")
    val safetyStock: Any? = null,
    @SerializedName("statusId")
    val statusID: Long,
    @SerializedName("referenceField1")
    val referenceField1: Any? = null,
    @SerializedName("referenceField2")
    val referenceField2: Any? = null,
    @SerializedName("referenceField3")
    val referenceField3: Any? = null,
    @SerializedName("referenceField4")
    val referenceField4: Any? = null,
    @SerializedName("referenceField5")
    val referenceField5: Any? = null,
    @SerializedName("referenceField6")
    val referenceField6: Any? = null,
    @SerializedName("referenceField7")
    val referenceField7: Any? = null,
    @SerializedName("referenceField8")
    val referenceField8: Any? = null,
    @SerializedName("referenceField9")
    val referenceField9: Any? = null,
    @SerializedName("referenceField10")
    val referenceField10: Any? = null,
    @SerializedName("deletionIndicator")
    val deletionIndicator: Long,
    @SerializedName("createdBy")
    val createdBy: String,
    @SerializedName("createdOn")
    val createdOn: String,
    @SerializedName("updatedBy")
    val updatedBy: String,
    @SerializedName("updatedOn")
    val updatedOn: String
)
