package com.example.compoundeffectV1_01.ui.categoryScreen

import android.annotation.SuppressLint
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.compoundeffectV1_01.data.dataBaseRoom.tables.category.CategoryEntity
import com.example.compoundeffectV1_01.data.dataBaseRoom.tables.taskSchedule.ScheduleMode
import com.example.compoundeffectV1_01.utils.DimmedDialog
import com.example.compoundeffectV1_01.utils.IconOption
import com.example.compoundeffectV1_01.utils.buildColorOptions
import com.example.compoundeffectV1_01.utils.buildIconSections
import com.example.compoundeffectV1_01.utils.colorFromHex
import com.example.compoundeffectV1_01.utils.iconFromKey
import org.burnoutcrew.reorderable.ReorderableItem
import org.burnoutcrew.reorderable.detectReorderAfterLongPress
import org.burnoutcrew.reorderable.rememberReorderableLazyListState
import org.burnoutcrew.reorderable.reorderable
import java.time.LocalTime

@SuppressLint("SuspiciousIndentation")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryScreen(
    onNavigateToSchedule: () -> Unit, // فعلاً استفاده نمی‌کنیم، بعداً به bottom bar وصل می‌کنیم
    viewModel: CategoryViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val draft by viewModel.draft.collectAsState()
    val createResult by viewModel.createResult.collectAsState()
    val pickerFlatten by viewModel.parentPickerItems.collectAsState()
    val editingTaskId by viewModel.editingTaskId.collectAsState()
    val taskDraft by viewModel.taskDraft.collectAsState()
    val scheduleDraft by viewModel.scheduleDraft.collectAsState()
    val menuCategoryId by viewModel.menuCategoryId.collectAsState()
    val tasksForMenu by viewModel.tasksForMenuCategory.collectAsState()
    val tasksWithSchedule by viewModel.tasksWithScheduleForMenu.collectAsState()
    val scheduledCount by viewModel.scheduledCountForMenu.collectAsState()



    var showPickParent by rememberSaveable { mutableStateOf(false) }
    var showAddCategory by rememberSaveable { mutableStateOf(false) }
    var isDragging by remember { mutableStateOf(false) }

    var showIconPicker by rememberSaveable { mutableStateOf(false) }
    var showColorPicker by rememberSaveable { mutableStateOf(false) }

    var nameError by rememberSaveable { mutableStateOf<String?>(null) }

    val parentEntity = state.categories.firstOrNull { it.categoryId == draft.parentId }



    var showDeleteConfirm by rememberSaveable { mutableStateOf(false) }
    val menuCategory = state.categories.firstOrNull { it.categoryId == menuCategoryId }


    var showRenameDialog by rememberSaveable { mutableStateOf(false) }
    var renameText by rememberSaveable { mutableStateOf("") }
    var renameError by rememberSaveable { mutableStateOf<String?>(null) }
    var showEditIconPicker by rememberSaveable { mutableStateOf(false) }
    var showEditColorPicker by rememberSaveable { mutableStateOf(false) }

    var showAddTaskDialog by rememberSaveable { mutableStateOf(false) }
    var tasksExpanded by rememberSaveable(menuCategoryId) { mutableStateOf(false) }

    var showScheduleDialog by rememberSaveable { mutableStateOf(false) }


    LaunchedEffect(createResult) {
        nameError = (createResult as? CategoryViewModel.CreateResult.Error)?.message
    }

    LaunchedEffect(createResult) {
        when (createResult) {
            is CategoryViewModel.CreateResult.Success -> {
                showAddCategory = false
                showPickParent = false
                showIconPicker = false
                showColorPicker = false
                viewModel.resetDraft()
            }

            else -> Unit
        }
    }

    LaunchedEffect(menuCategoryId, tasksForMenu.size) {
        if (menuCategoryId != null && tasksForMenu.isNotEmpty()) {
            tasksExpanded = true // ✅ وقتی تسک آمد، اتومات باز شود
        }
    }



    Scaffold(
//        topBar = { TopAppBar(title = { Text("CategoryScreen2") }) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddCategory = true }
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Add Category")
            }


        },
        floatingActionButtonPosition = androidx.compose.material3.FabPosition.Start

        // bottomBar فعلاً از Navigation foundation میاد، بعداً یکدستش می‌کنیم
    ) { padding ->

        Column(modifier = Modifier.padding(padding)) {
            val visibleItems = state.renderItems.filter { it.isVisible }

            // ✅ لیست لوکال برای UI (مثل بامبو)
            var dragging by remember { mutableStateOf(false) }

            val listState = remember { mutableStateOf(state.renderItems) }

            // parent قبلی و parent موقت جدید
            val fromParentId = remember { mutableStateOf<Int?>(null) }
            val pendingParentId = remember { mutableStateOf<Int?>(null) }

            val pendingParentById =
                remember { mutableStateMapOf<Int, Int?>() } // draggedId -> newParentId
            val dragOffsetXById =
                remember { mutableStateMapOf<Int, Float>() }  // draggedId -> accumulatedX
            val threshold = 70f

            val lastReparentAtById = remember { mutableStateMapOf<Int, Long>() }
            val reparentDelayMs = 200L

            val childrenByParent = remember(state.categories) {
                state.categories
                    .filter { it.categoryId != null }
                    .groupBy { it.parentCategoryId }
                    .mapValues { entry -> entry.value.mapNotNull { it.categoryId } }
            }

            fun isDescendantOfDragged(candidateParentId: Int, draggedId: Int): Boolean {
                // آیا candidateParentId داخل زیر درخت draggedId است؟
                val stack = ArrayDeque<Int>()
                stack.add(draggedId)

                while (stack.isNotEmpty()) {
                    val cur = stack.removeLast()
                    val children = childrenByParent[cur] ?: emptyList()
                    if (candidateParentId in children) return true
                    children.forEach { stack.add(it) }
                }
                return false
            }


            fun effectiveParentId(item: CategoryRenderItem): Int? {
                val id = item.category.categoryId ?: return item.category.parentCategoryId
                return pendingParentById[id] ?: item.category.parentCategoryId
            }


            // وقتی درگ نداریم، با state.sync شو
            LaunchedEffect(state.renderItems) {
                if (!dragging) listState.value = state.renderItems
            }

            val draggingKey = remember { mutableStateOf<Int?>(null) }

            val reorderState = rememberReorderableLazyListState(
                onMove = { from, to ->
                    dragging = true

                    val draggedId = from.key as? Int ?: return@rememberReorderableLazyListState

                    val list = listState.value.toMutableList()
                    val fromIndex = from.index
                    val toIndex = to.index.coerceIn(0, list.lastIndex)
                    if (fromIndex !in list.indices || toIndex !in list.indices) return@rememberReorderableLazyListState
                    if (fromIndex == toIndex) return@rememberReorderableLazyListState

                    val toItem = list[toIndex]

                    val moved = list.removeAt(fromIndex)
                    list.add(toIndex, moved)
                    listState.value = list

                    // ✅ parent موقت = parentِ مقصد (عمودی)
                    val targetParent = effectiveParentId(toItem) // همون fun(item)
                    pendingParentById[draggedId] = targetParent
                },
                onDragEnd = { _, _ ->
                    dragging = false

                    val draggedId = draggingKey.value ?: return@rememberReorderableLazyListState

                    val oldParent = fromParentId.value
                    val newParent = pendingParentById[draggedId]

                    viewModel.applyDragResult(
                        draggedId = draggedId,
                        oldParentId = oldParent,
                        newParentId = newParent,
                        currentList = listState.value
                    )

                    viewModel.onDragEndRestoreExpand()

                    draggingKey.value = null
                    fromParentId.value = null

                    pendingParentById.clear()
                    dragOffsetXById.clear()
                    lastReparentAtById.clear()

                }


            )

            // ✅ collapse/restore هنگام شروع/پایان درگ (مثل قبل)
            LaunchedEffect(reorderState.draggingItemKey) {
                val key = reorderState.draggingItemKey as? Int
                if (key != null) {
                    draggingKey.value = key
                    fromParentId.value =
                        listState.value.firstOrNull { it.category.categoryId == key }?.category?.parentCategoryId
                    pendingParentId.value = null
                    viewModel.onDragStartMaybeCollapse(key)
                }
            }

            val allById = remember(state.renderItems) {
                state.renderItems.associateBy { it.category.categoryId }
            }

            // کمک: دسترسی سریع به آیتم‌ها با id (از لیست واقعی uiState)
            val entityById = remember(state.categories) {
                state.categories.associateBy { it.categoryId }
            }

            // parent موثر: اگر pending داریم از آن استفاده کن
            fun effectiveParentId(id: Int): Int? {
                return pendingParentById[id] ?: entityById[id]?.parentCategoryId
            }

            // محاسبه level بر اساس parent موثر (حداکثر 4)
            fun effectiveLevel(id: Int): Int {
                var level = 1
                var curParent = effectiveParentId(id)
                var guard = 0
                while (curParent != null && curParent != -1 && guard < 10) {
                    level++
                    val next = effectiveParentId(curParent)
                    curParent = next
                    guard++
                }
                return level.coerceAtMost(4)
            }


            fun tryIndent(id: Int) {
                // child شدن: parent = آیتم قبلی بالای خودش در همون لیست که level < 4
                val list = listState.value
                val idx = list.indexOfFirst { it.category.categoryId == id }
                if (idx <= 0) return
                val prev = list[idx - 1]
                val prevId = prev.category.categoryId ?: return

                val parentLevel = effectiveLevel(prevId)
                if (parentLevel >= 4) return


                // ✅ جلوگیری از cycle: نباید زیر بچه‌ی خودش بره
                if (isDescendantOfDragged(candidateParentId = prevId, draggedId = id)) return


                if (effectiveLevel(prevId) >= 4) return
                pendingParentById[id] = prevId
            }

            fun tryOutdent(id: Int) {
                val current = allById[id] ?: return
                val currentParent = pendingParentById[id] ?: current.category.parentCategoryId
                if (currentParent == null) return

                val parentItem = allById[currentParent] ?: return
                val newParent = parentItem.category.parentCategoryId
                pendingParentById[id] = newParent
            }

            val onHorizontalReparentHint: (Int, Float) -> Unit = reparent@{ id, deltaX ->
                val acc = (dragOffsetXById[id] ?: 0f) + deltaX
                dragOffsetXById[id] = acc

                val now = android.os.SystemClock.uptimeMillis()
                val last = lastReparentAtById[id] ?: 0L
                val canStep = (now - last) >= reparentDelayMs
                if (!canStep) return@reparent

                if (acc >= threshold) {
                    dragOffsetXById[id] = 0f
                    lastReparentAtById[id] = now
                    tryIndent(id)   // +1 level
                } else if (acc <= -threshold) {
                    dragOffsetXById[id] = 0f
                    lastReparentAtById[id] = now
                    tryOutdent(id)  // -1 level
                }
            }

            val currentDraggingId = reorderState.draggingItemKey as? Int
            val isDragActive = currentDraggingId != null


            LazyColumn(
                state = reorderState.listState, // ✅ مهم
                modifier = Modifier
                    .fillMaxSize()
                    .reorderable(reorderState)
            ) {
                items(
                    items = listState.value,
                    key = { it.category.categoryId ?: it.hashCode() }
                ) { item ->
                    val id = item.category.categoryId ?: return@items

                    ReorderableItem(reorderState, key = id) { _ ->
                        androidx.compose.animation.AnimatedVisibility(
                            visible = item.isVisible,
                            enter = fadeIn() + expandVertically(),
                            exit = fadeOut() + shrinkVertically()
                        ) {

                            val rowDragModifier = Modifier
                                .pointerInput(id, draggingKey.value) {
                                    awaitPointerEventScope {
                                        while (true) {
                                            val event = awaitPointerEvent()
                                            val change = event.changes.firstOrNull() ?: continue
                                            if (draggingKey.value == id && change.pressed) {
                                                val dx = change.positionChange().x
                                                if (dx != 0f) onHorizontalReparentHint(id, dx)
                                            }
                                        }
                                    }
                                }
                                .detectReorderAfterLongPress(reorderState) // ✅ کل ردیف draggable


                            CategoryRow2(
                                item = item,
                                computedLevel = effectiveLevel(id),
                                onToggleExpand = viewModel::toggleExpand,
                                modifier = rowDragModifier,
                                onOpenMenu = { id -> viewModel.setMenuCategoryId(id) }
                            )

                            HorizontalDivider(thickness = 0.5.dp) // ✅ خط باریک
                        }
                    }
                }

            }

        }

    }


    if (showAddCategory) {
        AddCategoryDialog2(
            draft = draft,
            parentName = state.categories.firstOrNull { it.categoryId == draft.parentId }?.name
                ?: "ریشه اصلی",
            parentIconName = parentEntity?.iconName ?: "Category",
            parentColorHex = parentEntity?.color ?: "#9E9E9E",
            onDismiss = {
                showAddCategory = false
                viewModel.resetDraft()
            },
            onPickParent = { showPickParent = true },
            onPickIcon = { showIconPicker = true },   // ✅
            onPickColor = { showColorPicker = true }, // ✅
            onNameChange = viewModel::setDraftName,
            onDescriptionChange = { desc ->
                // اگر draft.description را در VM نداری، فعلاً همینجا نگه دار یا یک setter اضافه کن
                // پیشنهاد: viewModel.setDraftDescription(desc)
            },
            onConfirm = {
                viewModel.createCategoryFromDraft()
            }

        )
    }

    if (showAddCategory && showPickParent) {
        PickParentDialogSmall(
            items = pickerFlatten.items,      // ✅ به جای state.renderItems
            levelById = pickerFlatten.levelById,
            onDismiss = { showPickParent = false },
            onPick = { parentId ->
                val ok = viewModel.trySetDraftParent(parentId)
                if (ok) showPickParent = false
            }
        )
    }


    if (showAddCategory && showIconPicker) {
        ChooseIconDialog(
            selectedKey = draft.iconName,
            onDismiss = { showIconPicker = false },
            onPick = { opt ->
                viewModel.setDraftIconName(opt.key)
                showIconPicker = false
            }
        )
    }

    if (showAddCategory && showColorPicker) {
        ChooseColorDialog(
            initialHex = draft.color,
            onDismiss = { showColorPicker = false },
            onConfirm = { hex ->
                viewModel.setDraftColor(hex)
                showColorPicker = false
            }
        )
    }

    if (menuCategory != null) {

        CategoryOptionsSideSheet(
            category = menuCategory,
            onDismiss = { viewModel.setMenuCategoryId(null) },
            onClickPickIcon = { showEditIconPicker = true },
            onClickPickColor = { showEditColorPicker = true },
            onClickRename = {
                showRenameDialog = true
                renameText = menuCategory.name
                renameError = null
            },
            onClickEditDescription = {
                // یک دیالوگ برای description
            },
            onClickDelete = {
                showDeleteConfirm = true
            },
            tasks = tasksWithSchedule.map { tws ->
                TaskMiniUi(
                    id = tws.task.id ?: -1,
                    title = tws.task.name,
                    isDone = tws.task.isCompleted,
                    hasSchedule = (tws.schedule != null)
                )
            },
            onAddTask = {
                val cat = menuCategory ?: return@CategoryOptionsSideSheet
                viewModel.startAddTask(cat.categoryId!!, cat.color)
                showAddTaskDialog = true
            },
            tasksExpanded = tasksExpanded,
            onToggleTasksExpand = { tasksExpanded = !tasksExpanded },
            onClickTask = { taskId ->
                viewModel.startEditTask(taskId)
            },
            scheduledCount=scheduledCount

        )
    }

    if (showDeleteConfirm && menuCategoryId != null) {
        androidx.compose.material3.AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text("Delete category?") },
            text = { Text("Are you sure you want to delete this category?") },
            confirmButton = {
                TextButton(onClick = {
                    val id = menuCategoryId ?: return@TextButton
                    viewModel.deleteCategoryPromoteChildren(id)
                    showDeleteConfirm = false
                    viewModel.setMenuCategoryId(null)
                }) { Text("Delete") }

            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false }) { Text("Cancel") }
            }
        )
    }

    if (showRenameDialog && menuCategoryId != null) {
        androidx.compose.material3.AlertDialog(
            onDismissRequest = { showRenameDialog = false },
            title = { Text("Rename") },
            text = {
                Column {
                    OutlinedTextField(
                        value = renameText,
                        onValueChange = {
                            renameText = it
                            if (renameError != null) renameError = null
                        },
                        singleLine = true,
                        label = { Text("Name") },
                        isError = renameError != null
                    )
                    if (renameError != null) {
                        Text(
                            text = renameError!!,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.labelSmall,
                            modifier = Modifier.padding(top = 6.dp)
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    val trimmed = renameText.trim()
                    if (trimmed.isBlank()) {
                        renameError = "نام نمی‌تواند خالی باشد"
                        return@TextButton
                    }
                    viewModel.renameCategory(menuCategoryId!!, trimmed)
                    showRenameDialog = false
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showRenameDialog = false }) { Text("Cancel") }
            }
        )
    }

    if (menuCategoryId != null && showEditIconPicker) {
        val current = state.categories.firstOrNull { it.categoryId == menuCategoryId } ?: return

            ChooseIconDialog(
                selectedKey = current.iconName,
                onDismiss = { showEditIconPicker = false },
                onPick = { opt ->
                    viewModel.updateCategoryIcon(menuCategoryId!!, opt.key)
                    showEditIconPicker = false
                }
            )
    }

    if (menuCategoryId != null && showEditColorPicker) {
        val current = state.categories.firstOrNull { it.categoryId == menuCategoryId } ?: return

            ChooseColorDialog(
                initialHex = current.color,
                onDismiss = { showEditColorPicker = false },
                onConfirm = { hex ->
                    viewModel.updateCategoryColor(menuCategoryId!!, hex)
                    showEditColorPicker = false
                }
            )
    }

    if (showAddTaskDialog && menuCategory != null) {
        AddTaskDialog(
            addTaskMod = true,
            categoryName = menuCategory.name,
            categoryIconName = menuCategory.iconName,
            categoryColorHex = menuCategory.color,
            draft = taskDraft,
            onDismiss = { showAddTaskDialog = false },
            onNameChange = viewModel::setTaskName,
            onPriorityChange = viewModel::setTaskPriority,
            onCompletedToggle = viewModel::setTaskCompleted,
            onNoteChange = viewModel::setTaskNote,
            onConfirm = {
                viewModel.createTaskForCategory(menuCategory.color)
                showAddTaskDialog = false
            },
            onOpenSchedule = { showScheduleDialog = true }
        )
    }

    if (editingTaskId != null && menuCategory != null) {
        AddTaskDialog(
            addTaskMod = false,
            categoryName = menuCategory.name,
            categoryIconName = menuCategory.iconName,
            categoryColorHex = menuCategory.color,
            draft = taskDraft,
            onDismiss = { viewModel.finishEditTask() },
            onNameChange = viewModel::setTaskName,
            onPriorityChange = viewModel::setTaskPriority,
            onCompletedToggle = viewModel::setTaskCompleted,
            onNoteChange = viewModel::setTaskNote,
            onConfirm = {
                viewModel.saveEditedTask(menuCategory.color)
            },
            onOpenSchedule = { showScheduleDialog = true }
        )
    }
    if (showScheduleDialog && menuCategory != null) {
        TaskScheduleDialog(
            taskName = taskDraft.name.ifBlank { "Task" },
            draft = scheduleDraft,
            onDismiss = { showScheduleDialog = false },
            onTitleChange = viewModel::setScheduleTitle,
            onModeChange = viewModel::setScheduleMode,
            onStartChange = viewModel::setScheduleStart,
            onEndChange = viewModel::setScheduleEnd,
            onDurationChange = viewModel::setScheduleDuration,
            onRepeatingChange = viewModel::setScheduleRepeating,
            onConfirm = {
                viewModel.markScheduleConfirmedForNewTask()
                viewModel.saveScheduleForCurrentTask()
                showScheduleDialog = false
            }
        )
    }


}

