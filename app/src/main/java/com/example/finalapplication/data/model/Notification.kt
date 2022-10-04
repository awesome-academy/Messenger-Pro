package com.example.finalapplication.data.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Notification(
    @SerializedName("data")
    val data: DataNotification?,
    @SerializedName("registration_ids")
    val registrationIds: List<String?>?) : Parcelable
