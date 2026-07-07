package ir.roozchinapp.ui.taskScreen

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.Interaction
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.automirrored.filled.Note
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.EventRepeat
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.GolfCourse
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Pattern
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.SubdirectoryArrowRight
import androidx.compose.material.icons.filled.Today
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TimePicker
import androidx.compose.material3.VerticalDivider
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.gmail.hamedvakhide.compose_jalali_datepicker.JalaliDatePickerDialog
import ir.huri.jcal.JalaliCalendar
import ir.roozchinapp.data.dataBaseRoom.tables.reminder.BeforeAfter
import ir.roozchinapp.data.dataBaseRoom.tables.reminder.ReminderMode
import ir.roozchinapp.data.dataBaseRoom.tables.reminder.ReminderStrengthMode
import ir.roozchinapp.data.dataBaseRoom.tables.reminder.StartEnd
import ir.roozchinapp.data.dataBaseRoom.tables.reminder.TaskReminderEntity
import ir.roozchinapp.data.dataBaseRoom.tables.task.TaskChildResetScope
import ir.roozchinapp.data.dataBaseRoom.tables.task.TaskChildRuleEntity
import ir.roozchinapp.data.dataBaseRoom.tables.task.TaskChildRuleType
import ir.roozchinapp.data.dataBaseRoom.tables.task.TaskEntity
import ir.roozchinapp.data.dataBaseRoom.tables.task.TaskMode
import ir.roozchinapp.data.dataBaseRoom.tables.taskSchedule.RepeatUnit
import ir.roozchinapp.data.dataBaseRoom.tables.taskSchedule.ScheduleMode
import ir.roozchinapp.data.dataBaseRoom.tables.taskSchedule.TaskSchedule
import ir.roozchinapp.data.dataClasses.CategoryRenderItem
import ir.roozchinapp.data.dataClasses.ReminderDraft
import ir.roozchinapp.data.dataClasses.ScheduleDraft
import ir.roozchinapp.data.dataClasses.TaskDraft
import ir.roozchinapp.data.dataClasses.TaskReminderUi
import ir.roozchinapp.data.dataClasses.TaskScheduleUi
import ir.roozchinapp.data.notification.rememberPostNotificationsPermissionRequester
import ir.roozchinapp.ui.categoryScreen.ConfirmAction
import ir.roozchinapp.ui.categoryScreen.ROOT
import ir.roozchinapp.utils.DimmedDialog
import ir.roozchinapp.utils.colorFromHex
import ir.roozchinapp.utils.durationMinutesSameDay
import ir.roozchinapp.utils.ensureAfter
import ir.roozchinapp.utils.iconFromKey
import ir.roozchinapp.utils.plusMinutesClamped
import ir.roozchinapp.utils.reminderModeIcon
import ir.roozchinapp.utils.scheduleModeIcon
import ir.roozchinapp.utils.toFaText
import ir.roozchinapp.utils.toJalali
import ir.roozchinapp.utils.toLocalDate
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.yield
import java.time.LocalDate
import java.time.LocalTime
import kotlin.math.roundToInt

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun TaskScreen(
    onClickBack: () -> Unit,
    onOpenTask: (Int) -> Unit,
    viewModel: TaskScreenViewModel = hiltViewModel()
) {

    val state by viewModel.uiState.collectAsState()
    val pickerFlatten by viewModel.parentPickerItems.collectAsState()
    val editingTaskId by viewModel.editingTaskId.collectAsState()
    val taskDraft by viewModel.taskDraft.collectAsState()
    val isEditingDraftTask by viewModel.isEditingDraftTask.collectAsState()
    val scheduleDraft by viewModel.scheduleDraft.collectAsState()
    val menuCategoryId by viewModel.menuCategoryId.collectAsState()
    val childLevelUi by viewModel.childLevelUi.collectAsState()
    val schedules by viewModel.schedulesUiForTaskDialog.collectAsState()
    val editingScheduleKey by viewModel.editingScheduleKey.collectAsState()
    val reminders by viewModel.remindersUiForScheduleDialog.collectAsState()
    val reminderDraft by viewModel.reminderDraft.collectAsState()
    val editingReminderKey by viewModel.editingReminderKey.collectAsState()
    val childRulesForEditingTask by viewModel.childRulesForEditingTask.collectAsState()
    val editingChildRule by viewModel.editingChildRule.collectAsState()
    val childRulesForEditingParentTask by viewModel.childRulesForEditingParentTask.collectAsState()

    val allTaskEntities by viewModel.allTaskEntities.collectAsState()
    val allEnabledChildRules by viewModel.allEnabledChildRules.collectAsState()

    val requestPostNotifPermission = rememberPostNotificationsPermissionRequester()

    val editingTaskEntity = remember(
        editingTaskId,
        state.taskEntities
    ) {
        val id = editingTaskId ?: return@remember null

        state.taskEntities.firstOrNull { it.id == id }
    }

    val isEditingChildTask = remember(editingTaskEntity) {
        val parentId = editingTaskEntity?.parentTaskId
        parentId != null && parentId != ROOT
    }

    LaunchedEffect(
        editingTaskId,
        isEditingChildTask
    ) {
        if (isEditingChildTask && editingTaskId != null) {
            viewModel.ensureDefaultRuleForEditingChildTask()
        }
    }


    val blockedGrandChildTasksForEditingTask = remember(
        editingTaskId,
        state.taskEntities,
        isEditingChildTask
    ) {
        val parentId = editingTaskId ?: return@remember emptyList<TaskEntity>()

        if (!isEditingChildTask) {
            emptyList()
        } else {
            state.taskEntities
                .filter { it.parentTaskId == parentId }
                .sortedWith(
                    compareBy<TaskEntity> { it.siblingIndex }
                        .thenBy { it.id ?: 0 }
                )
        }
    }

    val directChildTasksForEditingTask = remember(
        editingTaskId,
        state.taskEntities,
        isEditingChildTask
    ) {
        val parentId = editingTaskId ?: return@remember emptyList<TaskEntity>()

        if (isEditingChildTask) {
            emptyList()
        } else {
            state.taskEntities
                .filter { it.parentTaskId == parentId }
                .sortedWith(
                    compareBy<TaskEntity> { it.isCompleted }
                        .thenBy { it.siblingIndex }
                        .thenBy { it.id ?: 0 }
                )
        }
    }

    val childRuleByChildTaskId = remember(
        childRulesForEditingParentTask
    ) {
        childRulesForEditingParentTask.associateBy { it.childTaskId }
    }


    LaunchedEffect(
        editingTaskId,
        directChildTasksForEditingTask.size,
        isEditingChildTask
    ) {
        if (!isEditingChildTask && editingTaskId != null && directChildTasksForEditingTask.isNotEmpty()) {
            viewModel.ensureDefaultChildRulesForEditingParentTask()
        }
    }





    val menuCategory = state.categories.firstOrNull { it.categoryId == menuCategoryId }

    var showPickTaskCategory by rememberSaveable { mutableStateOf(false) }
    var showScheduleDialog by rememberSaveable { mutableStateOf(false) }
    var showReminderDialog by rememberSaveable { mutableStateOf(false) }

    // 1) Back سیستم (gesture/btn) هم مثل دکمه Back خودت رفتار کنه
    BackHandler {
        val handledInsideEditor = viewModel.navigateBackInsideTaskEditor()

        if (!handledInsideEditor) {
            viewModel.finishOrDeleteEmptyDraftTask()
            viewModel.clearTaskEditBackStack()
            onClickBack()
        }
    }



    Scaffold(
        modifier = Modifier
    ) { padding ->

        if (menuCategory != null) {

            val selectedCategoryId = taskDraft.categoryId ?: menuCategory.categoryId
            val selectedCategory =
                state.categories.firstOrNull { it.categoryId == selectedCategoryId }
                    ?: menuCategory

            val isEdit =
                editingTaskId != null && !isEditingDraftTask

            AddEditeTaskScreen(
                addTaskMod = !isEdit,
                categoryName = selectedCategory.name,
                categoryIconName = selectedCategory.iconName,
                categoryColorHex = selectedCategory.color,
                draft = taskDraft,
                onClickBack = {
                    val handledInsideEditor = viewModel.navigateBackInsideTaskEditor()

                    if (!handledInsideEditor) {
                        viewModel.finishOrDeleteEmptyDraftTask()
                        viewModel.clearTaskEditBackStack()
                        onClickBack()
                    }
                },
                onNameChange = viewModel::setTaskName,
                onPriorityChange = viewModel::setTaskPriority,
                onCompletedToggle = viewModel::setTaskCompleted,
                onNoteChange = viewModel::setTaskNote,
                onToggleChildTaskCompleted = viewModel::setDirectChildTaskCompleted,
                onDeleteChildTask = viewModel::deleteDirectChildTask,
                onInsertAtTopChange = viewModel::setTaskInsertAtTop,
                onChildLevelChange = viewModel::setTaskChildLevel,
                onPickCategory = { showPickTaskCategory = true },
                onConfirm = { action ->
                    val hasDbTask = editingTaskId != null
                    val color = selectedCategory.color

                    if (hasDbTask) {
                        viewModel.saveEditedTask(color)

                        when (action) {
                            ConfirmAction.SAVE_AND_CLOSE -> {
                                viewModel.finishEditTask()
                                onClickBack()
                            }

                            ConfirmAction.SAVE_AND_CONTINUE -> {
                                // فعلاً بعد از DB-backed draft، Save & Continue را در فاز بعدی تمیز می‌کنیم.
                                // الان task ذخیره و ACTIVE می‌شود، ولی صفحه باز می‌ماند.
                            }
                        }
                    } else {
                        // fallback قدیمی؛ در حالت عادی دیگر نباید وارد این شاخه شویم.
                        viewModel.createTaskForCategory(color)

                        when (action) {
                            ConfirmAction.SAVE_AND_CLOSE -> {
                                viewModel.finishEditTask()
                                onClickBack()
                            }

                            ConfirmAction.SAVE_AND_CONTINUE -> {
                                viewModel.resetTaskDraftKeepSomeDefaults()
                            }
                        }
                    }
                },
                onOpenSchedule = {
                    viewModel.startAddSchedule()
                    showScheduleDialog = true
                },
                allowedChildLevels = childLevelUi.allowed,
                schedules = schedules,
                onClickSchedule = { key ->
                    viewModel.startEditScheduleByKey(key)
                    showScheduleDialog = true
                },
                onDeleteSchedule = { key ->
                    viewModel.deleteScheduleByKey(key)
                },
                onPomodoroToggle = viewModel::setTaskPomodoroEnabled,
                onPomodoroTargetUnitsChange = viewModel::setTaskPomodoroTargetUnits,
                onPomodoroDoneUnitsChange = viewModel::setTaskPomodoroDoneUnits,
                childTasks = directChildTasksForEditingTask,
                editingParentTaskId = editingTaskId,
                taskNameSuggestionCandidates = allTaskEntities,
                childNameSuggestionRules = allEnabledChildRules,
                onClickChildTask = { childTaskId ->
                    viewModel.openChildTaskForEdit(childTaskId)
                },
                canManageChildTasks = !isEditingChildTask,
                canAddChildTask = editingTaskId != null && !isEditingChildTask,
                onAddChildTask = { childName, ruleType, timesPerDay, timesPerOccurrence, g5TargetCount, onCreated ->
                    viewModel.createDirectChildTaskForEditingTask(
                        categoryColor = selectedCategory.color,
                        childName = childName,
                        ruleType = ruleType,
                        timesPerDay = timesPerDay,
                        timesPerOccurrence = timesPerOccurrence,
                        g5TargetCount = g5TargetCount,
                        onCreated = onCreated
                    )
                },
                blockedGrandChildTasks = blockedGrandChildTasksForEditingTask,
                canManageSchedules = !isEditingChildTask,
                childRuleByChildTaskId = childRuleByChildTaskId,
                onSaveChildRule = viewModel::saveChildRuleForEditingTask,
                isEditingChildTask = isEditingChildTask,
                editingChildRule = editingChildRule,
                onSaveEditingChildRule = viewModel::saveRuleForEditingChildTask,

                modifier = Modifier.padding(padding)

            )

            if (showPickTaskCategory) {
                PickParentDialogSmall(
                    items = pickerFlatten.items,
                    levelById = pickerFlatten.levelById,
                    onDismiss = { showPickTaskCategory = false },
                    onPick = { categoryId ->
                        viewModel.setTaskCategoryId(categoryId)
                        showPickTaskCategory = false
                    }
                )
            }
            if (showScheduleDialog) {
                val addSchedule = (editingScheduleKey == null)
                AddEditeScheduleDialog(
                    addSchedule = addSchedule,
                    taskName = taskDraft.name.ifBlank { "Task" },
                    draft = scheduleDraft,
                    onDismiss = {
                        showScheduleDialog = false
                        viewModel.finishEditSchedule()
                    },
                    onTitleChange = viewModel::setScheduleTitle,
                    onModeChange = viewModel::setScheduleMode,
                    onStartChange = viewModel::setScheduleStart,
                    onEndChange = viewModel::setScheduleEnd,
                    onDurationChange = viewModel::setScheduleDuration,
                    onRepeatingChange = viewModel::setRepeatEnabled,
                    onConfirm = {
                        viewModel.confirmScheduleFromDialog()

                        showScheduleDialog = false
                    },
                    onDateChange = viewModel::setScheduleDate,
                    onRepeatIntervalChange = viewModel::setRepeatInterval,
                    onRepeatUnitChange = viewModel::setRepeatUnit,
                    onWeekdaysMaskChange = viewModel::setRepeatWeekdaysMask,
                    reminders = reminders,
                    onOpenAddReminder = {
                        viewModel.startAddReminder()
                        showReminderDialog = true
                    },
                    onClickReminder = { key ->
                        viewModel.startEditReminderByKey(key)
                        showReminderDialog = true
                    },
                    onDeleteReminder = { key ->
                        viewModel.deleteReminderByKey(key)
                    },
                    isPomodoroTask = (taskDraft.taskMode == TaskMode.POMODORO),
                    onFocusChange = viewModel::setScheduleFocusMinutes,
                    onShortBreakChange = viewModel::setScheduleShortBreakMinutes,
                    onLongBreakChange = viewModel::setScheduleLongBreakMinutes,
                    onLongBreakEveryChange = viewModel::setScheduleLongBreakEvery,
                    onPomodoroUnitsPerDayChange = viewModel::setSchedulePomodoroUnitsPerDay,

                    )
            }

            if (showReminderDialog) {
                val addReminder = (editingReminderKey == null)

                AddEditeReminderDialog(
                    addReminder = addReminder,
                    draft = reminderDraft,
                    onDismiss = {
                        showReminderDialog = false
                        viewModel.finishEditReminder() // ✅ اگر داری؛ اگر نداری پایین میگم چی باید باشه
                    },
                    isPomodoroSchedule=scheduleDraft.mode == ScheduleMode.POMODORO,
                    onTitleChange = viewModel::setReminderTitle,
                    onModeChange = viewModel::setReminderMode,
                    onOffsetDaysChange = viewModel::setReminderOffsetDays,
                    onOffsetHoursChange = viewModel::setReminderOffsetHours,
                    onOffsetMinutesChange = viewModel::setReminderOffsetMinutes,
                    onBeforeAfterChange = viewModel::setReminderBeforeAfter,
                    onAnchorChange = viewModel::setReminderAnchor,
                    onFixedTimeChange = viewModel::setReminderFixedTime,
                    onStrengthChange = viewModel::setReminderStrength,
                    onVibrateChange = viewModel::setReminderVibrate,
                    onAlarmSoundUriChange = viewModel::setReminderAlarmSoundUri,
                    onCaptchaEnabledChange = viewModel::setReminderCaptchaEnabled,
                    onStartFocusToggle =viewModel::setOnStartFocusEnabled,
                    onStartBreakToggle =viewModel::setOnStartBreakEnabled,
                    onEndBreakToggle =viewModel::setOnEndBreakEnabled,
                    onConfirm = {
                        requestPostNotifPermission { granted ->
                            viewModel.confirmReminderFromDialog()
                            showReminderDialog = false
                        }
                    }
                )
            }


        }

    }


}