@Composable
fun AddCategoryDialog2(
    draft: CategoryDraft2,
    parentName: String,
    parentIconName: String,   // ✅ جدید
    parentColorHex: String,
    onDismiss: () -> Unit,
    onPickParent: () -> Unit,
    onPickIcon: () -> Unit,   // ✅ جدید
    onPickColor: () -> Unit,  // ✅ جدید
    onNameChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit, // ✅ جدید (فعلاً اگر نداری، می‌تونی خالی پاس بدی)
    onConfirm: () -> Unit,
) {
    DimmedDialog(
        onDismiss = onDismiss,
        modifier = Modifier
            .fillMaxWidth(0.92f)
            .fillMaxHeight(0.85f)
            .background(MaterialTheme.colorScheme.surface, shape = MaterialTheme.shapes.extraLarge),
        dimAlpha = 0.6f,
        dismissOnBackdropClick = true
    ) {
        Column(modifier = Modifier.fillMaxSize()) {

            // --- Top bar داخل دیالوگ (مثل عکس) ---
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                TextButton(onClick = onDismiss) { Text("Back") }
                Text(text = "New category", style = MaterialTheme.typography.titleLarge)
                TextButton(onClick = onConfirm) { Text("✓") }
            }

            HorizontalDivider()

            // --- محتوا ---
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {

                // Name*
                OutlinedTextField(
                    value = draft.name,
                    onValueChange = onNameChange,
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Name *") },
                    singleLine = true
                )

                Spacer(Modifier.size(10.dp))

                // Parent row (مثل عکس یک ردیف ساده)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onPickParent() }
                        .padding(vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Parent", modifier = Modifier.weight(1f))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = iconFromKey(parentIconName),
                            contentDescription = parentIconName,
                            tint = colorFromHex(parentColorHex),
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(parentName, style = MaterialTheme.typography.titleMedium)
                    }
                }


                HorizontalDivider()

                // Icon row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onPickIcon() }
                        .padding(vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Icon", modifier = Modifier.weight(1f))

                    Icon(
                        imageVector = iconFromKey(draft.iconName),
                        contentDescription = draft.iconName,
                        tint = colorFromHex(draft.color),   // ✅ مهم
                        modifier = Modifier.size(24.dp)
                    )

                }


                HorizontalDivider()

                // Color row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onPickColor() }
                        .padding(vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Color", modifier = Modifier.weight(1f))

                    Box(
                        modifier = Modifier
                            .size(22.dp)
                            .border(1.dp, Color.Black.copy(alpha = 0.15f), CircleShape)
                            .background(colorFromHex(draft.color), CircleShape)
                    )
                }

                HorizontalDivider()

                Spacer(Modifier.size(10.dp))

                // Description (اختیاری)
                OutlinedTextField(
                    value = draft.description,
                    onValueChange = onDescriptionChange,
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Add description") },
                    minLines = 2
                )

                Spacer(Modifier.weight(1f))

                // (اختیاری) نمایش parentId برای دیباگ
                Text(
                    text = "ParentId: ${draft.parentId}",
                    style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
    }
}


@Composable
private fun CategoryRow2(
    item: CategoryRenderItem,
    computedLevel: Int,
    onToggleExpand: (Int) -> Unit,
    modifier: Modifier = Modifier,
    onOpenMenu: (Int) -> Unit, // ✅ جدید
) {
    val id = item.category.categoryId ?: return
    val indent = (computedLevel - 1).coerceAtLeast(0) * 16
    val bg = containerColorForLevel(computedLevel)

    ListItem(
        modifier = modifier
            .fillMaxWidth()
            .padding(start = indent.dp),
        colors = ListItemDefaults.colors(containerColor = bg),
        leadingContent = {
            Icon(
                imageVector = iconFromKey(item.category.iconName),
                contentDescription = null,
                tint = colorFromHex(item.category.color),
                modifier = Modifier.size(30.dp)
            )
        },
        headlineContent = { Text(item.category.name) },
        trailingContent = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                val canHaveChildren = computedLevel < 4
                if (item.hasChildren && canHaveChildren) {
                    IconButton(onClick = { onToggleExpand(id) }) {
                        Icon(
                            imageVector = if (item.isExpanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                            contentDescription = "expand"
                        )
                    }
                }

                IconButton(onClick = { onOpenMenu(id) }) { // ✅ مستقیم SideSheet
                    Icon(Icons.Filled.MoreVert, contentDescription = "menu")
                }
            }
        }
    )
}


