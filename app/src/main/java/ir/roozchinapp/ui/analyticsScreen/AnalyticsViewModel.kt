package ir.roozchinapp.ui.analyticsScreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.roozchinapp.data.dataBaseRoom.tables.task.PomodoroDailyAdjustmentEntity
import ir.roozchinapp.data.dataBaseRoom.tables.task.TaskEntity
import ir.roozchinapp.data.dataBaseRoom.tables.task.TaskRepository
import ir.roozchinapp.data.dataBaseRoom.tables.taskSchedule.PomodoroAnalyticsOccurrenceRow
import ir.roozchinapp.data.dataBaseRoom.tables.taskSchedule.RepeatUnit
import ir.roozchinapp.data.dataBaseRoom.tables.taskSchedule.TaskSchedule
import ir.roozchinapp.data.dataBaseRoom.tables.taskSchedule.TaskScheduleRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import javax.inject.Inject
import kotlin.math.max
import kotlin.math.min

@HiltViewModel
class AnalyticsViewModel @Inject constructor(
    private val taskScheduleRepository: TaskScheduleRepository,
    private val taskRepository: TaskRepository
) : ViewModel() {

    private val selectedPeriod =
        MutableStateFlow(AnalyticsPeriod.TODAY)

    private val anchorDate =
        MutableStateFlow(LocalDate.now())

    @OptIn(ExperimentalCoroutinesApi::class)
    val uiState: StateFlow<AnalyticsUiState> =
        combine(
            selectedPeriod,
            anchorDate
        ) { period, today ->
            buildAnalyticsRange(
                period = period,
                today = today
            )
        }
            .flatMapLatest { range ->
                combine(
                    taskScheduleRepository.observePomodoroRules(),
                    taskScheduleRepository.observePomodoroOccurrencesBetween(
                        startEpochDay = range.startDate.toEpochDay(),
                        endEpochDay = range.endDate.toEpochDay()
                    ),
                    taskRepository.observePomodoroDailyAdjustmentsBetween(
                        startEpochDay = range.startDate.toEpochDay(),
                        endEpochDay = range.endDate.toEpochDay()
                    ),
                    taskRepository.observeAllTasks()
                ) { rules, occurrences, adjustments, tasks ->
                    buildAnalyticsUiState(
                        range = range,
                        rules = rules,
                        occurrences = occurrences,
                        adjustments = adjustments,
                        tasks = tasks
                    )
                }
                    .onStart {
                        emit(
                            AnalyticsUiState(
                                selectedPeriod = range.period,
                                isLoading = true
                            )
                        )
                    }
            }
            .catch { throwable ->
                emit(
                    AnalyticsUiState(
                        selectedPeriod = selectedPeriod.value,
                        isLoading = false,
                        errorMessage =
                        throwable.message
                            ?: "خطا در محاسبه آمار پومودورو"
                    )
                )
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = AnalyticsUiState(
                    isLoading = true
                )
            )

    fun selectPeriod(period: AnalyticsPeriod) {
        if (selectedPeriod.value == period) return

        selectedPeriod.value = period
    }

    fun refreshDate() {
        val today = LocalDate.now()

        if (anchorDate.value != today) {
            anchorDate.value = today
        }
    }
}

private data class AnalyticsRange(
    val period: AnalyticsPeriod,
    val startDate: LocalDate,
    val endDate: LocalDate
)

private data class TaskDateKey(
    val taskId: Int,
    val dateEpochDay: Long
)

private data class DayAnalyticsResult(
    val point: PomodoroAnalyticsPoint,
    val fulfilledCount: Int,
    val extraCompletedCount: Int,
    val taskTotalsById: Map<Int, TaskAnalyticsTotals>
)

private data class TaskAnalyticsTotals(
    val plannedCount: Int = 0,
    val scheduledCount: Int = 0,
    val completedCount: Int = 0,
    val fulfilledCount: Int = 0,
    val extraCompletedCount: Int = 0,
    val plannedFocusMinutes: Int = 0,
    val completedFocusMinutes: Int = 0
)

private operator fun TaskAnalyticsTotals.plus(
    other: TaskAnalyticsTotals
): TaskAnalyticsTotals {
    return TaskAnalyticsTotals(
        plannedCount =
        plannedCount + other.plannedCount,

        scheduledCount =
        scheduledCount + other.scheduledCount,

        completedCount =
        completedCount + other.completedCount,

        fulfilledCount =
        fulfilledCount + other.fulfilledCount,

        extraCompletedCount =
        extraCompletedCount + other.extraCompletedCount,

        plannedFocusMinutes =
        plannedFocusMinutes + other.plannedFocusMinutes,

        completedFocusMinutes =
        completedFocusMinutes + other.completedFocusMinutes
    )
}

