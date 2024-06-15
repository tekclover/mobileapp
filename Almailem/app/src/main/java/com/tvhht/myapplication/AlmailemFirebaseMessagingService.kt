package com.tvhht.myapplication

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.tvhht.myapplication.home.HomePageActivity
import com.tvhht.myapplication.login.LoginActivity
import com.tvhht.myapplication.utils.PrefConstant
import com.tvhht.myapplication.utils.WMSApplication
import com.tvhht.myapplication.utils.WMSSharedPref

class AlmailemFirebaseMessagingService : FirebaseMessagingService() {
    override fun onNewToken(token: String) {
        super.onNewToken(token)
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        showNotification(message)
    }

    @SuppressLint("UnspecifiedImmutableFlag")
    private fun showNotification(remoteMessage: RemoteMessage) {
        try {
            remoteMessage.apply {
                this.notification?.let {
                    val isLoggedIn = WMSSharedPref.getAppPrefs(WMSApplication.getInstance().context)
                        .getBooleanValue(PrefConstant.LOGIN_STATUS)
                    val intent =
                        Intent(
                            this@AlmailemFirebaseMessagingService,
                            if (isLoggedIn) HomePageActivity::class.java else LoginActivity::class.java
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
                        this@AlmailemFirebaseMessagingService,
                        0,
                        intent,
                        pendingIntentFlags
                    )
                    val channelId = getString(R.string.notification_channel_id)
                    val channelName = getString(R.string.notification_channel_name)
                    val builder: NotificationCompat.Builder =
                        NotificationCompat.Builder(this@AlmailemFirebaseMessagingService, channelId)
                            .setSmallIcon(R.drawable.icon_login)
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