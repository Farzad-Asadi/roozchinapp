package com.example.compoundeffectV1_01.data.backup.compound

import com.example.compoundeffectV1_01.data.dataBaseRoom.tables.appSystemInfo.AppSystemInfo
import com.example.compoundeffectV1_01.data.dataBaseRoom.tables.category.CategoryEntity
import com.example.compoundeffectV1_01.data.dataBaseRoom.tables.reminder.TaskReminderEntity
import com.example.compoundeffectV1_01.data.dataBaseRoom.tables.task.TaskEntity
import com.example.compoundeffectV1_01.data.dataBaseRoom.tables.taskSchedule.TaskSchedule

data class CompoundBackupData(
    val categories: List<CategoryEntity> = emptyList(),
    val taskEntities: List<TaskEntity> = emptyList(),
    val schedules: List<TaskSchedule> = emptyList(),
    val reminders: List<TaskReminderEntity> = emptyList(),
    val systemInfos: List<AppSystemInfo> = emptyList()
)