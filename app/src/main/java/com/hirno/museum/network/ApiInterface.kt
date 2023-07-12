package com.hirno.museum.network

import com.hirno.museum.model.collection.CollectionResponseModel
import com.hirno.museum.data.GenericResponse
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * API interface of the app endpoints
 */
interface ApiInterface {
    @GET("collection")
    suspend fun getCollections(@Query("q") query: String, @Query("ps") pageSize: Int = 100): GenericResponse<CollectionResponseModel>
}