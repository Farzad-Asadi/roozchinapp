package com.example.compoundeffectV1_01.data.workManager

import android.content.Context
import android.util.Log
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.example.compoundeffectV1_01.data.dataBaseRoom.tables.reminder.BeforeAfter
import com.example.compoundeffectV1_01.data.dataBaseRoom.tables.reminder.ReminderMode
import com.example.compoundeffectV1_01.data.dataBaseRoom.tables.reminder.StartEnd
import com.example.compoundeffectV1_01.data.dataBaseRoom.tables.reminder.TaskReminderEntity
import com.example.compoundeffectV1_01.data.dataBaseRoom.tables.reminder.TaskReminderRepository
import com.example.compoundeffectV1_01.data.dataBaseRoom.tables.taskSchedule.RepeatUnit
import com.example.compoundeffectV1_01.data.dataBaseRoom.tables.taskSchedule.ScheduleMode
import com.example.compoundeffectV1_01.data.dataBaseRoom.tables.taskSchedule.TaskSchedule
import com.example.compoundeffectV1_01.data.dataBaseRoom.tables.taskSchedule.TaskScheduleRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.concurrent.TimeUnit
import javax.inject.Inject


//سازنده و گرفتن WorkManager
class ReminderScheduler @Inject constructor(
    @ApplicationContext private val appContext: Context,
    private val reminderRepo: TaskReminderRepository,
    private val scheduleRepo: TaskScheduleRepository,
) {


    //ساخت ورک منیجر
    private val wm by lazy { WorkManager.getInstance(appContext) }



    //ایجاد زمانبندی
    suspend fun reschedule(reminderId: Int) {

        //خواندن از دیتابیس
        val reminder = reminderRepo.getById(reminderId) ?: return
        val schedule = scheduleRepo.getById(reminder.scheduleId) ?: return



        // زمان اجرای نهایی را حساب کن
        // زمان الان
        val after = System.currentTimeMillis() + 1000L // ✅ 1 ثانیه جلوتر تا تکرار همون لحظه نشه

        //تریگر به میلی ثانیه
        val triggerAtMillis = computeNextTriggerAtMillis(reminder, schedule, after) ?: run {
            cancel(reminderId)
            return
        }

        //اختلاف زمان اجرا با «الان»
        val delay = (triggerAtMillis - System.currentTimeMillis()).coerceAtLeast(0L) // نمیگذارد منفی باشد



        //درخواست Work را می‌سازد
        val req = OneTimeWorkRequestBuilder<ReminderNotificationWorker>()
            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
            .setInputData(workDataOf("reminderId" to reminderId))   //ارسال reminderId به doWork
            .addTag("reminders")
            .addTag(workName(reminderId))
            .build()



        //در صف قرار دادن درخواست کار
        wm.enqueueUniqueWork(
            workName(reminderId),
            ExistingWorkPolicy.REPLACE,  // Replace: اگر قبلاً work داشته، جایگزین کن
            req
        )



        //Log
        val name = workName(reminderId) // همون "reminder_$reminderId"
        withContext(Dispatchers.IO) {
            val infos = wm.getWorkInfosForUniqueWork(name).get()
            Log.d("WM", "after enqueue name=$name infos=${infos.map { it.state to it.id }} delayMs=$delay")
        }

    }



    //کنسل کردن کار در صف کار
    fun cancel(reminderId: Int) {
        wm.cancelUniqueWork(workName(reminderId))
    }

}


//نام گذاری
private fun workName(reminderId: Int) = "reminder_$reminderId"



