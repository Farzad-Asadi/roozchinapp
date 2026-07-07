package ir.roozchinapp.data.dataClasses


import ir.roozchinapp.data.dataBaseRoom.tables.category.CategoryEntity
import ir.roozchinapp.data.dataBaseRoom.tables.reminder.BeforeAfter
import ir.roozchinapp.data.dataBaseRoom.tables.reminder.ReminderMode
import ir.roozchinapp.data.dataBaseRoom.tables.reminder.ReminderStrengthMode
import ir.roozchinapp.data.dataBaseRoom.tables.reminder.StartEnd
import ir.roozchinapp.data.dataBaseRoom.tables.reminder.TaskReminderEntity
import ir.roozchinapp.data.dataBaseRoom.tables.task.TaskChildStructure
import ir.roozchinapp.data.dataBaseRoom.tables.task.TaskEntity
import ir.roozchinapp.data.dataBaseRoom.tables.task.TaskMode
import ir.roozchinapp.data.dataBaseRoom.tables.taskSchedule.RepeatUnit
import ir.roozchinapp.data.dataBaseRoom.tables.taskSchedule.ScheduleMode
import ir.roozchinapp.data.dataBaseRoom.tables.taskSchedule.TaskSchedule
import java.time.LocalDate
import java.time.LocalTime


data class CategoryUiState2(
    val isLoading: Boolean = true,
    val categories: List<CategoryEntity> = emptyList(),
    val renderItems: List<CategoryRenderItem> = emptyList(),

    val taskEntities: List<TaskEntity> = emptyList(),
    val taskRenderItems: List<TaskRenderItem> = emptyList(),
    val levelById: Map<Int, Int> = emptyMap(),

    )

data class CategoryRenderItem(
    val category: CategoryEntity,
    val level: Int,
    val hasChildren: Boolean,
    val isExpanded: Boolean,
    val isVisible: Boolean
)

data class FlattenResult(
    val items: List<CategoryRenderItem>,
    val levelById: Map<Int, Int>
)

data class TaskMiniUi(
    val id: Int,
    val title: String,
    val isDone: Boolean = false,
    val hasSchedule: Boolean = false,

    val parentTaskId: Int? = null,
    val siblingIndex: Int = 0,
    val priority: Int = 0
)

data class TaskRenderItem(
    val task: TaskMiniUi,
    val level: Int,        // 1..4 (نمایشی)
    val hasChildren: Boolean,
    val isExpanded: Boolean,
    val isVisible: Boolean
)

data class TaskScheduleUi(
    val key: Int,              // برای pending منفی، برای DB همون id
    val schedule: TaskSchedule,
    val isPending: Boolean
)

data class CategoryDraft(
    val name: String = "",
    val parentId: Int = -1,
    val iconName: String = "QuestionMark",
    val color: String =  "#2196F3",  // آبی متریال
    val description: String = ""
)

data class TaskDraft(
    val name: String = "",
    val categoryId: Int? = null,
    val priority: Int = 0,
    val isCompleted: Boolean = false,
    val note: String = "",

    // ✅ جدیدها برای دیالوگ
    val insertAtTop: Boolean = false, // false=آخر لیست، true=اول لیست
    val childLevel: Int = 0,           // 0..3 (0 یعنی هیچ)

    val childStructure: String = TaskChildStructure.SUBTASKS,
    val showInAnytimePallet: Boolean = false,

    // ✅ Pomodoro
    val taskMode: TaskMode = TaskMode.NORMAL,
    val pomodoroTargetUnits: Int = 50,
    val pomodoroDoneUnits: Int = 0,
)

data class ScheduleDraft(
    val title: String = "",
    val mode: ScheduleMode = ScheduleMode.TIME_RANGE,

    val date: LocalDate = LocalDate.now(),
    val start: LocalTime = LocalTime.of(20, 0),
    val end: LocalTime = LocalTime.of(21, 0),
    val durationMinutes: Int = 0,

    // ✅ Pomodoro
    val focusMinutes: Int = 25,
    val shortBreakMinutes: Int = 5,
    val longBreakMinutes: Int = 15,
    val longBreakEvery: Int = 4,
    val pomodoroUnitsPerDay: Int = 5,

    val note: String = "",

    val repeat: RepeatDraft = RepeatDraft(),
    val reminder: ReminderDraft? = null
)

data class RepeatDraft(
    val enabled: Boolean = false,
    val interval: Int = 1,
    val unit: RepeatUnit = RepeatUnit.DAY,
    val weekdaysMask: Int = 127
)

data class ReminderDraft(
    val mode: ReminderMode = ReminderMode.ALLOCATED,
    val title: String = "",

    // Allocated
    val offsetDays: Int = 0,
    val offsetHours: Int = 0,
    val offsetMinutes: Int = 0,
    val beforeAfter: BeforeAfter = BeforeAfter.BEFORE,
    val anchor: StartEnd = StartEnd.START,

    // Fixed time
    val fixedTime: LocalTime = LocalTime.of(11, 0),

    //Pomodoro Reminder
    val onStartFocus: Boolean = false,
    val onStartBreak: Boolean = false,
    val onEndBreak: Boolean = false,

    // Strength
    val strength: ReminderStrengthMode = ReminderStrengthMode.NOTIFICATION,
    val vibrate: Boolean = true,

    // Sound (برای Alarm ها)
    val alarmSoundUri: String? = null, // Uri.toString()

    // Captcha
    val captchaEnabled: Boolean = false, // فقط وقتی strength = ALARM_AND_CAPTCHA
)

data class TaskUiState(
    val isLoading: Boolean = true,
    val categories: List<CategoryEntity> = emptyList(),
    val renderItems: List<CategoryRenderItem> = emptyList(),

    val taskEntities: List<TaskEntity> = emptyList(),
    val taskRenderItems: List<TaskRenderItem> = emptyList(),
    val levelById: Map<Int, Int> = emptyMap(),

    )

data class RemindersInputs(
    val tid: Int?,
    val schKey: Int?,
    val draftList: List<TaskReminderUi>,
    val pendingMap: Map<Int, List<TaskReminderUi>>,
)

data class ChildLevelUi(
    val allowed: Set<Int> = setOf(0),
    val maxAllowed: Int = 0
)

data class TaskReminderUi(
    val key: Int,              // برای pending منفی، برای DB همون id
    val entity: TaskReminderEntity,
    val isPending: Boolean
)

