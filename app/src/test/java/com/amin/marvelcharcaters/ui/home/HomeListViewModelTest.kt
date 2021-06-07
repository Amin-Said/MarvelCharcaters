package com.amin.marvelcharcaters.ui.home

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.asLiveData
import com.amin.marvelcharcaters.MainCoroutineRule
import com.amin.marvelcharcaters.MockData
import com.amin.marvelcharcaters.api.ApiClient
import com.amin.marvelcharcaters.api.ApiService
import com.amin.marvelcharcaters.model.CharacterResponse
import com.amin.marvelcharcaters.repository.CharactersRepository
import com.amin.marvelcharcaters.utils.data.ApiResult
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import io.mockk.MockKAnnotations
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class HomeListViewModelTest {
    private lateinit var viewModel: HomeListViewModel
    private lateinit var repository: CharactersRepository


    private lateinit var apiClient : ApiClient

    @MockK
    private lateinit var apiService: ApiService


    @ExperimentalCoroutinesApi
    @get:Rule
    var coroutineRule = MainCoroutineRule()

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @ExperimentalCoroutinesApi
    @Before
    fun setup(){
        MockKAnnotations.init(this, relaxed = true)
        apiClient = ApiClient(apiService)
        repository = CharactersRepository(apiClient, Dispatchers.IO)
        viewModel = HomeListViewModel(repository)
    }

    @Test
    fun fetchAllCharactersTest() = runBlocking {
        val mockResponse = MockData.characterResponse

        val observer: Observer<ApiResult<CharacterResponse>> = mock()
        val fetchedData: LiveData<ApiResult<CharacterResponse>> = repository.fetchAllCharacters(
            MockData.key,
            MockData.hash,
            MockData.timestamp,
            MockData.page
        ).asLiveData()
        fetchedData.observeForever(observer)

        viewModel.fetchAllCharacters(MockData.key,MockData.hash,MockData.timestamp,MockData.page)
        delay(500L)

        verify(observer).onChanged(ApiResult.Success(mockResponse))
        fetchedData.removeObserver(observer)
    }

    @Test
    fun fetchCharactersDataForSearchTest() = runBlocking {
        val mockResponse = MockData.characterResponse

        val observer: Observer<ApiResult<CharacterResponse>> = mock()
        val fetchedData: LiveData<ApiResult<CharacterResponse>> = repository.fetchCharactersDataForSearch(
            MockData.key,
            MockData.hash,
            MockData.timestamp,
            MockData.query
        ).asLiveData()
        fetchedData.observeForever(observer)

        viewModel.fetchCharactersDataForSearch(MockData.key,MockData.hash,MockData.timestamp,MockData.query)
        delay(500L)

        verify(observer).onChanged(ApiResult.Success(mockResponse))
        fetchedData.removeObserver(observer)
    }
}