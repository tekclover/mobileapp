package com.clara.timekeeping.model

import com.google.gson.annotations.SerializedName

data class MatterRate(
    @SerializedName("matterNumber") var matterNumber: String? = null,
    @SerializedName("classId") var classId: Int? = null,
    @SerializedName("clientId") var clientId: String? = null,
    @SerializedName("timeKeeperCode") var timeKeeperCode: String? = null,
    @SerializedName("defaultRatePerHour") var defaultRatePerHour: Number? = null,
    @SerializedName("assignedRatePerHour") var assignedRatePerHour: Number? = null
)

data class MatterDetails(
    @SerializedName("matterNumber") var matterNumber: String? = null,
    @SerializedName("classId") var classId: Int? = null,
    @SerializedName("clientId") var clientId: String? = null,
    @SerializedName("billingFormatId") var billingFormatId: String? = null
)

class TicketRequest {
    @SerializedName("allmatters")
    var allMatters: Boolean? = null

    @SerializedName("billType")
    var billType: String? = null

    @SerializedName("classId")
    var classId: Int? = null

    @SerializedName("clientId")
    var clientId: String? = null

    @SerializedName("clientName")
    var clientName: String? = null

    @SerializedName("defaultRate")
    var defaultRate: String? = null

    @SerializedName("defaultRatePerHour")
    var defaultRatePerHour: String? = null

    @SerializedName("languageId")
    var languageId: String? = null

    @SerializedName("matterNumber")
    var matterNumber: String? = null

    @SerializedName("statusId")
    var statusId: Int? = null

    @SerializedName("stimeTicketDate")
    var sTimeTicketDate: String? = null

    @SerializedName("timeKeeperCode")
    var timeKeeperCode: String? = null

    @SerializedName("timeTicketAmount")
    var timeTicketAmount: String? = null

    @SerializedName("timeTicketDescription")
    var timeTicketDescription: String? = null

    @SerializedName("timeTicketHours")
    var timeTicketHours: String? = null

    @SerializedName("timer")
    var timer: String? = null

    @SerializedName("activityCode")
    var activityCode: String? = null

    @SerializedName("taskCode")
    var taskCode: String? = null
}

data class NewTicketResponse(
    @SerializedName("timeTicketNumber") var timeTicketNumber: String? = null,
    @SerializedName("languageId") var languageId: String? = null,
    @SerializedName("classId") var classId: Int? = null,
    @SerializedName("matterNumber") var matterNumber: String? = null,
    @SerializedName("clientId") var clientId: String? = null,
    @SerializedName("timeKeeperCode") var timeKeeperCode: String? = null,
    @SerializedName("caseCategoryId") var caseCategoryId: String? = null,
    @SerializedName("caseSubCategoryId") var caseSubCategoryId: String? = null,
    @SerializedName("timeTicketHours") var timeTicketHours: Double? = null,
    @SerializedName("timeTicketDate") var timeTicketDate: String? = null,
    @SerializedName("activityCode") var activityCode: String? = null,
    @SerializedName("taskCode") var taskCode: String? = null,
    @SerializedName("defaultRate") var defaultRate: Int? = null,
    @SerializedName("rateUnit") var rateUnit: String? = null,
    @SerializedName("timeTicketAmount") var timeTicketAmount: Double? = null,
    @SerializedName("billType") var billType: String? = null,
    @SerializedName("timeTicketDescription") var timeTicketDescription: String? = null,
    @SerializedName("assignedPartner") var assignedPartner: String? = null,
    @SerializedName("assignedOn") var assignedOn: String? = null,
    @SerializedName("approvedBillableTimeInHours") var approvedBillableTimeInHours: String? = null,
    @SerializedName("approvedBillableAmount") var approvedBillableAmount: String? = null,
    @SerializedName("approvedOn") var approvedOn: String? = null,
    @SerializedName("statusId") var statusId: Int? = null,
    @SerializedName("deletionIndicator") var deletionIndicator: Int? = null,
    @SerializedName("createdBy") var createdBy: String? = null,
    @SerializedName("createdOn") var createdOn: String? = null,
    @SerializedName("updatedOn") var updatedOn: String? = null,
    @SerializedName("assignedRatePerHour") var assignedRatePerHour: String? = null,
    @SerializedName("clientName") var clientName: String? = null,
    @SerializedName("clientIdDesc") var clientIdDesc: String? = null,
    @SerializedName("matterIdDesc") var matterIdDesc: String? = null,
    @SerializedName("classIdDesc") var classIdDesc: String? = null,
    @SerializedName("statusDesc") var statusDesc: String? = null,
    @SerializedName("updatedBy") var updatedBy: String? = null,
    @SerializedName("stimeTicketDate") var sTimeTicketDate: String? = null,
    @SerializedName("assignedRatePerHourNew") var assignedRatePerHourNew: Double? = null,
    @SerializedName("defaultRatePerHourNew") var defaultRatePerHourNew: Double? = null
)

