package com.amin.marvelcharcaters.repository

import com.amin.marvelcharcaters.api.ApiClient
import com.amin.marvelcharcaters.di.IODispatcher
import com.amin.marvelcharcaters.model.details.ResourceResponse
import com.amin.marvelcharcaters.utils.data.ApiResult
import com.amin.marvelcharcaters.utils.data.DataSource
import com.amin.marvelcharcaters.utils.extensions.getResult
import com.amin.marvelcharcaters.utils.extensions.isSuccessAndNotNull
import com.amin.marvelcharcaters.utils.extensions.letOnFalseOnSuspend
import com.amin.marvelcharcaters.utils.extensions.letOnTrueOnSuspend
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import timber.log.Timber
import javax.inject.Inject

class DetailsRepository @Inject constructor(
    private val apiClient: ApiClient,
    @IODispatcher private val ioDispatcher: CoroutineDispatcher
) : DataSource(), DetailsRepositoryImpl {

    override fun fetchResourceData(
        url: String, key: String, hash: String, timestamp: String
    ): Flow<ApiResult<ResourceResponse>> = flow {
        emit(ApiResult.Loading)
        networkCall {
            apiClient.fetchResourceData(url, key, hash, timestamp)
        }.let {
            it.isSuccessAndNotNull().letOnTrueOnSuspend {
                Timber.d("fetchResourceData apiResult : ${(it.getResult() as ResourceResponse).data}")
                val response = (it.getResult() as ResourceResponse)
                emit(ApiResult.Success(response))
            }.letOnFalseOnSuspend {
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
    ): Flow<ApiResult<ResourceResponse>>


}