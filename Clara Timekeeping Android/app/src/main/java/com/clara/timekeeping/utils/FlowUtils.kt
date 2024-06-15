package com.clara.timekeeping.utils

import com.clara.timekeeping.model.DeleteTicketResponse
import com.clara.timekeeping.model.ErrorResponse
import com.clara.timekeeping.network.APIConstant
import com.clara.timekeeping.network.NetworkResult
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import retrofit2.Response

fun <ResultType> apiCall(
    code: Int = 0,
    isNetworkConnected: Boolean = false,
    fetch: suspend () -> Response<ResultType>
) = flow {
    if (isNetworkConnected) {
        val flow = flow {
            try {
                val response = fetch()
                when {
                    response.isSuccessful && response.body() != null -> {
                        emit(NetworkResult.Success(response.body(), code))
                    }

                    response.isSuccessful && code == APIConstant.DELETE_TICKET_ID -> {
                        emit(NetworkResult.Success(DeleteTicketResponse(Constants.SUCCESS), code))
                    }

                    response.errorBody() != null -> {
                        val type = object : TypeToken<ErrorResponse>() {}.type
                        response.errorBody()?.charStream()?.let {
                            val errorResponse: ErrorResponse? = Gson().fromJson(it, type)
                            emit(NetworkResult.Failure(errorResponse, code))
                        }
                    }

                    else -> {
                        emit(NetworkResult.Failure(ErrorResponse(""), code))
                    }
                }
            } catch (e: Exception) {
                emit(NetworkResult.Error(e, code))
            }
        }
        emitAll(flow)
    } else {
        emit(NetworkResult.NoNetwork)
    }
}