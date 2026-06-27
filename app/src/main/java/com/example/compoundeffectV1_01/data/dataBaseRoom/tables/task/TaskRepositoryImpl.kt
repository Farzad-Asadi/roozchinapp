package com.example.compoundeffectV1_01.data.dataBaseRoom.tables.task


import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class TaskRepositoryImpl @Inject constructor(
    private val taskDao: TaskDao
) : TaskRepository {

    override suspend fun insertTask(vararg taskEntity: TaskEntity) =
        taskDao.insertTask(*taskEntity)

    override suspend fun insertTaskAndReturnId(taskEntity: TaskEntity): Int =
        taskDao.insertTaskAndReturnId(taskEntity).toInt()

    override suspend fun updateTask(taskEntity: TaskEntity) =
        taskDao.updateTask(taskEntity)

    override suspend fun deleteTask(taskEntity: TaskEntity) =
        taskDao.deleteTask(taskEntity)

    override fun observeTasksByCategory(categoryId: Int): Flow<List<TaskEntity>> =
        taskDao.observeTasksByCategory(categoryId)

    override suspend fun getTasksByCategory(categoryId: Int): List<TaskEntity> =
        taskDao.getTasksByCategory(categoryId)

    override suspend fun updateSiblingIndex(id: Int, siblingIndex: Int) =
        taskDao.updateSiblingIndex(id, siblingIndex)

    override suspend fun updateTaskParent(id: Int, parentTaskId: Int?) =
        taskDao.updateTaskParent(id, parentTaskId)

    override suspend fun getTaskById(id: Int): TaskEntity? =
        taskDao.getTaskById(id)

    override fun observeTasksWithScheduleByCategory(categoryId: Int): Flow<List<TaskWithSchedule>> =
        taskDao.observeTasksWithScheduleByCategory(categoryId)

    override fun observeScheduledCountByCategory(categoryId: Int): Flow<Int> =
        taskDao.observeScheduledCountByCategory(categoryId)

    override fun observeAllScheduledTasksWithSchedule(): Flow<List<TaskWithSchedule>> =
        taskDao.observeAllScheduledTasksWithSchedule()

    override suspend fun getTasksByCategoryOrdered(categoryId: Int): List<TaskEntity> =
        taskDao.getTasksByCategoryOrdered(categoryId)


    override suspend fun countChildren(taskId: Int): Int =
        taskDao.countChildren(taskId)


    override suspend fun setCompletedForIds(ids: List<Int>, done: Boolean) {
        if (ids.isEmpty()) return
        taskDao.setCompletedForIds(ids, done)
    }

    override suspend fun getSiblings(categoryId: Int, parentId: Int): List<TaskEntity> =
        taskDao.getSiblings(categoryId, parentId)

    override suspend fun shiftSiblingsDown(categoryId: Int, parentId: Int) =
        taskDao.shiftSiblingsDown(categoryId, parentId)

    override suspend fun normalizeNullParentsToRoot() =
        taskDao.normalizeNullParentsToRoot()

    override suspend fun completeAllInCategory(categoryId: Int) =
        taskDao.completeAllInCategory(categoryId)

    override suspend fun uncompleteAllInCategory(categoryId: Int) =
        taskDao.uncompleteAllInCategory(categoryId)

    override suspend fun deleteCompletedInCategory(categoryId: Int) =
        taskDao.deleteCompletedInCategory(categoryId)

    override suspend fun deleteAllInCategory(categoryId: Int) =
        taskDao.deleteAllInCategory(categoryId)

    override suspend fun incrementPomodoroDoneUnits(taskId: Int) =
        taskDao.incrementPomodoroDoneUnits(taskId)

    override fun observePomodoroDailyAdjustments(): Flow<List<PomodoroDailyAdjustmentEntity>> =
        taskDao.observePomodoroDailyAdjustments()

    override suspend fun adjustManualPomodoroDone(
        taskId: Int,
        dateEpochDay: Long,
        delta: Int
    ) =
        taskDao.adjustManualPomodoroDone(
            taskId = taskId,
            dateEpochDay = dateEpochDay,
            delta = delta
        )


}