package com.clara.timekeeping.repository

import com.clara.timekeeping.model.NotificationRequest
import com.clara.timekeeping.model.NotificationResponse
import com.clara.timekeeping.model.PushNotificationRequest
import com.clara.timekeeping.model.SearchExecuteRequest
import com.clara.timekeeping.model.TicketRequest
import com.clara.timekeeping.model.request.AuthTokenRequest
import com.clara.timekeeping.model.request.TimeTicketSummaryRequest
import com.clara.timekeeping.network.APIConstant
import com.clara.timekeeping.network.ApiService
import com.clara.timekeeping.utils.apiCall
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class TimeTicketRepository @Inject constructor(
    private val apiService: ApiService,
    private val isNetworkConnected: Boolean
) {
    suspend fun getAuthToken(
        authTokenRequest: AuthTokenRequest,
        transactionId: Int
    ) =
        apiCall(transactionId, isNetworkConnected) {
            apiService.authToken(authTokenRequest)
        }

    suspend fun login(authToken: String, userId: String, password: String) =
        apiCall(APIConstant.LOGIN_ID, isNetworkConnected) {
            apiService.login(authToken, userId, password)
        }

    suspend fun verifyOtp(authToken: String, userId: String, otp: String) =
        apiCall(APIConstant.VERIFY_EMAIL_OTP_ID, isNetworkConnected) {
            apiService.verifyEmailOtp(authToken, userId, otp)
        }

    suspend fun resendOtp(authToken: String, userId: String) =
        apiCall(APIConstant.RESEND_OTP_ID, isNetworkConnected) {
            apiService.resendOtp(authToken, userId)
        }

    suspend fun timeTicketSummary(authToken: String, request: TimeTicketSummaryRequest) =
        apiCall(APIConstant.FIND_MATTER_TIME_TICKET_ID, isNetworkConnected) {
            apiService.timeTicketSummary(authToken, request)
        }

    suspend fun searchMatterId(url: String, authToken: String, transactionId: Int) =
        apiCall(transactionId, isNetworkConnected) {
            apiService.matterId(url, authToken)
        }

    private suspend fun timeKeeperCode(authToken: String) =
        apiCall(APIConstant.SEARCH_TIME_KEEPER_CODE_ID, isNetworkConnected) {
            apiService.timeKeeperCode(authToken)
        }

    private suspend fun searchStatus(authToken: String) =
        apiCall(APIConstant.SEARCH_STATUS_ID, isNetworkConnected) {
            apiService.searchStatus(authToken)
        }

    suspend fun timeTicketSearchTimekeeperAndStatusDetails(
        authToken: String,
        isTimeKeeperCode: Boolean
    ) = channelFlow {
        if (isTimeKeeperCode) {
            launch {
                timeKeeperCode(authToken).collect(::send)
            }
        }
        launch {
            searchStatus(authToken).collect(::send)
        }
    }

    suspend fun searchExecute(
        authToken: String,
        searchExecuteRequest: SearchExecuteRequest
    ) =
        apiCall(APIConstant.SEARCH_EXECUTE_ID, isNetworkConnected) {
            apiService.searchExecute(authToken, searchExecuteRequest)
        }

    private suspend fun matterDetails(matterNo: String, authToken: String) =
        apiCall(APIConstant.MATTER_DETAILS_ID, isNetworkConnected) {
            apiService.matterDetails(matterNo, authToken)
        }

    private suspend fun matterRate(matterNo: String, authToken: String, timekeeperCode: String) =
        apiCall(APIConstant.MATTER_RATE_ID, isNetworkConnected) {
            apiService.matterRate(matterNo, authToken, timekeeperCode)
        }

    suspend fun matterDetailsAndRate(matterNo: String, authToken: String, timekeeperCode: String) =
        channelFlow {
            launch {
                matterDetails(matterNo, authToken).collect(::send)
            }
            launch {
                matterRate(matterNo, authToken, timekeeperCode).collect(::send)
            }
        }

    suspend fun newTimeTicket(
        authToken: String,
        loginUserId: String,
        newTicketRequest: TicketRequest
    ) =
        apiCall(APIConstant.NEW_TIME_TICKET_ID, isNetworkConnected) {
            apiService.newTimeTicket(authToken, loginUserId, newTicketRequest)
        }

    private suspend fun activityCode(authToken: String) =
        apiCall(APIConstant.ACTIVITY_CODE_ID, isNetworkConnected) {
            apiService.activityCode(authToken)
        }

    private suspend fun taskBasedCode(authToken: String) =
        apiCall(APIConstant.TASK_BASED_CODE_ID, isNetworkConnected) {
            apiService.taskBasedCode(authToken)
        }

    suspend fun deleteTicket(timeTicketNumber: String, authToken: String, loginUserId: String) =
        apiCall(APIConstant.DELETE_TICKET_ID, isNetworkConnected) {
            apiService.deleteTicket(timeTicketNumber, authToken, loginUserId)
        }

    suspend fun editTicket(
        timeTicketNumber: String,
        authToken: String,
        loginUserId: String,
        request: TicketRequest
    ) =
        apiCall(APIConstant.EDIT_TICKET_ID, isNetworkConnected) {
            apiService.editTicket(timeTicketNumber, authToken, loginUserId, request)
        }

    suspend fun newTicketDropdownDetails(authToken: String, isFromEdit: Boolean) =
        channelFlow {
            if (isFromEdit.not()) {
                launch {
                    timeKeeperCode(authToken).collect(::send)
                }
            }
            launch {
                activityCode(authToken).collect(::send)
            }
            launch {
                taskBasedCode(authToken).collect(::send)
            }
        }

    suspend fun timeTicketDetails(
        timeTicketNumber: String,
        authToken: String
    ) =
        apiCall(APIConstant.TIME_TICKET_DETAILS_ID, isNetworkConnected) {
            apiService.timeTicketDetails(timeTicketNumber, authToken)
        }

    suspend fun getNotificationMessages(
        authToken: String,
        notificationRequest: NotificationRequest
    ) = apiCall(APIConstant.NOTIFICATION_MESSAGE_ID, isNetworkConnected) {
        apiService.getNotification(authToken, notificationRequest)
    }

    suspend fun notificationCount(clientUserId: String, authToken: String) =
        apiCall(APIConstant.NOTIFICATION_COUNT_ID, isNetworkConnected) {
            apiService.getNotificationCount(clientUserId, authToken)
        }

    suspend fun notificationMessageUpdate(
        authToken: String,
        loginUserId: String,
        updateRequest: List<NotificationResponse>
    ) = apiCall(APIConstant.NOTIFICATION_MESSAGE_UPDATE_ID, isNetworkConnected) {
        apiService.notificationUpdate(authToken, loginUserId, updateRequest)
    }

    suspend fun createPushNotification(
        authToken: String,
        loginUserId: String,
        pushNotificationRequest: PushNotificationRequest
    ) = apiCall(APIConstant.CREATE_PUSH_NOTIFICATION_ID, isNetworkConnected) {
        apiService.createPushNotification(authToken, loginUserId, pushNotificationRequest)
    }
}