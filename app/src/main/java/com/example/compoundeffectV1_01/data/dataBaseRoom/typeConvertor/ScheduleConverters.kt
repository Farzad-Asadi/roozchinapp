package com.example.compoundeffectV1_01.data.dataBaseRoom.typeConvertor

import androidx.room.TypeConverter
import com.example.compoundeffectV1_01.data.dataBaseRoom.tables.reminder.BeforeAfter
import com.example.compoundeffectV1_01.data.dataBaseRoom.tables.reminder.ReminderMode
import com.example.compoundeffectV1_01.data.dataBaseRoom.tables.reminder.ReminderStrengthMode
import com.example.compoundeffectV1_01.data.dataBaseRoom.tables.reminder.StartEnd
import com.example.compoundeffectV1_01.data.dataBaseRoom.tables.task.TaskMode
import com.example.compoundeffectV1_01.data.dataBaseRoom.tables.taskSchedule.RepeatUnit
import com.example.compoundeffectV1_01.data.dataBaseRoom.tables.taskSchedule.ScheduleMode
import java.time.LocalDate
import java.time.LocalTime

class ScheduleConverters {

    @TypeConverter fun dateToString(v: LocalDate?): String? = v?.toString()
    @TypeConverter fun stringToDate(v: String?): LocalDate? = v?.let(LocalDate::parse)

    @TypeConverter fun timeToString(v: LocalTime?): String? = v?.toString()
    @TypeConverter fun stringToTime(v: String?): LocalTime? = v?.let(LocalTime::parse)

    @TypeConverter fun modeToString(v: ScheduleMode?): String? = v?.name
    @TypeConverter fun stringToMode(v: String?): ScheduleMode? = v?.let(ScheduleMode::valueOf)

    @TypeConverter fun toDb(v: RepeatUnit?): String? = v?.name
    @TypeConverter fun fromDb(v: String?): RepeatUnit? = v?.let(RepeatUnit::valueOf)

    // ===== Reminder enums =====
    @TypeConverter fun reminderModeToDb(v: ReminderMode?): String? = v?.name
    @TypeConverter fun reminderModeFromDb(v: String?): ReminderMode? = v?.let(ReminderMode::valueOf)

    @TypeConverter fun strengthToDb(v: ReminderStrengthMode?): String? = v?.name
    @TypeConverter fun strengthFromDb(v: String?): ReminderStrengthMode? = v?.let(ReminderStrengthMode::valueOf)

    @TypeConverter fun beforeAfterToDb(v: BeforeAfter?): String? = v?.name
    @TypeConverter fun beforeAfterFromDb(v: String?): BeforeAfter? = v?.let(BeforeAfter::valueOf)

    @TypeConverter fun startEndToDb(v: StartEnd?): String? = v?.name
    @TypeConverter fun startEndFromDb(v: String?): StartEnd? = v?.let(StartEnd::valueOf)

    @TypeConverter fun taskModeToDb(v: TaskMode?): String? = v?.name
    @TypeConverter fun taskModeFromDb(v: String?): TaskMode? = v?.let(TaskMode::valueOf)
}