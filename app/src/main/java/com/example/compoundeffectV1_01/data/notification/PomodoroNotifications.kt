package com.example.compoundeffectV1_01.data.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.media.AudioAttributes
import android.os.Build

object PomodoroNotifications {

    const val CHANNEL_ID = "pomodoro_alarm_channel_v2"

    fun ensureChannel(context: Context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return

        val manager = context.getSystemService(NotificationManager::class.java)

        val existing = manager.getNotificationChannel(CHANNEL_ID)
        if (existing != null) return

        val channel = NotificationChannel(
            CHANNEL_ID,
            "Pomodoro Alarms",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Pomodoro focus and break alerts"
            enableVibration(true)
            setBypassDnd(false)

            val audioAttributes = AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_NOTIFICATION_EVENT)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build()

            setSound(
                android.provider.Settings.System.DEFAULT_NOTIFICATION_URI,
                audioAttributes
            )
        }

        manager.createNotificationChannel(channel)
    }
}