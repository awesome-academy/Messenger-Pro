package com.example.finalapplication.screen.incoming

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import com.example.finalapplication.data.model.DataNotification
import com.example.finalapplication.data.model.Message
import com.example.finalapplication.data.model.User
import com.example.finalapplication.databinding.ActivityIncomingBinding
import com.example.finalapplication.utils.FcmConstant
import com.example.finalapplication.utils.base.BaseActivity
import com.example.finalapplication.utils.loadImageByUrl
import com.example.finalapplication.utils.showMessage
import org.jitsi.meet.sdk.JitsiMeetActivity
import org.jitsi.meet.sdk.JitsiMeetConferenceOptions
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.net.URL

class IncomingActivity : BaseActivity<ActivityIncomingBinding>(ActivityIncomingBinding::inflate) {

    private val incomingViewModel by viewModel<IncomingViewModel>()
    private var user: User? = null
    private lateinit var inviterToken: String
    private lateinit var roomId: String
    private lateinit var meetingType: String
    private val localReceiver = object : BroadcastReceiver() {
        override fun onReceive(p0: Context?, broadcardIntent: Intent?) {
            when (broadcardIntent?.getStringExtra(FcmConstant.INVITATION)) {
                FcmConstant.CANCEL_INVITATION -> finish()
                else -> {}
            }
        }

    }

    override fun bindData() {
        val filter = IntentFilter(FcmConstant.INVITATION)
        registerReceiver(localReceiver, filter)
        incomingViewModel.user.observe(this) { data ->
            user = data
        }
        incomingViewModel.errorMessage.observe(this) { data ->
            data.showMessage(applicationContext)
        }
        incomingViewModel.isFinish.observe(this) { data ->
            if (data) finish()
        }
    }

    override fun handleEvent() {
        binding.apply {
            buttonAccept.setOnClickListener {
                incomingViewModel.sendNotification(
                    user,
                    inviterToken,
                    FcmConstant.ACCEPT_INVITATION
                )
                val builder = JitsiMeetConferenceOptions.Builder()
                    .setServerURL(URL(FcmConstant.BASE_URL_MEET))
                    .setWelcomePageEnabled(false)
                    .setRoom(roomId)
                if (meetingType == Message.call)
                    builder.setVideoMuted(true)
                JitsiMeetActivity.launch(this@IncomingActivity, builder.build())
                finish()
            }
            buttonDeny.setOnClickListener {
                incomingViewModel.sendNotification(
                    user,
                    inviterToken,
                    FcmConstant.DENY_INVITATION
                )
            }
        }
    }

    override fun initData() {
        binding.apply {
            imageAvatar.loadImageByUrl(
                intent.getStringExtra(DataNotification.avatar),
                applicationContext
            )
            meetingType = intent.getStringExtra(DataNotification.meetingType).toString()
            textCallType.text = meetingType
            textUser.text = intent.getStringExtra(DataNotification.name)
            inviterToken = intent.getStringExtra(DataNotification.senderToken).toString()
            roomId = intent.getStringExtra(DataNotification.id).toString()
        }
    }

    override fun onDestroy() {
        unregisterReceiver(localReceiver)
        super.onDestroy()
    }
}
