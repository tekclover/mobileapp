package com.tvhht.myapplication.picking.model

import com.google.gson.annotations.SerializedName

data class AddNewBarcodeRequest(
    @SerializedName("barcodeId") val barcodeId: String? = null,
    @SerializedName("companyCodeId") val companyCodeId: String? = null,
    @SerializedName("itemCode") val itemCode: String? = null,
    @SerializedName("languageId") val languageId: String? = null,
    @SerializedName("loginUserID") val loginUserId: String? = null,
    @SerializedName("manufacturerName") val manufacturerName: String? = null,
    @SerializedName("plantId") val plantId: String? = null,
    @SerializedName("warehouseId") val warehouseId: String? = null,
)

data class AddNewBarcodeResponse(
    @SerializedName("languageId") var languageId: String? = null,
    @SerializedName("companyCodeId") var companyCodeId: String? = null,
    @SerializedName("plantId") var plantId: String? = null,
    @SerializedName("warehouseId") var warehouseId: String? = null,
    @SerializedName("itemCode") var itemCode: String? = null,
    @SerializedName("businessPartnerType") var businessPartnerType: String? = null,
    @SerializedName("businessPartnerCode") var businessPartnerCode: String? = null,
    @SerializedName("partnerItemBarcode") var partnerItemBarcode: String? = null,
    @SerializedName("manufacturerCode") var manufacturerCode: String? = null,
    @SerializedName("manufacturerName") var manufacturerName: String? = null,
    @SerializedName("partnerName") var partnerName: String? = null,
    @SerializedName("partnerItemNo") var partnerItemNo: String? = null,
    @SerializedName("vendorItemBarcode") var vendorItemBarcode: String? = null,
    @SerializedName("mfrBarcode") var mfrBarcode: String? = null,
    @SerializedName("brandName") var brandName: String? = null,
    @SerializedName("stock") var stock: String? = null,
    @SerializedName("stockUom") var stockUom: String? = null,
    @SerializedName("statusId") var statusId: String? = null,
    @SerializedName("referenceField1") var referenceField1: String? = null,
    @SerializedName("referenceField2") var referenceField2: String? = null,
    @SerializedName("referenceField3") var referenceField3: String? = null,
    @SerializedName("referenceField4") var referenceField4: String? = null,
    @SerializedName("referenceField5") var referenceField5: String? = null,
    @SerializedName("referenceField6") var referenceField6: String? = null,
    @SerializedName("referenceField7") var referenceField7: String? = null,
    @SerializedName("referenceField8") var referenceField8: String? = null,
    @SerializedName("referenceField9") var referenceField9: String? = null,
    @SerializedName("referenceField10") var referenceField10: String? = null,
    @SerializedName("deletionIndicator") var deletionIndicator: Int? = null,
    @SerializedName("createdBy") var createdBy: String? = null,
    @SerializedName("createdOn") var createdOn: String? = null,
    @SerializedName("updatedBy") var updatedBy: String? = null,
    @SerializedName("updatedOn") var updatedOn: String? = null,
    @SerializedName("oldPartnerItemBarcode") var oldPartnerItemBarcode: String? = null
)