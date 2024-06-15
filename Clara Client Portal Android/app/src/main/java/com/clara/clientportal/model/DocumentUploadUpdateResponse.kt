package com.clara.clientportal.model

import com.google.gson.annotations.SerializedName

data class DocumentUploadUpdateResponse(
    @SerializedName("matterDocumentId") var matterDocumentId: Number? = null,
    @SerializedName("documentNo") var documentNo: String? = null,
    @SerializedName("classId") var classId: Number? = null,
    @SerializedName("matterNumber") var matterNumber: String? = null,
    @SerializedName("clientId") var clientId: String? = null,
    @SerializedName("documentUrl") var documentUrl: String? = null,
    @SerializedName("clientUserId") var clientUserId: String? = null
)

