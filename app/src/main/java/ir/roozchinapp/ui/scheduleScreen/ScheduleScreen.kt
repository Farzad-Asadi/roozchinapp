package ir.roozchinapp.ui.scheduleScreen

import android.Manifest
import android.app.Activity
import android.app.AlarmManager
import android.content.Context
import android.content.ContextWrapper
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBackIos
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.LinkOff
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.NavHostController
import ir.roozchinapp.data.dataBaseRoom.tables.task.TaskChildRequirementContextType
import ir.roozchinapp.data.dataBaseRoom.tables.task.TaskChildRequirementStatus
import ir.roozchinapp.data.dataBaseRoom.tables.task.TaskChildRequirementSummaryUi
import ir.roozchinapp.data.dataBaseRoom.tables.task.TaskChildRequirementUi
import ir.roozchinapp.data.dataBaseRoom.tables.task.TaskChildStructure
import ir.roozchinapp.data.dataBaseRoom.tables.task.TaskEntity
import ir.roozchinapp.data.dataBaseRoom.tables.taskSchedule.RepeatUnit
import ir.roozchinapp.data.dataBaseRoom.tables.taskSchedule.ScheduleMode
import ir.roozchinapp.ui.navigation.AppRoutes
import ir.roozchinapp.utils.colorFromHex
import ir.roozchinapp.utils.convertToPersianDatePretty
import ir.roozchinapp.utils.iconFromKey
import ir.roozchinapp.utils.requestExactAlarmPermission
import ir.roozchinapp.utils.scheduleModeColor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.temporal.ChronoUnit
import kotlin.math.PI
import kotlin.math.acos
import kotlin.math.asin
import kotlin.math.atan
import kotlin.math.cos
import kotlin.math.floor
import kotlin.math.round
import kotlin.math.roundToInt
import kotlin.math.sin
import kotlin.math.tan

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleScreen(
    navController: NavHostController,
    viewModel: ScheduleScreenViewModel = hiltViewModel()
) {
    val allItems by viewModel.allItems.collectAsState()

    val anytimePalletTasks by viewModel.anytimePalletTasks.collectAsState()

    val runningPomodoroScheduleId by viewModel.runningPomodoroScheduleId.collectAsState()

    val savedScheduleVerticalZoom by viewModel.scheduleVerticalZoom.collectAsState()

    val pomodoroDailyAdjustments by viewModel.pomodoroDailyAdjustments.collectAsState()

    val taskChildSheetState by viewModel.taskChildSheetState.collectAsState()

    val taskChildRequirements by viewModel.taskChildSheetRequirements.collectAsState()

    val taskChildRequirementSummaries by viewModel.taskChildRequirementSummaries.collectAsState()

    val taskChildRequirementPreviews by viewModel.taskChildRequirementPreviews.collectAsState()

    val timelineItemsReal = remember(allItems) { allItems.filter { !it.inPallet } }

    val taskChildRequirementsByParentTaskId = remember(
        taskChildRequirementPreviews
    ) {
        taskChildRequirementPreviews.groupBy { requirement ->
            requirement.parentTaskId
        }
    }

    val overrideKeys = remember(timelineItemsReal) {
        // key = "ruleId|occurrenceDay"
        timelineItemsReal
            .asSequence()
            .filter { it.parentRuleScheduleId != null && it.occurrenceDateEpochDay != null }
            .map { "${it.parentRuleScheduleId}|${it.occurrenceDateEpochDay}" }
            .toHashSet()
    }

    val numDays = 5

    var scheduleAnchorEpochDay by rememberSaveable {
        mutableStateOf(LocalDate.now().toEpochDay())
    }

    val today = remember(scheduleAnchorEpochDay) {
        LocalDate.ofEpochDay(scheduleAnchorEpochDay)
    }

    val startDate = today

    val endDate = remember(startDate) {
        startDate.plusDays((numDays - 1).toLong())
    }






    LaunchedEffect(startDate, endDate) {
        viewModel.setTaskChildVisibleRange(
            startEpochDay = startDate.toEpochDay(),
            endEpochDay = endDate.toEpochDay()
        )
    }




    val timelineItems = remember(timelineItemsReal, startDate, endDate, overrideKeys) {
        val existingKeys = HashSet<String>(timelineItemsReal.size * 2)

        fun keyOf(taskId: Int, dateEpochDay: Long, startMin: Int, endMin: Int, mode: ScheduleMode): String =
            "$taskId|$dateEpochDay|$startMin|$endMin|$mode"

        // کلیدهای آیتم‌های واقعی (برای جلوگیری از دوبل)
        timelineItemsReal.forEach { it ->
            val d = it.dateEpochDay ?: return@forEach
            val sMin = it.start.toLocalTime().hour * 60 + it.start.toLocalTime().minute
            val eMin = it.end.toLocalTime().hour * 60 + it.end.toLocalTime().minute
            existingKeys += keyOf(it.taskId, d, sMin, eMin, it.mode)
        }

        val repeatingRules = timelineItemsReal
            .asSequence()
            .filter { it.repeating && it.repeatUnit != null }
            .toList()

        val virtuals = ArrayList<ScheduleScreenItem>(64)

        var d = startDate
        while (!d.isAfter(endDate)) {
            val dEpoch = d.toEpochDay()

            repeatingRules.forEach { rule ->
                val baseDate = rule.dateEpochDay?.let(LocalDate::ofEpochDay) ?: return@forEach
                if (d == baseDate) return@forEach

                // ✅ اگر برای این روز override ثبت شده، occurrence مجازی نساز
                val ovKey = "${rule.scheduleId}|$dEpoch"
                if (overrideKeys.contains(ovKey)) return@forEach

                // ✅ فعال بودن rule در این روز
                if (!ruleIsActiveOnDate(rule, d)) return@forEach

                val sMin = rule.start.toLocalTime().hour * 60 + rule.start.toLocalTime().minute
                val eMin = rule.end.toLocalTime().hour * 60 + rule.end.toLocalTime().minute
                if (eMin <= sMin) return@forEach

                // ✅ اگر آیتم واقعی همون بازه وجود داره، occurrence مجازی نساز
                val k = keyOf(rule.taskId, dEpoch, sMin, eMin, rule.mode)
                if (existingKeys.contains(k)) return@forEach

                // ✅ یک ID منفی برای آیتم مجازی
                val virtualId =
                    if (d == today) rule.scheduleId
                    else -kotlin.math.abs((rule.scheduleId * 31) xor (dEpoch.toInt() * 17)).coerceAtLeast(1)

                virtuals += rule.copy(
                    scheduleId = virtualId,     // منفی یعنی مجازی
                    dateEpochDay = dEpoch,
                    start = d.atStartOfDay().plusMinutes(sMin.toLong()),
                    end = d.atStartOfDay().plusMinutes(eMin.toLong()),
                    repeating = false,          // این خود rule نیست، occurrence است
                    inPallet = false,

                    // ✅ مهم: لینک دادن به rule
                    parentRuleScheduleId = rule.scheduleId,
                    occurrenceDateEpochDay = dEpoch
                )

                existingKeys += k
            }

            d = d.plusDays(1)
        }

        timelineItemsReal + virtuals
    }

    val palletItems = remember(allItems) {
        allItems.filter { it.inPallet && it.mode != ScheduleMode.POMODORO }
    }




    // ✅ rule های پومودورو (برای expectedToday و config)
    val pomodoroPARENTRulesToday = remember(allItems, today) {
        allItems.filter { it.mode == ScheduleMode.POMODORO && it.repeating }
            .filter { ruleIsActiveToday(it, today) }
    }

    // ✅ پومودوهای تایم‌لاینِ امروز (برای scheduledToday)
    val todayTimelinePomodoros = remember(allItems, today) {
        allItems.filter { !it.inPallet && it.mode == ScheduleMode.POMODORO }
            .filter { it.start.toLocalDate() == today }
    }

    val todayPomodorosByTaskId = remember(todayTimelinePomodoros) {
        todayTimelinePomodoros.groupBy { item ->
            item.taskId
        }
    }



    val todayEpochDay = remember(today) {
        today.toEpochDay()
    }

    val manualDoneTodayDeltaByTaskId = remember(
        pomodoroDailyAdjustments,
        todayEpochDay
    ) {
        pomodoroDailyAdjustments
            .asSequence()
            .filter { it.dateEpochDay == todayEpochDay }
            .associate { it.taskId to it.delta }
    }

    // ✅ کارت‌های تجمیعی پالت
    val pomodoroPalletCards = remember(
        pomodoroPARENTRulesToday,
        todayPomodorosByTaskId,
        manualDoneTodayDeltaByTaskId
    ) {
        pomodoroPARENTRulesToday
            .groupBy { rule ->
                rule.taskId
            }
            .mapNotNull { (taskId, rules) ->
                val any = rules.firstOrNull() ?: return@mapNotNull null
                val taskPomodoros = todayPomodorosByTaskId[taskId].orEmpty()

                val expected = rules.sumOf { rule ->
                    (rule.pomodoroUnitsPerDay ?: 1).coerceAtLeast(1)
                }

                val scheduled = taskPomodoros.size

                val realDoneToday = taskPomodoros.count { item ->
                    item.pomodoroFocusDoneApplied
                }

                val manualDeltaToday = manualDoneTodayDeltaByTaskId[taskId] ?: 0

                // این مقدار برای خود کارت پومودورو است.
                // بنابراین مازاد هم باید دیده شود.
                val doneToday = (realDoneToday + manualDeltaToday).coerceAtLeast(0)

                // اجازه می‌دهیم نهایتاً یک پومودوروی اضافه‌تر از هدف روزانه ساخته شود.
                val addableToday = Int.MAX_VALUE

                PomodoroPalletCardItem(
                    taskId = taskId,
                    scheduleId = any.scheduleId,
                    taskName = any.title,

                    totalTarget = any.pomodoroTargetUnits ?: 0,
                    totalDone = any.pomodoroDoneUnits.coerceAtLeast(0),

                    expectedToday = expected,
                    scheduledToday = scheduled,

                    focus = any.focusMinutes ?: 25,
                    shortBreak = any.shortBreakMinutes ?: 5,
                    longBreak = any.longBreakMinutes ?: 15,
                    longBreakEvery = any.longBreakEvery ?: 4,
                    categoryColor = any.categoryColor,
                    categoryIconName = any.categoryIconName,
                    doneToday = doneToday,

                    remainingToday = addableToday
                )
            }
            .sortedBy { card ->
                card.taskName
            }
    }

    val todayDoneFocusMinutes = remember(pomodoroPalletCards) {
        pomodoroPalletCards.sumOf { card ->
            card.doneToday.coerceAtLeast(0) * card.focus.coerceAtLeast(1)
        }
    }

    val todayFocusText = remember(todayDoneFocusMinutes) {
        minutesToHHmm(todayDoneFocusMinutes)
    }

// کل هدف امروز باید از ruleهای فعال امروز بیاید، نه از تعداد کارت‌های ساخته‌شده در تایم‌لاین.
    val todayTargetPomodoroCount = remember(pomodoroPalletCards) {
        pomodoroPalletCards.sumOf { card ->
            card.expectedToday.coerceAtLeast(0)
        }
    }

// انجام‌شده‌ی امروز هم باید per-task به سقف expectedToday محدود شود.
// یعنی پومودوروهای اضافه، آمار امروز را بیشتر از هدف روزانه نمی‌کنند.
    val todayDonePomodoroCount = remember(pomodoroPalletCards) {
        pomodoroPalletCards.sumOf { card ->
            card.doneToday.coerceIn(
                minimumValue = 0,
                maximumValue = card.expectedToday.coerceAtLeast(0)
            )
        }
    }

    val todayDoneScheduledProgress = remember(
        todayDonePomodoroCount,
        todayTargetPomodoroCount
    ) {
        when {
            todayTargetPomodoroCount > 0 -> {
                todayDonePomodoroCount
                    .toFloat()
                    .div(todayTargetPomodoroCount.toFloat())
                    .coerceIn(0f, 1f)
            }

            else -> 0f
        }
    }

    var hasAppliedSavedVerticalZoom by remember { mutableStateOf(false) }

// مقدار محلی؛ مقدار واقعی از DataStore می‌آید
    var verticalZoomSaved by remember { mutableFloatStateOf(1f) }

// زوم واقعیِ در حال pinch
    var verticalZoomRaw by remember { mutableFloatStateOf(verticalZoomSaved) }

// زوم نمایشی
    val verticalZoomAnim = remember {
        androidx.compose.animation.core.Animatable(verticalZoomSaved)
    }

// مقدار نهایی که بقیه‌ی UI باید ببینه
    val verticalZoom = verticalZoomAnim.value

    LaunchedEffect(savedScheduleVerticalZoom) {
        val saved = savedScheduleVerticalZoom ?: return@LaunchedEffect

        if (!hasAppliedSavedVerticalZoom) {
            val cleanZoom = snapZoom(
                saved.coerceIn(ZOOM_MIN, ZOOM_MAX)
            )

            verticalZoomSaved = cleanZoom
            verticalZoomRaw = cleanZoom
            verticalZoomAnim.snapTo(cleanZoom)

            hasAppliedSavedVerticalZoom = true
        }
    }


    var isPinchZooming by remember { mutableStateOf(false) }
    val horizontalZoom by rememberSaveable { mutableFloatStateOf(1f) }

    var doubleTapRestoreZoom by rememberSaveable {
        mutableFloatStateOf(1f)
    }

    val hourHeightDp = (72.dp * verticalZoom).coerceIn(48.dp, 720.dp)
    val dayWidthDp = (220.dp * horizontalZoom).coerceIn(160.dp, 360.dp)

    val vScroll = remember { ScrollState(0) }
    val hScroll = remember { ScrollState(0) }

    var didAutoScroll by remember { mutableStateOf(false) }

    var currentTimeAutoScrollRequest by rememberSaveable {
        mutableIntStateOf(0)
    }

    val timelineInitialReady = hasAppliedSavedVerticalZoom && didAutoScroll

    var pendingScrollTo by remember { mutableStateOf<Int?>(null) }

    var pendingScrollByPx by remember { mutableFloatStateOf(0f) }

    var gridViewportHeightPx by remember { mutableFloatStateOf(0f) }
    val density = LocalDensity.current

    var autoScrollDir by remember { mutableIntStateOf(0) } // -1 بالا، +1 پایین، 0 هیچ
    var autoScrollIntensity by remember { mutableFloatStateOf(0f) } // 0..1 سرعت نسبی

    val edgeThresholdPx = with(density) { 72.dp.toPx() }  // ناحیه حساس
    val baseScrollStepPx = with(density) { 22.dp.toPx() } // سرعت پایه

    val scope = rememberCoroutineScope()

    var dragX by remember { mutableFloatStateOf(0f) }
    var dragY by remember { mutableFloatStateOf(0f) }

    // برای اینکه بفهمیم Grid کجای صفحه‌ست
    var gridOriginInWindow by remember { mutableStateOf(Offset.Zero) }
    var timelineBodyOriginInRoot by remember { mutableStateOf(Offset.Zero) }
    var palletExpanded by rememberSaveable { mutableStateOf(false) }

    var selectedPalletTabName by rememberSaveable {
        mutableStateOf(PalletPanelTab.DAILY.name)
    }

    val selectedPalletTab = remember(selectedPalletTabName) {
        runCatching {
            PalletPanelTab.valueOf(selectedPalletTabName)
        }.getOrDefault(PalletPanelTab.DAILY)
    }

    var overlayLayerOriginInRoot by remember { mutableStateOf(Offset.Zero) }

    val palletContentWidth = 160.dp
    val palletHandleWidth = 48.dp

// کل عرض پالت برای خود RightPallet
    val palletOverlayWidth = palletHandleWidth + if (palletExpanded) palletContentWidth else 0.dp

// فقط این مقدار باید از Grid کم شود.
// نوار باز/بسته شدن پالت باید روی Grid بیفتد و شفاف دیده شود.
    val palletReservedWidth = if (palletExpanded) palletContentWidth else 0.dp

    val pendingMove = remember { mutableStateMapOf<Int, PendingMove>() } // key = scheduleId

    var pendingMoveVersion by remember { mutableIntStateOf(0) }

    var selectedScheduleId by rememberSaveable { mutableStateOf<Int?>(null) }

    val dayWidthPx = with(density) { dayWidthDp.toPx() }
    val hourHeightPx = with(density) { hourHeightDp.toPx() }


    //launchDoubleTapVerticalZoom با محل دبل‌تاچ، دقیقه‌ی لمس‌شده را حساب می‌کند.
    //بعد zoom را تا targetZoom انیمیت می‌کند و هم‌زمان vScroll را طوری تنظیم می‌کند که همان دقیقه وسط viewport قرار بگیرد.
    fun launchDoubleTapVerticalZoom(
        tapYInGridContentPx: Float
    ) {
        if (gridViewportHeightPx <= 0f) return

        val currentZoom = verticalZoomAnim.value
        val isCurrentlyMaxZoom =
            currentZoom >= ZOOM_MAX - 0.01f

        val targetZoom =
            if (isCurrentlyMaxZoom) {
                doubleTapRestoreZoom.coerceIn(ZOOM_MIN, ZOOM_MAX)
            } else {
                doubleTapRestoreZoom = currentZoom.coerceIn(ZOOM_MIN, ZOOM_MAX)
                ZOOM_MAX
            }

        if (kotlin.math.abs(targetZoom - currentZoom) < 0.01f) return

        scope.launch {
            isPinchZooming = true

            try {
                val oldHourHeightPx = with(density) {
                    (72.dp * currentZoom)
                        .coerceIn(48.dp, 720.dp)
                        .toPx()
                }

                val contentYAtTap =
                    tapYInGridContentPx.coerceIn(
                        0f,
                        oldHourHeightPx * 24f
                    )

                val minuteAtTap =
                    (contentYAtTap / oldHourHeightPx) * 60f

                val startZoom = currentZoom
                val endZoom = targetZoom

                val durationMs = 260L
                val startNs = withFrameNanos { it }
                val endNs = startNs + durationMs * 1_000_000L

                while (true) {
                    val frameNs = withFrameNanos { it }

                    val rawT =
                        ((frameNs - startNs).toDouble() / (endNs - startNs).toDouble())
                            .coerceIn(0.0, 1.0)
                            .toFloat()

                    val t = FastOutSlowInEasing.transform(rawT)

                    val zoom =
                        startZoom + (endZoom - startZoom) * t

                    verticalZoomAnim.snapTo(zoom)

                    val newHourHeightPx = with(density) {
                        (72.dp * zoom)
                            .coerceIn(48.dp, 720.dp)
                            .toPx()
                    }

                    val contentHeightPx = newHourHeightPx * 24f

                    val maxScrollForZoom =
                        (contentHeightPx - gridViewportHeightPx)
                            .roundToInt()
                            .coerceAtLeast(0)

                    val newContentY =
                        (minuteAtTap / 60f) * newHourHeightPx

                    val targetScroll =
                        (newContentY - gridViewportHeightPx / 2f)
                            .roundToInt()
                            .coerceIn(0, maxScrollForZoom)

                    vScroll.scrollTo(targetScroll)

                    if (rawT >= 1f) break
                }

                verticalZoomSaved = endZoom
                verticalZoomRaw = endZoom
                viewModel.setScheduleVerticalZoom(endZoom)
            } finally {
                isPinchZooming = false
            }
        }
    }

    var grabOffsetX by remember { mutableFloatStateOf(0f) }
    var grabOffsetY by remember { mutableFloatStateOf(0f) }

    var draggingFromPallet by remember { mutableStateOf<ScheduleScreenItem?>(null) }

    var draggingAnytimeTask by remember { mutableStateOf<TaskEntity?>(null) }

    val overlayItem = draggingFromPallet
    val overlayWidth = dayWidthDp - 12.dp

    val overlayHeight = remember(overlayItem, hourHeightDp) {
        if (overlayItem == null) 48.dp
        else {
            val s =
                overlayItem.start.toLocalTime().hour * 60 + overlayItem.start.toLocalTime().minute
            val e = overlayItem.end.toLocalTime().hour * 60 + overlayItem.end.toLocalTime().minute
            val durMin = (e - s).coerceAtLeast(15) // حداقل مثل تایم‌لاین
            hourHeightDp * (durMin / 60f)
        }
    }

    var isAutoScrollingActive by remember { mutableStateOf(false) }

    val updateAutoScroll2: (
        startMin: Int,
        endMin: Int,
        mode: AutoScrollMode,
        pointerYInGridViewportPx: Float?
    ) -> Unit =
        let@{ startMin, endMin, mode, pointerYInGridViewportPx ->

            if (gridViewportHeightPx <= 0f) {
                autoScrollDir = 0
                autoScrollIntensity = 0f
                return@let
            }

            val viewTopPx = vScroll.value.toFloat()
            val viewBottomPx = viewTopPx + gridViewportHeightPx

            fun edgeIntensity(edgeMin: Int): Pair<Int, Float> {
                val yPx = hourHeightPx * (edgeMin / 60f)
                val distTop = yPx - viewTopPx
                val distBottom = viewBottomPx - yPx

                val topHit = distTop < edgeThresholdPx && vScroll.value > 0
                val botHit = distBottom < edgeThresholdPx && vScroll.value < vScroll.maxValue

                val topI = if (topHit) {
                    ((edgeThresholdPx - distTop) / edgeThresholdPx).coerceIn(0f, 1f)
                } else {
                    0f
                }

                val botI = if (botHit) {
                    ((edgeThresholdPx - distBottom) / edgeThresholdPx).coerceIn(0f, 1f)
                } else {
                    0f
                }

                return if (botI > topI) +1 to botI else -1 to topI
            }

            fun pointerIntensity(pointerY: Float): Pair<Int, Float> {
                val distTop = pointerY
                val distBottom = gridViewportHeightPx - pointerY

                val topHit = distTop < edgeThresholdPx && vScroll.value > 0
                val botHit = distBottom < edgeThresholdPx && vScroll.value < vScroll.maxValue

                val topI = if (topHit) {
                    ((edgeThresholdPx - distTop) / edgeThresholdPx).coerceIn(0f, 1f)
                } else {
                    0f
                }

                val botI = if (botHit) {
                    ((edgeThresholdPx - distBottom) / edgeThresholdPx).coerceIn(0f, 1f)
                } else {
                    0f
                }

                return if (botI > topI) +1 to botI else -1 to topI
            }

            when (mode) {
                AutoScrollMode.RESIZE_START -> {
                    val (dir, inten) = edgeIntensity(startMin)
                    autoScrollDir = if (inten == 0f) 0 else dir
                    autoScrollIntensity = inten
                }

                AutoScrollMode.RESIZE_END -> {
                    val (dir, inten) = edgeIntensity(endMin)
                    autoScrollDir = if (inten == 0f) 0 else dir
                    autoScrollIntensity = inten
                }

                AutoScrollMode.MOVE -> {
                    val pointerY = pointerYInGridViewportPx

                    if (pointerY == null) {
                        autoScrollDir = 0
                        autoScrollIntensity = 0f
                        return@let
                    }

                    val (dir, inten) = pointerIntensity(pointerY)
                    autoScrollDir = if (inten == 0f) 0 else dir
                    autoScrollIntensity = inten
                }
            }
        }


    var pendingBringDayIntoView by remember { mutableStateOf<Int?>(null) }


    var draggingPomodoro by remember { mutableStateOf<PomodoroPalletCardItem?>(null) }




    val isDraggingAnything =
        draggingFromPallet != null ||
                draggingPomodoro != null ||
                draggingAnytimeTask != null

    fun requestScrollToCurrentTimeLine() {
        if (isPinchZooming || isDraggingAnything) {
            return
        }

        didAutoScroll = false
        currentTimeAutoScrollRequest++
    }

    val overlayScheduleItem = draggingFromPallet
    val overlayPomodoroItem = draggingPomodoro


    var pomodoroAdjust by remember { mutableStateOf<PomodoroAdjustState?>(null) }

    // 1) اول آیتم‌های نمایشی (با pendingMove) را بساز
    val displayTimelineItems = remember(timelineItems, pendingMoveVersion) {
        timelineItems.map { item ->
            val pending = pendingMove[item.scheduleId]

            if (pending == null) {
                item
            } else {
                item.copy(
                    start = pending.date.atStartOfDay().plusMinutes(pending.startMin.toLong()),
                    end = pending.date.atStartOfDay().plusMinutes(pending.endMin.toLong())
                )
            }
        }
    }

    val pomodoroChainFlagsById = remember(displayTimelineItems) {
        computePomodoroChainFlags(displayTimelineItems)
    }

    val previousPomodoroEndMinById = remember(displayTimelineItems) {
        computePreviousPomodoroEndMinById(displayTimelineItems)
    }

    val childRequirementRenderInfoByScheduleId = remember(
        displayTimelineItems,
        taskChildRequirementsByParentTaskId
    ) {
        displayTimelineItems.associate { item ->
            val requirements = taskChildRequirementsByParentTaskId[item.taskId]
                .orEmpty()
                .filter { requirement ->
                    requirementMatchesTimelineItem(
                        requirement = requirement,
                        item = item
                    )
                }

            val summary = buildTaskChildRequirementSummaryForTimelineItem(
                item = item,
                requirements = requirements
            )

            item.scheduleId to TimelineChildRequirementRenderInfo(
                requirements = requirements,
                summary = summary
            )
        }
    }



// 2) بعد layout های همپوشانی را حساب کن (فقط داخل روز)
    val overlapLayouts = remember(displayTimelineItems, startDate, numDays) {
        computeOverlapLayouts(displayTimelineItems, startDate, numDays)
    }


    val context = LocalContext.current



    val activity = context as Activity

    val hasAskedSchedulePermissions by viewModel.hasAskedSchedulePermissions.collectAsState()

    var showPermissionDialog by rememberSaveable { mutableStateOf(false) }

    val alarmManager = remember(context) {
        context.getSystemService(AlarmManager::class.java)
    }

    fun refreshScheduleDateIfNeeded() {
        val realTodayEpochDay = LocalDate.now().toEpochDay()

        if (realTodayEpochDay == scheduleAnchorEpochDay) {
            return
        }

        scheduleAnchorEpochDay = realTodayEpochDay

        selectedScheduleId = null
        draggingFromPallet = null
        draggingPomodoro = null
        pomodoroAdjust = null

        draggingAnytimeTask = null

        pendingMove.clear()
        pendingMoveVersion++

        didAutoScroll = false
        pendingBringDayIntoView = null

        pendingScrollTo = null
        pendingScrollByPx = 0f

        currentTimeAutoScrollRequest++
    }

    val scheduleLifecycleOwner = context.findLifecycleOwner()

    DisposableEffect(scheduleLifecycleOwner) {
        if (scheduleLifecycleOwner == null) {
            onDispose { }
        } else {
            val observer = LifecycleEventObserver { _, event ->
                when (event) {
                    Lifecycle.Event.ON_START,
                    Lifecycle.Event.ON_RESUME -> {
                        refreshScheduleDateIfNeeded()
                        requestScrollToCurrentTimeLine()
                    }

                    else -> Unit
                }
            }

            scheduleLifecycleOwner.lifecycle.addObserver(observer)

            // همین لحظه هم یک بار چک کن.
            refreshScheduleDateIfNeeded()
            requestScrollToCurrentTimeLine()

            onDispose {
                scheduleLifecycleOwner.lifecycle.removeObserver(observer)
            }
        }
    }

    LaunchedEffect(Unit) {
        while (true) {
            val now = LocalDateTime.now()

            val millisUntilNextMinute =
                ((60 - now.second) * 1_000L - now.nano / 1_000_000L)
                    .coerceAtLeast(250L)

            delay(millisUntilNextMinute)

            refreshScheduleDateIfNeeded()
        }
    }

    fun hasNotificationPermission(): Boolean {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
    }

    fun hasExactAlarmPermission(): Boolean {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.S ||
                alarmManager.canScheduleExactAlarms()
    }

    val openExactAlarmSettings = {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !alarmManager.canScheduleExactAlarms()) {
            activity.requestExactAlarmPermission()
        }
    }

    val notificationPermissionLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission()
        ) {
            openExactAlarmSettings()
        }

    LaunchedEffect(
        hasAskedSchedulePermissions,
        hasNotificationPermission(),
        hasExactAlarmPermission()
    ) {
        val asked = hasAskedSchedulePermissions ?: return@LaunchedEffect

        val needsNotification = !hasNotificationPermission()
        val needsExactAlarm = !hasExactAlarmPermission()

        if (needsNotification || needsExactAlarm) {
            showPermissionDialog = true
        }
    }



    LaunchedEffect(isAutoScrollingActive) {
        while (isAutoScrollingActive) {
            val dir = autoScrollDir
            val intensity = autoScrollIntensity

            if (dir != 0) {
                val step = baseScrollStepPx * (0.25f + 0.75f * intensity)
                val prev = vScroll.value
                val next = (prev + dir * step).toInt().coerceIn(0, vScroll.maxValue)

                if (next != prev) {
                    vScroll.scrollTo(next)
                } else {
                    // ✅ hit edge -> pause, don't stop
                    autoScrollDir = 0
                    autoScrollIntensity = 0f
                }
            }

            delay(16)
        }
    }

    LaunchedEffect(timelineItems) {
        var changed = false

        timelineItems.forEach { item ->
            val pending = pendingMove[item.scheduleId] ?: return@forEach

            val currentDate = item.start.toLocalDate()
            val currentStart = item.start.toLocalTime().hour * 60 + item.start.toLocalTime().minute
            val currentEnd = item.end.toLocalTime().hour * 60 + item.end.toLocalTime().minute

            if (
                currentDate == pending.date &&
                currentStart == pending.startMin &&
                currentEnd == pending.endMin
            ) {
                pendingMove.remove(item.scheduleId)
                changed = true
            }
        }

        if (changed) {
            pendingMoveVersion++
        }
    }

    LaunchedEffect(pendingBringDayIntoView, dayWidthDp, numDays) {
        val day = pendingBringDayIntoView ?: return@LaunchedEffect
        // یکم حاشیه بده که روز کامل دیده شه
        val targetX = (day * dayWidthPx - dayWidthPx * 0.2f)
            .coerceIn(0f, hScroll.maxValue.toFloat())
        hScroll.animateScrollTo(targetX.toInt())
        pendingBringDayIntoView = null
    }

    LaunchedEffect(
        currentTimeAutoScrollRequest,
        hasAppliedSavedVerticalZoom,
        vScroll.maxValue,
        hScroll.maxValue,
        gridViewportHeightPx,
        hourHeightPx,
        dayWidthPx,
        startDate
    ) {
        if (didAutoScroll) return@LaunchedEffect

        if (!hasAppliedSavedVerticalZoom) return@LaunchedEffect
        if (gridViewportHeightPx <= 0f) return@LaunchedEffect

        if (vScroll.maxValue <= 0) {
            didAutoScroll = true
            return@LaunchedEffect
        }

        // چند فریم صبر می‌کنیم تا layout نهایی شود.
        repeat(3) {
            withFrameNanos { }
        }

        if (gridViewportHeightPx <= 0f) return@LaunchedEffect

        if (vScroll.maxValue <= 0) {
            didAutoScroll = true
            return@LaunchedEffect
        }

        fun calculateTargetY(): Int {
            val now = LocalDateTime.now()
            val minutesNow = now.hour * 60 + now.minute

            val currentLineYpx = hourHeightPx * (minutesNow / 60f)

            // 0.50 یعنی دقیقاً وسط.
            // 0.42 یعنی کمی بالاتر از وسط؛ برای دیدن ادامه‌ی ساعات بعدی بهتر است.
            val currentLineAnchorInViewport = gridViewportHeightPx * 0.42f

            return (currentLineYpx - currentLineAnchorInViewport)
                .coerceIn(0f, vScroll.maxValue.toFloat())
                .roundToInt()
        }

        fun calculateTargetX(): Int? {
            val now = LocalDateTime.now()

            val dayIndex = ChronoUnit.DAYS.between(
                startDate,
                now.toLocalDate()
            ).toInt()

            if (dayIndex !in 0 until numDays) return null
            if (hScroll.maxValue <= 0) return null

            return (dayIndex * dayWidthPx - dayWidthPx * 0.2f)
                .coerceIn(0f, hScroll.maxValue.toFloat())
                .roundToInt()
        }

        // پاس اول
        vScroll.scrollTo(calculateTargetY())
        calculateTargetX()?.let { targetX ->
            hScroll.scrollTo(targetX)
        }

        // یک فریم بعد، اگر maxValue یا viewport اصلاح شد، دوباره دقیق کن.
        withFrameNanos { }

        // پاس اصلاحی
        vScroll.scrollTo(calculateTargetY())
        calculateTargetX()?.let { targetX ->
            hScroll.scrollTo(targetX)
        }

        didAutoScroll = true
    }

    LaunchedEffect(pendingScrollTo) {
        val t = pendingScrollTo ?: return@LaunchedEffect
        vScroll.scrollTo(t)
        pendingScrollTo = null
    }

    LaunchedEffect(pendingScrollByPx) {
        val d = pendingScrollByPx
        if (d != 0f) {
            vScroll.scrollBy(d)
            pendingScrollByPx = 0f
        }
    }





    Scaffold(
        modifier = Modifier,
        contentWindowInsets = WindowInsets(0),
        topBar = {
            CompactTopBar(
                title = "Schedule"
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.surface)
                .onGloballyPositioned { coords ->
                    overlayLayerOriginInRoot = coords.positionInRoot()
                }
        ) {



            Column(Modifier.fillMaxSize()) {

                // --- Header (روزها) ---
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(hScroll)
                        .padding(start = 56.dp, end = palletReservedWidth)// جای ستون ساعت
                ) {
                    repeat(numDays) { i ->
                        val day = startDate.plusDays(i.toLong())
                        Box(
                            modifier = Modifier
                                .width(dayWidthDp)
                                .padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = convertToPersianDatePretty(day),
                                style = MaterialTheme.typography.titleSmall
                            )
                        }
                    }
                }

                HorizontalDivider(thickness = 0.5.dp)

                // --- بدنه: Sidebar ساعت + Grid ---
                Box(Modifier.fillMaxSize()) {
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .onGloballyPositioned { coords ->
                                timelineBodyOriginInRoot = coords.positionInRoot()
                            }
                            .pointerInput(Unit) {
                                awaitEachGesture {
                                    awaitFirstDown(pass = PointerEventPass.Initial)

                                    var lastDistance = 0f
                                    var lastStepTarget = verticalZoomAnim.value
                                    var animJob: kotlinx.coroutines.Job? = null

                                    while (true) {
                                        val event =
                                            awaitPointerEvent(pass = PointerEventPass.Initial)
                                        val pressed = event.changes.filter { it.pressed }

                                        if (pressed.size < 2) {
                                            isPinchZooming = false
                                            break
                                        }

                                        isPinchZooming = true


                                        val p1 = pressed[0].position
                                        val p2 = pressed[1].position
                                        val focus = (p1 + p2) / 2f
                                        val dist = (p1 - p2).getDistance()
                                        if (dist == 0f) continue

                                        if (lastDistance != 0f) {
                                            val zoomChange = dist / lastDistance

                                            // 1) rawZoom پیوسته
                                            val newRaw = (verticalZoomRaw * zoomChange).coerceIn(
                                                ZOOM_MIN,
                                                ZOOM_MAX
                                            )
                                            verticalZoomRaw = newRaw

                                            // 2) پله‌ی هدف
                                            val targetStep = snapZoom(newRaw)

                                            // 3) فقط وقتی پله عوض شد، نرم animate کن
                                            if (targetStep != lastStepTarget) {
                                                lastStepTarget = targetStep
                                                verticalZoomSaved = targetStep
                                                viewModel.setScheduleVerticalZoom(targetStep)

                                                animJob?.cancel()
                                                animJob = scope.launch {
                                                    // --- 1) anchor را در لحظه‌ی شروع پله‌ جدید قفل کن ---
                                                    // focus.y در مختصات Row است.
                                                    // پس اول آن را به مختصات Root تبدیل می‌کنیم، بعد نسبت به Grid می‌سنجیم.
                                                    val focusYInRoot = timelineBodyOriginInRoot.y + focus.y

                                                    val focusYInGridViewport =
                                                        (focusYInRoot - gridOriginInWindow.y).coerceIn(
                                                            0f,
                                                            gridViewportHeightPx
                                                        )

                                                    // زوم فعلیِ نمایشی (شروع انیمیشن)
                                                    val z0 = verticalZoomAnim.value
                                                    val hourH0 = with(density) {
                                                        (72.dp * z0).coerceIn(
                                                            48.dp,
                                                            720.dp
                                                        ).toPx()
                                                    }

                                                    // دقیقه‌ای که دقیقاً زیر انگشت است (در مختصات محتوا)
                                                    val contentY0 =
                                                        vScroll.value + focusYInGridViewport
                                                    val minuteUnderFinger =
                                                        (contentY0 / hourH0) * 60f

                                                    // --- 2) انیمیشن دستی: هم‌زمان zoom و scroll ---
                                                    val z1 = targetStep
                                                    val durationMs = 120L
                                                    val startNs = withFrameNanos { it }
                                                    val endNs = startNs + durationMs * 1_000_000L

                                                    while (true) {
                                                        val now = withFrameNanos { it }
                                                        val tRaw =
                                                            ((now - startNs).toDouble() / (endNs - startNs).toDouble())
                                                                .coerceIn(0.0, 1.0)
                                                                .toFloat()

                                                        // easing مشابه FastOutSlowInEasing (تقریب خوب و سبک)
                                                        val t = FastOutSlowInEasing.transform(tRaw)

                                                        // lerp زوم
                                                        val z = z0 + (z1 - z0) * t

                                                        // 2-1) اعمال زوم (بدون animateTo)
                                                        verticalZoomAnim.snapTo(z)

                                                        // 2-2) محاسبه‌ی اسکرول لازم برای ثابت ماندن دقیقه زیر انگشت
                                                        val hourH = with(density) {
                                                            (72.dp * z).coerceIn(
                                                                48.dp,
                                                                720.dp
                                                            ).toPx()
                                                        }
                                                        val contentY =
                                                            (minuteUnderFinger / 60f) * hourH
                                                        val targetScroll =
                                                            (contentY - focusYInGridViewport)
                                                                .roundToInt()
                                                                .coerceIn(0, vScroll.maxValue)

                                                        vScroll.scrollTo(targetScroll)

                                                        if (tRaw >= 1f) break
                                                    }


                                                    // ذخیره برای بعد
                                                    verticalZoomSaved = z1
                                                }
                                            }
                                        }

                                        lastDistance = dist
                                        pressed.forEach { it.consume() }
                                    }
                                }
                            }

                    ) {

                        // Sidebar ساعت
                        Column(
                            modifier = Modifier
                                .width(56.dp)
                                .verticalScroll(vScroll, enabled = !isPinchZooming)
                        ) {
                            repeat(24) { h ->
                                Box(
                                    modifier = Modifier
                                        .height(hourHeightDp)
                                        .fillMaxWidth(),
                                    contentAlignment = Alignment.TopCenter
                                ) {
                                    Text(
                                        text = "%02d:00".format(h),
                                        style = MaterialTheme.typography.labelSmall,
                                        modifier = Modifier.padding(top = 4.dp)
                                    )
                                }
                            }
                        }

                        // Grid
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .padding(end = palletReservedWidth)
                                .onGloballyPositioned { coords ->
                                    gridOriginInWindow = coords.positionInRoot()
                                }
                                .onSizeChanged { size ->
                                    gridViewportHeightPx = size.height.toFloat()
                                }
                                .verticalScroll(vScroll, enabled = !isPinchZooming)
                                .horizontalScroll(hScroll, enabled = !isPinchZooming)
                        ) {
                            val totalHeight = hourHeightDp * 24
                            val totalWidth = dayWidthDp * numDays
                            val colorFor = MaterialTheme.colorScheme.outlineVariant

                            val sunWindows = remember(startDate, numDays) {
                                List(numDays) { dayIndex ->
                                    calculateTimelineSunWindow(
                                        date = startDate.plusDays(dayIndex.toLong()),
                                        location = SAQQEZ_TIMELINE_LIGHT_LOCATION
                                    )
                                }
                            }

                            // پس‌زمینه Grid + شب/روز + خطوط
                            Box(
                                modifier = Modifier
                                    .width(totalWidth)
                                    .height(totalHeight)
                                    .drawBehind {
                                        // ✅ اول بک‌گراند شب/روز سقز را بکش؛ خطوط و کارت‌ها روی آن می‌آیند.
                                        drawSaqqezDayNightTimelineBackground(
                                            sunWindows = sunWindows,
                                            dayWidthDp = dayWidthDp,
                                            hourHeightDp = hourHeightDp
                                        )

                                        val hourH = hourHeightDp.toPx()
                                        val dayW = dayWidthDp.toPx()

                                        val gridHaloColor = Color.White.copy(alpha = 0.42f)
                                        val gridHaloStroke = 3.dp.toPx()
                                        val gridMainStroke = 1.dp.toPx()

                                        // خطوط افقی ساعت
                                        repeat(24) { i ->
                                            val y = i * hourH

                                            // halo روشن: در روز تقریباً محو است، در شب مرز ساعت‌ها را زنده می‌کند
                                            drawLine(
                                                color = gridHaloColor,
                                                start = Offset(0f, y),
                                                end = Offset(size.width, y),
                                                strokeWidth = gridHaloStroke
                                            )

                                            // خط اصلی فعلی
                                            drawLine(
                                                color = colorFor,
                                                start = Offset(0f, y),
                                                end = Offset(size.width, y),
                                                strokeWidth = gridMainStroke
                                            )
                                        }

                                        // خطوط عمودی روزها
                                        repeat(numDays + 1) { i ->
                                            val x = i * dayW

                                            // halo روشن برای دیده‌شدن مرز روزها روی پس‌زمینه شب
                                            drawLine(
                                                color = gridHaloColor,
                                                start = Offset(x, 0f),
                                                end = Offset(x, size.height),
                                                strokeWidth = gridHaloStroke
                                            )

                                            // خط اصلی فعلی
                                            drawLine(
                                                color = colorFor,
                                                start = Offset(x, 0f),
                                                end = Offset(x, size.height),
                                                strokeWidth = gridMainStroke
                                            )
                                        }
                                    }
                            ) {

                                // ✅ لایه‌ی خالیِ قابل کلیک/دبل‌تاچ (زیر آیتم‌ها)
                                Box(
                                    modifier = Modifier
                                        .matchParentSize()
                                        .pointerInput(
                                            gridViewportHeightPx
                                        ) {
                                            detectTapGestures(
                                                onTap = {
                                                    selectedScheduleId = null
                                                },
                                                onDoubleTap = { tapOffset ->
                                                    selectedScheduleId = null
                                                    launchDoubleTapVerticalZoom(
                                                        tapYInGridContentPx = tapOffset.y
                                                    )
                                                }
                                            )
                                        }
                                )

                                // ✅ آیتم‌ها روی Grid
                                displayTimelineItems.forEach { displayItem ->

                                    val layout = overlapLayouts[displayItem.scheduleId]

                                    val item = displayItem

                                    val childRequirementRenderInfo =
                                        childRequirementRenderInfoByScheduleId[item.scheduleId]

                                    val childPreviewRequirements =
                                        childRequirementRenderInfo?.requirements.orEmpty()

                                    val childSummary =
                                        childRequirementRenderInfo?.summary

                                    TimelineItemBox(
                                        item = item,
                                        startDate = startDate,
                                        overlapLayout = layout,
                                        dayWidthDp = dayWidthDp,
                                        hourHeightDp = hourHeightDp,
                                        verticalZoom = verticalZoom,
                                        numDays = numDays,
                                        selected = (selectedScheduleId == displayItem.scheduleId),
                                        pomodoroChainedWithPrevious = pomodoroChainFlagsById[displayItem.scheduleId]?.hasPrevious == true,
                                        pomodoroChainedWithNext = pomodoroChainFlagsById[displayItem.scheduleId]?.hasNext == true,
                                        isPomodoroTimerActive = runningPomodoroScheduleId == displayItem.scheduleId,
                                        previousPomodoroEndMin = previousPomodoroEndMinById[displayItem.scheduleId],
                                        onToggleSelected = {
                                            val newSelection =
                                                if (selectedScheduleId == displayItem.scheduleId) null
                                                else displayItem.scheduleId

                                            selectedScheduleId = newSelection

                                            // ✅ اگر آیتم انتخاب شد، پالت بسته شود
                                            if (newSelection != null) {
                                                palletExpanded = false
                                            }
                                        },
                                        onMoveCommit = { scheduleId, date, s, e ->
                                            val isVirtual = scheduleId < 0

                                            if (isVirtual) {
                                                // 🔥 مجازی → واقعی بساز (Override)
                                                // پیشنهاد: توی ViewModel یه تابع unified داشته باشی که هم TIME_RANGE هم POMODORO رو ساپورت کنه
                                                viewModel.materializeVirtual(
                                                    virtual = displayItem,
                                                    newDate = date,
                                                    newStartMin = s,
                                                    newEndMin = e,
                                                    inPallet = false
                                                )

                                                selectedScheduleId = null
                                                return@TimelineItemBox
                                            }

                                            // واقعی → همان رفتار فعلی
                                            pendingMove[scheduleId] = PendingMove(date, s, e)
                                            pendingMoveVersion++

                                            if (item.mode == ScheduleMode.POMODORO) {
                                                viewModel.movePomodoroSchedule(scheduleId, date, s, e)
                                            } else {
                                                viewModel.moveSchedule(scheduleId, date, s, e)
                                            }
                                        },
                                        onResizeEndCommit = { scheduleId, newEnd ->
                                            val isVirtual = scheduleId < 0

                                            if (isVirtual) {
                                                val d = displayItem.start.toLocalDate()
                                                val startMin =
                                                    displayItem.start.toLocalTime().hour * 60 + displayItem.start.toLocalTime().minute

                                                viewModel.materializeVirtual(
                                                    virtual = displayItem,
                                                    newDate = d,
                                                    newStartMin = startMin,
                                                    newEndMin = newEnd,
                                                    inPallet = false
                                                )

                                                selectedScheduleId = null
                                                return@TimelineItemBox
                                            }

                                            viewModel.resizeScheduleEnd(scheduleId, newEnd)
                                        },
                                        onResizeStartCommit = { scheduleId, newStart ->
                                            val isVirtual = scheduleId < 0

                                            if (isVirtual) {
                                                val d = displayItem.start.toLocalDate()
                                                val endMin =
                                                    displayItem.end.toLocalTime().hour * 60 + displayItem.end.toLocalTime().minute

                                                viewModel.materializeVirtual(
                                                    virtual = displayItem,
                                                    newDate = d,
                                                    newStartMin = newStart,
                                                    newEndMin = endMin,
                                                    inPallet = false
                                                )

                                                selectedScheduleId = null
                                                return@TimelineItemBox
                                            }

                                            viewModel.resizeScheduleStart(scheduleId, newStart)
                                        },
                                        onSendToPallet = { scheduleId, _ ->
                                            val isVirtual = scheduleId < 0

                                            if (isVirtual) {
                                                val d = displayItem.start.toLocalDate()
                                                val s =
                                                    displayItem.start.toLocalTime().hour * 60 + displayItem.start.toLocalTime().minute
                                                val e =
                                                    displayItem.end.toLocalTime().hour * 60 + displayItem.end.toLocalTime().minute

                                                viewModel.materializeVirtual(
                                                    virtual = displayItem,
                                                    newDate = d,
                                                    newStartMin = s,
                                                    newEndMin = e,
                                                    inPallet = true
                                                )

                                                selectedScheduleId = null
                                                return@TimelineItemBox
                                            }

                                            viewModel.moveScheduleFromTimeLineToPallet(scheduleId)
                                            selectedScheduleId = null
                                        },
                                        onAutoScroll = updateAutoScroll2,
                                        onAutoScrollStart = { isAutoScrollingActive = true },
                                        onAutoScrollStop = {
                                            isAutoScrollingActive = false
                                            autoScrollDir = 0
                                            autoScrollIntensity = 0f
                                        },
                                        vScrollValueProvider = { vScroll.value },
                                        onBringDayIntoView = { dayIndex ->
                                            pendingBringDayIntoView = dayIndex
                                        },
                                        onEdit = { taskId ->
                                            // ✅ همان مسیر CategoryScreen
                                            navController.navigate(AppRoutes.taskEdit(taskId))
                                        },
                                        onDelete = { scheduleId ->
                                            viewModel.deleteScheduleById(scheduleId)
                                            selectedScheduleId = null
                                        },
                                        onMakeIndependent = {
                                            // این callback فقط از منوی آیتم مجازی صدا زده می‌شود
                                            val d = displayItem.start.toLocalDate()
                                            val s = displayItem.start.toLocalTime().hour * 60 + displayItem.start.toLocalTime().minute
                                            val e = displayItem.end.toLocalTime().hour * 60 + displayItem.end.toLocalTime().minute

                                            viewModel.materializeVirtual(
                                                virtual = displayItem,
                                                newDate = d,
                                                newStartMin = s,
                                                newEndMin = e,
                                                inPallet = false
                                            )
                                            selectedScheduleId = null
                                        },
                                        onStartPomodoroNow = { scheduleId ->
                                            viewModel.startPomodoroNow(scheduleId)
                                            selectedScheduleId = null
                                        },
                                        onOpenChildren = {
                                            viewModel.openTaskChildSheet(displayItem)
                                            selectedScheduleId = null
                                        },
                                        childRequirementSummary = childSummary,
                                        childPreviewRequirements = childPreviewRequirements,

                                        )
                                }
                            }
                        }
                    }

                    // ✅ خط زمان روی کل ناحیه (ستون ساعت + گرید)
                    CurrentTimeOverlay(
                        startDate = startDate,
                        numDays = numDays,
                        dayWidthDp = dayWidthDp,
                        hourHeightDp = hourHeightDp,
                        vScroll = vScroll,
                        palletOverlayWidth = palletReservedWidth,
                        sidebarWidth = 56.dp
                    )
                }
            }

            if (!timelineInitialReady) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.surface)
                        .zIndex(900f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        CircularProgressIndicator(
                            strokeWidth = 3.dp,
                            modifier = Modifier.size(34.dp)
                        )

                        Spacer(Modifier.height(14.dp))

                        Text(
                            text = "Loading timeline...",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // ✅ Drag overlay: same visual as pallet card
            if (draggingFromPallet != null || draggingPomodoro != null || draggingAnytimeTask != null) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .zIndex(1000f)
                ) {
                    val overlayModifier = Modifier
                        .offset {
                            IntOffset(
                                (dragX - grabOffsetX - overlayLayerOriginInRoot.x).toInt(),
                                (dragY - grabOffsetY - overlayLayerOriginInRoot.y).toInt()
                            )
                        }
                        .width(palletContentWidth)
                        .graphicsLayer {
                            alpha = 0.92f
                            scaleX = 1.02f
                            scaleY = 1.02f
                            shadowElevation = 12f
                        }

                    draggingPomodoro?.let { card ->
                        PomodoroPalletCardVisual(
                            item = card,
                            modifier = overlayModifier
                        )
                    }

                    draggingFromPallet?.let { item ->
                        PalletTaskItemVisual(
                            item = item,
                            modifier = overlayModifier
                        )
                    }

                    draggingAnytimeTask?.let { task ->
                        AnytimePalletTaskCardVisual(
                            task = task,
                            modifier = overlayModifier
                        )
                    }
                }
            }

            //Stepper
            pomodoroAdjust?.let { st ->
                PomodoroCountStepperOverlay(
                    count = st.ids.size,
                    max = st.maxAllowed,
                    anchorInRoot = st.anchorInRoot,
                    onInc = {
                        if (st.maxAllowed != Int.MAX_VALUE && st.ids.size >= st.maxAllowed) {
                            return@PomodoroCountStepperOverlay
                        }
                        val nextStart = st.startMin + (st.ids.size * (st.focus + st.shortBreak))
                        scope.launch {
                            val newId = withContext(Dispatchers.IO) {
                                viewModel.insertOnePomodoroTimelineItem(
                                    taskId = st.taskId,
                                    scheduleId=st.scheduleId,
                                    date = st.date,
                                    startMin = nextStart,
                                    focus = st.focus,
                                    shortBreak = st.shortBreak
                                )
                            }
                            pomodoroAdjust = st.copy(ids = st.ids + newId)
                        }
                    },
                    onDec = {
                        if (st.ids.size <= 1) return@PomodoroCountStepperOverlay
                        val lastId = st.ids.last()
                        if (lastId != null) {
                            viewModel.deleteScheduleById(lastId)
                        }
                        pomodoroAdjust = st.copy(ids = st.ids.dropLast(1))
                    },
                    onDismiss = { pomodoroAdjust = null }
                )
            }


            //تایمر پومودورو
            RunningPomodoroPanelHost(
                viewModel = viewModel
            )

            RightPallet(
                palletItems = palletItems,
                pomodoroCards = pomodoroPalletCards,
                anytimeTasks = anytimePalletTasks,
                selectedTab = selectedPalletTab,
                expanded = palletExpanded,
                isDraggingFromPallet = isDraggingAnything,
                onToggle = { palletExpanded = !palletExpanded },
                onSelectTab = { tab ->
                    selectedPalletTabName = tab.name
                    if (!palletExpanded) {
                        palletExpanded = true
                    }
                },
                onDragStart = { item, sx, sy, downX, downY ->
                    palletExpanded = false
                    draggingPomodoro = null
                    draggingFromPallet = item

                    dragX = sx
                    dragY = sy
                    grabOffsetX = downX
                    grabOffsetY = downY
                },
                onPomodoroDragStart = { card, sx, sy, downX, downY ->
                    palletExpanded = false
                    draggingFromPallet = null
                    draggingPomodoro = card

                    dragX = sx
                    dragY = sy
                    grabOffsetX = downX
                    grabOffsetY = downY
                },
                onAnytimeDragStart = { task, sx, sy, downX, downY ->
                    palletExpanded = false
                    draggingFromPallet = null
                    draggingPomodoro = null
                    draggingAnytimeTask = task

                    dragX = sx
                    dragY = sy
                    grabOffsetX = downX
                    grabOffsetY = downY
                },
                onDrag = { dx, dy ->
                    dragX += dx
                    dragY += dy
                },
                onDragEnd = { scheduleId ->
                    val localX = dragX - gridOriginInWindow.x
                    val localY = dragY - gridOriginInWindow.y

                    val contentX = localX + hScroll.value
                    val contentY = localY + vScroll.value

                    val dayIndex = (contentX / dayWidthPx).toInt()
                    val minuteOfDay = ((contentY / hourHeightPx) * 60f).toInt()

                    if (dayIndex !in 0 until numDays || minuteOfDay !in 0 until 24 * 60) {
                        draggingFromPallet = null
                        draggingPomodoro = null
                        return@RightPallet
                    }

                    val date = startDate.plusDays(dayIndex.toLong())
                    val minDur = 5
                    val dropSnapStep = snapStepForZoom(verticalZoom)
                    val startMin = snap(
                        minuteOfDay.coerceIn(0, 24 * 60 - minDur),
                        dropSnapStep
                    )

                    // ✅ اگر task آزاد drag شده بود، یک schedule جدید بساز.
                    // خود task در پالت آزاد باقی می‌ماند.
                    val anytimeTask = draggingAnytimeTask
                    if (anytimeTask != null) {
                        val taskId = anytimeTask.id

                        if (taskId != null) {
                            val defaultDurationMin = 60
                            val endMin = (startMin + defaultDurationMin).coerceAtMost(24 * 60)

                            viewModel.createTimelineScheduleFromAnytimeTask(
                                taskId = taskId,
                                date = date,
                                startMin = startMin,
                                endMin = endMin
                            )
                        }

                        draggingFromPallet = null
                        draggingPomodoro = null
                        draggingAnytimeTask = null
                        return@RightPallet
                    }

                    // ✅ اگر پومودورو کارت درگ می‌شد
                    val pomo = draggingPomodoro
                    if (pomo != null) {
                        val dur = (pomo.focus + pomo.shortBreak).coerceAtLeast(5)
                        val endMin = (startMin + dur).coerceAtMost(24 * 60)

                        // anchor برای stepper: نزدیک نقطه drop
                        val anchor = Offset(
                            x = (dragX - overlayLayerOriginInRoot.x).coerceAtLeast(0f),
                            y = (dragY - overlayLayerOriginInRoot.y).coerceAtLeast(0f)
                        )


                        val maxAllowed = Int.MAX_VALUE

                        // ✅ یک عدد بساز و id بگیر
                        scope.launch {
                            val id = withContext(Dispatchers.IO) {
                                viewModel.insertOnePomodoroTimelineItem(
                                    taskId = pomo.taskId,
                                    scheduleId = scheduleId,
                                    date = date,
                                    startMin = startMin,
                                    focus = pomo.focus,
                                    shortBreak = pomo.shortBreak
                                )
                            }

                            pomodoroAdjust = PomodoroAdjustState(
                                taskId = pomo.taskId,
                                scheduleId = scheduleId,
                                date = date,
                                startMin = startMin,
                                focus = pomo.focus,
                                shortBreak = pomo.shortBreak,
                                ids = listOf(id),
                                anchorInRoot = Offset(
                                    x = dragX - overlayLayerOriginInRoot.x,
                                    y = dragY - overlayLayerOriginInRoot.y
                                ),
                                maxAllowed = maxAllowed
                            )
                        }

                        draggingFromPallet = null
                        draggingPomodoro = null
                        draggingAnytimeTask = null
                        return@RightPallet
                    }


                    // ✅ در غیر این صورت schedule معمولی
                    val t = draggingFromPallet ?: run {
                        draggingFromPallet = null
                        draggingPomodoro = null
                        return@RightPallet
                    }

                    val oldStartMin = t.start.toLocalTime().hour * 60 + t.start.toLocalTime().minute
                    val oldEndMin = t.end.toLocalTime().hour * 60 + t.end.toLocalTime().minute
                    val oldDurMin = (oldEndMin - oldStartMin).coerceAtLeast(15)

                    val endMin = (startMin + oldDurMin).coerceAtMost(24 * 60)

                    viewModel.dropScheduleFromPalletToTimeLine(
                        scheduleId = t.scheduleId,
                        date = date,
                        startMin = startMin,
                        endMin = endMin
                    )

                    draggingFromPallet = null
                    draggingPomodoro = null
                    draggingAnytimeTask = null
                    return@RightPallet
                },
                onDragCancel = {
                    draggingFromPallet = null
                    draggingPomodoro = null
                    draggingAnytimeTask = null
                },
                onEditTask = { taskId ->
                    palletExpanded = false
                    navController.navigate(AppRoutes.taskEdit(taskId))
                },
                todayFocusText = todayFocusText,
                todayDonePomodoroCount = todayDonePomodoroCount,
                todayScheduledPomodoroCount = todayTargetPomodoroCount,
                onPomodoroDoneTodayInc = { taskId ->
                    viewModel.adjustPomodoroDoneToday(
                        taskId = taskId,
                        delta = +1
                    )
                },
                onPomodoroDoneTodayDec = { taskId ->
                    val currentManualDelta = manualDoneTodayDeltaByTaskId[taskId] ?: 0

                    val realDoneToday = todayPomodorosByTaskId[taskId]
                        .orEmpty()
                        .count { item ->
                            item.pomodoroFocusDoneApplied
                        }

                    val visibleDoneToday = realDoneToday + currentManualDelta

                    if (visibleDoneToday > 0) {
                        viewModel.adjustPomodoroDoneToday(
                            taskId = taskId,
                            delta = -1
                        )
                    }
                },
                modifier = Modifier.align(Alignment.CenterEnd),
            )




        }
        taskChildSheetState?.let { state ->
            val sheetState = rememberModalBottomSheetState(
                skipPartiallyExpanded = true
            )

            ModalBottomSheet(
                onDismissRequest = {
                    viewModel.closeTaskChildSheet()
                },
                sheetState = sheetState
            ) {
                TaskChildRequirementsSheetContent(
                    title = state.title,
                    occurrenceDateEpochDay = state.occurrenceDateEpochDay,
                    requirements = taskChildRequirements,
                    onToggle = { requirementId, checked ->
                        viewModel.toggleTaskChildRequirementCompleted(
                            requirementId = requirementId,
                            completed = checked
                        )
                    }
                )
            }
        }
        if (showPermissionDialog) {
            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                AlertDialog(
                    onDismissRequest = {
                        viewModel.markSchedulePermissionsAsked()
                        showPermissionDialog = false
                    },
                    title = {
                        Text(
                            text = "فعال‌سازی یادآورها",
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Right
                        )
                    },
                    text = {
                        Text(
                            text = "برای اینکه تایمر پومودورو و یادآورها دقیق‌تر اجرا شوند، لازم است اجازه نوتیفیکیشن و در صورت نیاز اجازه Alarms & reminders را فعال کنید.\n\nاین پیام فقط یک بار نمایش داده می‌شود.",
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Right,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                viewModel.markSchedulePermissionsAsked()
                                showPermissionDialog = false

                                if (
                                    Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                                    ContextCompat.checkSelfPermission(
                                        context,
                                        Manifest.permission.POST_NOTIFICATIONS
                                    ) != PackageManager.PERMISSION_GRANTED
                                ) {
                                    notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                                } else {
                                    openExactAlarmSettings()
                                }
                            }
                        ) {
                            Text("ادامه")
                        }
                    },
                    dismissButton = {
                        TextButton(
                            onClick = {
                                viewModel.markSchedulePermissionsAsked()
                                showPermissionDialog = false
                            }
                        ) {
                            Text("بعداً")
                        }
                    }
                )
            }
        }

    }
}

