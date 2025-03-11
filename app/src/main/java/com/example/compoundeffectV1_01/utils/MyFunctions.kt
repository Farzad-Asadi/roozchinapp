package com.example.compoundeffectV1_01.utils

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.layout.ParentDataModifier
import androidx.compose.ui.unit.Density
import com.example.compoundeffectV1_01.data.room.event.Event
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Calendar
import kotlin.math.roundToInt

@Composable
fun LoadingScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(text = "LoadingScreen")
    }
}



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

fun createTimeForSampleEvents(timeInstance: Calendar, hour: Int, minute: Int, second: Int): LocalDateTime {
    return LocalDateTime.of(
        timeInstance.get(Calendar.YEAR),
        timeInstance.get(Calendar.MONTH) + 1, // چون ماه‌ها از 0 شروع می‌شوند
        timeInstance.get(Calendar.DAY_OF_MONTH),
        hour,
        minute,
        second
    )
}



fun convertToPersianDate(localDate: LocalDate): String {
    val year = localDate.year
    val month = localDate.monthValue
    val day = localDate.dayOfMonth

    // آرایه‌ای برای تعداد روزهای هر ماه در سال‌های غیرکبیسه و کبیسه
    val monthDays = arrayOf(31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31)

    // محاسبه تعداد روزهای گذشته از ابتدای سال میلادی
    var dayOfYear = day
    for (i in 0 until month - 1) {
        dayOfYear += monthDays[i]
    }

    // بررسی سال کبیسه میلادی
    if (isGregorianLeapYear(year) && month > 2) {
        dayOfYear += 1
    }

    // محاسبه سال شمسی
    val persianYear = if (dayOfYear <= 79) {
        year - 622
    } else {
        year - 621
    }

    // محاسبه روزهای گذشته از ابتدای سال شمسی
    val persianDayOfYear = if (dayOfYear > 79) {
        dayOfYear - 79
    } else {
        if (isGregorianLeapYear(year - 1)) {
            dayOfYear + 287
        } else {
            dayOfYear + 286
        }
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

// تابع برای بررسی سال کبیسه میلادی
fun isGregorianLeapYear(year: Int): Boolean {
    return (year % 4 == 0 && year % 100 != 0) || (year % 400 == 0)
}

// تابع برای تعداد روزهای هر ماه در سال شمسی
fun getPersianMonthDays(year: Int, month: Int): Int {
    return if (month <= 6) {
        31
    } else if (month <= 11) {
        30
    } else {
        if (isPersianLeapYear(year)) 30 else 29
    }
}

// تابع برای بررسی سال کبیسه شمسی
fun isPersianLeapYear(year: Int): Boolean {
    return (year % 33 % 4 == 1)
}



val EventTimeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("h:mm")
val DayFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("E MM dd")
val HourFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("H:00")


//extended functions
fun Color.colorToString():String{
    return  "${red},${green},${blue},${alpha}"
}

fun String.stringToColor(): Color {
    val components = this.split(",").map { it.toFloat() }
//    require(components.size == 4) { "String representation of color should have four components: red, green, blue, alpha" }
//    val (red, green, blue, alpha) = components
    return Color(components[0], components[1], components[2], components[3])
}




// for Attaching Data to Composables
class EventDataModifier(
    val event: Event,
) : ParentDataModifier {
    override fun Density.modifyParentData(parentData: Any?): Any {
        return event
    }
}

fun Modifier.eventData(event: Event) = this.then(EventDataModifier(event))



fun currentTimeHeightPx(timeInstance: Calendar, hourHeight: Float): Int {
    val currentTimeToMinute =
        (timeInstance.get(Calendar.HOUR_OF_DAY) * 60) + (timeInstance.get(Calendar.MINUTE))
    val currentTimeHeight = ((hourHeight / 60) * (currentTimeToMinute.toFloat())).roundToInt()
    return currentTimeHeight
}




