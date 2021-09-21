package com.beaconinc.roarhousing.fcm

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.NotificationManager.*
import android.graphics.Color
import android.os.Build
import androidx.core.content.ContextCompat
import com.beaconinc.roarhousing.R
import com.beaconinc.roarhousing.notification.sendLodgeNotification
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import timber.log.Timber

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Timber.i("Cloud Token: $token")
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Timber.i("onMessage receive Called")

        val notificationManager = ContextCompat.getSystemService(
            applicationContext,
            NotificationManager::class.java,
        )
        createChannel( notificationManager!!,
            getString(R.string.lodges_notification_channel_id),
            getString(R.string.lodges_notification_channel_name)
        )

        Timber.i("message: ${remoteMessage.data["message"]},title: ${remoteMessage.data["title"]}, imageUrl: ${remoteMessage.data["imageUrl"]}")
        notificationManager.sendLodgeNotification(remoteMessage,this)
    }

    private fun createChannel(
        notificationManager: NotificationManager,
        channelId: String, channelName: String) {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel (
                channelId,
                channelName,
                IMPORTANCE_HIGH
            ).apply {
                setShowBadge(false)
            }
            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.RED
            notificationChannel.enableVibration(true)
            notificationChannel.description = "lodge update notification"

            notificationManager.createNotificationChannel(notificationChannel)
        }
    }

}