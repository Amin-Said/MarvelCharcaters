package com.amin.marvelcharcaters.api

import com.amin.marvelcharcaters.model.CharacterResponse
import com.amin.marvelcharcaters.model.details.ResourceResponse
import com.amin.marvelcharcaters.utils.Config
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query
import retrofit2.http.Url

interface ApiService {
    @GET(Config.BASE_RETURN)
    @Headers("Content-Type: application/json")
    suspend fun fetchCharactersDataForSearch(
        @Query(Config.KEY_PARAM) apiKey: String,
        @Query(Config.HASH_PARAM) hash: String,
        @Query(Config.TIMESTAMP_PARRAM) timestamp: String,
        @Query(Config.SEARCH_PARAM) searchQuery: String
    ): Response<CharacterResponse>

    @GET(Config.BASE_RETURN)
    @Headers("Content-Type: application/json")
    suspend fun fetchAllCharacters(
        @Query(Config.KEY_PARAM) apiKey: String,
        @Query(Config.HASH_PARAM) hash: String,
        @Query(Config.TIMESTAMP_PARRAM) timestamp: String,
        @Query(Config.PAGE_PARAM) page: String
    ): Response<CharacterResponse>

    @GET
    @Headers("Content-Type: application/json")
    suspend fun fetchResourceData(
        @Url url: String,
        @Query(Config.KEY_PARAM) apiKey: String,
        @Query(Config.HASH_PARAM) hash: String,
        @Query(Config.TIMESTAMP_PARRAM) timestamp: String
    ): Response<ResourceResponse>

}