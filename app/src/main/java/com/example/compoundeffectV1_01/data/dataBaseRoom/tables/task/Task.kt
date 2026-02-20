package com.example.compoundeffectV1_01.data.dataBaseRoom.tables.task

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.Relation
import com.example.compoundeffectV1_01.data.dataBaseRoom.tables.category.CategoryEntity
import com.example.compoundeffectV1_01.data.dataBaseRoom.tables.taskSchedule.TaskSchedule
import java.time.LocalDateTime

@Entity(
    tableName = "task",
    foreignKeys = [
        ForeignKey(
            entity = CategoryEntity::class,
            parentColumns = ["categoryId"],
            childColumns = ["categoryId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("categoryId")]
)
data class Task(
    @PrimaryKey(autoGenerate = true)
    val id: Int? = null,

    val name: String,
    val color: String,
    val description: String,

    val durationOverlap: Int = 0,
    val selected: Boolean,
    val changed: Boolean = false,

    val categoryId: Int? = null,
    val isCompleted: Boolean = false,
    val priority: Int = 0,
    val isExtended: Boolean = true,

    val parentTaskId: Int? = null,
    val siblingIndex: Int = 0
)




data class TaskWithSchedule(
    @Embedded val task: Task,

    @Relation(
        parentColumn = "id",
        entityColumn = "taskId"
    )
    val schedule: TaskSchedule?
)

