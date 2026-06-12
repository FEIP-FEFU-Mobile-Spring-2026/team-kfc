package com.kfc.onlinestore.network

import com.kfc.onlinestore.model.StoreResponse
import retrofit2.http.GET

interface CatalogApiService {

    @GET("https://fefu2026spring.deploy.feip.dev/catalog")
    suspend fun getCatalog(): StoreResponse
}