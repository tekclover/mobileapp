package com.clara.clientportal.network

import com.clara.clientportal.BuildConfig

object APIConstant {
    const val BASE_URL = BuildConfig.BASE_URL
    const val SETUP_SERVICE_AUTH_TOKEN = "auth-token"
    const val SENT_EMAIL_OTP = "mnr-setup-service/login/clientUser/emailOTP"
    const val VERIFY_EMAIL_OTP = "mnr-setup-service/login/clientUser/verifyEmailOTP"
    const val SENT_MOBILE_OTP = "mnr-setup-service/login/clientUser/sendOTP"
    const val VERIFY_MOBILE_OTP = "mnr-setup-service/login/clientUser/verifyOTP"
    const val FIND_CLIENT_GENERAL = "mnr-management-service/clientgeneral/findClientGeneral"
    const val FIND_CLIENT_USER_NEW = "mnr-setup-service/clientUser/findClientUserNew"
    const val FIND_MATTER_GENERAL_NEW = "mnr-management-service/mattergenacc/findMatterGeneralNew"
    const val FIND_MATTER_GENERAL_MOBILE = "mnr-management-service/mattergenacc/findMatterGeneralMobile"
    const val FIND_QUOTATION = "mnr-accounting-service/quotationheader/findQuotationHeader"
    const val MATTER_POPUP_DETAILS = "mnr-management-service/mattergenacc/date/"
    const val FIND_INVOICE = "mnr-accounting-service/invoiceheader/findInvoiceHeader"
    const val STATUS = "mnr-setup-service/status"
    const val PAYMENT_PLAN = "mnr-accounting-service/paymentplanheader"
    const val PAYMENT_PLAN_DETAILS = "mnr-accounting-service/paymentplanheader/"
    const val CHECK_LIST = "mnr-management-service/matterdoclistheader/find"
    const val CHECK_LIST_VIEW_DETAILS = "mnr-management-service/matterdoclistheader/find"
    const val RECEIPT_NO = "mnr-management-service/receiptappnotice"
    const val UPLOADED_DOCUMENT = "mnr-management-service/matterdocument/findMatterDocument"
    const val MATTER_ID = "mnr-management-service/mattergenacc/findMatterGenAccs"
    const val DOCUMENT_UPLOAD = "doc-storage/upload"
    const val DOWNLOAD = "doc-storage/download"
    const val DOCUMENT_UPLOAD_UPDATE = "mnr-management-service/matterdocument/clientPortal/docsUpload"
    const val CHECKLIST_DOCUMENT_UPLOAD_UPDATE = "mnr-management-service/matterdoclistheader/new/"
    const val DOCUMENT_UPLOAD_MATTER = "mnr-management-service/mattergenacc"
    const val CHECKLIST_DOCUMENT_SUBMIT = "mnr-management-service/matterdoclist/{MATTER_NO}/clientPortal/docCheckList"
    const val CREATE_PUSH_NOTIFICATION = "mnr-management-service/hhtnotification/createnotification"
    const val NOTIFICATION_MESSAGE = "mnr-management-service/notificationMessage/findNotificationMessage"
    const val NOTIFICATION_MESSAGE_UPDATE = "mnr-management-service/notificationMessage/update"
    const val NOTIFICATION_COUNT = "mnr-management-service/notificationMessage/{clientId}"

    const val SENT_EMAIL_OTP_ID = 10001
    const val VERIFY_EMAIL_OTP_ID = 10002
    const val SENT_MOBILE_OTP_ID = 10003
    const val VERIFY_MOBILE_OTP_ID = 10004
    const val FIND_CLIENT_GENERAL_ID = 10005
    const val UPLOAD_DOCUMENT = 10006
    const val MATTER_IDS = 10007
    const val UPLOAD = 10008
    const val CHECK_LIST_Id = 10009
    const val DOCUMENT_UPLOAD_UPDATE_ID = 10010
    const val DOCUMENT_UPLOAD_MATTER_ID = 10011
    const val CHECK_LIST_DOCUMENT_SUBMIT_ID = 10012
    const val CREATE_PUSH_NOTIFICATION_ID = 10013
    const val NOTIFICATION_MESSAGE_ID = 10014
    const val NOTIFICATION_MESSAGE_UPDATE_ID = 10015
    const val NOTIFICATION_COUNT_ID = 10016
    const val FIND_CLIENT_USER_NEW_ID = 10017
}