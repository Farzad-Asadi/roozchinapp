package com.example.compoundeffectV1_01.ui.categoryScreen

import android.annotation.SuppressLint
import android.os.Build
import android.os.SystemClock
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowRight
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.RadioButtonChecked
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.RemoveDone
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Task
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FabPosition
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberSwipeToDismissBoxState
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
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.compoundeffectV1_01.data.dataBaseRoom.tables.category.CategoryEntity
import com.example.compoundeffectV1_01.data.dataBaseRoom.tables.task.TaskEntity
import com.example.compoundeffectV1_01.data.dataClasses.CategoryDraft
import com.example.compoundeffectV1_01.data.dataClasses.CategoryRenderItem
import com.example.compoundeffectV1_01.data.dataClasses.TaskMiniUi
import com.example.compoundeffectV1_01.data.dataClasses.TaskRenderItem
import com.example.compoundeffectV1_01.data.sharedViewModel.MainSharedViewModel
import com.example.compoundeffectV1_01.ui.navigation.AppGraphRoutes
import com.example.compoundeffectV1_01.ui.navigation.AppRoutes
import com.example.compoundeffectV1_01.utils.DimmedDialog
import com.example.compoundeffectV1_01.utils.IconOption
import com.example.compoundeffectV1_01.utils.buildIconSections
import com.example.compoundeffectV1_01.utils.colorFromHex
import com.example.compoundeffectV1_01.utils.iconFromKey
import org.burnoutcrew.reorderable.ReorderableItem
import org.burnoutcrew.reorderable.detectReorderAfterLongPress
import org.burnoutcrew.reorderable.rememberReorderableLazyListState
import org.burnoutcrew.reorderable.reorderable

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@SuppressLint("SuspiciousIndentation", "UnrememberedGetBackStackEntry")
@Composable
fun CategoryScreen(
    navController: NavHostController,
    viewModel: CategoryViewModel = hiltViewModel()
) {

    val state by viewModel.uiState.collectAsState()
    val draft by viewModel.draft.collectAsState()
    val createResult by viewModel.createResult.collectAsState()
    val pickerFlatten by viewModel.parentPickerItems.collectAsState()
    val menuCategoryId by viewModel.menuCategoryId.collectAsState()
    val tasksForMenu by viewModel.tasksForMenuCategory.collectAsState()
    val tasksWithSchedule by viewModel.tasksWithScheduleForMenu.collectAsState()
    val scheduledCount by viewModel.scheduledCountForMenu.collectAsState()


    var showPickParent by rememberSaveable { mutableStateOf(false) }
    var showIconPicker by rememberSaveable { mutableStateOf(false) }
    var showColorPicker by rememberSaveable { mutableStateOf(false) }
    var nameError by rememberSaveable { mutableStateOf<String?>(null) }
    var showDeleteConfirm by rememberSaveable { mutableStateOf(false) }
    var showRenameDialog by rememberSaveable { mutableStateOf(false) }
    var renameText by rememberSaveable { mutableStateOf("") }
    var renameError by rememberSaveable { mutableStateOf<String?>(null) }
    var showEditIconPicker by rememberSaveable { mutableStateOf(false) }
    var showEditColorPicker by rememberSaveable { mutableStateOf(false) }
    var tasksExpanded by rememberSaveable(menuCategoryId) { mutableStateOf(false) }
    var sheetMode by rememberSaveable(menuCategoryId) { mutableStateOf(CategorySheetMode.OVERVIEW) }
    var showAddCategory by rememberSaveable { mutableStateOf(false) }
    val rootEntry = remember(navController) {
        navController.getBackStackEntry(AppGraphRoutes.ROOT)
    }
    val sharedVm: MainSharedViewModel = hiltViewModel(rootEntry)


    val parentEntity = state.categories.firstOrNull { it.categoryId == draft.parentId }
    val menuCategory = state.categories.firstOrNull { it.categoryId == menuCategoryId }


    LaunchedEffect(Unit) {
        sharedVm.events.collect { e ->
            when (e) {
                MainSharedViewModel.Event.AddCategoryClicked -> {
                    showAddCategory = true
                }
            }
        }
    }
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

        floatingActionButtonPosition = FabPosition.Start
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {

            var dragging by remember { mutableStateOf(false) }

            val listState = remember {
                mutableStateOf<List<CategoryRenderItem>>(emptyList())
            }

            val visibleCategoryItems =
                if (dragging) listState.value else state.renderItems

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

            val draggingKey = remember { mutableStateOf<Int?>(null) }


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
            LaunchedEffect(state.renderItems, dragging) {
                if (!dragging) {
                    listState.value = state.renderItems
                }
            }

            val reorderState = rememberReorderableLazyListState(
                onMove = { from, to ->
                    dragging = true

                    val draggedId = from.key as? Int ?: return@rememberReorderableLazyListState


                    val fromIndex = from.index
                    val list = listState.value.toMutableList()
                    if (list.isEmpty()) return@rememberReorderableLazyListState

                    val max = list.lastIndex
                    val toIndex = to.index.coerceIn(0, max)
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

                val now = SystemClock.uptimeMillis()
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




            when {
                state.isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        // عمداً چیزی نشان نمی‌دهیم تا Empty State چشمک نزند.
                        // اگر خواستی می‌توانیم بعداً Progress هم بگذاریم.
                    }
                }

                visibleCategoryItems.isEmpty() -> {
                    EmptyCategoryState(
                        onAddCategoryClick = {
                            showAddCategory = true
                        }
                    )
                }

                else -> {
                    LazyColumn(
                        state = reorderState.listState,
                        modifier = Modifier
                            .fillMaxSize()
                            .reorderable(reorderState)
                    ) {
                        items(
                            items = visibleCategoryItems,
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

                                                    if (draggingKey.value != id) continue
                                                    if (!change.pressed) continue

                                                    val dx = change.positionChange().x
                                                    if (dx != 0f) onHorizontalReparentHint(id, dx)
                                                }
                                            }
                                        }
                                        .detectReorderAfterLongPress(reorderState)

                                    CategoryRow(
                                        item = item,
                                        computedLevel = effectiveLevel(id),
                                        onToggleExpand = viewModel::toggleExpand,
                                        modifier = rowDragModifier,
                                        onOpenMenu = { id -> viewModel.setMenuCategoryId(id) }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }


    if (showAddCategory) {
        AddCategoryDialog(
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
            onPickIcon = { showIconPicker = true },
            onPickColor = { showColorPicker = true },
            onNameChange = viewModel::setDraftName,
            onDescriptionChange = { desc ->

                viewModel.setDraftDescription(desc)
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
            taskEntities = state.taskEntities,
            taskMiniUis = tasksWithSchedule.map { tws ->
                TaskMiniUi(
                    id = tws.taskEntity.id ?: -1,
                    title = tws.taskEntity.name,
                    isDone = tws.taskEntity.isCompleted,
                    hasSchedule = (tws.schedule != null),
                    parentTaskId = tws.taskEntity.parentTaskId,
                    siblingIndex = tws.taskEntity.siblingIndex,
                    priority = tws.taskEntity.priority,

                    )
            },
            onDismiss = {
                sheetMode = CategorySheetMode.OVERVIEW
                viewModel.setMenuCategoryId(null)
            },
            onClickPickIcon = { showEditIconPicker = true },
            onClickPickColor = { showEditColorPicker = true },
            onClickRename = {
                showRenameDialog = true
                renameText = menuCategory.name
                renameError = null
            },
            onClickDelete = {
                showDeleteConfirm = true
            },
            tasksRenderList = state.taskRenderItems,
            onAddTask = {
                // رفتن به TaskScreen در حالت Add برای همین دسته
                navController.navigate(AppRoutes.taskAdd(menuCategory.categoryId!!))
            },
            onClickTask = { taskId ->
                // رفتن به TaskScreen در حالت Edit
                navController.navigate(AppRoutes.taskEdit(taskId))
            },
            scheduledCount = scheduledCount,
            sheetMode = sheetMode,
            onChangeMode = { sheetMode = it },
            toggleTaskCompleted = { taskId, _ ->
                viewModel.toggleTaskCompletedCascade(taskId)
            },
            deleteTask = { taskId: Int ->
                viewModel.deleteTask(taskId)
            },
            applyTaskDragResult = { draggedId, oldParentId, newParentId, categoryId, currentList ->
                viewModel.applyTaskDragResult(
                    draggedId,
                    oldParentId,
                    newParentId,
                    categoryId,
                    currentList
                )
            },
            onDragEndRestoreExpandForTask = { viewModel.onDragEndRestoreExpandForTask() },
            onDragStartMaybeCollapseForTask = { viewModel.onDragStartMaybeCollapseForTask(it) },
            toggleExpandForTask = { viewModel.toggleExpandForTask(it) },
            onCompleteAll = { viewModel.completeAllTasks(it) },
            onUncompletedAll = { viewModel.uncompletedAllTasks(it) },
            onDeleteCompleted = { viewModel.deleteCompletedTasks(it) },
            onDeleteAll = { viewModel.deleteAllTasks(it) },
            onDescriptionChange = { categoryId, description ->
                viewModel.updateCategoryDescription(categoryId, description)
            }

        )
    }

    if (showDeleteConfirm && menuCategoryId != null) {
        AlertDialog(
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
        AlertDialog(
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


}


//>>>>>>>>>>>>>>>> Sheets <<<<<<<<<<<<<<<<<<

@Composable
private fun CategoryOptionsSideSheet(
    category: CategoryEntity,
    taskEntities: List<TaskEntity>,
    taskMiniUis: List<TaskMiniUi>,
    tasksRenderList: List<TaskRenderItem>,
    onDismiss: () -> Unit,
    onClickPickIcon: () -> Unit,
    onClickPickColor: () -> Unit,
    onClickRename: () -> Unit,
    onClickDelete: () -> Unit,
    onAddTask: () -> Unit,
    onClickTask: (Int) -> Unit,
    scheduledCount: Int,
    sheetMode: CategorySheetMode,
    onChangeMode: (CategorySheetMode) -> Unit,
    toggleTaskCompleted: (taskId: Int, done: Boolean) -> Unit,
    deleteTask: (taskId: Int) -> Unit,
    applyTaskDragResult: (
        draggedId: Int,
        oldParentId: Int?,
        newParentId: Int?,
        categoryId: Int,
        currentList: List<TaskRenderItem>
    ) -> Unit,
    onDragEndRestoreExpandForTask: () -> Unit,
    onDragStartMaybeCollapseForTask: (taskId: Int) -> Unit,
    toggleExpandForTask: (taskId: Int) -> Unit,
    onDescriptionChange: (categoryId: Int?, description: String) -> Unit,
    onCompleteAll: (Int) -> Unit,
    onUncompletedAll: (Int) -> Unit,
    onDeleteCompleted: (Int) -> Unit,
    onDeleteAll: (Int) -> Unit,
) {
    val surfaceColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.10f)
    Box(
        modifier = Modifier
            .fillMaxSize()
            .zIndex(100f)
    ) {
        // ✅ Backdrop کلیکی برای dismiss
        Box(
            modifier = Modifier
                .fillMaxSize()
//                .background(Color.Black.copy(alpha = 0.45f))
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                ) { onDismiss() }
        )
        Surface(
            color = MaterialTheme.colorScheme.background,
            tonalElevation = 0.dp,
            shadowElevation = 12.dp,
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .fillMaxHeight()
                .fillMaxWidth(0.73f)
//                .background(MaterialTheme.colorScheme.surface)
                .drawBehind {
                    val strokeWidth = 2.dp.toPx()
                    drawLine(
                        color = surfaceColor,
                        start = Offset(size.width, 0f),
                        end = Offset(0f, 0f),
                        strokeWidth = strokeWidth
                    )
                }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                when (sheetMode) {
                    CategorySheetMode.OVERVIEW -> {


                        CategorySheetOverviewMode(
                            category = category,
                            tasksCount = taskEntities.size,
                            scheduledCount = scheduledCount,
                            onChangeMode = onChangeMode,
                            onClickPickIcon = onClickPickIcon,
                            onClickPickColor = onClickPickColor,
                            onClickRename = onClickRename,
                            onDescriptionChange = { description ->
                                onDescriptionChange(category.categoryId, description)
                            },
                            onClickDelete = onClickDelete
                        )

                    }

                    CategorySheetMode.TASKS -> {
                        // ✅ حالت Tasks: کل سایدشیت عوض میشه
                        CategorySheetTasksMode(
                            category = category,
                            taskEntities = taskEntities,
                            taskMiniList = taskMiniUis,
                            tasksRenderList = tasksRenderList,
                            onBack = { onChangeMode(CategorySheetMode.OVERVIEW) },
                            onAddTask = onAddTask,
                            onClickTask = onClickTask,
                            toggleTaskCompleted = { taskId: Int, done: Boolean ->
                                toggleTaskCompleted(taskId, done)
                            },
                            deleteTask = { taskId: Int ->
                                deleteTask(taskId)
                            },
                            applyTaskDragResult = { draggedId, oldParentId, newParentId, categoryId, currentList ->
                                applyTaskDragResult(
                                    draggedId,
                                    oldParentId,
                                    newParentId,
                                    categoryId,
                                    currentList
                                )
                            },
                            onDragEndRestoreExpandForTask = { onDragEndRestoreExpandForTask() },
                            onDragStartMaybeCollapseForTask = { onDragStartMaybeCollapseForTask(it) },
                            toggleExpandForTask = { toggleExpandForTask(it) },
                            onCompleteAll = { onCompleteAll(category.categoryId!!) },
                            onUncompletedAll = { onUncompletedAll(category.categoryId!!) },
                            onDeleteCompleted = { onDeleteCompleted(category.categoryId!!) },
                            onDeleteAll = { onDeleteAll(category.categoryId!!) },
                        )
                    }
                }
            }
        }


    }
}

@Composable
private fun CategorySheetOverviewMode(
    category: CategoryEntity,
    tasksCount: Int,
    scheduledCount: Int,
    onChangeMode: (CategorySheetMode) -> Unit,
    onClickPickIcon: () -> Unit,
    onClickPickColor: () -> Unit,
    onClickRename: () -> Unit,
    onDescriptionChange: (String) -> Unit,
    onClickDelete: () -> Unit,
) {

    var showEditDescriptionDialog by rememberSaveable { mutableStateOf(false) }


    // ✅ هدر کتگوری
    Surface(
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 0.dp,
        shadowElevation = 8.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
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
            Spacer(Modifier.width(14.dp))
            Text(
                text = category.name,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.weight(1f)
            )
        }
    }

    // ✅ بدنه اسکرول‌دار
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
    ) {
        item {
            SheetTasksCompactRow(
                count = tasksCount,
                onClick = { onChangeMode(CategorySheetMode.TASKS) }
            )

            SheetStatRow(
                icon = Icons.Filled.Event,
                text = "$scheduledCount scheduled activities"
            )
            SheetStatRow(
                icon = Icons.Filled.History,
                text = "0 logged activities"
            )
            SheetStatRow(icon = Icons.Filled.Description, text = "0 notes")
            SheetStatRow(
                icon = Icons.Filled.AttachFile,
                text = "0 attachments",
                showDivider = false
            )
        }

        item {
            var paramsExpanded by rememberSaveable { mutableStateOf(true) }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(colorFromHex(category.color).copy(alpha = 0.15f))
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
                SheetActionRow(
                    leading = {
                        Icon(
                            iconFromKey(category.iconName),
                            contentDescription = null,
                            tint = colorFromHex(category.color)
                        )
                    },
                    title = "Icon",
                    trailing = { Icon(Icons.Filled.GridView, contentDescription = null) },
                    onClick = onClickPickIcon
                )

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

                SheetActionRow(
                    leading = { Icon(Icons.Filled.Edit, contentDescription = null) },
                    title = "Rename",
                    onClick = onClickRename
                )

                SheetActionRow(
                    leading = { Icon(Icons.Filled.Description, contentDescription = null) },
                    title = "description",
                    onClick = { showEditDescriptionDialog = true }
                )

                SheetActionRow(
                    leading = { Icon(Icons.Filled.Delete, contentDescription = null) },
                    title = "Delete",
                    titleColor = MaterialTheme.colorScheme.error,
                    onClick = onClickDelete
                )
            }

            Spacer(Modifier.height(24.dp))
        }
    }

    if (showEditDescriptionDialog) {
        AddEditDescriptionDialog(
            title = "Add Description",
            value = category.description,
            onValueChange = onDescriptionChange,
            onDismiss = { showEditDescriptionDialog = false },
            onConfirm = { showEditDescriptionDialog = false }
        )
    }
}


