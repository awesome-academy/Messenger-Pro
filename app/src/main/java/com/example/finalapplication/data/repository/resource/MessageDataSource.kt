package com.example.finalapplication.data.repository.resource

import com.example.finalapplication.data.model.Message
import com.example.finalapplication.data.model.User

interface MessageDataSource {
    interface Remote {
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
}
