package com.example.compoundeffectV1_01.ui.scheduleScreen

import androidx.compose.animation.core.snap
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.material3.*
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
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.compoundeffectV1_01.data.dataBaseRoom.tables.task.Task
import com.example.compoundeffectV1_01.ui.navigation.bottomBarDestinations
import com.example.compoundeffectV1_01.ui.navigation.AppRoutes
import com.example.compoundeffectV1_01.utils.colorFromHex
import com.example.compoundeffectV1_01.utils.iconFromKey
import kotlinx.coroutines.delay
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import kotlin.math.roundToInt

@Composable
fun ScheduleScreen2(
    viewModel: ScheduleScreenViewModel = hiltViewModel()
) {
    val items by viewModel.timelineItems.collectAsState()
    val pallet by viewModel.palletTasks.collectAsState()


    // ✅ تنظیمات تایم‌لاین
    val numDays = 5
    var startDate by rememberSaveable { mutableStateOf(LocalDate.now()) }

    var verticalZoom by rememberSaveable { mutableStateOf(1f) }
    var horizontalZoom by rememberSaveable { mutableStateOf(1f) }

    val hourHeightDp = (72.dp * verticalZoom).coerceIn(48.dp, 140.dp)
    val dayWidthDp = (220.dp * horizontalZoom).coerceIn(160.dp, 360.dp)

    val vScroll = rememberScrollState()
    val hScroll = rememberScrollState()

    var draggingTask by remember { mutableStateOf<Task?>(null) }
    var dragX by remember { mutableStateOf(0f) }
    var dragY by remember { mutableStateOf(0f) }

// برای اینکه بفهمیم Grid کجای صفحه‌ست
    var gridOriginInWindow by remember { mutableStateOf(Offset.Zero) }
    var palletExpanded by rememberSaveable { mutableStateOf(false) }

    val palletContentWidth = 200.dp
    val palletOverlayWidth = 18.dp + if (palletExpanded) palletContentWidth else 0.dp

    val pendingMove = remember { mutableStateMapOf<Int, PendingMove>() } // key = scheduleId


    var selectedScheduleId by rememberSaveable { mutableStateOf<Int?>(null) }

    LaunchedEffect(items) {
        items.forEach { ti ->
            val pm = pendingMove[ti.scheduleId] ?: return@forEach

            val curDate = ti.start.toLocalDate()
            val curStart = ti.start.toLocalTime().hour * 60 + ti.start.toLocalTime().minute
            val curEnd = ti.end.toLocalTime().hour * 60 + ti.end.toLocalTime().minute

            if (curDate == pm.date && curStart == pm.startMin && curEnd == pm.endMin) {
                pendingMove.remove(ti.scheduleId)
            }
        }
    }






    Scaffold(
        modifier = Modifier
//        topBar = {
//            TopAppBar(
//                title = { Text("ScheduleScreen2") }
//            )
//        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.surface)
        ) {

            Column(Modifier.fillMaxSize()) {

                // --- Header (روزها) ---
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(hScroll)
                        .padding(start = 56.dp, end = palletOverlayWidth) // جای ستون ساعت
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
                                text = day.toString(), // بعداً شمسی/فارسی می‌کنیم
                                style = MaterialTheme.typography.titleSmall
                            )
                        }
                    }
                }

                HorizontalDivider(thickness = 0.5.dp)

                // --- بدنه: Sidebar ساعت + Grid ---
                Row(Modifier.fillMaxSize()) {

                    // Sidebar ساعت
                    Column(
                        modifier = Modifier
                            .width(56.dp)
                            .verticalScroll(vScroll)
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
                            .padding(end = palletOverlayWidth)
                            .verticalScroll(vScroll)
                            .horizontalScroll(hScroll)
                            .onGloballyPositioned { coords ->
                                gridOriginInWindow = coords.positionInWindow()
                            }
                    ) {
                        val totalHeight = hourHeightDp * 24
                        val totalWidth = dayWidthDp * numDays
                        val colorFor = MaterialTheme.colorScheme.outlineVariant

                        // پس‌زمینه Grid + خطوط
                        Box(
                            modifier = Modifier
                                .width(totalWidth)
                                .height(totalHeight)
                                .drawBehind {
                                    val hourH = hourHeightDp.toPx()
                                    val dayW = dayWidthDp.toPx()

                                    // خطوط افقی ساعت
                                    repeat(24) { i ->
                                        val y = i * hourH
                                        drawLine(
                                            color = colorFor,
                                            start = Offset(0f, y),
                                            end = Offset(size.width, y),
                                            strokeWidth = 1.dp.toPx()
                                        )
                                    }

                                    // خطوط عمودی روزها
                                    repeat(numDays + 1) { i ->
                                        val x = i * dayW
                                        drawLine(
                                            color = colorFor,
                                            start = Offset(x, 0f),
                                            end = Offset(x, size.height),
                                            strokeWidth = 1.dp.toPx()
                                        )
                                    }
                                }
                        ) {
                            // ✅ خط زمان (اگر امروز داخل بازه بود)
                            CurrentTimeLine(
                                startDate = startDate,
                                numDays = numDays,
                                dayWidthDp = dayWidthDp,
                                hourHeightDp = hourHeightDp
                            )

                            // ✅ آیتم‌ها روی Grid
                            items.forEach { it ->

                                val pm = pendingMove[it.scheduleId]
                                val displayItem = if (pm == null) it else it.copy(
                                    start = LocalDateTime.of(pm.date, java.time.LocalTime.MIN)
                                        .plusMinutes(pm.startMin.toLong()),
                                    end = LocalDateTime.of(pm.date, java.time.LocalTime.MIN)
                                        .plusMinutes(pm.endMin.toLong())
                                )

                                TimelineItemBox(
                                    item = displayItem,
                                    startDate = startDate,
                                    dayWidthDp = dayWidthDp,
                                    hourHeightDp = hourHeightDp,
                                    numDays = numDays,
                                    selected = (selectedScheduleId == displayItem.scheduleId),
                                    onToggleSelected = {
                                        selectedScheduleId =
                                            if (selectedScheduleId == displayItem.scheduleId) null else displayItem.scheduleId
                                    },
                                    onMoveCommit = { scheduleId, date, s, e ->
                                        pendingMove[scheduleId] = PendingMove(date, s, e)
                                        viewModel.moveSchedule(scheduleId, date, s, e)
                                    },
                                    onResizeEndCommit = { scheduleId, newEnd ->
                                        viewModel.resizeScheduleEnd(scheduleId, newEnd)
                                    },
                                    onResizeStartCommit = { scheduleId, newStart ->
                                        viewModel.resizeScheduleStart(scheduleId, newStart)
                                    },
                                    onSendToPallet = { scheduleId, taskId ->
                                        viewModel.sendScheduleToPallet(scheduleId, taskId)
                                        selectedScheduleId = null
                                    }

                                )
                            }

                        }
                    }
                }
            }

            RightPallet(
                tasks = pallet,
                expanded = palletExpanded,
                onToggle = { palletExpanded = !palletExpanded },
                onStartDrag = { task, sx, sy ->
                    draggingTask = task
                    dragX = sx
                    dragY = sy
                },
                modifier = Modifier.align(Alignment.CenterEnd)
            )

            // ✅ Drag overlay
            if (draggingTask != null) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .pointerInput(draggingTask?.id) {
                            detectDragGestures(
                                onDrag = { change, dragAmount ->
                                    change.consume()
                                    dragX += dragAmount.x
                                    dragY += dragAmount.y
                                },
                                onDragEnd = {
                                    // Drop
                                    val t = draggingTask ?: return@detectDragGestures.also {
                                        draggingTask = null
                                    }

                                    // مختصات drop نسبت به Grid
                                    val localX = dragX - gridOriginInWindow.x
                                    val localY = dragY - gridOriginInWindow.y

                                    // تبدیل به dayIndex و minute
                                    val dayIndex = (localX / dayWidthDp.toPx()).toInt()
                                    val minuteOfDay = ((localY / hourHeightDp.toPx()) * 60f).toInt()

                                    if (dayIndex in 0 until numDays && minuteOfDay in 0 until 24 * 60) {
                                        val date = startDate.plusDays(dayIndex.toLong())

                                        val startMin = minuteOfDay.coerceIn(0, 24 * 60 - 15)
                                        val endMin =
                                            (startMin + 60).coerceAtMost(24 * 60) // پیش‌فرض 1 ساعت

                                        viewModel.dropTaskToSchedule(
                                            taskId = t.id!!,
                                            date = date,
                                            startMinute = startMin,
                                            endMinute = endMin
                                        )
                                    }

                                    draggingTask = null
                                },
                                onDragCancel = { draggingTask = null }
                            )
                        }
                ) {
                    // کارت شناور
                    Box(
                        modifier = Modifier
                            .offset { IntOffset(dragX.toInt(), dragY.toInt()) }
                            .background(
                                colorFromHex(draggingTask!!.color).copy(alpha = 0.85f),
                                shape = MaterialTheme.shapes.medium
                            )
                            .padding(horizontal = 12.dp, vertical = 10.dp)
                    ) {
                        Text(draggingTask!!.name, maxLines = 1)
                    }
                }
            }

        }


    }
}


