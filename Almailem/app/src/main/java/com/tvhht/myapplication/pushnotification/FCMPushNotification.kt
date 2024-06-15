package com.tvhht.myapplication.pushnotification

import com.google.gson.annotations.SerializedName

data class FCMTokenRequest(
    @SerializedName("companyId") val companyId: String? = null,
    @SerializedName("deviceId") val deviceId: String? = null,
    @SerializedName("deletionIndicator") val deletionIndicator: Int? = null,
    @SerializedName("languageId") val languageId: String? = null,
    @SerializedName("plantId") val plantId: String? = null,
    @SerializedName("tokenId") val tokenId: String? = null,
    @SerializedName("userId") val userId: String? = null,
    @SerializedName("warehouseId") val warehouseId: String? = null,
    @SerializedName("isLoggedIn") val isLoggedIn: Boolean? = null
)

data class FCMTokenResponse(
    @SerializedName("languageId") var languageId: String? = null,
    @SerializedName("companyId") var companyId: String? = null,
    @SerializedName("plantId") var plantId: String? = null,
    @SerializedName("warehouseId") var warehouseId: String? = null,
    @SerializedName("deviceId") var deviceId: String? = null,
    @SerializedName("userId") var userId: String? = null,
    @SerializedName("tokenId") var tokenId: String? = null,
    @SerializedName("deletionIndicator") var deletionIndicator: Int? = null,
    @SerializedName("isLoggedIn") var isLoggedIn: Boolean? = null,
    @SerializedName("createdBy") var createdBy: String? = null,
    @SerializedName("createdOn") var createdOn: String? = null,
    @SerializedName("updatedBy") var updatedBy: String? = null,
    @SerializedName("updatedOn") var updatedOn: String? = null
)