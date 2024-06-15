package com.clara.clientportal.model.request

import com.clara.clientportal.model.MatterDocLists
import com.google.gson.annotations.SerializedName

data class CheckListDocumentUploadUpdateRequest(
    @SerializedName("caseSubCategoryId") val caseSubCategoryId: Number? = null,
    @SerializedName("checkListNo") val checkListNo: Number? = null,
    @SerializedName("classId") val classId: Number? = null,
    @SerializedName("clientId") val clientId: String? = null,
    @SerializedName("languageId") val languageId: String? = null,
    @SerializedName("matterNumber") val matterNumber: String? = null,
    @SerializedName("statusId") val statusId: Number? = null,
    @SerializedName("matterDocLists") val matterDocLists: List<MatterDocListRequest>? = null
)

data class MatterDocListRequest(
    @SerializedName("caseCategoryId") var caseCategoryId: Number? = null,
    @SerializedName("caseSubCategoryId") var caseSubCategoryId: Number? = null,
    @SerializedName("checkListNo") var checkListNo: Number? = null,
    @SerializedName("classId") var classId: Int? = null,
    @SerializedName("clientId") var clientId: String? = null,
    @SerializedName("documentName") var documentName: String? = null,
    @SerializedName("documentUrl") var documentUrl: String? = null,
    @SerializedName("matterNumber") var matterNumber: String? = null,
    @SerializedName("matterText") var matterText: String? = null,
    @SerializedName("sequenceNumber") var sequenceNumber: Number? = null,
    @SerializedName("statusId") var statusId: Number? = null,
    @SerializedName("languageId") var languageId: String? = null
)
