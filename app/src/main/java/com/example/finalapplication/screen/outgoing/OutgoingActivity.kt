package com.example.finalapplication.screen.outgoing

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.lifecycle.lifecycleScope
import com.example.finalapplication.data.model.Message
import com.example.finalapplication.data.model.User
import com.example.finalapplication.databinding.ActivityOutgoingBinding
import com.example.finalapplication.utils.Constant
import com.example.finalapplication.utils.FcmConstant
import com.example.finalapplication.utils.TimeConstant
import com.example.finalapplication.utils.base.BaseActivity
import com.example.finalapplication.utils.getNewid
import com.example.finalapplication.utils.loadImageByUrl
import com.example.finalapplication.utils.showMessage
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jitsi.meet.sdk.JitsiMeetActivity
import org.jitsi.meet.sdk.JitsiMeetConferenceOptions
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.net.URL

class OutgoingActivity : BaseActivity<ActivityOutgoingBinding>(ActivityOutgoingBinding::inflate) {

    private val outgoingViewModel by viewModel<OutgoingViewModel>()
    private lateinit var currentUser: User
    private lateinit var adversaryUser: User
    private lateinit var meetingType: String
    private val localReceiver = object : BroadcastReceiver() {
        override fun onReceive(p0: Context?, broadcardIntent: Intent?) {
            when (broadcardIntent?.getStringExtra(FcmConstant.RESPOND_INVITATION)) {
                FcmConstant.DENY_INVITATION -> {
                    addMissMessage()
                }
                FcmConstant.ACCEPT_INVITATION -> {
                    addCallMessage()
                    val builder = JitsiMeetConferenceOptions.Builder()
                        .setServerURL(URL(FcmConstant.BASE_URL_MEET))
                        .setWelcomePageEnabled(false)
                        .setRoom(currentUser?.id + adversaryUser?.id)
                    if (meetingType == Message.call)
                        builder.setVideoMuted(true)
                    JitsiMeetActivity.launch(this@OutgoingActivity, builder.build())
                }
            }
        }

    }

    private fun addCallMessage() {
        val message = Message(
            getNewid().toString(),
            meetingType,
            System.currentTimeMillis(),
            currentUser.id.toString(),
            adversaryUser.id.toString()
        )
        message.roomId = currentUser.id + adversaryUser.id
        outgoingViewModel.sendMessage(currentUser, adversaryUser, message)
    }

    override fun bindData() {
        outgoingViewModel.errorMessage.observe(this) { data ->
            if (data.isNullOrEmpty()) data.showMessage(applicationContext)
        }
        outgoingViewModel.isFinish.observe(this) { data ->
            if (data) {
                finish()
            }
        }
    }

    private fun addMissMessage() {
        val message = Message(
            getNewid().toString(),
            meetingType,
            System.currentTimeMillis(),
            currentUser.id.toString(),
            adversaryUser.id.toString()
        )
        message.roomId = currentUser.id + adversaryUser.id
        message.callTime = -1
        outgoingViewModel.sendMessage(currentUser, adversaryUser, message)
    }

    override fun handleEvent() {
        binding.apply {
            buttonCancel.setOnClickListener {
                outgoingViewModel.sendNotification(
                    currentUser,
                    adversaryUser,
                    FcmConstant.CANCEL_INVITATION
                )
                addMissMessage()
            }
        }
    }

    override fun initData() {
        val filter = IntentFilter(FcmConstant.RESPOND_INVITATION)
        registerReceiver(localReceiver, filter)
        val bundle = intent.getBundleExtra(FcmConstant.DATA)
        currentUser = bundle?.getSerializable(User.currentUser) as User
        adversaryUser = bundle?.getSerializable(User.adversaryUser) as User
        meetingType = bundle?.getString(FcmConstant.MEETING_TYPE).toString()
        binding.apply {
            textCallType.text = meetingType
            imageAvatar.loadImageByUrl(adversaryUser.avatar, applicationContext)
            textUser.text = adversaryUser.name
        }
        outgoingViewModel.sendNotification(currentUser, adversaryUser, meetingType)
        lifecycleScope.launch {
            delay(TimeConstant.DELAY_FOR_CANCEL)
            Constant.ERROR_USER_NOT_CONNECT.showMessage(applicationContext)
            outgoingViewModel.sendNotification(
                currentUser,
                adversaryUser,
                FcmConstant.CANCEL_INVITATION
            )
            addMissMessage()
        }
    }

    override fun onDestroy() {
        unregisterReceiver(localReceiver)
        super.onDestroy()
    }
}
