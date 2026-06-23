package com.example.compoundeffectV1_01.domain.pomodoro.scheduler

interface PomodoroScheduler {
    fun schedule(type: String, triggerAtMillis: Long)
    fun cancelAll()
}