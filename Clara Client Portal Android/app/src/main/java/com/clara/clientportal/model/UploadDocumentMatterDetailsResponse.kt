package com.clara.clientportal.model

import com.google.gson.annotations.SerializedName

data class UploadDocumentMatterDetailsResponse(
    @SerializedName("matterNumber") var matterNumber: String? = null

)
