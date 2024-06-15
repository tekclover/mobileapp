package com.clara.clientportal.network

import com.clara.clientportal.model.CheckListDocumentSubmitResponse
import com.clara.clientportal.model.CheckListResponse
import com.clara.clientportal.model.DocumentUploadUpdateResponse
import com.clara.clientportal.model.DocumentUploadedResponse
import com.clara.clientportal.model.FindClientGeneralResponse
import com.clara.clientportal.model.InvoiceResponse
import com.clara.clientportal.model.MatterDetails
import com.clara.clientportal.model.MatterIdResponse
import com.clara.clientportal.model.MatterPopupDetailsResponse
import com.clara.clientportal.model.MatterResponse
import com.clara.clientportal.model.NotificationCount
import com.clara.clientportal.model.NotificationRequest
import com.clara.clientportal.model.NotificationResponse
import com.clara.clientportal.model.PaymentPlanResponse
import com.clara.clientportal.model.PushNotificationRequest
import com.clara.clientportal.model.PushNotificationResponse
import com.clara.clientportal.model.QuotationResponse
import com.clara.clientportal.model.ReceiptNoResponse
import com.clara.clientportal.model.SetupServiceAuthResponse
import com.clara.clientportal.model.StatusResponse
import com.clara.clientportal.model.UploadResponse
import com.clara.clientportal.model.VerifyOtpResponse
import com.clara.clientportal.model.request.CheckListDocumentUploadUpdateRequest
import com.clara.clientportal.model.request.CheckListViewDetailsRequest
import com.clara.clientportal.model.request.DocumentUploadUpdateRequest
import com.clara.clientportal.model.request.FindClientGeneralEmailRequest
import com.clara.clientportal.model.request.FindClientUserNewRequest
import com.clara.clientportal.model.request.FindGeneralClientMobileRequest
import com.clara.clientportal.model.request.MatterRequest
import com.clara.clientportal.model.request.SetupServiceAuthTokenRequest
import com.clara.clientportal.utils.Constants
import okhttp3.RequestBody
import org.json.JSONObject
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.Url

interface ApiService {
    @POST(APIConstant.SETUP_SERVICE_AUTH_TOKEN)
    suspend fun setupServiceAuthToken(@Body serviceAuthTokenRequest: SetupServiceAuthTokenRequest): Response<SetupServiceAuthResponse>

    @GET(APIConstant.SENT_EMAIL_OTP)
    suspend fun sendEmailOtp(
        @Query(Constants.AUTH_TOKEN) authToken: String,
        @Query(Constants.EMAIL_ID) emailId: String
    ): Response<Boolean>

    @GET(APIConstant.VERIFY_EMAIL_OTP)
    suspend fun verifyEmailOtp(
        @Query(Constants.AUTH_TOKEN) authToken: String,
        @Query(Constants.EMAIL_ID) emailId: String,
        @Query(Constants.OTP) otp: String
    ): Response<VerifyOtpResponse>

    @GET(APIConstant.SENT_MOBILE_OTP)
    suspend fun sendMobileOtp(
        @Query(Constants.AUTH_TOKEN) authToken: String,
        @Query(Constants.CONTACT_NUMBER) contactNo: String
    ): Response<Boolean>

    @GET(APIConstant.VERIFY_MOBILE_OTP)
    suspend fun verifyMobileOtp(
        @Query(Constants.AUTH_TOKEN) authToken: String,
        @Query(Constants.CONTACT_NUMBER) contactNo: String,
        @Query(Constants.OTP) otp: String
    ): Response<VerifyOtpResponse>
    @POST(APIConstant.FIND_CLIENT_GENERAL)
    suspend fun findGeneralClient(
        @Query(Constants.AUTH_TOKEN) authToken: String,
        @Body request: JSONObject
    ): Response<List<FindClientGeneralResponse>>

    @POST(APIConstant.FIND_CLIENT_GENERAL)
    suspend fun findGeneralClientEmail(
        @Query(Constants.AUTH_TOKEN) authToken: String,
        @Body findClientGeneralRequest: FindClientGeneralEmailRequest
    ): Response<List<FindClientGeneralResponse>>
    @POST(APIConstant.FIND_CLIENT_GENERAL)
    suspend fun findGeneralClientMobile(
        @Query(Constants.AUTH_TOKEN) authToken: String,
        @Body findClientGeneralRequest: FindGeneralClientMobileRequest
    ): Response<List<FindClientGeneralResponse>>

    @POST(APIConstant.FIND_CLIENT_USER_NEW)
    suspend fun findClientUserNew(
        @Query(Constants.AUTH_TOKEN) authToken: String,
        @Body findClientUserNewRequest: FindClientUserNewRequest
    ): Response<List<FindClientGeneralResponse>>

    @POST(APIConstant.FIND_MATTER_GENERAL_MOBILE)
    suspend fun findMatterGeneralNew(
        @Query(Constants.AUTH_TOKEN) authToken: String,
        @Body matterRequest: MatterRequest
    ): Response<List<MatterResponse>>

    @GET()
    suspend fun matterPopupDetails(
        @Url url: String,
        @Query(Constants.AUTH_TOKEN) authToken: String
    ): Response<MatterPopupDetailsResponse>

    @POST(APIConstant.FIND_QUOTATION)
    suspend fun findQuotation(
        @Query(Constants.AUTH_TOKEN) authToken: String,
        @Body matterRequest: MatterRequest
    ): Response<List<QuotationResponse>>

    @POST(APIConstant.FIND_INVOICE)
    suspend fun findInvoice(
        @Query(Constants.AUTH_TOKEN) authToken: String,
        @Body matterRequest: MatterRequest
    ): Response<List<InvoiceResponse>>

