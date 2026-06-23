package com.example.compoundeffectV1_01.ui.scheduleScreen


import android.util.Log
import androidx.compose.ui.geometry.Offset
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.compoundeffectV1_01.data.alarm.PomodoroAlarmScheduler
import com.example.compoundeffectV1_01.data.dataBaseRoom.tables.reminder.TaskReminderRepository
import com.example.compoundeffectV1_01.data.dataBaseRoom.tables.task.TaskMode
import com.example.compoundeffectV1_01.data.dataBaseRoom.tables.task.TaskRepository
import com.example.compoundeffectV1_01.data.dataBaseRoom.tables.taskSchedule.RepeatUnit
import com.example.compoundeffectV1_01.data.dataBaseRoom.tables.taskSchedule.ScheduleMode
import com.example.compoundeffectV1_01.data.dataBaseRoom.tables.taskSchedule.TaskSchedule
import com.example.compoundeffectV1_01.data.dataBaseRoom.tables.taskSchedule.TaskScheduleRepository
import com.example.compoundeffectV1_01.data.workManager.ReminderScheduler
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import javax.inject.Inject

@HiltViewModel
class ScheduleScreenViewModel @Inject constructor(
    private val taskRepo: TaskRepository,
    private val taskScheduleRepo: TaskScheduleRepository,
    private val reminderScheduler: ReminderScheduler,
    private val reminderRepo: TaskReminderRepository,
    private val pomodoroAlarmScheduler: PomodoroAlarmScheduler,
) : ViewModel() {

    private var runningPomodoroJob: Job? = null

    private val _runningPomodoro = MutableStateFlow<RunningPomodoroUiState?>(null)
    val runningPomodoro = _runningPomodoro.asStateFlow()



    val allItems: StateFlow<List<ScheduleScreenItem>> =
        taskScheduleRepo.observeAllSchedulesWithTask()
            .map { rows ->
                rows.mapNotNull { r ->
                    val date = r.s_dateEpochDay?.let(LocalDate::ofEpochDay) ?: return@mapNotNull null
                    val s = r.s_startMinuteOfDay ?: return@mapNotNull null
                    val e = r.s_endMinuteOfDay ?: return@mapNotNull null
                    if (e <= s) return@mapNotNull null

                    val start = date.atStartOfDay().plusMinutes(s.toLong())
                    val end   = date.atStartOfDay().plusMinutes(e.toLong())

                    ScheduleScreenItem(
                        scheduleId = r.s_id,
                        taskId = r.t_id,
                        title = r.t_name,
                        mode = r.s_mode,

                        dateEpochDay = r.s_dateEpochDay,
                        start = start,
                        end = end,

                        durationMinutes = r.s_durationMinutes,

                        inPallet = r.s_inPallet,
                        repeating = r.s_repeating,

                        categoryName = r.c_name,
                        categoryIconName = r.c_iconName,
                        categoryColor = r.c_color,
                        taskMode = r.t_taskMode,
                        pomodoroTargetUnits = r.t_pomodoroTargetUnits      ,
                        pomodoroDoneUnits =r.t_pomodoroDoneUnits,
                        repeatInterval = r.s_repeatInterval,
                        repeatUnit = r.s_repeatUnit,
                        weekdaysMask = r.s_weekdaysMask,
                        focusMinutes = r.s_focusMinutes,
                        shortBreakMinutes = r.s_shortBreakMinutes,
                        longBreakMinutes = r.s_longBreakMinutes,
                        longBreakEvery = r.s_longBreakEvery,
                        pomodoroUnitsPerDay = r.s_pomodoroUnitsPerDay,
                        parentRuleScheduleId = r.s_parentRuleScheduleId,
                        occurrenceDateEpochDay = r.s_occurrenceDateEpochDay,
                    )
                }
            }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())




    //common


    fun deleteScheduleById(scheduleId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            taskScheduleRepo.deleteById(scheduleId)
            val reminders = reminderRepo.getByScheduleId(scheduleId)
            reminders.forEach { rUi ->
                try {
                    reminderScheduler.cancel(rUi.id)   // ✅ این خط حیاتی است
                } catch (_: Throwable) {}
            }
        }
    }
    fun materializeVirtual(
        virtual: ScheduleScreenItem,
        newDate: LocalDate,
        newStartMin: Int,
        newEndMin: Int,
        inPallet: Boolean = false
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val ruleId = virtual.parentRuleScheduleId ?: return@launch
            val occDay = virtual.occurrenceDateEpochDay ?: return@launch

            // اینجا باید بسته به ساختار Entity شما پر بشه:
            val entity = TaskSchedule(
                // id اگر auto است null بزن
                id = null, // یا null (طبق Entity خودت)
                taskId = virtual.taskId,
                mode = virtual.mode,

                dateEpochDay = newDate.toEpochDay(),
                startMinuteOfDay = newStartMin,
                endMinuteOfDay = newEndMin,

                inPallet = inPallet,
                repeating = false,

                parentRuleScheduleId = ruleId,
                occurrenceDateEpochDay = occDay,
            )

            taskScheduleRepo.insert(entity)
        }
    }

    fun startPomodoroNow(scheduleId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val schedule = taskScheduleRepo.getById(scheduleId) ?: return@launch
            if (schedule.mode != ScheduleMode.POMODORO) return@launch

            val focus = schedule.focusMinutes ?: return@launch
            val shortBreak = schedule.shortBreakMinutes ?: 0

            val rawNow = LocalDateTime.now()

            val now = if (rawNow.second > 0 || rawNow.nano > 0) {
                rawNow.plusMinutes(1).withSecond(0).withNano(0)
            } else {
                rawNow.withSecond(0).withNano(0)
            }

            val date = now.toLocalDate()
            val startMin = now.hour * 60 + now.minute
            val endMin = (startMin + focus + shortBreak).coerceAtMost(24 * 60)

            taskScheduleRepo.updatePomodoroTimeRange(
                scheduleId = scheduleId,
                date = date,
                startMin = startMin,
                endMin = endMin
            )

            val focusEnd = now.plusMinutes(focus.toLong())
            val breakEnd = now.plusMinutes((focus + shortBreak).toLong())


            pomodoroAlarmScheduler.schedule(
                type = "FOCUS_END",
                triggerAtMillis = focusEnd.atZone(java.time.ZoneId.systemDefault())
                    .toInstant()
                    .toEpochMilli()
            )

            pomodoroAlarmScheduler.schedule(
                type = "BREAK_END",
                triggerAtMillis = breakEnd.atZone(java.time.ZoneId.systemDefault())
                    .toInstant()
                    .toEpochMilli()
            )

            pomodoroAlarmScheduler.schedule(
                type = "FOCUS_START",
                triggerAtMillis = now.atZone(java.time.ZoneId.systemDefault())
                    .toInstant()
                    .toEpochMilli()
            )


            startRunningPomodoroTimer(
                schedule = schedule,
                clickedAt = rawNow,
                realStartAt = now,
                focusMinutes = focus,
                shortBreakMinutes = shortBreak
            )
        }
    }

    private fun startRunningPomodoroTimer(
        schedule: TaskSchedule,
        clickedAt: LocalDateTime,
        realStartAt: LocalDateTime,
        focusMinutes: Int,
        shortBreakMinutes: Int
    ) {
        runningPomodoroJob?.cancel()

        val focusEndAt = realStartAt.plusMinutes(focusMinutes.toLong())
        val breakEndAt = focusEndAt.plusMinutes(shortBreakMinutes.toLong())

        runningPomodoroJob = viewModelScope.launch {
            while (true) {
                val now = LocalDateTime.now()

                val phase = when {
                    now.isBefore(realStartAt) -> PomodoroRunPhase.WAITING_TO_START
                    now.isBefore(focusEndAt) -> PomodoroRunPhase.FOCUS
                    now.isBefore(breakEndAt) -> PomodoroRunPhase.BREAK
                    else -> PomodoroRunPhase.FINISHED
                }

                val waitingSeconds = if (now.isBefore(realStartAt)) {
                    Duration.between(now, realStartAt).seconds
                        .coerceAtLeast(0)
                } else {
                    0
                }

                val focusElapsed = java.time.Duration.between(realStartAt, now)
                    .seconds
                    .coerceIn(0, focusMinutes * 60L)

                val breakElapsed = java.time.Duration.between(focusEndAt, now)
                    .seconds
                    .coerceIn(0, shortBreakMinutes * 60L)

                _runningPomodoro.value = RunningPomodoroUiState(
                    scheduleId = schedule.id ?: return@launch,
                    taskId = schedule.taskId,
                    title = schedule.title ?: "Pomodoro",
                    clickedAt = clickedAt,
                    realStartAt = realStartAt,
                    focusEndAt = focusEndAt,
                    breakEndAt = breakEndAt,
                    phase = phase,
                    waitingSeconds = waitingSeconds,
                    focusElapsedSeconds = focusElapsed,
                    breakElapsedSeconds = breakElapsed
                )

                if (phase == PomodoroRunPhase.FINISHED) {
                    delay(3_000)
                    _runningPomodoro.value = null
                    break
                }

                delay(1_000)
            }
        }
    }




    //only Schedule TIME_RANGE

    fun resizeScheduleEnd(scheduleId: Int, newEnd: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            taskScheduleRepo.updateEndMinute(scheduleId, newEnd)

            val reminders = reminderRepo.getByScheduleId(scheduleId)
            reminders.forEach { rUi ->
                try {
                    reminderScheduler.reschedule(rUi.id)   // ✅ این خط حیاتی است
                } catch (_: Throwable) {}
            }
        }
    }
    fun resizeScheduleStart(scheduleId: Int, newStart: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            taskScheduleRepo.updateStartMinute(scheduleId, newStart)

            val reminders = reminderRepo.getByScheduleId(scheduleId)
            reminders.forEach { rUi ->
                try {
                    reminderScheduler.reschedule(rUi.id)   // ✅ این خط حیاتی است
                } catch (_: Throwable) {}
            }
        }
    }
    fun moveSchedule(scheduleId: Int, newDate: LocalDate, newStart: Int, newEnd: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            taskScheduleRepo.updateTimeRange(scheduleId, newDate, newStart, newEnd)


            val reminders = reminderRepo.getByScheduleId(scheduleId)
            reminders.forEach { rUi ->
                try {
                    reminderScheduler.reschedule(rUi.id)   // ✅ این خط حیاتی است
                } catch (_: Throwable) {}
            }

        }

    }
    fun moveScheduleFromTimeLineToPallet(scheduleId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val reminders = reminderRepo.getByScheduleId(scheduleId)
            reminders.forEach { rUi ->
                try {
                    reminderScheduler.cancel(rUi.id)   // ✅ این خط حیاتی است
                } catch (_: Throwable) {}
            }
            val schedule=taskScheduleRepo.getById(scheduleId)
            taskScheduleRepo.setSchedulePalletState(scheduleId, true)
        }
    }
    fun dropScheduleFromPalletToTimeLine(
        scheduleId: Int,
        date: LocalDate,
        startMin: Int,
        endMin: Int
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val reminders = reminderRepo.getByScheduleId(scheduleId)
            reminders.forEach { rUi ->
                try {
                    reminderScheduler.reschedule(rUi.id)   // ✅ این خط حیاتی است
                } catch (_: Throwable) {}
            }
            taskScheduleRepo.dropFromPalletToTimeline(scheduleId, date, startMin, endMin)
        }
    }




    //only Schedule  POMODORO

    suspend fun insertOnePomodoroTimelineItem(
        taskId: Int,
        scheduleId: Int,
        date: LocalDate,
        startMin: Int,
        focus: Int,
        shortBreak: Int,
    ): Int? {
        val end = (startMin + focus + shortBreak).coerceAtMost(24 * 60)

        val parentSchedule = taskScheduleRepo.getById(scheduleId)

        val childSchedule = parentSchedule?.copy(
            id = null,
            inPallet = false,
            repeating = false,
            dateEpochDay = date.toEpochDay(),
            startMinuteOfDay = startMin,
            endMinuteOfDay = end,
            focusMinutes = focus,
            shortBreakMinutes = shortBreak,
            pomodoroParentId = scheduleId
        )

        val newScheduleChildId = childSchedule?.let { taskScheduleRepo.insert(it) }


        val reminders = reminderRepo.getByScheduleId(scheduleId)
        reminders.forEach { rUi ->
            try {
                val newReminder = newScheduleChildId?.let { rUi.copy(id = null ,scheduleId = it) }
                if (newReminder != null) {
                    val newReminderId = reminderRepo.upsert(newReminder)
                    Log.i("TEST1","newReminderId=$newReminderId")
                    reminderScheduler.reschedule(newReminderId)
                }

            } catch (_: Throwable) {}
        }

        return newScheduleChildId
    }

    fun movePomodoroSchedule(scheduleId: Int, newDate: LocalDate, newStart: Int, newEnd: Int) {
        viewModelScope.launch(Dispatchers.IO) {

            taskScheduleRepo.updatePomodoroTimeRange(scheduleId, newDate, newStart, newEnd)

            val reminders = reminderRepo.getByScheduleId(scheduleId)
            reminders.forEach { rUi ->
                try {
                    Log.i("TEST3","in movePomodoroSchedule}")
                    reminderScheduler.reschedule(rUi.id)
                } catch (_: Throwable) {}
            }

        }
    }




}





