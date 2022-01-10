package com.column.roar.notification

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.column.roar.MainActivity
import com.column.roar.R
import com.bumptech.glide.Glide
import com.google.firebase.messaging.RemoteMessage
import java.util.*

const val LODGE_NOTIFICATION_ID = 10323
const val PRODUCT_NOTIFICATION_ID = 2329
fun NotificationManager.sendLodgeNotification(
    remoteMessage: RemoteMessage, context: Context) {

    val contentIntent = Intent(context, MainActivity::class.java)
        .putExtra("notification","lodge_notifier")
        .putExtra("lodgeId", remoteMessage.data["id"])
        .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP + Intent.FLAG_ACTIVITY_SINGLE_TOP)

//    val lodgeNotificationId = Random().nextInt()
    val contentPendingIntent = PendingIntent.getActivity(
        context,
        LODGE_NOTIFICATION_ID,
        contentIntent,
        PendingIntent.FLAG_ONE_SHOT
    )

    val builder = NotificationCompat.Builder(
        context,
        context.getString(R.string.lodges_notification_channel_id)
    ).apply {
        setSmallIcon(R.drawable.ic_emma_icon)
        color = ContextCompat.getColor(context, R.color.blue_100)

        val imageUrl = remoteMessage.data["imageBitmap"]
        val futureTarget = Glide.with(context)
            .asBitmap()
            .load(imageUrl)
            .submit()

        val bitmapImage = futureTarget.get()

        setLargeIcon(bitmapImage)
        setStyle(NotificationCompat.BigPictureStyle()
            .bigPicture(bitmapImage)
            .bigLargeIcon(null))

        setContentTitle(remoteMessage.data["title"])
        setContentText(remoteMessage.data["message"])
        setContentIntent(contentPendingIntent)
        setAutoCancel(true)
        addAction(R.drawable.ic_forward,"View Lodge", contentPendingIntent)
        priority = NotificationCompat.PRIORITY_HIGH
    }
    notify(LODGE_NOTIFICATION_ID,builder.build())
}

fun NotificationManager.cancelAllNotification() {
    cancelAll()
}


fun NotificationManager.sendProductNotification(
    remoteMessage: RemoteMessage, context: Context) {

    val contentIntent = Intent(context, MainActivity::class.java)
        .putExtra("notification","product_notifier")
        .putExtra("productId", remoteMessage.data["id"])
        .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP + Intent.FLAG_ACTIVITY_SINGLE_TOP)

//    val productNotificationId = Random().nextInt()

    val contentPendingIntent = PendingIntent.getActivity(
        context,
        PRODUCT_NOTIFICATION_ID,
        contentIntent,
        PendingIntent.FLAG_ONE_SHOT
    )

    val builder = NotificationCompat.Builder(
        context,
        context.getString(R.string.product_notification_channel_id)
    ).apply {
        setSmallIcon(R.drawable.ic_emma_icon)
        color = ContextCompat.getColor(context, R.color.blue_100)

        val imageUrl = remoteMessage.data["imageBitmap"]
        val futureTarget = Glide.with(context)
            .asBitmap()
            .load(imageUrl)
            .submit()

        val bitmapImage = futureTarget.get()

        setLargeIcon(bitmapImage)
        setStyle(NotificationCompat.BigPictureStyle()
            .bigPicture(bitmapImage)
            .bigLargeIcon(null))

        setContentTitle(remoteMessage.data["title"])
        setContentText(remoteMessage.data["message"])
        setContentIntent(contentPendingIntent)
        setAutoCancel(true)
        addAction(R.drawable.ic_forward,"View Product", contentPendingIntent)
        priority = NotificationCompat.PRIORITY_HIGH
    }
    notify(PRODUCT_NOTIFICATION_ID,builder.build())
}