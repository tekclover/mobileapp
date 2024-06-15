package com.clara.clientportal.model

import com.google.gson.annotations.SerializedName

data class MatterIdResponse(
    @SerializedName("matterNumber") var matterNumber: String? = null
)
