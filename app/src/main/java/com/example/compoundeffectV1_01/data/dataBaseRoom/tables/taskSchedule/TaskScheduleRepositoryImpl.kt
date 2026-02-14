package com.example.compoundeffectV1_01.data.dataBaseRoom.tables.taskSchedule

import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

class TaskScheduleRepositoryImpl @Inject constructor(
    private val dao: TaskScheduleDao
) : TaskScheduleRepository {

    override fun observeByTaskId(taskId: Int): Flow<TaskSchedule?> = dao.observeByTaskId(taskId)
    override suspend fun getByTaskId(taskId: Int): TaskSchedule? = dao.getByTaskId(taskId)
    override suspend fun upsert(schedule: TaskSchedule) = dao.upsert(schedule)
    override suspend fun deleteByTaskId(taskId: Int) = dao.deleteByTaskId(taskId)



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

    override fun observeAllTimeRangeSchedulesWithTask() =
        dao.observeAllTimeRangeSchedulesWithTask()

    override suspend fun deleteById(scheduleId: Int) =
        dao.deleteById(scheduleId)

    override suspend fun countByTaskId(taskId: Int): Int =
            dao.countByTaskId(taskId)

}
