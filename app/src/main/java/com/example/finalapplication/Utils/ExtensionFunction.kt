package com.example.finalapplication.utils

import android.content.Context
import android.widget.ImageView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.finalapplication.R
import de.hdodenhof.circleimageview.CircleImageView
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

fun CircleImageView.loadImageByUrl(url: String?, context: Context?) {
    if (context != null) {
        Glide.with(context)
            .load(url)
            .placeholder(R.drawable.ic_account_circle_24)
            .error(R.drawable.ic_account_circle_24)
            .into(this)
    }
}

fun String.showMessage(context: Context?) {
    if (this.isNullOrEmpty()) return
    Toast.makeText(context, this, Toast.LENGTH_SHORT).show()
}

fun Long.toDateString(dateFormat: Int = DateFormat.MEDIUM): String {
    val df = DateFormat.getDateInstance(dateFormat, Locale.getDefault())
    return df.format(this)
}

fun ImageView.loadImageByUrl(url: String?, context: Context?) {
    if (context != null) {
        Glide.with(context)
            .load(url)
            .placeholder(R.drawable.ic_base_image_24)
            .error(R.drawable.ic_base_image_24)
            .into(this)
    }
}

fun Long.toDateTime(): String {
    val date = Date(this)
    val format = SimpleDateFormat(TimeConstant.FORMAT_DATE_TIME)
    return format.format(date)
}

fun Long.toTime(): String {
    val date = Date(this)
    val format = SimpleDateFormat(TimeConstant.FORMAT_TIME)
    return format.format(date)
}
