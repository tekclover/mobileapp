package com.clara.clientportal.model

import com.google.gson.annotations.SerializedName

data class ErrorResponse(
    @SerializedName("error") val error: String? = "",
    @SerializedName("status") val status: Number? = null
)