@Composable
private fun TaskChildRequirementsSheetContent(
    title: String,
    occurrenceDateEpochDay: Long,
    requirements: List<TaskChildRequirementUi>,
    onToggle: (requirementId: Int, checked: Boolean) -> Unit
) {
    val completedCount = requirements.count {
        it.status == TaskChildRequirementStatus.COMPLETE
    }

    val totalCount = requirements.size

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp)
    ) {
        Text(
            text = title,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Right,
            style = MaterialTheme.typography.titleLarge.copy(
                textDirection = TextDirection.ContentOrRtl
            )
        )

        Spacer(Modifier.height(4.dp))

        Text(
            text = "${convertToPersianDatePretty(LocalDate.ofEpochDay(occurrenceDateEpochDay))}  •  $completedCount/$totalCount",
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Right,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(Modifier.height(12.dp))

        HorizontalDivider()

        Spacer(Modifier.height(8.dp))

        if (requirements.isEmpty()) {
            Text(
                text = "برای این کارت هنوز زیرتسک قابل انجام وجود ندارد.",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 24.dp),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(420.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                items(
                    items = requirements,
                    key = { it.requirementId }
                ) { requirement ->

                    val now = System.currentTimeMillis()

                    val isWaitingNotDue =
                        requirement.status == TaskChildRequirementStatus.WAITING &&
                                requirement.dueAtEpochMillis != null &&
                                requirement.dueAtEpochMillis > now

                    val learningText =
                        if (requirement.learningTargetCount != null) {
                            "  •  ${requirement.learningIndex + 1}/${requirement.learningTargetCount}"
                        } else {
                            ""
                        }

                    val waitingText =
                        if (isWaitingNotDue) {
                            "  •  waiting"
                        } else {
                            ""
                        }

                    val checked =
                        requirement.status == TaskChildRequirementStatus.COMPLETE

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .clickable(
                                enabled = !isWaitingNotDue
                            ) {
                                onToggle(
                                    requirement.requirementId,
                                    !checked
                                )
                            }
                            .padding(horizontal = 8.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = requirement.childTitle + learningText + waitingText,
                            modifier = Modifier.weight(1f),
                            textAlign = TextAlign.Right,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                textDirection = TextDirection.ContentOrRtl
                            ),
                            color = when {
                                checked -> MaterialTheme.colorScheme.onSurfaceVariant
                                isWaitingNotDue -> MaterialTheme.colorScheme.onSurfaceVariant
                                else -> MaterialTheme.colorScheme.onSurface
                            }
                        )

                        Spacer(Modifier.width(8.dp))

                        Checkbox(
                            checked = checked,
                            enabled = !isWaitingNotDue,
                            onCheckedChange = null
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(24.dp))
    }
}


@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun TimelineItemBox(
    item: ScheduleScreenItem,
    startDate: LocalDate,
    overlapLayout: OverlapLayout?,
    dayWidthDp: Dp,
    hourHeightDp: Dp,
    verticalZoom: Float,
    numDays: Int,
    selected: Boolean,
    pomodoroChainedWithPrevious: Boolean,
    pomodoroChainedWithNext: Boolean,
    isPomodoroTimerActive: Boolean,
    previousPomodoroEndMin: Int?,
    onToggleSelected: () -> Unit,
    onMoveCommit: (scheduleId: Int, newDate: LocalDate, newStartMin: Int, newEndMin: Int) -> Unit,
    onResizeEndCommit: (scheduleId: Int, newEndMin: Int) -> Unit,
    onResizeStartCommit: (scheduleId: Int, newStartMin: Int) -> Unit,
    onSendToPallet: (scheduleId: Int, taskId: Int) -> Unit,
    onAutoScroll: (
        startMin: Int,
        endMin: Int,
        mode: AutoScrollMode,
        pointerYInGridViewportPx: Float?
    ) -> Unit,
    onAutoScrollStart: () -> Unit,
    onAutoScrollStop: () -> Unit,
    vScrollValueProvider: () -> Int,
    onBringDayIntoView: (dayIndex: Int) -> Unit,
    onEdit: (taskId: Int) -> Unit,
    onDelete: (scheduleId: Int) -> Unit,
    onMakeIndependent: () -> Unit,
    onStartPomodoroNow: (scheduleId: Int) -> Unit,
    onOpenChildren: () -> Unit,
    childRequirementSummary: TaskChildRequirementSummaryUi?,
    childPreviewRequirements: List<TaskChildRequirementUi>,


    ) {


    val density = LocalDensity.current

    //ثابت ها
    val minDur = 15


    //متغییر های کلی
    val dayWpx = with(density) { dayWidthDp.toPx() }
    val hourHpx = with(density) { hourHeightDp.toPx() }
    val snapStep = snapStepForZoom(verticalZoom)


    //مشخصات اسکچول
    //فاصله روز اسکچول با روز جاری
    val dayIndex0 = ChronoUnit.DAYS.between(startDate, item.start.toLocalDate()).toInt()
    if (dayIndex0 !in 0 until numDays) return
    val startMin0 = item.start.toLocalTime().hour * 60 + item.start.toLocalTime().minute
    val endMin0 = item.end.toLocalTime().hour * 60 + item.end.toLocalTime().minute
    val dur0 = (endMin0 - startMin0).coerceAtLeast(MIN_GAP_MIN)


    // Move state
    var dragDx by remember(item.scheduleId) { mutableFloatStateOf(0f) }
    var dragDy by remember(item.scheduleId) { mutableFloatStateOf(0f) }

    var movePointerYInGridViewportPx by remember(item.scheduleId) {
        mutableFloatStateOf(Float.NaN)
    }

    var moveDragDistancePx by remember(item.scheduleId) {
        mutableFloatStateOf(0f)
    }

    var moveAutoScrollArmed by remember(item.scheduleId) {
        mutableStateOf(false)
    }

    val moveAutoScrollArmThresholdPx = with(density) {
        24.dp.toPx()
    }


    //ترشولد برای جایجایی افقی
    val daySnapThresholdPx = dayWpx / 4f

    //دلتا برای حرکت افقی
    val dayDelta = when {
        dragDx > 0f -> ((dragDx + daySnapThresholdPx) / dayWpx).toInt()
        dragDx < 0f -> ((dragDx - daySnapThresholdPx) / dayWpx).toInt()
        else -> 0
    }

    val effectiveDragDyForPreview = dragDy
    val minDelta = ((effectiveDragDyForPreview / hourHpx) * 60f).roundToInt()


    val moveDayIndex = (dayIndex0 + dayDelta).coerceIn(0, numDays - 1)
    val moveStartRaw = (startMin0 + minDelta).coerceIn(0, 24 * 60 - minDur)
    val desiredMoveStart = snap(moveStartRaw, snapStep)
    val desiredMoveEnd = desiredMoveStart + dur0

    var lastScrollPx by remember(item.scheduleId) { mutableIntStateOf(0) }

    val moveRange = clampRange(desiredMoveStart, desiredMoveEnd)
    val moveStart = moveRange.start
    val moveEnd = moveRange.end

    // ---------------- Resize state ----------------
    var topDy by remember(item.scheduleId) { mutableFloatStateOf(0f) }
    var bottomDy by remember(item.scheduleId) { mutableFloatStateOf(0f) }

    val topDeltaMin = ((topDy / hourHpx) * 60f).roundToInt()
    val bottomDeltaMin = ((bottomDy / hourHpx) * 60f).roundToInt()

    val resizeStartRaw = (startMin0 + topDeltaMin)
        .coerceIn(0, (endMin0 - minDur).coerceAtLeast(0))
    val desiredResizeStart = snap(resizeStartRaw, snapStep)
    val rStart = clampRange(desiredResizeStart, endMin0)
    val resizeStart = rStart.start


    val resizeEndRaw = (endMin0 + bottomDeltaMin)
        .coerceIn((startMin0 + minDur).coerceAtMost(24 * 60), 24 * 60)
    val desiredResizeEnd = snap(resizeEndRaw, snapStep)
    val rEnd = clampRange(startMin0, desiredResizeEnd)
    val resizeEnd = rEnd.end

    var isMoving by remember(item.scheduleId) { mutableStateOf(false) }
    var isResizingStart by remember(item.scheduleId) { mutableStateOf(false) }
    var isResizingEnd by remember(item.scheduleId) { mutableStateOf(false) }


    val showMovePreview = isMoving // وقتی در حال move هستیم، همیشه preview=move
    val showResizePreview = selected && (isResizingStart || isResizingEnd) // وقتی resize می‌کنیم

    val displayDayIndex = if (showMovePreview) moveDayIndex else dayIndex0

    val displayStart = when {
        showMovePreview -> moveStart
        showResizePreview -> resizeStart
        else -> startMin0
    }

    val displayEnd = when {
        showMovePreview -> moveEnd
        showResizePreview -> resizeEnd
        else -> endMin0
    }


    val baseW = (dayWidthDp - 12.dp)

    val widthFrac = overlapLayout?.widthFrac ?: 1f
    val offsetFrac = overlapLayout?.offsetFrac ?: 0f
    val z = overlapLayout?.z ?: item.scheduleId.toFloat()

    val x = dayWidthDp * displayDayIndex + (baseW * offsetFrac)
    val y = hourHeightDp * (displayStart / 60f)
    val h = hourHeightDp * (((displayEnd - displayStart).coerceAtLeast(MIN_GAP_MIN)) / 60f)

    // ارتفاع واقعی کارت بر اساس زمان شروع و پایان.
    // دیگر حداقل بصری برای خود کارت نداریم.
    val taskH = h

    // فقط وقتی کارت انتخاب شده و خیلی کوچک است، فضای انتخاب/بوردر را بزرگ‌تر می‌کنیم.
    // خود کارت همچنان با ارتفاع واقعی رسم می‌شود.
    val selectionH =
        if (selected && taskH < 76.dp) 76.dp else taskH


    val containerY = (y )



    var lastBroughtDay by remember(item.scheduleId) { mutableIntStateOf(dayIndex0) }

    // مقدار dragDx که باعث تغییر روز شده رو جدا می‌کنیم، فقط "باقی‌مانده" رو برای نمایش حرکت می‌ذاریم
    val residualDx = if (isMoving) (dragDx - (dayDelta * dayWpx)) else 0f

    // محدودش کن که کارت خیلی از ستون خارج نشه (اختیاری ولی خوش‌دست‌تر)
    val previewDx = residualDx.coerceIn(-dayWpx * 0.45f, dayWpx * 0.45f)

    var menuExpanded by remember(item.scheduleId) { mutableStateOf(false) }
    var showDeleteConfirm by remember(item.scheduleId) { mutableStateOf(false) }

    val level = overlapLayout?.level ?: 0

    val borderWidth = when (level) {
        1 -> 1.dp
        2 -> 1.5.dp
        3 -> 2.dp
        else -> 0.dp
    }

    val borderColor = when (level) {
        1 -> MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
        2 -> MaterialTheme.colorScheme.outline.copy(alpha = 0.6f)
        3 -> MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
        else -> Color.Transparent
    }

    LaunchedEffect(isMoving, moveDayIndex) {
        if (isMoving && moveDayIndex != lastBroughtDay) {
            lastBroughtDay = moveDayIndex
            onBringDayIntoView(moveDayIndex)
        }
    }


    // ✅ بعد از هر آپدیت از DB، state های drag/resize ریست بشن تا پرش نداشته باشیم
    LaunchedEffect(item.scheduleId, item.start, item.end) {
        dragDx = 0f
        dragDy = 0f
        topDy = 0f
        bottomDy = 0f
        movePointerYInGridViewportPx = Float.NaN
        moveDragDistancePx = 0f
        moveAutoScrollArmed = false
    }


    val isAnyDragging = isMoving || isResizingStart || isResizingEnd
    LaunchedEffect(isAnyDragging) {
        if (!isAnyDragging) return@LaunchedEffect

        while (true) {
            // اگر درگ تمام شد، خارج شو
            if (!(isMoving || isResizingStart || isResizingEnd)) break

            val cur = vScrollValueProvider()
            val ds = cur - lastScrollPx
            if (ds != 0) {
                if (isMoving) dragDy += ds.toFloat()
                if (isResizingStart) topDy += ds.toFloat()
                if (isResizingEnd) bottomDy += ds.toFloat()
                lastScrollPx = cur
            }

            delay(16)
        }
    }

    val isPomodoroPARENT = item.mode == ScheduleMode.POMODORO
    val showPomodoroChainTop = isPomodoroPARENT && pomodoroChainedWithPrevious
    val showPomodoroChainBottom = isPomodoroPARENT && pomodoroChainedWithNext

    val focusMin = (item.focusMinutes ?: 25).coerceAtLeast(1)
    val breakMin = (item.shortBreakMinutes ?: 5).coerceAtLeast(0)

    // اگر schedule طولش با focus+break یکی نبود، باز هم نسبت رو از همین‌ها می‌گیریم
    val totalPomo = (focusMin + breakMin).coerceAtLeast(1)
    val focusFrac = (focusMin.toFloat() / totalPomo).coerceIn(0f, 1f)

    val pomoFocusColor = scheduleModeColor(ScheduleMode.POMODORO).copy(alpha = 0.45f)
    val pomoBreakColor = scheduleModeColor(ScheduleMode.POMODORO).copy(alpha = 0.22f)


    val handleH = 16.dp
    val colorForBorder = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.45f)
    val allowResize = selected && item.mode != ScheduleMode.POMODORO
    val showTimeHandles = selected || isMoving   // ✅ در حالت move هم نمایش بده
    val showHandles = (selected || isMoving)


    val resizeStartModifier = if (allowResize) {
        Modifier.pointerInput(item.scheduleId, item.start, item.end) {
            detectVerticalDragGestures(
                onDragStart = {
                    isResizingStart = true
                    isMoving = false
                    isResizingEnd = false
                    lastScrollPx = vScrollValueProvider()
                    onAutoScrollStart()
                },
                onVerticalDrag = { change, dragAmount ->
                    change.consume()
                    topDy += dragAmount

                    val effectiveTopDy = topDy
                    val topDeltaNow =
                        ((effectiveTopDy / hourHpx) * 60f).toInt()
                    val raw = (startMin0 + topDeltaNow)
                        .coerceIn(
                            DAY_MIN,
                            (endMin0 - MIN_GAP_MIN).coerceAtLeast(DAY_MIN)
                        )

                    val desired = snap(raw, snapStep)
                    val clamped = clampRange(desired, endMin0)

                    onAutoScroll(
                        clamped.start,
                        endMin0,
                        AutoScrollMode.RESIZE_START,
                        null
                    )
                },
                onDragEnd = {
                    val topDeltaMin2 = ((topDy / hourHpx) * 60f).roundToInt()

                    val raw = (startMin0 + topDeltaMin2)
                        .coerceIn(
                            DAY_MIN,
                            (endMin0 - MIN_GAP_MIN).coerceAtLeast(DAY_MIN)
                        )

                    val desired = snap(raw, snapStep)
                    val clamped = clampRange(desired, endMin0)

                    onResizeStartCommit(item.scheduleId, clamped.start)

                    isResizingStart = false
                    topDy = 0f
                    onAutoScrollStop()
                },
                onDragCancel = {
                    isResizingStart = false
                    topDy = 0f
                    onAutoScrollStop()
                }
            )
        }
    } else {
        Modifier
    }


    val resizeEndModifier = if (allowResize) {
        Modifier.pointerInput(item.scheduleId, item.start, item.end) {
            detectVerticalDragGestures(
                onDragStart = {
                    isResizingEnd = true
                    isMoving = false
                    isResizingStart = false
                    lastScrollPx = vScrollValueProvider()
                    onAutoScrollStart()

                },
                onVerticalDrag = { change, dragAmount ->
                    change.consume()
                    bottomDy += dragAmount

                    val effectiveBottomDy = bottomDy
                    val bottomDeltaNow =
                        ((effectiveBottomDy / hourHpx) * 60f).toInt()
                    val raw = (endMin0 + bottomDeltaNow)
                        .coerceIn(
                            (startMin0 + MIN_GAP_MIN).coerceAtMost(DAY_MAX),
                            DAY_MAX
                        )
                    val desired = snap(raw, snapStep)

                    val clamped = clampRange(startMin0, desired)

                    onAutoScroll(
                        startMin0,
                        clamped.end,
                        AutoScrollMode.RESIZE_END,
                        null
                    )

//                    Log.i("TEST" , "bottomDy=$bottomDy ")
//                    Log.i("TEST" , "bottomDeltaNow=$bottomDeltaNow ")
//                    Log.i("TEST" , "raw=$raw ")
//                    Log.i("TEST" , "desired=$desired ")
//                    Log.i("TEST" , "clamped=$clamped ")
//                    Log.i("TEST" , "------------------- ")
                },
                onDragEnd = {
                    val effectiveBottomDy2 = bottomDy
                    val bottomDeltaMin2 =
                        ((effectiveBottomDy2 / hourHpx) * 60f).roundToInt()


                    val raw = (endMin0 + bottomDeltaMin2)
                        .coerceIn(
                            (startMin0 + MIN_GAP_MIN).coerceAtMost(DAY_MAX),
                            DAY_MAX
                        )

                    val desired = snap(raw, snapStep)

                    val clamped = clampRange(startMin0, desired)

                    onResizeEndCommit(item.scheduleId, clamped.end)

                    isResizingEnd = false
                    bottomDy = 0f
                    onAutoScrollStop()
                },
                onDragCancel = {
                    isResizingEnd = false
                    bottomDy = 0f
                    onAutoScrollStop()
                }
            )
        }
    } else {
        Modifier
    }



    val showHeader = taskH >= 56.dp   // ✅ این عدد را به سلیقه‌ات تنظیم کن

    val isVirtual = item.scheduleId < 0

    val scheduleColor =if (isVirtual) {
        scheduleModeColor(item.mode).copy(alpha = 0.15f)
    }else{
        scheduleModeColor(item.mode).copy(alpha = 0.35f)
    }

    val categoryBorderColor = colorFromHex(item.categoryColor ?: "#9E9E9E")

    val timelineBorderColor = if (isVirtual) {
        categoryBorderColor.copy(alpha = 0.55f)
    } else {
        categoryBorderColor
    }

    val activePomodoroBorderAlpha =
        if (isPomodoroTimerActive) {
            val transition = rememberInfiniteTransition(label = "active_pomodoro_border")
            val alpha by transition.animateFloat(
                initialValue = 0.35f,
                targetValue = 1f,
                animationSpec = infiniteRepeatable(
                    animation = tween(durationMillis = 650),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "active_pomodoro_border_alpha"
            )
            alpha
        } else {
            1f
        }

    val effectiveTimelineBorderColor =
        if (isPomodoroTimerActive) {
            timelineBorderColor.copy(alpha = activePomodoroBorderAlpha)
        } else {
            timelineBorderColor
        }

    val timelineBorderWidth = when {
        selected -> 2.5.dp
        level > 0 -> 2.dp
        else -> 1.5.dp
    }

    val effectiveTimelineBorderWidth =
        if (isPomodoroTimerActive) {
            3.dp
        } else {
            timelineBorderWidth
        }



    val dashed = selected && selectionH > taskH




    Box(
        modifier = Modifier
            .offset(x = x + 6.dp, y = containerY)
            .width(baseW * widthFrac)
            .height(selectionH)
            .zIndex(z)
            .drawBehind {
                if (dashed) {
                    val stroke = Stroke(
                        width = 2.dp.toPx(),
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
                    )
                    // radius رو با shape کارتت نزدیک می‌گیریم
                    val r = 12.dp.toPx()
                    drawRoundRect(
                        color = colorForBorder,
                        cornerRadius = CornerRadius(r, r),
                        style = stroke
                    )
                }
            }

    ) {

        //آیکون وضعیت بالا راست
        if (isVirtual) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 6.dp, end = 6.dp)
                    .size(22.dp)
                    .background(
                        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.85f),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Link,
                    contentDescription = "Virtual occurrence",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
        else if (item.repeating){
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 6.dp, end = 6.dp)
                    .size(22.dp)
                    .background(
                        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.85f),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Repeat,
                    contentDescription = "Virtual occurrence",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
            }
        }


        //  آیکون گروه + نام اسکچول یا تسک
        //  آیکون گروه + نام اسکچول یا تسک
        if (showHeader) {
            Row(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(start = 8.dp, top = 16.dp, end = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                CategoryIconWithPlate(
                    iconName = item.categoryIconName,
                    tint = categoryBorderColor
                )

                Spacer(Modifier.width(8.dp))

                Text(
                    text = item.title,
                    style = MaterialTheme.typography.labelLarge,
                    maxLines = 1
                )
            }
        }


        //  باکس اصلی
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .width(dayWidthDp - 12.dp)
                .height(taskH)
                .border(
                    width = effectiveTimelineBorderWidth,
                    color = effectiveTimelineBorderColor,
                    shape = MaterialTheme.shapes.medium
                )
                .clip(MaterialTheme.shapes.medium) // ✅ مهم: دو رنگ داخل شکل کلیپ شوند
        ) {

            if (isPomodoroPARENT) {
                // ✅ دو رنگ: بالا فوکوس، پایین استراحت
                Box(Modifier.fillMaxSize()) {

                    // بخش فوکوس (بالا)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight(focusFrac)
                            .background(pomoFocusColor)
                            .align(Alignment.TopStart)
                    )

                    // بخش استراحت (پایین)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight(1f - focusFrac)
                            .background(pomoBreakColor)
                            .align(Alignment.BottomStart)
                    )
                }
            }
            //(is time range)
            else {
                // ✅ حالت عادی همان رنگ قبلی
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            color = scheduleColor,
                            shape = RectangleShape // چون clip بالا انجام شده
                        )
                )
            }
            if (showPomodoroChainTop) {
                PomodoroChainMarker(
                    color = scheduleModeColor(ScheduleMode.POMODORO),
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 2.dp)
                )
            }

            if (showPomodoroChainBottom) {
                PomodoroChainMarker(
                    color = scheduleModeColor(ScheduleMode.POMODORO),
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 2.dp)
                )
            }
            val innerTopPad = if (showHeader) 34.dp else 8.dp

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(
                        top = if (selected) handleH else 0.dp,
                        bottom = if (selected) handleH else 0.dp
                    )
                    .combinedClickable(
                        onClick = onToggleSelected,
                        onLongClick = { /* فقط UX */ }
                    )
                    .pointerInput(item.scheduleId, item.start, item.end, selected) {
                        detectDragGesturesAfterLongPress(
                            onDragStart = { downPos ->
                                isMoving = true
                                isResizingStart = false
                                isResizingEnd = false
                                lastScrollPx = vScrollValueProvider()

                                movePointerYInGridViewportPx = with(density) {
                                    y.toPx()
                                } - vScrollValueProvider() + downPos.y

                                moveDragDistancePx = 0f
                                moveAutoScrollArmed = false

                                // فقط loop را آماده می‌کنیم؛ هنوز auto-scroll فعال نمی‌شود
                                onAutoScrollStart()
                            },
                            onDrag = { change, dragAmount ->
                                change.consume()
                                dragDx += dragAmount.x
                                dragDy += dragAmount.y

                                // ✅ محاسبه‌ی لحظه‌ای start/end برای auto-scroll
                                val r = calculateMovePreviewRange(
                                    startMin0 = startMin0,
                                    minDur = minDur,
                                    dur0 = dur0,
                                    dragDy = dragDy,
                                    hourHpx = hourHpx,
                                    snapStep = snapStep
                                )

                                movePointerYInGridViewportPx += dragAmount.y
                                moveDragDistancePx += dragAmount.getDistance()

                                if (!moveAutoScrollArmed && moveDragDistancePx >= moveAutoScrollArmThresholdPx) {
                                    moveAutoScrollArmed = true
                                }

                                val pointerYForAutoScroll =
                                    if (moveAutoScrollArmed && !movePointerYInGridViewportPx.isNaN()) {
                                        movePointerYInGridViewportPx
                                    } else {
                                        null
                                    }

                                onAutoScroll(
                                    r.start,
                                    r.end,
                                    AutoScrollMode.MOVE,
                                    pointerYForAutoScroll
                                )

                            },
                            onDragEnd = {
                                // محاسبه نهایی با dragDx/dragDy فعلی
                                val daySnapThresholdPx2 = dayWpx / 3f
                                val dayDelta2 = when {
                                    dragDx > 0f -> ((dragDx + daySnapThresholdPx2) / dayWpx).toInt()
                                    dragDx < 0f -> ((dragDx - daySnapThresholdPx2) / dayWpx).toInt()
                                    else -> 0
                                }


                                val moveDayIndex2 = (dayIndex0 + dayDelta2).coerceIn(0, numDays - 1)
                                val finalDate = startDate.plusDays(moveDayIndex2.toLong())

                                val r = calculateMovePreviewRange(
                                    startMin0 = startMin0,
                                    minDur = minDur,
                                    dur0 = dur0,
                                    dragDy = dragDy,
                                    hourHpx = hourHpx,
                                    snapStep = snapStep
                                )

                                onMoveCommit(item.scheduleId, finalDate, r.start, r.end)
                                isMoving = false
                                dragDx = 0f
                                dragDy = 0f
                                movePointerYInGridViewportPx = Float.NaN
                                moveDragDistancePx = 0f
                                moveAutoScrollArmed = false
                                onAutoScrollStop()


                            },
                            onDragCancel = {
                                isMoving = false
                                dragDx = 0f
                                dragDy = 0f
                                movePointerYInGridViewportPx = Float.NaN
                                moveDragDistancePx = 0f
                                moveAutoScrollArmed = false
                                onAutoScrollStop()

                            }
                        )
                    }
                    .padding(start = 8.dp, top = innerTopPad, end = 8.dp, bottom = 8.dp)
            ) {
                childRequirementSummary?.let { summary ->
                    if (summary.totalCount > 0) {
                        Text(
                            text = "${summary.completedCount}/${summary.totalCount}",
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Right,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.78f)
                        )

                        Spacer(Modifier.height(2.dp))
                    }
                }

                val maxPreviewItems = when {
                    taskH >= 160.dp -> 4
                    taskH >= 120.dp -> 3
                    taskH >= 86.dp -> 2
                    taskH >= 64.dp -> 1
                    else -> 0
                }

                if (maxPreviewItems > 0 && childPreviewRequirements.isNotEmpty()) {
                    val visibleRequirements = childPreviewRequirements.take(maxPreviewItems)
                    val hiddenCount = childPreviewRequirements.size - visibleRequirements.size

                    visibleRequirements.forEach { requirement ->
                        val checked =
                            requirement.status == TaskChildRequirementStatus.COMPLETE

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (checked) "✓" else "○",
                                style = MaterialTheme.typography.labelSmall,
                                color = if (checked) {
                                    MaterialTheme.colorScheme.primary
                                } else {
                                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.62f)
                                }
                            )

                            Spacer(Modifier.width(4.dp))

                            Text(
                                text = requirement.childTitle,
                                modifier = Modifier.weight(1f),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                textAlign = TextAlign.Right,
                                style = MaterialTheme.typography.labelMedium.copy(
                                    textDirection = TextDirection.ContentOrRtl
                                ),
                                color = if (checked) {
                                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.48f)
                                } else {
                                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.78f)
                                }
                            )
                        }
                    }

                    if (hiddenCount > 0) {
                        Text(
                            text = "+$hiddenCount",
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Right,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.58f)
                        )
                    }
                }

            }


        }


        // هندل‌ها وقتی selected یا در حال move
        if (showHandles) {

            // --- Top handle (resize start) ---

            Box(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .fillMaxWidth()
                    .height(handleH)
                    .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.10f))
                    .then(resizeStartModifier),
                contentAlignment = Alignment.Center
            ) {
                // ⬅️ زمان چسبیده به چپ (لحظه‌ای)
                Text(
                    text = minuteToHHmm(displayStart),
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .padding(start = 6.dp)
                )

                // grip وسط
                if (allowResize) {
                    Box(
                        Modifier
                            .width(38.dp)
                            .height(4.dp)
                            .background(
                                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.25f),
                                CircleShape
                            )
                    )
                }
            }




            // --- More menu (move/edit/delete) ---
            Box(
                modifier = Modifier
                    .align(
                        if (selectionH > taskH) Alignment.TopEnd
                        else Alignment.CenterEnd
                    )
                    .padding(
                        end = 4.dp,
                        top = if (selectionH > taskH) 2.dp else 0.dp
                    )
            ) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .background(
                            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.75f),
                            shape = CircleShape
                        )
                        .clickable { menuExpanded = true },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.MoreVert,
                        contentDescription = "More",
                        tint = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.size(20.dp)
                    )
                }

                val canAttachAfterPreviousPomodoro =
                    item.mode == ScheduleMode.POMODORO &&
                            !isVirtual &&
                            previousPomodoroEndMin != null &&
                            previousPomodoroEndMin + dur0 <= DAY_MAX

                DropdownMenu(
                    expanded = menuExpanded,
                    onDismissRequest = { menuExpanded = false }
                ) {
                    if (isVirtual) {
                        DropdownMenuItem(
                            text = { Text("زیرتسک‌ها") },
                            leadingIcon = {
                                Icon(Icons.Filled.Timeline, contentDescription = null)
                            },
                            onClick = {
                                menuExpanded = false
                                onOpenChildren()
                            }
                        )
                        // ✅ فقط یک گزینه برای مجازی‌ها
                        DropdownMenuItem(
                            text = { Text("تبدیل به زمان‌بندی مستقل") },
                            leadingIcon = {
                                Icon(Icons.Default.LinkOff, contentDescription = null)
                                // اگر آیکون بهتری داشتی: LinkOff / CallSplit
                            },
                            onClick = {
                                menuExpanded = false
                                onMakeIndependent()
                            }
                        )
                    } else {


                        if (item.mode == ScheduleMode.POMODORO) {
                            DropdownMenuItem(
                                text = { Text("همین حالا شروع کن") },
                                leadingIcon = {
                                    Icon(Icons.Filled.PlayArrow, contentDescription = null)
                                },
                                onClick = {
                                    menuExpanded = false
                                    onStartPomodoroNow(item.scheduleId)
                                }
                            )

                            DropdownMenuItem(
                                text = { Text("بعد از پومودوی بالا قرار بگیر") },
                                leadingIcon = {
                                    Icon(Icons.Filled.Link, contentDescription = null)
                                },
                                enabled = canAttachAfterPreviousPomodoro,
                                onClick = {
                                    val newStart = previousPomodoroEndMin ?: return@DropdownMenuItem
                                    val newEnd = newStart + dur0

                                    menuExpanded = false

                                    onMoveCommit(
                                        item.scheduleId,
                                        item.start.toLocalDate(),
                                        newStart,
                                        newEnd
                                    )
                                }
                            )
                        }

                        DropdownMenuItem(
                            text = { Text("زیرتسک‌ها") },
                            leadingIcon = {
                                Icon(Icons.Filled.Timeline, contentDescription = null)
                            },
                            onClick = {
                                menuExpanded = false
                                onOpenChildren()
                            }
                        )

                        // ✅ آیتم‌های واقعی: Move / Edit / Delete
                        DropdownMenuItem(
                            text = { Text("Move to pallet") },
                            leadingIcon = { Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null) },
                            onClick = {
                                menuExpanded = false
                                onSendToPallet(item.scheduleId, item.taskId)
                            }
                        )

                        DropdownMenuItem(
                            text = { Text("Edit") },
                            leadingIcon = { Icon(Icons.Filled.Edit, contentDescription = null) },
                            onClick = {
                                menuExpanded = false
                                onEdit(item.taskId)
                            }
                        )

                        DropdownMenuItem(
                            text = { Text("Delete") },
                            leadingIcon = { Icon(Icons.Filled.Delete, contentDescription = null) },
                            onClick = {
                                menuExpanded = false
                                showDeleteConfirm = true
                            }
                        )
                    }
                }
            }




            // --- Bottom handle (resize end) ---

            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .height(handleH)
                    .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.10f))
                    .then(resizeEndModifier),
                contentAlignment = Alignment.Center
            ) {
                // ⬅️ زمان چسبیده به چپ (لحظه‌ای)
                Text(
                    text = minuteToHHmm(displayEnd),
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .padding(start = 6.dp)
                )

                // grip وسط
                if (allowResize) {
                    Box(
                        Modifier
                            .width(38.dp)
                            .height(4.dp)
                            .background(
                                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.25f),
                                CircleShape
                            )
                    )
                }
            }


        }


        if (showDeleteConfirm) {
            AlertDialog(
                onDismissRequest = { showDeleteConfirm = false },
                title = { Text("Delete schedule?") },
                text = { Text("This action cannot be undone.") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            showDeleteConfirm = false
                            onDelete(item.scheduleId)
                        }
                    ) { Text("Delete") }
                },
                dismissButton = {
                    TextButton(onClick = { showDeleteConfirm = false }) { Text("Cancel") }
                }
            )
        }


    }
}