@Composable
private fun AddEditeTaskScreen(
    addTaskMod: Boolean,
    categoryName: String,
    categoryIconName: String,
    categoryColorHex: String,
    draft: TaskDraft,
    onClickBack: () -> Unit,
    onNameChange: (String) -> Unit,
    onPriorityChange: (Int) -> Unit,
    onCompletedToggle: (Boolean) -> Unit,
    onNoteChange: (String) -> Unit,
    onInsertAtTopChange: (Boolean) -> Unit,   // ✅ جدید
    onChildLevelChange: (Int) -> Unit,        // ✅ جدید
    onConfirm: (ConfirmAction) -> Unit,       // ✅ جدید
    onOpenSchedule: () -> Unit,
    onPickCategory: () -> Unit,
    allowedChildLevels: Set<Int>,
    schedules: List<TaskScheduleUi>,
    onClickSchedule: (Int) -> Unit,
    onDeleteSchedule: (Int) -> Unit,
    onPomodoroToggle: (Boolean) -> Unit,
    onPomodoroTargetUnitsChange: (Int?) -> Unit,
    onPomodoroDoneUnitsChange: (Int) -> Unit,
    childTasks: List<TaskEntity>,
    editingParentTaskId: Int?,
    taskNameSuggestionCandidates: List<TaskEntity>,
    childNameSuggestionRules: List<TaskChildRuleEntity>,
    onClickChildTask: (Int) -> Unit,
    canManageChildTasks: Boolean,
    onToggleChildTaskCompleted: (Int, Boolean) -> Unit,
    onDeleteChildTask: (Int) -> Unit,
    blockedGrandChildTasks: List<TaskEntity>,
    canManageSchedules: Boolean,
    childRuleByChildTaskId: Map<Int, TaskChildRuleEntity>,
    onSaveChildRule: (
        childTaskId: Int,
        ruleType: String,
        timesPerDay: Int,
        timesPerOccurrence: Int,
        g5TargetCount: Int,
        sortOrder: Int
    ) -> Unit,
    isEditingChildTask: Boolean,
    editingChildRule: TaskChildRuleEntity?,
    onSaveEditingChildRule: (
        ruleType: String,
        timesPerDay: Int,
        timesPerOccurrence: Int,
        g5TargetCount: Int
    ) -> Unit,
    canAddChildTask: Boolean,
    onAddChildTask: (
        childName: String,
        ruleType: String,
        timesPerDay: Int,
        timesPerOccurrence: Int,
        g5TargetCount: Int,
        onCreated: (Int) -> Unit
    ) -> Unit,
    modifier: Modifier,

    ) {

    var pendingDeleteScheduleId by rememberSaveable { mutableStateOf<Int?>(null) }
    var pendingDeleteScheduleTitle by rememberSaveable { mutableStateOf("") }
    var showAddNote by rememberSaveable { mutableStateOf(false) }

    var schedulesExpanded by rememberSaveable { mutableStateOf(false) }

    var childTasksExpanded by rememberSaveable { mutableStateOf(false) }

    var editingRuleChildTaskId by rememberSaveable { mutableStateOf<Int?>(null) }

    var showEditingChildRuleDialog by rememberSaveable { mutableStateOf(false) }

    var showAddChildTaskDialog by rememberSaveable { mutableStateOf(false) }
    var newChildTaskName by rememberSaveable { mutableStateOf("") }

    var showNewChildRuleDialog by rememberSaveable { mutableStateOf(false) }

    var newChildRuleType by rememberSaveable {
        mutableStateOf(TaskChildRuleType.ONCE_PER_PARENT_OCCURRENCE)
    }

    var newChildTimesPerDay by rememberSaveable {
        mutableIntStateOf(1)
    }

    var newChildTimesPerOccurrence by rememberSaveable {
        mutableIntStateOf(1)
    }

    var newChildG5TargetCount by rememberSaveable {
        mutableIntStateOf(5)
    }

    val newChildTaskNameSuggestions = remember(
        newChildTaskName,
        newChildRuleType,
        editingParentTaskId,
        taskNameSuggestionCandidates,
        childNameSuggestionRules,
        childTasks
    ) {
        buildChildTaskNameSuggestions(
            query = newChildTaskName,
            selectedRuleType = newChildRuleType,
            parentTaskId = editingParentTaskId,
            allTasks = taskNameSuggestionCandidates,
            allRules = childNameSuggestionRules,
            currentParentChildren = childTasks,
            limit = 8
        )
    }

    var pendingDeleteChildTaskId by rememberSaveable { mutableStateOf<Int?>(null) }
    var pendingDeleteChildTaskName by rememberSaveable { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface, shape = MaterialTheme.shapes.extraLarge)
            .verticalScroll(rememberScrollState())
    )

    {

        // Top bar
        AddEditeDialogTopBar(
            title = if (addTaskMod) "New Task" else "Edit Task",
            onNavigationClick = onClickBack,
            actions = {
                if (addTaskMod) {
                    IconButton(
                        onClick = { onConfirm(ConfirmAction.SAVE_AND_CONTINUE) },
                        enabled = draft.name.isNotBlank()
                    ) {
                        Icon(Icons.Filled.DoneAll, contentDescription = null)
                    }
                }

                IconButton(
                    onClick = { onConfirm(ConfirmAction.SAVE_AND_CLOSE) },
                    enabled = draft.name.isNotBlank()
                ) {
                    Icon(Icons.Filled.Check, contentDescription = null)
                }
            }
        )


        //title
        AddEditeDialogTextField(
            value = draft.name,
            onValueChange = onNameChange,
            hint = "what to do",
        )

        // Category
        AddEditeDialogRow(
            onClick = onPickCategory,
            content = {
                Icon(
                    imageVector = iconFromKey(categoryIconName),
                    contentDescription = null,
                    tint = colorFromHex(categoryColorHex),
                    modifier = Modifier.size(26.dp)
                )
                Spacer(Modifier.width(14.dp))
                Text(
                    categoryName,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.weight(1f)
                )

                Spacer(Modifier.weight(1f))

                VerticalDivider(thickness = 1.dp, modifier = Modifier.height(24.dp))

                Spacer(Modifier.width(8.dp))

                IconButton(onClick = { onInsertAtTopChange(!draft.insertAtTop) }) {
                    Icon(
                        imageVector = if (draft.insertAtTop) Icons.Filled.ArrowUpward else Icons.Filled.ArrowDownward,
                        contentDescription = "insert position"
                    )
                }
            },
        )

        // Priority
        AddEditeDialogRow(
            onClick = null,
            content = {
                Icon(Icons.Filled.Flag, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Priority", modifier = Modifier.width(70.dp))
                PriorityDots(
                    selected = draft.priority,
                    onPick = onPriorityChange
                )
            },
        )

        //Child
        AddEditeDialogRow(
            onClick = null,
            content = {
                Icon(Icons.Filled.SubdirectoryArrowRight, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Child", modifier = Modifier.width(70.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {

                    ChildLevelChip(
                        "-",
                        selected = draft.childLevel == 0,
                        enabled = 0 in allowedChildLevels
                    ) { onChildLevelChange(0) }
                    ChildLevelChip(
                        ">",
                        selected = draft.childLevel == 1,
                        enabled = 1 in allowedChildLevels
                    ) { onChildLevelChange(1) }
                    ChildLevelChip(
                        ">>",
                        selected = draft.childLevel == 2,
                        enabled = 2 in allowedChildLevels
                    ) { onChildLevelChange(2) }
                    ChildLevelChip(
                        ">>>",
                        selected = draft.childLevel == 3,
                        enabled = 3 in allowedChildLevels
                    ) { onChildLevelChange(3) }

                }
            },
        )


        // Completed & Pomodoro
        AddEditeDialogRow(
            onClick = null,
            content = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {

                    // ---- Completed (left) ----
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .weight(1f)
                            .clickable { onCompletedToggle(!draft.isCompleted) }
                            .padding(vertical = 6.dp)
                    ) {
                        Icon(
                            imageVector = if (draft.isCompleted) Icons.Filled.CheckCircle else Icons.Filled.RadioButtonUnchecked,
                            contentDescription = null,
                            tint = if (draft.isCompleted) Color(0xFF2E7D32) else Color.Gray
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(if (draft.isCompleted) "Completed" else "Uncompleted")
                    }

                    VerticalDivider(Modifier.height(34.dp))
                    Spacer(Modifier.width(10.dp))

                    // ---- Pomodoro (right) ----
                    val isPomodoro = draft.taskMode == TaskMode.POMODORO
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .weight(1f)
                            .clickable { onPomodoroToggle(!isPomodoro) }
                            .padding(vertical = 6.dp)
                    ) {
                        Icon(
                            imageVector = if (isPomodoro) Icons.Filled.CheckCircle else Icons.Filled.RadioButtonUnchecked,
                            contentDescription = null,
                            tint = if (isPomodoro) Color(0xFF2E7D32) else Color.Gray
                        )
                        Spacer(Modifier.width(8.dp))
                        Text("Pomodoro")
                    }
                }
            },
        )

        //Pomodoro Row
        if (draft.taskMode == TaskMode.POMODORO) {

            AddEditeDialogRow(
                onClick = null,
                content = {
                    TaskDialogForPomodoroRow(
                        draft = draft,
                        onPomodoroTargetUnitsChange = onPomodoroTargetUnitsChange,
                        onPomodoroDoneUnitsChange = onPomodoroDoneUnitsChange,
                    )
                }
            )
        }


        // Note
        AddEditeDialogRow(
            onClick = { showAddNote = true },
            content = {
                Icon(Icons.AutoMirrored.Filled.Note, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Add note", modifier = Modifier.weight(1f))

            },
        )

        // Schedule or child rule section
        if (!isEditingChildTask) {
            AddEditeDialogRow(
                onClick = {
                    if (schedules.isNotEmpty()) {
                        schedulesExpanded = !schedulesExpanded
                    } else {
                        onOpenSchedule()
                    }
                },
                content = {
                    Icon(Icons.Filled.Event, contentDescription = null)

                    Spacer(Modifier.width(8.dp))

                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = "Schedules",
                            style = MaterialTheme.typography.bodyLarge
                        )

                        if (schedules.isNotEmpty()) {
                            Text(
                                text = "${schedules.size} schedule${if (schedules.size > 1) "s" else ""}",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        } else {
                            Text(
                                text = "No schedule yet",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    IconButton(
                        onClick = onOpenSchedule
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Add,
                            contentDescription = "Add schedule"
                        )
                    }

                    if (schedules.isNotEmpty()) {
                        IconButton(
                            onClick = {
                                schedulesExpanded = !schedulesExpanded
                            }
                        ) {
                            Icon(
                                imageVector = if (schedulesExpanded) {
                                    Icons.Filled.KeyboardArrowUp
                                } else {
                                    Icons.Filled.KeyboardArrowDown
                                },
                                contentDescription = "Expand schedules"
                            )
                        }
                    }
                }
            )

            if (schedules.isNotEmpty() && schedulesExpanded) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 44.dp, end = 12.dp)
                ) {
                    schedules.forEach { ui ->
                        ScheduleRow(
                            schedule = ui.schedule,
                            onClick = { onClickSchedule(ui.key) },
                            onRequestDelete = {
                                pendingDeleteScheduleId = ui.key
                                pendingDeleteScheduleTitle =
                                    ui.schedule.title?.takeIf { it.isNotBlank() }
                                        ?: taskNameForScheduleFallback(draft.name)
                            }
                        )

                        HorizontalDivider(thickness = 0.5.dp)
                    }
                }
            }
        } else {
            AddEditeDialogRow(
                onClick = {
                    showEditingChildRuleDialog = true
                },
                content = {
                    Icon(Icons.Filled.Pattern, contentDescription = null)

                    Spacer(Modifier.width(8.dp))

                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = "Child rule",
                            style = MaterialTheme.typography.bodyLarge
                        )

                        Text(
                            text = childRuleSummaryText(editingChildRule),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Icon(
                        imageVector = Icons.Filled.ArrowForwardIos,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                }
            )

            AddEditeDialogRow(
                onClick = null,
                content = {
                    Icon(
                        imageVector = Icons.Filled.Event,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(Modifier.width(8.dp))

                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = "Schedule controlled by parent",
                            style = MaterialTheme.typography.bodyLarge
                        )

                        Text(
                            text = "This task is a child task. Its timing is controlled by the parent rule.",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            )

            if (schedules.isNotEmpty()) {
                AddEditeDialogRow(
                    onClick = null,
                    content = {
                        Icon(
                            imageVector = Icons.Filled.Warning,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.error
                        )

                        Spacer(Modifier.width(8.dp))

                        Column(
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = "Invalid child schedule",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.error
                            )

                            Text(
                                text = "This child task already has ${schedules.size} independent schedule(s). Cleanup is required.",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                )
            }
        }




        // Child tasks section
        if (canManageChildTasks) {
            // Child tasks section
            AddEditeDialogRow(
                onClick = {
                    if (childTasks.isNotEmpty()) {
                        childTasksExpanded = !childTasksExpanded
                    }
                },
                content = {
                    Icon(Icons.Filled.SubdirectoryArrowRight, contentDescription = null)

                    Spacer(Modifier.width(8.dp))

                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = "Child tasks",
                            style = MaterialTheme.typography.bodyLarge
                        )

                        if (childTasks.isNotEmpty()) {
                            Text(
                                text = "${childTasks.size} child${if (childTasks.size > 1) "ren" else ""}",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        } else {
                            Text(
                                text = "No child task yet",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    IconButton(
                        enabled = canAddChildTask,
                        onClick = {
                            newChildTaskName = ""

                            newChildRuleType = TaskChildRuleType.ONCE_PER_PARENT_OCCURRENCE
                            newChildTimesPerDay = 1
                            newChildTimesPerOccurrence = 1
                            newChildG5TargetCount = 5

                            showAddChildTaskDialog = true
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Add,
                            contentDescription = "Add child task"
                        )
                    }

                    if (childTasks.isNotEmpty()) {
                        IconButton(
                            onClick = {
                                childTasksExpanded = !childTasksExpanded
                            }
                        ) {
                            Icon(
                                imageVector = if (childTasksExpanded) {
                                    Icons.Filled.KeyboardArrowUp
                                } else {
                                    Icons.Filled.KeyboardArrowDown
                                },
                                contentDescription = "Expand child tasks"
                            )
                        }
                    }
                }
            )

            if (childTasks.isNotEmpty() && childTasksExpanded) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 44.dp, end = 12.dp)
                ) {
                    childTasks.forEach { child ->
                        val childId = child.id

                        ChildTaskRow(
                            child = child,
                            rule = childId?.let { childRuleByChildTaskId[it] },
                            onClick = {
                                childId?.let(onClickChildTask)
                            },
                            onToggleCompleted = { completed ->
                                childId?.let {
                                    onToggleChildTaskCompleted(it, completed)
                                }
                            },
                            onRequestDelete = {
                                childId?.let {
                                    pendingDeleteChildTaskId = it
                                    pendingDeleteChildTaskName = child.name
                                }
                            }
                        )

                        HorizontalDivider(thickness = 0.5.dp)
                    }
                }
            }
        }

        if (!canManageChildTasks && blockedGrandChildTasks.isNotEmpty()) {
            AddEditeDialogRow(
                onClick = null,
                content = {
                    Icon(
                        imageVector = Icons.Filled.Warning,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.error
                    )

                    Spacer(Modifier.width(8.dp))

                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = "Invalid child structure",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.error
                        )

                        Text(
                            text = "This child task already has ${blockedGrandChildTasks.size} nested child task(s). Cleanup is required.",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            )
        }

        val editingRuleChildTask = remember(
            editingRuleChildTaskId,
            childTasks
        ) {
            val childId = editingRuleChildTaskId ?: return@remember null
            childTasks.firstOrNull { it.id == childId }
        }

        editingRuleChildTask?.let { child ->
            val childId = child.id

            if (childId != null) {
                ChildRuleEditorDialog(
                    child = child,
                    rule = childRuleByChildTaskId[childId],
                    onDismiss = {
                        editingRuleChildTaskId = null
                    },
                    onConfirm = { ruleType, timesPerDay, timesPerOccurrence, g5TargetCount ->
                        onSaveChildRule(
                            childId,
                            ruleType,
                            timesPerDay,
                            timesPerOccurrence,
                            g5TargetCount,
                            child.siblingIndex
                        )

                        editingRuleChildTaskId = null
                    }
                )
            }
        }

        if (showEditingChildRuleDialog) {
            ChildRuleEditorDialog(
                title = draft.name.ifBlank { "Child task" },
                rule = editingChildRule,
                onDismiss = {
                    showEditingChildRuleDialog = false
                },
                onConfirm = { ruleType, timesPerDay, timesPerOccurrence, g5TargetCount ->
                    onSaveEditingChildRule(
                        ruleType,
                        timesPerDay,
                        timesPerOccurrence,
                        g5TargetCount
                    )

                    showEditingChildRuleDialog = false
                }
            )
        }

        if (pendingDeleteScheduleId != null) {
            AlertDialog(
                onDismissRequest = { pendingDeleteScheduleId = null },
                title = { Text("Delete schedule?") },
                text = { Text("آیا از حذف این اسکچول مطمئن هستی؟\n\n$pendingDeleteScheduleTitle") },
                confirmButton = {
                    TextButton(onClick = {
                        val key = pendingDeleteScheduleId!!
                        onDeleteSchedule(key) // وصل به viewModel.deleteScheduleByKey(key)
                        pendingDeleteScheduleId = null
                    }) { Text("Delete") }
                },
                dismissButton = {
                    TextButton(onClick = {
                        pendingDeleteScheduleId = null
                    }) { Text("Cancel") }
                }
            )
        }

        if (showAddNote) {
            AddEditDescriptionDialog(
                title = "Add Note",
                value = draft.note,
                onValueChange = onNoteChange,
                onDismiss = { showAddNote = false },
                onConfirm = { showAddNote = false }
            )
        }

        if (showAddChildTaskDialog) {
            val rulePreview = newChildRulePreviewEntity(
                ruleType = newChildRuleType,
                timesPerDay = newChildTimesPerDay,
                timesPerOccurrence = newChildTimesPerOccurrence,
                g5TargetCount = newChildG5TargetCount
            )

            fun submitChildTask(
                closeDialog: Boolean,
                openFullEditor: Boolean
            ) {
                val cleanName = newChildTaskName.trim()

                if (cleanName.isBlank()) return

                onAddChildTask(
                    cleanName,
                    newChildRuleType,
                    newChildTimesPerDay,
                    newChildTimesPerOccurrence,
                    newChildG5TargetCount
                ) { createdChildTaskId ->
                    if (openFullEditor) {
                        onClickChildTask(createdChildTaskId)
                    }
                }

                childTasksExpanded = true

                if (closeDialog || openFullEditor) {
                    showAddChildTaskDialog = false
                    newChildTaskName = ""
                } else {
                    newChildTaskName = ""
                }
            }

            DimmedDialog(
                onDismiss = {
                    showAddChildTaskDialog = false
                    newChildTaskName = ""
                },
                shape = RoundedCornerShape(28.dp),
                modifier = Modifier
                    .fillMaxWidth(0.92f)
                    .background(
                        MaterialTheme.colorScheme.surface,
                        shape = RoundedCornerShape(28.dp)
                    )
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    AddEditeDialogTopBar(
                        title = "New child task",
                        onNavigationClick = {
                            showAddChildTaskDialog = false
                            newChildTaskName = ""
                        },
                        actions = {
                            IconButton(
                                onClick = {
                                    showNewChildRuleDialog = true
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Pattern,
                                    contentDescription = "Rule"
                                )
                            }

                            IconButton(
                                enabled = newChildTaskName.isNotBlank(),
                                onClick = {
                                    submitChildTask(
                                        closeDialog = true,
                                        openFullEditor = false
                                    )
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Check,
                                    contentDescription = "Create"
                                )
                            }

                            IconButton(
                                enabled = newChildTaskName.isNotBlank(),
                                onClick = {
                                    submitChildTask(
                                        closeDialog = false,
                                        openFullEditor = false
                                    )
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.DoneAll,
                                    contentDescription = "Create and continue"
                                )
                            }

                            IconButton(
                                enabled = newChildTaskName.isNotBlank(),
                                onClick = {
                                    submitChildTask(
                                        closeDialog = true,
                                        openFullEditor = true
                                    )
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.ArrowForwardIos,
                                    contentDescription = "Create and open"
                                )
                            }
                        }
                    )

                    AddEditeDialogRow(
                        onClick = {
                            showNewChildRuleDialog = true
                        },
                        content = {
                            Icon(
                                imageVector = Icons.Filled.Pattern,
                                contentDescription = null
                            )

                            Spacer(Modifier.width(8.dp))

                            Column(
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = "Rule",
                                    style = MaterialTheme.typography.bodyLarge
                                )

                                Text(
                                    text = childRuleSummaryText(rulePreview),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }

                            Icon(
                                imageVector = Icons.Filled.ArrowForwardIos,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                        },
                        showDivider = true
                    )

                    AddEditeDialogTextField(
                        value = newChildTaskName,
                        onValueChange = { value ->
                            newChildTaskName = value
                        },
                        hint = "Child task name",
                        showDivider = newChildTaskNameSuggestions.isEmpty()
                    )

                    if (newChildTaskNameSuggestions.isNotEmpty()) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 18.dp, end = 18.dp, bottom = 10.dp)
                        ) {
                            newChildTaskNameSuggestions.forEach { suggestion ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(14.dp))
                                        .clickable {
                                            newChildTaskName = suggestion
                                        }
                                        .padding(horizontal = 12.dp, vertical = 9.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.History,
                                        contentDescription = null,
                                        modifier = Modifier.size(18.dp),
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                                    )

                                    Spacer(Modifier.width(10.dp))

                                    Text(
                                        text = suggestion,
                                        modifier = Modifier.weight(1f),
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                            }

                            HorizontalDivider(thickness = 0.5.dp)
                        }
                    }
                }
            }
        }

        if (showNewChildRuleDialog) {
            val rulePreview = newChildRulePreviewEntity(
                ruleType = newChildRuleType,
                timesPerDay = newChildTimesPerDay,
                timesPerOccurrence = newChildTimesPerOccurrence,
                g5TargetCount = newChildG5TargetCount
            )

            ChildRuleEditorDialog(
                title = newChildTaskName.ifBlank { "Child rule" },
                rule = rulePreview,
                onDismiss = {
                    showNewChildRuleDialog = false
                },
                onConfirm = { ruleType, timesPerDay, timesPerOccurrence, g5TargetCount ->
                    newChildRuleType = ruleType
                    newChildTimesPerDay = timesPerDay
                    newChildTimesPerOccurrence = timesPerOccurrence
                    newChildG5TargetCount = g5TargetCount

                    showNewChildRuleDialog = false
                }
            )
        }

        if (pendingDeleteChildTaskId != null) {
            AlertDialog(
                onDismissRequest = {
                    pendingDeleteChildTaskId = null
                    pendingDeleteChildTaskName = ""
                },
                title = {
                    Text("Delete child task?")
                },
                text = {
                    Text(
                        "آیا از حذف این تسک فرزند مطمئنی؟\n\n$pendingDeleteChildTaskName"
                    )
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            val childId = pendingDeleteChildTaskId ?: return@TextButton

                            onDeleteChildTask(childId)

                            pendingDeleteChildTaskId = null
                            pendingDeleteChildTaskName = ""
                        }
                    ) {
                        Text("Delete")
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = {
                            pendingDeleteChildTaskId = null
                            pendingDeleteChildTaskName = ""
                        }
                    ) {
                        Text("Cancel")
                    }
                }
            )
        }


    }

}

