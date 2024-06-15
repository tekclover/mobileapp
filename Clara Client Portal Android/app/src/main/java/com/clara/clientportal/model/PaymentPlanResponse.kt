package com.clara.clientportal.model

import com.google.gson.annotations.SerializedName

data class PaymentPlanResponse(
    @SerializedName("matterNumber") val matterNumber: String? = null,
    @SerializedName("statusId") val statusId: Int? = -1,
    @SerializedName("clientId") val clientId: String? = null,
    @SerializedName("paymentPlanNumber") val paymentPlanNumber: String? = null,
    @SerializedName("paymentPlanDate") val paymentPlanDate: String? = null,
    @SerializedName("paymentPlanTotalAmount") val paymentPlanTotalAmount: Number?,
    @SerializedName("paymentPlanRevisionNo") val paymentPlanRevisionNo: Int? = -1,
    @SerializedName("dueAmount") val dueAmount: Number? = 0,
    @SerializedName("noOfInstallment") val noOfInstallment: Int? = 0,
    @SerializedName("paymentPlanLines") var paymentPlanLines: List<PaymentPlanLines>? = null,
    var isExpand: Boolean = false
)

data class PaymentPlanLines(
    @SerializedName("matterNumber") var matterNumber: String? = null,
    @SerializedName("clientId") var clientId: String? = null,
    @SerializedName("paymentPlanNumber") var paymentPlanNumber: String? = null,
    @SerializedName("paymentPlanRevisionNo") var paymentPlanRevisionNo: Number? = null,
    @SerializedName("itemNumber") var itemNumber: Int? = null,
    @SerializedName("dueDate") var dueDate: String? = null,
    @SerializedName("paidAmount") var paidAmount: Number? = null,
    @SerializedName("balanceAmount") var balanceAmount: Number? = null,
    @SerializedName("dueAmount") var dueAmount: Number? = null,
    @SerializedName("remainingDueNow") var remainingDueNow: Number? = null,
    @SerializedName("statusId") var statusId: Number? = null,
)