//RunningPomodoroPanelHost فقط مسئول نمایش پنل تایمر فعال است.
//چون collect کردن runningPomodoro داخل این composable انجام می‌شود، tick ثانیه‌ای تایمر فقط همین بخش کوچک را recompose می‌کند، نه کل ScheduleScreen.
@Composable
private fun RunningPomodoroPanelHost(
    viewModel: ScheduleScreenViewModel
) {
    val runningPomodoro by viewModel.runningPomodoro.collectAsState()

    runningPomodoro?.let { running ->
        RunningPomodoroPanel(
            state = running,
            onPause = { viewModel.pauseRunningPomodoro() },
            onResume = { viewModel.resumeRunningPomodoro() },
            onSkip = { viewModel.skipRunningPomodoro() },
            onRestart = { viewModel.restartRunningPomodoro() }
        )
    }
}



private data class PomodoroChainFlags(
    val hasPrevious: Boolean = false,
    val hasNext: Boolean = false
)

private fun computePomodoroChainFlags(
    items: List<ScheduleScreenItem>
): Map<Int, PomodoroChainFlags> {
    val pomodoros = items
        .asSequence()
        .filter { !it.inPallet }
        .filter { it.mode == ScheduleMode.POMODORO }
        .toList()

    if (pomodoros.isEmpty()) return emptyMap()

    val startsByMoment = pomodoros.groupBy {
        it.start.truncatedTo(ChronoUnit.MINUTES)
    }

    val endsByMoment = pomodoros.groupBy {
        it.end.truncatedTo(ChronoUnit.MINUTES)
    }

    val result = LinkedHashMap<Int, PomodoroChainFlags>()

    pomodoros.forEach { item ->
        val startKey = item.start.truncatedTo(ChronoUnit.MINUTES)
        val endKey = item.end.truncatedTo(ChronoUnit.MINUTES)

        val hasPrevious = endsByMoment[startKey]
            .orEmpty()
            .any { it.scheduleId != item.scheduleId }

        val hasNext = startsByMoment[endKey]
            .orEmpty()
            .any { it.scheduleId != item.scheduleId }

        if (hasPrevious || hasNext) {
            result[item.scheduleId] = PomodoroChainFlags(
                hasPrevious = hasPrevious,
                hasNext = hasNext
            )
        }
    }

    return result
}

