package com.clara.clientportal.model

import com.google.gson.annotations.SerializedName

data class NotificationRequest(
    @SerializedName("clientId") var clientIdList: List<String>? = null,
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
    @SerializedName("matterCount") var matterCount: Int? = null,
    @SerializedName("initialRetainerCount") var initialRetainerCount: Int? = null,
    @SerializedName("paymentPlantCount") var paymentPlantCount: Int? = null,
    @SerializedName("invoiceCount") var invoiceCount: Int? = null,
    @SerializedName("checkListCount") var checkListCount: Int? = null,
    @SerializedName("documentUploadCount") var documentUploadCount: Int? = null,
    @SerializedName("receiptNoCount") var receiptNoCount: Int? = null,
    @SerializedName("overAllCount") var overAllCount: Int? = null
)