    @GET(APIConstant.STATUS)
    suspend fun getStatus(
        @Query(Constants.AUTH_TOKEN) authToken: String
    ): Response<List<StatusResponse>>

    @GET(APIConstant.PAYMENT_PLAN)
    suspend fun getPaymentPlan(
        @Query(Constants.AUTH_TOKEN) authToken: String
    ): Response<List<PaymentPlanResponse>>

    @GET()
    suspend fun getPaymentPlanDetails(
        @Url url: String,
        @Query(Constants.AUTH_TOKEN) authToken: String,
        @Query(Constants.PAYMENT_PLAN_REVISION_NO) paymentPlanRevisionNo: Int
    ): Response<PaymentPlanResponse>

    @POST(APIConstant.CHECK_LIST)
    suspend fun getCheckList(
        @Query(Constants.AUTH_TOKEN) authToken: String,
        @Body matterRequest: MatterRequest
    ): Response<List<CheckListResponse>>
    @POST(APIConstant.CHECK_LIST)
    suspend fun getCheckListViewDetails(
        @Query(Constants.AUTH_TOKEN) authToken: String,
        @Body checkListViewDetailsRequest: CheckListViewDetailsRequest
    ): Response<List<CheckListResponse>>

    @GET(APIConstant.RECEIPT_NO)
    suspend fun getReceiptNo(
        @Query(Constants.AUTH_TOKEN) authToken: String
    ): Response<List<ReceiptNoResponse>>

    @POST(APIConstant.UPLOADED_DOCUMENT)
    suspend fun getUploadedDocument(
        @Query(Constants.AUTH_TOKEN) authToken: String,
        @Body matterRequest: MatterRequest
    ): Response<List<DocumentUploadedResponse>>

    @POST(APIConstant.MATTER_ID)
    suspend fun getMatterId(
        @Query(Constants.AUTH_TOKEN) authToken: String,
        @Body matterRequest: MatterRequest
    ): Response<List<MatterIdResponse>>

    @POST(APIConstant.DOCUMENT_UPLOAD)
    suspend fun documentUpload(
        @Query(Constants.AUTH_TOKEN) authToken: String,
        @Query(Constants.LOCATION) location: String,
        @Query(Constants.CLASS_ID) classId: Int,
        @Body requestBody: RequestBody
    ): Response<UploadResponse>
    @POST(APIConstant.DOCUMENT_UPLOAD)
    suspend fun checkListUpload(
        @Query(Constants.LOCATION) location: String,
        @Body requestBody: RequestBody
    ): Response<UploadResponse>

    @POST(APIConstant.DOCUMENT_UPLOAD_UPDATE)
    suspend fun documentUploadUpdate(
        @Query(Constants.AUTH_TOKEN) authToken: String,
        @Query(Constants.PARAM_LOGIN_USER_ID) loginUserID: String,
        @Body request: DocumentUploadUpdateRequest
    ): Response<DocumentUploadUpdateResponse>

    @PATCH()
    suspend fun checkListDocumentUploadUpdate(
        @Url url: String,
        @Query(Constants.AUTH_TOKEN) authToken: String,
        @Query(Constants.PARAM_LOGIN_USER_ID) loginUserID: String,
        @Body request: CheckListDocumentUploadUpdateRequest
    ): Response<DocumentUploadUpdateResponse>

    @GET()
    suspend fun getUploadDocumentMatterDetails(
        @Url url: String,
        @Query(Constants.AUTH_TOKEN) authToken: String,
    ): Response<MatterDetails>

    @GET(APIConstant.CHECKLIST_DOCUMENT_SUBMIT)
    suspend fun checkListDocumentSubmit(
        @Path(Constants.MATTER_NO) matterNo: String,
        @Query(Constants.AUTH_TOKEN) authToken: String,
        @Query(Constants.QUERY_PARAM_CHECK_LIST_NO) checkListNo: Number,
        @Query(Constants.QUERY_PARAM_CLIENT_ID) clientId: String,
        @Query(Constants.PARAM_LOGIN_USER_ID) loginUserID: String,
        @Query(Constants.QUERY_PARAM_MATTER_HEADER_ID) matterHeaderId: Number,
    ): Response<CheckListDocumentSubmitResponse>

    @POST(APIConstant.CREATE_PUSH_NOTIFICATION)
    suspend fun createPushNotification(
        @Query(Constants.AUTH_TOKEN) authToken: String,
        @Query(Constants.PARAM_LOGIN_USER_ID) loginUserID: String,
        @Body request: PushNotificationRequest
    ): Response<PushNotificationResponse>

    @POST(APIConstant.NOTIFICATION_MESSAGE)
    suspend fun getNotification(
        @Query(Constants.AUTH_TOKEN) authToken: String,
        @Body request: NotificationRequest
    ): Response<List<NotificationResponse>>

    @GET(APIConstant.NOTIFICATION_COUNT)
    suspend fun getNotificationCount(
        @Path(Constants.QUERY_PARAM_CLIENT_ID) clientId: String,
        @Query(Constants.AUTH_TOKEN) authToken: String,
    ): Response<NotificationCount>

    @PATCH(APIConstant.NOTIFICATION_MESSAGE_UPDATE)
    suspend fun notificationUpdate(
        @Query(Constants.AUTH_TOKEN) authToken: String,
        @Query(Constants.PARAM_LOGIN_USER_ID) loginUserID: String,
        @Body request: List<NotificationResponse>
    ): Response<List<NotificationResponse>>
}