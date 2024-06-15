package com.clara.clientportal.model

import com.google.gson.annotations.SerializedName

data class VerifyOtpResponse(
    @SerializedName("languageId") val languageId: String?,
    @SerializedName("classId") val classId: Int?,
    @SerializedName("clientUserId") val clientUserId: String?,
    @SerializedName("clientId") val clientId: String?,
    @SerializedName("contactNumber") val contactNumber: String?,
    @SerializedName("firstName") val firstName: String?,
    @SerializedName("lastName") val lastName: String?,
    @SerializedName("emailId") val emailId: String?,
    @SerializedName("quotation") val quotation: Int?,
    @SerializedName("paymentPlan") val paymentPlan: Int?,
    @SerializedName("matter") val matter: Int?,
    @SerializedName("documents") val documents: Int?,
    @SerializedName("invoice") val invoice: Int?,
    @SerializedName("agreement") val agreement: Int?,
    @SerializedName("deletionIndicator") val deletionIndicator: Int?,
    @SerializedName("createdBy") val createdBy: String?,
    @SerializedName("createdOn") val createdOn: String?,
    @SerializedName("updatedBy") val updatedBy: String?,
    @SerializedName("updatedOn") val updatedOn: String?
)