@Composable
private fun PomodoroChainMarker(
    color: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(22.dp)
            .background(
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.92f),
                shape = CircleShape
            )
            .border(
                width = 1.dp,
                color = color.copy(alpha = 0.85f),
                shape = CircleShape
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Filled.Link,
            contentDescription = null,
            tint = color.copy(alpha = 0.95f),
            modifier = Modifier.size(15.dp)
        )
    }
}


@Composable
private fun RightPallet(
    palletItems: List<ScheduleScreenItem>,
    pomodoroCards: List<PomodoroPalletCardItem>,
    anytimeTasks: List<TaskEntity>,
    selectedTab: PalletPanelTab,
    todayFocusText: String,
    todayDonePomodoroCount: Int,
    todayScheduledPomodoroCount: Int,
    expanded: Boolean,
    isDraggingFromPallet: Boolean,
    onToggle: () -> Unit,
    onSelectTab: (PalletPanelTab) -> Unit,
    onDragStart: (ScheduleScreenItem, Float, Float, Float, Float) -> Unit,
    onPomodoroDragStart: (PomodoroPalletCardItem, Float, Float, Float, Float) -> Unit,
    onAnytimeDragStart: (TaskEntity, Float, Float, Float, Float) -> Unit,
    onDrag: (dx: Float, dy: Float) -> Unit,
    onDragEnd: (scheduleId: Int) -> Unit,
    onDragCancel: () -> Unit,
    onEditTask: (taskId: Int) -> Unit,
    onPomodoroDoneTodayInc: (taskId: Int) -> Unit,
    onPomodoroDoneTodayDec: (taskId: Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val keepContent = expanded || isDraggingFromPallet

    val sortedPomodoroCards = remember(pomodoroCards) {
        pomodoroCards.sortedWith(
            compareBy<PomodoroPalletCardItem> {
                it.doneToday >= it.expectedToday
            }
                .thenBy { it.taskName }
        )
    }

    Row(modifier = modifier.fillMaxHeight()) {

        // دکمه باز/بسته
        // دکمه باز/بسته
        val palletShape = RoundedCornerShape(
            topStart = 18.dp,
            bottomStart = 18.dp
        )

        Column(
            modifier = Modifier
                .width(48.dp)
                .fillMaxHeight()
                .graphicsLayer { alpha = if (isDraggingFromPallet) 0f else 1f }
                .clip(palletShape)
                .background(
                    MaterialTheme.colorScheme.surfaceContainerHigh.copy(alpha = 0.28f)
                )
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.38f),
                    shape = palletShape
                )
        ) {
            PalletVerticalTabButton(
                label = "روزانه",
                selected = selectedTab == PalletPanelTab.DAILY,
                expanded = expanded,
                enabled = !isDraggingFromPallet,
                modifier = Modifier.weight(1f),
                onClick = {
                    if (selectedTab == PalletPanelTab.DAILY) {
                        onToggle()
                    } else {
                        onSelectTab(PalletPanelTab.DAILY)
                    }
                }
            )

            HorizontalDivider(
                thickness = 0.8.dp,
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.28f)
            )

            PalletVerticalTabButton(
                label = "آزاد",
                selected = selectedTab == PalletPanelTab.ANYTIME,
                expanded = expanded,
                enabled = !isDraggingFromPallet,
                modifier = Modifier.weight(1f),
                onClick = {
                    if (selectedTab == PalletPanelTab.ANYTIME) {
                        onToggle()
                    } else {
                        onSelectTab(PalletPanelTab.ANYTIME)
                    }
                }
            )
        }


        // ✅ محتوا را هنگام Drag هم نگه دار
        if (keepContent) {
            // ظاهر را اگر می‌خواهی “بسته” نشان بدهی، عرض را کم کن
            val contentWidth = 160.dp
            val visibleAlpha = if (expanded && !isDraggingFromPallet) 1f else 0f

            val contentShape = RoundedCornerShape(
                topEnd = 18.dp,
                bottomEnd = 18.dp
            )

            Box(
                modifier = Modifier
                    .width(contentWidth)
                    .fillMaxHeight()
                    .graphicsLayer { alpha = visibleAlpha }
                    .clip(contentShape)
                    .background(MaterialTheme.colorScheme.surfaceContainerHigh.copy(alpha = 0.94f))
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.16f),
                        shape = contentShape
                    )
                    .padding(8.dp)
            ) {
                Column(Modifier.fillMaxSize()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 4.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = when (selectedTab) {
                                PalletPanelTab.DAILY -> "روزانه"
                                PalletPanelTab.ANYTIME -> "آزاد"
                            },
                            style = MaterialTheme.typography.titleMedium,
                            maxLines = 1
                        )

                        Column(
                            horizontalAlignment = Alignment.End
                        ) {
                            Text(
                                text = todayFocusText,
                                style = MaterialTheme.typography.labelLarge,
                                maxLines = 1,
                                color = MaterialTheme.colorScheme.primary
                            )

                            Spacer(Modifier.height(3.dp))

                            TodayPomodoroDoneRemainingBar(
                                doneCount = todayDonePomodoroCount,
                                scheduledCount = todayScheduledPomodoroCount
                            )
                        }
                    }

                    HorizontalDivider(
                        thickness = 0.8.dp,
                        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.32f)
                    )

                    Spacer(Modifier.height(8.dp))

                    when (selectedTab) {
                        PalletPanelTab.DAILY -> {
                            LazyColumn {
                                items(sortedPomodoroCards, key = { "pomo_${it.taskId}" }) { c ->
                                    PomodoroPalletCard(
                                        item = c,
                                        onDragStart = onPomodoroDragStart,
                                        onDrag = onDrag,
                                        onDragEnd = { onDragEnd(c.scheduleId) },
                                        onDragCancel = onDragCancel,
                                        onEdit = { onEditTask(c.taskId) },
                                        onDoneTodayInc = { onPomodoroDoneTodayInc(c.taskId) },
                                        onDoneTodayDec = { onPomodoroDoneTodayDec(c.taskId) }
                                    )
                                    HorizontalDivider(thickness = 0.5.dp)
                                }

                                items(palletItems, key = { it.scheduleId }) { t ->
                                    PalletTaskItem(
                                        item = t,
                                        onDragStart = onDragStart,
                                        onDrag = onDrag,
                                        onDragEnd = { onDragEnd(t.scheduleId) },
                                        onDragCancel = onDragCancel,
                                        onEdit = { onEditTask(t.taskId) }
                                    )
                                    HorizontalDivider(thickness = 0.5.dp)
                                }
                            }
                        }

                        PalletPanelTab.ANYTIME -> {
                            AnytimePalletTaskList(
                                tasks = anytimeTasks,
                                onEditTask = onEditTask,
                                onDragStart = onAnytimeDragStart,
                                onDrag = onDrag,
                                onDragEnd = { onDragEnd(-1) },
                                onDragCancel = onDragCancel,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AnytimePalletTaskList(
    tasks: List<TaskEntity>,
    onEditTask: (taskId: Int) -> Unit,
    onDragStart: (TaskEntity, Float, Float, Float, Float) -> Unit,
    onDrag: (dx: Float, dy: Float) -> Unit,
    onDragEnd: () -> Unit,
    onDragCancel: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (tasks.isEmpty()) {
        AnytimePalletPlaceholder(
            modifier = modifier
        )
        return
    }

    LazyColumn(
        modifier = modifier
    ) {
        items(
            items = tasks,
            key = { task -> task.id ?: task.hashCode() }
        ) { task ->
            AnytimePalletTaskCard(
                task = task,
                onEditTask = onEditTask,
                onDragStart = onDragStart,
                onDrag = onDrag,
                onDragEnd = onDragEnd,
                onDragCancel = onDragCancel
            )

            HorizontalDivider(thickness = 0.5.dp)
        }
    }
}

@Composable
private fun AnytimePalletTaskCardVisual(
    task: TaskEntity,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(16.dp)
    val borderColor = colorFromHex(task.color)

    Card(
        modifier = modifier
            .padding(vertical = 6.dp)
            .border(
                width = 1.5.dp,
                color = borderColor,
                shape = shape
            ),
        shape = shape,
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 9.dp),
            verticalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            Text(
                text = task.name,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Right,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.bodyMedium.copy(
                    textDirection = TextDirection.ContentOrRtl
                ),
                color = if (task.isCompleted) {
                    MaterialTheme.colorScheme.onSurfaceVariant
                } else {
                    MaterialTheme.colorScheme.onSurface
                }
            )

            Text(
                text = when (task.childStructure) {
                    TaskChildStructure.LIST_ITEMS -> "آیتم‌های لیست"
                    else -> "کارهای فرعی"
                },
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Right,
                maxLines = 1,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun AnytimePalletTaskCard(
    task: TaskEntity,
    onEditTask: (taskId: Int) -> Unit,
    onDragStart: (TaskEntity, Float, Float, Float, Float) -> Unit,
    onDrag: (dx: Float, dy: Float) -> Unit,
    onDragEnd: () -> Unit,
    onDragCancel: () -> Unit
) {
    val taskId = task.id
    var coords by remember { mutableStateOf<LayoutCoordinates?>(null) }
    var menuExpanded by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .onGloballyPositioned { coords = it }
            .pointerInput(taskId) {
                detectDragGesturesAfterLongPress(
                    onDragStart = { downPos ->
                        val c = coords ?: return@detectDragGesturesAfterLongPress
                        val r = c.localToRoot(downPos)
                        onDragStart(task, r.x, r.y, downPos.x, downPos.y)
                    },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        onDrag(dragAmount.x, dragAmount.y)
                    },
                    onDragEnd = onDragEnd,
                    onDragCancel = onDragCancel
                )
            }
            .clickable {
                menuExpanded = true
            }
    ) {
        AnytimePalletTaskCardVisual(
            task = task,
            modifier = Modifier.fillMaxWidth()
        )

        DropdownMenu(
            expanded = menuExpanded,
            onDismissRequest = { menuExpanded = false }
        ) {
            DropdownMenuItem(
                text = { Text("Edit") },
                leadingIcon = {
                    Icon(Icons.Filled.Edit, contentDescription = null)
                },
                onClick = {
                    menuExpanded = false
                    if (taskId != null) {
                        onEditTask(taskId)
                    }
                }
            )
        }
    }
}

@Composable
private fun AnytimePalletPlaceholder(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .padding(horizontal = 8.dp, vertical = 24.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "هنوز کاری برای پالت آزاد تعریف نشده.",
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun PalletVerticalTabButton(
    label: String,
    selected: Boolean,
    expanded: Boolean,
    enabled: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val selectedColor = MaterialTheme.colorScheme.primary
    val normalColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.72f)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clickable(enabled = enabled) {
                onClick()
            }
            .background(
                if (selected) {
                    MaterialTheme.colorScheme.primary.copy(alpha = 0.10f)
                } else {
                    Color.Transparent
                }
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            maxLines = 1,
            style = MaterialTheme.typography.labelLarge,
            color = if (selected) selectedColor else normalColor,
            modifier = Modifier.graphicsLayer {
                rotationZ = -90f
            }
        )

        if (selected) {
            Icon(
                imageVector = if (expanded) {
                    Icons.AutoMirrored.Filled.ArrowForwardIos
                } else {
                    Icons.AutoMirrored.Filled.ArrowBackIos
                },
                contentDescription = null,
                tint = if (selected) selectedColor else normalColor,
                modifier = Modifier
                    .align(Alignment.Center)
                    .offset(y = 32.dp)
                    .size(15.dp)
            )
        }
    }
}

@Composable
private fun TodayPomodoroDoneRemainingBar(
    doneCount: Int,
    scheduledCount: Int,
    modifier: Modifier = Modifier
) {
    val safeDone = doneCount.coerceAtLeast(0)
    val safeScheduled = scheduledCount.coerceAtLeast(0)

    val progress = remember(safeDone, safeScheduled) {
        when {
            safeScheduled > 0 -> {
                safeDone.toFloat()
                    .div(safeScheduled.toFloat())
                    .coerceIn(0f, 1f)
            }

            safeDone > 0 -> 1f

            else -> 0f
        }
    }

    Row(
        modifier = modifier.width(76.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .height(7.dp)
                .clip(RoundedCornerShape(999.dp))
                .background(
                    MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.85f)
                )
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.45f),
                    shape = RoundedCornerShape(999.dp)
                )
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(progress)
                    .clip(RoundedCornerShape(999.dp))
                    .background(
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.92f)
                    )
                    .align(Alignment.CenterStart)
            )
        }

        Text(
            text = "$safeDone/$safeScheduled",
            style = MaterialTheme.typography.labelSmall,
            maxLines = 1,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun PomodoroPalletCard(
    item: PomodoroPalletCardItem,
    onDragStart: (PomodoroPalletCardItem, Float, Float, Float, Float) -> Unit,
    onDrag: (dx: Float, dy: Float) -> Unit,
    onDragEnd: () -> Unit,
    onDragCancel: () -> Unit,
    onEdit: () -> Unit,
    onDoneTodayInc: () -> Unit,
    onDoneTodayDec: () -> Unit
) {
    var coords by remember { mutableStateOf<LayoutCoordinates?>(null) }

    var menuExpanded by remember { mutableStateOf(false) }

    val canAddMoreToday = item.remainingToday > 0

    val pomoColor = scheduleModeColor(ScheduleMode.POMODORO)
    val categoryBorderColor = colorFromHex(item.categoryColor ?: "#9E9E9E")
    val shape = RoundedCornerShape(18.dp)

    val doneProgress = remember(item.expectedToday, item.doneToday) {
        if (item.expectedToday <= 0) 0f
        else (item.doneToday.toFloat() / item.expectedToday.toFloat())
            .coerceIn(0f, 1f)
    }

    val dragModifier =
        if (canAddMoreToday) {
            Modifier.pointerInput(item.taskId, item.remainingToday) {
                detectDragGesturesAfterLongPress(
                    onDragStart = { downPos ->
                        val c = coords ?: return@detectDragGesturesAfterLongPress
                        val r = c.localToRoot(downPos)
                        onDragStart(item, r.x, r.y, downPos.x, downPos.y)
                    },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        onDrag(dragAmount.x, dragAmount.y)
                    },
                    onDragEnd = onDragEnd,
                    onDragCancel = onDragCancel
                )
            }
        } else {
            Modifier
        }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .onGloballyPositioned { coords = it }
            .then(dragModifier)
            .clickable { menuExpanded = true }
    ) {
        PomodoroPalletCardVisual(
            item = item,
            modifier = Modifier.fillMaxWidth(),
            enabledAlpha = if (canAddMoreToday) 1f else 0.72f
        )

        DropdownMenu(
            expanded = menuExpanded,
            onDismissRequest = { menuExpanded = false }
        ) {
            DropdownMenuItem(
                text = { Text("Edit") },
                leadingIcon = {
                    Icon(Icons.Filled.Edit, contentDescription = null)
                },
                onClick = {
                    menuExpanded = false
                    onEdit()
                }
            )

            HorizontalDivider(thickness = 0.5.dp)

            DropdownMenuItem(
                text = {
                    Column {
                        Text(
                            text = "انجام‌شده‌های امروز",
                            style = MaterialTheme.typography.labelMedium
                        )

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            IconButton(
                                onClick = onDoneTodayDec,
                                modifier = Modifier.size(36.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.KeyboardArrowDown,
                                    contentDescription = null
                                )
                            }

                            Text(
                                text = item.doneToday.toString(),
                                style = MaterialTheme.typography.titleMedium
                            )

                            IconButton(
                                onClick = onDoneTodayInc,
                                modifier = Modifier.size(36.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.KeyboardArrowUp,
                                    contentDescription = null
                                )
                            }
                        }
                    }
                },
                onClick = {
                    // کنترل‌ها داخل خود Row هستند
                }
            )
        }
    }
}


