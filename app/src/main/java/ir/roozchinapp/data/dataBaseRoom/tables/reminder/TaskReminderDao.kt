package ir.roozchinapp.data.dataBaseRoom.tables.reminder

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface TaskReminderDao {

    // ===== Observe =====
    @Query("""
        SELECT * FROM task_reminder
        WHERE scheduleId = :scheduleId
        ORDER BY id ASC
    """)
    fun observeByScheduleId(scheduleId: Int): kotlinx.coroutines.flow.Flow<List<TaskReminderEntity>>

    @Query("""
        SELECT * FROM task_reminder
        WHERE scheduleId = :scheduleId
        ORDER BY id ASC
    """)
    suspend fun getByScheduleId(scheduleId: Int): List<TaskReminderEntity>

    @Query("SELECT * FROM task_reminder WHERE id = :id LIMIT 1")
    suspend fun getById(id: Int): TaskReminderEntity?

    // ===== Write =====


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: TaskReminderEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(list: List<TaskReminderEntity>)

    @Delete
    suspend fun delete(entity: TaskReminderEntity)

    @Query("DELETE FROM task_reminder WHERE id = :id")
    suspend fun deleteById(id: Int)

    @Query("DELETE FROM task_reminder WHERE scheduleId = :scheduleId")
    suspend fun deleteByScheduleId(scheduleId: Int)



    //region Backup / Restore

    @Query("SELECT * FROM task_reminder")
    suspend fun getAllRemindersForBackup(): List<TaskReminderEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRemindersForRestore(reminders: List<TaskReminderEntity>)

    @Query("DELETE FROM task_reminder")
    suspend fun deleteAllRemindersForRestore()

//endregion
}