private fun buildAnalyticsRange(
    period: AnalyticsPeriod,
    today: LocalDate
): AnalyticsRange {
    val startDate =
        when (period) {
            AnalyticsPeriod.TODAY ->
                today

            AnalyticsPeriod.WEEK ->
                today.minusDays(6)

            AnalyticsPeriod.MONTH ->
                today.minusDays(29)
        }

    return AnalyticsRange(
        period = period,
        startDate = startDate,
        endDate = today
    )
}

private fun buildAnalyticsUiState(
    range: AnalyticsRange,
    rules: List<TaskSchedule>,
    occurrences: List<PomodoroAnalyticsOccurrenceRow>,
    adjustments: List<PomodoroDailyAdjustmentEntity>,
    tasks: List<TaskEntity>
): AnalyticsUiState {

    val occurrencesByDate =
        occurrences.groupBy { occurrence ->
            occurrence.dateEpochDay
        }

    val adjustmentByTaskDate =
        adjustments.associate { adjustment ->
            TaskDateKey(
                taskId = adjustment.taskId,
                dateEpochDay = adjustment.dateEpochDay
            ) to adjustment.delta
        }

    val dayResults =
        datesBetween(
            startDate = range.startDate,
            endDate = range.endDate
        ).map { date ->
            buildDayAnalytics(
                period = range.period,
                date = date,
                rules = rules,
                occurrences = occurrencesByDate[
                    date.toEpochDay()
                ].orEmpty(),
                adjustmentByTaskDate = adjustmentByTaskDate
            )
        }

    val taskById =
        tasks.mapNotNull { task ->
            val taskId = task.id ?: return@mapNotNull null
            taskId to task
        }.toMap()

    val aggregatedTaskTotals =
        linkedMapOf<Int, TaskAnalyticsTotals>()

    dayResults.forEach { dayResult ->
        dayResult.taskTotalsById.forEach { (taskId, dayTotals) ->
            val previousTotals =
                aggregatedTaskTotals[taskId]
                    ?: TaskAnalyticsTotals()

            aggregatedTaskTotals[taskId] =
                previousTotals + dayTotals
        }
    }

    val taskItems =
        aggregatedTaskTotals
            .map { (taskId, totals) ->
                val task = taskById[taskId]

                val completionPercent =
                    if (totals.plannedCount > 0) {
                        totals.fulfilledCount
                            .toFloat()
                            .div(totals.plannedCount.toFloat())
                            .times(100f)
                            .coerceIn(0f, 100f)
                    } else {
                        0f
                    }

                PomodoroTaskAnalyticsItem(
                    taskId = taskId,
                    taskName =
                    task?.name
                        ?.takeIf { it.isNotBlank() }
                        ?: "تسک شماره $taskId",

                    taskColorHex =
                    task?.color
                        ?.takeIf { it.isNotBlank() }
                        ?: "#9E9E9E",

                    plannedCount = totals.plannedCount,
                    scheduledCount = totals.scheduledCount,
                    completedCount = totals.completedCount,
                    extraCompletedCount =
                    totals.extraCompletedCount,

                    plannedFocusMinutes =
                    totals.plannedFocusMinutes,

                    completedFocusMinutes =
                    totals.completedFocusMinutes,

                    completionPercent = completionPercent
                )
            }
            .filter { item ->
                item.plannedCount > 0 ||
                        item.scheduledCount > 0 ||
                        item.completedCount > 0
            }
            .sortedWith(
                compareByDescending<PomodoroTaskAnalyticsItem> {
                    it.completedFocusMinutes
                }.thenByDescending {
                    it.completedCount
                }.thenBy {
                    it.taskName
                }
            )

    val plannedCount =
        dayResults.sumOf { it.point.plannedCount }

    val scheduledCount =
        dayResults.sumOf { it.point.scheduledCount }

    val completedCount =
        dayResults.sumOf { it.point.completedCount }

    val fulfilledCount =
        dayResults.sumOf { it.fulfilledCount }

    val extraCompletedCount =
        dayResults.sumOf { it.extraCompletedCount }

    val plannedFocusMinutes =
        dayResults.sumOf { it.point.plannedFocusMinutes }

    val completedFocusMinutes =
        dayResults.sumOf { it.point.completedFocusMinutes }

    val completionPercent =
        if (plannedCount > 0) {
            fulfilledCount
                .toFloat()
                .div(plannedCount.toFloat())
                .times(100f)
                .coerceIn(0f, 100f)
        } else {
            0f
        }

    return AnalyticsUiState(
        selectedPeriod = range.period,
        summary = PomodoroAnalyticsSummary(
            plannedCount = plannedCount,
            scheduledCount = scheduledCount,
            completedCount = completedCount,
            extraCompletedCount = extraCompletedCount,
            plannedFocusMinutes = plannedFocusMinutes,
            completedFocusMinutes = completedFocusMinutes,
            completionPercent = completionPercent
        ),
        points = dayResults.map { it.point },
        taskItems = taskItems,
        isLoading = false,
        errorMessage = null
    )
}

