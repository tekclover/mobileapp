package com.clara.clientportal.model.request

import com.google.gson.annotations.SerializedName

data class DocumentUploadUpdateRequest(
    @SerializedName("classId") val classId: Number? = null,
    @SerializedName("clientId") val clientId: String? = null,
    @SerializedName("caseCategoryId") val caseCategoryId: Number? = null,
    @SerializedName("caseSubCategoryId") val caseSubCategoryId: Number? = null,
    @SerializedName("matterNumber") val matterNumber: String? = null,
    @SerializedName("documentUrl") val documentUrl: String? = null,
    @SerializedName("deletionIndicator") val deletionIndicator: Number? = null,
    @SerializedName("clientUserId") val clientUserId: String? = null,
    @SerializedName("languageId") val languageId: String? = null,
    @SerializedName("statusId") val statusId: Number? = null
)