data class ScheduleScreenItem(
    val scheduleId: Int,
    val taskId: Int,
    val title: String,
    val mode: ScheduleMode ,

    // برای TIME_RANGE
    val dateEpochDay: Long? = null,       // LocalDate.toEpochDay()
    val start: LocalDateTime,
    val end: LocalDateTime,

    // برای AMOUNT_OF_TIME
    val durationMinutes: Int? = null,

    val inPallet: Boolean ,
    val repeating: Boolean ,

    val categoryName: String?,
    val categoryIconName: String?,
    val categoryColor: String?,
    val taskMode: TaskMode,
    val pomodoroTargetUnits: Int?,
    val pomodoroDoneUnits: Int,

    val repeatInterval: Int?,
    val repeatUnit: RepeatUnit?,
    val weekdaysMask: Int?,

    val focusMinutes: Int?,
    val shortBreakMinutes: Int?,
    val longBreakMinutes: Int?,
    val longBreakEvery: Int?,
    val pomodoroUnitsPerDay: Int?,

    val parentRuleScheduleId: Int?,
    val occurrenceDateEpochDay: Long?,
    )

data class PendingMove(
    val date: LocalDate,
    val startMin: Int,
    val endMin: Int
)

data class ScheduleItemsRow(
    val s_id: Int,
    val s_taskId: Int,
    val s_inPallet: Boolean,
    val s_title: String?,
    val s_mode: ScheduleMode,
    val s_dateEpochDay: Long?,
    val s_startMinuteOfDay: Int?,
    val s_endMinuteOfDay: Int?,
    val s_durationMinutes: Int?,
    val s_repeating: Boolean,
    val s_repeatInterval: Int?,
    val s_repeatUnit: RepeatUnit?,
    val s_weekdaysMask: Int?,

    val s_focusMinutes: Int?,
    val s_shortBreakMinutes: Int?,
    val s_longBreakMinutes: Int?,
    val s_longBreakEvery: Int?,
    val s_pomodoroUnitsPerDay: Int?,

    val s_parentRuleScheduleId: Int?,
    val s_occurrenceDateEpochDay: Long?,

    val t_taskMode: TaskMode,
    val t_pomodoroTargetUnits: Int?,
    val t_pomodoroDoneUnits: Int,

    val t_id: Int,
    val t_name: String,
    val t_color: String,
    val t_description: String,
    val t_categoryId: Int?,
    val t_isCompleted: Boolean,
    val t_priority: Int,
    val t_selected: Boolean,
    val t_changed: Boolean,

    // ✅ from CategoryEntity (LEFT JOIN)
    val c_name: String?,
    val c_iconName: String?,
    val c_color: String?
)

