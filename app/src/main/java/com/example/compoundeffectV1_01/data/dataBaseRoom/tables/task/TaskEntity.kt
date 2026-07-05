package com.example.compoundeffectV1_01.data.dataBaseRoom.tables.task

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.Relation
import com.example.compoundeffectV1_01.data.dataBaseRoom.tables.category.CategoryEntity
import com.example.compoundeffectV1_01.data.dataBaseRoom.tables.taskSchedule.TaskSchedule

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
    indices = [
        Index("categoryId"),
        Index("entityStatus"),
        Index(value = ["categoryId", "entityStatus"])
    ]
)
data class TaskEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int? = null,
    val taskMode: TaskMode = TaskMode.NORMAL,
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
    val siblingIndex: Int = 0,

    val entityStatus: String = TaskEntityStatus.ACTIVE,
    val draftCreatedAtEpochMillis: Long? = null,

    /** هدف کل به واحد پومودورو (مثلاً 1200) */
    val pomodoroTargetUnits: Int? = null,

    /** تعداد پومودوهای انجام‌شده */
    val pomodoroDoneUnits: Int = 0,
)




data class TaskWithSchedule(
    @Embedded val taskEntity: TaskEntity,

    @Relation(
        parentColumn = "id",
        entityColumn = "taskId"
    )
    val schedule: TaskSchedule?
)




enum class TaskMode { NORMAL, POMODORO }

object TaskEntityStatus {
    const val DRAFT = "DRAFT"
    const val ACTIVE = "ACTIVE"
}