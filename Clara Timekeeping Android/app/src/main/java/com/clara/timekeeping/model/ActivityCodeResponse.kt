package com.clara.timekeeping.model

import com.google.gson.annotations.SerializedName

data class ActivityCodeResponse(
    @SerializedName("activityCodeId") var activityCodeId: Int? = null,
    @SerializedName("activityCode") var activityCode: String? = null,
    @SerializedName("languageId") var languageId: String? = null,
    @SerializedName("classId") var classId: Int? = null,
    @SerializedName("activityCodeDescription") var activityCodeDescription: String? = null,
    @SerializedName("activityCodeStatus") var activityCodeStatus: String? = null,
    @SerializedName("deletionIndicator") var deletionIndicator: Int? = null,
    @SerializedName("createdBy") var createdBy: String? = null,
    @SerializedName("updatedBy") var updatedBy: String? = null,
    @SerializedName("createdOn") var createdOn: String? = null,
    @SerializedName("updatedOn") var updatedOn: String? = null
)
