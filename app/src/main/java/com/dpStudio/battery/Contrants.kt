package com.dpStudio.battery

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.appcompat.app.AppCompatActivity

object Contrants {
    val CHANNEL_ID_BATTERY_MONITOR = "chanel_id_monitor"

    public fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = context.getString(R.string.notification)
            val descriptionText = context.getString(R.string.notification_description)
            val importance = NotificationManager.IMPORTANCE_LOW
            val channel = NotificationChannel( CHANNEL_ID_BATTERY_MONITOR, name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                    context.getSystemService(AppCompatActivity.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}