@Composable
private fun CategorySheetTasksMode(
    category: CategoryEntity,
    taskEntities: List<TaskEntity>,
    taskMiniList: List<TaskMiniUi>,
    tasksRenderList: List<TaskRenderItem>,
    onBack: () -> Unit,
    onAddTask: () -> Unit,
    onClickTask: (Int) -> Unit,
    toggleTaskCompleted: (taskId: Int, done: Boolean) -> Unit,
    deleteTask: (taskId: Int) -> Unit,
    applyTaskDragResult: (
        draggedId: Int,
        oldParentId: Int?,
        newParentId: Int?,
        categoryId: Int,
        currentList: List<TaskRenderItem>
    ) -> Unit,
    onDragEndRestoreExpandForTask: () -> Unit,
    onDragStartMaybeCollapseForTask: (taskId: Int) -> Unit,
    toggleExpandForTask: (taskId: Int) -> Unit,
    onCompleteAll: () -> Unit,
    onUncompletedAll: () -> Unit,
    onDeleteCompleted: () -> Unit,
    onDeleteAll: () -> Unit,
) {


    var dragging by remember { mutableStateOf(false) }
    val listState = remember { mutableStateOf(tasksRenderList) }
    Log.i("TEST", "listState=$listState")

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

    val childrenByParent = remember(taskMiniList) {
        taskMiniList
            .filter { true }
            .groupBy { it.parentTaskId }
            .mapValues { entry -> entry.value.map { it.id } }
    }

    fun normalizeParent(p: Int?): Int? = if (p == ROOT) null else p

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

    fun effectiveParentId(item: TaskRenderItem): Int? {
        val id = item.task.id
        return pendingParentById[id] ?: item.task.parentTaskId
    }


    // وقتی درگ نداریم، با state.sync شو
    LaunchedEffect(tasksRenderList) {
        if (!dragging) listState.value = tasksRenderList
    }

    val draggingKey = remember { mutableStateOf<Int?>(null) }

// کمک: دسترسی سریع به آیتم‌ها با id (از لیست واقعی uiState)
    val entityById = remember(taskEntities) {
        taskEntities.associateBy { it.id }
    }

    val reorderState = rememberReorderableLazyListState(
        onMove = { from, to ->
            dragging = true

            val draggedId = from.key as? Int ?: return@rememberReorderableLazyListState


            val fromIndex = from.index
            val list = listState.value.toMutableList()
            if (list.isEmpty()) return@rememberReorderableLazyListState

            val max = list.lastIndex
            val toIndex = to.index.coerceIn(0, max)
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
            val categoryId = category.categoryId ?: 1

            applyTaskDragResult(draggedId, oldParent, newParent, categoryId, listState.value)

            onDragEndRestoreExpandForTask()

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
                listState.value.firstOrNull { it.task.id == key }?.task?.parentTaskId
            pendingParentId.value = null
            onDragStartMaybeCollapseForTask(key)
        }
    }

    val allById = remember(tasksRenderList) {
        tasksRenderList.associateBy { it.task.id }
    }


    // parent موثر: اگر pending داریم از آن استفاده کن
    fun effectiveParentId(id: Int): Int? {
        val p = pendingParentById[id] ?: entityById[id]?.parentTaskId
        return normalizeParent(p)
    }

    // محاسبه level بر اساس parent موثر (حداکثر 4)
    fun effectiveLevel(id: Int): Int {
        var level = 1
        var curParent = effectiveParentId(id)
        var guard = 0
        while (curParent != null && guard < 10) {
            level++
            curParent = effectiveParentId(curParent)
            guard++
        }

        return level.coerceAtMost(4)
    }

    fun tryIndent(id: Int) {
        // child شدن: parent = آیتم قبلی بالای خودش در همون لیست که level < 4
        val list = listState.value
        val idx = list.indexOfFirst { it.task.id == id }
        if (idx <= 0) return
        val prev = list[idx - 1]
        val prevId = prev.task.id

        val parentLevel = effectiveLevel(prevId)
        if (parentLevel >= 4) return


        // ✅ جلوگیری از cycle: نباید زیر بچه‌ی خودش بره
        if (isDescendantOfDragged(candidateParentId = prevId, draggedId = id)) return


        if (effectiveLevel(prevId) >= 4) return
        pendingParentById[id] = prevId
    }

    fun tryOutdent(id: Int) {
        val current = allById[id] ?: return

        val currentParent = pendingParentById[id] ?: current.task.parentTaskId
        // اگر همین الان ریشه است، دیگه چیزی برای outdent نداریم
        if (currentParent == null) return

        val parentItem = allById[currentParent] ?: return
        val newParent = parentItem.task.parentTaskId

        // ✅ ریشه را با -1 نگه دار (نه null)
        pendingParentById[id] = newParent
    }


    val onHorizontalReparentHint: (Int, Float) -> Unit = reparent@{ id, deltaX ->
        val acc = (dragOffsetXById[id] ?: 0f) + deltaX
        dragOffsetXById[id] = acc

        val now = SystemClock.uptimeMillis()
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

    var expandedForAll by rememberSaveable { mutableStateOf(true) }

    var sortMode by rememberSaveable { mutableStateOf(TaskSortMode.NONE) }

    LaunchedEffect(sortMode, tasksRenderList) {
        if (!dragging) {
            listState.value = when (sortMode) {
                TaskSortMode.NONE -> tasksRenderList
                TaskSortMode.BY_NAME -> tasksRenderList.sortedBy { it.task.title.lowercase() }
                TaskSortMode.BY_PRIORITY -> tasksRenderList.sortedByDescending { it.task.priority }
                TaskSortMode.BY_COMPLETED -> tasksRenderList.sortedBy { it.task.isDone } // انجام نشده‌ها بالا
            }
        }
    }





    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {

        // ردیف اول: Add task + آیکون کتگوری + Search
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surfaceContainerHigh)
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(onClick = onBack) { Text("Back") }

            Spacer(Modifier.width(8.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onAddTask() },
                verticalAlignment = Alignment.CenterVertically
            ) {

                Icon(
                    imageVector = iconFromKey(category.iconName),
                    contentDescription = null,
                    tint = colorFromHex(category.color),
                    modifier = Modifier.size(20.dp)
                )
                Spacer(Modifier.width(10.dp))

                Text(
                    text = "Add task",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier
                        .weight(1f)

                )
            }

        }

        HorizontalDivider()

        // ردیف دوم: آیکون تسک، تعداد، Tasks، اکسپند، سه نقطه
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surfaceContainerHigh)
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Filled.Task,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.width(10.dp))

            Text("${taskEntities.size}", color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.width(10.dp))

            Text("Tasks", modifier = Modifier.weight(1f))

            IconButton(onClick = { expandedForAll = !expandedForAll }) {
                Icon(
                    imageVector = if (expandedForAll) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                    contentDescription = "expand"
                )
            }

            TasksOptionsMenu(
                sortMode = sortMode,
                onChangeSort = { sortMode = it },
                onCompleteAll = onCompleteAll,
                onUncompletedAll = onUncompletedAll,
                onDeleteCompleted = onDeleteCompleted,
                onDeleteAll = onDeleteAll
            )


        }
        HorizontalDivider(thickness = 0.5.dp)

        var pendingDeleteTaskId by rememberSaveable { mutableStateOf<Int?>(null) }
        var pendingDeleteTaskTitle by rememberSaveable { mutableStateOf("") }


        androidx.compose.animation.AnimatedVisibility(
            visible = expandedForAll,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {

            LazyColumn(
                state = reorderState.listState, // ✅ مهم
                modifier = Modifier
                    .fillMaxSize()
                    .reorderable(reorderState)
            ) {
                items(
                    items = listState.value,
                    key = { it.task.id }
                ) { item ->
                    val id = item.task.id

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

                                            // فقط وقتی خود این آیتم در حال drag واقعی است
                                            if (draggingKey.value != id) continue
                                            if (!change.pressed) continue

                                            val dx = change.positionChange().x
                                            if (dx != 0f) onHorizontalReparentHint(id, dx)
                                        }
                                    }
                                }

                                .detectReorderAfterLongPress(reorderState) // ✅ کل ردیف draggable
                            val isDragActive = reorderState.draggingItemKey != null

                            val dismissState =
                                rememberSwipeToDismissBoxState(
                                    confirmValueChange = { value ->
                                        // فقط سوایپ به چپ (EndToStart) را به حذف تبدیل کن
                                        if (!isDragActive && value == SwipeToDismissBoxValue.EndToStart) {
                                            pendingDeleteTaskId = id
                                            pendingDeleteTaskTitle = item.task.title
                                        }
                                        // ❗ همیشه false تا آیتم واقعاً dismiss (حذف نمایشی) نشود
                                        false
                                    }
                                )

                            SwipeToDismissBox(
                                state = dismissState,
                                enableDismissFromStartToEnd = false,
                                enableDismissFromEndToStart = !isDragActive,
                                backgroundContent = {
                                    // پس‌زمینه‌ی حذف (ساده)
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .background(MaterialTheme.colorScheme.error.copy(alpha = 0.15f))
                                            .padding(horizontal = 16.dp),
                                        contentAlignment = Alignment.CenterEnd
                                    ) {
                                        Icon(Icons.Filled.Delete, contentDescription = "delete")
                                    }
                                }
                            ) {

                                TaskRow(
                                    item = item,
                                    computedLevel = effectiveLevel(id),
                                    onToggleExpand = { toggleExpandForTask(it) },
                                    onClickTask = { onClickTask(it) },
                                    onToggleDone = { taskId ->
                                        val cur =
                                            listState.value.firstOrNull { it.task.id == taskId }?.task
                                                ?: return@TaskRow
                                        toggleTaskCompleted(taskId, !cur.isDone)  // ✅ معکوس
                                    },
                                    modifier = rowDragModifier,
                                )

                                HorizontalDivider(thickness = 0.5.dp) // ✅ خط باریک

                            }

                        }
                    }
                }
            }

        }

        if (pendingDeleteTaskId != null) {
            AlertDialog(
                onDismissRequest = { pendingDeleteTaskId = null },
                title = { Text("Delete task?") },
                text = { Text("آیا از حذف این تسک مطمئن هستی؟\n\n${pendingDeleteTaskTitle}") },
                confirmButton = {
                    TextButton(onClick = {
                        val tid = pendingDeleteTaskId!!
                        deleteTask(tid)          // همون callback که به VM وصل است
                        pendingDeleteTaskId = null
                    }) { Text("Delete") }
                },
                dismissButton = {
                    TextButton(onClick = { pendingDeleteTaskId = null }) { Text("Cancel") }
                }
            )
        }


    }
}


