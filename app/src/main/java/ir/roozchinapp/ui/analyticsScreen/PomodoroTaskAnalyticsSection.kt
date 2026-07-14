package ir.roozchinapp.ui.analyticsScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import ir.roozchinapp.utils.colorFromHex
import kotlin.math.roundToInt

@Composable
internal fun PomodoroTaskAnalyticsSection(
    selectedPeriod: AnalyticsPeriod,
    items: List<PomodoroTaskAnalyticsItem>,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            Text(
                text = "عملکرد بر اساس تسک",
                style = MaterialTheme.typography.titleMedium
            )

            Text(
                text = taskAnalyticsPeriodSubtitle(
                    selectedPeriod
                ),
                style = MaterialTheme.typography.bodySmall,
                color =
                MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        if (items.isEmpty()) {
            EmptyTaskAnalyticsCard()
        } else {
            items.forEach { item ->
                PomodoroTaskAnalyticsCard(
                    item = item
                )
            }
        }
    }
}

@Composable
private fun PomodoroTaskAnalyticsCard(
    item: PomodoroTaskAnalyticsItem
) {
    val taskColor =
        colorFromHex(item.taskColorHex)

    val progress =
        item.completionPercent
            .div(100f)
            .coerceIn(0f, 1f)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor =
            MaterialTheme.colorScheme.surfaceContainerHigh
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .background(
                            color = taskColor,
                            shape = CircleShape
                        )
                )

                Spacer(Modifier.size(10.dp))

                Text(
                    text = item.taskName,
                    modifier = Modifier.weight(1f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.titleMedium
                )

                Text(
                    text =
                    if (item.plannedCount > 0) {
                        "${item.completionPercent.roundToInt()}٪"
                    } else {
                        "بدون هدف"
                    },
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color =
                    if (item.plannedCount > 0) {
                        taskColor
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    }
                )
            }

            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(999.dp)),
                color = taskColor,
                trackColor =
                MaterialTheme.colorScheme.outlineVariant
                    .copy(alpha = 0.45f)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement =
                Arrangement.SpaceBetween
            ) {
                Text(
                    text =
                    "انجام ${item.completedCount} از ${item.plannedCount}",
                    style = MaterialTheme.typography.bodySmall,
                    color =
                    MaterialTheme.colorScheme.onSurfaceVariant
                )

                Text(
                    text =
                    "زمان‌بندی ${item.scheduledCount}",
                    style = MaterialTheme.typography.bodySmall,
                    color =
                    MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Text(
                text =
                "تمرکز انجام‌شده: ${
                    formatTaskFocusMinutes(
                        item.completedFocusMinutes
                    )
                }",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )

            if (item.extraCompletedCount > 0) {
                Text(
                    text =
                    "${item.extraCompletedCount} پومودورو بیشتر از برنامه",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
private fun EmptyTaskAnalyticsCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor =
            MaterialTheme.colorScheme.surfaceContainerHigh
        )
    ) {
        Text(
            text = "در این بازه فعالیت پومودورو ثبت نشده است.",
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 28.dp),
            style = MaterialTheme.typography.bodyMedium,
            color =
            MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

private fun taskAnalyticsPeriodSubtitle(
    period: AnalyticsPeriod
): String {
    return when (period) {
        AnalyticsPeriod.TODAY ->
            "مقایسه هدف و عملکرد امروز هر تسک"

        AnalyticsPeriod.WEEK ->
            "مجموع عملکرد هر تسک در ۷ روز اخیر"

        AnalyticsPeriod.MONTH ->
            "مجموع عملکرد هر تسک در ۳۰ روز اخیر"
    }
}

private fun formatTaskFocusMinutes(
    totalMinutes: Int
): String {
    val safeMinutes =
        totalMinutes.coerceAtLeast(0)

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