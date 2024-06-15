package com.clara.timekeeping.network

import com.clara.timekeeping.model.ActivityCodeResponse
import com.clara.timekeeping.model.AuthTokenResponse
import com.clara.timekeeping.model.DeleteTicketResponse
import com.clara.timekeeping.model.LoginResponse
import com.clara.timekeeping.model.MatterDetails
import com.clara.timekeeping.model.MatterIdResponse
import com.clara.timekeeping.model.MatterRate
import com.clara.timekeeping.model.NewTicketResponse
import com.clara.timekeeping.model.NotificationCount
import com.clara.timekeeping.model.NotificationRequest
import com.clara.timekeeping.model.NotificationResponse
import com.clara.timekeeping.model.PushNotificationRequest
import com.clara.timekeeping.model.PushNotificationResponse
import com.clara.timekeeping.model.SearchExecuteRequest
import com.clara.timekeeping.model.SearchStatus
import com.clara.timekeeping.model.TaskBasedCodeResponse
import com.clara.timekeeping.model.TicketRequest
import com.clara.timekeeping.model.TimeKeeperCode
import com.clara.timekeeping.model.TimeTicketSummaryResponse
import com.clara.timekeeping.model.request.AuthTokenRequest
import com.clara.timekeeping.model.request.TimeTicketSummaryRequest
import com.clara.timekeeping.utils.Constants
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.Url

interface ApiService {
    @POST(APIConstant.AUTH_TOKEN)
    suspend fun authToken(@Body serviceAuthTokenRequest: AuthTokenRequest): Response<AuthTokenResponse>

    @GET(APIConstant.LOGIN)
    suspend fun login(
        @Query(Constants.AUTH_TOKEN) authToken: String,
        @Query(Constants.USER_ID) userId: String,
        @Query(Constants.PASSWORD) password: String
    ): Response<LoginResponse>

    @GET(APIConstant.VERIFY_EMAIL_OTP)
    suspend fun verifyEmailOtp(
        @Query(Constants.AUTH_TOKEN) authToken: String,
        @Query(Constants.USER_ID) userId: String,
        @Query(Constants.OTP) otp: String
    ): Response<LoginResponse>

    @GET(APIConstant.RESEND_OTP)
    suspend fun resendOtp(
        @Query(Constants.AUTH_TOKEN) authToken: String,
        @Query(Constants.USER_ID) userId: String
    ): Response<Boolean>

    @POST(APIConstant.FIND_MATTER_TIME_TICKET)
    suspend fun timeTicketSummary(
        @Query(Constants.AUTH_TOKEN) authToken: String,
        @Body request: TimeTicketSummaryRequest
    ): Response<List<TimeTicketSummaryResponse>>

    @GET
    suspend fun matterId(
        @Url url: String,
        @Query(Constants.AUTH_TOKEN) authToken: String
    ): Response<MatterIdResponse>

    @GET(APIConstant.TIME_KEEPER_CODE)
    suspend fun timeKeeperCode(
        @Query(Constants.AUTH_TOKEN) authToken: String
    ): Response<List<TimeKeeperCode>>

    @GET(APIConstant.SEARCH_STATUS)
    suspend fun searchStatus(
        @Query(Constants.AUTH_TOKEN) authToken: String
    ): Response<List<SearchStatus>>

    @POST(APIConstant.SEARCH_EXECUTE)
    suspend fun searchExecute(
        @Query(Constants.AUTH_TOKEN) authToken: String,
        @Body request: SearchExecuteRequest
    ): Response<List<TimeTicketSummaryResponse>>

    @GET(APIConstant.MATTER_RATE)
    suspend fun matterRate(
        @Path(Constants.MATTER_NO) matteNumber: String,
        @Query(Constants.AUTH_TOKEN) authToken: String,
        @Query(Constants.QUERY_PARAM_TIMEKEEPER_CODE) timekeeperCode: String,
    ): Response<MatterRate>

    @GET(APIConstant.MATTER_DETAILS)
    suspend fun matterDetails(
        @Path(Constants.MATTER_NO) matteNumber: String,
        @Query(Constants.AUTH_TOKEN) authToken: String
    ): Response<MatterDetails>

    @POST(APIConstant.NEW_TIME_TICKET)
    suspend fun newTimeTicket(
        @Query(Constants.AUTH_TOKEN) authToken: String,
        @Query(Constants.PARAM_LOGIN_USER_ID) loginUserID: String,
        @Body request: TicketRequest
    ): Response<NewTicketResponse>

    @GET(APIConstant.ACTIVITY_CODE)
    suspend fun activityCode(
        @Query(Constants.AUTH_TOKEN) authToken: String
    ): Response<List<ActivityCodeResponse>>

    @GET(APIConstant.TASK_BASED_CODE)
    suspend fun taskBasedCode(
        @Query(Constants.AUTH_TOKEN) authToken: String
    ): Response<List<TaskBasedCodeResponse>>

    @DELETE(APIConstant.DELETE_TICKET)
    suspend fun deleteTicket(
        @Path(Constants.TIME_TICKET_NUMBER) timeTicketNumber: String,
        @Query(Constants.AUTH_TOKEN) authToken: String,
        @Query(Constants.PARAM_LOGIN_USER_ID) loginUserID: String
    ): Response<DeleteTicketResponse>

    @PATCH(APIConstant.EDIT_TICKET)
    suspend fun editTicket(
        @Path(Constants.TIME_TICKET_NUMBER) timeTicketNumber: String,
        @Query(Constants.AUTH_TOKEN) authToken: String,
        @Query(Constants.PARAM_LOGIN_USER_ID) loginUserID: String,
        @Body request: TicketRequest
    ): Response<NewTicketResponse>

    @GET(APIConstant.TIME_TICKET_DETAILS)
    suspend fun timeTicketDetails(
        @Path(Constants.TIME_TICKET_NUMBER) timeTicketNumber: String,
        @Query(Constants.AUTH_TOKEN) authToken: String,
    ): Response<NewTicketResponse>

    @POST(APIConstant.NOTIFICATION_MESSAGE)
    suspend fun getNotification(
        @Query(Constants.AUTH_TOKEN) authToken: String,
        @Body request: NotificationRequest
    ): Response<List<NotificationResponse>>

    @GET(APIConstant.NOTIFICATION_COUNT)
    suspend fun getNotificationCount(
        @Path(Constants.CLIENT_USER_ID) clientUserId: String,
        @Query(Constants.AUTH_TOKEN) authToken: String
    ): Response<NotificationCount>

    @PATCH(APIConstant.NOTIFICATION_MESSAGE_UPDATE)
    suspend fun notificationUpdate(
        @Query(Constants.AUTH_TOKEN) authToken: String,
        @Query(Constants.PARAM_LOGIN_USER_ID) loginUserID: String,
        @Body request: List<NotificationResponse>
    ): Response<List<NotificationResponse>>

    @POST(APIConstant.CREATE_PUSH_NOTIFICATION)
    suspend fun createPushNotification(
        @Query(Constants.AUTH_TOKEN) authToken: String,
        @Query(Constants.PARAM_LOGIN_USER_ID) loginUserId: String,
        @Body request: PushNotificationRequest
    ): Response<PushNotificationResponse>
}