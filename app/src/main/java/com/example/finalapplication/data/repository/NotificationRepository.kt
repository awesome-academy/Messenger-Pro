package com.example.finalapplication.data.repository

import com.example.finalapplication.data.model.Notification
import com.example.finalapplication.data.repository.resource.Listenner

interface NotificationRepository {
    suspend fun sendNotification(body : Notification, listen : Listenner<Boolean>)
}