@Composable
private fun ChildTaskRow(
    child: TaskEntity,
    rule: TaskChildRuleEntity?,
    onClick: () -> Unit,
    onToggleCompleted: (Boolean) -> Unit,
    onRequestDelete: () -> Unit
) {
    val isDone = child.isCompleted

    ListItem(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick),
        leadingContent = {
            IconButton(
                onClick = {
                    onToggleCompleted(!isDone)
                },
                modifier = Modifier.size(40.dp)
            ) {
                Icon(
                    imageVector = if (isDone) {
                        Icons.Filled.CheckCircle
                    } else {
                        Icons.Filled.RadioButtonUnchecked
                    },
                    contentDescription = if (isDone) {
                        "Mark child task incomplete"
                    } else {
                        "Mark child task complete"
                    },
                    tint = if (isDone) {
                        Color(0xFF2E7D32)
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    }
                )
            }
        },
        headlineContent = {
            Text(
                text = child.name,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = if (isDone) {
                    MaterialTheme.colorScheme.onSurfaceVariant
                } else {
                    MaterialTheme.colorScheme.onSurface
                }
            )
        },
        supportingContent = {
            Text(
                text = childRuleSummaryText(rule),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        trailingContent = {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onRequestDelete,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Delete,
                        contentDescription = "Delete child task",
                        tint = MaterialTheme.colorScheme.error
                    )
                }

                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
            }
        },
        colors = ListItemDefaults.colors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    )
}

private fun buildChildTaskNameSuggestions(
    query: String,
    selectedRuleType: String,
    parentTaskId: Int?,
    allTasks: List<TaskEntity>,
    allRules: List<TaskChildRuleEntity>,
    currentParentChildren: List<TaskEntity>,
    limit: Int = 8
): List<String> {
    val normalizedQuery = normalizeTaskNameSuggestionText(query)

    val taskById = allTasks
        .mapNotNull { task ->
            val id = task.id ?: return@mapNotNull null
            id to task
        }
        .toMap()

    val sameRuleChildTaskIds = allRules
        .asSequence()
        .filter { rule ->
            rule.isEnabled
        }
        .filter { rule ->
            rule.ruleType == selectedRuleType
        }
        .map { rule ->
            rule.childTaskId
        }
        .toSet()

    val parentChildIds = currentParentChildren
        .mapNotNull { child ->
            child.id
        }
        .toSet()

    val candidates = buildList {
        // 1) تسک‌های فرزند همین والد، completedها بالاتر
        currentParentChildren
            .sortedWith(
                compareByDescending<TaskEntity> { child ->
                    child.isCompleted
                }.thenBy { child ->
                    child.siblingIndex
                }.thenBy { child ->
                    child.id ?: 0
                }
            )
            .forEach { child ->
                add(
                    ChildTaskNameSuggestionCandidate(
                        task = child,
                        groupPriority = 0
                    )
                )
            }

        // 2) تسک‌های هم‌رول در کل پروژه
        sameRuleChildTaskIds
            .asSequence()
            .mapNotNull { childTaskId ->
                taskById[childTaskId]
            }
            .filter { task ->
                task.id !in parentChildIds
            }
            .sortedWith(
                compareByDescending<TaskEntity> { task ->
                    task.isCompleted
                }.thenBy { task ->
                    task.name.trim()
                }.thenBy { task ->
                    task.id ?: 0
                }
            )
            .forEach { task ->
                add(
                    ChildTaskNameSuggestionCandidate(
                        task = task,
                        groupPriority = 1
                    )
                )
            }

        // 3) کل تسک‌ها
        allTasks
            .asSequence()
            .filter { task ->
                task.id != parentTaskId
            }
            .filter { task ->
                task.id !in parentChildIds
            }
            .filter { task ->
                task.id !in sameRuleChildTaskIds
            }
            .sortedWith(
                compareBy<TaskEntity> { task ->
                    task.name.trim()
                }.thenBy { task ->
                    task.id ?: 0
                }
            )
            .forEach { task ->
                add(
                    ChildTaskNameSuggestionCandidate(
                        task = task,
                        groupPriority = 2
                    )
                )
            }
    }

    return candidates
        .asSequence()
        .mapNotNull { candidate ->
            val cleanName = candidate.task.name.trim()
            val normalizedName = normalizeTaskNameSuggestionText(cleanName)

            if (cleanName.isBlank()) {
                null
            } else {
                candidate.copy(
                    cleanName = cleanName,
                    normalizedName = normalizedName
                )
            }
        }
        .filter { candidate ->
            normalizedQuery.isBlank() ||
                    candidate.normalizedName.contains(normalizedQuery)
        }
        .distinctBy { candidate ->
            candidate.normalizedName
        }
        .sortedWith(
            compareBy<ChildTaskNameSuggestionCandidate> { candidate ->
                candidate.groupPriority
            }.thenBy { candidate ->
                if (normalizedQuery.isBlank()) {
                    false
                } else {
                    !candidate.normalizedName.startsWith(normalizedQuery)
                }
            }.thenByDescending { candidate ->
                candidate.task.isCompleted
            }.thenBy { candidate ->
                candidate.cleanName.length
            }.thenBy { candidate ->
                candidate.cleanName
            }
        )
        .take(limit)
        .map { candidate ->
            candidate.cleanName
        }
        .toList()
}

private data class ChildTaskNameSuggestionCandidate(
    val task: TaskEntity,
    val groupPriority: Int,
    val cleanName: String = "",
    val normalizedName: String = ""
)

private fun normalizeTaskNameSuggestionText(
    value: String
): String {
    return value
        .trim()
        .replace('ي', 'ی')
        .replace('ك', 'ک')
        .replace(Regex("\\s+"), " ")
        .lowercase()
}


//>>>>>>>>>>>>>>>> Dialogs <<<<<<<<<<<<<<<<<<

