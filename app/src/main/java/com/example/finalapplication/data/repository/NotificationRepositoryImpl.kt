package com.example.finalapplication.data.repository

import com.example.finalapplication.data.model.Notification
import com.example.finalapplication.data.repository.resource.Listenner
import com.example.finalapplication.utils.ApiFcmService
import com.example.finalapplication.utils.FcmConstant
import java.io.IOException

class NotificationRepositoryImpl(private val apiFcmService: ApiFcmService) :
    NotificationRepository {

    override suspend fun sendNotification(body: Notification, listen: Listenner<Boolean>) {
        try {
            val response = apiFcmService.sendRemoteMessage(FcmConstant.getHeader(), body)
            if (response != null) listen.onSuccess(true)
            else listen.onSuccess(false)
        } catch (e: IOException) {
            listen.onError(e.toString())
        }
    }
}
