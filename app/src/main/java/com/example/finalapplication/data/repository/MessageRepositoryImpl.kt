package com.example.finalapplication.data.repository

import com.example.finalapplication.data.model.Message
import com.example.finalapplication.data.model.User
import com.example.finalapplication.data.repository.resource.Listenner
import com.example.finalapplication.data.repository.resource.MessageDataSource

class MessageRepositoryImpl(private val remote: MessageDataSource.Remote) : MessageRepository {

    override suspend fun sendMessage(
        currentUser: User,
        adversaryUser: User,
        message: Message,
        listen: Listenner<Boolean>
    ) {
        remote.sendMessage(currentUser, adversaryUser, message, listen)
    }

    override suspend fun getHistoryMessage(
        reciverId: String,
        lastIndex: Long,
        listen: Listenner<List<Message>>
    ) {
        remote.getHistoryMessage(reciverId, lastIndex, listen)
    }
}
