package com.clara.timekeeping.network

sealed class NetworkResult<out R> {
    data class Success<out T>(val result: T, val id: Int = 0) : NetworkResult<T>()
    data class Failure<out T>(val result: T, val id: Int = 0) : NetworkResult<T>()
    data class Error<out T>(val exception: T, val id: Int? = 0) : NetworkResult<T>()
    object NoNetwork : NetworkResult<Nothing>()
}