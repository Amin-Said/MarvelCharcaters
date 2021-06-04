package com.amin.marvelcharcaters.repository

import com.amin.marvelcharcaters.api.ApiClient
import com.amin.marvelcharcaters.di.IODispatcher
import com.amin.marvelcharcaters.model.details.ComicResourceResponse
import com.amin.marvelcharcaters.utils.data.ApiResult
import com.amin.marvelcharcaters.utils.data.DataSource
import com.amin.marvelcharcaters.utils.extensions.getResult
import com.amin.marvelcharcaters.utils.extensions.isSuccessAndNotNull
import com.amin.marvelcharcaters.utils.extensions.letOnFalseOnSuspend
import com.amin.marvelcharcaters.utils.extensions.letOnTrueOnSuspend
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import timber.log.Timber
import javax.inject.Inject

private const val FAKE_DELAY_TIME = 1500L

class DetailsRepository @Inject constructor(
    private val apiClient: ApiClient,
    @IODispatcher private val ioDispatcher: CoroutineDispatcher
) : DataSource(), DetailsRepositoryImpl {

    override fun fetchResourceData(
        url: String, key: String, hash: String, timestamp: String
    ): Flow<ApiResult<ComicResourceResponse>> = flow {
        emit(ApiResult.Loading)
        networkCall {
            apiClient.fetchResourceData(url, key, hash, timestamp)
        }.let {
            it.isSuccessAndNotNull().letOnTrueOnSuspend {
                Timber.d("fetchResourceData apiResult : ${(it.getResult() as ComicResourceResponse).data}")
                val response = (it.getResult() as ComicResourceResponse)
                emit(ApiResult.Success(response))
            }.letOnFalseOnSuspend {
                /* fake call */
                delay(FAKE_DELAY_TIME)
                emit(ApiResult.Error(Exception("Unexpected error.")))
            }
        }
    }.flowOn(ioDispatcher)

}


interface DetailsRepositoryImpl {
    fun fetchResourceData(
        key: String,
        hash: String,
        timestamp: String,
        page: String
    ): Flow<ApiResult<ComicResourceResponse>>


}