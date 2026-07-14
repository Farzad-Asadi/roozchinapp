package ir.roozchinapp.ui.analyticsScreen

data class PomodoroAnalyticsSummary(
    val plannedCount: Int = 0,
    val scheduledCount: Int = 0,
    val completedCount: Int = 0,
    val extraCompletedCount: Int = 0,

    val plannedFocusMinutes: Int = 0,
    val completedFocusMinutes: Int = 0,

    /**
     * مقدار نمایشی بین 0 تا 100.
     * مازاد completed جداگانه در extraCompletedCount نگهداری می‌شود.
     */
    val completionPercent: Float = 0f
)

data class PomodoroAnalyticsPoint(
    val dateEpochDay: Long,
    val label: String,

    val plannedCount: Int,
    val scheduledCount: Int,
    val completedCount: Int,

    val plannedFocusMinutes: Int,
    val completedFocusMinutes: Int
)

data class AnalyticsUiState(
    val selectedPeriod: AnalyticsPeriod = AnalyticsPeriod.TODAY,

    val summary: PomodoroAnalyticsSummary =
        PomodoroAnalyticsSummary(),

    val points: List<PomodoroAnalyticsPoint> =
        emptyList(),

    val isLoading: Boolean = false,
    val errorMessage: String? = null
)