@Composable
fun PickParentDialogSmall(
    items: List<CategoryRenderItem>,
    levelById: Map<Int, Int>,
    onDismiss: () -> Unit,
    onPick: (parentId: Int) -> Unit,
) {
    DimmedDialog(
        onDismiss = onDismiss,
        modifier = Modifier
            .fillMaxWidth(0.86f)
            .fillMaxHeight(0.65f) // کوچکتر از قبلی
            .background(MaterialTheme.colorScheme.surface, shape = MaterialTheme.shapes.extraLarge),
        dimAlpha = 0.4f, // کمی کمتر چون روی دیالوگ دیگر میاد
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


@Composable
fun ChooseIconDialog(
    selectedKey: String,
    onDismiss: () -> Unit,
    onPick: (IconOption) -> Unit,
) {
    val sections = remember { buildIconSections() }
    val expanded = remember {
        mutableStateMapOf<String, Boolean>().apply {
            sections.forEach { put(it.title, true) } // همه باز باشند مثل عکس
        }
    }

    DimmedDialog(
        onDismiss = onDismiss,
        modifier = Modifier
            .fillMaxWidth(0.92f)
            .fillMaxHeight(0.80f)
            .background(MaterialTheme.colorScheme.surface, shape = MaterialTheme.shapes.extraLarge),
        dimAlpha = 0.6f,
        dismissOnBackdropClick = true
    ) {
        Column(modifier = Modifier.fillMaxSize()) {

            Text(
                text = "Choose icon",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(16.dp)
            )

            HorizontalDivider()

            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 12.dp)
            ) {
                items(sections) { section ->
                    SectionHeaderRow(
                        title = section.title,
                        isExpanded = expanded[section.title] == true,
                        onToggle = { expanded[section.title] = !(expanded[section.title] == true) }
                    )

                    if (expanded[section.title] == true) {
                        IconGrid4(
                            options = section.options,
                            selectedKey = selectedKey,
                            onPick = onPick
                        )
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = onDismiss) { Text("CANCEL") }
            }
        }
    }
}

@Composable
private fun SectionHeaderRow(
    title: String,
    isExpanded: Boolean,
    onToggle: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 12.dp, bottom = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.weight(1f)
        )
        IconButton(onClick = onToggle) {
            Icon(
                imageVector = if (isExpanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                contentDescription = "toggle"
            )
        }
    }
}

