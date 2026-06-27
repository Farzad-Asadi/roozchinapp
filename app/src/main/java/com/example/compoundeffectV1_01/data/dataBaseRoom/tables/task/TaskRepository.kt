package com.example.compoundeffectV1_01.data.dataBaseRoom.tables.task

import kotlinx.coroutines.flow.Flow


interface TaskRepository {


    suspend fun insertTask(vararg taskEntity: TaskEntity)

    suspend fun insertTaskAndReturnId(taskEntity: TaskEntity): Int

    suspend fun updateTask(taskEntity: TaskEntity)

    suspend fun deleteTask(taskEntity: TaskEntity)

    fun observeTasksByCategory(categoryId: Int): Flow<List<TaskEntity>>

    suspend fun getTasksByCategory(categoryId: Int): List<TaskEntity>

    suspend fun updateSiblingIndex(id: Int, siblingIndex: Int)

    suspend fun updateTaskParent(id: Int, parentTaskId: Int?)

    suspend fun getTaskById(id: Int): TaskEntity?

    fun observeTasksWithScheduleByCategory(categoryId: Int): Flow<List<TaskWithSchedule>>

    fun observeScheduledCountByCategory(categoryId: Int): Flow<Int>

    fun observeAllScheduledTasksWithSchedule(): Flow<List<TaskWithSchedule>>

    suspend fun getTasksByCategoryOrdered(categoryId: Int): List<TaskEntity>

    suspend fun countChildren(taskId: Int): Int

    suspend fun setCompletedForIds(ids: List<Int>, done: Boolean)

    suspend fun getSiblings(categoryId: Int, parentId: Int): List<TaskEntity>

    suspend fun shiftSiblingsDown(categoryId: Int, parentId: Int)

    suspend fun normalizeNullParentsToRoot()


    suspend fun completeAllInCategory(categoryId: Int)

    suspend fun uncompleteAllInCategory(categoryId: Int)

    suspend fun deleteCompletedInCategory(categoryId: Int)

    suspend fun deleteAllInCategory(categoryId: Int)

    suspend fun incrementPomodoroDoneUnits(taskId: Int)

    fun observePomodoroDailyAdjustments(): Flow<List<PomodoroDailyAdjustmentEntity>>

    suspend fun adjustManualPomodoroDone(
        taskId: Int,
        dateEpochDay: Long,
        delta: Int
    )
}