package com.example.compoundeffectV1_01.domain.pomodoro

import com.example.compoundeffectV1_01.data.alarm.PomodoroAlarmScheduler
import com.example.compoundeffectV1_01.domain.pomodoro.scheduler.PomodoroScheduler
import javax.inject.Inject

object PomodoroEngine {

    private var alarmScheduler: PomodoroAlarmScheduler? = null

    private var sessionIndex = 0
    private var isRunning = false

    fun init(alarmScheduler: PomodoroAlarmScheduler) {
        this.alarmScheduler = alarmScheduler
    }

    fun start() {
        if (isRunning) return

        isRunning = true
        sessionIndex = 0

        startWorkSession()
    }

    private fun startWorkSession() {
        alarmScheduler?.scheduleStart(0)
    }

    fun onSessionFinished() {
        if (!isRunning) return

        sessionIndex++

        val isBreak = sessionIndex % 2 == 1

        if (isBreak) {
            alarmScheduler?.scheduleEnd(0)
        } else {
            alarmScheduler?.scheduleStart(0)
        }
    }

    fun stop() {
        isRunning = false
        alarmScheduler?.cancelAll()
    }
}