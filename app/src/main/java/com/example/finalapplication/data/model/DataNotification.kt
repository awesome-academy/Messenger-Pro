package com.example.finalapplication.data.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class DataNotification(
    @SerializedName("id")
    val id: String?,
    @SerializedName("name")
    val name: String?,
    @SerializedName("avatar")
    val avatar: String?,
    @SerializedName("fcm_token")
    val fcmToken: String?,
    @SerializedName("type")
    val type: String?,
    @SerializedName("meeting_type")
    val meetingType: String?,
) : Parcelable {

    companion object {
        const val id = "id$1"
        const val senderToken = "senderToken"
        const val type = "type$1"
        const val meetingType = "meetingType$1"
        const val avatar = "avatar$1"
        const val fcmToken = "fcmToken$1"
        const val name = "name$1"
    }
}
