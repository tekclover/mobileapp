package com.clara.clientportal.network

sealed class NetworkResult<out R> {
    data class Success<out T>(val result: T, val id: Int = 0) : NetworkResult<T>()
    data class Failure<out T>(val result: T, val id: Int = 0) : NetworkResult<T>()
    data class Error(val exception: Exception, val id: Int? = 0) : NetworkResult<Nothing>()
    object NoNetwork : NetworkResult<Nothing>()
}