@Composable
private fun AddEditeScheduleDialog(
    addSchedule: Boolean,
    taskName: String,
    draft: ScheduleDraft,
    onDismiss: () -> Unit,
    onTitleChange: (String) -> Unit,
    onModeChange: (ScheduleMode) -> Unit,
    onStartChange: (LocalTime) -> Unit,
    onEndChange: (LocalTime) -> Unit,
    onDurationChange: (Int) -> Unit,
    onRepeatingChange: (Boolean) -> Unit,
    onConfirm: () -> Unit,
    onDateChange: (LocalDate) -> Unit,
    onRepeatIntervalChange: (Int) -> Unit,
    onRepeatUnitChange: (RepeatUnit) -> Unit,
    onWeekdaysMaskChange: (Int) -> Unit,
    reminders: List<TaskReminderUi>,
    onOpenAddReminder: () -> Unit,
    onClickReminder: (Int) -> Unit,
    onDeleteReminder: (Int) -> Unit,
    isPomodoroTask: Boolean,
    onFocusChange: (Int) -> Unit,
    onShortBreakChange: (Int) -> Unit,
    onLongBreakChange: (Int) -> Unit,
    onLongBreakEveryChange: (Int) -> Unit,
    onPomodoroUnitsPerDayChange: (Int) -> Unit,

    ) {
    var pendingDeleteReminderId by rememberSaveable { mutableStateOf<Int?>(null) }
    var pendingDeleteReminderTitle by rememberSaveable { mutableStateOf("") }

    DimmedDialog(
        onDismiss = onDismiss,
        modifier = Modifier
            .fillMaxWidth(0.92f)
            .fillMaxHeight(0.85f)
            .background(MaterialTheme.colorScheme.surface, shape = MaterialTheme.shapes.extraLarge),
    ) {
        Column(
            Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {


            // Top bar
            AddEditeDialogTopBar(
                title = if (addSchedule) "New scheduled" else "Edit scheduled",
                onNavigationClick = onDismiss,
                actions = {
                    IconButton(
                        onClick = onConfirm,
                        enabled = true
                    ) {
                        Icon(Icons.Filled.Check, contentDescription = null)
                    }
                }
            )

            //title
            AddEditeDialogTextField(
                value = draft.title,
                onValueChange = onTitleChange,
                hint = "What is planned",
            )

            // mode dropdown
            AddEditeDialogRow(
                onClick = null,
                content = {
                    val allowedModes =
                        if (isPomodoroTask) listOf(ScheduleMode.POMODORO)
                        else listOf(ScheduleMode.TIME_RANGE, ScheduleMode.AMOUNT_OF_TIME)

                    ModeScheduleDropdownRow(
                        mode = draft.mode,
                        allowedModes = allowedModes,
                        onPick = onModeChange
                    )
                },
                startPadding = 0
            )

            //  TimePicker
            when (draft.mode) {
                ScheduleMode.TIME_RANGE -> {
                    AddEditeDialogRow(
                        onClick = null,
                        content = {
                            TimeRangeRow(
                                start = draft.start,
                                end = draft.end,
                                onStartChange = onStartChange,
                                onEndChange = onEndChange
                            )
                        }
                    )
                }

                ScheduleMode.AMOUNT_OF_TIME -> {
                    AddEditeDialogRow(
                        onClick = null,
                        content = {
                            AmountOfTimeRow(
                                minutes = draft.durationMinutes,
                                onMinutesChange = onDurationChange
                            )
                        }
                    )
                }

                ScheduleMode.POMODORO -> {
                    PomodoroConfigRow(
                        focus = draft.focusMinutes,
                        shortBreak = draft.shortBreakMinutes,
                        longBreak = draft.longBreakMinutes,
                        longBreakEvery = draft.longBreakEvery,
                        unitsPerDay = draft.pomodoroUnitsPerDay,
                        onFocusChange = onFocusChange,
                        onShortBreakChange = onShortBreakChange,
                        onLongBreakChange = onLongBreakChange,
                        onLongBreakEveryChange = onLongBreakEveryChange,
                        onUnitsPerDayChange = onPomodoroUnitsPerDayChange
                    )
                }
            }


            //DateRow
            if (draft.mode == ScheduleMode.TIME_RANGE || draft.mode == ScheduleMode.AMOUNT_OF_TIME) {

                AddEditeDialogRow(
                    onClick = null,
                    content = {
                        JalaliDateRow(
                            selectedDate = draft.date,
                            onChangeDate = { onDatePicked ->
                                // چون draft از VM میاد، باید setter داشته باشی:
                                // viewModel.setScheduleDate(onDatePicked)
                                // اینجا فقط callback می‌گیریم:
                                onDateChange(onDatePicked)
                            }
                        )
                    },
                    startPadding = 0
                )
            }

            // Repeating
            if (draft.mode == ScheduleMode.TIME_RANGE || draft.mode == ScheduleMode.AMOUNT_OF_TIME) {
                AddEditeDialogRow(
                    onClick = null,
                    content = {
                        Icon(Icons.Filled.Repeat, contentDescription = "Repeat")
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Repeating", modifier = Modifier.weight(1f))
                            Switch(
                                checked = draft.repeat.enabled,
                                onCheckedChange = onRepeatingChange
                            )
                        }
                    },
                )

                //RepeatOptions
                if (draft.repeat.enabled) {
                    AddEditeDialogRow(
                        onClick = null,
                        content = {
                            Icon(Icons.Filled.EventRepeat, contentDescription = "Repeat")
                            RepeatEveryRow(
                                interval = draft.repeat.interval,
                                unit = draft.repeat.unit,
                                onIntervalChange = { onRepeatIntervalChange(it) },
                                onUnitChange = { onRepeatUnitChange(it) }
                            )
                        }, showDivider = if (draft.repeat.unit == RepeatUnit.WEEK) false else true
                    )
                    if (draft.repeat.unit == RepeatUnit.WEEK) {
                        WeekdayPickerRow(
                            selectedMask = draft.repeat.weekdaysMask,
                            onChangeMask = onWeekdaysMaskChange
                        )
                        HorizontalDivider(modifier = Modifier.padding(start = 66.dp))
                    }
                }
            }

            Log.i("TEST2", "draft.repeat.weekdaysMask=${draft.repeat.weekdaysMask}")

            //WeeklyPicker for Mode.POMODORO
            if (draft.mode == ScheduleMode.POMODORO) {
                // فقط WeekdayPickerRow
                WeekdayPickerRow(
                    selectedMask = draft.repeat.weekdaysMask,
                    onChangeMask = onWeekdaysMaskChange
                )
                HorizontalDivider(modifier = Modifier.padding(start = 66.dp))
            }


            //add Reminder
            AddEditeDialogRow(
                onClick = onOpenAddReminder,
                content = {
                    Icon(Icons.Filled.History, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Add reminder", modifier = Modifier.weight(1f))
                },
            )

            //Reminder List
            if (reminders.isNotEmpty()) {
                Column(
                    Modifier
                        .fillMaxWidth()
                        .padding(start = 44.dp)
                ) {
                    reminders.forEach { reminderUi ->
                        ReminderRow(
                            ui = reminderUi,
                            onClick = { onClickReminder(reminderUi.key) },
                            onRequestDelete = {
                                pendingDeleteReminderId = reminderUi.key
                            }
                        )
                    }
                }
            }

            if (pendingDeleteReminderId != null) {
                AlertDialog(
                    onDismissRequest = { pendingDeleteReminderId = null },
                    title = { Text("Delete Reminder?") },
                    text = { Text("آیا از حذف این یادآور مطمئن هستی؟") },
                    confirmButton = {
                        TextButton(onClick = {
                            val key = pendingDeleteReminderId!!
                            onDeleteReminder(key) // وصل به viewModel.deleteScheduleByKey(key)
                            pendingDeleteReminderId = null
                        }) { Text("Delete") }
                    },
                    dismissButton = {
                        TextButton(onClick = {
                            pendingDeleteReminderId = null
                        }) { Text("Cancel") }
                    }
                )
            }

        }
    }
}


@Composable
private fun AddEditeReminderDialog(
    addReminder: Boolean,
    draft: ReminderDraft,
    onDismiss: () -> Unit,
    onTitleChange: (String) -> Unit,
    onModeChange: (ReminderMode) -> Unit,
    onOffsetDaysChange: (Int) -> Unit,
    onOffsetHoursChange: (Int) -> Unit,
    onOffsetMinutesChange: (Int) -> Unit,
    onBeforeAfterChange: (BeforeAfter) -> Unit,
    onAnchorChange: (StartEnd) -> Unit,
    onFixedTimeChange: (LocalTime) -> Unit,
    onStrengthChange: (ReminderStrengthMode) -> Unit,
    onVibrateChange: (Boolean) -> Unit,
    isPomodoroSchedule: Boolean,
    onAlarmSoundUriChange: (String?) -> Unit = {},
    onCaptchaEnabledChange: (Boolean) -> Unit,
    onStartFocusToggle: (Boolean) -> Unit,
    onStartBreakToggle: (Boolean) -> Unit,
    onEndBreakToggle: (Boolean) -> Unit,
    onConfirm: () -> Unit,
) {
    val context = LocalContext.current

    DimmedDialog(
        onDismiss = onDismiss,
        modifier = Modifier
            .fillMaxWidth(0.92f)
            .fillMaxHeight(0.85f)
            .background(MaterialTheme.colorScheme.surface, shape = MaterialTheme.shapes.extraLarge),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {

            //top bar
            AddEditeDialogTopBar(
                title = if (addReminder) "New reminder" else "Edit reminder",
                onNavigationClick = onDismiss,
                actions = {
                    IconButton(onClick = onConfirm, enabled = true) {
                        Icon(Icons.Filled.Check, contentDescription = null)
                    }
                }
            )


            //title
            AddEditeDialogTextField(
                value = draft.title,
                onValueChange = onTitleChange,
                hint = "Remind about",
            )


            // ====== Mode ======
            AddEditeDialogRow(
                onClick = null,
                content = {
                    val allowedModes =
                        if (isPomodoroSchedule) listOf(ReminderMode.POMODORO_REMINDER )
                        else listOf(ReminderMode.ALLOCATED, ReminderMode.FIXED_TIME)

                    ModeReminderDropdownRow(
                        mode = draft.mode,
                        allowedModes = allowedModes,
                        onPick = onModeChange
                    )
                },
                startPadding = 0
            )

            // ====== Mode-specific UI ======
            when (draft.mode) {
                ReminderMode.ALLOCATED -> {
                    AllocatedRows(
                        draft = draft,
                        onOffsetDaysChange = onOffsetDaysChange,
                        onOffsetHoursChange = onOffsetHoursChange,
                        onOffsetMinutesChange = onOffsetMinutesChange,
                        onBeforeAfterChange = onBeforeAfterChange,
                        onAnchorChange = onAnchorChange
                    )
                }

                ReminderMode.FIXED_TIME -> {
                    FixedTimeRow(
                        time = draft.fixedTime,
                        onTimeChange = onFixedTimeChange
                    )
                }

                ReminderMode.POMODORO_REMINDER -> {
                    PomodoroReminderRows(
                        draft = draft,
                        onStartFocusToggle = onStartFocusToggle,
                        onStartBreakToggle = onStartBreakToggle,
                        onEndBreakToggle =onEndBreakToggle,
                    )
                }

            }


            // ====== Strength (step bar) ======
            StrengthRow(
                strength = draft.strength,
                onStrengthChange = onStrengthChange
            )

            // ====== Vibrate ======
            AddEditeDialogRow(
                onClick = null,
                content = {
                    Icon(Icons.Filled.Vibration, contentDescription = null)
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Vibrate", modifier = Modifier.weight(1f))
                        Switch(checked = draft.vibrate, onCheckedChange = onVibrateChange)
                    }
                },
            )

            // ====== Sound / Settings hook ======
            SoundSettingsRow(
                strength = draft.strength,
                alarmSoundUri = draft.alarmSoundUri,
                onClickOpenSettings = {
                    when (draft.strength) {
                        ReminderStrengthMode.NOTIFICATION -> openAppNotificationSettings(context)
                        ReminderStrengthMode.ALARM,
                        ReminderStrengthMode.ALARM_AND_CAPTCHA -> openAppDetailsSettings(context) // امن‌ترین / عمومی‌ترین
                    }
                }
            )

            // ====== Captcha ======
            if (draft.strength == ReminderStrengthMode.ALARM_AND_CAPTCHA) {
                AddEditeDialogRow(
                    onClick = null,
                    content = {
                        Icon(Icons.Filled.Pattern, contentDescription = null)
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Captcha required", modifier = Modifier.weight(1f))
                            Switch(
                                checked = draft.captchaEnabled,
                                onCheckedChange = onCaptchaEnabledChange
                            )
                        }
                    },
                )

                // Placeholder برای صفحه پترن
                AddEditeDialogRow(
                    onClick = { /* بعداً: باز کردن صفحه طراحی/ویرایش پترن */ },
                    content = {
                        Spacer(Modifier.width(8.dp))
                        Text("Configure captcha pattern", modifier = Modifier.weight(1f))
                        Icon(Icons.Filled.ArrowForwardIos, contentDescription = null)
                    },
                    startPadding = 14,
                    showDivider = false
                )
            } else {
                Spacer(Modifier.height(8.dp))
            }

            Spacer(Modifier.height(18.dp))
        }
    }
}


@Composable
private fun AddEditDescriptionDialog(
    title: String,
    value: String,
    onValueChange: (String) -> Unit,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    confirmEnabled: Boolean = value.isNotBlank()
) {

    DimmedDialog(
        onDismiss = onDismiss,
        shape = RoundedCornerShape(28.dp),
        modifier = Modifier
            .fillMaxWidth(0.92f)
            .fillMaxHeight(0.85f)
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {

            // 🔹 Top Bar عمومی
            AddEditeDialogTopBar(
                title = title,
                onNavigationClick = onDismiss,
                actions = {
                    IconButton(
                        onClick = onConfirm,
                        enabled = confirmEnabled
                    ) {
                        Icon(Icons.Filled.Check, contentDescription = null)
                    }
                }
            )

            // 🔹 متن بزرگ (تقریباً کل دیالوگ)
            TextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)           // 👈 کل فضای باقی‌مانده را می‌گیرد
                    .padding(20.dp),
                placeholder = {
                    Text(
                        "Write description...",
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                    )
                },
                textStyle = MaterialTheme.typography.bodyLarge,
                singleLine = false,
                maxLines = Int.MAX_VALUE,
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent,
                    errorContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent,
                    errorIndicatorColor = Color.Transparent
                )
            )
        }
    }
}

@Composable
private fun PickParentDialogSmall(
    items: List<CategoryRenderItem>,
    levelById: Map<Int, Int>,
    onDismiss: () -> Unit,
    onPick: (parentId: Int) -> Unit,
) {
    DimmedDialog(
        onDismiss = onDismiss,
        modifier = Modifier
            .fillMaxWidth(0.86f)
            .fillMaxHeight(0.65f)
            .background(MaterialTheme.colorScheme.surface, shape = MaterialTheme.shapes.extraLarge),
        dimAlpha = 0.4f,
        dismissOnBackdropClick = true
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                TextButton(onClick = onDismiss) { Text("Close") }
                Text("انتخاب والد", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.size(48.dp)) // برای بالانس
            }

            HorizontalDivider(modifier = Modifier.padding(top = 8.dp, bottom = 8.dp))

            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(items, key = { it.category.categoryId ?: it.hashCode() }) { item ->
                    val id = item.category.categoryId ?: return@items

                    // level واقعی/نمایشی (از map بهتره)
                    val level = levelById[id] ?: item.level

                    // همون قانون قبلی: والد نباید سطح 4 باشه
                    val enabled = level < 4

                    ParentPickerRow(
                        item = item,
                        computedLevel = level,
                        enabled = enabled,
                        onPick = { onPick(id) }
                    )

                    HorizontalDivider(thickness = 0.5.dp) // مثل صفحه اصلی
                }
            }

        }
    }
}

//>>>>>>>>>>>>>>>> Component <<<<<<<<<<<<<<<<<<
@Composable
private fun AddEditeDialogTopBar(
    title: String,
    onNavigationClick: (() -> Unit)? = null,
    navigationIcon: ImageVector = Icons.Filled.ArrowBackIosNew,
    actions: @Composable RowScope.() -> Unit = {},
    showDivider: Boolean = true
) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            // 🔹 آیکن سمت چپ (اختیاری)
            if (onNavigationClick != null) {
                IconButton(onClick = onNavigationClick) {
                    Icon(navigationIcon, contentDescription = null)
                }
            } else {
                Spacer(Modifier.width(48.dp)) // جای خالی هم‌تراز
            }

            Spacer(Modifier.width(8.dp))

            // 🔹 عنوان
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.weight(1f)
            )

            // 🔹 اکشن‌های سمت راست (هرچی بدی رندر میشه)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                content = actions
            )
        }

        if (showDivider) {
            HorizontalDivider()
        }
    }
}