//>>>>>>>>>>>>>>>> Dialogs <<<<<<<<<<<<<<<<<<


@Composable
private fun AddCategoryDialog(
    draft: CategoryDraft,
    parentName: String,
    parentIconName: String,
    parentColorHex: String,
    onDismiss: () -> Unit,
    onPickParent: () -> Unit,
    onPickIcon: () -> Unit,
    onPickColor: () -> Unit,
    onNameChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit, // ✅ جدید (فعلاً اگر نداری، می‌تونی خالی پاس بدی)
    onConfirm: () -> Unit,
) {

    var showAddDescription by rememberSaveable { mutableStateOf(false) }

    DimmedDialog(
        onDismiss = onDismiss,
        modifier = Modifier
            .fillMaxWidth(0.92f)
            .fillMaxHeight(0.85f)
            .background(MaterialTheme.colorScheme.surface, shape = MaterialTheme.shapes.extraLarge),
    ) {
        Column(modifier = Modifier.fillMaxSize()) {

            //  Top bar
            AddEditeDialogTopBar(
                title = "New category",
                onNavigationClick = onDismiss,
                actions = {
                    IconButton(
                        onClick = { onConfirm() },
                        enabled = draft.name.isNotBlank()
                    ) {
                        Icon(Icons.Filled.Check, contentDescription = "Check")
                    }
                }
            )

            //title
            AddEditeDialogTextField(
                value = draft.name,
                onValueChange = onNameChange,
                hint = "Name *",
            )

            //parent
            AddEditeDialogRow(
                onClick = onPickParent,
                content = {
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
                },
            )

            //icon
            AddEditeDialogRow(
                onClick = onPickIcon,
                content = {
                    Text("Icon", modifier = Modifier.weight(1f))

                    Icon(
                        imageVector = iconFromKey(draft.iconName),
                        contentDescription = draft.iconName,
                        tint = colorFromHex(draft.color),   // ✅ مهم
                        modifier = Modifier.size(24.dp)
                    )
                },
            )

            //Color
            AddEditeDialogRow(
                onClick = onPickColor,
                content = {
                    Text("Color", modifier = Modifier.weight(1f))

                    Box(
                        modifier = Modifier
                            .size(22.dp)
                            .border(1.dp, Color.Black.copy(alpha = 0.15f), CircleShape)
                            .background(colorFromHex(draft.color), CircleShape)
                    )
                },
            )

            // Description
            AddEditeDialogRow(
                onClick = { showAddDescription = true },
                content = {
                    Text("Add description", modifier = Modifier.weight(1f))

                },
            )

            if (showAddDescription) {
                AddEditDescriptionDialog(
                    title = "Add Description",
                    value = draft.description,
                    onValueChange = onDescriptionChange,
                    onDismiss = { showAddDescription = false },
                    onConfirm = { showAddDescription = false }
                )
            }


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
private fun CategoryRow(
    item: CategoryRenderItem,
    computedLevel: Int,
    onToggleExpand: (Int) -> Unit,
    modifier: Modifier = Modifier,
    onOpenMenu: (Int) -> Unit,
) {
    val id = item.category.categoryId ?: return
    val indent = (computedLevel - 1).coerceAtLeast(0) * 16
    val bg = containerColorForLevel(computedLevel)

    Surface(
        shape = RoundedCornerShape(
            bottomStart = 12.dp   // 👈 فقط پایین راست گرد
        ),
        color = bg,
        modifier = modifier
            .fillMaxWidth()
            .padding(start = indent.dp)
    ) {
        ListItem(
            colors = ListItemDefaults.colors(containerColor = Color.Transparent),
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
                                imageVector = if (item.isExpanded)
                                    Icons.Filled.ExpandLess
                                else
                                    Icons.Filled.ExpandMore,
                                contentDescription = "expand"
                            )
                        }
                    }

                    IconButton(onClick = { onOpenMenu(id) }) {
                        Icon(Icons.Filled.MoreVert, contentDescription = "menu")
                    }
                }
            }
        )
    }

    HorizontalDivider(
        thickness = 0.5.dp,
        modifier = Modifier.padding(start = indent.dp)
    )
}


