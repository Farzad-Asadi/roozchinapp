package com.example.compoundeffectV1_01.domain.pomodoro.scheduler

interface PomodoroScheduler {

    fun scheduleStart(delayMillis: Long)

    fun scheduleEnd(delayMillis: Long)

    fun cancelAll()
}