@Composable
private fun AddEditeDialogTextField(
    value: String,
    onValueChange: (String) -> Unit,
    hint: String,
    modifier: Modifier = Modifier,
    singleLine: Boolean = true,
    showDivider: Boolean = true
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .height(66.dp) // ارتفاع دقیق
    ) {

        TextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            placeholder = {
                Text(
                    text = hint,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.35f)
                )
            },
            singleLine = singleLine,
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                disabledContainerColor = Color.Transparent,
                errorContainerColor = Color.Transparent,

                // خط پایین حذف
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent,
                errorIndicatorColor = Color.Transparent
            )
        )

        if (showDivider) HorizontalDivider()
    }
}


@Composable
private fun AddEditeDialogRow(
    onClick: (() -> Unit)? = null,
    content: @Composable () -> Unit,
    showDivider: Boolean = true,
    startPadding: Int = 14
) {
    val clickableModifier =
        if (onClick != null) {
            Modifier.clickable { onClick() }
        } else {
            Modifier
        }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(66.dp)
            .then(clickableModifier)
            .padding(start = startPadding.dp, end = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        content()
    }
    if (showDivider) HorizontalDivider(modifier = Modifier.padding(start = 66.dp))
}


@Composable
private fun PriorityDots(selected: Int, onPick: (Int) -> Unit) {
    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        (0..3).forEach { i ->
            val isSel = i == selected
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = if (isSel) 1f else 0.5f),
                        shape = CircleShape
                    )
                    .border(
                        width = if (isSel) 2.dp else 0.dp,
                        color = MaterialTheme.colorScheme.onSurface,
                        shape = CircleShape
                    )
                    .clickable { onPick(i) },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = when (i) {
                        0 -> "-"; 1 -> "*"; 2 -> "!"; else -> "!!"
                    },
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }
    }
}


@Composable
private fun ChildLevelChip(
    label: String,
    selected: Boolean,
    enabled: Boolean,
    onClick: () -> Unit
) {
    val alpha = if (enabled) 1f else 0.35f

    Box(
        modifier = Modifier
            .size(36.dp)
            .clip(CircleShape)
            .background(
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = if (selected) 1f else 0.5f),
                shape = CircleShape
            )
            .border(
                width = if (selected) 2.dp else 0.dp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = alpha),
                shape = CircleShape
            )
            .then(
                if (enabled) Modifier.clickable { onClick() } else Modifier
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            label,
            style = MaterialTheme.typography.titleMedium,
            color = LocalContentColor.current.copy(alpha = alpha)
        )
    }
}

@Composable
private fun TaskDialogForPomodoroRow(
    draft: TaskDraft,
    onPomodoroTargetUnitsChange: (Int?) -> Unit,
    onPomodoroDoneUnitsChange: (Int) -> Unit,
) {
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    fun digitsOnly(s: String, maxLen: Int = 6): String =
        s.filter(Char::isDigit).take(maxLen)

    fun parseOrNull(s: String): Int? = s.toIntOrNull()
    fun parseOrZero(s: String): Int = s.toIntOrNull() ?: 0
    fun clampNonNeg(n: Int) = n.coerceAtLeast(0)

    val totalFR = remember { FocusRequester() }
    val doneFR = remember { FocusRequester() }

    val totalInteraction = remember { MutableInteractionSource() }
    val doneInteraction = remember { MutableInteractionSource() }

    var totalFocused by remember { mutableStateOf(false) }
    var doneFocused by remember { mutableStateOf(false) }

    // TextFieldValue تا selection رو کنترل کنیم
    var totalTf by remember {
        val t = (draft.pomodoroTargetUnits ?: 0).toString()
        mutableStateOf(TextFieldValue(t, selection = TextRange(t.length)))
    }
    var doneTf by remember {
        val t = draft.pomodoroDoneUnits.toString()
        mutableStateOf(TextFieldValue(t, selection = TextRange(t.length)))
    }

    // ✅ هر بار لمس (Release) => فوکوس + select-all
    LaunchedEffect(totalInteraction) {
        totalInteraction.interactions.collectLatest { i ->
            if (i is PressInteraction.Release) {
                totalFR.requestFocus()
                yield()
                totalTf = totalTf.copy(selection = TextRange(0, totalTf.text.length))
            }
        }
    }
    LaunchedEffect(doneInteraction) {
        doneInteraction.interactions.collectLatest { i ->
            if (i is PressInteraction.Release) {
                doneFR.requestFocus()
                yield()
                doneTf = doneTf.copy(selection = TextRange(0, doneTf.text.length))
            }
        }
    }

    // ✅ sync از بیرون فقط وقتی فوکوس ندارند
    LaunchedEffect(draft.pomodoroTargetUnits) {
        val t = (draft.pomodoroTargetUnits ?: 0).toString()
        if (!totalFocused && totalTf.text != t) {
            totalTf = totalTf.copy(text = t, selection = TextRange(t.length))
        }
    }
    LaunchedEffect(draft.pomodoroDoneUnits) {
        val t = draft.pomodoroDoneUnits.toString()
        if (!doneFocused && doneTf.text != t) {
            doneTf = doneTf.copy(text = t, selection = TextRange(t.length))
        }
    }

    val tfColors = TextFieldDefaults.colors(
        focusedContainerColor = Color.Transparent,
        unfocusedContainerColor = Color.Transparent,
        disabledContainerColor = Color.Transparent,
        errorContainerColor = Color.Transparent,
        focusedIndicatorColor = Color.Transparent,
        unfocusedIndicatorColor = Color.Transparent,
        disabledIndicatorColor = Color.Transparent,
        errorIndicatorColor = Color.Transparent
    )

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Icon(Icons.Filled.GolfCourse, contentDescription = null)
        Spacer(Modifier.width(8.dp))
        Text("Goal", modifier = Modifier.width(70.dp))

        // ---- Total ----
        TextField(
            value = totalTf,
            onValueChange = { v ->
                val raw = digitsOnly(v.text, maxLen = 6)
                totalTf = v.copy(text = raw, selection = TextRange(raw.length))

                // حین تایپ: می‌تونه خالی هم باشه => null
                val total = parseOrNull(raw)?.let(::clampNonNeg)
                onPomodoroTargetUnitsChange(total)
            },
            singleLine = true,
            interactionSource = totalInteraction,
            modifier = Modifier
                .width(90.dp)
                .focusRequester(totalFR)
                .onFocusChanged { st ->
                    totalFocused = st.isFocused
                    if (!st.isFocused) {
                        // روی blur: خالی => 0، و sync به draft
                        val raw = digitsOnly(totalTf.text, maxLen = 6)
                        val total = clampNonNeg(parseOrZero(raw))
                        totalTf = totalTf.copy(
                            text = total.toString(),
                            selection = TextRange(total.toString().length)
                        )
                        onPomodoroTargetUnitsChange(total)
                    }
                },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Next
            ),
            textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Center),
            label = { Text("Total") },
            colors = tfColors
        )

        Spacer(Modifier.width(10.dp))

        // ---- Done ----
        TextField(
            value = doneTf,
            onValueChange = { v ->
                val raw = digitsOnly(v.text, maxLen = 6)
                doneTf = v.copy(text = raw, selection = TextRange(raw.length))

                val done = clampNonNeg(parseOrZero(raw))
                onPomodoroDoneUnitsChange(done)
            },
            singleLine = true,
            interactionSource = doneInteraction,
            modifier = Modifier
                .width(90.dp)
                .focusRequester(doneFR)
                .onFocusChanged { st ->
                    doneFocused = st.isFocused
                    if (!st.isFocused) {
                        val raw = digitsOnly(doneTf.text, maxLen = 6)
                        val done = clampNonNeg(parseOrZero(raw))
                        doneTf = doneTf.copy(
                            text = done.toString(),
                            selection = TextRange(done.toString().length)
                        )
                        onPomodoroDoneUnitsChange(done)
                    }
                },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    keyboardController?.hide()
                    focusManager.clearFocus()
                }
            ),
            textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Center),
            label = { Text("Done") },
            colors = tfColors
        )
    }
}


@Composable
private fun ScheduleRow(
    schedule: TaskSchedule,
    onClick: () -> Unit,
    onRequestDelete: () -> Unit
) {
    fun minuteToTime(min: Int): String {
        val h = min / 60
        val m = min % 60
        return "%02d:%02d".format(h, m)
    }


    val timeText = when (schedule.mode) {
        ScheduleMode.TIME_RANGE -> {
            val s = schedule.startMinuteOfDay ?: 0
            val e = schedule.endMinuteOfDay ?: 0
            "${minuteToTime(s)} - ${minuteToTime(e)}"
        }

        ScheduleMode.AMOUNT_OF_TIME -> {
            val d = schedule.durationMinutes ?: 0
            "$d min"
        }

        ScheduleMode.POMODORO -> {
            "Pomodoro"
        }
    }

    val dateText =
        schedule.dateEpochDay?.let { epoch ->
            LocalDate.ofEpochDay(epoch).toString()
        } ?: "No date"

    val repeatText = if (schedule.repeating) "Repeats" else "No repeat"

    val reminderText = schedule.reminderMinutesBefore?.let { "Reminder: $it min before" } ?: ""

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(scheduleModeIcon(schedule.mode), contentDescription = null)

        Spacer(Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(timeText, style = MaterialTheme.typography.titleMedium)
            Text(
                text = buildString {
                    append(dateText)
                    append(" • ")
                    append(repeatText)
                    if (reminderText.isNotBlank()) {
                        append(" • ")
                        append(reminderText)
                    }
                },
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        ScheduleOptionsMenu(
            onDelete = onRequestDelete
        )
    }
}

@Composable
private fun ScheduleOptionsMenu(
    onDelete: () -> Unit
) {
    var open by remember { mutableStateOf(false) }

    Box {
        IconButton(onClick = { open = true }) {
            Icon(Icons.Filled.MoreVert, contentDescription = "schedule menu")
        }
        DropdownMenu(expanded = open, onDismissRequest = { open = false }) {
            DropdownMenuItem(
                text = { Text("Delete") },
                leadingIcon = { Icon(Icons.Filled.Delete, contentDescription = null) },
                onClick = { open = false; onDelete() }
            )
        }
    }
}

private fun taskNameForScheduleFallback(taskName: String): String =
    taskName.trim().ifBlank { "Task" }

@Composable
private fun ParentPickerRow(
    item: CategoryRenderItem,
    computedLevel: Int,
    enabled: Boolean,
    onPick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val indent = (computedLevel - 1).coerceAtLeast(0) * 16
    val bg = containerColorForLevel(computedLevel)

    ListItem(
        modifier = modifier
            .fillMaxWidth()
            .padding(start = indent.dp)
            .then(if (enabled) Modifier.clickable { onPick() } else Modifier),
        colors = ListItemDefaults.colors(
            containerColor = bg
        ),
        leadingContent = {
            Icon(
                imageVector = iconFromKey(item.category.iconName),
                contentDescription = item.category.iconName,
                tint = if (enabled) colorFromHex(item.category.color)
                else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.25f),
                modifier = Modifier.size(28.dp)
            )
        },
        headlineContent = {
            Text(
                text = item.category.name,
                color = if (enabled) LocalContentColor.current
                else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.45f)
            )
        },
        supportingContent = {
            if (!enabled) {
                Text(
                    text = "حداکثر عمق",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    )
}

@Composable
private fun containerColorForLevel(level: Int): Color {
    val e = when (level.coerceIn(1, 4)) {
        1 -> 0.dp
        2 -> 2.dp
        3 -> 6.dp
        else -> 12.dp
    }
    return MaterialTheme.colorScheme.surfaceColorAtElevation(e)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ModeScheduleDropdownRow(
    mode: ScheduleMode,
    allowedModes: List<ScheduleMode>,
    onPick: (ScheduleMode) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    val selectedLabel = when (mode) {
        ScheduleMode.TIME_RANGE -> "Time range"
        ScheduleMode.AMOUNT_OF_TIME -> "Amount of time"
        ScheduleMode.POMODORO -> "Pomodoro"
    }

    val items = allowedModes.map { m ->
        m to when (m) {
            ScheduleMode.TIME_RANGE -> "Time range"
            ScheduleMode.AMOUNT_OF_TIME -> "Amount of time"
            ScheduleMode.POMODORO -> "Pomodoro"
        }
    }


    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = Modifier.fillMaxWidth()
    ) {
        // ✅ anchor = کل عرض دیالوگ
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(MenuAnchorType.PrimaryEditable), // یا Primary
            contentAlignment = Alignment.Center
        ) {
            TextField(
                value = selectedLabel,
                onValueChange = {},
                readOnly = true,
                singleLine = true,
                leadingIcon = { Icon(scheduleModeIcon(mode), contentDescription = null) },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier.fillMaxWidth(), // ✅ کوچیک‌تر و وسط
                textStyle = MaterialTheme.typography.bodyLarge.copy(
                    textAlign = TextAlign.Start // ✅ متن وسط
                ),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent,
                    errorContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent,
                    errorIndicatorColor = Color.Transparent
                ),
            )
        }

        // ✅ منو هم‌عرض anchor (یعنی هم‌عرض دیالوگ) و زیر ردیف
        ExposedDropdownMenu(
            expanded = expanded,
            containerColor = MaterialTheme.colorScheme.surface,
            onDismissRequest = { expanded = false },
            modifier = Modifier.fillMaxWidth(0.8f)
        ) {
            items.forEach { (value, label) ->

                DropdownMenuItem(
                    leadingIcon = { Icon(scheduleModeIcon(value), contentDescription = null) },
                    text = { Text(label, maxLines = 1, overflow = TextOverflow.Ellipsis) },
                    onClick = {
                        onPick(value)
                        expanded = false
                    },
                )
            }
        }
    }
}

@Composable
private fun TimeRangeRow(
    start: LocalTime,
    end: LocalTime,
    onStartChange: (LocalTime) -> Unit,
    onEndChange: (LocalTime) -> Unit,
) {
    var showStartPicker by remember { mutableStateOf(false) }
    var showEndPicker by remember { mutableStateOf(false) }

    // وقتی Start تایید شد، این فلگ باعث میشه End بعدش باز بشه
    var openEndAfterStart by remember { mutableStateOf(false) }

    // یک مدت پیش‌فرض/آخرین مدت معتبر برای پیشنهاد End
    var lastDurationMinutes by remember { mutableIntStateOf(60) }

    Row(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 44.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        TimeChip(time = start) { showStartPicker = true }
        Text("-", style = MaterialTheme.typography.titleLarge)
        TimeChip(time = end) { showEndPicker = true }
    }

    if (showStartPicker) {
        TimePickerDialog(
            title = "Select start time",
            initial = start,
            onDismiss = {
                showStartPicker = false
                openEndAfterStart = false
            },
            onConfirm = { newStart ->
                // مدت فعلی اگر معتبر بود نگه دار، وگرنه lastDuration همان 60 بماند
                val duration = durationMinutesSameDay(start, end)
                if (duration > 0) lastDurationMinutes = duration

                // Start را ست کن
                onStartChange(newStart)

                // End پیشنهادی (برای نمایش اولیه در دیالوگ End)
                val suggestedEnd =
                    newStart.plusMinutesClamped(lastDurationMinutes).ensureAfter(newStart)
                onEndChange(suggestedEnd)

                // Start بسته، End خودکار باز
                showStartPicker = false
                openEndAfterStart = true
            }
        )
    }

    // ✅ نکته مهم: باز کردن دیالوگ End را با LaunchedEffect انجام بده
    // تا در یک فریم، اول Start بسته شود بعد End باز شود (بدون تداخل)
    LaunchedEffect(openEndAfterStart) {
        if (openEndAfterStart) {
            showEndPicker = true
            openEndAfterStart = false
        }
    }

    if (showEndPicker) {
        TimePickerDialog(
            title = "Select end time",
            initial = end,
            onDismiss = { showEndPicker = false },
            onConfirm = { newEnd ->
                val fixed = newEnd.ensureAfter(start)
                // ✅ اینجا “برعکس نه” رعایت میشه: فقط End تغییر می‌کند
                onEndChange(fixed)

                // اگر end > start شد، lastDuration را آپدیت کن تا دفعه بعد پیشنهاد بهتر باشد
                val d = durationMinutesSameDay(start, fixed)
                if (d > 0) lastDurationMinutes = d

                showEndPicker = false
            }
        )
    }
}

