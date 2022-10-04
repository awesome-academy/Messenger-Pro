package com.example.finalapplication.screen.chatroom

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.ListAdapter
import com.example.finalapplication.R
import com.example.finalapplication.data.model.Message
import com.example.finalapplication.databinding.ItemLoadMoreBinding
import com.example.finalapplication.databinding.ItemReciveBinding
import com.example.finalapplication.databinding.ItemSendBinding
import com.example.finalapplication.utils.base.BaseViewHolder
import com.example.finalapplication.utils.loadImageByUrl
import com.example.finalapplication.utils.toDateTime

class ChatListAdapter(private val revicerid: String) :
    ListAdapter<Message, BaseViewHolder<Any>>(Message.diffCallback) {

    var haveNextPage = true

    override fun getItemViewType(position: Int): Int {
        if (currentList.size > 0 && position == currentList.size && haveNextPage) return TYPE_LOAD
        return when (currentList[position].senderId) {
            revicerid -> TYPE_RECEIVE_DATA
            else -> TYPE_SEND_DATA
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<Any> {
        val layoutInflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            TYPE_LOAD -> LoadViewHolder(ItemLoadMoreBinding.inflate(layoutInflater, parent, false))
            TYPE_SEND_DATA -> SendMessageViewHolder(
                ItemSendBinding.inflate(
                    layoutInflater,
                    parent,
                    false
                )
            )
            else -> ReceiveMessageViewHolder(
                ItemReciveBinding.inflate(
                    layoutInflater,
                    parent,
                    false
                )
            )
        }
    }

    override fun onBindViewHolder(holder: BaseViewHolder<Any>, position: Int) {
        if (currentList.size <= position) return
        holder.bind(currentList[position])
    }

    override fun getItemCount(): Int {
        if (haveNextPage && currentList.size > 0) return currentList.size + 1
        return super.getItemCount()
    }

    inner class SendMessageViewHolder(val binding: ItemSendBinding) : BaseViewHolder<Any>(binding) {

        override fun bind(item: Any) {
            val message = item as Message
            when (message.chatType) {
                Message.chatbox -> {
                    binding.apply {
                        containerCall.isVisible = false
                        if (message.text.isNullOrEmpty()) {
                            imageSend.isVisible = true
                            textSend.isVisible = false
                            imageSend.loadImageByUrl(message.image, binding.root.context)
                        } else {
                            textSend.isVisible = true
                            imageSend.isVisible = false
                            textSend.text = message.text
                        }
                    }
                }
                Message.call -> {
                    binding.apply {
                        containerCall.isVisible = true
                        textSend.isVisible = false
                        imageSend.isVisible = false
                        if (message.callTime < 0) {
                            imageCall.setImageResource(R.drawable.ic_baseline_call_missed_24)
                            textTypeCall.text = Message.missCall
                        } else {
                            imageCall.setImageResource(R.drawable.ic_baseline_call_24)
                            textTypeCall.text = Message.call
                        }
                        textTime.text = message.time.toDateTime()
                    }
                }
                Message.video -> {
                    binding.apply {
                        containerCall.isVisible = true
                        textSend.isVisible = false
                        imageSend.isVisible = false
                        if (message.callTime < 0) {
                            imageCall.setImageResource(R.drawable.ic_baseline_missed_video_call_24)
                            textTypeCall.text = Message.missVideo
                        } else {
                            imageCall.setImageResource(R.drawable.ic_baseline_videocam_24)
                            textTypeCall.text = Message.video
                        }
                        textTime.text = message.time.toDateTime()
                    }
                }
            }
        }
    }

    inner class ReceiveMessageViewHolder(val binding: ItemReciveBinding) :
        BaseViewHolder<Any>(binding) {

        override fun bind(item: Any) {
            val message = item as Message
            when (message.chatType) {
                Message.chatbox -> {
                    binding.apply {
                        containerCall.isVisible = false
                        if (message.text.isNullOrEmpty()) {
                            imageReceiver.isVisible = true
                            textReciver.isVisible = false
                            imageReceiver.loadImageByUrl(message.image, binding.root.context)
                        } else {
                            textReciver.isVisible = true
                            imageReceiver.isVisible = false
                            textReciver.text = message.text
                        }
                    }
                }
                Message.call -> {
                    binding.apply {
                        containerCall.isVisible = true
                        textReciver.isVisible = false
                        imageReceiver.isVisible = false
                        if (message.callTime < 0) {
                            imageCall.setImageResource(R.drawable.ic_baseline_call_missed_24)
                            textTypeCall.text = Message.missCall
                        } else {
                            imageCall.setImageResource(R.drawable.ic_baseline_call_24)
                            textTypeCall.text = Message.call
                        }
                        textTime.text = message.time.toDateTime()
                    }
                }
                Message.video -> {
                    binding.apply {
                        containerCall.isVisible = true
                        textReciver.isVisible = false
                        imageReceiver.isVisible = false
                        if (message.callTime < 0) {
                            imageCall.setImageResource(R.drawable.ic_baseline_missed_video_call_24)
                            textTypeCall.text = Message.missVideo
                        } else {
                            imageCall.setImageResource(R.drawable.ic_baseline_videocam_24)
                            textTypeCall.text = Message.video
                        }
                        textTime.text = message.time.toDateTime()
                    }
                }
            }
        }
    }

    inner class LoadViewHolder(val binding: ItemLoadMoreBinding) : BaseViewHolder<Any>(binding) {

        override fun bind(item: Any) {
            // TODo no-ip
        }
    }

    companion object {
        const val TYPE_SEND_DATA = 1
        const val TYPE_RECEIVE_DATA = 2
        const val TYPE_LOAD = 3
    }
}
