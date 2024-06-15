package com.tvhht.myapplication.api.error

import android.content.Context
import com.tvhht.myapplication.utils.WMSApplication
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.adapter.rxjava.HttpException
import java.io.IOException
import java.net.HttpURLConnection


class NetworkError(val error: Throwable?) : Throwable(error) {
    var errorCode = 0
        private set
    val errorMessage: String? = null
    private val context: Context? = null
    var requestCode = 0
    override val message: String
        get() = error!!.message!!
    val isAuthFailure: Boolean
        get() = error is HttpException && error.code() == HttpURLConnection.HTTP_UNAUTHORIZED
    val isResponseNull: Boolean
        get() = error is HttpException && error.response() == null

    fun displayAppErrorMessage(): NetworkState? {
        var mstate: NetworkState? = null
        mstate = displayAppErrorMessage(true)
        return mstate
    }

    //TODO check onApiError callback on paging scenario.
    fun displayAppErrorMessage(isLogoutRequired: Boolean): NetworkState? {
        var mstate: NetworkState? = null
        var msg = DEFAULT_ERROR_MESSAGE
        errorCode = 0
        if (WMSApplication.getInstance() != null && WMSApplication.getInstance()
                .isInternetConnected()
        ) {
            if (error is retrofit2.HttpException) {
                errorCode = error.code()
                msg = when (errorCode) {
                    204 -> NO_CONTENT
                    304 -> NOT_MODIFIED
                    400 -> BAD_REQUEST
                    401 -> UNAUTHORIZED
                    403 -> FORBIDDEN
                    404 -> API_COMMUNICATION_ERROR
                    415 -> UNSUPPORTED_MEDIA_TYPE
                    500 -> API_COMMUNICATION_ERROR
                    else -> DEFAULT_ERROR_MESSAGE
                }
                mstate = NetworkState(NetworkState.STATUS.ERROR, msg)
            } else if (error is IOException) {
                msg = API_COMMUNICATION_ERROR
                mstate = NetworkState(NetworkState.STATUS.ERROR, msg)
            }
            //in case no internet connection no need for a callback to show snackbar.
            if (mstate == null) mstate = NetworkState(NetworkState.STATUS.ERROR)
        } else {
            mstate = NetworkState(NetworkState.STATUS.INTERNET_ERROR, USER_OFFLINE)
        }
        return mstate
    }

    override fun equals(o: Any?): Boolean {
        if (this === o) return true
        if (o == null || javaClass != o.javaClass) return false
        val that = o as NetworkError
        return if (error != null) error == that.error else that.error == null
    }

    override fun hashCode(): Int {
        return error?.hashCode() ?: 0
    }

    fun getSpecificErrorCode(responseBody: ResponseBody, tagName: String?): Int {
        return try {
            val jsonObject = JSONObject(responseBody.string())
            jsonObject.getInt(tagName)
        } catch (e: Exception) {
            0
        }
    }

    companion object {
        const val DEFAULT_ERROR_MESSAGE = "Sorry, we are unable to load your information."
        private const val NETWORK_ERROR_MESSAGE = "No Internet Connection"
        const val USER_OFFLINE = "Device is offline"
        private const val ERROR_MESSAGE_HEADER = "Error-Message"
        private const val API_COMMUNICATION_ERROR = "Sorry, we are unable to load your information."
        private const val NO_CONTENT = "No content"
        private const val NOT_MODIFIED = "Not modified"
        private const val BAD_REQUEST = "Bad request"
        private const val UNAUTHORIZED = "Unauthorized"
        private const val FORBIDDEN = "Forbidden"
        private const val UNSUPPORTED_MEDIA_TYPE = "Unsupported media type"
    }

    init {
        displayAppErrorMessage(true)
    }
}