@Composable
private fun PalletTaskItem(
    item: ScheduleScreenItem,
    onDragStart: (item: ScheduleScreenItem, startWindowX: Float, startWindowY: Float, downX: Float, downY: Float) -> Unit,
    onDrag: (dx: Float, dy: Float) -> Unit,
    onDragEnd: () -> Unit,
    onDragCancel: () -> Unit,
    onEdit: () -> Unit
) {
    var coords by remember { mutableStateOf<LayoutCoordinates?>(null) }

    var menuExpanded by remember { mutableStateOf(false) }

    val shape = RoundedCornerShape(16.dp)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .onGloballyPositioned { coords = it }
            .pointerInput(item.scheduleId) {
                detectDragGesturesAfterLongPress(
                    onDragStart = { downPos ->
                        val c = coords ?: return@detectDragGesturesAfterLongPress
                        val r = c.localToRoot(downPos)
                        onDragStart(item, r.x, r.y, downPos.x, downPos.y)
                    },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        onDrag(dragAmount.x, dragAmount.y)
                    },
                    onDragEnd = onDragEnd,
                    onDragCancel = onDragCancel
                )
            }
            .clickable {
                menuExpanded = true
            }
    ) {
        PalletTaskItemVisual(
            item = item,
            modifier = Modifier.fillMaxWidth()
        )

        DropdownMenu(
            expanded = menuExpanded,
            onDismissRequest = { menuExpanded = false }
        ) {
            DropdownMenuItem(
                text = { Text("Edit") },
                leadingIcon = {
                    Icon(Icons.Filled.Edit, contentDescription = null)
                },
                onClick = {
                    menuExpanded = false
                    onEdit()
                }
            )
        }
    }
}

