package com.clara.timekeeping.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class TimeTicketSummaryResponse(
    @SerializedName("matterNumber") var matterNumber: String? = null,
    @SerializedName("matterIdDesc") var matterIdDesc: String? = null,
    @SerializedName("timeTicketNumber") var timeTicketNumber: String? = null,
    @SerializedName("stimeTicketDate") var sTimeTicketDate: String? = null,
    @SerializedName("timeTicketHours") var timeTicketHours: Double? = null,
    @SerializedName("timeTicketAmount") var timeTicketAmount: Double? = null,
    @SerializedName("billType") var billType: String? = null,
    @SerializedName("taskCode") var taskCode: String? = null,
    @SerializedName("activityCode") var activityCode: String? = null,
    @SerializedName("timeTicketDescription") var timeTicketDescription: String? = null,
    @SerializedName("timeKeeperCode") var timeKeeperCode: String? = null,
    @SerializedName("clientName") var clientName: String? = null,
    @SerializedName("clientId") var clientId: String? = null,
    @SerializedName("clientIdDesc") var clientIdDesc: String? = null,
    @SerializedName("assignedRatePerHour") var assignedRatePerHour: Number? = null,
    @SerializedName("defaultRate") var defaultRate: Number? = null,
    @SerializedName("statusId") var statusId: Int? = null,
    var isExpand: Boolean = false
) : Serializable