@Composable
private fun TimeChip(
    time: LocalTime,
    onClick: () -> Unit
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {

        TextButton(onClick = onClick) {
            Text(
                "%02d:%02d".format(time.hour, time.minute),
                style = MaterialTheme.typography.titleLarge
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TimePickerDialog(
    title: String,
    initial: LocalTime,
    onDismiss: () -> Unit,
    onConfirm: (LocalTime) -> Unit
) {
    val state = rememberTimePickerState(
        initialHour = initial.hour,
        initialMinute = initial.minute,
        is24Hour = true
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = { TimePicker(state = state) },
        confirmButton = {
            TextButton(onClick = {
                onConfirm(
                    LocalTime.of(
                        state.hour,
                        state.minute
                    )
                )
            }) { Text("OK") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}

@Composable
private fun AmountOfTimeRow(
    minutes: Int,
    onMinutesChange: (Int) -> Unit
) {
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    fun clampHour(h: Int) = h.coerceIn(0, 99)
    fun clampMin(m: Int) = m.coerceIn(0, 59)
    fun two(n: Int) = n.coerceIn(0, 99).toString().padStart(2, '0')

    fun hhFromTotal(total: Int) = clampHour(total / 60)
    fun mmFromTotal(total: Int) = clampMin(total % 60)

    fun emit(hh: Int, mm: Int) {
        onMinutesChange(clampHour(hh) * 60 + clampMin(mm))
    }

    fun digits2(s: String) = s.filter(Char::isDigit).take(2)
    fun parseOrZero(s: String) = s.toIntOrNull() ?: 0

    val hFocusRequester = remember { FocusRequester() }
    val mFocusRequester = remember { FocusRequester() }

    val hInteraction = remember { MutableInteractionSource() }
    val mInteraction = remember { MutableInteractionSource() }

    var hFocused by remember { mutableStateOf(false) }
    var mFocused by remember { mutableStateOf(false) }

    var hTf by remember {
        val t = two(hhFromTotal(minutes))
        mutableStateOf(TextFieldValue(t, selection = TextRange(t.length)))
    }
    var mTf by remember {
        val t = two(mmFromTotal(minutes))
        mutableStateOf(TextFieldValue(t, selection = TextRange(t.length)))
    }

    // ✅ هر بار لمس (Release) => فوکوس + select-all
    LaunchedEffect(hInteraction) {
        hInteraction.interactions.collectLatest { i: Interaction ->
            if (i is PressInteraction.Release) {
                hFocusRequester.requestFocus()
                yield()
                hTf = hTf.copy(selection = TextRange(0, hTf.text.length))
            }
        }
    }
    LaunchedEffect(mInteraction) {
        mInteraction.interactions.collectLatest { i: Interaction ->
            if (i is PressInteraction.Release) {
                mFocusRequester.requestFocus()
                yield()
                mTf = mTf.copy(selection = TextRange(0, mTf.text.length))
            }
        }
    }

    // ✅ sync از بیرون فقط وقتی فوکوس ندارند
    LaunchedEffect(minutes) {
        val hh = two(hhFromTotal(minutes))
        val mm = two(mmFromTotal(minutes))

        if (!hFocused && hTf.text != hh) hTf = hTf.copy(text = hh, selection = TextRange(hh.length))
        if (!mFocused && mTf.text != mm) mTf = mTf.copy(text = mm, selection = TextRange(mm.length))
    }

    val tfColors = TextFieldDefaults.colors(
        focusedContainerColor = Color.Transparent,
        unfocusedContainerColor = Color.Transparent,
        disabledContainerColor = Color.Transparent,
        errorContainerColor = Color.Transparent,
        focusedIndicatorColor = Color.Transparent,
        unfocusedIndicatorColor = Color.Transparent,
        disabledIndicatorColor = Color.Transparent,
        errorIndicatorColor = Color.Transparent
    )
    val centerStyle: TextStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Center)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {

        // --- Hours ---
        TextField(
            value = hTf,
            onValueChange = { v ->
                // وقتی فوکوس دارد: raw (۱ یا ۲ رقم) بدون pad
                val raw = digits2(v.text)
                val hh = clampHour(parseOrZero(raw))
                hTf = v.copy(
                    text = raw,
                    selection = TextRange(raw.length)
                )

                val mm = clampMin(parseOrZero(digits2(mTf.text)))
                emit(hh, mm)
            },
            singleLine = true,
            interactionSource = hInteraction,
            modifier = Modifier
                .width(64.dp)
                .focusRequester(hFocusRequester)
                .onFocusChanged { st ->
                    hFocused = st.isFocused
                    if (!st.isFocused) {
                        // روی blur: نرمال‌سازی به ۲ رقمی
                        val hh = clampHour(parseOrZero(digits2(hTf.text)))
                        val mm = clampMin(parseOrZero(digits2(mTf.text)))
                        val t = two(hh)
                        hTf = hTf.copy(text = t, selection = TextRange(t.length))
                        emit(hh, mm)
                    }
                },
            textStyle = centerStyle,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Next
            ),
            colors = tfColors
        )

        Text(" : ", style = MaterialTheme.typography.titleMedium)

        // --- Minutes ---
        TextField(
            value = mTf,
            onValueChange = { v ->
                val raw = digits2(v.text)
                val mm = clampMin(parseOrZero(raw))
                mTf = v.copy(
                    text = raw,
                    selection = TextRange(raw.length)
                )

                val hh = clampHour(parseOrZero(digits2(hTf.text)))
                emit(hh, mm)
            },
            singleLine = true,
            interactionSource = mInteraction,
            modifier = Modifier
                .width(64.dp)
                .focusRequester(mFocusRequester)
                .onFocusChanged { st ->
                    mFocused = st.isFocused
                    if (!st.isFocused) {
                        val hh = clampHour(parseOrZero(digits2(hTf.text)))
                        val mm = clampMin(parseOrZero(digits2(mTf.text)))
                        val t = two(mm)
                        mTf = mTf.copy(text = t, selection = TextRange(t.length))
                        emit(hh, mm)
                    }
                },
            textStyle = centerStyle,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    keyboardController?.hide()
                    focusManager.clearFocus()
                }
            ),
            colors = tfColors
        )
    }
}

@Composable
private fun PomodoroConfigRow(
    focus: Int,
    shortBreak: Int,
    longBreak: Int,
    longBreakEvery: Int,
    unitsPerDay: Int,
    onFocusChange: (Int) -> Unit,
    onShortBreakChange: (Int) -> Unit,
    onLongBreakChange: (Int) -> Unit,
    onLongBreakEveryChange: (Int) -> Unit,
    onUnitsPerDayChange: (Int) -> Unit,
) {
    Column(
        Modifier
            .fillMaxWidth()
            .padding(start = 56.dp, end = 16.dp)
    ) {

        PomodoroIntFieldRow(
            label = "Focus (min)",
            value = focus,
            min = 1,
            max = 240,
            imeAction = ImeAction.Next,
            onValueChange = onFocusChange
        )

        PomodoroIntFieldRow(
            label = "Short break (min)",
            value = shortBreak,
            min = 0,
            max = 60,
            imeAction = ImeAction.Next,
            onValueChange = onShortBreakChange
        )

        PomodoroIntFieldRow(
            label = "Long break (min)",
            value = longBreak,
            min = 0,
            max = 120,
            imeAction = ImeAction.Next,
            onValueChange = onLongBreakChange
        )

        PomodoroIntFieldRow(
            label = "Long break every",
            value = longBreakEvery,
            min = 2,
            max = 12,
            imeAction = ImeAction.Next,
            onValueChange = onLongBreakEveryChange
        )

        PomodoroIntFieldRow(
            label = "Pomodoros per day",
            value = unitsPerDay,
            min = 1,
            max = 20,
            imeAction = ImeAction.Done,
            onValueChange = onUnitsPerDayChange
        )
    }
}

@Composable
private fun PomodoroIntFieldRow(
    label: String,
    value: Int,
    min: Int,
    max: Int,
    imeAction: ImeAction,
    onValueChange: (Int) -> Unit,
) {
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    fun digitsOnly(s: String, maxLen: Int = 4): String =
        s.filter(Char::isDigit).take(maxLen)

    fun parseOrZero(s: String) = s.toIntOrNull() ?: 0
    fun clamp(n: Int) = n.coerceIn(min, max)

    val fr = remember { FocusRequester() }
    val interaction = remember { MutableInteractionSource() }
    var focused by remember { mutableStateOf(false) }

    var tf by remember {
        val t = value.coerceIn(min, max).toString()
        mutableStateOf(TextFieldValue(t, selection = TextRange(t.length)))
    }

    // ✅ هر بار لمس (Release) => فوکوس + select-all
    LaunchedEffect(interaction) {
        interaction.interactions.collectLatest { i ->
            if (i is PressInteraction.Release) {
                fr.requestFocus()
                yield()
                tf = tf.copy(selection = TextRange(0, tf.text.length))
            }
        }
    }

    // ✅ sync از بیرون فقط وقتی فوکوس ندارد
    LaunchedEffect(value) {
        val t = value.coerceIn(min, max).toString()
        if (!focused && tf.text != t) {
            tf = tf.copy(text = t, selection = TextRange(t.length))
        }
    }

    val tfColors = TextFieldDefaults.colors(
        focusedContainerColor = Color.Transparent,
        unfocusedContainerColor = Color.Transparent,
        disabledContainerColor = Color.Transparent,
        errorContainerColor = Color.Transparent,
        focusedIndicatorColor = Color.Transparent,
        unfocusedIndicatorColor = Color.Transparent,
        disabledIndicatorColor = Color.Transparent,
        errorIndicatorColor = Color.Transparent
    )

    Row(
        modifier = Modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            modifier = Modifier.weight(1f)
        )

        TextField(
            value = tf,
            onValueChange = { v ->
                val raw = digitsOnly(v.text)
                tf = v.copy(text = raw, selection = TextRange(raw.length))

                // حین تایپ: اگر خالی شد، فعلاً 0 می‌گیریم، ولی clamp می‌کنیم
                val n = clamp(parseOrZero(raw))
                onValueChange(n)
            },
            singleLine = true,
            interactionSource = interaction,
            modifier = Modifier
                .width(110.dp)
                .focusRequester(fr)
                .onFocusChanged { st ->
                    focused = st.isFocused
                    if (!st.isFocused) {
                        // روی blur: نرمال‌سازی نهایی
                        val raw = digitsOnly(tf.text)
                        val n = clamp(parseOrZero(raw))
                        val t = n.toString()
                        tf = tf.copy(text = t, selection = TextRange(t.length))
                        onValueChange(n)
                    }
                },
            textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Center),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = imeAction
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    keyboardController?.hide()
                    focusManager.clearFocus()
                }
            ),
            colors = tfColors
        )
    }
}

@Composable
private fun JalaliDateRow(
    selectedDate: LocalDate,
    onChangeDate: (LocalDate) -> Unit
) {
    val openDialog = remember { mutableStateOf(false) }

    val jalali = remember(selectedDate) { selectedDate.toJalali() }
    val dateText = remember(selectedDate) { jalali.toFaText() }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { openDialog.value = true }
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(Icons.Filled.CalendarMonth, contentDescription = null)
        Spacer(Modifier.width(12.dp))

        Text(
            text = dateText,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.weight(1f)
        )

        IconButton(onClick = { onChangeDate(LocalDate.now()) }) {
            Icon(Icons.Filled.Today, contentDescription = "today")
        }
    }


    // دیالوگ تقویم شمسی
    JalaliDatePickerDialog(
        openDialog = openDialog,
        initialDate = jalali,

        onConfirm = { picked: JalaliCalendar ->
            onChangeDate(picked.toLocalDate())
        },

        // محدودیت تاریخ نداشته باشیم
        disableBeforeDate = null,
        disableAfterDate = null,

        onSelectDay = { /* لازم نیست کاری کنیم */ },

        // رنگ‌ها از تم فعلی
        backgroundColor = Color(0xFFF8F9FB),         // خیلی روشن، نرم و مدرن

        textColor = Color(0xFF1C1C1E),                // مشکی نرم (نه کاملاً سیاه)
        textDisabledColor = Color(0xFFB0B3B8),       // خاکستری ملایم

        selectedIconColor = Color(0xFF00B894),        // سبز فیروزه‌ای زنده
        textColorHighlight = Color(0xFF009E84),       // سبز کمی تیره‌تر برای انتخاب

        dropDownColor = Color(0xFF1C1C1E),
        dayOfWeekLabelColor = Color(0xFF6B7280),      // خاکستری متوسط برای شنبه/یکشنبه...

        confirmBtnColor = Color(0xFF00B894),          // هم‌رنگ انتخاب
        cancelBtnColor = Color(0xFF9CA3AF),           // خاکستری خنثی
        todayBtnColor = Color(0xFF1C1C1E),
        nextPreviousBtnColor = Color(0xFF00B894),    // فلش‌های ماه

        fontFamily = FontFamily.Default,   // پیش‌فرض سیستم
        fontSize = 18.sp     // پیش‌فرض کتابخانه
    )

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RepeatEveryRow(
    interval: Int,
    unit: RepeatUnit,
    onIntervalChange: (Int) -> Unit,
    onUnitChange: (RepeatUnit) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    val unitLabel = when (unit) {
        RepeatUnit.DAY -> "days"
        RepeatUnit.WEEK -> "weeks"
        RepeatUnit.MONTH -> "months"
        RepeatUnit.YEAR -> "years"
    }

    // ✅ کنترل selection با TextFieldValue
    var intervalTf by remember(interval) {
        mutableStateOf(
            TextFieldValue(
                text = interval.toString(),
                selection = TextRange(interval.toString().length) // کرسر آخر
            )
        )
    }

    // ✅ اگر interval از بیرون تغییر کرد، متن فیلد هم sync شود
    LaunchedEffect(interval) {
        val t = interval.coerceIn(1, 99).toString()
        if (intervalTf.text != t) {
            intervalTf = intervalTf.copy(text = t, selection = TextRange(0, t.length))
        }
    }

    Row(
        Modifier
            .fillMaxWidth()
            .padding(start = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Text("Repeat every")
        Spacer(Modifier.width(10.dp))

        TextField(
            value = intervalTf,
            onValueChange = { v ->
                val digits = v.text.filter(Char::isDigit).take(2)
                val n = digits.toIntOrNull() ?: 0
                val clamped = n.coerceIn(0, 99)

                // هم interval بیرونی رو آپدیت کن
                onIntervalChange(clamped)

                // متن داخلی رو هم sync کن (و selection رو آخر بذار)
                val newText = clamped.toString()
                intervalTf = v.copy(
                    text = newText,
                    selection = TextRange(newText.length)
                )
            },
            singleLine = true,
            modifier = Modifier
                .width(54.dp)
                .onFocusChanged { state ->
                    if (state.isFocused) {
                        // ✅ وقتی فوکوس گرفت، کل متن انتخاب شود
                        intervalTf = intervalTf.copy(
                            selection = TextRange(0, intervalTf.text.length)
                        )
                    }
                },
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                disabledContainerColor = Color.Transparent,
                errorContainerColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent,
                errorIndicatorColor = Color.Transparent
            ),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    keyboardController?.hide()
                    focusManager.clearFocus()
                }
            )
        )

        Spacer(Modifier.width(4.dp))

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded },
            modifier = Modifier.weight(1f)
        ) {
            TextField(
                value = unitLabel,
                onValueChange = {},
                readOnly = true,
                singleLine = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent,
                    errorContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent,
                    errorIndicatorColor = Color.Transparent
                ),
            )

            ExposedDropdownMenu(
                expanded = expanded,
                containerColor = MaterialTheme.colorScheme.surface,
                onDismissRequest = { expanded = false },
                modifier = Modifier.fillMaxWidth(0.8f)
            ) {
                DropdownMenuItem(
                    text = { Text("days") },
                    onClick = { onUnitChange(RepeatUnit.DAY); expanded = false })
                DropdownMenuItem(
                    text = { Text("weeks") },
                    onClick = { onUnitChange(RepeatUnit.WEEK); expanded = false })
                DropdownMenuItem(
                    text = { Text("months") },
                    onClick = { onUnitChange(RepeatUnit.MONTH); expanded = false })
                DropdownMenuItem(
                    text = { Text("years") },
                    onClick = { onUnitChange(RepeatUnit.YEAR); expanded = false })
            }
        }
    }
}