@Composable
private fun IconGrid4(
    options: List<IconOption>,
    selectedKey: String,
    onPick: (IconOption) -> Unit,
) {
    // ساده و سبک: ۴تایی در هر ردیف، بدون LazyVerticalGrid
    val rows = (options.size + 3) / 4
    Column(modifier = Modifier.fillMaxWidth()) {
        repeat(rows) { r ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                for (c in 0 until 4) {
                    val idx = r * 4 + c
                    if (idx < options.size) {
                        val opt = options[idx]
                        val selected = opt.key == selectedKey

                        IconButton(
                            onClick = { onPick(opt) },
                            modifier = Modifier
                                .size(56.dp)
                                .then(
                                    if (selected) Modifier.border(
                                        width = 2.dp,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        shape = MaterialTheme.shapes.small
                                    ) else Modifier
                                )
                        ) {
                            Icon(
                                imageVector = opt.icon,
                                contentDescription = opt.key,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    } else {
                        Spacer(modifier = Modifier.size(56.dp))
                    }
                }
            }
        }
    }
}


@Composable
fun ChooseColorDialog(
    initialHex: String,
    onDismiss: () -> Unit,
    onConfirm: (hex: String) -> Unit
) {
    val options = remember { buildColorOptions() }
    var selectedHex by remember { mutableStateOf(initialHex.ifBlank { options.first().hex }) }

    DimmedDialog(
        onDismiss = onDismiss,
        modifier = Modifier
            .fillMaxWidth(0.92f)
            .fillMaxHeight(0.62f)
            .background(MaterialTheme.colorScheme.surface, shape = MaterialTheme.shapes.extraLarge),
        dimAlpha = 0.6f,
        dismissOnBackdropClick = true
    ) {
        Column(modifier = Modifier.fillMaxSize()) {

            Text(
                text = "Choose color",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(16.dp)
            )

            HorizontalDivider()

            Spacer(Modifier.height(18.dp))

            // Grid ساده: 6 ستون * چند ردیف
            val cols = 6
            val rows = (options.size + cols - 1) / cols

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                repeat(rows) { r ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        for (c in 0 until cols) {
                            val idx = r * cols + c
                            if (idx < options.size) {
                                val opt = options[idx]
                                val selected = opt.hex.equals(selectedHex, ignoreCase = true)

                                Box(
                                    modifier = Modifier
                                        .size(42.dp)
                                        .border(
                                            width = if (selected) 4.dp else 0.dp,
                                            color = if (selected) Color.Black else Color.Transparent,
                                            shape = CircleShape
                                        )
                                        .padding(if (selected) 4.dp else 0.dp)
                                        .border(
                                            width = 1.dp,
                                            color = Color.Black.copy(alpha = 0.08f),
                                            shape = CircleShape
                                        )
                                        .clickable { selectedHex = opt.hex },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .border(0.dp, Color.Transparent, CircleShape)
                                            .background(opt.color, CircleShape)
                                    )
                                }
                            } else {
                                Spacer(Modifier.size(42.dp))
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.weight(1f))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = onDismiss) { Text("CANCEL") }
                Spacer(Modifier.width(8.dp))
                TextButton(onClick = { onConfirm(selectedHex) }) { Text("OK") }
            }
        }
    }
}


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


