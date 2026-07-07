package ir.roozchinapp.data.backup.compound

import ir.roozchinapp.data.dataBaseRoom.tables.appSystemInfo.AppSystemInfo
import ir.roozchinapp.data.dataBaseRoom.tables.category.CategoryEntity
import ir.roozchinapp.data.dataBaseRoom.tables.reminder.TaskReminderEntity
import ir.roozchinapp.data.dataBaseRoom.tables.task.TaskEntity
import ir.roozchinapp.data.dataBaseRoom.tables.taskSchedule.TaskSchedule


data class CompoundBackupData(
    val categories: List<CategoryEntity> = emptyList(),
    val taskEntities: List<TaskEntity> = emptyList(),
    val schedules: List<TaskSchedule> = emptyList(),
    val reminders: List<TaskReminderEntity> = emptyList(),
    val systemInfos: List<AppSystemInfo> = emptyList()
)