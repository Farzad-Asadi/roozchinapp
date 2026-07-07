package ir.roozchinapp.data.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import ir.roozchinapp.data.notification.PomodoroNotifications

class PomodoroAlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val type = intent.getStringExtra(EXTRA_TYPE).orEmpty()
        val scheduleId = intent.getIntExtra(EXTRA_SCHEDULE_ID, -1)
        val triggerAtMillis = intent.getLongExtra(EXTRA_TRIGGER_AT_MILLIS, -1L)

        val delayMs =
            if (triggerAtMillis > 0L) {
                System.currentTimeMillis() - triggerAtMillis
            } else {
                null
            }
        val pomodoroTitle = intent.getStringExtra(EXTRA_TITLE)
            ?.takeIf { it.isNotBlank() }
            ?: "Pomodoro"

        Log.e(
            "POMODORO",
            "🔥 RECEIVER FIRED type=$type scheduleId=$scheduleId title=$pomodoroTitle delayMs=$delayMs"
        )

        val (title, message) = when (type) {
            TYPE_START_SOON -> {
                "پومودوروی «$pomodoroTitle" to
                        "  کمتر از یک دقیقه دیگر شروع می‌شود."
            }

            TYPE_FOCUS_START -> {
                "پومودوروی «$pomodoroTitle" to
                        "زمان تمرکز شروع شد."
            }

            TYPE_FOCUS_END -> {
                "پومودوروی «$pomodoroTitle" to
                        "تمرکز تمام شد. وقت استراحت است."
            }

            TYPE_BREAK_END -> {
                "پومودوروی «$pomodoroTitle" to
                        "استراحت به زودی تمام می شود."
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

        const val EXTRA_TRIGGER_AT_MILLIS = "triggerAtMillis"
    }
}