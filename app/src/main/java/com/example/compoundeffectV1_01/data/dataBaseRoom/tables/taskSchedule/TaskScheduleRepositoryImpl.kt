package com.example.compoundeffectV1_01.data.dataBaseRoom.tables.taskSchedule

import com.example.compoundeffectV1_01.ui.scheduleScreen.ScheduleItemsRow
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

class TaskScheduleRepositoryImpl @Inject constructor(
    private val dao: TaskScheduleDao
) : TaskScheduleRepository {

    override fun observeByTaskId(taskId: Int): Flow<List<TaskSchedule>> = dao.observeByTaskId(taskId)
    override suspend fun getByTaskId(taskId: Int): TaskSchedule? = dao.getByTaskId(taskId)
    override suspend fun upsert(schedule: TaskSchedule) = dao.upsert(schedule)
    override suspend fun upsertAndReturnId(schedule: TaskSchedule): Int =
        dao.upsertAndReturnId(schedule).toInt()

    override suspend fun delete(schedule: TaskSchedule) = dao.delete(schedule)

    override suspend fun deleteByTaskId(taskId: Int) = dao.deleteByTaskId(taskId)
    override suspend fun deleteAllForTask(taskId: Int) = dao.deleteAllForTask(taskId)


    override suspend fun insert(schedule: TaskSchedule): Int =
        dao.insert(schedule).toInt()

    override suspend fun updateTimeRange(scheduleId: Int, date: LocalDate, startMin: Int, endMin: Int) {
        dao.updateTimeRange(
            scheduleId = scheduleId,
            dateEpochDay = date.toEpochDay(),
            startMin = startMin,
            endMin = endMin
        )
    }

    override suspend fun updateEndMinute(scheduleId: Int, endMin: Int) {
        dao.updateEndMinute(scheduleId, endMin)
    }

    override suspend fun updateStartMinute(scheduleId: Int, startMin: Int) {
        dao.updateStartMinute(scheduleId, startMin)
    }

    override fun observeAllSchedulesWithTask(): Flow<List<ScheduleItemsRow>> =
        dao.observeAllSchedulesWithTask()


    override suspend fun deleteById(scheduleId: Int) =
        dao.deleteById(scheduleId)

    override suspend fun countByTaskId(taskId: Int): Int =
            dao.countByTaskId(taskId)

    override suspend fun getById(id: Int): TaskSchedule? =
        dao.getById(id)


    override suspend fun setSchedulePalletState(scheduleId: Int, inPallet: Boolean) =
        dao.setSchedulePalletState(scheduleId, inPallet)

    override suspend fun getLastInactiveTimeRange(taskId: Int, mode: ScheduleMode): TaskSchedule? =
        dao.getLastInactiveTimeRange(taskId, mode)

    override suspend fun dropFromPalletToTimeline(
        scheduleId: Int,
        date: LocalDate,
        startMin: Int,
        endMin: Int,
        mode: ScheduleMode
    ) =
        dao.dropFromPalletToTimeline(
            scheduleId = scheduleId,
            dateEpochDay = date.toEpochDay(), // ✅ تبدیل
            startMin = startMin,
            endMin = endMin
        )
    override suspend fun ensureTodayPomodorosInPallet(today: LocalDate) {
        val rules = dao.getPomodoroRules()
        if (rules.isEmpty()) return

        val todayEpoch = today.toEpochDay()
        val toInsert = ArrayList<TaskSchedule>(32)

        for (rule in rules) {
            if (!isRuleActiveOnDate(rule, today)) continue

            val perDay = (rule.pomodoroUnitsPerDay ?: 1).coerceIn(1, 50)
            val existing = dao.countPomodoroUnitsForDate(rule.taskId, todayEpoch)
            val missing = perDay - existing
            if (missing <= 0) continue

            val focus = (rule.focusMinutes ?: 25).coerceIn(1, 240)
            val short = (rule.shortBreakMinutes ?: 5).coerceIn(0, 60)

            // برای اینکه ScheduleScreen تو پالِت هم آیتم رو ببینه، start/end غیر null و end>start می‌ذاریم
            val start = 480 // 08:00
            val end = start + focus + short

            repeat(missing) {
                toInsert += rule.copy(
                    id = null,
                    repeating = false,
                    repeatInterval = null,
                    repeatUnit = null,
                    weekdaysMask = null,

                    inPallet = true,
                    dateEpochDay = todayEpoch,
                    startMinuteOfDay = start,
                    endMinuteOfDay = end,

                    // عنوان رو null می‌ذاریم تا تو UI از title تسک استفاده شه
                    title = null
                )
            }
        }

        if (toInsert.isNotEmpty()) {
            dao.insertAll(toInsert)
        }
    }

    override suspend fun updatePomodoroTimeRange(scheduleId: Int, date: LocalDate, startMin: Int, endMin: Int) {
        dao.updateJustTimeRange(scheduleId, date.toEpochDay(), startMin, endMin)
    }

    /** آیا این Rule در این تاریخ فعال است؟ */
    private fun isRuleActiveOnDate(rule: TaskSchedule, date: LocalDate): Boolean {
        if (!rule.repeating) return false
        val base = rule.dateEpochDay?.let(LocalDate::ofEpochDay) ?: return false
        if (date.isBefore(base)) return false

        val interval = (rule.repeatInterval ?: 1).coerceIn(1, 99)
        val unit = rule.repeatUnit ?: RepeatUnit.WEEK

        return when (unit) {
            RepeatUnit.DAY -> {
                val days = java.time.temporal.ChronoUnit.DAYS.between(base, date)
                days >= 0 && (days % interval == 0L)
            }

            RepeatUnit.WEEK -> {
                val mask = (rule.weekdaysMask ?: 0).coerceIn(0, 127)
                if (mask == 0) return false

                fun bitIndex(dow: java.time.DayOfWeek): Int = when (dow) {
                    java.time.DayOfWeek.SATURDAY -> 0
                    java.time.DayOfWeek.SUNDAY -> 1
                    java.time.DayOfWeek.MONDAY -> 2
                    java.time.DayOfWeek.TUESDAY -> 3
                    java.time.DayOfWeek.WEDNESDAY -> 4
                    java.time.DayOfWeek.THURSDAY -> 5
                    java.time.DayOfWeek.FRIDAY -> 6
                }

                val allowed = (mask and (1 shl bitIndex(date.dayOfWeek))) != 0
                if (!allowed) return false

                val weeks = java.time.temporal.ChronoUnit.WEEKS.between(base, date)
                weeks >= 0 && (weeks % interval == 0L)
            }

            else -> {
                // فعلاً مثل DAY
                val days = java.time.temporal.ChronoUnit.DAYS.between(base, date)
                days >= 0 && (days % interval == 0L)
            }
        }
    }


}
