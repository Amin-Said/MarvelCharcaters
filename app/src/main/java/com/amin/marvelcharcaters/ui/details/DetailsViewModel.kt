package com.amin.marvelcharcaters.ui.details

import android.accounts.NetworkErrorException
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.amin.marvelcharcaters.model.details.ResourceResponse
import com.amin.marvelcharcaters.repository.DetailsRepository
import com.amin.marvelcharcaters.utils.data.ApiResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber

class DetailsViewModel @ViewModelInject constructor(
    private val repository: DetailsRepository
) : ViewModel() {
    var result: LiveData<ApiResult<ResourceResponse>> = MutableLiveData()

    var isNetworkError = MutableLiveData(false)

    fun fetchResourceData(url: String, key: String, hash: String, timestamp: String) {
        viewModelScope.launch {
            try {
                result = repository.fetchResourceData(url, key, hash, timestamp)
                    .asLiveData(viewModelScope.coroutineContext + Dispatchers.Default)
            } catch (e: NetworkErrorException) {
                isNetworkError.value = true
                Timber.e(e)
            }
        }
    }


}