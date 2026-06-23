package com.example.compoundeffectV1_01.data.alarm

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.compoundeffectV1_01.domain.pomodoro.scheduler.PomodoroScheduler
import dagger.hilt.android.qualifiers.ApplicationContext
import java.time.LocalDateTime
import java.time.ZoneId
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PomodoroAlarmScheduler @Inject constructor(
    @ApplicationContext
    private val context: Context
) : PomodoroScheduler {


    override fun schedule(type: String, triggerAtMillis: Long) {

        val intent = Intent(context, PomodoroAlarmReceiver::class.java).apply {
            putExtra(PomodoroAlarmReceiver.EXTRA_TYPE, type)
        }

        val requestCode = type.hashCode()

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val alarmManager = context.getSystemService(AlarmManager::class.java)

        alarmManager.setAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            triggerAtMillis,
            pendingIntent
        )
    }

    override fun cancelAll() {
        // cancel alarms
    }




}