package com.example.compoundeffectV1_01.domain.pomodoro

sealed class PomodoroState {

    data object Idle : PomodoroState()

    data class Running(
        val sessionIndex: Int,
        val isWork: Boolean,
        val remainingMillis: Long
    ) : PomodoroState()

    data object Paused : PomodoroState()

    data object Finished : PomodoroState()
}