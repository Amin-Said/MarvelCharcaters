package com.amin.marvelcharcaters

import com.amin.marvelcharcaters.api.ApiServiceTest
import com.amin.marvelcharcaters.repository.CharactersRepositoryTest
import com.amin.marvelcharcaters.repository.DetailsRepositoryTest
import com.amin.marvelcharcaters.ui.details.DetailsViewModelTest
import com.amin.marvelcharcaters.ui.home.HomeListViewModelTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.runner.RunWith
import org.junit.runners.Suite

@ExperimentalCoroutinesApi
@RunWith(Suite::class)
@Suite.SuiteClasses(
    ApiServiceTest::class,
    CharactersRepositoryTest::class,
    HomeListViewModelTest::class,
    DetailsRepositoryTest::class,
    DetailsViewModelTest::class
)
class SuiteClass