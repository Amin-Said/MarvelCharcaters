package com.amin.marvelcharcaters.di

import com.amin.marvelcharcaters.api.ApiClient
import com.amin.marvelcharcaters.repository.CharactersRepository
import com.amin.marvelcharcaters.repository.DetailsRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun providesCharactersRepository(
        apiClient: ApiClient,
        @IODispatcher dispatcher: CoroutineDispatcher
    ) = CharactersRepository(apiClient, dispatcher)

    @Provides
    @Singleton
    fun providesDetailsRepository(
        apiClient: ApiClient,
        @IODispatcher dispatcher: CoroutineDispatcher
    ) = DetailsRepository(apiClient, dispatcher)

}