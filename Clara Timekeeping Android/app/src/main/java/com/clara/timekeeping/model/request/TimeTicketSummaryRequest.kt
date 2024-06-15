package com.clara.timekeeping.model.request

import com.google.gson.annotations.SerializedName

data class TimeTicketSummaryRequest(
    @SerializedName("statusId") var statusIdList: List<Int>? = null,
    @SerializedName("timeKeeperCode") var timeKeeperCodeList: List<String>? = null,
    @SerializedName("matterNumber") var matterNumberList: List<String>? = null,
    @SerializedName("sstartTimeTicketDate") var startDateList: String? = null,
    @SerializedName("sendTimeTicketDate") var endDateList: String? = null,
    @SerializedName("billType") var billTypeList: List<String>? = null
)
