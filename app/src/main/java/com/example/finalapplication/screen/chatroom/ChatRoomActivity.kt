package com.example.finalapplication.screen.chatroom

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.finalapplication.BuildConfig
import com.example.finalapplication.data.model.Message
import com.example.finalapplication.data.model.User
import com.example.finalapplication.databinding.ActivityChatRoomBinding
import com.example.finalapplication.screen.outgoing.OutgoingActivity
import com.example.finalapplication.utils.Constant
import com.example.finalapplication.utils.FcmConstant
import com.example.finalapplication.utils.NumberConstant
import com.example.finalapplication.utils.ScrollListenner
import com.example.finalapplication.utils.StatusConstant
import com.example.finalapplication.utils.base.BaseActivity
import com.example.finalapplication.utils.getNewid
import com.example.finalapplication.utils.loadImageByUrl
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File

class ChatRoomActivity : BaseActivity<ActivityChatRoomBinding>(ActivityChatRoomBinding::inflate) {

    private val chatViewModel: ChatViewModel by viewModel()
    private var currentUser: User? = null
    private var adversaryUser: User? = null
    private val messages = mutableListOf<Message>()
    private lateinit var chatAdapter: ChatListAdapter
    private var isFisrtMessage = true
    private var isEndPage = false
    private var isLoading = false
    private var currentPath = ""
    private var isLoadImage = false
    private lateinit var adversaryId: String
    private val activityResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK && result.data != null) {
                val intent = result.data
                val uri = intent?.data
                val message = Message(
                    getNewid().toString(),
                    Message.chatbox,
                    System.currentTimeMillis(),
                    currentUser?.id.toString(),
                    adversaryUser?.id.toString()
                )
                message.image = uri.toString()
                message.roomId = currentUser?.id + adversaryUser?.id
                isLoadImage = true
                adversaryUser?.let { adversary ->
                    currentUser?.let { current ->
                        chatViewModel.sendMessage(
                            current,
                            adversary,
                            message
                        )
                    }
                }
            }
        }
    private val activityResultCameraLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val message = Message(
                    getNewid().toString(),
                    Message.chatbox,
                    System.currentTimeMillis(),
                    currentUser?.id.toString(),
                    adversaryUser?.id.toString()
                )
                message.image = Uri.fromFile(File(currentPath)).toString()
                message.roomId = currentUser?.id + adversaryUser?.id
                isLoadImage = true
                adversaryUser?.let { adversary ->
                    currentUser?.let { current ->
                        chatViewModel.sendMessage(
                            current,
                            adversary,
                            message
                        )
                    }
                }
            }
        }

    override fun bindData() {
        chatViewModel.receiver.observe(this) { data ->
            adversaryUser = data
            binding.apply {
                imageAvatar.loadImageByUrl(data.avatar, applicationContext)
                textStatus.text = if (data.online) StatusConstant.ONLINE
                else StatusConstant.OFFLINE
                textName.text = data.name
            }
        }
        chatViewModel.currentUser.observe(this) { data ->
            currentUser = data
            if (data.id.toString() == adversaryId) {
                binding.buttonCall.isVisible = false
                binding.buttonVideoCall.isVisible = false
            }
        }
        chatViewModel.newMessage.observe(this) { data ->
            if (messages.isNotEmpty() && data.id == messages.first().id) {
                messages.removeFirst()
                messages.add(0, data)
            } else {
                messages.add(0, data)
                chatViewModel.updateSeenMessage(currentUser?.id + adversaryUser?.id)
            }
            binding.progressLoading.isVisible = false
            chatAdapter.submitList(messages)
            chatAdapter.notifyDataSetChanged()
            if (isFisrtMessage) {
                isFisrtMessage = false
                chatViewModel.getHistoryMessage(adversaryUser?.id.toString(), data.time)
            }
        }
        chatViewModel.historyMessage.observe(this) { data ->
            if (messages.size > 1 && data.isNotEmpty() && messages[1].time < data.first().time)
                messages.add(1, data.first())
            else messages.addAll(data)
            chatAdapter.submitList(messages)
            binding.recyclerHistoryMessage.smoothScrollToPosition(0)
            chatAdapter.notifyDataSetChanged()
            if (data.size < NumberConstant.ITEM_PER_PAGE) {
                isEndPage = true
                chatAdapter.haveNextPage = false
            }
        }
        chatViewModel.isLoading.observe(this) { data ->
            this.isLoading = data
            if (messages.isEmpty() || isLoadImage) {
                binding.progressLoading.isVisible = data
                isLoadImage = false
            }
        }
    }

    override fun handleEvent() {
        binding.apply {
            buttonBack.setOnClickListener {
                onBackPressed()
            }
            buttonSend.setOnClickListener {
                val messageText = textMessage.text?.trim().toString()
                textMessage.text?.clear()
                if (messageText.isNullOrEmpty()) return@setOnClickListener
                val message = Message(
                    getNewid().toString(),
                    Message.chatbox,
                    System.currentTimeMillis(),
                    currentUser?.id.toString(),
                    adversaryUser?.id.toString()
                )
                message.text = messageText
                message.roomId = currentUser?.id + adversaryUser?.id
                adversaryUser?.let { adversary ->
                    currentUser?.let { current ->
                        chatViewModel.sendMessage(
                            current,
                            adversary,
                            message
                        )
                    }
                }
            }
            recyclerHistoryMessage.addOnScrollListener(object :
                ScrollListenner(recyclerHistoryMessage.layoutManager as LinearLayoutManager) {
                override fun loadMore() {
                    chatViewModel.getHistoryMessage(
                        adversaryUser?.id.toString(),
                        messages.last().time
                    )
                }

                override fun isLoading() = isLoading

                override fun isEndPage() = isEndPage
            })
            buttonImage.setOnClickListener {
                val intent = Intent(Intent.ACTION_GET_CONTENT)
                intent.type = Constant.TYPE_IMAGE
                activityResultLauncher.launch(intent)
            }
            buttonCamera.setOnClickListener {
                handleCapture()
            }
            buttonCall.setOnClickListener {
                handleCall(Message.call)
            }
            buttonVideoCall.setOnClickListener {
                handleCall(Message.video)
            }
        }
    }

    private fun handleCall(meetingType: String) {
        val intent = Intent(this, OutgoingActivity::class.java)
        val bundle = Bundle()
        bundle.putSerializable(User.currentUser, currentUser)
        bundle.putSerializable(User.adversaryUser, adversaryUser)
        bundle.putString(FcmConstant.MEETING_TYPE, meetingType)
        intent.putExtra(FcmConstant.DATA, bundle)
        startActivity(intent)
    }

    private fun handleCapture() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        val store = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val imageFile =
            File.createTempFile(Message.messages, Constant.EXTENSION_IMAGE, store)
        imageFile.deleteOnExit()
        currentPath = imageFile.absolutePath
        val imageUri = FileProvider.getUriForFile(
            applicationContext,
            BuildConfig.APPLICATION_ID + Constant.PROVIDER,
            imageFile
        )
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
        activityResultCameraLauncher.launch(intent)
    }

    override fun initData() {
        adversaryId = intent.getStringExtra(Constant.RECIVER).toString()
        chatAdapter = ChatListAdapter(adversaryId)
        val layoutManager = binding.recyclerHistoryMessage.layoutManager as LinearLayoutManager
        layoutManager.reverseLayout = true
        layoutManager.stackFromEnd = true
        binding.recyclerHistoryMessage.layoutManager = layoutManager
        binding.recyclerHistoryMessage.adapter = chatAdapter
        chatViewModel.getReciver(adversaryId)
        chatViewModel.getNewMessage(adversaryId)
        chatAdapter.notifyDataSetChanged()
    }
}
