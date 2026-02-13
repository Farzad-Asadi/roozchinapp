package com.example.compoundeffectV1_01.data.dataBaseRoom.tables.taskSchedule

import kotlinx.coroutines.flow.Flow

interface TaskScheduleRepository {
    fun observeByTaskId(taskId: Int): Flow<TaskSchedule?>
    suspend fun getByTaskId(taskId: Int): TaskSchedule?
    suspend fun upsert(schedule: TaskSchedule)
    suspend fun deleteByTaskId(taskId: Int)
}
