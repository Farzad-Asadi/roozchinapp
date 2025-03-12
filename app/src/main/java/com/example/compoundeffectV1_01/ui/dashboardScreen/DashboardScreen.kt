package com.example.compoundeffectV1_01.ui.dashboardScreen


import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.compoundeffectV1_01.AppViewModelProvider
import com.example.compoundeffectV1_01.data.room.event.Event
import com.example.compoundeffectV1_01.utils.EventTimeFormatter
import com.example.compoundeffectV1_01.utils.HourFormatter
import com.example.compoundeffectV1_01.utils.LoadingScreen
import com.example.compoundeffectV1_01.utils.calculateHourHeight
import com.example.compoundeffectV1_01.utils.convertToPersianDate
import com.example.compoundeffectV1_01.utils.currentTimeHeightPx
import com.example.compoundeffectV1_01.utils.eventData
import com.example.compoundeffectV1_01.utils.eventHeightPx
import com.example.compoundeffectV1_01.utils.stringToColor
import com.example.compoundeffectV1_01.utils.timeInstanceToLocalDate
import kotlinx.coroutines.delay
import java.time.LocalDate
import java.time.LocalTime
import java.time.temporal.ChronoUnit
import java.util.Calendar
import kotlin.math.roundToInt

@Composable
fun DashboardScreen(
    modifier: Modifier = Modifier,
    viewModel: DashboardScreenViewModel = viewModel(factory = AppViewModelProvider.factory)
) {

    val dashboardUiState by viewModel.dashboardUiState.collectAsState()

    var activeEventId by rememberSaveable { mutableStateOf<Int?>(null) }

    if (dashboardUiState.isDataLoaded) {

        Scaffold(modifier = modifier) { innerPadding ->
            DashboardContent(
                eventList = dashboardUiState.eventList,
                activeEventId = activeEventId,
                onLongClickEvent = { activeEventId = if (activeEventId == it) null else it },
                modifier = Modifier.padding(innerPadding),
                onChangeDuration = { activeEventId: Int?,
                                     totalDragAmount: Float,
                                     hourHeight: Float,
                                     changeStart: Boolean,
                                     changeEnd: Boolean,
                                     changeStartEnd: Boolean,
                                     drugEnd: Boolean ->
                    viewModel.changeEventStartEndTimes(
                        activeEventId,
                        totalDragAmount,
                        hourHeight,
                        changeStart,
                        changeEnd,
                        changeStartEnd,
                        drugEnd
                    )
                }
            )
        }
    } else {

        LoadingScreen()
    }
}

