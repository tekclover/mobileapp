package com.clara.timekeeping.model

import com.google.gson.annotations.SerializedName

data class VerifyOtpResponse(
    @SerializedName("status") val status: Number? = null
)