@Composable
private fun CategoryOptionsSideSheet(
    category: CategoryEntity,
    tasks: List<TaskMiniUi>,              // ✅ جدید
    onDismiss: () -> Unit,
    onClickPickIcon: () -> Unit,
    onClickPickColor: () -> Unit,
    onClickRename: () -> Unit,
    onClickEditDescription: () -> Unit,
    onClickDelete: () -> Unit,
    onAddTask: () -> Unit,
    tasksExpanded: Boolean,
    onToggleTasksExpand: () -> Unit,
    onClickTask: (Int) -> Unit,
    scheduledCount: Int,
) {


    Box(
        modifier = Modifier
            .fillMaxSize()
            .zIndex(100f)
    ) {
        // ✅ Backdrop کلیکی برای dismiss
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.45f))
                .clickable(
                    indication = null,
                    interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() }
                ) { onDismiss() }
        )
        Column(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .fillMaxHeight()
                .fillMaxWidth(0.78f)
                .background(MaterialTheme.colorScheme.surface)
        ) {
            // Header (آیکن + اسم)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(colorFromHex(category.color).copy(alpha = 0.15f))
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = iconFromKey(category.iconName),
                    contentDescription = null,
                    tint = colorFromHex(category.color),
                    modifier = Modifier.size(28.dp)
                )
                Spacer(Modifier.width(12.dp))
                Text(
                    text = category.name,
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.weight(1f)
                )
            }

            HorizontalDivider()

            SheetActionRow(
                leading = {
                    Icon(
                        imageVector = iconFromKey(category.iconName),
                        contentDescription = null,
                        tint = colorFromHex(category.color)
                    )
                },
                title = "Add task",
                trailing = { Icon(Icons.Filled.Search, contentDescription = null) },
                onClick = {
                    onAddTask() // ✅ جدید
                }
            )
            HorizontalDivider(thickness = 0.5.dp)

            TasksSection(
                tasks = tasks,
                expanded = tasksExpanded,
                onToggleExpand = onToggleTasksExpand,
                onOpenTasksMenu = { /* بعداً */ },
                onClickTask = onClickTask
            )

            // بخش آمارها (فعلاً 0)

            SheetStatRow(icon = Icons.Filled.Event, text = "${scheduledCount} scheduled activities")
            SheetStatRow(icon = Icons.Filled.History, text = "0 logged activities")
            SheetStatRow(icon = Icons.Filled.Description, text = "0 notes")
            SheetStatRow(icon = Icons.Filled.AttachFile, text = "0 attachments")

            Spacer(Modifier.height(10.dp))
            HorizontalDivider()

            // Parameters (باز/بسته)
            var paramsExpanded by rememberSaveable { mutableStateOf(true) }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { paramsExpanded = !paramsExpanded }
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Filled.Settings, contentDescription = null)
                Spacer(Modifier.width(12.dp))
                Text(
                    "Parameters",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    imageVector = if (paramsExpanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                    contentDescription = null
                )
            }

            if (paramsExpanded) {
                HorizontalDivider()

                // Icon
                SheetActionRow(
                    leading = {
                        Icon(
                            iconFromKey(category.iconName),
                            contentDescription = null,
                            tint = colorFromHex(category.color)
                        )
                    },
                    title = "Icon",
                    trailing = {
                        Icon(Icons.Filled.GridView, contentDescription = null)
                    },
                    onClick = onClickPickIcon
                )

                // Color
                SheetActionRow(
                    leading = {
                        Box(
                            Modifier
                                .size(22.dp)
                                .border(1.dp, Color.Black.copy(alpha = 0.12f), CircleShape)
                                .background(colorFromHex(category.color), CircleShape)
                        )
                    },
                    title = "Color",
                    trailing = { Icon(Icons.Filled.Palette, contentDescription = null) },
                    onClick = onClickPickColor
                )

                // Rename
                SheetActionRow(
                    leading = { Icon(Icons.Filled.Edit, contentDescription = null) },
                    title = "Rename",
                    onClick = onClickRename
                )

                // Description
                SheetActionRow(
                    leading = { Icon(Icons.Filled.Description, contentDescription = null) },
                    title = "Add description",
                    onClick = onClickEditDescription
                )

                // Delete
                SheetActionRow(
                    leading = { Icon(Icons.Filled.Delete, contentDescription = null) },
                    title = "Delete",
                    titleColor = MaterialTheme.colorScheme.error,
                    onClick = onClickDelete
                )
            }
        }
    }
}


