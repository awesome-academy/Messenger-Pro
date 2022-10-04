package com.example.finalapplication.utils

import com.example.finalapplication.data.model.Notification
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.HeaderMap
import retrofit2.http.POST

interface ApiFcmService {

    @POST("send")
    suspend fun sendRemoteMessage(
        @HeaderMap headers: HashMap<String, String>,
        @Body body: Notification
    ): Notification
}
