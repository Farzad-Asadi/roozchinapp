package com.example.compoundeffectV1_01.data.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.compoundeffectV1_01.data.notification.PomodoroNotifications

class PomodoroAlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val type = intent.getStringExtra(EXTRA_TYPE).orEmpty()
        val scheduleId = intent.getIntExtra(EXTRA_SCHEDULE_ID, -1)
        val pomodoroTitle = intent.getStringExtra(EXTRA_TITLE)
            ?.takeIf { it.isNotBlank() }
            ?: "Pomodoro"

        Log.e(
            "POMODORO",
            "🔥 RECEIVER FIRED type=$type scheduleId=$scheduleId title=$pomodoroTitle"
        )

        val (title, message) = when (type) {
            TYPE_START_SOON -> {
                "شروع نزدیک است" to
                        "پومودوروی «$pomodoroTitle» کمتر از یک دقیقه دیگر شروع می‌شود."
            }

            TYPE_FOCUS_START -> {
                "پومودورو شروع شد" to
                        "زمان تمرکز «$pomodoroTitle» شروع شد."
            }

            TYPE_FOCUS_END -> {
                "تمرکز تمام شد" to
                        "تمرکز «$pomodoroTitle» تمام شد. وقت استراحت است."
            }

            TYPE_BREAK_END -> {
                "پومودورو تمام شد" to
                        "استراحت «$pomodoroTitle» تمام شد."
            }

            else -> {
                "Pomodoro" to "وضعیت پومودورو تغییر کرد."
            }
        }

        PomodoroNotifications.show(
            context = context,
            title = title,
            message = message
        )
    }

    companion object {
        const val EXTRA_SCHEDULE_ID = "scheduleId"
        const val EXTRA_TYPE = "type"
        const val EXTRA_TITLE = "title"

        const val TYPE_START_SOON = "START_SOON"
        const val TYPE_FOCUS_START = "FOCUS_START"
        const val TYPE_FOCUS_END = "FOCUS_END"
        const val TYPE_BREAK_END = "BREAK_END"
    }
}