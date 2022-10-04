package com.example.finalapplication.screen

import android.app.PendingIntent
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.util.Log
import com.example.finalapplication.data.model.DataNotification
import com.example.finalapplication.data.model.Message
import com.example.finalapplication.screen.incoming.IncomingActivity
import com.example.finalapplication.utils.FcmConstant
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlin.random.Random

class FcmService : FirebaseMessagingService() {

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        when (message.data[DataNotification.type]) {
            FcmConstant.INVITATION -> {
                if (message.data[DataNotification.meetingType] == Message.call ||
                    message.data[DataNotification.meetingType] == Message.video
                ) {
                    val intent = Intent(applicationContext, IncomingActivity::class.java)
                    intent.flags = FLAG_ACTIVITY_NEW_TASK
                    intent.putExtra(DataNotification.avatar, message.data[DataNotification.avatar])
                    intent.putExtra(DataNotification.name, message.data[DataNotification.name])
                    intent.putExtra(
                        DataNotification.senderToken,
                        message.data[DataNotification.fcmToken]
                    )
                    intent.putExtra(
                        DataNotification.meetingType,
                        message.data[DataNotification.meetingType]
                    )
                    intent.putExtra(DataNotification.id, message.data[DataNotification.id])
                    startActivity(intent)
                } else {
                    val intent = Intent(FcmConstant.INVITATION)
                    intent.putExtra(
                        FcmConstant.INVITATION,
                        message.data[DataNotification.meetingType]
                    )
                    PendingIntent.getBroadcast(
                        applicationContext,
                        Random.nextInt(),
                        intent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                    ).send()
                }
            }
            FcmConstant.RESPOND_INVITATION -> {
                val intent = Intent(FcmConstant.RESPOND_INVITATION)
                intent.putExtra(
                    FcmConstant.RESPOND_INVITATION,
                    message.data[DataNotification.meetingType]
                )
                PendingIntent.getBroadcast(
                    applicationContext,
                    Random.nextInt(),
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT
                ).send()
            }
        }
    }
}