@Composable
private fun CurrentTimeLine(
    startDate: LocalDate,
    numDays: Int,
    dayWidthDp: Dp,
    hourHeightDp: Dp
) {
    val now = remember { mutableStateOf(LocalDateTime.now()) }

    // هر 60 ثانیه آپدیت
    LaunchedEffect(Unit) {
        while (true) {
            delay(60_000)
            now.value = LocalDateTime.now()
        }
    }

    val today = now.value.toLocalDate()
    val dayIndex = ChronoUnit.DAYS.between(startDate, today).toInt()
    if (dayIndex !in 0 until numDays) return

    val minutes = now.value.toLocalTime().hour * 60 + now.value.toLocalTime().minute
    val y = hourHeightDp * (minutes / 60f)

    val xStart = dayWidthDp * dayIndex
    val xEnd = xStart + dayWidthDp

    Box(
        modifier = Modifier
            .offset(x = xStart, y = y)
            .width(dayWidthDp)
            .height(2.dp)
            .background(MaterialTheme.colorScheme.primary)
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun TimelineItemBox(
    item: TaskTimelineItem,
    startDate: LocalDate,
    dayWidthDp: Dp,
    hourHeightDp: Dp,
    numDays: Int,
    selected: Boolean,
    onToggleSelected: () -> Unit,
    onMoveCommit: (scheduleId: Int, newDate: LocalDate, newStartMin: Int, newEndMin: Int) -> Unit,
    onResizeEndCommit: (scheduleId: Int, newEndMin: Int) -> Unit,
    onResizeStartCommit: (scheduleId: Int, newStartMin: Int) -> Unit,
    onSendToPallet: (scheduleId: Int, taskId: Int) -> Unit,
) {
    val density = LocalDensity.current
    val dayWpx = with(density) { dayWidthDp.toPx() }
    val hourHpx = with(density) { hourHeightDp.toPx() }

    val dayIndex0 = ChronoUnit.DAYS.between(startDate, item.start.toLocalDate()).toInt()
    if (dayIndex0 !in 0 until numDays) return

    val minDur = 15
    val startMin0 = item.start.toLocalTime().hour * 60 + item.start.toLocalTime().minute
    val endMin0 = item.end.toLocalTime().hour * 60 + item.end.toLocalTime().minute
    val dur0 = (endMin0 - startMin0).coerceAtLeast(minDur)

    // ---------------- Move state ----------------
    var dragDx by remember(item.scheduleId) { mutableStateOf(0f) }
    var dragDy by remember(item.scheduleId) { mutableStateOf(0f) }

    val dayDelta = (dragDx / dayWpx).toInt()
    val minDelta = ((dragDy / hourHpx) * 60f).roundToInt()

    val moveDayIndex = (dayIndex0 + dayDelta).coerceIn(0, numDays - 1)
    val moveStartRaw = (startMin0 + minDelta).coerceIn(0, 24 * 60 - minDur)
    val desiredMoveStart = snap(moveStartRaw, 15)
    val desiredMoveEnd = desiredMoveStart + dur0

    val moveRange = clampRange(desiredMoveStart, desiredMoveEnd)
    val moveStart = moveRange.start
    val moveEnd = moveRange.end

    // ---------------- Resize state ----------------
    var topDy by remember(item.scheduleId) { mutableStateOf(0f) }
    var bottomDy by remember(item.scheduleId) { mutableStateOf(0f) }

    val topDeltaMin = ((topDy / hourHpx) * 60f).roundToInt()
    val bottomDeltaMin = ((bottomDy / hourHpx) * 60f).roundToInt()

    val resizeStartRaw = (startMin0 + topDeltaMin)
        .coerceIn(0, (endMin0 - minDur).coerceAtLeast(0))
    val desiredResizeStart = snap(resizeStartRaw, 15)
    val rStart = clampRange(desiredResizeStart, endMin0)
    val resizeStart = rStart.start


    val resizeEndRaw = (endMin0 + bottomDeltaMin)
        .coerceIn((startMin0 + minDur).coerceAtMost(24 * 60), 24 * 60)
    val desiredResizeEnd = snap(resizeEndRaw, 15)
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


    val x = dayWidthDp * displayDayIndex
    val y = hourHeightDp * (displayStart / 60f)
    val h = hourHeightDp * (((displayEnd - displayStart).coerceAtLeast(minDur)) / 60f)

    val minSelectedHeight = 110.dp

    val taskH = h.coerceAtLeast(44.dp)
    val selectionH = if (selected) maxOf(taskH, minSelectedHeight) else taskH
    val extra = if (selected) (selectionH - taskH) / 2 else 0.dp
    val dashed = selected && selectionH > taskH

    val containerY = (y + 2.dp) - extra





    // ✅ بعد از هر آپدیت از DB، state های drag/resize ریست بشن تا پرش نداشته باشیم
    LaunchedEffect(item.scheduleId, item.start, item.end) {
        dragDx = 0f
        dragDy = 0f
        topDy = 0f
        bottomDy = 0f
    }







    val handleH = 16.dp
    val colorForBorder =MaterialTheme.colorScheme.onSurface.copy(alpha = 0.45f)

    //  Box اصلی
    Box(
        modifier = Modifier
            .offset(x = x + 6.dp, y = containerY)
            .width(dayWidthDp - 12.dp)
            .height(selectionH)
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


        //  آیکون گروه + نام تسک
        Row(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(start = 8.dp, top = 16.dp, end = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            CategoryIconWithPlate(iconName = item.categoryIconName)
            Spacer(Modifier.width(8.dp))
            Text(
                text = item.name,
                style = MaterialTheme.typography.labelLarge,
                maxLines = 1
            )
        }



        //  باکس اصلی
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .width(dayWidthDp - 12.dp)
                .height(taskH)
                .background(
                    color = colorFromHex(item.colorHex).copy(alpha = 0.85f),
                    shape = MaterialTheme.shapes.medium
                )
        ) {


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
                            onDragStart = {
                                isMoving = true
                                isResizingStart = false
                                isResizingEnd = false
                            },
                            onDrag = { change, dragAmount ->
                                change.consume()
                                dragDx += dragAmount.x
                                dragDy += dragAmount.y
                            },
                            onDragEnd = {
                                // محاسبه نهایی با dragDx/dragDy فعلی
                                val dayDelta2 = (dragDx / dayWpx).toInt()
                                val minDelta2 = ((dragDy / hourHpx) * 60f).roundToInt()

                                val moveDayIndex2 = (dayIndex0 + dayDelta2).coerceIn(0, numDays - 1)
                                val moveStartRaw2 =
                                    (startMin0 + minDelta2).coerceIn(0, 24 * 60 - minDur)
                                val moveStart2 = snap(moveStartRaw2, 15)
                                val finalDate = startDate.plusDays(moveDayIndex2.toLong())

                                val desiredStart = snap(moveStartRaw2, 15)
                                val desiredEnd = desiredStart + dur0
                                val r = clampRange(desiredStart, desiredEnd)

                                onMoveCommit(item.scheduleId, finalDate, r.start, r.end)
                                isMoving = false
                                dragDx = 0f
                                dragDy = 0f
                            },
                            onDragCancel = {
                                isMoving = false
                                dragDx = 0f
                                dragDy = 0f
                            }
                        )
                    }
                    .padding(start = 8.dp, top = 34.dp, end = 8.dp, bottom = 8.dp)
            ) {

                if (item.description.isNotBlank()) {
                    Text(
                        item.description,
                        style = MaterialTheme.typography.labelSmall,
                        maxLines = 1
                    )
                }
            }



        }




        // ✅ هندل‌ها فقط وقتی selected
        if (selected) {

            // --- Top handle (resize start) ---
            Box(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .fillMaxWidth()
                    .height(handleH)
                    .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.10f))
                    .pointerInput(item.scheduleId, item.start, item.end) {
                        detectVerticalDragGestures(
                            onDragStart = {
                                isResizingStart = true
                                isMoving = false
                                isResizingEnd = false
                            },
                            onVerticalDrag = { change, dragAmount ->
                                change.consume()
                                topDy += dragAmount
                            },
                            onDragEnd = {
                                onResizeStartCommit(item.scheduleId, resizeStart)
                                isResizingStart = false
                                topDy = 0f
                            },
                            onDragCancel = {
                                isResizingStart = false
                                topDy = 0f
                            }
                        )
                    },
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



            // to pallet
            Box(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(end = 6.dp)
                    .size(34.dp)
                    .background(
                        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.85f),
                        shape = CircleShape
                    )
                    .clickable {
                        onSendToPallet(item.scheduleId, item.taskId)
                    },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward, // یا ArrowForwardIos
                    contentDescription = "Send to pallet",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }


            // --- Bottom handle (resize end) ---
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .height(handleH)
                    .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.10f))
                    .pointerInput(item.scheduleId, item.start, item.end) {
                        detectVerticalDragGestures(
                            onDragStart = {
                                isResizingEnd = true
                                isMoving = false
                                isResizingStart = false
                            },
                            onVerticalDrag = { change, dragAmount ->
                                change.consume()
                                bottomDy += dragAmount
                            },
                            onDragEnd = {
                                onResizeEndCommit(item.scheduleId, resizeEnd)
                                isResizingEnd = false
                                bottomDy = 0f
                            },
                            onDragCancel = {
                                isResizingEnd = false
                                bottomDy = 0f
                            }
                        )
                    },
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
}







