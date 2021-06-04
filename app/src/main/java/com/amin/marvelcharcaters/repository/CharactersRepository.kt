package com.amin.marvelcharcaters.repository

import com.amin.marvelcharcaters.model.CharacterResponse
import com.amin.marvelcharcaters.di.IODispatcher
import com.amin.marvelcharcaters.api.ApiClient
import com.amin.marvelcharcaters.utils.data.ApiResult
import com.amin.marvelcharcaters.utils.data.DataSource
import com.amin.marvelcharcaters.utils.extensions.getResult
import com.amin.marvelcharcaters.utils.extensions.isSuccessAndNotNull
import com.amin.marvelcharcaters.utils.extensions.letOnFalseOnSuspend
import com.amin.marvelcharcaters.utils.extensions.letOnTrueOnSuspend
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import timber.log.Timber
import javax.inject.Inject

private const val FAKE_DELAY_TIME = 1500L

class CharactersRepository @Inject constructor(
    private val apiClient: ApiClient,
    @IODispatcher private val ioDispatcher: CoroutineDispatcher
) : DataSource(), CharactersRepositoryImpl {


    override fun fetchAllCharacters(
        key: String,
        hash: String,
        timestamp: String,
        page: String
    ): Flow<ApiResult<CharacterResponse>> = flow {
        emit(ApiResult.Loading)
        networkCall {
            apiClient.fetchAllCharacters(key, hash, timestamp, page)
        }.let {
            it.isSuccessAndNotNull().letOnTrueOnSuspend {
                Timber.d("fetchAllCharacters apiResult : ${(it.getResult() as CharacterResponse).data}")
                val response = (it.getResult() as CharacterResponse)
                emit(ApiResult.Success(response))
            }.letOnFalseOnSuspend {
                /* fake call */
                //delay(FAKE_DELAY_TIME)
                emit(ApiResult.Error(Exception("Unexpected error.")))
            }
        }
    }.flowOn(ioDispatcher)

    override fun fetchCharactersDataForSearch(
        key: String,
        hash: String,
        timestamp: String,
        query: String
    ): Flow<ApiResult<CharacterResponse>> = flow {
        emit(ApiResult.Loading)
        networkCall {
            apiClient.fetchCharactersDataForSearch(key, hash, timestamp, query)
        }.let {
            it.isSuccessAndNotNull().letOnTrueOnSuspend {
                Timber.d("fetchCharactersSearch apiResult : ${(it.getResult() as CharacterResponse).data}")
                val response = (it.getResult() as CharacterResponse)
                emit(ApiResult.Success(response))
            }.letOnFalseOnSuspend {
                /* fake call */
                //delay(FAKE_DELAY_TIME)
                emit(ApiResult.Error(Exception("Unexpected error.")))
            }
        }
    }.flowOn(ioDispatcher)
}


interface CharactersRepositoryImpl {
    fun fetchAllCharacters(
        key: String,
        hash: String,
        timestamp: String,
        page: String
    ): Flow<ApiResult<CharacterResponse>>

    fun fetchCharactersDataForSearch(
        key: String,
        hash: String,
        timestamp: String,
        query: String
    ): Flow<ApiResult<CharacterResponse>>

}