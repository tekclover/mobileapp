package com.tvhht.myapplication.api.error

import android.content.Context
import android.text.TextUtils
import android.util.Log
import com.tvhht.myapplication.R
import com.tvhht.myapplication.utils.WMSApplication


class NetworkState {
    //     TODO: Change this to getMessage, when RestAPIBuilder starts giving appropriate error message.
    var message: String? = null
    var descText1: String? = null
    var headerTxt1: String? = null
    var btnText1: String? = null
    var status: STATUS
    var imageResourceId = 0
        private set
    var errorCode = 0
    var stateType = 0
    var requestCode = 0
    private var mErrorResponse: ErrorResponse? = null

    constructor(status: STATUS, message: String?) {
        this.status = status
        this.message = message
        setFieldsForState(status)
        if (status == STATUS.START_ERROR) Log.d("NetworkState WMS", "OnError")
    }

    constructor(status: STATUS) {
        this.status = status
        setFieldsForState(status)
    }

    constructor(status: STATUS, descText: String, headerText: String, imgResId: Int) {
        this.status = status
        setFields(descText, headerText, imgResId)
    }

    constructor(
        status: STATUS,
        descText: String,
        headerText: String?,
        btnTxt: String?,
        imgResId: Int
    ) {
        this.status = status
        setFields(descText, headerText, btnTxt, imgResId)
    }

    private fun setFieldsForState(status: STATUS) {
        val context: Context = WMSApplication.getInstance().context
        when (status) {
            STATUS.INTERNET_ERROR -> setFields(
                context.getString(R.string.no_internet_connection_msg),
                context.getString(R.string.no_internet_connection_msg_header),
                context.getString(R.string.retry), R.drawable.ic_internet
            )
            STATUS.START_ERROR, STATUS.ERROR -> setFields(
                context.getString(R.string.api_error_msg),
                context.getString(R.string.api_error_header_msg),
                context.getString(R.string.retry), R.drawable.communication_error
            )
            STATUS.NO_DATA -> {
                var headerText = message
                if (TextUtils.isEmpty(headerText)) {
                    headerText = context.getString(R.string.no_data_available)
                }
                setFields(
                    "",
                    headerText, null, R.drawable.ic_no_data
                )
            }

        }
    }

    private fun setFields(descText: String, headerText: String?, btnText: String?, imgResId: Int) {
        descText1 = descText
        headerTxt1 = headerText
        btnText1 = btnText
        setImageRosourceId(imgResId)
    }

    private fun setFields(descText: String, headerText: String, imgResId: Int) {
        descText1 = descText
        headerTxt1 = headerText
        setImageRosourceId(imgResId)
    }

    var errorResponse: ErrorResponse?
        get() = mErrorResponse
        set(errorResponse) {
            mErrorResponse = errorResponse
        }

    enum class STATUS {
        START, LOADING, LOADED, ERROR, START_ERROR, NO_DATA, INTERNET_ERROR, AUTH_ERROR, NO_FILTERED_DATA, NO_SEARCH_RESULT, NO_CONTACTS_AVAILABLE
    }

    fun setImageRosourceId(imageRosource: Int) {
        imageResourceId = imageRosource
    }
}