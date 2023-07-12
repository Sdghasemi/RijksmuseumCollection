package com.hirno.museum.data.source

import com.hirno.museum.data.GenericResponse
import com.hirno.museum.model.collection.CollectionResponseModel

/**
 * Interface to the data layer.
 */
interface CollectionsRepository {
    suspend fun getCollections(): GenericResponse<CollectionResponseModel>
}