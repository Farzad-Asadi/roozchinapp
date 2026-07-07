package ir.roozchinapp.domain.pomodoro


import ir.roozchinapp.data.alarm.PomodoroAlarmScheduler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow


private const val FOCUS_TIME = 25 * 60 * 1000L
private const val BREAK_TIME = 5 * 60 * 1000L


object PomodoroEngine {

    private var sessionStartTime: Long = 0L
    private var expectedEndTime: Long = 0L

    private var scheduler: PomodoroAlarmScheduler? = null

    private val _state = MutableStateFlow<PomodoroState>(PomodoroState.Idle)
    val state = _state.asStateFlow()

    private var sessionIndex = 0
    private var isRunning = false

    fun init(scheduler: PomodoroAlarmScheduler) {
        this.scheduler = scheduler
    }

    fun start() {
        if (isRunning) return

        isRunning = true
        sessionIndex = 0

        val duration = FOCUS_TIME

        sessionStartTime = System.currentTimeMillis()
        expectedEndTime = sessionStartTime + duration

        _state.value = PomodoroState.Running(
            sessionIndex = 0,
            isWork = true,
            remainingMillis = duration
        )

        scheduler?.schedule(
            type = "FOCUS_END",
            triggerAtMillis = expectedEndTime
        )
    }

    fun stop() {
        isRunning = false
        scheduler?.cancelAll()

        _state.value = PomodoroState.Idle
    }

    fun onAlarmTriggered() {

        if (!isRunning) return

        sessionIndex++

        val isFocus = sessionIndex % 2 == 0
        val duration = if (isFocus) FOCUS_TIME else BREAK_TIME

        sessionStartTime = System.currentTimeMillis()
        expectedEndTime = sessionStartTime + duration

        _state.value = PomodoroState.Running(
            sessionIndex = sessionIndex,
            isWork = isFocus,
            remainingMillis = duration
        )

        scheduler?.schedule(
            type = if (isFocus) "FOCUS_END" else "BREAK_END",
            triggerAtMillis = expectedEndTime
        )
    }

    private fun scheduleNext(isFocus: Boolean, triggerAt: Long) {
        scheduler?.schedule(
            type = if (isFocus) "FOCUS_END" else "BREAK_END",
            triggerAtMillis = triggerAt
        )
    }
}