package com.example.compoundeffectV1_01.ui.scheduleScreen


import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.compoundeffectV1_01.data.dataBaseRoom.tables.task.Task
import com.example.compoundeffectV1_01.data.dataBaseRoom.tables.task.TaskRepository
import com.example.compoundeffectV1_01.data.dataBaseRoom.tables.task.TaskWithSchedule
import com.example.compoundeffectV1_01.data.dataBaseRoom.tables.taskSchedule.ScheduleMode
import com.example.compoundeffectV1_01.data.dataBaseRoom.tables.taskSchedule.TaskSchedule
import com.example.compoundeffectV1_01.data.dataBaseRoom.tables.taskSchedule.TaskScheduleRepository
import com.example.compoundeffectV1_01.utils.createTimeForSampleEvents
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Duration
import java.time.LocalDateTime
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class ScheduleScreenViewModel @Inject constructor(
    private val taskRepo: TaskRepository,
    private val taskScheduleRepo: TaskScheduleRepository,
) : ViewModel() {

    val timelineItems: StateFlow<List<TaskTimelineItem>> =
        taskRepo.observeAllScheduledTasksWithSchedule()
            .map { list -> list.mapNotNull(::toTimelineItemOrNull).sortedBy { it.start } }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val palletTasks: StateFlow<List<Task>> =
        taskRepo.observePalletTasks()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())








    fun dropTaskToSchedule(
        taskId: Int,
        date: java.time.LocalDate,
        startMinute: Int,
        endMinute: Int
    ) {
        viewModelScope.launch(kotlinx.coroutines.Dispatchers.IO) {

            // 1) schedule upsert
            val schedule = TaskSchedule(
                id = null,
                taskId = taskId,
                title = null,
                mode = ScheduleMode.TIME_RANGE,
                dateEpochDay = date.toEpochDay(),
                startMinuteOfDay = startMinute,
                endMinuteOfDay = endMinute,
                durationMinutes = null,
                repeating = false
            )
            taskScheduleRepo.upsert(schedule)

            // 2) task flags update
            val t = taskRepo.getTaskById(taskId) ?: return@launch
            taskRepo.updateTask(
                t.copy(
                    inSchedule = true,
                    inPallet = false
                )
            )
        }
    }



    private fun toTimelineItemOrNull(
        tws: TaskWithSchedule
    ): TaskTimelineItem? {
        val s = tws.schedule ?: return null
        if (s.mode != ScheduleMode.TIME_RANGE) return null

        val date = s.dateEpochDay?.let(java.time.LocalDate::ofEpochDay) ?: return null
        val startMin = s.startMinuteOfDay ?: return null
        val endMin = s.endMinuteOfDay ?: return null

        val startTime = java.time.LocalTime.of(startMin / 60, startMin % 60)
        val endTime = java.time.LocalTime.of(endMin / 60, endMin % 60)

        val start = LocalDateTime.of(date, startTime)
        val end = LocalDateTime.of(date, endTime)

        // اگر end <= start بود، ignore
        if (!end.isAfter(start)) return null

        val t = tws.task
        val id = t.id ?: return null

        return TaskTimelineItem(
            taskId = id,
            name = t.name,
            colorHex = t.color,
            description = t.description,
            categoryId = t.categoryId,
            isCompleted = t.isCompleted,
            priority = t.priority,
            start = start,
            end = end
        )
    }


}


data class TaskTimelineItem(
    val taskId: Int,
    val name: String,
    val colorHex: String,
    val description: String,
    val categoryId: Int?,
    val isCompleted: Boolean,
    val priority: Int,

    // ✅ فقط برای تایم‌لاین: از schedule استخراج میشه
    val start: LocalDateTime,
    val end: LocalDateTime,

    // برای overlap مثل قبل (بعداً پر می‌کنیم)
    val overlapIndex: Int = 0
)