@Composable
private fun SheetStatRow(icon: androidx.compose.ui.graphics.vector.ImageVector, text: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.width(12.dp))
        Text(text)
    }
    HorizontalDivider(thickness = 0.5.dp)
}

@Composable
private fun SheetActionRow(
    leading: @Composable () -> Unit,
    title: String,
    onClick: () -> Unit,
    trailing: (@Composable () -> Unit)? = null,
    titleColor: Color = LocalContentColor.current,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(Modifier.size(26.dp), contentAlignment = Alignment.Center) { leading() }
        Spacer(Modifier.width(12.dp))
        Text(title, color = titleColor, modifier = Modifier.weight(1f))
        if (trailing != null) {
            Box(Modifier.size(24.dp), contentAlignment = Alignment.Center) { trailing() }
        }
    }
    HorizontalDivider(thickness = 0.5.dp)
}


@Composable
private fun TasksSection(
    tasks: List<TaskMiniUi>,
    expanded: Boolean,
    onToggleExpand: () -> Unit,
    onOpenTasksMenu: () -> Unit, // اگر خواستی سه نقطه کنار tasks هم داشته باشه
    onClickTask: (Int) -> Unit = {}
) {
    // Header row مثل عکس: "1 task" + فلش + سه‌نقطه
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onToggleExpand() }
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(Icons.Filled.Done, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.width(12.dp))

        Text("${tasks.size} task${if (tasks.size == 1) "" else "s"}", modifier = Modifier.weight(1f))

        IconButton(onClick = onToggleExpand) {
            Icon(
                imageVector = if (expanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                contentDescription = null
            )
        }

        IconButton(onClick = onOpenTasksMenu) {
            Icon(Icons.Filled.MoreVert, contentDescription = "tasks menu")
        }
    }

    HorizontalDivider(thickness = 0.5.dp)

    if (expanded) {
        tasks.forEach { t ->
            TaskRowMini(
                task = t,
                onClick = { onClickTask(t.id) }
            )
            HorizontalDivider(thickness = 0.5.dp)
        }
    }
}

