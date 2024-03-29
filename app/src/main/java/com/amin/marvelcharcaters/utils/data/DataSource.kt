package com.amin.marvelcharcaters.utils.data

import com.amin.marvelcharcaters.utils.extensions.isSuccessAndNotNull
import com.amin.marvelcharcaters.utils.extensions.letOnFalse
import com.amin.marvelcharcaters.utils.extensions.letOnTrue
import retrofit2.Response
import java.io.IOException

open class DataSource {

    suspend fun <T :Any> networkCall(
        call: suspend () -> Response<T>
    ) : ApiResult<T?> {
        var networkReturn: ApiResult<T?> = ApiResult.Loading
        try {
            val response = call.invoke()
            response.isSuccessAndNotNull().letOnTrue{
                networkReturn = ApiResult.Success(response.body())
            }.letOnFalse{
                networkReturn =
                    ApiResult.Error(IOException(response.errorBody()?.string().orEmpty()))
            }
        } catch (e: IllegalArgumentException) {
            networkReturn = ApiResult.Error(e)
        }
        return networkReturn
    }
}