@Composable
private fun RightPallet(
    tasks: List<Task>,
    expanded: Boolean,
    onToggle: () -> Unit,
    onStartDrag: (Task, startX: Float, startY: Float) -> Unit,
    modifier: Modifier = Modifier
) {
    val width = if (expanded) 200.dp else 18.dp

    Row(modifier = modifier.fillMaxHeight()) {
        // دکمه باز/بسته
        Box(
            modifier = Modifier
                .width(18.dp)
                .fillMaxHeight()
                .clickable { onToggle() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (expanded) Icons.Filled.ArrowForwardIos else Icons.Filled.ArrowBackIosNew,
                contentDescription = null
            )
        }

        // محتوا
        if (expanded) {
            Column(
                modifier = Modifier
                    .width(width)
                    .fillMaxHeight()
                    .background(MaterialTheme.colorScheme.surfaceContainerHigh)
                    .padding(8.dp)
            ) {
                Text("Pallet", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(8.dp))

                LazyColumn {
                    items(tasks, key = { it.id!! }) { t ->
                        PalletTaskItem(
                            task = t,
                            onStartDrag = onStartDrag
                        )
                        HorizontalDivider(thickness = 0.5.dp)
                    }
                }
            }
        }
    }
}

@Composable
private fun PalletTaskItem(
    task: Task,
    onStartDrag: (Task, startWindowX: Float, startWindowY: Float) -> Unit
) {
    var origin by remember { mutableStateOf(Offset.Zero) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .onGloballyPositioned { origin = it.positionInWindow() }
            .padding(vertical = 6.dp)
            .background(colorFromHex(task.color).copy(alpha = 0.25f), MaterialTheme.shapes.medium)
            .pointerInput(task.id) {
                detectDragGestures(
                    onDragStart = { local ->
                        onStartDrag(task, origin.x + local.x, origin.y + local.y)
                    },
                    onDrag = { change, _ ->
                        // ✅ مهم: فقط consume کنیم که همون لمس روی آیتم، کلیک/اسکرول لیست رو قفل نکنه
                        change.consume()
                    }
                )
            }
            .padding(10.dp)
    ) {
        Text(task.name, maxLines = 1)
    }
}


@Composable
private fun CategoryIconWithPlate(
    iconName: String?,
    modifier: Modifier = Modifier
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
            tint = MaterialTheme.colorScheme.onSurface
        )
    }
}



//>>>>>>>>>>>>>>>> Utils <<<<<<<<<<<<<<<<<<

private const val MIN_GAP_MIN = 5
private const val DAY_MIN = 0
private const val DAY_MAX = 24 * 60

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
