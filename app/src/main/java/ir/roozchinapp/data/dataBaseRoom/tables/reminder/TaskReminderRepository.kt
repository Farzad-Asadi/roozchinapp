package ir.roozchinapp.data.dataBaseRoom.tables.reminder

interface TaskReminderRepository {

    fun observeByScheduleId(scheduleId: Int): kotlinx.coroutines.flow.Flow<List<TaskReminderEntity>>

    suspend fun getByScheduleId(scheduleId: Int): List<TaskReminderEntity>

    suspend fun getById(id: Int): TaskReminderEntity?

    suspend fun upsert(entity: TaskReminderEntity): Int

    suspend fun upsertAll(list: List<TaskReminderEntity>)


    suspend fun delete(entity: TaskReminderEntity)

    suspend fun deleteById(id: Int)

    suspend fun deleteByScheduleId(scheduleId: Int)
}