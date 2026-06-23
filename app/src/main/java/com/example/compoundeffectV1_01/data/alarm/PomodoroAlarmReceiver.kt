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
import com.example.compoundeffectV1_01.domain.pomodoro.PomodoroEngine

class PomodoroAlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {

        Log.d("POMODORO_TEST", "RECEIVER FIRED")
        PomodoroEngine.onAlarmTriggered()
        Log.d("POMODORO_TEST", "ENGINE CALLED")
        PomodoroNotifications.show(
            context,
            title = "Pomodoro",
            message = "Session changed"
        )
    }

    companion object {
        const val EXTRA_SCHEDULE_ID = "scheduleId"
        const val EXTRA_TYPE = "type"

        const val TYPE_FOCUS_START = "focus_start"
        const val TYPE_FOCUS_END = "focus_end"
        const val TYPE_BREAK_END = "break_end"
    }



}