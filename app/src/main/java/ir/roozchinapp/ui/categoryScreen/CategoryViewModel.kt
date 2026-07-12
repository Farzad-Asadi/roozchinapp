package ir.roozchinapp.ui.categoryScreen


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.roozchinapp.data.dataBaseRoom.tables.category.CategoryEntity
import ir.roozchinapp.data.dataBaseRoom.tables.category.CategoryRepository
import ir.roozchinapp.data.dataBaseRoom.tables.reminder.TaskReminderRepository
import ir.roozchinapp.data.dataBaseRoom.tables.task.TaskEntity
import ir.roozchinapp.data.dataBaseRoom.tables.task.TaskRepository
import ir.roozchinapp.data.dataBaseRoom.tables.task.TaskWithSchedule
import ir.roozchinapp.data.dataBaseRoom.tables.taskSchedule.TaskScheduleRepository
import ir.roozchinapp.data.dataClasses.CategoryDraft
import ir.roozchinapp.data.dataClasses.CategoryRenderItem
import ir.roozchinapp.data.dataClasses.CategoryUiState2
import ir.roozchinapp.data.dataClasses.FlattenResult
import ir.roozchinapp.data.dataClasses.TaskMiniUi
import ir.roozchinapp.data.dataClasses.TaskRenderItem
import ir.roozchinapp.data.workManager.ReminderScheduler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject


