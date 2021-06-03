package com.amin.marvelcharcaters.ui.home

import android.accounts.NetworkErrorException
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.amin.marvelcharcaters.model.CharacterResponse
import com.amin.marvelcharcaters.repository.CharactersRepository
import com.amin.marvelcharcaters.utils.data.ApiResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber

class HomeListViewModel @ViewModelInject constructor(
    private val repository: CharactersRepository
): ViewModel(){

    var result: LiveData<ApiResult<CharacterResponse>> = MutableLiveData()
    var searchResult: LiveData<ApiResult<CharacterResponse>> = MutableLiveData()
    var isNetworkError = MutableLiveData(false)

    fun fetchAllCharacters(key:String,hash:String,timestamp:String,page:String){
        viewModelScope.launch {
            try{
                result = repository.fetchAllCharacters(key,hash,timestamp,page)
                    .asLiveData(viewModelScope.coroutineContext+ Dispatchers.Default)
            }catch (e: NetworkErrorException){
                isNetworkError.value = true
                Timber.e(e)
            }
        }
    }

    fun fetchCharactersDataForSearch(key:String,hash:String,timestamp:String,query:String){
        viewModelScope.launch {
            try{
                searchResult = repository.fetchCharactersDataForSearch(key,hash,timestamp,query)
                    .asLiveData(viewModelScope.coroutineContext+ Dispatchers.Default)
            }catch (e: NetworkErrorException){
                isNetworkError.value = true
                Timber.e(e)
            }
        }
    }

}