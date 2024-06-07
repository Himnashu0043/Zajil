package com.example.zajil.webservices

import okhttp3.ResponseBody

sealed class ApiResponse<T>
data class Success<T>(val response: T?) : ApiResponse<T>()

//data class Error<T>(val error: ErrorModel) : ApiResponse<T>()
data class Failure<T>(val error: ResponseBody?) : ApiResponse<T>()