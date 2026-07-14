package ir.roozchinapp.data.dataBaseRoom.tables.taskSchedule

data class PomodoroAnalyticsOccurrenceRow(
    val taskId: Int,
    val dateEpochDay: Long,
    val inPallet: Boolean,
    val focusMinutes: Int?,
    val pomodoroFocusDoneApplied: Boolean
)