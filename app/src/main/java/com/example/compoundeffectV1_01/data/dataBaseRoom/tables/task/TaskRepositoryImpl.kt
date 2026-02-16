package com.example.compoundeffectV1_01.data.dataBaseRoom.tables.task


import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime
import javax.inject.Inject

class TaskRepositoryImpl @Inject constructor(
    private val taskDao: TaskDao
) : TaskRepository {

    override suspend fun insertTask(vararg task: Task) =
        taskDao.insertTask(*task)

    override suspend fun insertTaskAndReturnId(task: Task): Int =
        taskDao.insertTaskAndReturnId(task).toInt()

    override suspend fun updateTask(task: Task) =
            taskDao.updateTask(task)

    override suspend fun deleteTask(task: Task) =
                taskDao.deleteTask(task)

    override fun observeTasksByCategory(categoryId: Int): Flow<List<Task>> =
        taskDao.observeTasksByCategory(categoryId)

    override suspend fun getTaskById(id: Int): Task? =
        taskDao.getTaskById(id)

    override fun observeTasksWithScheduleByCategory(categoryId: Int): Flow<List<TaskWithSchedule>> =
        taskDao.observeTasksWithScheduleByCategory(categoryId)

    override fun observeScheduledCountByCategory(categoryId: Int): Flow<Int> =
        taskDao.observeScheduledCountByCategory(categoryId)

    override fun observeAllScheduledTasksWithSchedule(): Flow<List<TaskWithSchedule>> =
        taskDao.observeAllScheduledTasksWithSchedule()



}