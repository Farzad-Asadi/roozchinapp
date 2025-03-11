package com.example.compoundeffectV1_01.ui.dashboardScreen


import android.util.Log
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.gestures.rememberScrollableState
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
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
import com.example.compoundeffectV1_01.utils.DayFormatter
import com.example.compoundeffectV1_01.utils.EventTimeFormatter
import com.example.compoundeffectV1_01.utils.HourFormatter
import com.example.compoundeffectV1_01.utils.LoadingScreen
import com.example.compoundeffectV1_01.utils.convertToPersianDate
import com.example.compoundeffectV1_01.utils.createTimeForSampleEvents
import com.example.compoundeffectV1_01.utils.currentTimeHeightPx
import com.example.compoundeffectV1_01.utils.eventData
import com.example.compoundeffectV1_01.utils.stringToColor
import com.example.compoundeffectV1_01.utils.timeInstanceToLocalDate
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.lang.System.currentTimeMillis
import java.time.LocalDate
import java.time.LocalTime
import java.time.temporal.ChronoUnit
import java.util.Calendar
import kotlin.coroutines.CoroutineContext
import kotlin.math.roundToInt

@Composable
fun DashboardScreen(
    modifier: Modifier = Modifier,
    viewModel: DashboardScreenViewModel = viewModel(factory = AppViewModelProvider.factory)
) {

    val dashboardUiState by viewModel.dashboardUiState.collectAsState()

    if (dashboardUiState.isDataLoaded) {

        Scaffold(modifier = modifier) { innerPadding ->
            DashboardContent(
                eventList = dashboardUiState.eventList,
                modifier = Modifier.padding(innerPadding),
            )
        }
    } else {

        LoadingScreen()
    }
}

@Composable
fun DashboardContent(
    eventList: List<Event>,
    modifier: Modifier = Modifier,

    ) {

    fun currentTime(): Calendar = Calendar.getInstance()
    var timeInstance by rememberSaveable { mutableStateOf(currentTime()) }
    var lastUpdateTime by remember { mutableLongStateOf(0L) }
    LaunchedEffect(Unit) {
        while (true) {
            delay(60000) // Check every second
            val currentTimeMillis = currentTimeMillis()
            if (currentTimeMillis - lastUpdateTime >= 60000) { // 10 seconds have passed
                timeInstance = currentTime()
                lastUpdateTime = currentTimeMillis
            }
        }
    }

    val minDate = timeInstanceToLocalDate(timeInstance).toLocalDate()
    val maxDate = timeInstanceToLocalDate(timeInstance).plusDays(1).toLocalDate()

    val density = LocalDensity.current

    var sidebarWidth by remember { mutableIntStateOf(0) }

    val hourHeight by remember { mutableFloatStateOf(140f) }

    val dayWidth by remember { mutableIntStateOf(500) }

    val verticalScrollState = rememberScrollState()
    val horizontalScrollState = rememberScrollState()

    val scope = rememberCoroutineScope()

    val numDay=3


    Column(
        modifier = modifier
            .fillMaxSize()

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
                modifier = Modifier
                    .verticalScroll(verticalScrollState)
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
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(verticalScrollState)
                    .horizontalScroll(horizontalScrollState)

            )
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
    label: @Composable (time: LocalTime) -> Unit = { TimeSidebarContent(time = it) },
) {
    val hourHeightDp = with(LocalDensity.current) { hourHeight.toDp() }
    Column(
        modifier = modifier
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
    modifier: Modifier = Modifier,
    eventContent: @Composable (event: Event, hourHeight: Float) -> Unit = { event, hourHeightPx ->
        BasicEvent(event = event, hourHeight =hourHeightPx )
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
                        eventContent(event, hourHeight)
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
                    if (!event.selected) {
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
                    if (event.selected) {
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


@Composable
fun BasicEvent(
    event: Event,
    hourHeight: Float,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
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