package ir.roozchinapp

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import ir.roozchinapp.data.notification.PomodoroNotifications
import ir.roozchinapp.data.notification.ReminderNotifications


@HiltAndroidApp
class RoozChinApplication : Application() {


    override fun onCreate() {
        super.onCreate()

        ReminderNotifications.ensureChannel(this)
        PomodoroNotifications.ensureChannel(this)

    }
}