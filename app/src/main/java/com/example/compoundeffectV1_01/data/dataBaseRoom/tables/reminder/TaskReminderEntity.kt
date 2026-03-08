package com.example.compoundeffectV1_01.data.dataBaseRoom.tables.reminder

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.compoundeffectV1_01.data.dataBaseRoom.tables.taskSchedule.TaskSchedule

@Entity(
    tableName = "task_reminder",
    foreignKeys = [
        ForeignKey(
            entity = TaskSchedule::class,
            parentColumns = ["id"],
            childColumns = ["scheduleId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("scheduleId")]
)
data class TaskReminderEntity(
    @PrimaryKey(autoGenerate = true) val id: Int? = 0,
    val scheduleId: Int,
    val title: String? = null,

    val mode: ReminderMode,

    // allocated
    val offsetDays: Int,
    val offsetHours: Int,
    val offsetMinutes: Int,
    val beforeAfter: BeforeAfter,
    val anchor: StartEnd,

    // fixed
    val fixedMinuteOfDay: Int?,

    // intervallic
//    val intervalStartMinuteOfDay: Int?,
//    val intervalEndMinuteOfDay: Int?,
//    val everyMinutesTotal: Int?, // everyHours*60 + everyMinutes

    // strength
    val strength: ReminderStrengthMode,
    val vibrate: Boolean,

    // sound
    val alarmSoundUri: String?,

    // captcha
    val captchaEnabled: Boolean,
)



enum class ReminderMode { ALLOCATED, FIXED_TIME }
enum class ReminderStrengthMode { NOTIFICATION, ALARM, ALARM_AND_CAPTCHA }
enum class BeforeAfter { BEFORE, AFTER }
enum class StartEnd { START, END }