package com.example.compoundeffectV1_01.data.alarm

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import java.time.LocalDateTime
import java.time.ZoneId
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PomodoroAlarmScheduler @Inject constructor(
    @ApplicationContext private val context: Context
) {


    fun scheduleFocusStart(scheduleId: Int, triggerAt: LocalDateTime) {
        schedule(
            scheduleId = scheduleId,
            type = PomodoroAlarmReceiver.TYPE_FOCUS_START,
            requestCode = scheduleId * 10,
            triggerAt = triggerAt
        )
    }


    fun scheduleFocusEnd(scheduleId: Int, triggerAt: LocalDateTime) {
        schedule(
            scheduleId = scheduleId,
            type = PomodoroAlarmReceiver.TYPE_FOCUS_END,
            requestCode = scheduleId * 10 + 1,
            triggerAt = triggerAt
        )
    }

    fun scheduleBreakEnd(scheduleId: Int, triggerAt: LocalDateTime) {
        schedule(
            scheduleId = scheduleId,
            type = PomodoroAlarmReceiver.TYPE_BREAK_END,
            requestCode = scheduleId * 10 + 2,
            triggerAt = triggerAt
        )
    }

    @SuppressLint("ScheduleExactAlarm")
    private fun schedule(
        scheduleId: Int,
        type: String,
        requestCode: Int,
        triggerAt: LocalDateTime
    ) {
        val intent = Intent(context, PomodoroAlarmReceiver::class.java).apply {
            action = "POMODORO_ALARM_${scheduleId}_$type"
            putExtra(PomodoroAlarmReceiver.EXTRA_SCHEDULE_ID, scheduleId)
            putExtra(PomodoroAlarmReceiver.EXTRA_TYPE, type)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val triggerMillis = triggerAt
            .atZone(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()

        val alarmManager = context.getSystemService(AlarmManager::class.java)

        Log.d(
            "PomodoroAlarm",
            "schedule alarm => scheduleId=$scheduleId type=$type triggerMillis=$triggerMillis triggerAt=$triggerAt"
        )

        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            triggerMillis,
            pendingIntent
        )
    }

    fun cancelPomodoroAlarms(scheduleId: Int) {
        cancel(scheduleId, PomodoroAlarmReceiver.TYPE_FOCUS_START, scheduleId * 10)
        cancel(scheduleId, PomodoroAlarmReceiver.TYPE_FOCUS_END, scheduleId * 10 + 1)
        cancel(scheduleId, PomodoroAlarmReceiver.TYPE_BREAK_END, scheduleId * 10 + 2)
    }

    private fun cancel(scheduleId: Int, type: String, requestCode: Int) {
        val intent = Intent(context, PomodoroAlarmReceiver::class.java).apply {
            action = "POMODORO_ALARM_${scheduleId}_$type"
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        )

        if (pendingIntent != null) {
            val alarmManager = context.getSystemService(AlarmManager::class.java)
            alarmManager.cancel(pendingIntent)
            pendingIntent.cancel()
        }
    }
}