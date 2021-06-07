package com.amin.marvelcharcaters.api

import com.amin.marvelcharcaters.MainCoroutineRule
import com.amin.marvelcharcaters.MockData
import com.amin.marvelcharcaters.utils.ApiAbstract
import io.mockk.MockKAnnotations
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.io.IOException

class ApiServiceTest: ApiAbstract<ApiService>(){
    private lateinit var service: ApiService
    @MockK
    private lateinit var client: ApiClient

    @ExperimentalCoroutinesApi
    @get:Rule
    var coroutineRule = MainCoroutineRule()

    @Before
    fun initService(){
        MockKAnnotations.init(this, relaxed = true)
        service = createService(ApiService::class.java)
        client = ApiClient(service)
    }


    @Throws(IOException::class)
    @Test
    fun fetchAllCharactersTest(): Unit = runBlocking {
        enqueueResponse("/character.json")
        val response = service.fetchAllCharacters(
            MockData.key,
            MockData.hash,
            MockData.timestamp,
            MockData.page
        )
        mockWebServer.takeRequest()

        client.fetchAllCharacters(MockData.key, MockData.hash, MockData.timestamp, MockData.page)

        response.body()?.data?.let { data ->
            MatcherAssert.assertThat(data.results.size, CoreMatchers.`is`(20))
            MatcherAssert.assertThat(data.results[0].name, CoreMatchers.`is`("3-D Man"))
        }
    }

    @Throws(IOException::class)
    @Test
    fun fetchCharactersDataForSearchTest(): Unit = runBlocking {
        enqueueResponse("/character.json")
        val response = service.fetchCharactersDataForSearch(
            MockData.key,
            MockData.hash,
            MockData.timestamp,
            MockData.query
        )
        mockWebServer.takeRequest()

        client.fetchCharactersDataForSearch(
            MockData.key,
            MockData.hash,
            MockData.timestamp,
            MockData.query
        )

        MatcherAssert.assertThat(20, CoreMatchers.`is`(25))

        response.body()?.data?.let { data ->
            MatcherAssert.assertThat(data.results.size, CoreMatchers.`is`(20))
            MatcherAssert.assertThat(data.results[0].name, CoreMatchers.`is`("3-D Man"))
        }
    }

    @Throws(IOException::class)
    @Test
    fun fetchResourceDataTest(): Unit = runBlocking {
        enqueueResponse("/comic.json")
        val response = service.fetchResourceData(
            MockData.resourceURL,
            MockData.key,
            MockData.hash,
            MockData.timestamp
        )
        mockWebServer.takeRequest()

        client.fetchResourceData(
            MockData.resourceURL,
            MockData.key,
            MockData.hash,
            MockData.timestamp
        )

        MatcherAssert.assertThat(20, CoreMatchers.`is`(25))

        response.body()?.data?.let { data ->
            MatcherAssert.assertThat(data.results.size, CoreMatchers.`is`(1))
            MatcherAssert.assertThat(data.results[0].title, CoreMatchers.`is`("Hulk (2008) #53"))
        }
    }

}