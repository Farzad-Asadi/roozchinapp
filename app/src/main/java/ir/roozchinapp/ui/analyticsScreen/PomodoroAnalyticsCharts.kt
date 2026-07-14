package ir.roozchinapp.ui.analyticsScreen

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

@Composable
internal fun PomodoroAnalyticsChart(
    selectedPeriod: AnalyticsPeriod,
    summary: PomodoroAnalyticsSummary,
    points: List<PomodoroAnalyticsPoint>,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
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
                text = analyticsChartTitle(selectedPeriod),
                style = MaterialTheme.typography.titleMedium
            )

            Text(
                text = analyticsChartSubtitle(selectedPeriod),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            HorizontalDivider()

            when (selectedPeriod) {
                AnalyticsPeriod.TODAY -> {
                    TodayPomodoroCompletionChart(
                        summary = summary
                    )
                }

                AnalyticsPeriod.WEEK,
                AnalyticsPeriod.MONTH -> {
                    PeriodPomodoroBarChart(
                        period = selectedPeriod,
                        points = points
                    )
                }
            }
        }
    }
}

@Composable
private fun TodayPomodoroCompletionChart(
    summary: PomodoroAnalyticsSummary
) {
    val percentage =
        summary.completionPercent.coerceIn(
            minimumValue = 0f,
            maximumValue = 100f
        )

    val animatedProgress by animateFloatAsState(
        targetValue = percentage / 100f,
        label = "todayPomodoroCompletion"
    )

    val trackColor =
        MaterialTheme.colorScheme.outlineVariant.copy(
            alpha = 0.55f
        )

    val progressColor =
        MaterialTheme.colorScheme.primary

    val fulfilledCount =
        min(
            summary.completedCount.coerceAtLeast(0),
            summary.plannedCount.coerceAtLeast(0)
        )

    val remainingCount =
        (
                summary.plannedCount -
                        fulfilledCount
                )
            .coerceAtLeast(0)

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Box(
            modifier = Modifier.size(190.dp),
            contentAlignment = Alignment.Center
        ) {
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(190.dp)
                    .padding(18.dp)
            ) {
                val strokeWidth = 18.dp.toPx()

                drawArc(
                    color = trackColor,
                    startAngle = -90f,
                    sweepAngle = 360f,
                    useCenter = false,
                    style = Stroke(
                        width = strokeWidth,
                        cap = StrokeCap.Round
                    )
                )

                if (animatedProgress > 0f) {
                    drawArc(
                        color = progressColor,
                        startAngle = -90f,
                        sweepAngle =
                        360f * animatedProgress,
                        useCenter = false,
                        style = Stroke(
                            width = strokeWidth,
                            cap = StrokeCap.Round
                        )
                    )
                }
            }

            Column(
                horizontalAlignment =
                Alignment.CenterHorizontally,
                verticalArrangement =
                Arrangement.Center
            ) {
                Text(
                    text =
                    if (summary.plannedCount > 0) {
                        "${percentage.roundToInt()}٪"
                    } else {
                        "—"
                    },
                    style =
                    MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                Text(
                    text =
                    if (summary.plannedCount > 0) {
                        "${summary.completedCount} از ${summary.plannedCount}"
                    } else {
                        "${summary.completedCount} انجام‌شده"
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color =
                    MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement =
            Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            AnalyticsChartLegendItem(
                color = progressColor,
                text = "$fulfilledCount انجام‌شده"
            )

            AnalyticsChartLegendItem(
                color = trackColor,
                text = "$remainingCount باقی‌مانده"
            )
        }

        if (summary.plannedCount == 0) {
            Text(
                text = "برای امروز هدف پومودورو تعریف نشده است.",
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodySmall,
                color =
                MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        if (summary.extraCompletedCount > 0) {
            Text(
                text =
                "${summary.extraCompletedCount} پومودورو بیشتر از برنامه انجام شده است.",
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
private fun PeriodPomodoroBarChart(
    period: AnalyticsPeriod,
    points: List<PomodoroAnalyticsPoint>
) {
    val plannedColor =
        MaterialTheme.colorScheme.outlineVariant

    val completedColor =
        MaterialTheme.colorScheme.primary

    val displayPoints = remember(points) {
        /*
         * کل صفحه RTL است؛ با reverse کردن،
         * جدیدترین روز در سمت راست نمودار قرار می‌گیرد.
         */
        points.asReversed()
    }

    val maxCount =
        max(
            1,
            points.maxOfOrNull { point ->
                max(
                    point.plannedCount,
                    point.completedCount
                )
            } ?: 1
        )

    val itemWidth =
        when (period) {
            AnalyticsPeriod.WEEK -> 64.dp
            AnalyticsPeriod.MONTH -> 50.dp
            AnalyticsPeriod.TODAY -> 64.dp
        }

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement =
            Arrangement.spacedBy(18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AnalyticsChartLegendItem(
                color = plannedColor,
                text = "برنامه"
            )

            AnalyticsChartLegendItem(
                color = completedColor,
                text = "انجام‌شده"
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(scrollState),
            horizontalArrangement =
            Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.Bottom
        ) {
            displayPoints.forEach { point ->
                PomodoroDayBarGroup(
                    point = point,
                    maxCount = maxCount,
                    itemWidth = itemWidth,
                    plannedColor = plannedColor,
                    completedColor = completedColor
                )
            }
        }

        Text(
            text =
            if (period == AnalyticsPeriod.WEEK) {
                "جدیدترین روز در سمت راست نمایش داده می‌شود."
            } else {
                "برای مشاهده تمام ۳۰ روز، نمودار را به چپ و راست بکشید."
            },
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.labelSmall,
            color =
            MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun PomodoroDayBarGroup(
    point: PomodoroAnalyticsPoint,
    maxCount: Int,
    itemWidth: Dp,
    plannedColor: Color,
    completedColor: Color
) {
    Column(
        modifier = Modifier.width(itemWidth),
        horizontalAlignment =
        Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.height(176.dp),
            horizontalArrangement =
            Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.Bottom
        ) {
            AnalyticsVerticalBar(
                value = point.plannedCount,
                maxValue = maxCount,
                color = plannedColor
            )

            AnalyticsVerticalBar(
                value = point.completedCount,
                maxValue = maxCount,
                color = completedColor
            )
        }

        Spacer(Modifier.height(7.dp))

        Text(
            text = point.label,
            maxLines = 1,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.labelSmall,
            color =
            MaterialTheme.colorScheme.onSurfaceVariant
        )

        Text(
            text =
            "${point.completedCount}/${point.plannedCount}",
            maxLines = 1,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun AnalyticsVerticalBar(
    value: Int,
    maxValue: Int,
    color: Color
) {
    val maxBarHeight = 138.dp

    val fraction =
        value
            .coerceAtLeast(0)
            .toFloat()
            .div(maxValue.coerceAtLeast(1).toFloat())
            .coerceIn(0f, 1f)

    val visibleFraction =
        if (value > 0) {
            fraction.coerceAtLeast(0.04f)
        } else {
            0f
        }

    val barHeight =
        if (value > 0) {
            maxBarHeight * visibleFraction
        } else {
            2.dp
        }

    Column(
        modifier = Modifier
            .width(20.dp)
            .height(176.dp),
        horizontalAlignment =
        Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Bottom
    ) {
        Text(
            text = value.toString(),
            maxLines = 1,
            style = MaterialTheme.typography.labelSmall,
            color =
            MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(Modifier.height(3.dp))

        Box(
            modifier = Modifier
                .width(14.dp)
                .height(barHeight)
                .clip(
                    RoundedCornerShape(
                        topStart = 6.dp,
                        topEnd = 6.dp,
                        bottomStart = 2.dp,
                        bottomEnd = 2.dp
                    )
                )
                .background(
                    if (value > 0) {
                        color
                    } else {
                        color.copy(alpha = 0.28f)
                    }
                )
        )
    }
}

@Composable
private fun AnalyticsChartLegendItem(
    color: Color,
    text: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement =
        Arrangement.spacedBy(7.dp)
    ) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .background(
                    color = color,
                    shape = CircleShape
                )
        )

        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium,
            color =
            MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

private fun analyticsChartTitle(
    period: AnalyticsPeriod
): String {
    return when (period) {
        AnalyticsPeriod.TODAY ->
            "تحقق برنامه امروز"

        AnalyticsPeriod.WEEK ->
            "عملکرد ۷ روز اخیر"

        AnalyticsPeriod.MONTH ->
            "عملکرد ۳۰ روز اخیر"
    }
}

private fun analyticsChartSubtitle(
    period: AnalyticsPeriod
): String {
    return when (period) {
        AnalyticsPeriod.TODAY ->
            "درصد پومودوروهای انجام‌شده نسبت به هدف امروز"

        AnalyticsPeriod.WEEK ->
            "مقایسه برنامه و عملکرد واقعی در هر روز"

        AnalyticsPeriod.MONTH ->
            "روند تعداد پومودوروهای برنامه‌ریزی‌شده و انجام‌شده"
    }
}