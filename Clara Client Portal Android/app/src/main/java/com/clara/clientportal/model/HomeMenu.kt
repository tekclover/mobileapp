package com.clara.clientportal.model

data class HomeMenu(
    val name: String?,
    val icon: String?,
    var classId: Int? = 0,
    var matterCount: Int? = 0,
    var quotationCount: Int? = 0,
    var paymentPlanCount: Int? = 0,
    var invoiceCount: Int? = 0,
    var documentsCount: Int? = 0,
    var documentsCountUploadCount: String? = "0",
    var receiptNoCount: Int? = 0
)
