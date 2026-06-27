package com.example.compoundeffectV1_01.data.dataBaseRoom.tables.task

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

object TaskChildRequirementStatus {
    const val WAITING = "WAITING"
    const val INCOMPLETE = "INCOMPLETE"
    const val COMPLETE = "COMPLETE"
    const val MISSED = "MISSED"
    const val SKIPPED = "SKIPPED"
    const val CANCELLED = "CANCELLED"
}

object TaskChildRequirementContextType {
    const val PARENT_LIFETIME = "PARENT_LIFETIME"
    const val DAY = "DAY"
    const val SCHEDULE_OCCURRENCE = "SCHEDULE_OCCURRENCE"
    const val RULE_OCCURRENCE = "RULE_OCCURRENCE"
    const val LIST_SESSION = "LIST_SESSION"
    const val LEARNING_CYCLE = "LEARNING_CYCLE"
    const val MANUAL = "MANUAL"
}

@Entity(
    tableName = "task_child_requirement",
    foreignKeys = [
        ForeignKey(
            entity = TaskChildRuleEntity::class,
            parentColumns = ["id"],
            childColumns = ["ruleId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("ruleId"),
        Index("parentTaskId"),
        Index("childTaskId"),
        Index("contextType"),
        Index("scheduleId"),
        Index("parentRuleScheduleId"),
        Index("occurrenceDateEpochDay"),
        Index("listSessionId"),
        Index("status"),
        Index("dueAtEpochMillis"),
        Index("sourceCatalogItemId"),
        Index(value = ["uniqueKey"], unique = true)
    ]
)
data class TaskChildRequirementEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val uniqueKey: String,

    val ruleId: Int,

    val parentTaskId: Int,
    val childTaskId: Int,

    val contextType: String,

    val scheduleId: Int? = null,
    val parentRuleScheduleId: Int? = null,
    val occurrenceDateEpochDay: Long? = null,

    val listSessionId: Int? = null,

    val slotIndex: Int = 0,

    val learningIndex: Int = 0,
    val learningTargetCount: Int? = null,

    val status: String = TaskChildRequirementStatus.INCOMPLETE,

    val dueAtEpochMillis: Long? = null,
    val expiresAtEpochMillis: Long? = null,

    val completedAtEpochMillis: Long? = null,
    val skippedAtEpochMillis: Long? = null,
    val missedAtEpochMillis: Long? = null,

    val sourceCatalogItemId: Int? = null,

    val note: String? = null,

    val createdAtEpochMillis: Long = System.currentTimeMillis(),
    val updatedAtEpochMillis: Long = System.currentTimeMillis()
)