@HiltViewModel
class CategoryViewModel @Inject constructor(
    private val categoryRepository: CategoryRepository,
    private val taskScheduleRepo: TaskScheduleRepository,
    private val reminderRepo: TaskReminderRepository,
    private val reminderScheduler: ReminderScheduler,
    private val taskRepo: TaskRepository,
) : ViewModel() {



    // این دو تا برای سناریوی “درگ شروع شد/تمام شد”
    private val _dragCollapsedRestoreCategory = MutableStateFlow<Int?>(null)
    private val _dragCollapsedRestoreTask = MutableStateFlow<Int?>(null)
    private val _collapsedIdsCategory = MutableStateFlow<Set<Int>>(emptySet())
    private val _taskCollapsedIds = MutableStateFlow<Set<Int>>(emptySet())

    init {
        viewModelScope.launch {
            categoryRepository.observeAll()
                .map { categories ->
                    categories
                        .asSequence()
                        .filter { category ->
                            category.categoryId != null && !category.isExtended
                        }
                        .mapNotNull { category ->
                            category.categoryId
                        }
                        .toSet()
                }
                .distinctUntilChanged()
                .collect { savedCollapsedIds ->
                    _collapsedIdsCategory.value = savedCollapsedIds
                }
        }
    }

    private val _menuCategoryId = MutableStateFlow<Int?>(null)
    val menuCategoryId = _menuCategoryId.asStateFlow()

    // ✅ لیست تسک‌ها + schedule
    @OptIn(ExperimentalCoroutinesApi::class)
    val tasksWithScheduleForMenu: StateFlow<List<TaskWithSchedule>> =
        menuCategoryId
            .flatMapLatest { id ->
                if (id == null) flowOf(emptyList())
                else taskRepo.observeTasksWithScheduleByCategory(id)
            }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    private val taskMiniUiForMenu: StateFlow<List<TaskMiniUi>> =
        tasksWithScheduleForMenu
            .map { list ->
                list.mapNotNull { tws ->
                    val t = tws.taskEntity
                    val id = t.id ?: return@mapNotNull null

                    TaskMiniUi(
                        id = id,
                        title = t.name,
                        isDone = t.isCompleted,
                        hasSchedule = (tws.schedule != null),
                        parentTaskId = t.parentTaskId,
                        siblingIndex = t.siblingIndex,
                        priority = t.priority
                    )

                }
            }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    @OptIn(ExperimentalCoroutinesApi::class)
    val tasksForMenuCategory: StateFlow<List<TaskEntity>> =
        _menuCategoryId
            .flatMapLatest { id ->
                if (id == null) flowOf(emptyList())
                else taskRepo.observeTasksByCategory(id)
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = emptyList()
            )


    val uiState: StateFlow<CategoryUiState2> =
        combine(
            categoryRepository.observeAll(),
            _collapsedIdsCategory,
            tasksForMenuCategory,     // ✅ لیست خام Task ها
            taskMiniUiForMenu,        // ✅ برای ساخت renderItems تسک‌ها
            _taskCollapsedIds
        ) { categories, catCollapsed, tasks, tasksMini, taskCollapsed ->

            val catFlatten = flattenCategoryTreeWithLevelsAndVisibility(categories, catCollapsed)

            val taskRender = flattenTaskTreeWithLevelsAndVisibility(
                all = tasksMini,
                collapsedIds = taskCollapsed,
                rootParentId = ROOT,
                maxDepth = 4
            )


            CategoryUiState2(
                isLoading = false,
                categories = categories,
                renderItems = catFlatten.items,

                taskEntities = tasks,                 // ✅ اینجا کامل شد
                taskRenderItems = taskRender,  // ✅ اینم مثل قبل

                levelById = catFlatten.levelById
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = CategoryUiState2(
                isLoading = true,
                categories = emptyList(),
                taskEntities = emptyList()
            )
        )



    private val _draft = MutableStateFlow(CategoryDraft())
    val draft = _draft.asStateFlow()


    private val _createResult = MutableStateFlow<CreateResult?>(null)
    val createResult = _createResult.asStateFlow()

    val parentPickerItems: StateFlow<FlattenResult> =
        categoryRepository.observeAll()
            .map { categories ->
                flattenForPickerAllVisible(categories)
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = FlattenResult(emptyList(), emptyMap())
            )



    // ✅ شمارش scheduled (دو روش)
    @OptIn(ExperimentalCoroutinesApi::class)
    val scheduledCountForMenu: StateFlow<Int> =
        menuCategoryId
            .flatMapLatest { id ->
                if (id == null) flowOf(0)
                else taskRepo.observeScheduledCountByCategory(id)
                // یا اگر اون Query رو نذاشتی:
                // else taskRepository.observeTasksWithScheduleByCategory(id).map { list -> list.count { it.schedule != null } }
            }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), 0)












    //کتگوری ها
    fun setDraftName(value: String) {
        _draft.update { it.copy(name = value) }
    }
    fun setDraftDescription(value: String) {
        _draft.update { it.copy(description = value) }
    }
    fun setDraftIconName(value: String) {
        _draft.update { it.copy(iconName = value) }
    }
    fun setDraftColor(value: String) {
        _draft.update { it.copy(color = value) }
    }
    fun createCategoryFromDraft() {
        val d = _draft.value
        if (d.name.isBlank()) {
            _createResult.value = CreateResult.Error("نام گروه را وارد کن")
            return
        }

        viewModelScope.launch {
            val current = uiState.value.categories
            val nextSiblingIndex = current.count { it.parentCategoryId == d.parentId }

            categoryRepository.insertCategory(
                CategoryEntity(
                    name = d.name.trim(),
                    parentCategoryId = d.parentId,
                    iconName = d.iconName,
                    color = d.color,
                    description = d.description,
                    siblingIndex = nextSiblingIndex
                )
            )

            resetDraft()
            _createResult.value = CreateResult.Success
        }
    }
    fun resetDraft() {
        _draft.value = CategoryDraft()
        _createResult.value = null
    }
    fun renameCategory(categoryId: Int, newName: String) {
        val name = newName.trim()
        if (name.isBlank()) return

        viewModelScope.launch {
            val current = uiState.value.categories.firstOrNull { it.categoryId == categoryId } ?: return@launch
            categoryRepository.updateCategory(current.copy(name = name))
        }
    }
    fun updateCategoryIcon(categoryId: Int, iconName: String) {
        viewModelScope.launch {
            val current = uiState.value.categories.firstOrNull { it.categoryId == categoryId } ?: return@launch
            categoryRepository.updateCategory(current.copy(iconName = iconName))
        }
    }
    fun updateCategoryDescription(categoryId: Int?, description: String) {
        viewModelScope.launch {
            if (categoryId==null)return@launch
            val current = uiState.value.categories.firstOrNull { it.categoryId == categoryId } ?: return@launch
            categoryRepository.updateCategory(current.copy(description = description))
        }
    }
    fun updateCategoryColor(categoryId: Int, colorHex: String) {
        viewModelScope.launch {
            val current = uiState.value.categories.firstOrNull { it.categoryId == categoryId } ?: return@launch
            categoryRepository.updateCategory(current.copy(color = colorHex))
        }
    }
    fun applyDragResult(draggedId: Int, oldParentId: Int?, newParentId: Int?, currentList: List<CategoryRenderItem>) {
        viewModelScope.launch {
            val all = uiState.value.categories
            val dragged = all.firstOrNull { it.categoryId == draggedId } ?: return@launch

            val finalNewParent = newParentId ?: dragged.parentCategoryId ?: ROOT

            // ✅ 1) self-parent ممنوع
            if (finalNewParent == draggedId) return@launch

            // ✅ 2) cycle ممنوع
            // ✅ 2) cycle ممنوع (Category) — Root = -1
            val parentById: Map<Int, Int> =
                all.mapNotNull { c ->
                    val id = c.categoryId ?: return@mapNotNull null
                    id to (c.parentCategoryId ?: -1)
                }.toMap()

            if (wouldCreateCycleCategory(draggedId, finalNewParent, parentById)) {
                return@launch
            }


            // parent جدید باید سطحش < 4 باشد
            val parentLevel =
                if (finalNewParent == -1) 1
                else (uiState.value.levelById[finalNewParent] ?: 1)

            if (parentLevel >= 4) return@launch

            // آپدیت parent (اگر تغییر کرده)
            if (finalNewParent != dragged.parentCategoryId) {
                categoryRepository.updateCategory(dragged.copy(parentCategoryId = finalNewParent))
            }

            suspend fun reorderFor(parentId: Int?) {
                val orderedIds = currentList
                    .asSequence()
                    .filter { it.isVisible }
                    .filter { item ->
                        val id = item.category.categoryId ?: return@filter false
                        val p = if (id == draggedId) finalNewParent else item.category.parentCategoryId
                        p == parentId
                    }
                    .mapNotNull { it.category.categoryId }
                    .toList()

                val currentEntities = uiState.value.categories.filter { it.parentCategoryId == parentId }
                val byId = currentEntities.associateBy { it.categoryId }

                orderedIds.forEachIndexed { index, id ->
                    val entity =
                        if (id == draggedId) {
                            // dragged ممکنه هنوز تو uiState با parent قبلی باشه
                            dragged.copy(parentCategoryId = finalNewParent)
                        } else {
                            byId[id] ?: return@forEachIndexed
                        }

                    categoryRepository.updateCategory(entity.copy(siblingIndex = index))
                }
            }



            reorderFor(oldParentId)
            reorderFor(finalNewParent)
        }
    }
    fun toggleTaskCompletedCascade(taskId: Int) {
        viewModelScope.launch {
            val task = withContext(Dispatchers.IO) { taskRepo.getTaskById(taskId) } ?: return@launch
            val newDone = !task.isCompleted

            if (!newDone) {
                // ✅ برعکسش زنجیره‌ای نیست
                withContext(Dispatchers.IO) {
                    taskRepo.updateTask(task.copy(isCompleted = false))
                }
                return@launch
            }

            // ✅ done شدن: خودت + همه‌ی descendants
            val categoryId = task.categoryId ?: return@launch
            val allInCategory = withContext(Dispatchers.IO) { taskRepo.getTasksByCategoryOrdered(categoryId) }

            val childrenByParent: Map<Int?, List<Int>> =
                allInCategory
                    .filter { it.id != null }
                    .groupBy { it.parentTaskId }
                    .mapValues { it.value.mapNotNull { t -> t.id } }

            val idsToMarkDone = buildList {
                add(taskId)

                val stack = ArrayDeque<Int>()
                stack.add(taskId)

                while (stack.isNotEmpty()) {
                    val cur = stack.removeLast()
                    val children = childrenByParent[cur].orEmpty()
                    for (childId in children) {
                        add(childId)
                        stack.add(childId)
                    }
                }
            }.distinct()

            withContext(Dispatchers.IO) {
                taskRepo.setCompletedForIds(idsToMarkDone, true)
            }
        }
    }
    private fun wouldCreateCycleCategory(
        draggedId: Int,
        newParentId: Int,
        parentById: Map<Int, Int> // id -> parentId (Root = -1)
    ): Boolean {
        var cur = newParentId
        var guard = 0
        while (cur != -1 && guard < 100) {
            if (cur == draggedId) return true
            cur = parentById[cur] ?: -1
            guard++
        }
        return false
    }
    fun deleteCategoryPromoteChildren(categoryId: Int) {
        viewModelScope.launch {
            val all = uiState.value.categories
            val target = all.firstOrNull { it.categoryId == categoryId } ?: return@launch

            val parentOfTarget = target.parentCategoryId
            val children = all.filter { it.parentCategoryId == categoryId }

            // 1) بچه‌ها رو به parentِ کتگوری حذف‌شده وصل کن
            // siblingIndex جدید: بعد از آخرین بچه‌های parentOfTarget
            val baseIndex = all.count { it.parentCategoryId == parentOfTarget }

            children.forEachIndexed { i, child ->
                categoryRepository.updateCategory(
                    child.copy(
                        parentCategoryId = parentOfTarget,
                        siblingIndex = baseIndex + i
                    )
                )
            }

            //حذف ورک منیجرهای تسکهای کتگوری

            val taskList = target.categoryId?.let { taskRepo.getTasksByCategory(it) }
            taskList?.forEach { t ->
                val scheduleList = t.id?.let { taskScheduleRepo.getAllScheduleByTaskId(it) }

                scheduleList?.forEach { tech ->
                    val reminders = tech.id?.let { reminderRepo.getByScheduleId(it) }
                    reminders?.forEach { rUi ->
                        try {
                            reminderScheduler.cancel(rUi.id)   // ✅ این خط حیاتی است
                        } catch (_: Throwable) {}
                    }

                }

            }


            // 2) خود کتگوری حذف شود
            categoryRepository.deleteCategory(target)

            // 3) مرتب‌سازی siblingIndex برای parent قبلی (جای خالی پر شود)
            reorderSiblings(parentOfTarget)

            // 4) مرتب‌سازی siblingIndex برای parent جدید (بعد از promote)
            reorderSiblings(parentOfTarget)
        }
    }




    //تسک ها
    fun deleteTask(taskId: Int) {
        viewModelScope.launch {
            val t = taskRepo.getTaskById(taskId) ?: return@launch
            withContext(Dispatchers.IO) {


                val scheduleList = taskScheduleRepo.getAllScheduleByTaskId(taskId)

                scheduleList.forEach { tech ->
                    val reminders = tech.id?.let { reminderRepo.getByScheduleId(it) }
                    reminders?.forEach { rUi ->
                        try {
                            reminderScheduler.cancel(rUi.id)   // ✅ این خط حیاتی است
                        } catch (_: Throwable) {}
                    }

                }



                taskRepo.deleteTask(t)
            }
        }
    }
    private fun flattenTaskTreeWithLevelsAndVisibility(
        all: List<TaskMiniUi>,
        collapsedIds: Set<Int>,
        rootParentId: Int = ROOT,
        maxDepth: Int = 4
    ): List<TaskRenderItem> {

        val byParent = all.groupBy { it.parentTaskId }
        val items = mutableListOf<TaskRenderItem>()

        fun dfs(parentId: Int, realDepth: Int, ancestorCollapsed: Boolean) {
            if (realDepth > 50) return

            val renderLevel = realDepth.coerceAtMost(maxDepth)
            val children = byParent[parentId].orEmpty()
                .sortedWith(compareBy<TaskMiniUi> { it.siblingIndex }.thenBy { it.id })

            for (child in children) {
                val id = child.id
                val hasChildrenRaw = byParent[id].orEmpty().isNotEmpty()
                val hasChildren = (renderLevel < maxDepth) && hasChildrenRaw

                val selfCollapsed = collapsedIds.contains(id)
                val visible = !ancestorCollapsed

                items += TaskRenderItem(
                    task = child,
                    level = renderLevel,
                    hasChildren = hasChildren,
                    isExpanded = !selfCollapsed,
                    isVisible = visible
                )

                dfs(id, realDepth + 1, ancestorCollapsed || selfCollapsed)
            }
        }

        dfs(rootParentId, realDepth = 1, ancestorCollapsed = false)
        return items
    }
    fun applyTaskDragResult(
        draggedId: Int,
        oldParentId: Int?,
        newParentId: Int?,
        categoryId: Int,
        currentList: List<TaskRenderItem>
    ) {
        viewModelScope.launch {

            val all = withContext(Dispatchers.IO) { taskRepo.getTasksByCategory(categoryId) }
            val dragged = all.firstOrNull { it.id == draggedId } ?: return@launch

            val finalNewParent: Int = when (newParentId) {
                null -> dragged.parentTaskId ?: ROOT
                else -> newParentId
            }


            if (finalNewParent == draggedId) return@launch

            // cycle check
            val parentById = all.filter { it.id != null }.associate { it.id!! to (it.parentTaskId ?: ROOT) }
            if (wouldCreateTaskCycle(draggedId, finalNewParent, parentById)) return@launch


            // 1) parent update (اگر تغییر کرده)
            if (finalNewParent != dragged.parentTaskId) {
                withContext(Dispatchers.IO) {
                    taskRepo.updateTask(dragged.copy(parentTaskId = finalNewParent))
                }
            }

            suspend fun reorderFor(parentId: Int) {
                val orderedIds = currentList
                    .asSequence()
                    .filter { it.isVisible }
                    .filter { item ->
                        val id = item.task.id
                        val p = if (id == draggedId) finalNewParent else (item.task.parentTaskId ?: ROOT)
                        p == parentId

                    }
                    .map { it.task.id }
                    .toList()

                if (orderedIds.isEmpty()) return

                val freshAll = withContext(Dispatchers.IO) { taskRepo.getTasksByCategory(categoryId) }
                val byId = freshAll.associateBy { it.id }

                withContext(Dispatchers.IO) {
                    orderedIds.forEachIndexed { index, id ->
                        val e = byId[id] ?: return@forEachIndexed
                        if (e.siblingIndex != index) {
                            taskRepo.updateTask(e.copy(siblingIndex = index))
                        }
                    }
                }
            }

            reorderFor(oldParentId ?: ROOT)
            if (finalNewParent != (oldParentId ?: ROOT)) reorderFor(finalNewParent)

        }
    }
    fun onDragEndRestoreExpandForTask() {
        val id = _dragCollapsedRestoreTask.value ?: return
        _dragCollapsedRestoreTask.value = null

        _taskCollapsedIds.update { it - id }

        // Persist اختیاری:
        viewModelScope.launch {
            val current = uiState.value.taskEntities.firstOrNull { it.id == id } ?: return@launch
            taskRepo.updateTask(current.copy(isExtended = true))
        }
    }
    fun onDragStartMaybeCollapseForTask(taskId: Int) {
        val item = uiState.value.taskRenderItems.firstOrNull { it.task.id == taskId } ?: return
        if (!item.hasChildren) return

        val isExpandedNow = item.isExpanded
        if (isExpandedNow) {
            _dragCollapsedRestoreTask.value = taskId
            _taskCollapsedIds.update { it + taskId }
            // Persist اختیاری:
            viewModelScope.launch {
                val current = uiState.value.taskEntities.firstOrNull { it.id == taskId } ?: return@launch
                taskRepo.updateTask(current.copy(isExtended = false))
            }
        }
    }
    fun toggleExpandForTask(taskId: Int) {
        viewModelScope.launch {
            val current = uiState.value.taskEntities.firstOrNull { it.id == taskId } ?: return@launch
            val willCollapse = !_taskCollapsedIds.value.contains(taskId)

            _taskCollapsedIds.update { set ->
                if (willCollapse) set + taskId else set - taskId
            }

            // Persist
            taskRepo.updateTask(current.copy(isExtended = !willCollapse))
        }
    }
    private fun wouldCreateTaskCycle(
        draggedId: Int,
        newParentId: Int,
        parentById: Map<Int, Int>
    ): Boolean {
        var cur = newParentId
        var guard = 0
        while (cur != ROOT && guard < 200) {
            if (cur == draggedId) return true
            cur = parentById[cur] ?: ROOT
            guard++
        }
        return false
    }
    fun completeAllTasks(categoryId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            taskRepo.completeAllInCategory(categoryId)
        }
    }
    fun uncompletedAllTasks(categoryId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            taskRepo.uncompleteAllInCategory(categoryId)
        }
    }
    fun deleteCompletedTasks(categoryId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            taskRepo.deleteCompletedInCategory(categoryId)
        }
    }
    fun deleteAllTasks(categoryId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            taskRepo.deleteAllInCategory(categoryId)
        }
    }






    fun trySetDraftParent(parentId: Int): Boolean {
        val parentLevel = uiState.value.levelById[parentId] ?: 1
        if (parentLevel >= 4) return false
        _draft.update { it.copy(parentId = parentId) }
        return true
    }

    fun setMenuCategoryId(id: Int?) {
        _menuCategoryId.value = id
    }

    fun onDragStartMaybeCollapse(categoryId: Int) {
        val item = uiState.value.renderItems.firstOrNull { it.category.categoryId == categoryId } ?: return
        if (!item.hasChildren) return

        val isExpandedNow = item.isExpanded
        if (isExpandedNow) {
            _dragCollapsedRestoreCategory.value = categoryId
            _collapsedIdsCategory.update { it + categoryId }
            // Persist اختیاری:
            viewModelScope.launch {
                val current = uiState.value.categories.firstOrNull { it.categoryId == categoryId } ?: return@launch
                categoryRepository.updateCategory(current.copy(isExtended = false))
            }
        }
    }

    fun onDragEndRestoreExpand() {
        val id = _dragCollapsedRestoreCategory.value ?: return
        _dragCollapsedRestoreCategory.value = null

        _collapsedIdsCategory.update { it - id }

        // Persist اختیاری:
        viewModelScope.launch {
            val current = uiState.value.categories.firstOrNull { it.categoryId == id } ?: return@launch
            categoryRepository.updateCategory(current.copy(isExtended = true))
        }
    }

    fun toggleExpand(categoryId: Int) {
        viewModelScope.launch {
            val current = uiState.value.categories.firstOrNull { it.categoryId == categoryId } ?: return@launch
            val willCollapse = !_collapsedIdsCategory.value.contains(categoryId)

            _collapsedIdsCategory.update { set ->
                if (willCollapse) set + categoryId else set - categoryId
            }

            // Persist
            categoryRepository.updateCategory(current.copy(isExtended = !willCollapse))
        }
    }

    private fun flattenCategoryTreeWithLevelsAndVisibility(
        all: List<CategoryEntity>,
        collapsedIds: Set<Int>,
        rootParentId: Int = -1,
        maxDepth: Int = 4
    ): FlattenResult {

        val byParent = all.groupBy { it.parentCategoryId }
        val items = mutableListOf<CategoryRenderItem>()
        val levelById = mutableMapOf<Int, Int>()

        fun dfs(parentId: Int?, realDepth: Int, ancestorCollapsed: Boolean) {
            // ✅ توقف فقط برای امنیت (در صورت داده خراب/سیکل)
            if (realDepth > 50) return

            val renderLevel = realDepth.coerceAtMost(maxDepth)

            val children = (byParent[parentId] ?: emptyList())
                .sortedBy { it.siblingIndex }

            for (child in children) {
                val id = child.categoryId
                val hasChildrenRaw = (byParent[id] ?: emptyList()).isNotEmpty()

                // ✅ دکمه expand فقط تا سطح ۴ (مثل قبل)
                val hasChildren = (renderLevel < maxDepth) && hasChildrenRaw

                val selfCollapsed = id != null && collapsedIds.contains(id)
                val isExpanded = id != null && !selfCollapsed

                if (id != null) levelById[id] = renderLevel

                val visible = !ancestorCollapsed

                items.add(
                    CategoryRenderItem(
                        category = child,
                        level = renderLevel,         // ✅ level نمایشی
                        hasChildren = hasChildren,
                        isExpanded = isExpanded,
                        isVisible = visible
                    )
                )

                // ✅ ادامه بده، حتی اگر depth واقعی از ۴ رد شد
                dfs(
                    parentId = id,
                    realDepth = realDepth + 1,
                    ancestorCollapsed = ancestorCollapsed || selfCollapsed
                )
            }
        }

        dfs(rootParentId, realDepth = 1, ancestorCollapsed = false)
        return FlattenResult(items, levelById)
    }

    private fun flattenForPickerAllVisible(
        all: List<CategoryEntity>,
        rootParentId: Int = -1,
        maxDepth: Int = 4
    ): FlattenResult {

        val byParent = all.groupBy { it.parentCategoryId }
        val items = mutableListOf<CategoryRenderItem>()
        val levelById = mutableMapOf<Int, Int>()

        fun dfs(parentId: Int?, realDepth: Int) {
            if (realDepth > 50) return // گارد امنیتی

            val renderLevel = realDepth.coerceAtMost(maxDepth)

            val children = (byParent[parentId] ?: emptyList())
                .sortedBy { it.siblingIndex }

            for (child in children) {
                val id = child.categoryId
                val hasChildrenRaw = (byParent[id] ?: emptyList()).isNotEmpty()
                val hasChildren = (renderLevel < maxDepth) && hasChildrenRaw

                if (id != null) levelById[id] = renderLevel

                items.add(
                    CategoryRenderItem(
                        category = child,
                        level = renderLevel,
                        hasChildren = hasChildren,
                        isExpanded = true,  // در picker مهم نیست
                        isVisible = true    // ✅ همیشه visible
                    )
                )

                dfs(id, realDepth + 1)
            }
        }

        dfs(rootParentId, realDepth = 1)
        return FlattenResult(items, levelById)
    }



    private suspend fun reorderSiblings(parentId: Int?) {
        val siblings = uiState.value.categories
            .filter { it.parentCategoryId == parentId }
            .sortedBy { it.siblingIndex }

        siblings.forEachIndexed { idx, e ->
            if (e.siblingIndex != idx) {
                categoryRepository.updateCategory(e.copy(siblingIndex = idx))
            }
        }
    }










    sealed class CreateResult {
        data object Success : CreateResult()
        data class Error(val message: String) : CreateResult()
    }
}








