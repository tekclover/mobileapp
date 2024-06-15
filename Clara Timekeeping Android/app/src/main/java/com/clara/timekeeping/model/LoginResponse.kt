package com.clara.timekeeping.model

import com.google.gson.annotations.SerializedName

data class LoginResponse(
    @SerializedName("userId") var userId: String? = null,
    @SerializedName("classId") var classId: Int? = null,
    @SerializedName("firstName") var firstName: String? = null,
    @SerializedName("lastName") var lastName: String? = null,
    @SerializedName("fullName") var fullName: String? = null,
    @SerializedName("emailId") var emailId: String? = null,
    @SerializedName("phoneNumber") var phoneNumber: String? = null
)
