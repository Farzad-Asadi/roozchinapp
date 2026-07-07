package ir.roozchinapp.data.notification

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import ir.roozchinapp.R


object PomodoroNotifications {

    const val CHANNEL_ID = "pomodoro_channel"

    // ✅ همه نوتیف‌های پومودورو با همین ID می‌آیند؛ پس قبلی جایگزین می‌شود.
    private const val NOTIFICATION_ID = 2401

    fun ensureChannel(context: Context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return

        val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val channel = NotificationChannel(
            CHANNEL_ID,
            "Pomodoro",
            NotificationManager.IMPORTANCE_HIGH
        )

        nm.createNotificationChannel(channel)
    }

    fun show(context: Context, title: String, message: String) {
        Log.d("POMODORO", "NOTIFY CALLED title=$title message=$message")

        if (
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Log.e("POMODORO", "POST_NOTIFICATIONS not granted")
            return
        }

        ensureChannel(context)

        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setSmallIcon(R.drawable.ic_notification)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        // ✅ ثابت بودن ID یعنی نوتیف جدید، قبلی را replace می‌کند.
        notificationManager.notify(NOTIFICATION_ID, notification)
    }

    fun clear(context: Context) {
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        notificationManager.cancel(NOTIFICATION_ID)
    }
}