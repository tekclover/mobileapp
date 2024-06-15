package com.clara.timekeeping.model

import com.google.gson.annotations.SerializedName

data class TaskBasedCodeResponse(
    @SerializedName("taskCode") var taskCode: String? = null,
    @SerializedName("languageId") var languageId: String? = null,
    @SerializedName("classId") var classId: Int? = null,
    @SerializedName("taskcodeDescription") var taskcodeDescription: String? = null,
    @SerializedName("taskcodeType") var taskcodeType: String? = null,
    @SerializedName("summaryLevel") var summaryLevel: Boolean? = null,
    @SerializedName("taskCodeStatus") var taskCodeStatus: String? = null,
    @SerializedName("deletionIndicator") var deletionIndicator: Int? = null,
    @SerializedName("referenceField1") var referenceField1: String? = null,
    @SerializedName("referenceField2") var referenceField2: String? = null,
    @SerializedName("referenceField3") var referenceField3: String? = null,
    @SerializedName("referenceField4") var referenceField4: String? = null,
    @SerializedName("referenceField5") var referenceField5: String? = null,
    @SerializedName("referenceField6") var referenceField6: String? = null,
    @SerializedName("referenceField7") var referenceField7: String? = null,
    @SerializedName("referenceField8") var referenceField8: String? = null,
    @SerializedName("referenceField9") var referenceField9: String? = null,
    @SerializedName("referenceField10") var referenceField10: String? = null,
    @SerializedName("createdBy") var createdBy: String? = null,
    @SerializedName("updatedBy") var updatedBy: String? = null,
    @SerializedName("createdOn") var createdOn: String? = null,
    @SerializedName("updatedOn") var updatedOn: String? = null
)
