package com.amin.marvelcharcaters.utils.extensions

import com.amin.marvelcharcaters.utils.data.ApiResult
import retrofit2.Response

fun Response<*>?.isSuccessAndNotNull():Boolean = this?.let {
    it.body() != null && it.isSuccessful
} ?: run {
    false
}

fun ApiResult<*>?.isSuccessAndNotNull() =
    this is ApiResult.Success && this.data != null


fun ApiResult<*>?.getResult(): Any? =  when(this) {
    is ApiResult.Success -> this.data
    else -> null
}

