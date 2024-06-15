package com.clara.timekeeping.ui.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.core.app.NotificationCompat
import com.clara.timekeeping.R
import com.clara.timekeeping.ui.summary.TimeTicketSummaryActivity
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class TimekeepingFirebaseMessagingService : FirebaseMessagingService() {
    override fun onNewToken(token: String) {
        super.onNewToken(token)
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        showNotification(message)
    }

    private fun showNotification(remoteMessage: RemoteMessage) {
        try {
            remoteMessage.apply {
                this.notification?.let {
                    val intent =
                        Intent(
                            this@TimekeepingFirebaseMessagingService,
                            TimeTicketSummaryActivity::class.java
                        )
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    val bundle = Bundle()
                    bundle.putString("google.message_id", this.messageId ?: "")
                    intent.putExtras(bundle)
                    val pendingIntentFlags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                    } else {
                        PendingIntent.FLAG_UPDATE_CURRENT
                    }
                    val pendingIntent: PendingIntent = PendingIntent.getActivity(
                        this@TimekeepingFirebaseMessagingService,
                        0,
                        intent,
                        pendingIntentFlags
                    )
                    val channelId = getString(R.string.notification_channel_id)
                    val channelName = getString(R.string.notification_channel_name)
                    val builder: NotificationCompat.Builder =
                        NotificationCompat.Builder(
                            this@TimekeepingFirebaseMessagingService,
                            channelId
                        )
                            .setSmallIcon(R.mipmap.ic_launcher)
                            .setContentTitle(it.title ?: "")
                            .setContentText(it.body ?: "")
                            .setDefaults(NotificationCompat.DEFAULT_ALL)
                            .setPriority(NotificationCompat.PRIORITY_MAX)
                            .setAutoCancel(true)
                            .setContentIntent(pendingIntent)

                    val notificationManager =
                        getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        val channel = NotificationChannel(
                            channelId,
                            channelName,
                            NotificationManager.IMPORTANCE_HIGH
                        )
                        notificationManager.createNotificationChannel(channel)
                    }
                    notificationManager.notify(0, builder.build())
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}