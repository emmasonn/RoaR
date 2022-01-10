package com.column.roar.notification.data

import androidx.annotation.Keep

@Keep
data class NotificationData (
    val id: String,
    val title: String,
    val message: String,
    val itemType: String,
    val imageBitmap: String?
    )

@Keep
data class PushNotification (
    val data: NotificationData,
    val to: String
    )