@Composable
private fun TaskRow(
    item: TaskRenderItem,
    computedLevel: Int,
    onToggleExpand: (Int) -> Unit,
    onClickTask: (Int) -> Unit,
    onToggleDone: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val id = item.task.id
    val indent = ((computedLevel - 1).coerceAtLeast(0) * 16).dp
    val bg = containerColorForLevel(computedLevel)

    val canHaveChildren = computedLevel < 4
    val showExpand = item.hasChildren && canHaveChildren

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(52.dp) // ✅ ارتفاع ثابت
            .background(bg)
            .clickable { onClickTask(id) }
            .padding(start = indent, end = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Spacer(Modifier.width(6.dp))


        // ✅ دایره done
        Box(
            modifier = Modifier
                .size(22.dp)
                .border(
                    2.dp,
                    MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.35f),
                    CircleShape
                )
                .background(
                    color = if (item.task.isDone)
                        MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.55f)
                    else Color.Transparent,
                    shape = CircleShape
                )
                .clickable(
                    // ✅ مهم: کلیک دایره به کلیک ردیف نره
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                ) { onToggleDone(id) }
        )


        // ✅ Priority marker
        val (pText, pColor) = when (item.task.priority) {
            1 -> "*" to Color(0xFF2E7D32) // سبز
            2 -> "!" to Color(0xFFF9A825) // زرد
            3 -> "!!" to Color(0xFFC62828) // قرمز
            else -> "" to Color.Unspecified
        }
        if (pText.isNotBlank()) {
            Spacer(Modifier.width(8.dp))
            Text(
                text = pText,
                color = pColor,
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(Modifier.width(8.dp))
        } else {
            Spacer(Modifier.width(12.dp))
        }

        Spacer(Modifier.width(12.dp))


        // ✅ عنوان (یک خط، ellipsis)
        Text(
            text = item.task.title,
            modifier = Modifier.weight(1f),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )


        // ✅ schedule icon (اگر نبود، فضا نمی‌گیرد)
        if (item.task.hasSchedule) {
            Spacer(Modifier.width(8.dp))
            Icon(
                imageVector = Icons.Filled.Event,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.error
            )
        }

        Spacer(Modifier.width(8.dp))


        // ✅ جایگاه expand همیشه رزرو می‌شود تا چیدمان ثابت بماند
        // اندازه‌ی IconButton معمولاً 48.dp است، پس اینجا دقیقاً همان را رزرو می‌کنیم.
        Box(
            modifier = Modifier.size(48.dp),
            contentAlignment = Alignment.Center
        ) {
            if (showExpand) {
                IconButton(
                    onClick = { onToggleExpand(id) },
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(
                        imageVector = if (item.isExpanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                        contentDescription = "expand"
                    )
                }
            } else {
                // هیچ چیز نمایش نده، ولی فضا محفوظ است ✅
            }
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

@Composable
private fun ChooseIconDialog(
    selectedKey: String,
    onDismiss: () -> Unit,
    onPick: (IconOption) -> Unit,
) {
    val sections = remember { buildIconSections() }
    val expanded = remember {
        mutableStateMapOf<String, Boolean>().apply {
            sections.forEach { put(it.title, true) }
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
                        onToggle = { expanded[section.title] = expanded[section.title] != true }
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
private fun ChooseColorDialog(
    initialHex: String,
    onDismiss: () -> Unit,
    onConfirm: (hex: String) -> Unit
) {
    val colorColumns = remember { buildCategoryColorMatrix() }

    val defaultHex = colorColumns.firstOrNull()
        ?.main
        ?.hex
        ?: "#F44336"

    var selectedHex by remember {
        mutableStateOf(initialHex.ifBlank { defaultHex })
    }

    DimmedDialog(
        onDismiss = onDismiss,
        modifier = Modifier
            .fillMaxWidth(0.94f)
            .fillMaxHeight(0.84f)
            .background(
                MaterialTheme.colorScheme.surface,
                shape = MaterialTheme.shapes.extraLarge
            ),
        dimAlpha = 0.6f,
        dismissOnBackdropClick = true
    ) {
        Column(modifier = Modifier.fillMaxSize()) {

            AddEditeDialogTopBar(
                title = "Choose color",
                onNavigationClick = onDismiss,
                actions = {
                    IconButton(
                        onClick = { onConfirm(selectedHex) }
                    ) {
                        Icon(Icons.Filled.Check, contentDescription = "Confirm color")
                    }
                }
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 12.dp, vertical = 14.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "رنگ اصلی را از ردیف بالا انتخاب کن؛ طیف‌های همان رنگ در همان ستون پایین آمده‌اند.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Right
                )

                Spacer(Modifier.height(2.dp))

                // ردیف اول: رنگ‌های اصلی
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    colorColumns.forEach { column ->
                        CategoryColorDot(
                            cell = column.main,
                            selected = column.main.hex.equals(selectedHex, ignoreCase = true),
                            size = 44,
                            onClick = { selectedHex = column.main.hex }
                        )
                    }
                }

                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 6.dp),
                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.7f)
                )

                // ده ردیف طیف‌ها
                repeat(10) { shadeIndex ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        colorColumns.forEach { column ->
                            val cell = column.shades[shadeIndex]

                            CategoryColorDot(
                                cell = cell,
                                selected = cell.hex.equals(selectedHex, ignoreCase = true),
                                size = 38,
                                onClick = { selectedHex = cell.hex }
                            )
                        }
                    }
                }
            }

            HorizontalDivider()

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .border(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.45f),
                            shape = CircleShape
                        )
                        .background(colorFromHex(selectedHex), CircleShape)
                )

                Spacer(Modifier.width(10.dp))

                Text(
                    text = selectedHex.uppercase(),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(Modifier.weight(1f))

                TextButton(onClick = onDismiss) {
                    Text("CANCEL")
                }

                Spacer(Modifier.width(8.dp))

                TextButton(onClick = { onConfirm(selectedHex) }) {
                    Text("OK")
                }
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
private fun SheetTasksCompactRow(
    count: Int,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            Icons.Filled.Task,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.width(12.dp))
        Text("$count", color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.width(8.dp))
        // "Tasks" چسبیده به تعداد
        Text("Tasks", style = MaterialTheme.typography.bodyLarge)


    }
    HorizontalDivider(thickness = 0.5.dp, modifier = Modifier.padding(start = 48.dp))
}

