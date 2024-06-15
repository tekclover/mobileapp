package com.clara.clientportal.model

import com.google.gson.annotations.SerializedName

data class PushNotificationRequest(
    @SerializedName("classId") var classId: String? = null,
    @SerializedName("clientId") var clientId: String? = null,
    @SerializedName("clientUserId") var clientUserId: String? = null,
    @SerializedName("deviceId") var deviceId: String? = null,
    @SerializedName("isLoggedIn") var isLoggedIn: Boolean? = null,
    @SerializedName("tokenId") var tokenId: String? = null
)

data class PushNotificationResponse(
    @SerializedName("classId") var classId: String? = null,
    @SerializedName("clientId") var clientId: String? = null,
    @SerializedName("clientUserId") var clientUserId: String? = null,
    @SerializedName("deviceId") var deviceId: String? = null,
    @SerializedName("tokenId") var tokenId: String? = null,
    @SerializedName("isLoggedIn") var isLoggedIn: Boolean? = null,
    @SerializedName("deletionIndicator") var deletionIndicator: Int? = null,
    @SerializedName("createdBy") var createdBy: String? = null,
    @SerializedName("createdOn") var createdOn: String? = null,
    @SerializedName("updatedBy") var updatedBy: String? = null,
    @SerializedName("updatedOn") var updatedOn: String? = null
)