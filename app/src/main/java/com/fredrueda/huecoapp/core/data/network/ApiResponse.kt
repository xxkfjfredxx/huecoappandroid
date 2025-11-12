package com.fredrueda.huecoapp.core.data.network

sealed class ApiResponse<out T> {
    data class Success<T>(val data: T): ApiResponse<T>()
    data class HttpError(val code: Int, val message: String? = null): ApiResponse<Nothing>()
    data class NetworkError(val throwable: Throwable): ApiResponse<Nothing>()
}