package com.example.finalapplication.data.model

import android.annotation.SuppressLint
import android.os.Parcelable
import androidx.recyclerview.widget.DiffUtil
import kotlinx.parcelize.Parcelize


@Parcelize
class Message() : Parcelable {
    var id: String? = null
    var text: String? = null
    var image: String? = null
    var time: Long = 0
    var chatType: String? = null
    var callTime: Long = 0
    var senderId: String? = null
    var reciverId: String? = null
    var seen: Boolean = false
    var roomId: String? = null

    constructor(
        id: String,
        chatType: String,
        time: Long,
        senderId: String,
        reciverId: String
    ) : this() {
        this.id = id
        this.chatType = chatType
        this.time = time
        this.senderId = senderId
        this.reciverId = reciverId
    }

    companion object {
        const val messages = "messages"
        const val reciverId = "reciverId"
        const val senderId = "senderId"
        const val seen = "seen"
        const val time = "time"
        const val chatbox = "chatbox"
        const val call = "Cuộc gọi thoại"
        const val missCall = "Cuộc gọi nhỡ"
        const val missVideo = "Cuộc gọi nhỡ video"
        const val video = "Cuộc gọi video"
        const val image = "Hình ảnh"
        const val roomId = "roomId"
        val diffCallback = object : DiffUtil.ItemCallback<Message>() {
            override fun areItemsTheSame(oldItem: Message, newItem: Message): Boolean {
                return oldItem.id == newItem.id
            }

            @SuppressLint("DiffUtilEquals")
            override fun areContentsTheSame(oldItem: Message, newItem: Message): Boolean {
                return oldItem == newItem
            }

        }
    }

    override fun toString(): String {
        return text.toString()
    }
}
