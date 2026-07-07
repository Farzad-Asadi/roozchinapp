package ir.roozchinapp.data.dataBaseRoom.tables.task

data class TaskChildRequirementSummaryUi(
    val parentTaskId: Int,

    val scheduleId: Int?,
    val parentRuleScheduleId: Int?,
    val occurrenceDateEpochDay: Long?,

    val totalCount: Int,
    val completedCount: Int
)