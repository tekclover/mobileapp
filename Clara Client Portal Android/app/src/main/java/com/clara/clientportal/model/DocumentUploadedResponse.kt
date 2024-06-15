package com.clara.clientportal.model

import com.google.gson.annotations.SerializedName

data class DocumentUploadedResponse(
    @SerializedName("matterNumber") var matterNumber: String? = null,
    @SerializedName("documentUrl") var documentUrl: String? = null,
    @SerializedName("createdBy") var createdBy: String? = null,
    @SerializedName("createdOn") var createdOn: String? = null,
    @SerializedName("referenceField2") var referenceField2: String? = null,
    @SerializedName("referenceField5") var referenceField5: String? = null,
    @SerializedName("caseCategoryId") var caseCategoryId: Number? = null,
    @SerializedName("caseSubCategoryId") var caseSubCategoryId: Number? = null,
    @SerializedName("deletionIndicator") var deletionIndicator: Number? = null,
    @SerializedName("classId") var classId: Number? = null,
    @SerializedName("clientId") var clientId: String? = null,
    @SerializedName("languageId") var languageId: String? = null,
    @SerializedName("statusId") var statusId: Number? = null,
    @SerializedName("clientUserId") var clientUserId: String? = null,
    var isExpand: Boolean = false
)

data class MatterDetails(
    @SerializedName("classId") var classId: Int? = null,
    @SerializedName("clientId") var clientId: String? = null,
    @SerializedName("caseCategoryId") var caseCategoryId: Int? = null,
    @SerializedName("caseSubCategoryId") var caseSubCategoryId: Int? = null,
    @SerializedName("matterNumber") var matterNumber: String? = null,
    @SerializedName("deletionIndicator") var deletionIndicator: Int? = null,
    @SerializedName("languageId") var languageId: String? = null,
    @SerializedName("statusId") var statusId: Int? = null
)