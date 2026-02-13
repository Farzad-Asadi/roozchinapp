package com.example.compoundeffectV1_01.data.dataBaseRoom.tables.task

import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime


interface TaskRepository {


    suspend fun insertTask(vararg task: Task)

    suspend fun insertTaskAndReturnId(task: Task): Int

    suspend fun updateTask(task: Task)

    suspend fun deleteTask(task: Task)

    fun observeTasksByCategory(categoryId: Int): Flow<List<Task>>

    suspend fun getTaskById(id: Int): Task?

    fun observeTasksWithScheduleByCategory(categoryId: Int): kotlinx.coroutines.flow.Flow<List<TaskWithSchedule>>

    fun observeScheduledCountByCategory(categoryId: Int): kotlinx.coroutines.flow.Flow<Int>

    fun observeAllScheduledTasksWithSchedule(): Flow<List<TaskWithSchedule>>

    fun observePalletTasks(): Flow<List<Task>>


}