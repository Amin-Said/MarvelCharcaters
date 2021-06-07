package com.amin.marvelcharcaters.repository

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import app.cash.turbine.test
import com.amin.marvelcharcaters.MainCoroutineRule
import com.amin.marvelcharcaters.MockData
import com.amin.marvelcharcaters.api.ApiClient
import com.amin.marvelcharcaters.api.ApiService
import com.amin.marvelcharcaters.model.CharacterResponse
import com.amin.marvelcharcaters.utils.data.ApiResult
import com.nhaarman.mockitokotlin2.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito
import retrofit2.Response
import kotlin.time.ExperimentalTime

class CharactersRepositoryTest {

    private lateinit var repository: CharactersRepository
    private lateinit var client: ApiClient
    private val service: ApiService = mock()

    @ExperimentalCoroutinesApi
    @get:Rule
    var coroutinesRule = MainCoroutineRule()

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @ExperimentalCoroutinesApi
    @Before
    fun setup() {
        client = ApiClient(service)
        repository = CharactersRepository(client, Dispatchers.IO)
    }

    @ExperimentalTime
    @Test
    fun fetchAllCharactersTest() = runBlocking {
        val mockData = MockData.characterResponse
        val returnData = Response.success(mockData)
        whenever(service.fetchAllCharacters(MockData.key,MockData.hash,MockData.timestamp,MockData.page)).thenReturn(returnData)

        repository.fetchAllCharacters(MockData.key,MockData.hash,MockData.timestamp,MockData.page).test {
            val result = expectItem()
            assertEquals(result, ApiResult.Success(listOf(MockData.data)))
            assertEquals((result as ApiResult.Success<CharacterResponse>).data.data.results[0].name, "Spider Man")
            assertEquals(result.data.data.results[0].id, "24")
            expectComplete()
        }

        verify(service, atLeastOnce()).fetchAllCharacters(MockData.key,MockData.hash,MockData.timestamp,MockData.page)
        verifyNoMoreInteractions(service)
    }

    @ExperimentalTime
    @Test
    fun fetchCharactersDataForSearchTest() = runBlocking {
        val mockData = MockData.characterResponse
        val returnData = Response.success(mockData)
        Mockito.`when`(service.fetchCharactersDataForSearch(MockData.key,MockData.hash,MockData.timestamp,MockData.query))
            .thenReturn(returnData)

        repository.fetchCharactersDataForSearch(MockData.key,MockData.hash,MockData.timestamp,MockData.query).test {
            val result = expectItem()
            assertEquals(result, ApiResult.Success(listOf(MockData.data)))
            assertEquals((result as ApiResult.Success<CharacterResponse>).data.data.results[0].name, "Spider Man")
            assertEquals(result.data.data.results[0].id, "24")
            expectComplete()
        }

        verify(service, atLeastOnce()).fetchCharactersDataForSearch(MockData.key,MockData.hash,MockData.timestamp,MockData.query)
        verifyNoMoreInteractions(service)
    }
}