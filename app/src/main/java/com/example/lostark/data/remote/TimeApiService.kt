package com.example.lostark.data.remote

import com.example.lostark.data.model.DetailTime
import retrofit2.http.GET

interface TimeApiService {
    @GET("api/Time/current/zone?timeZone=Europe/Moscow")
    suspend fun getTime(): DetailTime
}