@Composable
private fun TasksOptionsMenu(
    sortMode: TaskSortMode,
    onChangeSort: (TaskSortMode) -> Unit,
    onCompleteAll: () -> Unit,
    onUncompletedAll: () -> Unit,
    onDeleteCompleted: () -> Unit,
    onDeleteAll: () -> Unit
) {

    var menuOpen by remember { mutableStateOf(false) }
    var sortSubOpen by remember { mutableStateOf(false) }

    Box {

        IconButton(onClick = { menuOpen = true }) {
            Icon(Icons.Filled.MoreVert, contentDescription = "tasks menu")
        }

        // ====== منوی اصلی ======
        DropdownMenu(
            expanded = menuOpen,
            onDismissRequest = {
                menuOpen = false
                sortSubOpen = false
            }
        ) {

            // ===== Sort =====
            DropdownMenuItem(
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.AutoMirrored.Filled.Sort, contentDescription = null)
                        Spacer(Modifier.width(10.dp))
                        Text("Sort")
                    }
                },
                trailingIcon = {
                    Icon(Icons.AutoMirrored.Filled.ArrowRight, contentDescription = null)
                },
                onClick = { sortSubOpen = true }
            )

            HorizontalDivider()

            DropdownMenuItem(
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.DoneAll, contentDescription = null)
                        Spacer(Modifier.width(10.dp))
                        Text("Complete all")
                    }
                },
                onClick = {
                    menuOpen = false
                    onCompleteAll()
                }
            )

            DropdownMenuItem(
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.RemoveDone, contentDescription = null)
                        Spacer(Modifier.width(10.dp))
                        Text("Uncomplete all")
                    }
                },
                onClick = {
                    menuOpen = false
                    onUncompletedAll()
                }
            )

            HorizontalDivider()

            DropdownMenuItem(
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.DeleteSweep, contentDescription = null)
                        Spacer(Modifier.width(10.dp))
                        Text("Delete completed")
                    }
                },
                onClick = {
                    menuOpen = false
                    onDeleteCompleted()
                }
            )

            DropdownMenuItem(
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.Delete, contentDescription = null)
                        Spacer(Modifier.width(10.dp))
                        Text("Delete all")
                    }
                },
                onClick = {
                    menuOpen = false
                    onDeleteAll()
                }
            )
        }

        // ====== ساب منوی Sort ======
        DropdownMenu(
            expanded = menuOpen && sortSubOpen,
            onDismissRequest = { sortSubOpen = false },
            offset = DpOffset(x = 180.dp, y = 0.dp)
        ) {

            SortMenuItem(
                title = "By name",
                selected = sortMode == TaskSortMode.BY_NAME
            ) {
                onChangeSort(TaskSortMode.BY_NAME)
                menuOpen = false
                sortSubOpen = false
            }

            SortMenuItem(
                title = "By priority",
                selected = sortMode == TaskSortMode.BY_PRIORITY
            ) {
                onChangeSort(TaskSortMode.BY_PRIORITY)
                menuOpen = false
                sortSubOpen = false
            }

            SortMenuItem(
                title = "By completed",
                selected = sortMode == TaskSortMode.BY_COMPLETED
            ) {
                onChangeSort(TaskSortMode.BY_COMPLETED)
                menuOpen = false
                sortSubOpen = false
            }

            SortMenuItem(
                title = "No sort",
                selected = sortMode == TaskSortMode.NONE
            ) {
                onChangeSort(TaskSortMode.NONE)
                menuOpen = false
                sortSubOpen = false
            }
        }
    }
}

