package com.kfc.onlinestore.network

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    private const val BASE_URL = "https://fefu2026spring.deploy.feip.dev/"

    private fun getToken(): String = "Cmt7wdwFgDIi1_SRX8hlJIExs0jJKPr4axflLpExAxM"

    private val okHttpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .addInterceptor { chain ->
                val token = getToken()

                val request = chain.request().newBuilder()
                    .addHeader("Authorization", "Bearer $token")
                    .build()

                chain.proceed(request)
            }
            .addInterceptor(
                HttpLoggingInterceptor().apply {
                    level = HttpLoggingInterceptor.Level.BODY
                }
            )
            .build()
    }

    val apiService: CatalogApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(CatalogApiService::class.java)
    }
}