@Composable
fun DashboardContent(
    eventList: List<Event>,
    activeEventId: Int?,
    onLongClickEvent: (eventId: Int) -> Unit,
    modifier: Modifier = Modifier,
    onChangeDuration: (
        activeEventId: Int?,
        totalDragAmount: Float,
        hourHeight: Float,
        changeStart: Boolean,
        changeEnd: Boolean,
        changeStartEnd: Boolean,
        drugEnd: Boolean
    ) -> Unit,

    ) {

    fun currentTime(): Calendar = Calendar.getInstance()
    var timeInstance by rememberSaveable { mutableStateOf(currentTime()) }
    var lastUpdateTime by remember { mutableLongStateOf(0L) }
    LaunchedEffect(Unit) {
        while (true) {
            delay(60000) // Check every second
            timeInstance = currentTime()
//            val currentTimeMillis = currentTimeMillis()
//            if (currentTimeMillis - lastUpdateTime >= 60000) { // 10 seconds have passed
//                timeInstance = currentTime()
//                lastUpdateTime = currentTimeMillis
//            }
        }
    }

    val minDate = timeInstanceToLocalDate(timeInstance).toLocalDate()
    val maxDate = timeInstanceToLocalDate(timeInstance).plusDays(1).toLocalDate()

    val density = LocalDensity.current

    var sidebarWidth by rememberSaveable { mutableIntStateOf(0) }

    val dayWidth by remember { mutableIntStateOf(500) }

    val verticalScrollState = rememberScrollState()
    val horizontalScrollState = rememberScrollState()

    val scope = rememberCoroutineScope()

    val numDay = 3

    var zoom by remember { mutableFloatStateOf(1f) }
    val hourHeight = calculateHourHeight(zoom) // محاسبه hourHeight بر اساس zoom

    var taskPalletWidth by rememberSaveable { mutableIntStateOf(12) }



    Box(
        modifier = modifier
            .fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectTransformGestures(
                        onGesture = { _, _, gestureZoom, _ ->
                            zoom = maxOf(1f, zoom * gestureZoom)
                        }
                    )
                }

        ) {
            Header(
                minDate = minDate,
                maxDate = maxDate,
                dayWidth = dayWidth,
                numDays = numDay,
                modifier = Modifier
                    .padding(start = with(density) { sidebarWidth.toDp() })
                    .horizontalScroll(horizontalScrollState, enabled = false)
            )
            Row(modifier = Modifier.weight(1f)) {
                TimeSidebar(
                    hourHeight = hourHeight,
                    onZoomChange = { zoom = it },
                    modifier = Modifier
                        .verticalScroll(verticalScrollState, enabled = false)
                        .onGloballyPositioned { sidebarWidth = it.size.width }
                )
                Schedule(
                    events = eventList,
                    hourHeight = hourHeight,
                    minDate = minDate,
                    maxDate = maxDate,
                    dayWidth = dayWidth,
                    numDays = numDay,
                    timeInstance = timeInstance,
                    activeEventId = activeEventId,
                    onLongClickEvent = onLongClickEvent,
                    onChangeDuration = onChangeDuration,
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(verticalScrollState)
                        .horizontalScroll(horizontalScrollState)

                )
            }

        }

        Row(
            modifier = Modifier,
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(modifier = Modifier.weight(1f))
            Icon(
                Icons.Filled.RadioButtonUnchecked,
                tint = MaterialTheme.colorScheme.onBackground,
                contentDescription = null,
                modifier = Modifier
                    .clickable(onClick = { taskPalletWidth = if (taskPalletWidth==12) 160 else 12 })
            )
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(taskPalletWidth.dp)
                    .background(
                        color = MaterialTheme.colorScheme.tertiaryContainer,
                        shape = RoundedCornerShape(6.dp)
                    )
                    .border(
                        width = 4.dp,
                        color = MaterialTheme.colorScheme.onBackground,
                        shape = RoundedCornerShape(22f)
                    )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                ){
                    Text(
                        text = "پالت رویدادها",
                        modifier = Modifier
                            .padding(6.dp)
                            .align(Alignment.CenterHorizontally)
                    )
                    LazyColumn(
                        modifier = Modifier
                            .weight(1f),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),

                        ) {
                        items(eventList){event ->
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = modifier
                            )
                            {

                                Column(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(end = 2.dp, bottom = 2.dp)
                                        .background(
                                            event.color.stringToColor(),
                                            shape = RoundedCornerShape(4.dp)
                                        )
                                        .padding(4.dp)

                                ) {
                                    Text(
                                        text = "${event.start.format(EventTimeFormatter)} - ${
                                            event.end.format(
                                                EventTimeFormatter
                                            )
                                        }",
                                        style = MaterialTheme.typography.bodyMedium,
                                    )

                                    Text(
                                        text = event.name,
                                        style = MaterialTheme.typography.labelLarge,
                                        fontWeight = FontWeight.Bold,
                                    )

                                    Text(
                                        text = event.description,
                                        style = MaterialTheme.typography.bodySmall,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                    )
                                }
                            }
                        }
                    }





                    }
                }

            }

        }



}


@Composable
fun Header(
    minDate: LocalDate,
    maxDate: LocalDate,
    dayWidth: Int,
    numDays: Int,
    modifier: Modifier = Modifier,
    dayHeader: @Composable (day: LocalDate) -> Unit = { HeaderContent(day = it) },
) {
    val density = LocalDensity.current
    val dayWidthDp = with(density) { dayWidth.toDp() }
    Row(modifier = modifier) {
        repeat(numDays) { i ->
            Box(modifier = Modifier.width(dayWidthDp)) {
                dayHeader(minDate.plusDays(i.toLong()))
            }
        }
    }
}

@Composable
fun HeaderContent(
    day: LocalDate,
    modifier: Modifier = Modifier,
) {
    Text(
        text = convertToPersianDate(day),
        textAlign = TextAlign.Center,
        modifier = modifier
            .fillMaxWidth()
            .padding(4.dp)
    )
}


