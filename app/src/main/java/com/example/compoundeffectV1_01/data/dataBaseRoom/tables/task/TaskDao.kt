package com.example.compoundeffectV1_01.data.dataBaseRoom.tables.task

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime

@Dao
interface TaskDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertTask(vararg task: Task)

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertTaskAndReturnId(task: Task): Long


    @Update
    suspend fun updateTask(task: Task)

    @Delete
    suspend fun deleteTask(task: Task)

    @Query("""
        SELECT * FROM task 
        WHERE categoryId = :categoryId
        ORDER BY id DESC
    """)
    fun observeTasksByCategory(categoryId: Int): Flow<List<Task>>

    @Query("SELECT * FROM task WHERE id = :id LIMIT 1")
    suspend fun getTaskById(id: Int): Task?


    @Transaction
    @Query("""
    SELECT * FROM task
    WHERE categoryId = :categoryId
    ORDER BY id DESC
""")
    fun observeTasksWithScheduleByCategory(categoryId: Int): Flow<List<TaskWithSchedule>>


    @Query("""
    SELECT COUNT(*) FROM task_schedule s
    INNER JOIN task t ON t.id = s.taskId
    WHERE t.categoryId = :categoryId
""")
    fun observeScheduledCountByCategory(categoryId: Int): Flow<Int>

    @Transaction
    @Query("""
    SELECT * FROM task
    INNER JOIN task_schedule s ON s.taskId = task.id
    WHERE s.mode = 'TIME_RANGE'
""")
    fun observeAllScheduledTasksWithSchedule(): Flow<List<TaskWithSchedule>>

    @Query("SELECT * FROM task WHERE inPallet = 1 ORDER BY id DESC")
    fun observePalletTasks(): Flow<List<Task>>



}
