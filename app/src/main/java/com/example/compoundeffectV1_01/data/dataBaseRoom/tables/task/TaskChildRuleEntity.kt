package com.example.compoundeffectV1_01.data.dataBaseRoom.tables.task

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

object TaskChildRuleType {
    const val ONCE_PER_PARENT_LIFETIME = "ONCE_PER_PARENT_LIFETIME"
    const val ONCE_PER_DAY = "ONCE_PER_DAY"
    const val N_TIMES_PER_DAY = "N_TIMES_PER_DAY"
    const val ONCE_PER_PARENT_OCCURRENCE = "ONCE_PER_PARENT_OCCURRENCE"
    const val N_TIMES_PER_PARENT_OCCURRENCE = "N_TIMES_PER_PARENT_OCCURRENCE"
    const val G5_LEARNING = "G5_LEARNING"
    const val MANUAL_LIST_ITEM = "MANUAL_LIST_ITEM"
    const val MANUAL_RESET = "MANUAL_RESET"
}

object TaskChildResetScope {
    const val PARENT_LIFETIME = "PARENT_LIFETIME"
    const val DAY = "DAY"
    const val PARENT_OCCURRENCE = "PARENT_OCCURRENCE"
    const val LIST_SESSION = "LIST_SESSION"
    const val LEARNING_CYCLE = "LEARNING_CYCLE"
    const val MANUAL = "MANUAL"
}

@Entity(
    tableName = "task_child_rule",
    foreignKeys = [
        ForeignKey(
            entity = TaskEntity::class,
            parentColumns = ["id"],
            childColumns = ["parentTaskId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = TaskEntity::class,
            parentColumns = ["id"],
            childColumns = ["childTaskId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("parentTaskId"),
        Index("childTaskId"),
        Index(value = ["parentTaskId", "childTaskId"], unique = true),
        Index("ruleType"),
        Index("resetScope"),
        Index("sourceCatalogItemId")
    ]
)
data class TaskChildRuleEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val parentTaskId: Int,
    val childTaskId: Int,

    val ruleType: String,
    val resetScope: String,

    val isRequired: Boolean = true,
    val isEnabled: Boolean = true,

    val sortOrder: Int = 0,

    val targetCount: Int = 1,

    val timesPerDay: Int = 1,
    val timesPerOccurrence: Int = 1,

    val daysMask: Int? = null,

    val g5TargetCount: Int = 5,

    val g5IntervalDaysCsv: String? = null,

    val dueTimeMinuteOfDay: Int? = null,
    val expiresAfterMinutes: Int? = null,

    val showInTimelineCard: Boolean = true,
    val showInBottomSheet: Boolean = true,

    val sourceCatalogItemId: Int? = null,

    val createdAtEpochMillis: Long = System.currentTimeMillis(),
    val updatedAtEpochMillis: Long = System.currentTimeMillis()
)