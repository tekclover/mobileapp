package com.clara.clientportal.enums

enum class NotificationOrderType(val orderType: String) {
    MATTER("MATTER"),
    INITIAL("INITIAL"),
    PAYMENT_PLAN("PAYMENTPLAN"),
    INVOICE("INVOICE"),
    CHECKLIST("CHECKLIST"),
    DOCUMENT_UPLOAD("DOCUMENTUPLOAD"),
    RECEIPT("RECEIPT")
}