private fun buildDayAnalytics(
    period: AnalyticsPeriod,
    date: LocalDate,
    rules: List<TaskSchedule>,
    occurrences: List<PomodoroAnalyticsOccurrenceRow>,
    adjustmentByTaskDate: Map<TaskDateKey, Int>
): DayAnalyticsResult {

    val activeRules =
        rules.filter { rule ->
            isPomodoroRuleActiveOnDate(
                rule = rule,
                date = date
            )
        }

    val rulesByTask =
        activeRules.groupBy { rule ->
            rule.taskId
        }

    val occurrencesByTask =
        occurrences.groupBy { occurrence ->
            occurrence.taskId
        }

    val adjustedTaskIdsForDate =
        adjustmentByTaskDate.keys
            .asSequence()
            .filter { key ->
                key.dateEpochDay == date.toEpochDay()
            }
            .map { key ->
                key.taskId
            }
            .toSet()

    val allTaskIds =
        buildSet {
            addAll(rulesByTask.keys)
            addAll(occurrencesByTask.keys)
            addAll(adjustedTaskIdsForDate)
        }

    var totalPlanned = 0
    var totalScheduled = 0
    var totalCompleted = 0
    var totalFulfilled = 0
    var totalExtra = 0

    var totalPlannedFocusMinutes = 0
    var totalCompletedFocusMinutes = 0

    val taskTotalsById =
        linkedMapOf<Int, TaskAnalyticsTotals>()

    allTaskIds.forEach { taskId ->
        val taskRules =
            rulesByTask[taskId].orEmpty()

        val taskOccurrences =
            occurrencesByTask[taskId].orEmpty()

        val plannedForTask =
            taskRules.sumOf { rule ->
                (rule.pomodoroUnitsPerDay ?: 1)
                    .coerceAtLeast(1)
            }

        val plannedFocusForTask =
            taskRules.sumOf { rule ->
                val count =
                    (rule.pomodoroUnitsPerDay ?: 1)
                        .coerceAtLeast(1)

                val focus =
                    (rule.focusMinutes ?: 25)
                        .coerceAtLeast(1)

                count * focus
            }

        val scheduledForTask =
            taskOccurrences.count { occurrence ->
                !occurrence.inPallet
            }

        val completedOccurrences =
            taskOccurrences.filter { occurrence ->
                occurrence.pomodoroFocusDoneApplied
            }

        val realCompletedCount =
            completedOccurrences.size

        val defaultFocusMinutes =
            taskRules
                .firstOrNull()
                ?.focusMinutes
                ?.coerceAtLeast(1)
                ?: taskOccurrences
                    .firstOrNull()
                    ?.focusMinutes
                    ?.coerceAtLeast(1)
                ?: 25

        val realCompletedFocusMinutes =
            completedOccurrences.sumOf { occurrence ->
                (occurrence.focusMinutes ?: defaultFocusMinutes)
                    .coerceAtLeast(1)
            }

        val manualDelta =
            adjustmentByTaskDate[
                TaskDateKey(
                    taskId = taskId,
                    dateEpochDay = date.toEpochDay()
                )
            ] ?: 0

        val completedForTask =
            (realCompletedCount + manualDelta)
                .coerceAtLeast(0)

        val completedFocusForTask =
            (
                    realCompletedFocusMinutes +
                            manualDelta * defaultFocusMinutes
                    )
                .coerceAtLeast(0)

        val fulfilledForTask =
            min(
                completedForTask,
                plannedForTask
            )

        val extraForTask =
            max(
                completedForTask - plannedForTask,
                0
            )

        taskTotalsById[taskId] =
            TaskAnalyticsTotals(
                plannedCount = plannedForTask,
                scheduledCount = scheduledForTask,
                completedCount = completedForTask,
                fulfilledCount = fulfilledForTask,
                extraCompletedCount = extraForTask,
                plannedFocusMinutes =
                plannedFocusForTask,
                completedFocusMinutes =
                completedFocusForTask
            )

        totalPlanned += plannedForTask
        totalScheduled += scheduledForTask
        totalCompleted += completedForTask
        totalFulfilled += fulfilledForTask
        totalExtra += extraForTask

        totalPlannedFocusMinutes += plannedFocusForTask
        totalCompletedFocusMinutes += completedFocusForTask
    }

    return DayAnalyticsResult(
        point = PomodoroAnalyticsPoint(
            dateEpochDay = date.toEpochDay(),
            label = analyticsDateLabel(
                period = period,
                date = date
            ),
            plannedCount = totalPlanned,
            scheduledCount = totalScheduled,
            completedCount = totalCompleted,
            plannedFocusMinutes =
            totalPlannedFocusMinutes,
            completedFocusMinutes =
            totalCompletedFocusMinutes
        ),
        fulfilledCount = totalFulfilled,
        extraCompletedCount = totalExtra,
        taskTotalsById = taskTotalsById
    )


}

