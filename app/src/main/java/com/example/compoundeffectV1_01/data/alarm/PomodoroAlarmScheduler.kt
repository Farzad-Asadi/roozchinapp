package com.example.compoundeffectV1_01.data.alarm

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import com.example.compoundeffectV1_01.domain.pomodoro.scheduler.PomodoroScheduler
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.abs

@Singleton
class PomodoroAlarmScheduler @Inject constructor(
    @ApplicationContext
    private val context: Context
) : PomodoroScheduler {

    private val activeRequestCodes = mutableSetOf<Int>()

    private fun requestCodeFor(scheduleId: Int, type: String, triggerAtMillis: Long): Int {
        if (scheduleId <= 0) {
            return (triggerAtMillis % Int.MAX_VALUE).toInt()
        }

        val typeCode = when (type) {
            PomodoroAlarmReceiver.TYPE_START_SOON -> 1
            PomodoroAlarmReceiver.TYPE_FOCUS_START -> 2
            PomodoroAlarmReceiver.TYPE_FOCUS_END -> 3
            PomodoroAlarmReceiver.TYPE_BREAK_END -> 4
            else -> 9
        }

        return abs((scheduleId * 10) + typeCode)
    }

    private fun pomodoroIntent(
        scheduleId: Int,
        title: String,
        type: String
    ): Intent {
        return Intent(context, PomodoroAlarmReceiver::class.java).apply {
            action = "com.example.compoundeffect.POMODORO_ALARM"
            putExtra(PomodoroAlarmReceiver.EXTRA_SCHEDULE_ID, scheduleId)
            putExtra(PomodoroAlarmReceiver.EXTRA_TITLE, title)
            putExtra(PomodoroAlarmReceiver.EXTRA_TYPE, type)
        }
    }

    @SuppressLint("ScheduleExactAlarm")
    fun schedulePomodoroEvent(
        scheduleId: Int,
        title: String,
        type: String,
        triggerAtMillis: Long
    ) {
        val alarmManager = context.getSystemService(AlarmManager::class.java)

        Log.e(
            "ALARM_DEBUG",
            "canScheduleExact=${if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) alarmManager.canScheduleExactAlarms() else true}"
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (!alarmManager.canScheduleExactAlarms()) {
                Log.e("ALARM_DEBUG", "❌ EXACT ALARM NOT GRANTED")
                return
            }
        }

        val safeTriggerAtMillis =
            if (triggerAtMillis <= System.currentTimeMillis()) {
                System.currentTimeMillis() + 1_000L
            } else {
                triggerAtMillis
            }

        val requestCode = requestCodeFor(
            scheduleId = scheduleId,
            type = type,
            triggerAtMillis = safeTriggerAtMillis
        )

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            requestCode,
            pomodoroIntent(scheduleId, title, type),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        activeRequestCodes += requestCode

        Log.e(
            "ALARM_DEBUG",
            "⏰ set pomodoro alarm type=$type scheduleId=$scheduleId at=$safeTriggerAtMillis"
        )

        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            safeTriggerAtMillis,
            pendingIntent
        )
    }

    // برای سازگاری با کدهای قبلی که هنوز schedule(type, millis) صدا می‌زنند.
    override fun schedule(type: String, triggerAtMillis: Long) {
        schedulePomodoroEvent(
            scheduleId = -1,
            title = "Pomodoro",
            type = type,
            triggerAtMillis = triggerAtMillis
        )
    }

    fun cancelPomodoroEvents(scheduleId: Int) {
        val alarmManager = context.getSystemService(AlarmManager::class.java)

        val types = listOf(
            PomodoroAlarmReceiver.TYPE_START_SOON,
            PomodoroAlarmReceiver.TYPE_FOCUS_START,
            PomodoroAlarmReceiver.TYPE_FOCUS_END,
            PomodoroAlarmReceiver.TYPE_BREAK_END
        )

        types.forEach { type ->
            val requestCode = requestCodeFor(
                scheduleId = scheduleId,
                type = type,
                triggerAtMillis = 0L
            )

            val pendingIntent = PendingIntent.getBroadcast(
                context,
                requestCode,
                pomodoroIntent(scheduleId, "Pomodoro", type),
                PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
            )

            if (pendingIntent != null) {
                alarmManager.cancel(pendingIntent)
                pendingIntent.cancel()
            }

            activeRequestCodes.remove(requestCode)
        }

        Log.e("ALARM_DEBUG", "🛑 cancel pomodoro events scheduleId=$scheduleId")
    }

    override fun cancelAll() {
        val alarmManager = context.getSystemService(AlarmManager::class.java)

        activeRequestCodes.forEach { requestCode ->
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                requestCode,
                Intent(context, PomodoroAlarmReceiver::class.java).apply {
                    action = "com.example.compoundeffect.POMODORO_ALARM"
                },
                PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
            )

            if (pendingIntent != null) {
                alarmManager.cancel(pendingIntent)
                pendingIntent.cancel()
            }
        }

        Log.e("ALARM_DEBUG", "🛑 cancelAll pomodoro alarms count=${activeRequestCodes.size}")

        activeRequestCodes.clear()
    }

    fun cancelPomodoroEvent(
        scheduleId: Int,
        type: String
    ) {
        val alarmManager = context.getSystemService(AlarmManager::class.java)

        val requestCode = requestCodeFor(
            scheduleId = scheduleId,
            type = type,
            triggerAtMillis = 0L
        )

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            requestCode,
            Intent(context, PomodoroAlarmReceiver::class.java).apply {
                action = "com.example.compoundeffect.POMODORO_ALARM"
                putExtra(PomodoroAlarmReceiver.EXTRA_SCHEDULE_ID, scheduleId)
                putExtra(PomodoroAlarmReceiver.EXTRA_TYPE, type)
            },
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        )

        if (pendingIntent != null) {
            alarmManager.cancel(pendingIntent)
            pendingIntent.cancel()
        }

        activeRequestCodes.remove(requestCode)
    }
}