@Composable
private fun SortMenuItem(
    title: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    DropdownMenuItem(
        text = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = if (selected)
                        Icons.Filled.RadioButtonChecked
                    else
                        Icons.Filled.RadioButtonUnchecked,
                    contentDescription = null
                )
                Spacer(Modifier.width(10.dp))
                Text(title)
            }
        },
        onClick = onClick
    )
}


@Composable
private fun SheetStatRow(
    icon: ImageVector,
    text: String,
    showDivider: Boolean = true
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.width(12.dp))
        Text(text)
    }
    if (showDivider) {
        HorizontalDivider(thickness = 0.5.dp, modifier = Modifier.padding(start = 48.dp))
    }

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
            .padding(horizontal = 16.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(Modifier.size(26.dp), contentAlignment = Alignment.Center) { leading() }
        Spacer(Modifier.width(12.dp))
        Text(title, color = titleColor, modifier = Modifier.weight(1f))
        if (trailing != null) {
            Box(Modifier.size(24.dp), contentAlignment = Alignment.Center) { trailing() }
        }
    }
    HorizontalDivider(thickness = 0.5.dp, modifier = Modifier.padding(start = 48.dp))
}

@Composable
private fun EmptyCategoryState(
    onAddCategoryClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            tonalElevation = 3.dp,
            color = MaterialTheme.colorScheme.surfaceColorAtElevation(2.dp)
        ) {
            Column(
                modifier = Modifier.padding(22.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Task,
                    contentDescription = null,
                    modifier = Modifier.size(48.dp),
                    tint = MaterialTheme.colorScheme.primary
                )

                Text(
                    text = "هنوز دسته‌ای ساخته نشده",
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.titleMedium
                )

                Text(
                    text = "برای ساخت تسک، اول باید یک دسته‌بندی بسازی. تسک‌ها در CompoundEffect بدون دسته‌بندی ساخته نمی‌شوند.",
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Button(
                    onClick = onAddCategoryClick,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("ساخت اولین دسته")
                }
            }
        }
    }
}

