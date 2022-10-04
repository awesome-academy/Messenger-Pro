package com.example.finalapplication.data.repository

import com.example.finalapplication.data.model.Message
import com.example.finalapplication.data.model.User
import com.example.finalapplication.data.repository.resource.Listenner

interface MessageRepository {
    suspend fun sendMessage(
        currentUser: User,
        adversaryUser: User,
        message: Message,
        listen: Listenner<Boolean>
    )

    suspend fun getHistoryMessage(
        reciverId: String,
        lastIndex: Long,
        listen: Listenner<List<Message>>
    )
}
