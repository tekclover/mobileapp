package com.clara.clientportal.model

import com.google.gson.annotations.SerializedName

data class StatusResponse(
    @SerializedName("statusId") var statusId: Int? = null,
    @SerializedName("languageId") var languageId: String? = null,
    @SerializedName("status") var status: String? = null,
    @SerializedName("deletionIndicator") var deletionIndicator: Int? = null,
    @SerializedName("createdBy") var createdBy: String? = null,
    @SerializedName("updatedBy") var updatedBy: String? = null,
    @SerializedName("createdOn") var createdOn: String? = null,
    @SerializedName("updatedOn") var updatedOn: String? = null
)
