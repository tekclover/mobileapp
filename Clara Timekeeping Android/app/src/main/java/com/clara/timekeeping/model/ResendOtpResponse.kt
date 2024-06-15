package com.clara.timekeeping.model

import com.google.gson.annotations.SerializedName

data class ResendOtpResponse(
    @SerializedName("status") val status: Number? = null
)
