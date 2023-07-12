package com.hirno.museum.ui.main

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.hirno.museum.MainCoroutineRule
import com.hirno.museum.R
import com.hirno.museum.getOrAwaitValue
import com.hirno.museum.model.collection.CollectionItemModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.hamcrest.CoreMatchers.*
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Unit tests for the implementation of [MainViewModel]
 */
@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class MainViewModelTest {

    // Subject under test
    private lateinit var mainViewModel: MainViewModel

    // Use a fake repository to be injected into the view model
    private lateinit var collectionsRepository: FakeCollectionsRepository

    // Set the main coroutines dispatcher for unit testing.
    @ExperimentalCoroutinesApi
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    // Executes each task synchronously using Architecture Components.
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setupViewModel() {
        // Construct the fake repository
        collectionsRepository = FakeCollectionsRepository()

        // Add some dummy objects to the repository
        val collection1 = CollectionItemModel(
            id = "nl-SK-A-4691",
            objectNumber = "SK-A-4691",
            title = "Zelfportret",
            webImage = CollectionItemModel.WebImage(
                width = 2118,
                height = 2598,
                url = "https://lh3.googleusercontent.com/7qzT0pbclLB7y3fdS1GxzMnV7m3gD3gWnhlquhFaJSn6gNOvMmTUAX3wVlTzhMXIs8kM9IH8AsjHNVTs8em3XQI6uMY=s0",
            ),
        )
        val collection2 = CollectionItemModel(
            id = "nl-SK-A-4050",
            objectNumber = "SK-A-4050",
            title = "Zelfportret als de apostel Paulus",
            webImage = CollectionItemModel.WebImage(
                width = 2287,
                height = 2724,
                url = "https://lh3.googleusercontent.com/NrCcfeY0r2F3M2hIQe5SLDRofR2tVzeOH18VjflOYGj88v4clb4v2H_VgCZR4nJhYsxxH9ATzfkL2tRqOWEK5-gPVEE=s0",
            ),
        )
        val collection3 = CollectionItemModel(
            id = "nl-SK-C-5",
            objectNumber = "SK-C-5",
            title = "De Nachtwacht",
            webImage = CollectionItemModel.WebImage(
                width = 5656,
                height = 4704,
                url = "https://lh3.googleusercontent.com/SsEIJWka3_cYRXXSE8VD3XNOgtOxoZhqW1uB6UFj78eg8gq3G4jAqL4Z_5KwA12aD7Leqp27F653aBkYkRBkEQyeKxfaZPyDx0O8CzWg=s0",
            ),
        )
        collectionsRepository.addItems(collection1, collection2, collection3)

        mainViewModel = MainViewModel(collectionsRepository)
    }

    @Test
    fun getCollectionsWithInternetAccess_collectionsLoadedWithoutError() {
        collectionsRepository.apply {
            isCacheAvailable = false
            isNetworkDown = false
        }
        mainViewModel.loadCollections()

        val collections = mainViewModel.collections.getOrAwaitValue()
        val dataLoading = mainViewModel.dataLoading.getOrAwaitValue()
        val errorText = mainViewModel.errorText.getOrAwaitValue()
        val isDataLoadingError = mainViewModel.isDataLoadingError.getOrAwaitValue()

        assertThat(collections, not(nullValue()))
        assertThat(dataLoading, `is`(false))
        assertThat(errorText, `is`(nullValue()))
        assertThat(isDataLoadingError, `is`(false))
    }

    @Test
    fun getCollectionsWhileNetworkIsDown_collectionsLoadingEncounteredError() {
        collectionsRepository.apply {
            isCacheAvailable = false
            isNetworkDown = true
        }
        mainViewModel.loadCollections()

        val dataLoading = mainViewModel.dataLoading.getOrAwaitValue()
        val errorText = mainViewModel.errorText.getOrAwaitValue()
        val isDataLoadingError = mainViewModel.isDataLoadingError.getOrAwaitValue()

        assertThat(dataLoading, `is`(false))
        assertThat(errorText, `is`(R.string.failed_to_connect_to_remote_server))
        assertThat(isDataLoadingError, `is`(true))
    }

    @Test
    fun retryFailedCollectionsRequestAfterReceivingInternetAccess_collectionsLoadedWithoutError() {
        collectionsRepository.apply {
            isCacheAvailable = false
            isNetworkDown = true
        }
        mainViewModel.loadCollections()
        collectionsRepository.apply {
            isNetworkDown = false
        }
        mainViewModel.onNetworkConnectivityChanged()

        val collections = mainViewModel.collections.getOrAwaitValue()
        val dataLoading = mainViewModel.dataLoading.getOrAwaitValue()
        val errorText = mainViewModel.errorText.getOrAwaitValue()
        val isDataLoadingError = mainViewModel.isDataLoadingError.getOrAwaitValue()

        assertThat(collections, not(nullValue()))
        assertThat(dataLoading, `is`(false))
        assertThat(errorText, `is`(nullValue()))
        assertThat(isDataLoadingError, `is`(false))
    }

    @Test
    fun retryFailedCollectionsRequestWhileNetworkIsStillDown_collectionsStillFailsToLoad() {
        collectionsRepository.apply {
            isCacheAvailable = false
            isNetworkDown = true
        }
        mainViewModel.loadCollections()
        collectionsRepository.apply {
            isNetworkDown = true
        }
        mainViewModel.onNetworkConnectivityChanged()

        val dataLoading = mainViewModel.dataLoading.getOrAwaitValue()
        val errorText = mainViewModel.errorText.getOrAwaitValue()
        val isDataLoadingError = mainViewModel.isDataLoadingError.getOrAwaitValue()

        assertThat(dataLoading, `is`(false))
        assertThat(errorText, `is`(R.string.failed_to_connect_to_remote_server))
        assertThat(isDataLoadingError, `is`(true))
    }

    @Test
    fun refreshCollectionsCacheWithInternetAccessWhileListIsNotLoaded_collectionsLoadedSilently() {
        collectionsRepository.apply {
            isCacheAvailable = false
            isNetworkDown = false
        }
        mainViewModel.loadCollections(silentRefresh = true)

        val collections = mainViewModel.collections.getOrAwaitValue()
        val dataLoading = mainViewModel.dataLoading.getOrAwaitValue()
        val errorText = mainViewModel.errorText.getOrAwaitValue()
        val isDataLoadingError = mainViewModel.isDataLoadingError.getOrAwaitValue()

        assertThat(collections, not(nullValue()))
        assertThat(dataLoading, `is`(false))
        assertThat(errorText, `is`(nullValue()))
        assertThat(isDataLoadingError, `is`(false))
    }

    @Test
    fun refreshCollectionsCacheWithoutInternetAccessWhileListIsNotLoaded_collectionsCacheRefreshFailedSilently() {
        collectionsRepository.apply {
            isCacheAvailable = false
            isNetworkDown = true
        }
        mainViewModel.loadCollections(silentRefresh = true)

        val dataLoading = mainViewModel.dataLoading.getOrAwaitValue()
        val errorText = mainViewModel.errorText.getOrAwaitValue()
        val isDataLoadingError = mainViewModel.isDataLoadingError.getOrAwaitValue()

        assertThat(dataLoading, `is`(false))
        assertThat(errorText, `is`(R.string.failed_to_connect_to_remote_server))
        assertThat(isDataLoadingError, `is`(true))
    }

    @Test
    fun retryRefreshingCollectionsCacheWithInternetAccess_collectionsLoadedSilently() {
        collectionsRepository.apply {
            isCacheAvailable = false
            isNetworkDown = true
        }
        mainViewModel.loadCollections(silentRefresh = true)
        collectionsRepository.apply {
            isNetworkDown = false
        }
        mainViewModel.onNetworkConnectivityChanged()

        val collections = mainViewModel.collections.getOrAwaitValue()
        val dataLoading = mainViewModel.dataLoading.getOrAwaitValue()
        val errorText = mainViewModel.errorText.getOrAwaitValue()
        val isDataLoadingError = mainViewModel.isDataLoadingError.getOrAwaitValue()

        assertThat(collections, not(nullValue()))
        assertThat(dataLoading, `is`(false))
        assertThat(errorText, `is`(nullValue()))
        assertThat(isDataLoadingError, `is`(false))
    }

    @Test
    fun retryRefreshingCollectionsCacheWhileNetworkIsDown_collectionsCacheRefreshFailedSilentlyAgain() {
        collectionsRepository.apply {
            isCacheAvailable = false
            isNetworkDown = true
        }
        mainViewModel.loadCollections(silentRefresh = true)
        collectionsRepository.isNetworkDown = true
        mainViewModel.onNetworkConnectivityChanged()

        val dataLoading = mainViewModel.dataLoading.getOrAwaitValue()
        val errorText = mainViewModel.errorText.getOrAwaitValue()
        val isDataLoadingError = mainViewModel.isDataLoadingError.getOrAwaitValue()

        assertThat(dataLoading, `is`(false))
        assertThat(errorText, `is`(R.string.failed_to_connect_to_remote_server))
        assertThat(isDataLoadingError, `is`(true))
    }

    @Test
    fun refreshCollectionsCacheWithInternetAccessWhileListIsLoaded_collectionsLoadedSilently() {
        collectionsRepository.apply {
            isCacheAvailable = false
            isNetworkDown = false
        }
        mainViewModel.loadCollections()
        collectionsRepository.apply {
            isNetworkDown = false
        }
        mainViewModel.loadCollections(silentRefresh = true)

        val collections = mainViewModel.collections.getOrAwaitValue()
        val dataLoading = mainViewModel.dataLoading.getOrAwaitValue()
        val errorText = mainViewModel.errorText.getOrAwaitValue()
        val isDataLoadingError = mainViewModel.isDataLoadingError.getOrAwaitValue()

        assertThat(collections, not(nullValue()))
        assertThat(dataLoading, `is`(false))
        assertThat(errorText, `is`(nullValue()))
        assertThat(isDataLoadingError, `is`(false))
    }

    @Test
    fun refreshCollectionsCacheWithoutInternetAccessWhileListIsLoaded_collectionsCacheRefreshFailedSilently() {
        collectionsRepository.apply {
            isCacheAvailable = false
            isNetworkDown = false
        }
        mainViewModel.loadCollections()
        collectionsRepository.apply {
            isNetworkDown = true
        }
        mainViewModel.loadCollections(silentRefresh = true)

        val collections = mainViewModel.collections.getOrAwaitValue()
        val dataLoading = mainViewModel.dataLoading.getOrAwaitValue()
        val errorText = mainViewModel.errorText.getOrAwaitValue()
        val isDataLoadingError = mainViewModel.isDataLoadingError.getOrAwaitValue()

        assertThat(collections, not(nullValue()))
        assertThat(dataLoading, `is`(false))
        assertThat(errorText, `is`(R.string.failed_to_connect_to_remote_server))
        assertThat(isDataLoadingError, `is`(false))
    }

    @Test
    fun getCollectionsFromCacheWithInternetAccess_collectionsLoadedWithoutError() {
        collectionsRepository.apply {
            isCacheAvailable = true
            isNetworkDown = false
        }
        mainViewModel.loadCollections()

        val collections = mainViewModel.collections.getOrAwaitValue()
        val dataLoading = mainViewModel.dataLoading.getOrAwaitValue()
        val errorText = mainViewModel.errorText.getOrAwaitValue()
        val isDataLoadingError = mainViewModel.isDataLoadingError.getOrAwaitValue()

        assertThat(collections, not(nullValue()))
        assertThat(dataLoading, `is`(false))
        assertThat(errorText, `is`(nullValue()))
        assertThat(isDataLoadingError, `is`(false))
    }

    @Test
    fun getCollectionsFromCacheWhileNetworkIsDown_collectionsLoadedWithoutError() {
        collectionsRepository.apply {
            isCacheAvailable = true
            isNetworkDown = true
        }
        mainViewModel.loadCollections()

        val collections = mainViewModel.collections.getOrAwaitValue()
        val dataLoading = mainViewModel.dataLoading.getOrAwaitValue()
        val errorText = mainViewModel.errorText.getOrAwaitValue()
        val isDataLoadingError = mainViewModel.isDataLoadingError.getOrAwaitValue()

        assertThat(collections, not(nullValue()))
        assertThat(dataLoading, `is`(false))
        assertThat(errorText, `is`(nullValue()))
        assertThat(isDataLoadingError, `is`(false))
    }

    @Test
    fun refreshCollectionsCacheAfterLoadingFromCacheWhileNetworkIsDown_collectionsCacheRefreshFailedSilently() {
        collectionsRepository.apply {
            isCacheAvailable = true
            isNetworkDown = true
        }
        mainViewModel.loadCollections()
        collectionsRepository.apply {
            isCacheAvailable = false
        }
        mainViewModel.loadCollections(silentRefresh = true)

        val collections = mainViewModel.collections.getOrAwaitValue()
        val dataLoading = mainViewModel.dataLoading.getOrAwaitValue()
        val errorText = mainViewModel.errorText.getOrAwaitValue()
        val isDataLoadingError = mainViewModel.isDataLoadingError.getOrAwaitValue()

        assertThat(collections, not(nullValue()))
        assertThat(dataLoading, `is`(false))
        assertThat(errorText, `is`(R.string.failed_to_connect_to_remote_server))
        assertThat(isDataLoadingError, `is`(false))
    }

    @Test
    fun refreshCollectionsCacheAfterLoadingFromCacheWhileGettingBackOnline_collectionsLoadedSilently() {
        collectionsRepository.apply {
            isCacheAvailable = true
            isNetworkDown = true
        }
        mainViewModel.loadCollections()
        collectionsRepository.apply {
            isCacheAvailable = false
            isNetworkDown = false
        }
        mainViewModel.loadCollections(silentRefresh = true)

        val collections = mainViewModel.collections.getOrAwaitValue()
        val dataLoading = mainViewModel.dataLoading.getOrAwaitValue()
        val errorText = mainViewModel.errorText.getOrAwaitValue()
        val isDataLoadingError = mainViewModel.isDataLoadingError.getOrAwaitValue()

        assertThat(collections, not(nullValue()))
        assertThat(dataLoading, `is`(false))
        assertThat(errorText, `is`(nullValue()))
        assertThat(isDataLoadingError, `is`(false))
    }
}