package com.clara.timekeeping.model

import com.google.gson.annotations.SerializedName

data class NotificationRequest(
    @SerializedName("clientUserId") var clientUserIdList: List<String>? = null,
    @SerializedName("orderType") var orderTypeList: List<String>? = null
)

data class NotificationResponse(
    @SerializedName("notificationId") var notificationId: Int? = null,
    @SerializedName("classId") var classId: String? = null,
    @SerializedName("clientId") var clientId: String? = null,
    @SerializedName("clientUserId") var clientUserId: String? = null,
    @SerializedName("title") var title: String? = null,
    @SerializedName("message") var message: String? = null,
    @SerializedName("menu") var menu: Boolean? = null,
    @SerializedName("tab") var tab: Boolean? = null,
    @SerializedName("orderType") var orderType: String? = null,
    @SerializedName("deletionIndicator") var deletionIndicator: Int? = null,
    @SerializedName("createdBy") var createdBy: String? = null,
    @SerializedName("createdOn") var createdOn: String? = null,
    @SerializedName("updatedBy") var updatedBy: String? = null,
    @SerializedName("updatedOn") var updatedOn: String? = null
)

data class NotificationCount(
    @SerializedName("overAllCount") var overAllCount: Int? = null
)