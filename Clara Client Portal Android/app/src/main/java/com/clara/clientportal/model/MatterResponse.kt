package com.clara.clientportal.model

import com.google.gson.annotations.SerializedName

data class MatterResponse(
    @SerializedName("matterNumber") var matterNumber: String? = null,
    @SerializedName("matterDescription") var matterDescription: String? = null,
    @SerializedName("caseOpenedDate") var caseOpenedDate: String? = null,
    @SerializedName("statusId") var statusId: Int? = null,
    var isExpand: Boolean = false
)