@Composable
private fun CategoryColorDot(
    cell: CategoryColorCell,
    selected: Boolean,
    size: Int,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(size.dp)
            .border(
                width = if (selected) 3.dp else 1.dp,
                color = if (selected) {
                    MaterialTheme.colorScheme.onSurface
                } else {
                    MaterialTheme.colorScheme.outline.copy(alpha = 0.28f)
                },
                shape = CircleShape
            )
            .padding(if (selected) 4.dp else 2.dp)
            .background(cell.color, CircleShape)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        if (selected) {
            Icon(
                imageVector = Icons.Filled.Check,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size((size * 0.46f).dp)
            )
        }
    }
}


private fun buildCategoryColorMatrix(): List<CategoryColorColumn> {
    fun cell(hex: String): CategoryColorCell {
        return CategoryColorCell(
            hex = hex,
            color = Color(android.graphics.Color.parseColor(hex))
        )
    }

    return listOf(
        // Red
        CategoryColorColumn(
            main = cell("#F44336"),
            shades = listOf(
                cell("#FFEBEE"),
                cell("#FFCDD2"),
                cell("#EF9A9A"),
                cell("#E57373"),
                cell("#EF5350"),
                cell("#E53935"),
                cell("#D32F2F"),
                cell("#C62828"),
                cell("#B71C1C"),
                cell("#7F0000")
            )
        ),

        // Orange
        CategoryColorColumn(
            main = cell("#FF9800"),
            shades = listOf(
                cell("#FFF3E0"),
                cell("#FFE0B2"),
                cell("#FFCC80"),
                cell("#FFB74D"),
                cell("#FFA726"),
                cell("#FB8C00"),
                cell("#F57C00"),
                cell("#EF6C00"),
                cell("#E65100"),
                cell("#993800")
            )
        ),

        // Yellow / Amber
        CategoryColorColumn(
            main = cell("#FFC107"),
            shades = listOf(
                cell("#FFF8E1"),
                cell("#FFECB3"),
                cell("#FFE082"),
                cell("#FFD54F"),
                cell("#FFCA28"),
                cell("#FFB300"),
                cell("#FFA000"),
                cell("#FF8F00"),
                cell("#FF6F00"),
                cell("#9E4500")
            )
        ),

        // Green
        CategoryColorColumn(
            main = cell("#4CAF50"),
            shades = listOf(
                cell("#E8F5E9"),
                cell("#C8E6C9"),
                cell("#A5D6A7"),
                cell("#81C784"),
                cell("#66BB6A"),
                cell("#43A047"),
                cell("#388E3C"),
                cell("#2E7D32"),
                cell("#1B5E20"),
                cell("#003D12")
            )
        ),

        // Blue
        CategoryColorColumn(
            main = cell("#2196F3"),
            shades = listOf(
                cell("#E3F2FD"),
                cell("#BBDEFB"),
                cell("#90CAF9"),
                cell("#64B5F6"),
                cell("#42A5F5"),
                cell("#1E88E5"),
                cell("#1976D2"),
                cell("#1565C0"),
                cell("#0D47A1"),
                cell("#002F6C")
            )
        ),

        // Purple
        CategoryColorColumn(
            main = cell("#9C27B0"),
            shades = listOf(
                cell("#F3E5F5"),
                cell("#E1BEE7"),
                cell("#CE93D8"),
                cell("#BA68C8"),
                cell("#AB47BC"),
                cell("#8E24AA"),
                cell("#7B1FA2"),
                cell("#6A1B9A"),
                cell("#4A148C"),
                cell("#2D004D")
            )
        )
    )
}


//>>>>>>>>>>>>>>>> Utils <<<<<<<<<<<<<<<<<<

private enum class CategorySheetMode { OVERVIEW, TASKS }
enum class ConfirmAction { SAVE_AND_CLOSE, SAVE_AND_CONTINUE }
enum class TaskSortMode { NONE, BY_NAME, BY_PRIORITY, BY_COMPLETED }
private data class CategoryColorColumn(
    val main: CategoryColorCell,
    val shades: List<CategoryColorCell>
)

private data class CategoryColorCell(
    val hex: String,
    val color: Color
)
const val ROOT = -1