private fun datesBetween(
    startDate: LocalDate,
    endDate: LocalDate
): List<LocalDate> {
    if (endDate.isBefore(startDate)) {
        return emptyList()
    }

    return buildList {
        var date = startDate

        while (!date.isAfter(endDate)) {
            add(date)
            date = date.plusDays(1)
        }
    }
}

/**
 * این تابع باید با منطق فعلی ساخت پومودوروهای روزانه هماهنگ بماند.
 */
private fun isPomodoroRuleActiveOnDate(
    rule: TaskSchedule,
    date: LocalDate
): Boolean {
    if (!rule.repeating) return false

    val baseDate =
        rule.dateEpochDay
            ?.let(LocalDate::ofEpochDay)
            ?: return false

    if (date.isBefore(baseDate)) {
        return false
    }

    val interval =
        (rule.repeatInterval ?: 1)
            .coerceIn(1, 99)

    val repeatUnit =
        rule.repeatUnit ?: RepeatUnit.WEEK

    return when (repeatUnit) {
        RepeatUnit.DAY -> {
            val days =
                ChronoUnit.DAYS.between(
                    baseDate,
                    date
                )

            days >= 0 &&
                    days % interval == 0L
        }

        RepeatUnit.WEEK -> {
            val weekdaysMask =
                (rule.weekdaysMask ?: 0)
                    .coerceIn(0, 127)

            if (weekdaysMask == 0) {
                return false
            }

            val bitIndex =
                dayOfWeekBitIndex(
                    date.dayOfWeek
                )

            val weekdayAllowed =
                weekdaysMask and (1 shl bitIndex) != 0

            if (!weekdayAllowed) {
                return false
            }

            val weeks =
                ChronoUnit.WEEKS.between(
                    baseDate,
                    date
                )

            weeks >= 0 &&
                    weeks % interval == 0L
        }

        else -> {
            // هماهنگ با منطق فعلی Repository:
            // MONTH و YEAR فعلاً مانند فاصله روزانه بررسی می‌شوند.
            val days =
                ChronoUnit.DAYS.between(
                    baseDate,
                    date
                )

            days >= 0 &&
                    days % interval == 0L
        }
    }
}

private fun dayOfWeekBitIndex(
    dayOfWeek: DayOfWeek
): Int {
    return when (dayOfWeek) {
        DayOfWeek.SATURDAY -> 0
        DayOfWeek.SUNDAY -> 1
        DayOfWeek.MONDAY -> 2
        DayOfWeek.TUESDAY -> 3
        DayOfWeek.WEDNESDAY -> 4
        DayOfWeek.THURSDAY -> 5
        DayOfWeek.FRIDAY -> 6
    }
}

private fun analyticsDateLabel(
    period: AnalyticsPeriod,
    date: LocalDate
): String {
    return when (period) {
        AnalyticsPeriod.TODAY ->
            "امروز"

        AnalyticsPeriod.WEEK ->
            when (date.dayOfWeek) {
                DayOfWeek.SATURDAY -> "شنبه"
                DayOfWeek.SUNDAY -> "یکشنبه"
                DayOfWeek.MONDAY -> "دوشنبه"
                DayOfWeek.TUESDAY -> "سه‌شنبه"
                DayOfWeek.WEDNESDAY -> "چهارشنبه"
                DayOfWeek.THURSDAY -> "پنجشنبه"
                DayOfWeek.FRIDAY -> "جمعه"
            }

        AnalyticsPeriod.MONTH ->
            "${date.monthValue}/${date.dayOfMonth}"
    }
}