package com.example.zajil.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import android.util.Log
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import com.example.zajil.R
import com.example.zajil.activities.HomeActivity
import com.example.zajil.util.App
import com.example.zajil.util.Commons.printData
import com.google.android.datatransport.runtime.firebase.transport.LogEventDropped
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlin.random.Random


class FirebasePushService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        App.preferenceManager.deviceToken = token
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        if (message.data.isNotEmpty()) {
            message.data.printData()
            showNotification(
                message.data["title"],
                message.data["message"]
            )
        } else if (message.notification != null) {
            showNotification(
                message.notification?.title ?: "",
                message.notification?.body ?: ""
            )
        }
    }

    private fun showNotification(
        title: String?,
        message: String?
    ) {
        val intent = Intent(this, HomeActivity::class.java)
        val channel_id = "ZajelRiderChannel"
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(
            this, Random.nextInt(), intent,
            PendingIntent.FLAG_MUTABLE
        )
        val builder: NotificationCompat.Builder = NotificationCompat.Builder(
            applicationContext,
            channel_id
        )
            .setContentTitle(title)
            .setContentText(message)
            .setSmallIcon(com.example.zajil.R.mipmap.ic_launcher_round)
            .setAutoCancel(true)
            .setVibrate(
                longArrayOf(
                    1000, 1000, 1000,
                    1000, 1000
                )
            )
            .setOnlyAlertOnce(true)
            .setContentIntent(pendingIntent)

        val notificationManager = getSystemService(
            NOTIFICATION_SERVICE
        ) as NotificationManager

        if (Build.VERSION.SDK_INT
            >= Build.VERSION_CODES.O
        ) {
            val notificationChannel = NotificationChannel(
                channel_id, "RideDetails",
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(
                notificationChannel
            )
        }

        notificationManager.notify(0, builder.build())
    }

    /* private fun getCustomDesign(
         title: String,
         message: String
     ): RemoteViews? {
         val remoteViews = RemoteViews(
             applicationContext.packageName,
             com.example.zajil.R.layout.notification
         )
         remoteViews.setTextViewText(R.id.title, title)
         remoteViews.setTextViewText(R.id.message, message)
         remoteViews.setImageViewResource(
             R.id.icon,
             R.drawable.gfg
         )
         return remoteViews
     }*/

}