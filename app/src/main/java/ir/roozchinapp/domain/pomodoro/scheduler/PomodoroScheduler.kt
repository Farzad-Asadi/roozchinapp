package ir.roozchinapp.domain.pomodoro.scheduler

interface PomodoroScheduler {
    fun schedule(type: String, triggerAtMillis: Long)
    fun cancelAll()
}