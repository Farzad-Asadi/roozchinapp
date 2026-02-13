package com.example.compoundeffectV1_01.data.dataBaseRoom.tables.taskSchedule

import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class TaskScheduleRepositoryImpl @Inject constructor(
    private val dao: TaskScheduleDao
) : TaskScheduleRepository {

    override fun observeByTaskId(taskId: Int): Flow<TaskSchedule?> = dao.observeByTaskId(taskId)
    override suspend fun getByTaskId(taskId: Int): TaskSchedule? = dao.getByTaskId(taskId)
    override suspend fun upsert(schedule: TaskSchedule) = dao.upsert(schedule)
    override suspend fun deleteByTaskId(taskId: Int) = dao.deleteByTaskId(taskId)
}
