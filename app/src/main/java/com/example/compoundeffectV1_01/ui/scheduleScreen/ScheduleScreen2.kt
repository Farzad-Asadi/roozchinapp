package com.example.compoundeffectV1_01.ui.scheduleScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.compoundeffectV1_01.data.dataBaseRoom.tables.task.Task
import com.example.compoundeffectV1_01.ui.navigation.bottomBarDestinations
import com.example.compoundeffectV1_01.ui.navigation.AppRoutes
import com.example.compoundeffectV1_01.utils.colorFromHex
import kotlinx.coroutines.delay
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

@OptIn(ExperimentalMaterial3Api::class)
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
                        .padding(start = 56.dp , end = palletOverlayWidth) // جای ستون ساعت
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
                                TimelineItemBox(
                                    item = it,
                                    startDate = startDate,
                                    dayWidthDp = dayWidthDp,
                                    hourHeightDp = hourHeightDp
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

@Composable
private fun TimelineItemBox(
    item: TaskTimelineItem,
    startDate: LocalDate,
    dayWidthDp: Dp,
    hourHeightDp: Dp
) {
    val dayIndex = ChronoUnit.DAYS.between(startDate, item.start.toLocalDate()).toInt()
    if (dayIndex < 0) return

    val startMin = item.start.toLocalTime().hour * 60 + item.start.toLocalTime().minute
    val endMin = item.end.toLocalTime().hour * 60 + item.end.toLocalTime().minute
    val durationMin = (endMin - startMin).coerceAtLeast(15)

    val x = dayWidthDp * dayIndex
    val y = hourHeightDp * (startMin / 60f)
    val h = hourHeightDp * (durationMin / 60f)

    Box(
        modifier = Modifier
            .offset(x = x + 6.dp, y = y + 2.dp)
            .width(dayWidthDp - 12.dp)
            .height(h.coerceAtLeast(28.dp))
            .background(
                color = colorFromHex(item.colorHex).copy(alpha = 0.85f),
                shape = MaterialTheme.shapes.medium
            )
            .padding(8.dp)
    ) {
        Column {
            Text(
                text = item.name,
                style = MaterialTheme.typography.labelLarge,
                maxLines = 1
            )
            if (item.description.isNotBlank()) {
                Text(
                    text = item.description,
                    style = MaterialTheme.typography.labelSmall,
                    maxLines = 1
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

