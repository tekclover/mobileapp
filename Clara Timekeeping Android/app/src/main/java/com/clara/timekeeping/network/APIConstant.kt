package com.clara.timekeeping.network

import com.clara.timekeeping.BuildConfig

object APIConstant {
    const val BASE_URL = BuildConfig.BASE_URL
    const val AUTH_TOKEN = "auth-token"
    const val LOGIN = "mnr-setup-service/login"
    const val VERIFY_EMAIL_OTP = "mnr-setup-service/login/verifyEmailOTP"
    const val RESEND_OTP = "mnr-setup-service/login/emailOTP"
    const val FIND_MATTER_TIME_TICKET =
        "mnr-management-service/mattertimeticket/findMatterTimeTicketMobile"
    const val DROP_DOWN_MATTER_ID = "mnr-management-service/mattergenacc/dropdown/matter"
    const val TIME_KEEPER_CODE = "mnr-setup-service/timekeeperCode"
    const val SEARCH_STATUS = "mnr-setup-service/status/mobile"
    const val SEARCH_EXECUTE = "mnr-management-service/mattertimeticket/findMatterTimeTicket"
    const val DROP_DOWN_MATTER_ID_OPEN = "mnr-management-service/dropdown/matter/open"
    const val MATTER_DETAILS = "mnr-management-service/mattergenacc/{MATTER_NO}"
    const val MATTER_RATE = "mnr-management-service/matterrate/{MATTER_NO}"
    const val NEW_TIME_TICKET = "mnr-management-service/mattertimeticket"
    const val ACTIVITY_CODE = "mnr-setup-service/activityCode"
    const val TASK_BASED_CODE = "mnr-setup-service/taskbasedCode"
    const val DELETE_TICKET = "mnr-management-service/mattertimeticket/{TIME_TICKET_NUMBER}"
    const val EDIT_TICKET = "mnr-management-service/mattertimeticket/{TIME_TICKET_NUMBER}"
    const val TIME_TICKET_DETAILS = "mnr-management-service/mattertimeticket/mobile/{TIME_TICKET_NUMBER}"
    const val NOTIFICATION_MESSAGE = "mnr-management-service/notificationMessage/findNotificationMessage"
    const val NOTIFICATION_MESSAGE_UPDATE = "mnr-management-service/notificationMessage/update"
    const val NOTIFICATION_COUNT = "mnr-management-service/notificationMessage/clientUserId/{clientUserId}"
    const val CREATE_PUSH_NOTIFICATION = "mnr-management-service/hhtnotification/createnotification"

    const val LOGIN_ID = 10001
    const val VERIFY_EMAIL_OTP_ID = 10002
    const val RESEND_OTP_ID = 10003
    const val FIND_MATTER_TIME_TICKET_ID = 10004
    const val SEARCH_TIME_KEEPER_CODE_ID = 10005
    const val SEARCH_STATUS_ID = 10006
    const val SEARCH_EXECUTE_ID = 10007
    const val MATTER_NUMBER_CHECKED_ID = 10008
    const val MATTER_NUMBER_UNCHECKED_ID = 10009
    const val MATTER_DETAILS_ID = 10010
    const val MATTER_RATE_ID = 10011
    const val NEW_TIME_TICKET_ID = 10012
    const val ACTIVITY_CODE_ID = 10013
    const val TASK_BASED_CODE_ID = 10014
    const val DELETE_TICKET_ID = 10015
    const val EDIT_TICKET_ID = 10016
    const val TIME_TICKET_DETAILS_ID = 10017
    const val NOTIFICATION_MESSAGE_ID = 10018
    const val NOTIFICATION_MESSAGE_UPDATE_ID = 10019
    const val NOTIFICATION_COUNT_ID = 10020
    const val CREATE_PUSH_NOTIFICATION_ID = 10021
}