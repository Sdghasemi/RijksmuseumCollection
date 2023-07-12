package com.hirno.museum.ui.main

import com.hirno.museum.data.GenericResponse
import com.hirno.museum.data.source.CollectionsRepository
import com.hirno.museum.model.collection.CollectionItemModel
import com.hirno.museum.model.collection.CollectionResponseModel
import com.hirno.museum.network.response.NetworkResponse
import okio.IOException

/**
 * A fake collections repository used for testing
 */
class FakeCollectionsRepository : CollectionsRepository {

    private val collections = ArrayList<CollectionItemModel>()

    var isCacheAvailable = false
    var isNetworkDown = false


    fun addItems(vararg collection: CollectionItemModel) {
        collections.addAll(collection)
    }

    /**
     * Returns suitable answer based on network and cache state
     *
     * @return [NetworkResponse.NetworkError] if there were no cache available while the network is down, [NetworkResponse.Success] otherwise
     */
    override suspend fun getCollections(): GenericResponse<CollectionResponseModel> {
        return if (isNetworkDown && !isCacheAvailable) NetworkResponse.NetworkError(IOException())
        else NetworkResponse.Success(CollectionResponseModel(collections))
    }
}