package com.amin.marvelcharcaters.utils.data


sealed class ApiResult<out R>{
    data class Success<out T>(val data:T) : ApiResult<T>()
    object Loading : ApiResult<Nothing>()
    data class Error(val exception: Exception) : ApiResult<Nothing>()
}