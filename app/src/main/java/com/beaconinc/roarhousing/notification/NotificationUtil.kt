package com.beaconinc.roarhousing.notification

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.provider.MediaStore
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import coil.request.ImageRequest
import com.beaconinc.roarhousing.MainActivity
import com.beaconinc.roarhousing.R
import com.bumptech.glide.Glide
import com.google.firebase.messaging.RemoteMessage
import java.util.*

fun NotificationManager.sendLodgeNotification(
    remoteMessage: RemoteMessage, context: Context) {

    val contentIntent = Intent(context, MainActivity::class.java)
        .putExtra("notification","lodge_notifier")
        .putExtra("lodgeId", remoteMessage.data["id"])
        .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP + Intent.FLAG_ACTIVITY_SINGLE_TOP)

    val lodgeNotificationId = Random().nextInt()
    val contentPendingIntent = PendingIntent.getActivity(
        context,
        lodgeNotificationId,
        contentIntent,
        PendingIntent.FLAG_ONE_SHOT
    )

    val builder = NotificationCompat.Builder(
        context,
        context.getString(R.string.lodges_notification_channel_id)
    ).apply {
        setSmallIcon(R.drawable.ic_emma)
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
    notify(lodgeNotificationId,builder.build())
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

    val productNotificationId = Random().nextInt()
    val contentPendingIntent = PendingIntent.getActivity(
        context,
        productNotificationId,
        contentIntent,
        PendingIntent.FLAG_ONE_SHOT
    )

    val builder = NotificationCompat.Builder(
        context,
        context.getString(R.string.product_notification_channel_id)
    ).apply {
        setSmallIcon(R.drawable.ic_emma)
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
    notify(productNotificationId,builder.build())
}