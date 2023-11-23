package com.example.lostark.data.remote

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object TimeApi {
    private const val BASE_URL = "https://www.timeapi.io/"
    val apiService: TimeApiService = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build().create(TimeApiService::class.java)
}