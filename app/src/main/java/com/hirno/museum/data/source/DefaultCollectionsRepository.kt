package com.hirno.museum.data.source

import android.content.Context
import com.hirno.museum.data.GenericResponse
import com.hirno.museum.model.collection.CollectionResponseModel
import com.hirno.museum.network.response.NetworkResponse

/**
 * Concrete implementation to load collections from the data sources into a cache.
 */
class DefaultCollectionsRepository(
    private val applicationContext: Context,
    private val collectionsRemoteDataSource: CollectionsDataSource,
    private val collectionsLocalDataSource: CollectionsDataSource,
) : CollectionsRepository {
    companion object {
        /**
         * Max cache lifetime in milliseconds
         */
        const val MAX_CACHE_LIFETIME_MILLIS = 5 * 60 * 1000
    }

    /**
     * Checks collections cache before retrieval. If the cache was still valid, will use local storage to load collections.
     * Otherwise it will load them from server
     */
    override suspend fun getCollections(): GenericResponse<CollectionResponseModel> {
        val preferences = applicationContext.getSharedPreferences("cache_expiry", Context.MODE_PRIVATE)
        val collectionsCacheExpiry = preferences.getLong("collections", 0)
        val isCacheExpired = collectionsCacheExpiry < System.currentTimeMillis()
        return if (isCacheExpired) getCollectionsFromRemoteDataSource()
        else collectionsLocalDataSource.getCollections()
    }

    /**
     * Gets collections from remote data source and updates the cache
     */
    private suspend fun getCollectionsFromRemoteDataSource(): GenericResponse<CollectionResponseModel> {
        return collectionsRemoteDataSource.getCollections().also { result ->
            if (result is NetworkResponse.Success) {
                collectionsLocalDataSource.cacheCollections(result.body).let { cached ->
                    if (cached) {
                        applicationContext.getSharedPreferences("cache_expiry", Context.MODE_PRIVATE).edit().apply {
                            putLong("collections", System.currentTimeMillis() + MAX_CACHE_LIFETIME_MILLIS)
                        }.apply()
                    }
                }
            }
        }
    }
}