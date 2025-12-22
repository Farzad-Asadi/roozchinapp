package com.example.compoundeffectV1_01.utils

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ParentDataModifier
import androidx.compose.ui.unit.Density
import com.example.compoundeffectV1_01.data.dataBaseRoom.tables.event.Event
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Calendar
import kotlin.math.roundToInt

@Composable
fun LoadingScreen(modifier:Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(text = "LoadingScreen")
    }
}


// region کار با localDataTime
@SuppressLint("DefaultLocale")
fun timeInstanceToLocalDate(timeInstance: Calendar): LocalDateTime {
    val formattedDateTime = String.format(
        "%04d-%02d-%02dT%02d:%02d:%02d",
        timeInstance.get(Calendar.YEAR),
        timeInstance.get(Calendar.MONTH) + 1, // اضافه کردن 1 به ماه برای تصحیح مقدار
        timeInstance.get(Calendar.DAY_OF_MONTH),
        timeInstance.get(Calendar.HOUR_OF_DAY), // استفاده از HOUR_OF_DAY به‌جای HOUR
        timeInstance.get(Calendar.MINUTE),
        timeInstance.get(Calendar.SECOND)
    )

    return LocalDateTime.parse(formattedDateTime, DateTimeFormatter.ISO_LOCAL_DATE_TIME)
}

fun createTimeForSampleEvents(
    timeInstance: Calendar,
    hour: Int,
    minute: Int,
    second: Int
): LocalDateTime {
    timeInstance.set(Calendar.HOUR_OF_DAY, hour)
    timeInstance.set(Calendar.HOUR_OF_DAY, hour)
    return LocalDateTime.of(
        timeInstance.get(Calendar.YEAR),
        timeInstance.get(Calendar.MONTH) + 1, // چون ماه‌ها از 0 شروع می‌شوند
        timeInstance.get(Calendar.DAY_OF_MONTH),
        timeInstance.time.hours, // استفاده از HOUR_OF_DAY برای فرمت ۲۴ ساعته
        minute,
        second
    )


}

val EventTimeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("H:mm")
val DayFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("E MM dd")
val HourFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("H:00")

// endregion

// region تبدیل میلادی به شمسی

fun convertToPersianDate(localDate: LocalDate): String {

    val year = localDate.year
    val month = localDate.monthValue
    val day = localDate.dayOfMonth

    // آرایه تعداد روزهای هر ماه میلادی (سال کبیسه بررسی شده)
    val monthDays = arrayOf(31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31)
    if (isGregorianLeapYear(year)) monthDays[1] = 29

    // محاسبه تعداد روزهای سپری‌شده از ابتدای سال میلادی
    var dayOfYear = day
    for (i in 0 until (month - 1)) {
        dayOfYear += monthDays[i]
    }

    // تبدیل سال میلادی به شمسی
    val persianYear = if (dayOfYear <= 79) year - 622 else year - 621

    // محاسبه روزهای سپری‌شده از ابتدای سال شمسی
    val persianDayOfYear = if (dayOfYear > 79) {
        dayOfYear - 79
    } else {
        if (isGregorianLeapYear(year - 1)) dayOfYear + 287 else dayOfYear + 286
    }

    // محاسبه ماه و روز شمسی
    var persianMonth = 1
    var persianDay = persianDayOfYear
    while (persianDay > getPersianMonthDays(persianYear, persianMonth)) {
        persianDay -= getPersianMonthDays(persianYear, persianMonth)
        persianMonth++
    }

    return "$persianYear/$persianMonth/$persianDay"
}

// بررسی سال کبیسه میلادی
fun isGregorianLeapYear(year: Int): Boolean {
    return (year % 4 == 0 && year % 100 != 0) || (year % 400 == 0)
}

// تعداد روزهای هر ماه شمسی
fun getPersianMonthDays(year: Int, month: Int): Int {
    return when {
        month <= 6 -> 31
        month <= 11 -> 30
        else -> if (isPersianLeapYear(year)) 30 else 29
    }
}

// بررسی سال کبیسه شمسی
fun isPersianLeapYear(year: Int): Boolean {

    return (((year + 2346) * 683) % 2820) < 683
}
// endregion

// region extended functions
fun Color.colorToString(): String {
    return "${red},${green},${blue},${alpha}"
}
fun String.stringToColor(): Color {
    val components = this.split(",").map { it.toFloat() }
//    require(components.size == 4) { "String representation of color should have four components: red, green, blue, alpha" }
//    val (red, green, blue, alpha) = components
    return Color(components[0], components[1], components[2], components[3])
}







// endregion

// region اتصال رویدادها به صفحه schedul
class EventDataModifier(
    val event: Event,
) : ParentDataModifier {
    override fun Density.modifyParentData(parentData: Any?): Any {
        return event
    }
}

fun Modifier.eventData(event: Event) = this.then(EventDataModifier(event))
// endregion


fun currentTimeHeightPx(timeInstance: Calendar, hourHeight: Float): Int {
    val currentTimeToMinute =
        (timeInstance.get(Calendar.HOUR_OF_DAY) * 60) + (timeInstance.get(Calendar.MINUTE))
    val currentTimeHeight = ((hourHeight / 60) * (currentTimeToMinute.toFloat())).roundToInt()
    return currentTimeHeight
}


fun calculateHourHeight(baseHeight: Float ,zoom: Float): Float {
    // تابع محاسبه hourHeight بر اساس zoom
    val step = 20f // مقدار افزایش به ازای هر مرحله
    return (baseHeight + (zoom - 1f) * step).roundToInt().toFloat() // محاسبه hourHeight
}