@Composable
private fun PomodoroPalletCardVisual(
    item: PomodoroPalletCardItem,
    modifier: Modifier = Modifier,
    enabledAlpha: Float = 1f
) {
    val pomoColor = scheduleModeColor(ScheduleMode.POMODORO)
    val categoryBorderColor = colorFromHex(item.categoryColor ?: "#9E9E9E")
    val shape = RoundedCornerShape(18.dp)

    val doneProgress = remember(item.expectedToday, item.doneToday) {
        if (item.expectedToday <= 0) {
            0f
        } else {
            (item.doneToday.toFloat() / item.expectedToday.toFloat())
                .coerceIn(0f, 1f)
        }
    }

    Card(
        modifier = modifier
            .height(112.dp)
            .padding(vertical = 2.dp)
            .border(
                width = 2.dp,
                color = categoryBorderColor,
                shape = shape
            )
            .graphicsLayer {
                alpha = enabledAlpha
            },
        shape = shape,
        elevation = CardDefaults.cardElevation(defaultElevation = 5.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(shape)
                .background(pomoColor.copy(alpha = 0.08f))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(doneProgress)
                    .background(categoryBorderColor.copy(alpha = 0.22f))
                    .align(Alignment.CenterStart)
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 10.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    text = "pomodoro",
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = iconFromKey(item.categoryIconName ?: "Category"),
                        contentDescription = null,
                        tint = categoryBorderColor,
                        modifier = Modifier.size(16.dp)
                    )

                    Spacer(Modifier.width(6.dp))

                    Text(
                        text = item.taskName,
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        textAlign = TextAlign.Right,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.labelLarge.copy(
                            textDirection = TextDirection.ContentOrRtl
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Spacer(Modifier.height(2.dp))

                Text(
                    text = "total/done  ${item.totalTarget}/${item.totalDone.toString().padStart(2, '0')}",
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Left,
                    maxLines = 1,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Text(
                    text = "p.p.d/done  ${item.expectedToday}/${item.doneToday.toString().padStart(2, '0')}",
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Left,
                    maxLines = 1,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

@Composable
private fun CategoryIconWithPlate(
    iconName: String?,
    modifier: Modifier = Modifier,
    tint: Color = MaterialTheme.colorScheme.onSurface
) {
    val icon = remember(iconName) { iconFromKey(iconName.orEmpty()) }

    Box(
        modifier = modifier
            .size(26.dp)
            .background(
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.85f),
                shape = RoundedCornerShape(8.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(18.dp),
            tint = tint
        )
    }
}

@Composable
fun CompactTopBar(
    title: String
) {
    Box(
        Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .height(44.dp)
    ) {
        Text(
            text = title,
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(horizontal = 12.dp),
            style = MaterialTheme.typography.titleSmall
        )
    }
}

@Composable
private fun PomodoroCountStepperOverlay(
    count: Int,
    max: Int,
    anchorInRoot: Offset,
    onInc: () -> Unit,
    onDec: () -> Unit,
    onDismiss: () -> Unit
) {
    // کلیک بیرون = بستن
    Box(
        Modifier
            .fillMaxSize()
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) { onDismiss() }
    )

    Box(
        modifier = Modifier
            .offset { IntOffset(anchorInRoot.x.toInt() + 12, anchorInRoot.y.toInt() - 16) }
            .background(
                MaterialTheme.colorScheme.surfaceContainerHigh,
                shape = MaterialTheme.shapes.large
            )
            .padding(horizontal = 14.dp, vertical = 10.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {

            val unlimited = max == Int.MAX_VALUE

            IconButton(
                onClick = onInc,
                enabled = unlimited || count < max,
                modifier = Modifier.size(52.dp)
            ) {
                Icon(Icons.Filled.KeyboardArrowUp, contentDescription = null)
            }

            Text(
                text = count.toString(),
                style = MaterialTheme.typography.titleLarge
            )

            IconButton(
                onClick = onDec,
                enabled = count > 1,
                modifier = Modifier.size(52.dp)
            ) { Icon(Icons.Filled.KeyboardArrowDown, contentDescription = null) }

            Spacer(Modifier.height(4.dp))
            Text(
                text = if (unlimited) "بدون محدودیت" else "1..$max",
                style = MaterialTheme.typography.labelSmall
            )
        }
    }
}


//CurrentTimeOverlay به جای گرفتن عدد scroll، خود ScrollState را می‌گیرد.
//با این کار خواندن vScroll.value داخل همین composable انجام می‌شود و اسکرول عمودی کمتر باعث recomposition سطح بالای ScheduleScreen می‌شود.
@Composable
private fun CurrentTimeOverlay(
    startDate: LocalDate,
    numDays: Int,
    dayWidthDp: Dp,
    hourHeightDp: Dp,
    vScroll: ScrollState,
    palletOverlayWidth: Dp,
    sidebarWidth: Dp = 56.dp,
) {
    fun currentMinuteNow(): LocalDateTime {
        return LocalDateTime.now()
            .withSecond(0)
            .withNano(0)
    }

    val now = remember {
        mutableStateOf(currentMinuteNow())
    }

    val lifecycleOwner = LocalContext.current.findLifecycleOwner()

    DisposableEffect(lifecycleOwner) {
        if (lifecycleOwner == null) {
            onDispose { }
        } else {
            val observer = LifecycleEventObserver { _, event ->
                when (event) {
                    Lifecycle.Event.ON_START,
                    Lifecycle.Event.ON_RESUME -> {
                        // ✅ وقتی کاربر از background / صفحه خاموش برمی‌گردد،
                        // خط زمان همان لحظه با ساعت واقعی sync می‌شود.
                        now.value = currentMinuteNow()
                    }

                    else -> Unit
                }
            }

            lifecycleOwner.lifecycle.addObserver(observer)

            // ✅ همان لحظه‌ی نصب observer هم یک sync فوری انجام بده.
            now.value = currentMinuteNow()

            onDispose {
                lifecycleOwner.lifecycle.removeObserver(observer)
            }
        }
    }

    LaunchedEffect(Unit) {
        while (true) {
            // ✅ ابتدای هر چرخه اول زمان را اصلاح کن، بعد منتظر دقیقه‌ی بعد بمان.
            // این باعث می‌شود اگر coroutine بعد از pause/resume ادامه پیدا کرد،
            // با اولین فرصت مقدار stale روی صفحه نماند.
            now.value = currentMinuteNow()

            val rawNow = LocalDateTime.now()

            // زمان باقی‌مانده تا ابتدای دقیقه‌ی بعد
            val millisUntilNextMinute =
                ((60 - rawNow.second) * 1_000L - rawNow.nano / 1_000_000L)
                    .coerceAtLeast(250L)

            delay(millisUntilNextMinute)
        }
    }

    val today = now.value.toLocalDate()
    val dayIndex = ChronoUnit.DAYS.between(startDate, today).toInt()

    // اگر امروز داخل بازه‌ی ۵ روزه‌ی تایم‌لاین نیست، خط را نشان نده.
    if (dayIndex !in 0 until numDays) return

    val minutesNow =
        now.value.toLocalTime().hour * 60 + now.value.toLocalTime().minute

    val density = LocalDensity.current

    val yContentPx = with(density) {
        hourHeightDp.toPx() * (minutesNow / 60f)
    }

    // ارتفاع overlay بیست‌وچهار dp است؛ خط را وسط همین overlay می‌کشیم.
    // پس خود overlay را نصف ارتفاعش بالاتر می‌بریم تا خط دقیقاً روی yContent بیفتد.
    val overlayHeight = 24.dp
    val halfOverlayHeightPx = with(density) { overlayHeight.toPx() / 2f }

    val vScrollValue = vScroll.value

    val yViewportPx = yContentPx - vScrollValue - halfOverlayHeightPx
    val yViewportDp = with(density) { yViewportPx.toDp() }

    val dash = remember { PathEffect.dashPathEffect(floatArrayOf(12f, 10f), 0f) }
    val red = Color(0xFFE53935)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(end = palletOverlayWidth)
            .offset(y = yViewportDp)
            .height(overlayHeight)
            .drawBehind {
                val lineY = size.height / 2f
                val xStart = with(density) { sidebarWidth.toPx() }

                drawLine(
                    color = red,
                    start = Offset(xStart, lineY),
                    end = Offset(size.width, lineY),
                    strokeWidth = 2.dp.toPx(),
                    pathEffect = dash
                )
            }
    ) {
        Text(
            text = "%02d:%02d".format(minutesNow / 60, minutesNow % 60),
            color = red,
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(start = 8.dp)
        )
    }
}


//TimelineChildRequirementRenderInfo اطلاعات آماده‌ی نمایش زیرتسک‌های یک کارت timeline را نگه می‌دارد:
//لیست requirementهای مربوط به همان کارت و summary شمارش کامل/انجام‌شده.
private data class TimelineChildRequirementRenderInfo(
    val requirements: List<TaskChildRequirementUi>,
    val summary: TaskChildRequirementSummaryUi?
)

private data class MovePreviewResult(
    val start: Int,
    val end: Int
)

private fun calculateMovePreviewRange(
    startMin0: Int,
    minDur: Int,
    dur0: Int,
    dragDy: Float,
    hourHpx: Float,
    snapStep: Int
): MovePreviewResult {
    val minDeltaNow = ((dragDy / hourHpx) * 60f).roundToInt()

    val moveStartRawNow =
        (startMin0 + minDeltaNow).coerceIn(0, 24 * 60 - minDur)

    val desiredStart = snap(moveStartRawNow, snapStep)
    val desiredEnd = desiredStart + dur0
    val r = clampRange(desiredStart, desiredEnd)

    return MovePreviewResult(
        start = r.start,
        end = r.end
    )
}

private fun computePreviousPomodoroEndMinById(
    items: List<ScheduleScreenItem>
): Map<Int, Int> {
    val result = LinkedHashMap<Int, Int>()

    val groups = items
        .asSequence()
        .filter { item ->
            !item.inPallet
        }
        .filter { item ->
            item.scheduleId > 0
        }
        .filter { item ->
            item.mode == ScheduleMode.POMODORO
        }
        .groupBy { item ->
            item.start.toLocalDate()
        }

    groups.values.forEach { dayItems ->
        val sortedByStart = dayItems.sortedWith(
            compareBy<ScheduleScreenItem> { item ->
                minuteOfDay(item.start)
            }.thenBy { item ->
                minuteOfDay(item.end)
            }.thenBy { item ->
                item.scheduleId
            }
        )

        val sortedByEnd = dayItems.sortedWith(
            compareBy<ScheduleScreenItem> { item ->
                minuteOfDay(item.end)
            }.thenBy { item ->
                minuteOfDay(item.start)
            }.thenBy { item ->
                item.scheduleId
            }
        )

        var endCursor = 0
        var bestPreviousEnd: Int? = null

        sortedByStart.forEach { current ->
            val currentStart = minuteOfDay(current.start)

            while (endCursor < sortedByEnd.size) {
                val candidate = sortedByEnd[endCursor]
                val candidateEnd = minuteOfDay(candidate.end)

                if (candidateEnd > currentStart) {
                    break
                }

                if (candidate.scheduleId != current.scheduleId) {
                    bestPreviousEnd = when (val currentBest = bestPreviousEnd) {
                        null -> candidateEnd
                        else -> maxOf(currentBest, candidateEnd)
                    }
                }

                endCursor++
            }

            bestPreviousEnd?.let { previousEnd ->
                result[current.scheduleId] = previousEnd
            }
        }
    }

    return result
}


private fun buildTaskChildOccurrenceKey(
    parentTaskId: Int,
    scheduleId: Int?,
    parentRuleScheduleId: Int?,
    occurrenceDateEpochDay: Long?
): String {
    return "$parentTaskId|${scheduleId ?: "null"}|${parentRuleScheduleId ?: "null"}|${occurrenceDateEpochDay ?: "null"}"
}

private fun buildTaskChildOccurrenceKeyForItem(
    item: ScheduleScreenItem
): String {
    val occurrenceDateEpochDay =
        item.occurrenceDateEpochDay
            ?: item.dateEpochDay
            ?: item.start.toLocalDate().toEpochDay()

    val isVirtualOccurrence =
        item.parentRuleScheduleId != null &&
                item.occurrenceDateEpochDay != null &&
                (item.scheduleId < 0 || item.scheduleId == item.parentRuleScheduleId)

    val realScheduleId =
        if (!isVirtualOccurrence && item.scheduleId > 0) {
            item.scheduleId
        } else {
            null
        }

    val ruleScheduleId =
        if (realScheduleId == null) {
            item.parentRuleScheduleId
        } else {
            null
        }

    return buildTaskChildOccurrenceKey(
        parentTaskId = item.taskId,
        scheduleId = realScheduleId,
        parentRuleScheduleId = ruleScheduleId,
        occurrenceDateEpochDay = occurrenceDateEpochDay
    )
}

@Composable
private fun PalletTaskItemVisual(
    item: ScheduleScreenItem,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(16.dp)

    Card(
        modifier = modifier
            .padding(vertical = 6.dp),
        shape = shape,
        elevation = CardDefaults.cardElevation(defaultElevation = 5.dp),
        colors = CardDefaults.cardColors(
            containerColor = scheduleModeColor(item.mode).copy(alpha = 0.45f)
        )
    ) {
        Box(
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = item.title,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(10.dp),
                style = MaterialTheme.typography.bodyMedium.copy(
                    textDirection = TextDirection.ContentOrRtl
                )
            )
        }
    }
}

private fun requirementMatchesTimelineItem(
    requirement: TaskChildRequirementUi,
    item: ScheduleScreenItem
): Boolean {
    if (requirement.parentTaskId != item.taskId) return false
    if (requirement.status == TaskChildRequirementStatus.CANCELLED) return false

    val occurrenceDateEpochDay =
        item.occurrenceDateEpochDay
            ?: item.dateEpochDay
            ?: item.start.toLocalDate().toEpochDay()

    val isVirtualOccurrence =
        item.parentRuleScheduleId != null &&
                item.occurrenceDateEpochDay != null &&
                (item.scheduleId < 0 || item.scheduleId == item.parentRuleScheduleId)

    val realScheduleId =
        if (!isVirtualOccurrence && item.scheduleId > 0) {
            item.scheduleId
        } else {
            null
        }

    val ruleScheduleId =
        if (realScheduleId == null) {
            item.parentRuleScheduleId
        } else {
            null
        }

    return when (requirement.contextType) {
        TaskChildRequirementContextType.SCHEDULE_OCCURRENCE ->
            requirement.scheduleId == realScheduleId &&
                    requirement.occurrenceDateEpochDay == occurrenceDateEpochDay

        TaskChildRequirementContextType.RULE_OCCURRENCE ->
            requirement.parentRuleScheduleId == ruleScheduleId &&
                    requirement.occurrenceDateEpochDay == occurrenceDateEpochDay

        TaskChildRequirementContextType.DAY ->
            requirement.occurrenceDateEpochDay == occurrenceDateEpochDay

        TaskChildRequirementContextType.PARENT_LIFETIME,
        TaskChildRequirementContextType.LEARNING_CYCLE,
        TaskChildRequirementContextType.MANUAL ->
            true

        TaskChildRequirementContextType.LIST_SESSION ->
            false

        else ->
            false
    }
}

private fun buildTaskChildRequirementSummaryForTimelineItem(
    item: ScheduleScreenItem,
    requirements: List<TaskChildRequirementUi>
): TaskChildRequirementSummaryUi? {
    if (requirements.isEmpty()) return null

    val occurrenceDateEpochDay =
        item.occurrenceDateEpochDay
            ?: item.dateEpochDay
            ?: item.start.toLocalDate().toEpochDay()

    val isVirtualOccurrence =
        item.parentRuleScheduleId != null &&
                item.occurrenceDateEpochDay != null &&
                (item.scheduleId < 0 || item.scheduleId == item.parentRuleScheduleId)

    val realScheduleId =
        if (!isVirtualOccurrence && item.scheduleId > 0) {
            item.scheduleId
        } else {
            null
        }

    val ruleScheduleId =
        if (realScheduleId == null) {
            item.parentRuleScheduleId
        } else {
            null
        }

    return TaskChildRequirementSummaryUi(
        parentTaskId = item.taskId,
        scheduleId = realScheduleId,
        parentRuleScheduleId = ruleScheduleId,
        occurrenceDateEpochDay = occurrenceDateEpochDay,
        totalCount = requirements.size,
        completedCount = requirements.count {
            it.status == TaskChildRequirementStatus.COMPLETE
        }
    )
}

private fun minutesToHHmm(
    totalMinutes: Int
): String {
    val safe = totalMinutes.coerceAtLeast(0)
    val hours = safe / 60
    val minutes = safe % 60

    return "${hours.toString().padStart(2, '0')}:${minutes.toString().padStart(2, '0')}"
}

//>>>>>>>>>>>>>>>> Utils <<<<<<<<<<<<<<<<<<

private const val MIN_GAP_MIN = 5
const val DAY_MIN = 0
const val DAY_MAX = 24 * 60

private const val ZOOM_MIN = 0.6f
private const val ZOOM_MAX = 10.0f


private const val OVERLAP_MAX_LEVEL = 3            // 0..3  => 4 لایه
private const val OVERLAP_STEP_FRAC = 0.25f        // هر لایه 1/4 عرض جابه‌جا + کوچک می‌شود

private const val SUN_OFFICIAL_ZENITH_DEGREES = 90.833
private const val TWILIGHT_BLEND_MINUTES = 35

private val SAQQEZ_TIMELINE_LIGHT_LOCATION = TimelineLightLocation(
    latitude = 36.2499,
    longitude = 46.2735,
    zoneId = ZoneId.of("Asia/Tehran")
)

private data class TimelineLightLocation(
    val latitude: Double,
    val longitude: Double,
    val zoneId: ZoneId
)

private data class TimelineSunWindow(
    val sunriseMinute: Int,
    val sunsetMinute: Int
)

private fun DrawScope.drawSaqqezDayNightTimelineBackground(
    sunWindows: List<TimelineSunWindow>,
    dayWidthDp: Dp,
    hourHeightDp: Dp,
    twilightMinutes: Int = TWILIGHT_BLEND_MINUTES
) {
    val dayW = dayWidthDp.toPx()
    val hourH = hourHeightDp.toPx()
    val dayHeight = hourH * 24f

    val nightColor = Color(0xFF071B3A).copy(alpha = 0.24f)
    val nightSoftColor = Color(0xFF0E2B55).copy(alpha = 0.18f)
    val transitionLineColor = Color(0xFFFFD166).copy(alpha = 0.22f)

    fun yOfMinute(minute: Int): Float {
        return hourH * (minute.coerceIn(DAY_MIN, DAY_MAX) / 60f)
    }

    fun drawMinuteRect(
        dayX: Float,
        fromMinute: Int,
        toMinute: Int,
        color: Color
    ) {
        val from = fromMinute.coerceIn(DAY_MIN, DAY_MAX)
        val to = toMinute.coerceIn(DAY_MIN, DAY_MAX)
        if (to <= from) return

        val top = yOfMinute(from)
        val bottom = yOfMinute(to)

        drawRect(
            color = color,
            topLeft = Offset(dayX, top),
            size = Size(dayW, bottom - top)
        )
    }

    fun drawMinuteGradient(
        dayX: Float,
        fromMinute: Int,
        toMinute: Int,
        colors: List<Color>
    ) {
        val from = fromMinute.coerceIn(DAY_MIN, DAY_MAX)
        val to = toMinute.coerceIn(DAY_MIN, DAY_MAX)
        if (to <= from) return

        val top = yOfMinute(from)
        val bottom = yOfMinute(to)

        drawRect(
            brush = Brush.verticalGradient(
                colors = colors,
                startY = top,
                endY = bottom
            ),
            topLeft = Offset(dayX, top),
            size = Size(dayW, bottom - top)
        )
    }

    sunWindows.forEachIndexed { dayIndex, sunWindow ->
        val dayX = dayIndex * dayW

        val sunriseStart = sunWindow.sunriseMinute - twilightMinutes
        val sunriseEnd = sunWindow.sunriseMinute + twilightMinutes

        val sunsetStart = sunWindow.sunsetMinute - twilightMinutes
        val sunsetEnd = sunWindow.sunsetMinute + twilightMinutes

        // نیمه‌شب تا قبل از روشن‌شدن تدریجی هوا
        drawMinuteRect(
            dayX = dayX,
            fromMinute = DAY_MIN,
            toMinute = sunriseStart,
            color = nightColor
        )

        // گرادیان طلوع: شب → روز
        drawMinuteGradient(
            dayX = dayX,
            fromMinute = sunriseStart,
            toMinute = sunriseEnd,
            colors = listOf(
                nightColor,
                nightSoftColor,
                Color.Transparent
            )
        )

        // گرادیان غروب: روز → شب
        drawMinuteGradient(
            dayX = dayX,
            fromMinute = sunsetStart,
            toMinute = sunsetEnd,
            colors = listOf(
                Color.Transparent,
                nightSoftColor,
                nightColor
            )
        )

        // بعد از تاریک‌شدن تدریجی هوا تا آخر شب
        drawMinuteRect(
            dayX = dayX,
            fromMinute = sunsetEnd,
            toMinute = DAY_MAX,
            color = nightColor
        )

        // خط خیلی ظریف روی خود لحظه‌ی طلوع
        val sunriseY = yOfMinute(sunWindow.sunriseMinute)
        drawLine(
            color = transitionLineColor,
            start = Offset(dayX, sunriseY),
            end = Offset(dayX + dayW, sunriseY),
            strokeWidth = 1.dp.toPx()
        )

        // خط خیلی ظریف روی خود لحظه‌ی غروب
        val sunsetY = yOfMinute(sunWindow.sunsetMinute)
        drawLine(
            color = transitionLineColor,
            start = Offset(dayX, sunsetY),
            end = Offset(dayX + dayW, sunsetY),
            strokeWidth = 1.dp.toPx()
        )
    }
}

private fun calculateTimelineSunWindow(
    date: LocalDate,
    location: TimelineLightLocation
): TimelineSunWindow {
    val sunrise = calculateTimelineSunEventMinute(
        date = date,
        location = location,
        isSunrise = true
    )

    val sunset = calculateTimelineSunEventMinute(
        date = date,
        location = location,
        isSunrise = false
    )

    return TimelineSunWindow(
        sunriseMinute = sunrise ?: 6 * 60,
        sunsetMinute = sunset ?: 18 * 60
    )
}

private fun calculateTimelineSunEventMinute(
    date: LocalDate,
    location: TimelineLightLocation,
    isSunrise: Boolean
): Int? {
    val dayOfYear = date.dayOfYear.toDouble()
    val longitudeHour = location.longitude / 15.0

    val approximateTime = if (isSunrise) {
        dayOfYear + ((6.0 - longitudeHour) / 24.0)
    } else {
        dayOfYear + ((18.0 - longitudeHour) / 24.0)
    }

    val meanAnomaly = (0.9856 * approximateTime) - 3.289

    val trueLongitude = normalizeTimelineDegrees(
        meanAnomaly +
                (1.916 * sinTimelineDegrees(meanAnomaly)) +
                (0.020 * sinTimelineDegrees(2.0 * meanAnomaly)) +
                282.634
    )

    var rightAscension = radiansToTimelineDegrees(
        atan(0.91764 * tanTimelineDegrees(trueLongitude))
    )

    rightAscension = normalizeTimelineDegrees(rightAscension)

    val longitudeQuadrant = floor(trueLongitude / 90.0) * 90.0
    val rightAscensionQuadrant = floor(rightAscension / 90.0) * 90.0

    rightAscension += longitudeQuadrant - rightAscensionQuadrant
    rightAscension /= 15.0

    val sinDeclination = 0.39782 * sinTimelineDegrees(trueLongitude)
    val cosDeclination = cos(asin(sinDeclination))

    val cosHourAngle =
        (cosTimelineDegrees(SUN_OFFICIAL_ZENITH_DEGREES) -
                (sinDeclination * sinTimelineDegrees(location.latitude))) /
                (cosDeclination * cosTimelineDegrees(location.latitude))

    if (cosHourAngle > 1.0 || cosHourAngle < -1.0) {
        return null
    }

    var hourAngle = radiansToTimelineDegrees(acos(cosHourAngle))

    if (isSunrise) {
        hourAngle = 360.0 - hourAngle
    }

    hourAngle /= 15.0

    val localMeanTime =
        hourAngle +
                rightAscension -
                (0.06571 * approximateTime) -
                6.622

    val utcHour = normalizeTimelineHours(localMeanTime - longitudeHour)

    val offsetMinutes = date
        .atTime(12, 0)
        .atZone(location.zoneId)
        .offset
        .totalSeconds / 60

    val localMinute = ((utcHour * 60.0) + offsetMinutes).roundToInt()

    return floorModTimelineDayMinute(localMinute)
}

private fun degreesToTimelineRadians(
    degrees: Double
): Double {
    return degrees * PI / 180.0
}

private fun radiansToTimelineDegrees(
    radians: Double
): Double {
    return radians * 180.0 / PI
}

private fun sinTimelineDegrees(
    degrees: Double
): Double {
    return sin(degreesToTimelineRadians(degrees))
}

private fun cosTimelineDegrees(
    degrees: Double
): Double {
    return cos(degreesToTimelineRadians(degrees))
}

private fun tanTimelineDegrees(
    degrees: Double
): Double {
    return tan(degreesToTimelineRadians(degrees))
}

private fun normalizeTimelineDegrees(
    degrees: Double
): Double {
    val value = degrees % 360.0
    return if (value < 0.0) value + 360.0 else value
}

private fun normalizeTimelineHours(
    hours: Double
): Double {
    val value = hours % 24.0
    return if (value < 0.0) value + 24.0 else value
}

private fun floorModTimelineDayMinute(
    minute: Int
): Int {
    val value = minute % DAY_MAX
    return if (value < 0) value + DAY_MAX else value
}


private fun zoomStep(z: Float) = when {
    z < 1.2f -> 0.1f
    z < 2.5f -> 0.2f
    else -> 0.5f
}
private fun snapZoom(z: Float): Float {
    val step = zoomStep(z)
    return (round(z / step) * step).coerceIn(ZOOM_MIN, ZOOM_MAX)
}

private enum class AutoScrollMode { MOVE, RESIZE_START, RESIZE_END }

private enum class PalletPanelTab {
    DAILY,
    ANYTIME
}

private data class MinRange(val start: Int, val end: Int)

private fun clampRange(start: Int, end: Int): MinRange {
    // Clamp to [0..1440] and keep at least MIN_GAP_MIN
    var s = start.coerceIn(DAY_MIN, DAY_MAX)
    var e = end.coerceIn(DAY_MIN, DAY_MAX)

    if (e - s < MIN_GAP_MIN) {
        // try push end forward first
        val needed = MIN_GAP_MIN - (e - s)
        if (e + needed <= DAY_MAX) e += needed
        else s = (s - needed).coerceAtLeast(DAY_MIN)
    }

    // final safety
    if (e - s < MIN_GAP_MIN) {
        e = (s + MIN_GAP_MIN).coerceAtMost(DAY_MAX)
        s = (e - MIN_GAP_MIN).coerceAtLeast(DAY_MIN)
    }
    return MinRange(s, e)
}

private fun snap(minute: Int, step: Int = 5): Int {
    val m = minute.coerceIn(DAY_MIN, DAY_MAX)
    return ((m + step / 2) / step) * step
}

private fun minuteToHHmm(minute: Int): String {
    val m = minute.coerceIn(0, 24 * 60)
    val h = m / 60
    val mm = m % 60
    return "%02d:%02d".format(h, mm)
}



private fun snapStepForZoom(zoom: Float): Int {
    return when {
        zoom >= 4.0f -> 1
        zoom >= 2.0f -> 5
        else -> 15
    }
}


private data class DayKey(val date: LocalDate, val dayIndex: Int)

private fun minuteOfDay(dt: LocalDateTime): Int =
    dt.toLocalTime().hour * 60 + dt.toLocalTime().minute


/**
 * فقط داخل یک روز همپوشانی را محاسبه می‌کند.
 * اگر بیشتر از ۴ همپوشانی شد، همه اضافه‌ها روی level=3 تلنبار می‌شوند.
 */
private fun computeOverlapLayouts(
    items: List<ScheduleScreenItem>,
    startDate: LocalDate,
    numDays: Int
): Map<Int, OverlapLayout> {

    // فقط آیتم‌های داخل بازه 5 روزه
    val inRange = items.filter {
        it.start.toLocalDate() >= startDate && it.start.toLocalDate() < startDate.plusDays(numDays.toLong())
    }

    // group per-day
    val groups: Map<DayKey, List<ScheduleScreenItem>> = inRange.groupBy { it ->
        val dayIndex = ChronoUnit.DAYS.between(startDate, it.start.toLocalDate()).toInt()
        DayKey(it.start.toLocalDate(), dayIndex)
    }

    val result = mutableMapOf<Int, OverlapLayout>()

    for ((_, dayItems) in groups) {
        // مرتب‌سازی برای sweep-line
        val sorted = dayItems.sortedWith(
            compareBy<ScheduleScreenItem> { minuteOfDay(it.start) }
                .thenBy { minuteOfDay(it.end) }
                .thenBy { it.scheduleId }
        )

        // endMin هر level (اگر null یعنی آزاد)
        val endByLevel = Array<Int?>(OVERLAP_MAX_LEVEL + 1) { null }

        for (it in sorted) {
            val s = minuteOfDay(it.start).coerceIn(0, 1440)
            val e = minuteOfDay(it.end).coerceIn(0, 1440)
            if (e <= s) continue

            // levelهای 0..2 را اگر آزاد بود انتخاب کن
            var chosen = -1
            for (lvl in 0 until OVERLAP_MAX_LEVEL) { // 0..2
                val activeEnd = endByLevel[lvl]
                val free = (activeEnd == null) || (s >= activeEnd)
                if (free) {
                    chosen = lvl
                    endByLevel[lvl] = e
                    break
                }
            }

            // اگر جا نبود => تلنبار روی level=3
            if (chosen == -1) {
                chosen = OVERLAP_MAX_LEVEL
                val prev = endByLevel[OVERLAP_MAX_LEVEL]
                endByLevel[OVERLAP_MAX_LEVEL] = if (prev == null) e else maxOf(prev, e)
            }

            val widthFrac = (1f - chosen * OVERLAP_STEP_FRAC).coerceAtLeast(OVERLAP_STEP_FRAC)
            val offsetFrac = (chosen * OVERLAP_STEP_FRAC).coerceIn(0f, 1f - OVERLAP_STEP_FRAC)

            val idForZ = kotlin.math.abs(it.scheduleId)
            val z = (10_000_000 + chosen * 1_000_000 + idForZ).toFloat()

            result[it.scheduleId] = OverlapLayout(
                level = chosen,
                widthFrac = widthFrac,
                offsetFrac = offsetFrac,
                z = z
            )
        }
    }

    return result
}

private fun ruleIsActiveToday(rule: ScheduleScreenItem, today: LocalDate): Boolean {
    val base = rule.dateEpochDay?.let(LocalDate::ofEpochDay) ?: return false
    if (today.isBefore(base)) return false

    val unit = rule.repeatUnit ?: return false
    val interval = (rule.repeatInterval ?: 1).coerceAtLeast(1)

    return when (unit) {
        RepeatUnit.WEEK -> {
            val mask = (rule.weekdaysMask ?: 0)
            if (mask == 0) return false

            fun bitIndex(dow: DayOfWeek): Int = when (dow) {
                DayOfWeek.SATURDAY -> 0
                DayOfWeek.SUNDAY -> 1
                DayOfWeek.MONDAY -> 2
                DayOfWeek.TUESDAY -> 3
                DayOfWeek.WEDNESDAY -> 4
                DayOfWeek.THURSDAY -> 5
                DayOfWeek.FRIDAY -> 6
            }

            val allowed = (mask and (1 shl bitIndex(today.dayOfWeek))) != 0
            if (!allowed) return false

            val weeks = ChronoUnit.WEEKS.between(base, today)
            weeks >= 0 && (weeks % interval == 0L)
        }

        RepeatUnit.DAY -> {
            val days = ChronoUnit.DAYS.between(base, today)
            days >= 0 && (days % interval == 0L)
        }

        else -> true
    }
}


private fun ruleIsActiveOnDate(rule: ScheduleScreenItem, date: LocalDate): Boolean {
    val base = rule.dateEpochDay?.let(LocalDate::ofEpochDay) ?: return false
    if (date.isBefore(base)) return false

    val unit = rule.repeatUnit ?: return false
    val interval = (rule.repeatInterval ?: 1).coerceAtLeast(1)

    return when (unit) {
        RepeatUnit.WEEK -> {
            val mask = (rule.weekdaysMask ?: 0)
            if (mask == 0) return false

            fun bitIndex(dow: DayOfWeek): Int = when (dow) {
                DayOfWeek.SATURDAY -> 0
                DayOfWeek.SUNDAY -> 1
                DayOfWeek.MONDAY -> 2
                DayOfWeek.TUESDAY -> 3
                DayOfWeek.WEDNESDAY -> 4
                DayOfWeek.THURSDAY -> 5
                DayOfWeek.FRIDAY -> 6
            }

            val allowed = (mask and (1 shl bitIndex(date.dayOfWeek))) != 0
            if (!allowed) return false

            val weeks = ChronoUnit.WEEKS.between(base, date)
            weeks >= 0 && (weeks % interval == 0L)
        }

        RepeatUnit.DAY -> {
            val days = ChronoUnit.DAYS.between(base, date)
            days >= 0 && (days % interval == 0L)
        }

        else -> {
            // فعلاً مثل DAY
            val days = ChronoUnit.DAYS.between(base, date)
            days >= 0 && (days % interval == 0L)
        }
    }
}

@Composable
private fun RunningPomodoroPanel(
    state: RunningPomodoroUiState,
    onPause: () -> Unit,
    onResume: () -> Unit,
    onSkip: () -> Unit,
    onRestart: () -> Unit
) {
    val resumePulse by rememberInfiniteTransition(label = "resume_pulse")
        .animateFloat(
            initialValue = 1f,
            targetValue = if (state.isPaused) 1.25f else 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 550),
                repeatMode = RepeatMode.Reverse
            ),
            label = "resume_pulse_scale"
        )

    val resumeAlpha by rememberInfiniteTransition(label = "resume_alpha")
        .animateFloat(
            initialValue = 0.15f,
            targetValue = if (state.isPaused) 0.65f else 0.15f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 550),
                repeatMode = RepeatMode.Reverse
            ),
            label = "resume_pulse_alpha"
        )
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(  start= 52.dp,end= 106.dp,top= 32.dp),
//        elevation = CardDefaults.cardElevation(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.78f)
        )
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(state.title, style = MaterialTheme.typography.titleMedium)



            Text(
                text = "انتظار تا شروع: ${formatSeconds(state.waitingSeconds)}",
                color = Color(0xFF4CAF50)
            )

            Text(
                text = "تمرکز: ${formatSeconds(state.focusElapsedSeconds)}",
                color = Color.Red
            )

            if (state.phase == PomodoroRunPhase.BREAK || state.phase == PomodoroRunPhase.FINISHED) {
                Text(
                    text = "استراحت: ${formatSeconds(state.breakElapsedSeconds)}",
                    color = MaterialTheme.colorScheme.tertiary
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                IconButton(
                    onClick = onPause,
                    enabled = !state.isPaused && state.phase != PomodoroRunPhase.FINISHED
                ) {
                    Icon(Icons.Filled.Pause, contentDescription = "Pause")
                }

                IconButton(
                    onClick = onResume,
                    enabled = state.isPaused,
                    modifier = Modifier
                        .graphicsLayer {
                            scaleX = if (state.isPaused) resumePulse else 1f
                            scaleY = if (state.isPaused) resumePulse else 1f
                        }
                        .background(
                            color = if (state.isPaused)
                                MaterialTheme.colorScheme.primary.copy(alpha = resumeAlpha)
                            else
                                Color.Transparent,
                            shape = CircleShape
                        )
                ) {
                    Icon(
                        imageVector = Icons.Filled.PlayArrow,
                        contentDescription = "Resume",
                        tint = if (state.isPaused)
                            MaterialTheme.colorScheme.primary
                        else
                            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                    )
                }

                IconButton(
                    onClick = onSkip,
                    enabled = state.phase != PomodoroRunPhase.FINISHED
                ) {
                    Icon(
                        imageVector = Icons.Filled.SkipNext,
                        contentDescription = "Skip"
                    )
                }

                IconButton(
                    onClick = onRestart,
                    enabled = state.phase != PomodoroRunPhase.FINISHED
                ) {
                    Icon(
                        imageVector = Icons.Filled.Refresh,
                        contentDescription = "Restart"
                    )
                }
            }
        }
    }
}

private fun formatSeconds(totalSeconds: Long): String {
    val m = totalSeconds / 60
    val s = totalSeconds % 60
    return "%02d:%02d".format(m, s)
}

private tailrec fun Context.findLifecycleOwner(): LifecycleOwner? {
    return when (this) {
        is LifecycleOwner -> this
        is ContextWrapper -> baseContext.findLifecycleOwner()
        else -> null
    }
}