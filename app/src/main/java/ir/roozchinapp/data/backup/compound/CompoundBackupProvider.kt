package ir.roozchinapp.data.backup.compound

import androidx.room.withTransaction
import com.google.gson.Gson
import ir.roozchinapp.data.backup.core.BackupModuleProvider
import ir.roozchinapp.data.dataBaseRoom.appDataBase.AppDatabase
import ir.roozchinapp.data.dataBaseRoom.tables.appSystemInfo.SystemDao
import ir.roozchinapp.data.dataBaseRoom.tables.category.CategoryDao
import ir.roozchinapp.data.dataBaseRoom.tables.reminder.TaskReminderDao
import ir.roozchinapp.data.dataBaseRoom.tables.task.TaskDao
import ir.roozchinapp.data.dataBaseRoom.tables.taskSchedule.TaskScheduleDao
import javax.inject.Inject

class CompoundBackupProvider @Inject constructor(
    private val db: AppDatabase,
    private val categoryDao: CategoryDao,
    private val taskDao: TaskDao,
    private val scheduleDao: TaskScheduleDao,
    private val reminderDao: TaskReminderDao,
    private val systemDao: SystemDao,
    private val gson: Gson
) : BackupModuleProvider {

    override val moduleName: String = "compound"

    override suspend fun exportData(): String {
        val data = CompoundBackupData(
            categories = categoryDao.getAllCategoriesForBackup(),
            taskEntities = taskDao.getAllTasksForBackup(),
            schedules = scheduleDao.getAllSchedulesForBackup(),
            reminders = reminderDao.getAllRemindersForBackup(),
            systemInfos = systemDao.getAllAppSystemInfo()
        )

        return gson.toJson(data)
    }

    override suspend fun importData(json: String) {
        val data = gson.fromJson(
            json,
            CompoundBackupData::class.java
        )

        db.withTransaction {
            // Delete order: children first
            reminderDao.deleteAllRemindersForRestore()
            scheduleDao.deleteAllSchedulesForRestore()
            taskDao.deleteAllTasksForRestore()
            categoryDao.deleteAllCategoriesForRestore()
            systemDao.deleteAllSystemInfosForRestore()

            // Insert order: parents first
            categoryDao.insertCategoriesForRestore(data.categories)
            taskDao.insertTasksForRestore(data.taskEntities)
            scheduleDao.insertSchedulesForRestore(data.schedules)
            reminderDao.insertRemindersForRestore(data.reminders)
            systemDao.insertSystemInfosForRestore(data.systemInfos)
        }
    }
}