@Composable
private fun TaskRowMini(
    task: TaskMiniUi,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // شبیه عکس: دایره کنار تسک
        Box(
            modifier = Modifier
                .size(22.dp)
                .border(2.dp, MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.35f), CircleShape)
        )
        Spacer(Modifier.width(14.dp))
        Text(task.title)
    }
}

@Composable
fun AddTaskDialog(
    addTaskMod:Boolean,
    categoryName: String,
    categoryIconName: String,
    categoryColorHex: String,
    draft: TaskDraft,
    onDismiss: () -> Unit,
    onNameChange: (String) -> Unit,
    onPriorityChange: (Int) -> Unit,
    onCompletedToggle: (Boolean) -> Unit,
    onNoteChange: (String) -> Unit,
    onConfirm: () -> Unit,
    onOpenSchedule: () -> Unit
) {
    DimmedDialog(
        onDismiss = onDismiss,
        modifier = Modifier
            .fillMaxWidth(0.92f)
            .fillMaxHeight(0.88f)
            .background(MaterialTheme.colorScheme.surface, shape = MaterialTheme.shapes.extraLarge),
        dimAlpha = 0.6f,
        dismissOnBackdropClick = true
    ) {
        Column(modifier = Modifier.fillMaxSize()) {

            // Top bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                TextButton(onClick = onDismiss) { Text("Back") }
                Text(
                    if (addTaskMod)"New task" else "Edit Task",
                    style = MaterialTheme.typography.titleLarge)
                TextButton(
                    onClick = onConfirm,
                    enabled = draft.name.isNotBlank()
                ) { Text("✓") }
            }

            HorizontalDivider()

            // Title row (مثل Test زیرخط)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Filled.Check, contentDescription = null)
                Spacer(Modifier.width(10.dp))
                OutlinedTextField(
                    value = draft.name,
                    onValueChange = onNameChange,
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Title") },
                    singleLine = true
                )
            }

            HorizontalDivider()

            // Category row (آیکن+نام)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = iconFromKey(categoryIconName),
                    contentDescription = null,
                    tint = colorFromHex(categoryColorHex),
                    modifier = Modifier.size(26.dp)
                )
                Spacer(Modifier.width(12.dp))
                Text(categoryName, style = MaterialTheme.typography.titleMedium, modifier = Modifier.weight(1f))
            }

            HorizontalDivider()

            // Priority row (سه دایره مثل عکس)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Priority", modifier = Modifier.width(90.dp))
                PriorityDots(
                    selected = draft.priority,
                    onPick = onPriorityChange
                )
            }

            HorizontalDivider()

            // Completed row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(if (draft.isCompleted) "Completed" else "Uncompleted", modifier = Modifier.weight(1f))
                Switch(checked = draft.isCompleted, onCheckedChange = onCompletedToggle)
            }

            HorizontalDivider()

            // Note
            OutlinedTextField(
                value = draft.note,
                onValueChange = onNoteChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                label = { Text("Add note") },
                minLines = 2
            )

            SheetActionRow(
                leading = { Icon(Icons.Filled.Event, contentDescription = null) },
                title = "Schedule this task",
                onClick = { onOpenSchedule() }
            )

        }
    }
}

