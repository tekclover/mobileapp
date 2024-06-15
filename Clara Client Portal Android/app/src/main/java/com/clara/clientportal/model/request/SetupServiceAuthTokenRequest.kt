package com.clara.clientportal.model.request

import com.google.gson.annotations.SerializedName

data class SetupServiceAuthTokenRequest(
    @SerializedName("clientId") val clientId: String? = null,
    @SerializedName("clientSecretKey") val clientSecretKey: String? = null,
    @SerializedName("grantType") val grantType: String? = null,
    @SerializedName("oauthPassword") val oauthPassword: String? = null,
    @SerializedName("oauthUserName") val oauthUserName: String? = null,
    @SerializedName("apiName") val apiName: String? = null
)
