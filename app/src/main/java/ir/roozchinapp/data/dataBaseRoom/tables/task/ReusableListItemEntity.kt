package ir.roozchinapp.data.dataBaseRoom.tables.task

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

object ReusableListItemType {
    const val SHOPPING = "SHOPPING"
    const val CHECKLIST = "CHECKLIST"
    const val TRAVEL = "TRAVEL"
    const val TOOLKIT = "TOOLKIT"
    const val CUSTOM = "CUSTOM"
}

@Entity(
    tableName = "reusable_list_item",
    indices = [
        Index("itemType"),
        Index("title"),
        Index("normalizedTitle"),
        Index(value = ["itemType", "normalizedTitle"], unique = true),
        Index("usageCount"),
        Index("lastUsedAtEpochMillis"),
        Index("lastCompletedAtEpochMillis"),
        Index("isArchived")
    ]
)
data class ReusableListItemEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val itemType: String = ReusableListItemType.CHECKLIST,

    val title: String,

    val normalizedTitle: String,

    val categoryLabel: String? = null,

    val defaultQuantityText: String? = null,
    val defaultUnitText: String? = null,

    val usageCount: Int = 0,
    val completedCount: Int = 0,

    val lastUsedAtEpochMillis: Long? = null,
    val lastCompletedAtEpochMillis: Long? = null,

    val isArchived: Boolean = false,

    val createdAtEpochMillis: Long = System.currentTimeMillis(),
    val updatedAtEpochMillis: Long = System.currentTimeMillis()
)