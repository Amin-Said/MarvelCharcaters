package com.amin.marvelcharcaters.ui.details

import android.accounts.NetworkErrorException
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.amin.marvelcharcaters.model.BaseResponse
import com.amin.marvelcharcaters.model.comicresource.ComicResourceResponse
import com.amin.marvelcharcaters.repository.DetailsRepository
import com.amin.marvelcharcaters.utils.data.ApiResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber

class DetailsViewModel @ViewModelInject constructor(
    private val repository: DetailsRepository
) : ViewModel() {

    var result: LiveData<ApiResult<BaseResponse>> = MutableLiveData()
    var resultComics: LiveData<ApiResult<ComicResourceResponse>> = MutableLiveData()

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

    fun fetchComicResourceData(url: String, key: String, hash: String, timestamp: String) {
        viewModelScope.launch {
            try {
                resultComics = repository.fetchComicResourceData(url, key, hash, timestamp)
                    .asLiveData(viewModelScope.coroutineContext + Dispatchers.Default)
            } catch (e: NetworkErrorException) {
                isNetworkError.value = true
                Timber.e(e)
            }
        }
    }


}