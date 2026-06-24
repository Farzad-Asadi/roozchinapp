package com.example.compoundeffectV1_01

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import com.example.compoundeffectV1_01.data.modules.SeederEntryPoint
import com.example.compoundeffectV1_01.data.notification.ReminderNotifications
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import androidx.work.Configuration
import com.example.compoundeffectV1_01.data.notification.PomodoroNotifications
import javax.inject.Inject


@HiltAndroidApp
class CompoundEffectApplication : Application() {

    private val appScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)



    override fun onCreate() {
        super.onCreate()

        ReminderNotifications.ensureChannel(this)
        PomodoroNotifications.ensureChannel(this)

        ReminderNotifications.ensureChannel(this)

        val entryPoint = EntryPointAccessors.fromApplication(
            this,
            SeederEntryPoint::class.java
        )

        val seeder = entryPoint.seeder()

        appScope.launch {
            seeder.seedIfNeeded()
        }
    }
}