data class OverlapLayout(
    val level: Int,
    val widthFrac: Float,   // 1.0, 0.75, 0.5, 0.25
    val offsetFrac: Float,  // 0.0, 0.25, 0.5, 0.75
    val z: Float
)

data class PomodoroPalletCardItem(
    val taskId: Int,
    val scheduleId: Int,
    val taskName: String,

    val totalTarget: Int,
    val totalDone: Int,

    val expectedToday: Int,   // E
    val scheduledToday: Int,  // D (در پالت یعنی Scheduled)

    val focus: Int,
    val shortBreak: Int,
    val longBreak: Int,
    val longBreakEvery: Int,

    val remainingToday: Int
)
data class PomodoroAdjustState(
    val taskId: Int,
    val scheduleId: Int,
    val date: LocalDate,
    val startMin: Int,
    val focus: Int,
    val shortBreak: Int,
    val ids: List<Int?>,
    val anchorInRoot: Offset, // محل نمایش stepper
    val maxAllowed: Int // remainingToday از پالت
)



data class RunningPomodoroUiState(
    val scheduleId: Int,
    val taskId: Int,
    val title: String,
    val clickedAt: LocalDateTime,
    val realStartAt: LocalDateTime,
    val focusEndAt: LocalDateTime,
    val breakEndAt: LocalDateTime,
    val phase: PomodoroRunPhase = PomodoroRunPhase.WAITING_TO_START,
    val waitingSeconds: Long = 0,
    val focusElapsedSeconds: Long = 0,
    val breakElapsedSeconds: Long = 0,
    val focusDoneApplied: Boolean = false,
    val isPaused: Boolean = false,
    val pauseAt: LocalDateTime? = null
)




enum class PomodoroRunPhase {
    WAITING_TO_START,
    FOCUS,
    BREAK,
    FINISHED
}