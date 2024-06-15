package com.clara.timekeeping.model

import com.google.gson.annotations.SerializedName

data class DeleteTicketResponse(
    @SerializedName("status") val status: String? = null
)
