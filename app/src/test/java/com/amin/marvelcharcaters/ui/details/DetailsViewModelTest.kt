package com.amin.marvelcharcaters.ui.details

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.asLiveData
import com.amin.marvelcharcaters.MainCoroutineRule
import com.amin.marvelcharcaters.MockData
import com.amin.marvelcharcaters.api.ApiClient
import com.amin.marvelcharcaters.api.ApiService
import com.amin.marvelcharcaters.model.details.ResourceResponse
import com.amin.marvelcharcaters.repository.DetailsRepository
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
class DetailsViewModelTest {
    private lateinit var viewModel: DetailsViewModel
    private lateinit var repository: DetailsRepository


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
        repository = DetailsRepository(apiClient, Dispatchers.IO)
        viewModel = DetailsViewModel(repository)
    }
    @Test
    fun fetchResourceDataTest() = runBlocking {
        val mockResponse = MockData.resourceResponse

        val observer: Observer<ApiResult<ResourceResponse>> = mock()
        val fetchedData: LiveData<ApiResult<ResourceResponse>> = repository.fetchResourceData(
            MockData.resourceURL,
            MockData.key,
            MockData.hash,
            MockData.timestamp
        ).asLiveData()
        fetchedData.observeForever(observer)

        viewModel.fetchResourceData(
            MockData.resourceURL,
            MockData.key,
            MockData.hash,
            MockData.timestamp
        )
        delay(500L)

        verify(observer).onChanged(ApiResult.Success(mockResponse))
        fetchedData.removeObserver(observer)
    }


}