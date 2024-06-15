package com.clara.clientportal.model

import com.google.gson.annotations.SerializedName

data class UploadResponse(
    @SerializedName("status") var status: String? = null,
    @SerializedName("file") var file: String? = null,
    @SerializedName("location") var location: String? = null
)
