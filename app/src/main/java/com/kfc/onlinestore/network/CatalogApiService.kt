package com.kfc.onlinestore.network

import com.kfc.onlinestore.model.StoreResponse
import retrofit2.http.GET

interface CatalogApiService {

    @GET("catalog")
    suspend fun getCatalog(): StoreResponse
}