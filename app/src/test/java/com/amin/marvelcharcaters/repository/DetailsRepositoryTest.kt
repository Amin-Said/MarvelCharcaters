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

@ExperimentalCoroutinesApi
class DetailsRepositoryTest{

    private lateinit var repository: DetailsRepository
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
        repository = DetailsRepository(client, Dispatchers.IO)
    }

    @ExperimentalTime
    @Test
    fun fetchResourceData() = runBlocking {
        val mockData = MockData.resourceResponse
        val returnData = Response.success(mockData)
        whenever(service.fetchResourceData(
            MockData.resourceURL,
            MockData.key,
            MockData.hash,
            MockData.timestamp)).thenReturn(returnData)

        repository.fetchResourceData( MockData.resourceURL,MockData.key, MockData.hash, MockData.timestamp).test {
            val result = expectItem()
            assertEquals(result, ApiResult.Success(listOf(MockData.resourceData)))
            assertEquals((result as ApiResult.Success<CharacterResponse>).data.data.results[0].name, "name")
            assertEquals(result.data.data.results[0].id, "20")
            expectComplete()
        }

        verify(service, atLeastOnce()).fetchResourceData(
            MockData.resourceURL,
            MockData.key,
            MockData.hash,
            MockData.timestamp)
        verifyNoMoreInteractions(service)
    }

}