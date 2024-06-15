package com.clara.clientportal.model

import com.google.gson.annotations.SerializedName

data class ReceiptNoResponse(
    @SerializedName("documentType") val documentType: String? = null,
    @SerializedName("receiptNo") val receiptNo: String? = null,
    @SerializedName("receiptType") val receiptType: String? = null,
    @SerializedName("receiptDate") val receiptDate: String? = null,
    @SerializedName("statusId") val statusId: Int? = -1,
    @SerializedName("matterNumber") val matterNumber: String? = null,
    @SerializedName("clientId") val clientId: String? = null,
    @SerializedName("referenceField8") val referenceField8: String? = null,
    var isExpand: Boolean = false
)
