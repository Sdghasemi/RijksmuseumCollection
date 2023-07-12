package com.hirno.museum.data.source

import com.hirno.museum.model.collection.CollectionResponseModel
import com.hirno.museum.data.GenericResponse

/**
 * Main entry point for accessing collections data.
 */
interface CollectionsDataSource {
    suspend fun getCollections(): GenericResponse<CollectionResponseModel>

    suspend fun cacheCollections(collections: CollectionResponseModel): Boolean

    suspend fun deleteAllCollections()
}