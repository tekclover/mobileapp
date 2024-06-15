package com.clara.timekeeping.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable
import java.util.ArrayList

data class MatterIdResponse(
    @SerializedName("matterDropDown") val matterDropDownList: ArrayList<MatterId>? = null
): Serializable

data class MatterId(
    @SerializedName("matterNumber") val matterNumber: String? = null,
    @SerializedName("matterDescription") val matterDescription: String? = null,
    @SerializedName("clientId") val clientId: String? = null,
    @SerializedName("clientName") val clientName: String? = null
)

data class TimeKeeperCode(
    @SerializedName("timekeeperCode") val timekeeperCode: String? = null,
    @SerializedName("languageId") val languageId: String? = null,
    @SerializedName("classId") val classId: Int? = null,
    @SerializedName("userTypeId") val userTypeId: Int? = null,
    @SerializedName("defaultRate") val defaultRate: Number? = null,
    @SerializedName("timekeeperName") val timekeeperName: String? = null,
    @SerializedName("rateUnit") val rateUnit: String? = null,
    @SerializedName("timekeeperStatus") val timekeeperStatus: String? = null
)

data class SearchStatus(
    @SerializedName("statusId") val statusId: Int? = null,
    @SerializedName("statusDesc") val statusDesc: String? = null,
    @SerializedName("statusIdDesc") val statusIdDesc: String? = null
)

data class SearchExecuteRequest(
    @SerializedName("statusId") val statusIdList: List<Int>? = null,
    @SerializedName("timeKeeperCode") val timeKeeperCodeList: List<String>? = null,
    @SerializedName("matterNumber") val matterNumberList: List<String>? = null,
    @SerializedName("billType") val billTypeList: List<String>? = null,
    @SerializedName("sstartTimeTickerDate") val sstartTimeTickerDate: String? = null,
    @SerializedName("eendTimeTicketDate") val eendTimeTicketDate: String? = null
)

data class SearchExecuteResponse(
    @SerializedName("statusId") val statusId: Int? = null
)