package com.clara.clientportal.model.request

import com.google.gson.annotations.SerializedName

data class CheckListViewDetailsRequest(
    @SerializedName("checkListNo") val checkListNo: List<Int>? = null,
    @SerializedName("matterHeaderId") val matterHeaderId: List<Int>? = null,
    @SerializedName("matterNumber") val matterNumber: List<String>? = null
)
