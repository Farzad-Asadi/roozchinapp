package com.example.compoundeffectV1_01.data.dataBaseRoom.tables.taskSchedule

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskScheduleDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(schedule: TaskSchedule)

    @Update
    suspend fun update(schedule: TaskSchedule)

    @Query("DELETE FROM task_schedule WHERE taskId = :taskId")
    suspend fun deleteByTaskId(taskId: Int)

    @Query("SELECT * FROM task_schedule WHERE taskId = :taskId LIMIT 1")
    fun observeByTaskId(taskId: Int): Flow<TaskSchedule?>

    @Query("SELECT * FROM task_schedule WHERE taskId = :taskId LIMIT 1")
    suspend fun getByTaskId(taskId: Int): TaskSchedule?
}
