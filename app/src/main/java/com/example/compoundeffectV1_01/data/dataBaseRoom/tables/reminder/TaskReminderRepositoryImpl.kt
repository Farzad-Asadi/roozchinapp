package com.example.compoundeffectV1_01.data.dataBaseRoom.tables.reminder

import javax.inject.Inject

class TaskReminderRepositoryImpl @Inject constructor(
    private val dao: TaskReminderDao
) : TaskReminderRepository {
   override fun observeByScheduleId(scheduleId: Int) = dao.observeByScheduleId(scheduleId)

   override suspend fun getByScheduleId(scheduleId: Int) = dao.getByScheduleId(scheduleId)

   override suspend fun getById(id: Int) = dao.getById(id)

   override suspend fun upsert(entity: TaskReminderEntity): Int {
        // Room @Insert برمی‌گردونه Long (rowId). برای INSERT جدید مفیده.
        // برای UPDATE همون id قبلی معمولاً معتبره.
        val rowId = dao.upsert(entity)
        return if (entity.id != 0) entity.id else rowId.toInt()
    }

   override suspend fun upsertAll(list: List<TaskReminderEntity>) = dao.upsertAll(list)

   override suspend fun delete(entity: TaskReminderEntity) = dao.delete(entity)

   override suspend fun deleteById(id: Int) = dao.deleteById(id)

   override suspend fun deleteByScheduleId(scheduleId: Int) = dao.deleteByScheduleId(scheduleId)
}