fun calculateDayWidth(zoom: Float): Int {
    val baseDayWidth = 500f // کمترین مقدار dayWidth
    val step = 20f // مقدار افزایش به ازای هر مرحله
    return (baseDayWidth + (zoom - 1f) * step).roundToInt()
}

@Composable
fun eventHeightPx(event: Event, hourHeight: Float): Int {
    val eventDurationMinutes = ChronoUnit.MINUTES.between(event.start, event.end)
    return ((eventDurationMinutes / 60f) * hourHeight).roundToInt()
}


fun preparationEventListForSchedule(eventList: List<Event>): List<Event> {
    val preparedEventList: MutableList<Event> = eventList.filter { it.inSchedule }.toMutableList()
    preparedEventList.forEachIndexed { index, event ->
        val otherEvents = preparedEventList.filter { it.id != event.id }
        var otherEventDurationOverlap = 0

        val overlapCount: Int = otherEvents.count { otherEvent ->
            otherEvent.start.isBefore(event.start) &&
                    otherEvent.start.isBefore(event.end) &&
                    otherEvent.end.isAfter(event.start) &&
                    otherEvent.end.isBefore(event.end)
        }
        val overlapCount2: Int = otherEvents.count { otherEvent ->
            otherEvent.start.isBefore(event.start) &&
                    otherEvent.start.isBefore(event.end) &&
                    otherEvent.end.isAfter(event.start) &&
                    otherEvent.end.isAfter(event.end)
        }
        val overlapCount3: Int = otherEvents.count { otherEvent ->
        otherEvent.start.isEqual(event.start) &&
                otherEvent.start.isBefore(event.end) &&
                otherEvent.end.isAfter(event.start) &&
                otherEvent.end.isAfter(event.end)
    }
        val overlapCount4: Int = otherEvents.count { otherEvent ->
            otherEvent.start.isBefore(event.start) &&
                    otherEvent.start.isBefore(event.end) &&
                    otherEvent.end.isAfter(event.start) &&
                    otherEvent.end.isEqual(event.end)
        }
        if (
            !otherEvents.any { otherEvent ->
                otherEventDurationOverlap = otherEvent.durationOverlap


                otherEvent.start.isBefore(event.start) &&
                        otherEvent.start.isBefore(event.end) &&
                        otherEvent.end.isAfter(event.start) &&
                        otherEvent.end.isAfter(event.end)
            }||
            !otherEvents.any { otherEvent ->
                otherEventDurationOverlap = otherEvent.durationOverlap

                        otherEvent.start.isBefore(event.start) &&
                        otherEvent.start.isBefore(event.end) &&
                        otherEvent.end.isAfter(event.start) &&
                        otherEvent.end.isBefore(event.end)
            } ||
            !otherEvents.any { otherEvent ->
                otherEventDurationOverlap = otherEvent.durationOverlap

                otherEvent.start.isEqual(event.start) &&
                        otherEvent.start.isBefore(event.end) &&
                        otherEvent.end.isAfter(event.start) &&
                        otherEvent.end.isAfter(event.end)
            }||
            !otherEvents.any { otherEvent ->
                otherEventDurationOverlap = otherEvent.durationOverlap

                otherEvent.start.isBefore(event.start) &&
                        otherEvent.start.isBefore(event.end) &&
                        otherEvent.end.isAfter(event.start) &&
                        otherEvent.end.isEqual(event.end)
            }

        ) {

            preparedEventList[index] = event.copy(durationOverlap = 0)

        }
        if (otherEvents.any { otherEvent ->
                otherEventDurationOverlap = otherEvent.durationOverlap

                otherEvent.start.isBefore(event.start) &&
                        otherEvent.start.isBefore(event.end) &&
                        otherEvent.end.isAfter(event.start) &&
                        otherEvent.end.isAfter(event.end)
            }){
            preparedEventList[index] = event.copy(
                durationOverlap = otherEventDurationOverlap +
                    overlapCount+
                    overlapCount2+
                    overlapCount3+
                    overlapCount4
                )

        }
        if (otherEvents.any { otherEvent ->
                otherEventDurationOverlap = otherEvent.durationOverlap

                otherEvent.start.isBefore(event.start) &&
                        otherEvent.start.isBefore(event.end) &&
                        otherEvent.end.isAfter(event.start) &&
                        otherEvent.end.isBefore(event.end)
            } ){
            preparedEventList[index] = event.copy(
                durationOverlap = otherEventDurationOverlap +
                        overlapCount+
                        overlapCount2+
                        overlapCount3+
                        overlapCount4
            )
        }
        if (otherEvents.any { otherEvent ->
                otherEventDurationOverlap = otherEvent.durationOverlap

                otherEvent.start.isEqual(event.start) &&
                        otherEvent.start.isBefore(event.end) &&
                        otherEvent.end.isAfter(event.start) &&
                        otherEvent.end.isAfter(event.end)
            }){
            preparedEventList[index] = event.copy(
                durationOverlap = otherEventDurationOverlap +
                        overlapCount+
                        overlapCount2+
                        overlapCount3+
                        overlapCount4
            )
        }
        if (otherEvents.any { otherEvent ->
                otherEventDurationOverlap = otherEvent.durationOverlap

                otherEvent.start.isBefore(event.start) &&
                        otherEvent.start.isBefore(event.end) &&
                        otherEvent.end.isAfter(event.start) &&
                        otherEvent.end.isEqual(event.end)
            }){
            preparedEventList[index] = event.copy(
                durationOverlap = otherEventDurationOverlap +
                        overlapCount+
                        overlapCount2+
                        overlapCount3+
                        overlapCount4
            )
        }
    }

//    val sortedPreparedEventList: MutableList<Event> =
//        preparedEventList.sortedByDescending { Duration.between(it.start, it.end) }.toMutableList()
    return preparedEventList
}














