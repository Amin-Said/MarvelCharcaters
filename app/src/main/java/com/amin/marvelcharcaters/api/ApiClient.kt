package com.amin.marvelcharcaters.api

import javax.inject.Inject

class ApiClient @Inject constructor(
    private val apiService: ApiService
) {
    suspend fun fetchCharactersDataForSearch(key: String, hash: String,timestamp: String,query: String) =
        apiService.fetchCharactersDataForSearch(key, hash,timestamp,query)

    suspend fun fetchAllCharacters(key: String, hash: String,timestamp: String,page: String) =
        apiService.fetchAllCharacters(key, hash,timestamp,page)
}