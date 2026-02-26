package com.example.compoundeffectV1_01.data.dataBaseRoom.tables.taskSchedule

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.Relation
import com.example.compoundeffectV1_01.data.dataBaseRoom.tables.task.Task
import java.time.LocalDate
import java.time.LocalTime



@Entity(
    tableName = "task_schedule",
    indices = [Index(value = ["taskId"])],
    foreignKeys = [
        ForeignKey(
            entity = Task::class,
            parentColumns = ["id"],
            childColumns = ["taskId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class TaskSchedule(
    @PrimaryKey(autoGenerate = true)
    val id: Int? = null,
    val taskId: Int,
    val title: String? = null,
    val mode: ScheduleMode = ScheduleMode.TIME_RANGE,

    // TIME_RANGE
    val dateEpochDay: Long? = null,
    val startMinuteOfDay: Int? = null,
    val endMinuteOfDay: Int? = null,

    // AMOUNT_OF_TIME
    val durationMinutes: Int? = null,

    // POMODORO
    val focusMinutes: Int? = null,
    val shortBreakMinutes: Int? = null,
    val longBreakMinutes: Int? = null,
    val longBreakEvery: Int? = null,
    val isLongBreak: Boolean = false,
    val pomodoroUnitsPerDay: Int? = null,


    // common
    val reminderMinutesBefore: Int? = null,
    val inPallet: Boolean = false,
    val repeating: Boolean = false,
    val repeatInterval: Int? = null,
    val repeatUnit: RepeatUnit? = null,
    val weekdaysMask: Int? = null,

)







enum class ScheduleMode { TIME_RANGE, AMOUNT_OF_TIME, POMODORO  }
enum class RepeatUnit { DAY, WEEK, MONTH, YEAR }