@Composable
fun TimeSidebar(
    hourHeight: Float,
    modifier: Modifier = Modifier,
    onZoomChange: (zoom: Float) -> Unit,
    label: @Composable (time: LocalTime) -> Unit = { TimeSidebarContent(time = it) },
) {
    val hourHeightDp = with(LocalDensity.current) { hourHeight.toDp() }


    var zoom by remember { mutableFloatStateOf(1f) }

    Column(
        modifier = modifier
            .pointerInput(Unit) {
                detectTransformGestures(
                    onGesture = { _, _, gestureZoom, _ ->

                        zoom = maxOf(1f, zoom * gestureZoom)
                        onZoomChange(zoom)
                    }
                )
            }
    ) {
        val startTime = LocalTime.MIN
        repeat(24) { i ->
            Box(
                modifier = Modifier
                    .height(hourHeightDp)
            ) {
                label(startTime.plusHours(i.toLong()))
            }
        }
    }
}

@Composable
fun TimeSidebarContent(
    time: LocalTime,
    modifier: Modifier = Modifier,
) {
    Text(
        text = time.format(HourFormatter),
        modifier = modifier
            .fillMaxHeight()
            .padding(horizontal = 4.dp)
    )
}


@Composable
fun Schedule(
    events: List<Event>,
    hourHeight: Float,
    minDate: LocalDate,
    maxDate: LocalDate,
    numDays: Int,
    dayWidth: Int,
    timeInstance: Calendar,
    activeEventId: Int?,
    onLongClickEvent: (eventId: Int) -> Unit,
    modifier: Modifier = Modifier,
    onChangeDuration: (
        activeEventId: Int?,
        totalDragAmount: Float,
        hourHeight: Float,
        changeStart: Boolean,
        changeEnd: Boolean,
        changeStartEnd: Boolean,
        drugEnd: Boolean
    ) -> Unit,
    eventContent: @Composable (
        event: Event,
        hourHeight: Float,
        activeEventId: Int?,
        onLongClickEvent: (eventId: Int) -> Unit,
        onChangeDuration: (
            activeEventId: Int?,
            totalDragAmount: Float,
            hourHeight: Float,
            changeStart: Boolean,
            changeEnd: Boolean,
            changeStartEnd: Boolean,
            drugEnd: Boolean
        ) -> Unit,
    ) -> Unit =
        { event0, hourHeight0, activeEventId0, onLongClickEvent0, onChangeDuration0 ->
            BasicEvent(
                event = event0,
                hourHeight = hourHeight0,
                activeEventId = activeEventId0,
                onLongClickEvent = onLongClickEvent0,
                onChangeDuration = onChangeDuration0
            )
        },
) {


    val dividerColor = MaterialTheme.colorScheme.onBackground
    val currentTimeLine = MaterialTheme.colorScheme.onBackground
    val currentTimeHeightPx = currentTimeHeightPx(timeInstance, hourHeight)

    Box(
        modifier = modifier
            .fillMaxSize()
    ) {
        Layout(
            content = {
                events.forEach { event ->
                    Box(modifier = Modifier.eventData(event)) {
                        eventContent(
                            event,
                            hourHeight,
                            activeEventId,
                            onLongClickEvent,
                            onChangeDuration
                        )
                    }
                }
            },
            modifier = Modifier
                .drawBehind {
                    repeat(23) {
                        drawLine(
                            dividerColor,
                            start = Offset(0f, (it + 1) * hourHeight),
                            end = Offset(size.width, (it + 1) * hourHeight),
                            strokeWidth = 1.dp.toPx()
                        )
                    }
                    repeat(numDays - 1) {
                        drawLine(
                            dividerColor,
                            start = Offset((it + 1) * dayWidth.toFloat(), 0f),
                            end = Offset((it + 1) * dayWidth.toFloat(), size.height),
                            strokeWidth = 1.dp.toPx()
                        )
                    }
                }
                .drawWithContent {
                    drawContent()
                    drawLine(
                        color = currentTimeLine,
                        start = Offset(
                            0f,
                            currentTimeHeightPx
                                .toFloat()
                        ),
                        end = Offset(
                            dayWidth.toFloat(),
                            currentTimeHeightPx
                                .toFloat()
                        ),
                        strokeWidth = 2.dp.toPx(),
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 10f)
                    )
                }

        )

        { measureables, constraints ->
            val height = (hourHeight * 24).roundToInt()
            val width = dayWidth * numDays
            val placeablesWithEvents = measureables.map { measurable ->
                val event = measurable.parentData as Event
                val eventDurationMinutes = ChronoUnit.MINUTES.between(event.start, event.end)
                val eventHeight = ((eventDurationMinutes / 60f) * hourHeight).roundToInt()
                val placeable = measurable.measure(
                    constraints.copy(
                        minWidth = dayWidth,
                        maxWidth = dayWidth,
                        minHeight = if (eventHeight >= 0) eventHeight else eventHeight * -1,
                        maxHeight = if (eventHeight >= 0) eventHeight else eventHeight * -1
                    )
                )
                Pair(placeable, event)
            }
            layout(width, height) {
                placeablesWithEvents.forEach { (placeable, event) ->
                    if (event.id != activeEventId) {
                        val eventOffsetMinutes =
                            ChronoUnit.MINUTES.between(LocalTime.MIN, event.start.toLocalTime())
                        val eventY = ((eventOffsetMinutes / 60f) * hourHeight).roundToInt()
                        val eventOffsetDays =
                            ChronoUnit.DAYS.between(minDate, event.start.toLocalDate()).toInt()
                        val eventX = eventOffsetDays * dayWidth
                        placeable.place(eventX, eventY)
                    }
                }
                placeablesWithEvents.forEach { (placeable, event) ->
                    if (event.id == activeEventId) {
                        val eventOffsetMinutes =
                            ChronoUnit.MINUTES.between(LocalTime.MIN, event.start.toLocalTime())
                        val eventY = ((eventOffsetMinutes / 60f) * hourHeight).roundToInt()
                        val eventOffsetDays =
                            ChronoUnit.DAYS.between(minDate, event.start.toLocalDate()).toInt()
                        val eventX = eventOffsetDays * dayWidth
                        placeable.place(eventX, eventY)
                    }
                }
            }
        }
    }
}


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun BasicEvent(
    event: Event,
    hourHeight: Float,
    activeEventId: Int?,
    onLongClickEvent: (eventId: Int) -> Unit,
    onChangeDuration: (
        activeEventId: Int?,
        totalDragAmount: Float,
        hourHeight: Float,
        changeStart: Boolean,
        changeEnd: Boolean,
        changeStartEnd: Boolean,
        drugEnd: Boolean
    ) -> Unit,
    modifier: Modifier = Modifier,
) {


    val density = LocalDensity.current
    val selectedEventHeightPx = eventHeightPx(event, hourHeight)
    var selectedEventHeight by remember { mutableStateOf(0.dp) }
    selectedEventHeight = with(density) { selectedEventHeightPx.toDp() }


    var totalDragAmount by rememberSaveable { mutableFloatStateOf(0f) }




    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
    )
    {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(end = 2.dp, bottom = 2.dp)
                .background(
                    event.color.stringToColor(),
                    shape = RoundedCornerShape(4.dp)
                )
                .padding(4.dp)

                .combinedClickable(
                    onClick = { },
                    onLongClick = { event.id?.let { onLongClickEvent(it) } }
                )
        ) {
            Text(
                text = "${event.start.format(EventTimeFormatter)} - ${
                    event.end.format(
                        EventTimeFormatter
                    )
                }",
                style = MaterialTheme.typography.bodyMedium,
            )

            Text(
                text = event.name,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
            )

            Text(
                text = event.description,
                style = MaterialTheme.typography.bodySmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }

        if (event.id == activeEventId) {
            Box(    // root
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxSize()
            ) {
                Box(
                    //event background
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            event.color.stringToColor(),
                            shape = RoundedCornerShape(6.dp)
                        )
                        .padding(4.dp),
                ) {

                }
                Box(     //event content and border
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .requiredHeight(
                            if (selectedEventHeight > 100.dp) selectedEventHeight else 100.dp
                        )
                        .border(
                            width = 4.dp,
                            color = MaterialTheme.colorScheme.onBackground,
                            shape = RoundedCornerShape(22f)
                        )
                        .alpha(30f)
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .fillMaxSize()
                    ) {
                        Column(
                            //content
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(4.dp),
                            verticalArrangement = Arrangement.Center,
                        ) {
                            Text(
                                text = "${event.start.format(EventTimeFormatter)} ",
                                modifier = Modifier
                            )
                            Spacer(modifier = Modifier.weight(0.5f))
                            Text(
                                text = event.name,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                overflow = TextOverflow.Clip,
                                modifier = Modifier
                                    .padding(start = 16.dp)
                                    .weight(1f)
                            )
                            Spacer(modifier = Modifier.weight(0.5f))
                            Text(
                                text = "${event.end.format(EventTimeFormatter)} ",
                                modifier = Modifier
                            )
                        }
                        Box(
                            contentAlignment = Alignment.TopCenter,
                            modifier = Modifier
                                .fillMaxSize()
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(4.dp)
                            ) {
                                Icon(
                                    Icons.Filled.RadioButtonUnchecked,
                                    tint = MaterialTheme.colorScheme.onBackground,
                                    contentDescription = null,
                                    modifier = Modifier
                                        .pointerInput(hourHeight) {
                                            detectVerticalDragGestures(
                                                onDragStart = {},
                                                onVerticalDrag = { _, dragAmount ->

                                                    totalDragAmount += dragAmount


                                                    onChangeDuration(
                                                        activeEventId,
                                                        totalDragAmount,
                                                        hourHeight,
                                                        true,
                                                        false,
                                                        false,
                                                        false
                                                    )
                                                },
                                                onDragEnd = {

                                                    onChangeDuration(
                                                        activeEventId,
                                                        totalDragAmount,
                                                        hourHeight,
                                                        true,
                                                        false,
                                                        false,
                                                        true
                                                    )
                                                    totalDragAmount = 0f
                                                },
                                                onDragCancel = {

                                                    onChangeDuration(
                                                        activeEventId,
                                                        totalDragAmount,
                                                        hourHeight,
                                                        true,
                                                        false,
                                                        false,
                                                        true
                                                    )
                                                    totalDragAmount = 0f
                                                }
                                            )
                                        }


                                )
                                Row(
                                    modifier = Modifier.weight(0.1f),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Spacer(modifier = Modifier.weight(0.1f))
                                    Icon(
                                        Icons.Filled.RadioButtonUnchecked,
                                        tint = MaterialTheme.colorScheme.onBackground,
                                        contentDescription = null,
                                        modifier = Modifier
                                            .size(86.dp)
                                            .pointerInput(hourHeight) {

                                                detectVerticalDragGestures(

                                                    onDragStart = {},
                                                    onVerticalDrag = { _, dragAmount ->

                                                        totalDragAmount += dragAmount


                                                        onChangeDuration(
                                                            activeEventId,
                                                            totalDragAmount,
                                                            hourHeight,
                                                            false,
                                                            false,
                                                            true,
                                                            false
                                                        )
                                                    },
                                                    onDragEnd = {
                                                        onChangeDuration(
                                                            activeEventId,
                                                            totalDragAmount,
                                                            hourHeight,
                                                            false,
                                                            false,
                                                            true,
                                                            true
                                                        )
                                                        totalDragAmount = 0f
                                                    },
                                                    onDragCancel = {
                                                        onChangeDuration(
                                                            activeEventId,
                                                            totalDragAmount,
                                                            hourHeight,
                                                            false,
                                                            false,
                                                            true,
                                                            true
                                                        )
                                                        totalDragAmount = 0f
                                                    }
                                                )
                                            }
                                    )
                                }
                                Icon(
                                    Icons.Filled.RadioButtonUnchecked,
                                    tint = MaterialTheme.colorScheme.onBackground,
                                    contentDescription = null,
                                    modifier = Modifier
                                        .pointerInput(hourHeight) {
                                            detectVerticalDragGestures(
                                                onDragStart = {},
                                                onVerticalDrag = { _, dragAmount ->

                                                    totalDragAmount += dragAmount


                                                    onChangeDuration(
                                                        activeEventId,
                                                        totalDragAmount,
                                                        hourHeight,
                                                        false,
                                                        true,
                                                        false,
                                                        false
                                                    )
                                                },
                                                onDragEnd = {

                                                    onChangeDuration(
                                                        activeEventId,
                                                        totalDragAmount,
                                                        hourHeight,
                                                        false,
                                                        true,
                                                        false,
                                                        true
                                                    )
                                                    totalDragAmount = 0f
                                                },
                                                onDragCancel = {

                                                    onChangeDuration(
                                                        activeEventId,
                                                        totalDragAmount,
                                                        hourHeight,
                                                        false,
                                                        true,
                                                        false,
                                                        true
                                                    )
                                                    totalDragAmount = 0f
                                                }
                                            )
                                        }

                                )
                            }
                        }
                    }
                }
            }
        }
    }
}