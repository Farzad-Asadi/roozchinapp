package ir.roozchinapp.data.dataBaseRoom.tables.taskSchedule

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import ir.roozchinapp.ui.scheduleScreen.ScheduleItemsRow
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskScheduleDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(schedule: TaskSchedule)

    @Transaction
    suspend fun upsertAndReturnId(schedule: TaskSchedule): Int {
        val id = schedule.id
        return if (id == null) {
            insert(schedule).toInt()
        } else {
            update(schedule)     // ✅ این دیگه delete نمی‌کنه
            id
        }
    }


    @Query("DELETE FROM task_schedule WHERE taskId = :taskId")
    suspend fun deleteByTaskId(taskId: Int)

    @Query("SELECT * FROM task_schedule WHERE taskId = :taskId ORDER BY dateEpochDay ASC, startMinuteOfDay ASC, id ASC")
    fun observeByTaskId(taskId: Int): Flow<List<TaskSchedule>>

    @Query("SELECT * FROM task_schedule WHERE taskId = :taskId LIMIT 1")
    suspend fun getByTaskId(taskId: Int): TaskSchedule?

    @Query("SELECT * FROM task_schedule WHERE taskId = :taskId")
    suspend fun getAllScheduleByTaskId(taskId: Int): List<TaskSchedule>


    @Delete
    suspend fun delete(schedule: TaskSchedule)

    @Query("DELETE FROM task_schedule WHERE taskId = :taskId")
    suspend fun deleteAllForTask(taskId: Int)





    @Insert
    suspend fun insert(schedule: TaskSchedule): Long  // ✅ id جدید را برمی‌گرداند

    @Update
    suspend fun update(schedule: TaskSchedule)

    @Query("""
        UPDATE task_schedule
        SET dateEpochDay = :dateEpochDay,
            startMinuteOfDay = :startMin,
            endMinuteOfDay = :endMin,
            mode = :mode
        WHERE id = :scheduleId
    """)
    suspend fun updateTimeRange(
        scheduleId: Int,
        mode: ScheduleMode = ScheduleMode.TIME_RANGE,
        dateEpochDay: Long,
        startMin: Int,
        endMin: Int
    ): Int  // rows affected

    @Query("""
        UPDATE task_schedule
        SET endMinuteOfDay = :endMin
        WHERE id = :scheduleId
    """)
    suspend fun updateEndMinute(scheduleId: Int, endMin: Int): Int

    @Query("""
        UPDATE task_schedule
        SET startMinuteOfDay = :startMin
        WHERE id = :scheduleId
    """)
    suspend fun updateStartMinute(scheduleId: Int, startMin: Int): Int

    @Query("SELECT * FROM task_schedule WHERE id = :id LIMIT 1")
    suspend fun getById(id: Int): TaskSchedule?

    // ✅ برای تایم‌لاین: join schedule + task
    @Query("""
SELECT 
  s.id             AS s_id,
  s.taskId         AS s_taskId,
  s.inPallet      AS s_inPallet,
  s.title          AS s_title,
  s.mode           AS s_mode,
  s.dateEpochDay   AS s_dateEpochDay,
  s.startMinuteOfDay AS s_startMinuteOfDay,
  s.endMinuteOfDay AS s_endMinuteOfDay,
  s.durationMinutes AS s_durationMinutes,
  s.repeating      AS s_repeating,
  s.repeatInterval    AS s_repeatInterval,
  s.repeatUnit        AS s_repeatUnit,
  s.weekdaysMask      AS s_weekdaysMask,

  s.focusMinutes      AS s_focusMinutes,
  s.shortBreakMinutes AS s_shortBreakMinutes,
  s.longBreakMinutes  AS s_longBreakMinutes,
  s.longBreakEvery    AS s_longBreakEvery,
  s.pomodoroUnitsPerDay AS s_pomodoroUnitsPerDay,
  s.pomodoroFocusDoneApplied AS s_pomodoroFocusDoneApplied,
  
  s.parentRuleScheduleId      AS s_parentRuleScheduleId,
  s.occurrenceDateEpochDay    AS s_occurrenceDateEpochDay,

  t.taskMode            AS t_taskMode,
  t.pomodoroTargetUnits AS t_pomodoroTargetUnits,
  t.pomodoroDoneUnits   AS t_pomodoroDoneUnits,


  t.id             AS t_id,
  t.name           AS t_name,
  t.color          AS t_color,
  t.description    AS t_description,
  t.categoryId     AS t_categoryId,
  t.isCompleted    AS t_isCompleted,
  t.priority       AS t_priority,
  t.selected       AS t_selected,
  t.changed        AS t_changed,

  c.name           AS c_name,
  c.iconName       AS c_iconName,
  c.color          AS c_color

FROM task_schedule s
JOIN task t ON t.id = s.taskId
LEFT JOIN category c ON c.categoryId = t.categoryId
""")
    fun observeAllSchedulesWithTask(): Flow<List<ScheduleItemsRow>>



    @Query("DELETE FROM task_schedule WHERE id = :scheduleId")
    suspend fun deleteById(scheduleId: Int)

    @Query("SELECT COUNT(*) FROM task_schedule WHERE taskId = :taskId")
    suspend fun countByTaskId(taskId: Int): Int



    @Query("UPDATE task_schedule SET inPallet = :inPallet WHERE id = :scheduleId")
    suspend fun setSchedulePalletState(scheduleId: Int, inPallet: Boolean)

    @Query("SELECT * FROM task_schedule WHERE taskId=:taskId AND mode=:mode AND inPallet=1 ORDER BY id DESC LIMIT 1")
    suspend fun getLastInactiveTimeRange(taskId: Int, mode: ScheduleMode = ScheduleMode.TIME_RANGE): TaskSchedule?

    @Query("""
        UPDATE task_schedule
        SET 
            inPallet = 0,
            dateEpochDay = :dateEpochDay,
            startMinuteOfDay = :startMin,
            endMinuteOfDay = :endMin,
            mode = :mode,
            pomodoroFocusDoneApplied = 0
        WHERE id = :scheduleId
    """)
    suspend fun dropFromPalletToTimeline(
        scheduleId: Int,
        dateEpochDay: Long,
        startMin: Int,
        endMin: Int,
        mode: ScheduleMode = ScheduleMode.TIME_RANGE
    )

    @Query("""
    SELECT * FROM task_schedule
    WHERE mode = :mode
      AND repeating = 1
      AND inPallet = 1
""")
    suspend fun getPomodoroRules(mode: ScheduleMode = ScheduleMode.POMODORO): List<TaskSchedule>


    @Query("""
    SELECT COUNT(*) FROM task_schedule
    WHERE taskId = :taskId
      AND mode = :mode
      AND repeating = 0
      AND inPallet = 1
      AND dateEpochDay = :dateEpochDay
""")
    suspend fun countPomodoroUnitsForDate(
        taskId: Int,
        dateEpochDay: Long,
        mode: ScheduleMode = ScheduleMode.POMODORO
    ): Int


    @Insert
    suspend fun insertAll(schedules: List<TaskSchedule>): List<Long>

    @Query("""
UPDATE task_schedule
SET dateEpochDay = :dateEpochDay,
    startMinuteOfDay = :startMin,
    endMinuteOfDay = :endMin
WHERE id = :scheduleId
""")
    suspend fun updateJustTimeRange(
        scheduleId: Int,
        dateEpochDay: Long,
        startMin: Int,
        endMin: Int
    )


    @Query("SELECT * FROM task_schedule WHERE pomodoroParentId = :pomodoroParentId")
    suspend fun getAllScheduleByPomodoroParentId(pomodoroParentId : Int) : List<TaskSchedule>


    //region Backup / Restore

    @Query("SELECT * FROM task_schedule")
    suspend fun getAllSchedulesForBackup(): List<TaskSchedule>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSchedulesForRestore(schedules: List<TaskSchedule>)

    @Query("DELETE FROM task_schedule")
    suspend fun deleteAllSchedulesForRestore()

    @Query("""
    UPDATE task_schedule
    SET pomodoroFocusDoneApplied = 1
    WHERE id = :scheduleId
      AND pomodoroFocusDoneApplied = 0
""")
    suspend fun markPomodoroFocusDoneIfNeeded(scheduleId: Int): Int

//endregion

}
