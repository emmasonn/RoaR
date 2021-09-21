package com.beaconinc.roarhousing.notification.data

import android.graphics.Bitmap

data class NotificationData (
    val id: String,
    val title: String,
    val message: String,
    val imageBitmap: String?
    )

data class PushNotification (
    val data: NotificationData,
    val to: String
    )