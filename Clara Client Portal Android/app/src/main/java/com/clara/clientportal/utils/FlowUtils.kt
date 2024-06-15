package com.clara.clientportal.utils

import com.clara.clientportal.network.NetworkResult
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
                if (response.isSuccessful && response.body() != null) {
                    emit(NetworkResult.Success(response.body(), code))
                } else {
                    emit(NetworkResult.Failure(response.body(), code))
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