//محاسبه زمان تریگر
private fun computeNextTriggerAtMillis(
    reminder: TaskReminderEntity,
    schedule: TaskSchedule,
    afterMillis: Long
): Long? {

    val baseDate = schedule.dateEpochDay?.let(LocalDate::ofEpochDay) ?: return null

    fun minuteToTime(m: Int) = LocalTime.of(m / 60, m % 60)

    fun toMillis(dt: LocalDateTime): Long =
        dt.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()

    val afterLocal = LocalDateTime.ofInstant(
        java.time.Instant.ofEpochMilli(afterMillis),
        ZoneId.systemDefault()
    )

    // --- پیدا کردن تاریخ occurrence بعدی مطابق repeat schedule ---
    fun nextScheduleDateOnOrAfter(from: LocalDate): LocalDate? {
        if (!schedule.repeating) {
            return if (!baseDate.isBefore(from)) baseDate else null
        }

        val interval = (schedule.repeatInterval ?: 1).coerceIn(1, 99)
        val unit = schedule.repeatUnit ?: RepeatUnit.DAY

        return when (unit) {
            RepeatUnit.DAY -> {
                var d = baseDate
                if (d.isBefore(from)) {
                    val daysBetween = java.time.temporal.ChronoUnit.DAYS.between(d, from)
                    val steps = ((daysBetween + interval - 1) / interval) // ceil
                    d = d.plusDays(steps * interval.toLong())
                }
                d
            }

            RepeatUnit.WEEK -> {
                val mask = (schedule.weekdaysMask ?: 0).coerceIn(0, 127)
                if (mask == 0) return null

                fun bitIndex(dow: DayOfWeek): Int = when (dow) {
                    DayOfWeek.SATURDAY -> 0
                    DayOfWeek.SUNDAY -> 1
                    DayOfWeek.MONDAY -> 2
                    DayOfWeek.TUESDAY -> 3
                    DayOfWeek.WEDNESDAY -> 4
                    DayOfWeek.THURSDAY -> 5
                    DayOfWeek.FRIDAY -> 6
                }

                fun isAllowed(date: LocalDate): Boolean {
                    val bit = 1 shl bitIndex(date.dayOfWeek)
                    return (mask and bit) != 0
                }

                fun isInAllowedWeek(date: LocalDate): Boolean {
                    val weeks = java.time.temporal.ChronoUnit.WEEKS.between(baseDate, date)
                    return weeks >= 0 && (weeks % interval == 0L)
                }

                var d = if (baseDate.isAfter(from)) baseDate else from
                repeat(366) {
                    if (!d.isBefore(baseDate) && isAllowed(d) && isInAllowedWeek(d)) return d
                    d = d.plusDays(1)
                }
                null
            }

            else -> {
                // فعلاً سایر واحدها را مثل DAY در نظر می‌گیریم
                var d = baseDate
                if (d.isBefore(from)) d = from
                d
            }
        }
    }

    data class Occurrence(val start: LocalDateTime, val end: LocalDateTime)

    // --- ساختن start/end occurrence برای یک تاریخ مشخص (برای ALLOCATED لازم است) ---
    fun occurrenceForDate(date: LocalDate): Occurrence? {
        val startMin = schedule.startMinuteOfDay ?: return null
        val start = date.atTime(minuteToTime(startMin))

        return when (schedule.mode) {
            ScheduleMode.TIME_RANGE -> {
                val endMin = schedule.endMinuteOfDay ?: return null
                val endSameDay = date.atTime(minuteToTime(endMin))

                // اگر end قبل از start باشد (time-range شبانه)، فعلاً پشتیبانی نمی‌کنیم:
                // اگر خواستی پشتیبانی کنی: end = endSameDay.plusDays(1)
                if (endSameDay.isBefore(start)) return null

                Occurrence(start, endSameDay)
            }

            ScheduleMode.AMOUNT_OF_TIME -> {
                val dur = schedule.durationMinutes ?: return null
                val end = start.plusMinutes(dur.toLong()) // ممکن است وارد فردا شود
                Occurrence(start, end)
            }

            ScheduleMode.POMODORO -> { //فعلاً مثل AmountOfTime محاسبه کن
                val dur = schedule.durationMinutes ?: return null
                val end = start.plusMinutes(dur.toLong()) // ممکن است وارد فردا شود
                Occurrence(start, end)
            }
        }
    }

    // --- محاسبه fire برای یک تاریخ occurrence ---
    fun fireDateTimeForDate(date: LocalDate, occ: Occurrence?): LocalDateTime? {
        return when (reminder.mode) {

            ReminderMode.FIXED_TIME -> {
                // ✅ قانون تو: داخل بازه بودن مهم نیست
                val fixedMin = reminder.fixedMinuteOfDay ?: return null
                date.atTime(minuteToTime(fixedMin))
            }

            ReminderMode.ALLOCATED -> {
                // allocated به anchor نیاز دارد => occurrence باید موجود باشد
                val o = occ ?: return null

                val anchor = when (reminder.anchor) {
                    StartEnd.START -> o.start
                    StartEnd.END -> o.end
                }

                val offsetTotalMinutes =
                    reminder.offsetDays * 24 * 60 +
                            reminder.offsetHours * 60 +
                            reminder.offsetMinutes

                if (reminder.beforeAfter == BeforeAfter.BEFORE)
                    anchor.minusMinutes(offsetTotalMinutes.toLong())
                else
                    anchor.plusMinutes(offsetTotalMinutes.toLong())
            }
        }
    }

    // --- جستجوی اولین fire بعد از afterMillis ---
    var date = nextScheduleDateOnOrAfter(afterLocal.toLocalDate()) ?: return null

    repeat(366) {
        val occ = if (reminder.mode == ReminderMode.ALLOCATED) occurrenceForDate(date) else null
        val dt = fireDateTimeForDate(date, occ)

        if (dt != null && toMillis(dt) > afterMillis) return toMillis(dt)

        date = nextScheduleDateOnOrAfter(date.plusDays(1)) ?: return null
    }

    return null
}


