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
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            AnalyticsPeriodSelector(
                selectedPeriod = uiState.selectedPeriod,
                onSelectPeriod = onSelectPeriod
            )

            Text(
                text = "خلاصه پومودورو",
                style = MaterialTheme.typography.titleMedium
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                AnalyticsMetricCard(
                    title = "برنامه",
                    value = uiState.summary.plannedCount.toString(),
                    modifier = Modifier.weight(1f)
                )

                AnalyticsMetricCard(
                    title = "زمان‌بندی‌شده",
                    value = uiState.summary.scheduledCount.toString(),
                    modifier = Modifier.weight(1f)
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                AnalyticsMetricCard(
                    title = "انجام‌شده",
                    value = uiState.summary.completedCount.toString(),
                    modifier = Modifier.weight(1f)
                )

                AnalyticsMetricCard(
                    title = "تحقق برنامه",
                    value = "${uiState.summary.completionPercent.roundToInt()}٪",
                    modifier = Modifier.weight(1f)
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                AnalyticsMetricCard(
                    title = "تمرکز برنامه‌ریزی‌شده",
                    value = formatFocusMinutes(
                        uiState.summary.plannedFocusMinutes
                    ),
                    modifier = Modifier.weight(1f)
                )

                AnalyticsMetricCard(
                    title = "تمرکز انجام‌شده",
                    value = formatFocusMinutes(
                        uiState.summary.completedFocusMinutes
                    ),
                    modifier = Modifier.weight(1f)
                )
            }

            if (uiState.summary.extraCompletedCount > 0) {
                AnalyticsMetricCard(
                    title = "پومودوروی مازاد",
                    value = uiState.summary.extraCompletedCount.toString(),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            AnalyticsChartPlaceholder(
                selectedPeriod = uiState.selectedPeriod,
                pointCount = uiState.points.size
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
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Filled.Analytics,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(26.dp)
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
                    .padding(horizontal = 8.dp, vertical = 11.dp),
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
private fun AnalyticsMetricCard(
    title: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor =
            MaterialTheme.colorScheme.surfaceContainerHigh
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
private fun AnalyticsChartPlaceholder(
    selectedPeriod: AnalyticsPeriod,
    pointCount: Int
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor =
            MaterialTheme.colorScheme.surfaceContainerHigh
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "نمودار ${selectedPeriod.displayTitle()}",
                style = MaterialTheme.typography.titleMedium
            )

            HorizontalDivider()

            Text(
                text = if (pointCount == 0) {
                    "در مرحله بعد داده‌های واقعی پومودورو و نمودار این بخش متصل می‌شوند."
                } else {
                    "$pointCount نقطه آماری برای نمایش آماده است."
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 34.dp),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
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
    val safeMinutes = totalMinutes.coerceAtLeast(0)

    val hours = safeMinutes / 60
    val minutes = safeMinutes % 60

    return when {
        hours > 0 && minutes > 0 ->
            "$hours ساعت و $minutes دقیقه"

        hours > 0 ->
            "$hours ساعت"

        else ->
            "$minutes دقیقه"
    }
}