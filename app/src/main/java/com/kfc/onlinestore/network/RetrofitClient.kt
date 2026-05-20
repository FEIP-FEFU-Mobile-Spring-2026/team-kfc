package com.kfc.onlinestore.network

import com.kfc.onlinestore.model.CatalogApiService
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val BASE_URL = "https://api.example.com/"

    private fun getToken(): String {
        return ""
    }

    // Настраиваем OkHttpClient
    private val okHttpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .addInterceptor { chain ->
                // Перехватываем оригинальный запрос
                val originalRequest = chain.request()

                // Получаем актуальный токен
                val token = getToken()

                // Создаем новый запрос на основе старого, но добавляем заголовок авторизации
                val authenticatedRequest = originalRequest.newBuilder()
                    .header("Authorization", "Bearer $token")
                    .build()

                // Отправляем модифицированный запрос дальше
                chain.proceed(authenticatedRequest)
            }
            // Полезно для отладки: выводит все запросы и ответы в лог (Logcat)
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            .build()
    }

    // Передаем настроенный okHttpClient в Retrofit
    val apiService: CatalogApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient) // <-- Ключевой момент: связываем Retrofit с нашим клиентом
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(CatalogApiService::class.java)
    }
}