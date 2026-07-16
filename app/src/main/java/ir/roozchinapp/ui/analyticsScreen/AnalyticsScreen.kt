package ir.roozchinapp.ui.analyticsScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import kotlin.math.roundToInt

@Composable
fun AnalyticsScreen(
    viewModel: AnalyticsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.refreshDate()
    }

    CompositionLocalProvider(
        LocalLayoutDirection provides LayoutDirection.Rtl
    ) {
        AnalyticsScreenContent(
            uiState = uiState,
            onSelectPeriod = viewModel::selectPeriod
        )
    }
}

@Composable
private fun AnalyticsScreenContent(
    uiState: AnalyticsUiState,
    onSelectPeriod: (AnalyticsPeriod) -> Unit
) {
    Scaffold(
        topBar = {
            AnalyticsTopBar()
        }
    ) { padding ->

        if (uiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }

            return@Scaffold
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(
                    horizontal = 14.dp,
                    vertical = 10.dp
                ),
            verticalArrangement =
            Arrangement.spacedBy(12.dp)
        ) {
            AnalyticsPeriodSelector(
                selectedPeriod = uiState.selectedPeriod,
                onSelectPeriod = onSelectPeriod
            )

            PomodoroSummarySection(
                summary = uiState.summary
            )

            PomodoroAnalyticsChart(
                selectedPeriod = uiState.selectedPeriod,
                summary = uiState.summary,
                points = uiState.points
            )

            PomodoroTaskAnalyticsSection(
                selectedPeriod = uiState.selectedPeriod,
                items = uiState.taskItems
            )

            uiState.errorMessage?.let { message ->
                Text(
                    text = message,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.error
                )
            }

            Spacer(Modifier.size(12.dp))
        }
    }
}

@Composable
private fun AnalyticsTopBar() {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 0.dp,
        shadowElevation = 4.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = 16.dp,
                    vertical = 11.dp
                ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Filled.Analytics,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )

            Spacer(Modifier.size(12.dp))

            Text(
                text = "آمار پومودورو",
                style = MaterialTheme.typography.titleLarge
            )
        }
    }
}

@Composable
private fun AnalyticsPeriodSelector(
    selectedPeriod: AnalyticsPeriod,
    onSelectPeriod: (AnalyticsPeriod) -> Unit
) {
    val periods = remember {
        listOf(
            AnalyticsPeriod.TODAY,
            AnalyticsPeriod.WEEK,
            AnalyticsPeriod.MONTH
        )
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        periods.forEach { period ->
            val selected = period == selectedPeriod
            val shape = RoundedCornerShape(14.dp)

            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(shape)
                    .background(
                        color = if (selected) {
                            MaterialTheme.colorScheme.primaryContainer
                        } else {
                            MaterialTheme.colorScheme.surfaceContainerHigh
                        },
                        shape = shape
                    )
                    .clickable {
                        onSelectPeriod(period)
                    }
                    .padding(
                        horizontal = 6.dp,
                        vertical = 9.dp
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = period.displayTitle(),
                    maxLines = 1,
                    style = MaterialTheme.typography.labelLarge,
                    color = if (selected) {
                        MaterialTheme.colorScheme.onPrimaryContainer
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    }
                )
            }
        }
    }
}

@Composable
private fun PomodoroSummarySection(
    summary: PomodoroAnalyticsSummary,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement =
        Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "خلاصه پومودورو",
            style = MaterialTheme.typography.titleSmall
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement =
            Arrangement.spacedBy(8.dp)
        ) {
            CompactAnalyticsMetricCard(
                title = "برنامه",
                value = summary.plannedCount.toString(),
                modifier = Modifier.weight(1f)
            )

            CompactAnalyticsMetricCard(
                title = "زمان‌بندی",
                value = summary.scheduledCount.toString(),
                modifier = Modifier.weight(1f)
            )

            CompactAnalyticsMetricCard(
                title = "انجام",
                value = summary.completedCount.toString(),
                modifier = Modifier.weight(1f)
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement =
            Arrangement.spacedBy(8.dp)
        ) {
            CompactAnalyticsMetricCard(
                title = "تحقق",
                value =
                "${summary.completionPercent.roundToInt()}٪",
                modifier = Modifier.weight(1f)
            )

            CompactAnalyticsMetricCard(
                title = "تمرکز برنامه",
                value = formatFocusMinutes(
                    summary.plannedFocusMinutes
                ),
                modifier = Modifier.weight(1f)
            )

            CompactAnalyticsMetricCard(
                title = "تمرکز انجام",
                value = formatFocusMinutes(
                    summary.completedFocusMinutes
                ),
                modifier = Modifier.weight(1f)
            )
        }

        if (summary.extraCompletedCount > 0) {
            ExtraPomodoroSummaryCard(
                count = summary.extraCompletedCount
            )
        }
    }
}

@Composable
private fun CompactAnalyticsMetricCard(
    title: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor =
            MaterialTheme.colorScheme
                .surfaceContainerHigh
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = 6.dp,
                    vertical = 8.dp
                ),
            horizontalAlignment =
            Alignment.CenterHorizontally,
            verticalArrangement =
            Arrangement.spacedBy(2.dp)
        ) {
            Text(
                text = title,
                modifier = Modifier.fillMaxWidth(),
                maxLines = 1,
                textAlign = TextAlign.Center,
                style =
                MaterialTheme.typography.labelSmall,
                color =
                MaterialTheme.colorScheme
                    .onSurfaceVariant
            )

            Text(
                text = value,
                modifier = Modifier.fillMaxWidth(),
                maxLines = 1,
                textAlign = TextAlign.Center,
                style =
                MaterialTheme.typography.titleMedium,
                color =
                MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
private fun ExtraPomodoroSummaryCard(
    count: Int,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor =
            MaterialTheme.colorScheme
                .primaryContainer
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = 12.dp,
                    vertical = 7.dp
                ),
            horizontalArrangement =
            Arrangement.SpaceBetween,
            verticalAlignment =
            Alignment.CenterVertically
        ) {
            Text(
                text = "پومودوروی مازاد",
                style =
                MaterialTheme.typography.labelMedium,
                color =
                MaterialTheme.colorScheme
                    .onPrimaryContainer
            )

            Text(
                text = "+$count",
                style =
                MaterialTheme.typography.titleSmall,
                color =
                MaterialTheme.colorScheme
                    .onPrimaryContainer
            )
        }
    }
}



private fun AnalyticsPeriod.displayTitle(): String {
    return when (this) {
        AnalyticsPeriod.TODAY -> "امروز"
        AnalyticsPeriod.WEEK -> "هفته"
        AnalyticsPeriod.MONTH -> "ماه"
    }
}

private fun formatFocusMinutes(
    totalMinutes: Int
): String {
    val safeMinutes =
        totalMinutes.coerceAtLeast(0)

    val hours =
        safeMinutes / 60

    val minutes =
        safeMinutes % 60

    return buildString {
        append(hours)
        append(":")
        append(
            minutes
                .toString()
                .padStart(
                    length = 2,
                    padChar = '0'
                )
        )
    }
}