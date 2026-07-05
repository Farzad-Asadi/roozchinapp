package com.example.compoundeffectV1_01.data.dataBaseRoom.tables.task

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertTask(vararg taskEntity: TaskEntity)

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertTaskAndReturnId(taskEntity: TaskEntity): Long


    @Update
    suspend fun updateTask(taskEntity: TaskEntity)

    @Delete
    suspend fun deleteTask(taskEntity: TaskEntity)


    @Query("SELECT * FROM task WHERE id = :id LIMIT 1")
    suspend fun getTaskById(id: Int): TaskEntity?


    @Query("""
SELECT * FROM task
WHERE entityStatus = 'DRAFT'
  AND draftCreatedAtEpochMillis IS NOT NULL
  AND draftCreatedAtEpochMillis < :cutoffEpochMillis
  AND (
      parentTaskId IS NULL
      OR parentTaskId = :rootParentTaskId
  )
ORDER BY draftCreatedAtEpochMillis ASC, id ASC
""")
    suspend fun getOldDraftRootTasks(
        cutoffEpochMillis: Long,
        rootParentTaskId: Int = -1
    ): List<TaskEntity>



    @Query("""
SELECT COUNT(*) FROM task_schedule s
INNER JOIN task t ON t.id = s.taskId
WHERE t.categoryId = :categoryId
  AND t.entityStatus = 'ACTIVE'
""")
    fun observeScheduledCountByCategory(categoryId: Int): Flow<Int>

    @Transaction
    @Query("""
SELECT * FROM task
INNER JOIN task_schedule s ON s.taskId = task.id
WHERE s.mode = 'TIME_RANGE'
  AND task.entityStatus = 'ACTIVE'
""")
    fun observeAllScheduledTasksWithSchedule(): Flow<List<TaskWithSchedule>>

    @Query("""
SELECT * FROM task
WHERE categoryId = :categoryId
  AND entityStatus = 'ACTIVE'
""")
    fun observeTasksByCategory(categoryId: Int): Flow<List<TaskEntity>>


    @Query("""
SELECT * FROM task
WHERE entityStatus = 'ACTIVE'
ORDER BY name COLLATE NOCASE ASC, id ASC
""")
    fun observeAllTasks(): Flow<List<TaskEntity>>

    @Query("""
SELECT * FROM task
WHERE categoryId = :categoryId
  AND entityStatus = 'ACTIVE'
""")
    suspend fun getTasksByCategory(categoryId: Int): List<TaskEntity>


    @Query("UPDATE task SET siblingIndex = :siblingIndex WHERE id = :id")
    suspend fun updateSiblingIndex(id: Int, siblingIndex: Int)

    @Query("UPDATE task SET parentTaskId = :parentTaskId WHERE id = :id")
    suspend fun updateTaskParent(id: Int, parentTaskId: Int?)









    @Transaction
    @Query("""
SELECT * FROM task
WHERE categoryId = :categoryId
  AND entityStatus = 'ACTIVE'
""")
    fun observeTasksWithScheduleByCategory(categoryId: Int): Flow<List<TaskWithSchedule>>


    @Query("""
SELECT * FROM task
WHERE categoryId = :categoryId
  AND entityStatus = 'ACTIVE'
""")
    suspend fun getTasksByCategoryOrdered(categoryId: Int): List<TaskEntity>


    @Query("""
SELECT COUNT(*) FROM task
WHERE parentTaskId = :taskId
  AND entityStatus = 'ACTIVE'
""")
    suspend fun countChildren(taskId: Int): Int



    @Query("""
UPDATE task
SET isCompleted = :done
WHERE id IN (:ids)
  AND entityStatus = 'ACTIVE'
""")
    suspend fun setCompletedForIds(ids: List<Int>, done: Boolean)

    @Query("""
SELECT * FROM task
WHERE categoryId = :categoryId 
  AND parentTaskId = :parentId
  AND entityStatus = 'ACTIVE'
ORDER BY siblingIndex ASC, id ASC
""")
    suspend fun getSiblings(categoryId: Int, parentId: Int): List<TaskEntity>


    @Query("""
UPDATE task
SET siblingIndex = siblingIndex + 1
WHERE categoryId = :categoryId
  AND parentTaskId = :parentId
  AND entityStatus = 'ACTIVE'
""")
    suspend fun shiftSiblingsDown(categoryId: Int, parentId: Int)






    @Query("UPDATE task SET parentTaskId = -1 WHERE parentTaskId IS NULL")
    suspend fun normalizeNullParentsToRoot()


    @Query("""
UPDATE task
SET isCompleted = 1
WHERE categoryId = :categoryId
  AND entityStatus = 'ACTIVE'
""")
    suspend fun completeAllInCategory(categoryId: Int)

    @Query("""
UPDATE task
SET isCompleted = 0
WHERE categoryId = :categoryId
  AND entityStatus = 'ACTIVE'
""")
    suspend fun uncompleteAllInCategory(categoryId: Int)

    @Query("""
DELETE FROM task
WHERE categoryId = :categoryId
  AND isCompleted = 1
  AND entityStatus = 'ACTIVE'
""")
    suspend fun deleteCompletedInCategory(categoryId: Int)

    @Query("DELETE FROM task WHERE categoryId = :categoryId")
    suspend fun deleteAllInCategory(categoryId: Int)


    @Query("""
UPDATE task
SET pomodoroDoneUnits = pomodoroDoneUnits + 1
WHERE id = :taskId
  AND entityStatus = 'ACTIVE'
""")
    suspend fun incrementPomodoroDoneUnits(taskId: Int)

    @Query("""
    SELECT * FROM pomodoro_daily_adjustment
""")
    fun observePomodoroDailyAdjustments(): Flow<List<PomodoroDailyAdjustmentEntity>>

    @Query("""
    SELECT delta FROM pomodoro_daily_adjustment
    WHERE taskId = :taskId AND dateEpochDay = :dateEpochDay
    LIMIT 1
""")
    suspend fun getPomodoroDailyAdjustmentDelta(
        taskId: Int,
        dateEpochDay: Long
    ): Int?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertPomodoroDailyAdjustment(
        entity: PomodoroDailyAdjustmentEntity
    )

    @Query("""
    DELETE FROM pomodoro_daily_adjustment
    WHERE taskId = :taskId AND dateEpochDay = :dateEpochDay
""")
    suspend fun deletePomodoroDailyAdjustment(
        taskId: Int,
        dateEpochDay: Long
    )

    @Query("""
UPDATE task
SET pomodoroDoneUnits =
    CASE
        WHEN pomodoroDoneUnits + :delta < 0 THEN 0
        ELSE pomodoroDoneUnits + :delta
    END
WHERE id = :taskId
  AND entityStatus = 'ACTIVE'
""")
    suspend fun adjustPomodoroDoneUnitsByDelta(
        taskId: Int,
        delta: Int
    )

    @Transaction
    suspend fun adjustManualPomodoroDone(
        taskId: Int,
        dateEpochDay: Long,
        delta: Int
    ) {
        val currentDelta = getPomodoroDailyAdjustmentDelta(
            taskId = taskId,
            dateEpochDay = dateEpochDay
        ) ?: 0

        val newDelta = currentDelta + delta

        if (newDelta == 0) {
            deletePomodoroDailyAdjustment(
                taskId = taskId,
                dateEpochDay = dateEpochDay
            )
        } else {
            upsertPomodoroDailyAdjustment(
                PomodoroDailyAdjustmentEntity(
                    taskId = taskId,
                    dateEpochDay = dateEpochDay,
                    delta = newDelta
                )
            )
        }

        adjustPomodoroDoneUnitsByDelta(
            taskId = taskId,
            delta = delta
        )
    }



    //region Backup / Restore

    @Query("""
SELECT * FROM task
WHERE entityStatus = 'ACTIVE'
""")
    suspend fun getAllTasksForBackup(): List<TaskEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTasksForRestore(taskEntities: List<TaskEntity>)

    @Query("DELETE FROM task")
    suspend fun deleteAllTasksForRestore()

//endregion
}
