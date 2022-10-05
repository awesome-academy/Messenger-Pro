package com.example.finalapplication.utils

import com.example.finalapplication.BuildConfig

object FcmConstant {
    const val BASE_URL_FCM = "https://fcm.googleapis.com/fcm/"
    const val MEETING_TYPE = "meeting_type"
    const val DATA = "data"
    const val INVITATION = "invitation"
    const val RESPOND_INVITATION = "respond"
    const val CANCEL_INVITATION = "cancel_invitation"
    const val ACCEPT_INVITATION = "accept_invitation"
    const val DENY_INVITATION = "deny_invitation"
    const val SERVER_KEY = BuildConfig.API_KEY
    const val AUTHORIZATION = "Authorization"
    const val CONTENT_TYPE = "Content-Type"
    const val CONTENT_TYPE_VALUE = "application/json"
    const val BASE_URL_MEET = "https://meet.jit.si"

    fun getHeader(): HashMap<String, String> {
        val headers = hashMapOf<String, String>()
        headers[AUTHORIZATION] = SERVER_KEY
        headers[CONTENT_TYPE] = CONTENT_TYPE_VALUE
        return headers
    }
}
