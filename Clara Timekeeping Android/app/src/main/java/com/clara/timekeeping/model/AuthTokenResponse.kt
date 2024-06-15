package com.clara.timekeeping.model

import com.google.gson.annotations.SerializedName

data class AuthTokenResponse(
    @SerializedName("access_token") val accessToken: String?,
    @SerializedName("token_type") val tokenType: String?,
    @SerializedName("refresh_token") val refreshToken: String?,
    @SerializedName("expires_in") val expiresIn: String?,
    @SerializedName("scope") val scope: String?,
    @SerializedName("jti") val jti: String?,
    var transactionId: Int = 0
)