@Composable
private fun PriorityDots(selected: Int, onPick: (Int) -> Unit) {
    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        (0..2).forEach { i ->
            val isSel = i == selected
            Box(
                modifier = Modifier
                    .size(44.dp)
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
                    text = when (i) { 0 -> "*" ; 1 -> "!" ; else -> "!!" },
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }
    }
}

@Composable
fun TaskScheduleDialog(
    taskName: String,
    draft: ScheduleDraft,
    onDismiss: () -> Unit,
    onTitleChange: (String) -> Unit,
    onModeChange: (ScheduleMode) -> Unit,
    onStartChange: (LocalTime) -> Unit,
    onEndChange: (LocalTime) -> Unit,
    onDurationChange: (Int) -> Unit,
    onRepeatingChange: (Boolean) -> Unit,
    onConfirm: () -> Unit
) {
    DimmedDialog(
        onDismiss = onDismiss,
        modifier = Modifier
            .fillMaxWidth(0.92f)
            .fillMaxHeight(0.88f)
            .background(MaterialTheme.colorScheme.surface, shape = MaterialTheme.shapes.extraLarge),
        dimAlpha = 0.6f,
        dismissOnBackdropClick = true
    ) {
        Column(Modifier.fillMaxSize()) {

            // top bar
            Row(
                Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                TextButton(onClick = onDismiss) { Text("Back") }
                Text("New scheduled activity", style = MaterialTheme.typography.titleLarge)
                TextButton(onClick = onConfirm) { Text("✓") }
            }

            HorizontalDivider()

            // title (کم‌رنگ پیش‌فرض)
            OutlinedTextField(
                value = draft.title,
                onValueChange = onTitleChange,
                modifier = Modifier.fillMaxWidth().padding(12.dp),
                label = { Text("Title (optional)") },
                placeholder = { Text(taskName, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.35f)) },
                singleLine = true
            )

            HorizontalDivider()

            // mode dropdown: Time range / Amount of time
            ModeDropdownRow(
                mode = draft.mode,
                onPick = onModeChange
            )

            HorizontalDivider()

            if (draft.mode == ScheduleMode.TIME_RANGE) {
                // فعلاً ساده: نمایش متن + بعداً TimePicker
                TimeRangeRow(
                    start = draft.start,
                    end = draft.end,
                    onStartChange = onStartChange,
                    onEndChange = onEndChange
                )
            } else {
                AmountOfTimeRow(
                    minutes = draft.durationMinutes,
                    onMinutesChange = onDurationChange
                )
            }

            HorizontalDivider()

            // Repeating
            Row(
                Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Repeating", modifier = Modifier.weight(1f))
                Switch(checked = draft.repeating, onCheckedChange = onRepeatingChange)
            }

            HorizontalDivider()

            // Note / Reminder placeholder (فعلاً)
            SheetActionRow(
                leading = { Icon(Icons.Filled.Description, null) },
                title = "Add note",
                onClick = { /* بعداً */ }
            )
            SheetActionRow(
                leading = { Icon(Icons.Filled.History, null) },
                title = "Add reminder",
                onClick = { /* بعداً */ }
            )
        }
    }
}

@Composable
private fun ModeDropdownRow(
    mode: ScheduleMode,
    onPick: (ScheduleMode) -> Unit
) {
    var open by remember { mutableStateOf(false) }
    Row(
        Modifier.fillMaxWidth().clickable { open = true }.padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("Time range", modifier = Modifier.weight(1f))
        Text(
            text = if (mode == ScheduleMode.TIME_RANGE) "Time range" else "Amount of time",
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.width(8.dp))
        Icon(Icons.Filled.ExpandMore, contentDescription = null)
    }
    DropdownMenu(expanded = open, onDismissRequest = { open = false }) {
        DropdownMenuItem(
            text = { Text("Time range") },
            onClick = { open = false; onPick(ScheduleMode.TIME_RANGE) }
        )
        DropdownMenuItem(
            text = { Text("Amount of time") },
            onClick = { open = false; onPick(ScheduleMode.AMOUNT_OF_TIME) }
        )
    }
}

@Composable
private fun TimeRangeRow(
    start: LocalTime,
    end: LocalTime,
    onStartChange: (LocalTime) -> Unit,
    onEndChange: (LocalTime) -> Unit
) {
    // فعلاً فقط نمایش، بعداً TimePicker می‌ذاریم
    Row(
        Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 18.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("${start}", style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.width(18.dp))
        Text("-", style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.width(18.dp))
        Text("${end}", style = MaterialTheme.typography.titleLarge)
    }
}

@Composable
private fun AmountOfTimeRow(
    minutes: Int,
    onMinutesChange: (Int) -> Unit
) {
    OutlinedTextField(
        value = minutes.toString(),
        onValueChange = { v -> onMinutesChange(v.filter(Char::isDigit).toIntOrNull() ?: minutes) },
        modifier = Modifier.fillMaxWidth().padding(12.dp),
        label = { Text("Minutes") },
        singleLine = true
    )
}
