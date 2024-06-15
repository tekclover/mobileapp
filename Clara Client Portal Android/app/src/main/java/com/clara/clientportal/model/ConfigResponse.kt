package com.clara.clientportal.model

import com.google.gson.annotations.SerializedName

data class ConfigResponse(
    @SerializedName("status") private var status: String? = null
)