@Composable
private fun WeekdayPickerRow(
    selectedMask: Int,
    onChangeMask: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    // Sa Su Mo Tu We Th Fr => بیت‌های 0..6
    val days = listOf(
        0 to "Sa", 1 to "Su", 2 to "Mo", 3 to "Tu",
        4 to "We", 5 to "Th", 6 to "Fr",
    )

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .padding(top = 6.dp, bottom = 10.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically
    ) {
        days.forEach { (bit, label) ->
            val selected = (selectedMask and (1 shl bit)) != 0

            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(
                        color = if (selected) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.surfaceVariant,
                        shape = CircleShape
                    )
                    .border(
                        1.dp,
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f),
                        CircleShape
                    )
                    .clickable {
                        val newMask =
                            if (selected) selectedMask and (1 shl bit).inv()
                            else selectedMask or (1 shl bit)
                        onChangeMask(newMask)
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelMedium,
                    color = if (selected) MaterialTheme.colorScheme.onPrimary
                    else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun ReminderRow(
    ui: TaskReminderUi,
    onClick: () -> Unit,
    onRequestDelete: () -> Unit
) {
    val entity = ui.entity

    AddEditeDialogRow(
        onClick = onClick,
        content = {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Filled.Notifications,
                    contentDescription = null
                )

                Column(
                    modifier = Modifier
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                        .weight(1f)
                ) {
                    Text(
                        text = entity.buildSummary(),
                        style = MaterialTheme.typography.titleMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    // optional: strength small subtitle
                    Text(
                        text = entity.strength.name.lowercase().replace("_", " "),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // مود آیکون
                Icon(
                    imageVector = reminderModeIcon(entity.mode),
                    contentDescription = null,
                    modifier = Modifier.padding(end = 8.dp)
                )

                ReminderOptionsMenu(
                    onDelete = onRequestDelete
                )
            }
        },
        startPadding = 0
    )
}

@Composable
private fun ReminderOptionsMenu(
    onDelete: () -> Unit
) {
    var open by remember { mutableStateOf(false) }

    Box {
        IconButton(onClick = { open = true }) {
            Icon(Icons.Filled.MoreVert, contentDescription = "schedule menu")
        }
        DropdownMenu(expanded = open, onDismissRequest = { open = false }) {
            DropdownMenuItem(
                text = { Text("Delete") },
                leadingIcon = { Icon(Icons.Filled.Delete, contentDescription = null) },
                onClick = { open = false; onDelete() }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ModeReminderDropdownRow(
    mode: ReminderMode,
    allowedModes: List<ReminderMode>,
    onPick: (ReminderMode) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    val selectedLabel = when (mode) {
        ReminderMode.ALLOCATED -> "Allocated"
        ReminderMode.FIXED_TIME -> "Fixed time"
        ReminderMode.POMODORO_REMINDER -> "Pomodoro Reminder"
    }


    val items = allowedModes.map { m ->
        m to when (m) {
            ReminderMode.ALLOCATED -> "Allocated"
            ReminderMode.FIXED_TIME -> "Fixed time"
            ReminderMode.POMODORO_REMINDER -> "Pomodoro Reminder"
        }
    }


    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = Modifier.fillMaxWidth()
    ) {
        // ✅ anchor = کل عرض دیالوگ
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(MenuAnchorType.PrimaryEditable), // یا Primary
            contentAlignment = Alignment.Center
        ) {
            TextField(
                value = selectedLabel,
                onValueChange = {},
                readOnly = true,
                singleLine = true,
                leadingIcon = { Icon(reminderModeIcon(mode), contentDescription = null) },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier.fillMaxWidth(), // ✅ کوچیک‌تر و وسط
                textStyle = MaterialTheme.typography.bodyLarge.copy(
                    textAlign = TextAlign.Start // ✅ متن وسط
                ),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent,
                    errorContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent,
                    errorIndicatorColor = Color.Transparent
                ),
            )
        }

        // ✅ منو هم‌عرض anchor (یعنی هم‌عرض دیالوگ) و زیر ردیف
        ExposedDropdownMenu(
            expanded = expanded,
            containerColor = MaterialTheme.colorScheme.surface,
            onDismissRequest = { expanded = false },
            modifier = Modifier.fillMaxWidth(0.8f)
        ) {
            items.forEach { (value, label) ->

                DropdownMenuItem(
                    leadingIcon = { Icon(reminderModeIcon(value), contentDescription = null) },
                    text = { Text(label, maxLines = 1, overflow = TextOverflow.Ellipsis) },
                    onClick = {
                        onPick(value)
                        expanded = false
                    },
                )
            }
        }
    }
}

@Composable
private fun AllocatedRows(
    draft: ReminderDraft,
    onOffsetDaysChange: (Int) -> Unit,
    onOffsetHoursChange: (Int) -> Unit,
    onOffsetMinutesChange: (Int) -> Unit,
    onBeforeAfterChange: (BeforeAfter) -> Unit,
    onAnchorChange: (StartEnd) -> Unit
) {
    // row 1: 0d : 0h : 0m
    AddEditeDialogRow(
        onClick = null,
        content = {
            Row(
                Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                SmallNumberField(
                    value = draft.offsetDays,
                    suffix = "d",
                    maxDigits = 2,
                    onChange = { onOffsetDaysChange(it.coerceIn(0, 99)) }
                )

                Spacer(Modifier.width(6.dp))
                Text(":", style = MaterialTheme.typography.titleLarge)
                Spacer(Modifier.width(6.dp))

                SmallNumberField(
                    value = draft.offsetHours,
                    suffix = "h",
                    maxDigits = 2,
                    onChange = { onOffsetHoursChange(it.coerceIn(0, 23)) }
                )

                Spacer(Modifier.width(6.dp))
                Text(":", style = MaterialTheme.typography.titleLarge)
                Spacer(Modifier.width(6.dp))

                SmallNumberField(
                    value = draft.offsetMinutes,
                    suffix = "m",
                    maxDigits = 2,
                    onChange = { onOffsetMinutesChange(it.coerceIn(0, 59)) }
                )
            }

        },
        startPadding = 0,
        showDivider = false
    )

    // row 2: before/after + start/end
    AddEditeDialogRow(
        onClick = null,
        content = {

            Row(
                Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Absolute.Center
            ) {

                BeforeAfterDropdown(
                    value = draft.beforeAfter,
                    onPick = onBeforeAfterChange,
                    modifier = Modifier
                )

                Spacer(Modifier.width(12.dp))

                StartEndDropdown(
                    value = draft.anchor,
                    onPick = onAnchorChange,
                    modifier = Modifier
                )

            }
        },
        startPadding = 0,
//        showDivider = false
    )
}

@Composable
private fun PomodoroReminderRows(
    draft: ReminderDraft,
    onStartFocusToggle: (Boolean) -> Unit,
    onStartBreakToggle: (Boolean) -> Unit,
    onEndBreakToggle: (Boolean) -> Unit,
) {

    //on Start focus
    AddEditeDialogRow(
        onClick = null,
        content = {
            val onStartFocus = draft.onStartFocus
            Row(
                Modifier
                    .fillMaxWidth()
                    .clickable { onStartFocusToggle(!onStartFocus) }
                    .padding(start = 86.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {

                Icon(
                    imageVector = if (onStartFocus) Icons.Filled.CheckCircle else Icons.Filled.RadioButtonUnchecked,
                    contentDescription = null,
                    tint = if (onStartFocus) Color(0xFF2E7D32) else Color.Gray
                )
                Spacer(Modifier.width(8.dp))
                Text("on Start focus")

            }

        },
        startPadding = 0,
        showDivider = false
    )

    //on Start break
    AddEditeDialogRow(
        onClick = null,
        content = {
            val onStartBreak = draft.onStartBreak
            Row(
                Modifier
                    .fillMaxWidth()
                    .clickable { onStartBreakToggle(!onStartBreak) }
                    .padding(start = 86.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {

                Icon(
                    imageVector = if (onStartBreak) Icons.Filled.CheckCircle else Icons.Filled.RadioButtonUnchecked,
                    contentDescription = null,
                    tint = if (onStartBreak) Color(0xFF2E7D32) else Color.Gray
                )
                Spacer(Modifier.width(8.dp))
                Text("on Start break")

            }

        },
        startPadding = 0,
        showDivider = false
    )

    //on End break
    AddEditeDialogRow(
        onClick = null,
        content = {
            val onEndBreak = draft.onEndBreak
            Row(
                Modifier
                    .fillMaxWidth()
                    .clickable { onEndBreakToggle(!onEndBreak) }
                    .padding(start = 86.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {

                Icon(
                    imageVector = if (onEndBreak) Icons.Filled.CheckCircle else Icons.Filled.RadioButtonUnchecked,
                    contentDescription = null,
                    tint = if (onEndBreak) Color(0xFF2E7D32) else Color.Gray
                )
                Spacer(Modifier.width(8.dp))
                Text("on End break")

            }

        },
        startPadding = 0,
        showDivider = true
    )

}

@Composable
private fun SmallNumberField(
    value: Int,
    suffix: String,
    maxDigits: Int,
    onChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    var tf by remember(value) {
        mutableStateOf(
            TextFieldValue(
                text = value.toString(),
                selection = TextRange(value.toString().length)
            )
        )
    }

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        TextField(
            value = tf,
            onValueChange = { v ->
                val digits = v.text.filter(Char::isDigit).take(maxDigits)
                val n = digits.toIntOrNull() ?: 0
                onChange(n)

                val text = n.toString()
                tf = v.copy(text = text, selection = TextRange(text.length))
            },
            singleLine = true,
            modifier = Modifier.width(54.dp), // کوچکتر
            placeholder = { Text("0") },      // فقط 0
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                disabledContainerColor = Color.Transparent,
                errorContainerColor = Color.Transparent,
            ),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(onDone = {})
        )

        Spacer(Modifier.width(2.dp)) // فاصله خیلی کم
        Text(
            text = suffix,
            style = MaterialTheme.typography.titleMedium
        )
    }
}

@Composable
private fun FixedTimeRow(
    time: LocalTime,
    onTimeChange: (LocalTime) -> Unit
) {
    AddEditeDialogRow(
        onClick = null,
        content = {
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                var showPicker by remember { mutableStateOf(false) }
                TimeChip(time = time) {
                    showPicker = true
                }

                if (showPicker) {
                    TimePickerDialog(
                        title = "Select reminder time",
                        initial = time,
                        onDismiss = { showPicker = false },
                        onConfirm = { t -> onTimeChange(t); showPicker = false }
                    )
                }
            }
        },
        startPadding = 0,
//        showDivider = false
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BeforeAfterDropdown(
    value: BeforeAfter,
    onPick: (BeforeAfter) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    val label = when (value) {
        BeforeAfter.BEFORE -> "before"
        BeforeAfter.AFTER -> "after"
    }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = modifier
    ) {
        TextField(
            value = label,
            onValueChange = {},
            readOnly = true,
            singleLine = true,
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
            modifier = Modifier
                .width(118.dp)
                .menuAnchor(MenuAnchorType.PrimaryNotEditable),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            ),
        )
        ExposedDropdownMenu(
            expanded = expanded,
            containerColor = MaterialTheme.colorScheme.surface,
            onDismissRequest = { expanded = false }
        ) {
            DropdownMenuItem(
                text = { Text("before") },
                onClick = { onPick(BeforeAfter.BEFORE); expanded = false })
            DropdownMenuItem(
                text = { Text("after") },
                onClick = { onPick(BeforeAfter.AFTER); expanded = false })
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun StartEndDropdown(
    value: StartEnd,
    onPick: (StartEnd) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    val label = when (value) {
        StartEnd.START -> "start"
        StartEnd.END -> "end"
    }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = modifier
    ) {
        TextField(
            value = label,
            onValueChange = {},
            readOnly = true,
            singleLine = true,
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
            modifier = Modifier
                .width(118.dp)
                .menuAnchor(MenuAnchorType.PrimaryNotEditable),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            ),
        )
        ExposedDropdownMenu(
            expanded = expanded,
            containerColor = MaterialTheme.colorScheme.surface,
            onDismissRequest = { expanded = false }
        ) {
            DropdownMenuItem(
                text = { Text("start") },
                onClick = { onPick(StartEnd.START); expanded = false })
            DropdownMenuItem(
                text = { Text("end") },
                onClick = { onPick(StartEnd.END); expanded = false })
        }
    }
}


@Composable
private fun StrengthRow(
    strength: ReminderStrengthMode,
    onStrengthChange: (ReminderStrengthMode) -> Unit
) {
    // 3-state stepbar (0..2)
    val idx = when (strength) {
        ReminderStrengthMode.NOTIFICATION -> 0
        ReminderStrengthMode.ALARM -> 1
        ReminderStrengthMode.ALARM_AND_CAPTCHA -> 2
    }
    val state = when (strength) {
        ReminderStrengthMode.NOTIFICATION -> "Notification"
        ReminderStrengthMode.ALARM -> "Alarm"
        ReminderStrengthMode.ALARM_AND_CAPTCHA -> "Alarm+Captcha"
    }

    AddEditeDialogRow(
        onClick = null,
        content = {
            Icon(Icons.Filled.Warning, contentDescription = null)
            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp)
            ) {
                Text("Strength ($state)", style = MaterialTheme.typography.titleMedium)

                Spacer(Modifier.height(10.dp))

                // slider with steps = 1? نه، برای 3 حالت steps=1 نیست. در compose: steps = numberOfDiscreteValues-2 => 1
                // اما چون ما 3 مقدار داریم: steps = 1 (0,1,2)
                var sliderPos by remember(idx) { mutableFloatStateOf(idx.toFloat()) }

                androidx.compose.material3.Slider(
                    value = sliderPos,
                    onValueChange = { sliderPos = it },
                    onValueChangeFinished = {
                        val rounded = sliderPos.roundToInt().coerceIn(0, 2)
                        val newStrength = when (rounded) {
                            0 -> ReminderStrengthMode.NOTIFICATION
                            1 -> ReminderStrengthMode.ALARM
                            else -> ReminderStrengthMode.ALARM_AND_CAPTCHA
                        }
                        onStrengthChange(newStrength)
                    },
                    valueRange = 0f..2f,
                    steps = 1
                )

            }
        },
    )
}

@Composable
private fun SoundSettingsRow(
    strength: ReminderStrengthMode,
    alarmSoundUri: String?,
    onClickOpenSettings: () -> Unit
) {
    val title = when (strength) {
        ReminderStrengthMode.NOTIFICATION -> "Notification settings"
        ReminderStrengthMode.ALARM,
        ReminderStrengthMode.ALARM_AND_CAPTCHA -> "Sound & notification settings"
    }

    val subtitle = when (strength) {
        ReminderStrengthMode.NOTIFICATION -> "Manage sound/vibration in system settings"
        ReminderStrengthMode.ALARM -> "Pick alarm sound in system/app settings"
        ReminderStrengthMode.ALARM_AND_CAPTCHA -> "Pick alarm sound in settings (captcha enabled)"
    }

    AddEditeDialogRow(
        onClick = onClickOpenSettings,
        content = {
            Icon(Icons.Filled.Notifications, contentDescription = null)
            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Text(title, style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                if (alarmSoundUri != null && strength != ReminderStrengthMode.NOTIFICATION) {
                    Spacer(Modifier.height(6.dp))
                    Text(
                        text = "Current: $alarmSoundUri",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
            Icon(Icons.Filled.ArrowForwardIos, contentDescription = null)
        },
    )
}


//>>>>>>>>>>>>>>>> Utils <<<<<<<<<<<<<<<<<<

private fun TaskReminderEntity.buildSummary(): String {
    return when (mode) {

        ReminderMode.ALLOCATED -> {
            val parts = buildList {
                if (offsetDays > 0) add("${offsetDays}d")
                if (offsetHours > 0) add("${offsetHours}h")
                if (offsetMinutes > 0) add("${offsetMinutes}m")
            }

            val timePart = if (parts.isEmpty()) "0m" else parts.joinToString(" ")

            val beforeAfterText = when (beforeAfter) {
                BeforeAfter.BEFORE -> "before"
                BeforeAfter.AFTER -> "after"
            }

            val anchorText = when (anchor) {
                StartEnd.START -> "start"
                StartEnd.END -> "end"
            }

            "$timePart $beforeAfterText $anchorText"
        }

        ReminderMode.FIXED_TIME -> {
            val minute = fixedMinuteOfDay ?: return ""
            val h = minute / 60
            val m = minute % 60
            "%02d:%02d".format(h, m)
        }

        ReminderMode.POMODORO_REMINDER -> "Pomodoro Reminder"

    }
}

@Composable
private fun ChildRuleEditorDialog(
    child: TaskEntity,
    rule: TaskChildRuleEntity?,
    onDismiss: () -> Unit,
    onConfirm: (
        ruleType: String,
        timesPerDay: Int,
        timesPerOccurrence: Int,
        g5TargetCount: Int
    ) -> Unit
) {
    var selectedRuleType by remember(child.id, rule?.id) {
        mutableStateOf(
            rule?.ruleType ?: TaskChildRuleType.ONCE_PER_PARENT_OCCURRENCE
        )
    }

    var timesPerDayText by remember(child.id, rule?.id) {
        mutableStateOf(
            (rule?.timesPerDay ?: 1).coerceAtLeast(1).toString()
        )
    }

    var timesPerOccurrenceText by remember(child.id, rule?.id) {
        mutableStateOf(
            (rule?.timesPerOccurrence ?: 1).coerceAtLeast(1).toString()
        )
    }

    var g5TargetCountText by remember(child.id, rule?.id) {
        mutableStateOf(
            (rule?.g5TargetCount ?: 5).coerceAtLeast(1).toString()
        )
    }

    fun cleanPositiveInt(
        text: String,
        fallback: Int
    ): Int {
        return text
            .trim()
            .toIntOrNull()
            ?.coerceAtLeast(1)
            ?: fallback
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Child rule")
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(520.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = child.name,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(Modifier.height(12.dp))

                childRuleTypeOptions.forEach { option ->
                    ChildRuleTypeOptionRow(
                        option = option,
                        selected = selectedRuleType == option.ruleType,
                        onClick = {
                            selectedRuleType = option.ruleType
                        }
                    )

                    Spacer(Modifier.height(4.dp))
                }

                Spacer(Modifier.height(12.dp))

                when (selectedRuleType) {
                    TaskChildRuleType.N_TIMES_PER_DAY -> {
                        ChildRuleNumberField(
                            label = "Times per day",
                            value = timesPerDayText,
                            onValueChange = { timesPerDayText = it }
                        )
                    }

                    TaskChildRuleType.N_TIMES_PER_PARENT_OCCURRENCE -> {
                        ChildRuleNumberField(
                            label = "Times per parent occurrence",
                            value = timesPerOccurrenceText,
                            onValueChange = { timesPerOccurrenceText = it }
                        )
                    }

                    TaskChildRuleType.G5_LEARNING -> {
                        ChildRuleNumberField(
                            label = "G5 step count",
                            value = g5TargetCountText,
                            onValueChange = { g5TargetCountText = it }
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirm(
                        selectedRuleType,
                        cleanPositiveInt(timesPerDayText, rule?.timesPerDay ?: 1),
                        cleanPositiveInt(timesPerOccurrenceText, rule?.timesPerOccurrence ?: 1),
                        cleanPositiveInt(g5TargetCountText, rule?.g5TargetCount ?: 5)
                    )
                }
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss
            ) {
                Text("Cancel")
            }
        }
    )
}

private fun newChildRulePreviewEntity(
    ruleType: String,
    timesPerDay: Int,
    timesPerOccurrence: Int,
    g5TargetCount: Int
): TaskChildRuleEntity {
    return TaskChildRuleEntity(
        parentTaskId = 0,
        childTaskId = 0,
        ruleType = ruleType,
        resetScope = resetScopeForRuleTypeUi(ruleType),
        targetCount = when (ruleType) {
            TaskChildRuleType.N_TIMES_PER_DAY -> timesPerDay.coerceAtLeast(1)
            TaskChildRuleType.N_TIMES_PER_PARENT_OCCURRENCE -> timesPerOccurrence.coerceAtLeast(1)
            TaskChildRuleType.G5_LEARNING -> g5TargetCount.coerceAtLeast(1)
            else -> 1
        },
        timesPerDay = timesPerDay.coerceAtLeast(1),
        timesPerOccurrence = timesPerOccurrence.coerceAtLeast(1),
        g5TargetCount = g5TargetCount.coerceAtLeast(1),
        showInTimelineCard = true,
        showInBottomSheet = true
    )
}

private fun resetScopeForRuleTypeUi(ruleType: String): String {
    return when (ruleType) {
        TaskChildRuleType.ONCE_PER_PARENT_LIFETIME ->
            TaskChildResetScope.PARENT_LIFETIME

        TaskChildRuleType.ONCE_PER_DAY,
        TaskChildRuleType.N_TIMES_PER_DAY ->
            TaskChildResetScope.DAY

        TaskChildRuleType.ONCE_PER_PARENT_OCCURRENCE,
        TaskChildRuleType.N_TIMES_PER_PARENT_OCCURRENCE ->
            TaskChildResetScope.PARENT_OCCURRENCE

        TaskChildRuleType.G5_LEARNING ->
            TaskChildResetScope.LEARNING_CYCLE

        TaskChildRuleType.MANUAL_LIST_ITEM ->
            TaskChildResetScope.LIST_SESSION

        TaskChildRuleType.MANUAL_RESET ->
            TaskChildResetScope.MANUAL

        else ->
            TaskChildResetScope.PARENT_OCCURRENCE
    }
}

private fun openAppNotificationSettings(context: Context) {
    val intent = Intent().apply {
        // Android 8+
        action = Settings.ACTION_APP_NOTIFICATION_SETTINGS
        putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }
    runCatching { context.startActivity(intent) }
        .recoverCatching { openAppDetailsSettings(context) }
}

private fun openAppDetailsSettings(context: Context) {
    val intent = Intent(
        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
        Uri.fromParts("package", context.packageName, null)
    ).apply {
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }
    runCatching { context.startActivity(intent) }
}

private fun childRuleSummaryText(
    rule: TaskChildRuleEntity?
): String {
    if (rule == null) {
        return "Rule: every parent occurrence"
    }

    return when (rule.ruleType) {
        TaskChildRuleType.ONCE_PER_PARENT_OCCURRENCE ->
            "Rule: every parent occurrence"

        TaskChildRuleType.N_TIMES_PER_PARENT_OCCURRENCE ->
            "Rule: ${rule.timesPerOccurrence.coerceAtLeast(1)} times per parent occurrence"

        TaskChildRuleType.ONCE_PER_DAY ->
            "Rule: once per day"

        TaskChildRuleType.N_TIMES_PER_DAY ->
            "Rule: ${rule.timesPerDay.coerceAtLeast(1)} times per day"

        TaskChildRuleType.ONCE_PER_PARENT_LIFETIME ->
            "Rule: once in parent lifetime"

        TaskChildRuleType.G5_LEARNING ->
            "Rule: G5 learning ${rule.g5TargetCount.coerceAtLeast(1)} steps"

        TaskChildRuleType.MANUAL_LIST_ITEM ->
            "Rule: manual list item"

        TaskChildRuleType.MANUAL_RESET ->
            "Rule: manual reset"

        else ->
            "Rule: ${rule.ruleType}"
    }
}

@Composable
private fun ChildRuleEditorDialog(
    title: String,
    rule: TaskChildRuleEntity?,
    onDismiss: () -> Unit,
    onConfirm: (
        ruleType: String,
        timesPerDay: Int,
        timesPerOccurrence: Int,
        g5TargetCount: Int
    ) -> Unit
) {
    var selectedRuleType by remember(rule?.id) {
        mutableStateOf(
            rule?.ruleType ?: TaskChildRuleType.ONCE_PER_PARENT_OCCURRENCE
        )
    }

    var timesPerDayText by remember(rule?.id) {
        mutableStateOf(
            (rule?.timesPerDay ?: 1).coerceAtLeast(1).toString()
        )
    }

    var timesPerOccurrenceText by remember(rule?.id) {
        mutableStateOf(
            (rule?.timesPerOccurrence ?: 1).coerceAtLeast(1).toString()
        )
    }

    var g5TargetCountText by remember(rule?.id) {
        mutableStateOf(
            (rule?.g5TargetCount ?: 5).coerceAtLeast(1).toString()
        )
    }

    fun cleanPositiveInt(
        text: String,
        fallback: Int
    ): Int {
        return text
            .trim()
            .toIntOrNull()
            ?.coerceAtLeast(1)
            ?: fallback
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Child rule")
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(520.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(Modifier.height(12.dp))

                childRuleTypeOptions.forEach { option ->
                    ChildRuleTypeOptionRow(
                        option = option,
                        selected = selectedRuleType == option.ruleType,
                        onClick = {
                            selectedRuleType = option.ruleType
                        }
                    )

                    Spacer(Modifier.height(4.dp))
                }

                Spacer(Modifier.height(12.dp))

                when (selectedRuleType) {
                    TaskChildRuleType.N_TIMES_PER_DAY -> {
                        ChildRuleNumberField(
                            label = "Times per day",
                            value = timesPerDayText,
                            onValueChange = { timesPerDayText = it }
                        )
                    }

                    TaskChildRuleType.N_TIMES_PER_PARENT_OCCURRENCE -> {
                        ChildRuleNumberField(
                            label = "Times per parent occurrence",
                            value = timesPerOccurrenceText,
                            onValueChange = { timesPerOccurrenceText = it }
                        )
                    }

                    TaskChildRuleType.G5_LEARNING -> {
                        ChildRuleNumberField(
                            label = "G5 step count",
                            value = g5TargetCountText,
                            onValueChange = { g5TargetCountText = it }
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirm(
                        selectedRuleType,
                        cleanPositiveInt(timesPerDayText, rule?.timesPerDay ?: 1),
                        cleanPositiveInt(timesPerOccurrenceText, rule?.timesPerOccurrence ?: 1),
                        cleanPositiveInt(g5TargetCountText, rule?.g5TargetCount ?: 5)
                    )
                }
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss
            ) {
                Text("Cancel")
            }
        }
    )
}

@Composable
private fun ChildRuleTypeOptionRow(
    option: ChildRuleTypeOption,
    selected: Boolean,
    onClick: () -> Unit
) {
    ListItem(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick),
        leadingContent = {
            Icon(
                imageVector = if (selected) {
                    Icons.Filled.CheckCircle
                } else {
                    Icons.Filled.RadioButtonUnchecked
                },
                contentDescription = null,
                tint = if (selected) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                }
            )
        },
        headlineContent = {
            Text(
                text = option.title,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        supportingContent = {
            Text(
                text = option.subtitle,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        },
        colors = ListItemDefaults.colors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    )
}

@Composable
private fun ChildRuleNumberField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit
) {
    TextField(
        value = value,
        onValueChange = { raw ->
            onValueChange(raw.filter { it.isDigit() }.take(3))
        },
        label = {
            Text(label)
        },
        singleLine = true,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Number
        ),
        modifier = Modifier.fillMaxWidth()
    )
}

private data class ChildRuleTypeOption(
    val ruleType: String,
    val title: String,
    val subtitle: String
)

private val childRuleTypeOptions = listOf(
    ChildRuleTypeOption(
        ruleType = TaskChildRuleType.ONCE_PER_PARENT_OCCURRENCE,
        title = "Every parent occurrence",
        subtitle = "Create one requirement each time the parent runs."
    ),
    ChildRuleTypeOption(
        ruleType = TaskChildRuleType.N_TIMES_PER_PARENT_OCCURRENCE,
        title = "N times per parent occurrence",
        subtitle = "Create multiple requirements each time the parent runs."
    ),
    ChildRuleTypeOption(
        ruleType = TaskChildRuleType.ONCE_PER_DAY,
        title = "Once per day",
        subtitle = "Count this child once per active day."
    ),
    ChildRuleTypeOption(
        ruleType = TaskChildRuleType.N_TIMES_PER_DAY,
        title = "N times per day",
        subtitle = "Count this child multiple times per active day."
    ),
    ChildRuleTypeOption(
        ruleType = TaskChildRuleType.ONCE_PER_PARENT_LIFETIME,
        title = "Once in parent lifetime",
        subtitle = "Complete this child once and keep it done."
    ),
    ChildRuleTypeOption(
        ruleType = TaskChildRuleType.G5_LEARNING,
        title = "G5 learning",
        subtitle = "Complete this child across multiple learning steps."
    ),
    ChildRuleTypeOption(
        ruleType = TaskChildRuleType.MANUAL_LIST_ITEM,
        title = "Manual list item",
        subtitle = "Use this child as a manually added list item."
    ),
    ChildRuleTypeOption(
        ruleType = TaskChildRuleType.MANUAL_RESET,
        title = "Manual reset",
        subtitle = "Keep it done until the user manually resets it."
    )
)

