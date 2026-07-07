package ir.roozchinapp.data.dataBaseRoom.tables.task

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

object TaskListSessionStatus {
    const val ACTIVE = "ACTIVE"
    const val CLOSED = "CLOSED"
    const val ARCHIVED = "ARCHIVED"
    const val CANCELLED = "CANCELLED"
}

object TaskListSessionType {
    const val SHOPPING = "SHOPPING"
    const val CHECKLIST = "CHECKLIST"
    const val TRAVEL = "TRAVEL"
    const val TOOLKIT = "TOOLKIT"
    const val CUSTOM = "CUSTOM"
}

@Entity(
    tableName = "task_list_session",
    foreignKeys = [
        ForeignKey(
            entity = TaskEntity::class,
            parentColumns = ["id"],
            childColumns = ["parentTaskId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("parentTaskId"),
        Index("sessionType"),
        Index("status"),
        Index("startedAtEpochMillis"),
        Index("closedAtEpochMillis")
    ]
)
data class TaskListSessionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val parentTaskId: Int,

    val sessionType: String = TaskListSessionType.CHECKLIST,

    val title: String? = null,

    val status: String = TaskListSessionStatus.ACTIVE,

    val startedAtEpochMillis: Long = System.currentTimeMillis(),
    val closedAtEpochMillis: Long? = null,

    val createdAtEpochMillis: Long = System.currentTimeMillis(),
    val updatedAtEpochMillis: Long = System.currentTimeMillis()
)