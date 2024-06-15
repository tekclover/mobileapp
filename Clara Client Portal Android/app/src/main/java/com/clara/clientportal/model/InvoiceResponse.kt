package com.clara.clientportal.model

import com.google.gson.annotations.SerializedName

data class InvoiceResponse(
    @SerializedName("invoiceNumber") var invoiceNumber: String? = null,
    @SerializedName("languageId") var languageId: String? = null,
    @SerializedName("classId") var classId: Int? = null,
    @SerializedName("matterNumber") var matterNumber: String? = null,
    @SerializedName("clientId") var clientId: String? = null,
    @SerializedName("invoiceDate") var invoiceDate: String? = null,
    @SerializedName("invoiceAmount") var invoiceAmount: Number? = null,
    @SerializedName("referenceField10") var referenceField10: String? = null,
    @SerializedName("referenceField1") var referenceField1: String? = null,
    @SerializedName("referenceField8") var referenceField8: String? = null,
    @SerializedName("statusId") var statusId: Int? = null,
    var isExpand: Boolean = false
)
