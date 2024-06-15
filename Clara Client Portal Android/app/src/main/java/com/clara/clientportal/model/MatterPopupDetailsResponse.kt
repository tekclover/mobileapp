package com.clara.clientportal.model

import com.google.gson.annotations.SerializedName

data class MatterPopupDetailsResponse(
    @SerializedName("caseOpenedDate") var caseOpenedDate: String? = null,
    @SerializedName("caseClosedDate") var caseClosedDate: String? = null,
    @SerializedName("caseFiledDate") var caseFiledDate: String? = null,
    @SerializedName("priorityDate") var priorityDate: String? = null,
    @SerializedName("receiptDate") var receiptDate: String? = null,
    @SerializedName("expirationDate") var expirationDate: String? = null,
    @SerializedName("courtDate") var courtDate: String? = null,
    @SerializedName("approvalDate") var approvalDate: String? = null,
    @SerializedName("createdOn") var createdOn: String? = null,
    @SerializedName("updatedOn") var updatedOn: String? = null
)
