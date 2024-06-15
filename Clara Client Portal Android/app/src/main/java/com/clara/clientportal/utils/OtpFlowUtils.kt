package com.clara.clientportal.utils

import com.clara.clientportal.model.ErrorResponse
import com.clara.clientportal.network.NetworkResult
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import retrofit2.Response

fun <ResultType> apiCalls(
    code: Int = 0,
    isNetworkConnected: Boolean = false,
    fetch: suspend () -> Response<ResultType>
) = flow {
    if (isNetworkConnected) {
        val flow = flow {
            try {
                val response = fetch()
                if (response.isSuccessful && response.body() != null) {
                    emit(NetworkResult.Success(response.body(), code))
                } else {
                    val type = object : TypeToken<ErrorResponse>() {}.type
                    response.errorBody()?.charStream()?.let {
                        val errorResponse: ErrorResponse? = Gson().fromJson(it, type)
                        emit(NetworkResult.Failure(errorResponse, code))
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