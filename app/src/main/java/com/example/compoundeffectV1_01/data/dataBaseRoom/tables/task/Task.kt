package com.example.compoundeffectV1_01.data.dataBaseRoom.tables.task

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation
import com.example.compoundeffectV1_01.data.dataBaseRoom.tables.taskSchedule.TaskSchedule
import java.time.LocalDateTime

@Entity(tableName = "task")
data class Task(
    @PrimaryKey(autoGenerate = true)
    val id: Int? = null,

    val name: String,
    val color: String,


    val description: String,

    val inPallet: Boolean = false,
    val inSchedule: Boolean = false,

    val durationOverlap: Int = 0,
    val selected: Boolean,
    val changed: Boolean = false,

    val categoryId: Int? = null,     // تسک مربوط به کدام category
    val isCompleted: Boolean = false,
    val priority: Int = 0            // 0..2 مثلا
)



data class TaskWithSchedule(
    @Embedded val task: Task,

    @Relation(
        parentColumn = "id",
        entityColumn = "taskId"
    )
    val schedule: TaskSchedule?
)

