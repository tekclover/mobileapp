package com.clara.clientportal.model.request

import com.google.gson.annotations.SerializedName

data class MatterRequest(
    @SerializedName("clientId") var clientIdList: List<String>? = null
)
