package com.clara.clientportal.model

import com.google.gson.annotations.SerializedName

data class CheckListDocumentSubmitResponse(
    @SerializedName("status") var status: String? = null
)
