package com.example.compoundeffectV1_01.data.alarm

import android.Manifest
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.example.compoundeffectV1_01.R
import com.example.compoundeffectV1_01.data.notification.PomodoroNotifications
import com.example.compoundeffectV1_01.data.notification.ReminderNotifications

class PomodoroAlarmReceiver : BroadcastReceiver() {

    private fun playAlarmSound(context: Context) {
        val notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val ringtone = RingtoneManager.getRingtone(context, notification)
        ringtone.play()
    }

    override fun onReceive(context: Context, intent: Intent) {


        Log.d(
            "PomodoroAlarm",
            "alarm received => type=${intent.getStringExtra(EXTRA_TYPE)} scheduleId=${intent.getIntExtra(EXTRA_SCHEDULE_ID, -1)}"
        )

        PomodoroNotifications.ensureChannel(context)

        if (
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)
            != PackageManager.PERMISSION_GRANTED
        ) {
            Log.d("PomodoroAlarm", "notification permission denied")
            return
        }

        val type = intent.getStringExtra(EXTRA_TYPE)

        val title = when (type) {
            TYPE_FOCUS_START -> "پومودو شروع شد"
            TYPE_FOCUS_END -> "پومودو تمام شد"
            TYPE_BREAK_END -> "استراحت تمام شد"
            else -> "پومودو"
        }

        val text = when (type) {
            TYPE_FOCUS_START -> "زمان تمرکز شروع شد."
            TYPE_FOCUS_END -> "زمان تمرکز تمام شد؛ وقت استراحت است."
            TYPE_BREAK_END -> "زمان استراحت تمام شد."
            else -> ""
        }

        val notification = NotificationCompat.Builder(context, PomodoroNotifications.CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(title)
//            .setContentText(text)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setVibrate(longArrayOf(0, 500, 300, 500))
            .setAutoCancel(true)
            .build()

        val scheduleId = intent.getIntExtra(EXTRA_SCHEDULE_ID, -1)
        val notificationId = 3000 + scheduleId

        context.getSystemService(NotificationManager::class.java)
            .notify(notificationId, notification)

//        playAlarmSound(context)

    }

    companion object {
        const val EXTRA_SCHEDULE_ID = "scheduleId"
        const val EXTRA_TYPE = "type"
        const val TYPE_FOCUS_START = "focus_start"

        const val TYPE_FOCUS_END = "focus_end"
        const val TYPE_BREAK_END = "break_end"
    }
}