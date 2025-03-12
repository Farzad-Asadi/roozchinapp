package com.example.compoundeffectV1_01.ui.dashboardScreen


import android.util.Log
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.compoundeffectV1_01.data.room.appSystemInfo.AppSystemInfo
import com.example.compoundeffectV1_01.data.room.appSystemInfo.AppSystemInfoRepository
import com.example.compoundeffectV1_01.data.room.event.Event
import com.example.compoundeffectV1_01.data.room.event.EventRepository
import com.example.compoundeffectV1_01.utils.colorToString
import com.example.compoundeffectV1_01.utils.createTimeForSampleEvents
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.Calendar


class DashboardScreenViewModel(
    private val eventRepository: EventRepository,
    private val appSystemInfoRepository: AppSystemInfoRepository
) : ViewModel() {


    private val _dashboardUiState = MutableStateFlow(EventUiState())
    val dashboardUiState = _dashboardUiState.asStateFlow()

    init {
        initializeDashboardViewModel()
    }


    private fun initializeDashboardViewModel() {

        viewModelScope.launch {


            val job1 = async {

                // Sample data
                val currentTime: Calendar = Calendar.getInstance()
                val sampleEvents = arrayOf(
                    Event(
                        id = 1,
                        name = "Google",
                        color = Color(0xFFAFBBF2).colorToString(),
                        start = createTimeForSampleEvents(currentTime, 7, 0, 0),
                        end = createTimeForSampleEvents(currentTime, 9, 0, 0),
                        description = "Tune in to find out about how we're furthering our mission to organize the world’s information and make it universally accessible and useful.",
                        selected = false
                    ),
                    Event(
                        id = 2,
                        name = "Developer Keynote",
                        color = Color(0xFFAFBBF2).colorToString(),
                        start = createTimeForSampleEvents(currentTime, 11, 0, 0),
                        end = createTimeForSampleEvents(currentTime, 12, 0, 0),
                        description = "Learn about the latest updates to our developer products and platforms from Google Developers.",
                        selected = false
                    ),
                    Event(
                        id = 3,
                        name = "What's new in Android",
                        color = Color(0xFF1B998B).colorToString(),
                        start = createTimeForSampleEvents(currentTime, 13, 0, 0),
                        end = createTimeForSampleEvents(currentTime, 15, 0, 0),
                        description = "In this Keynote, Chet Haase, Dan Sandler, and Romain Guy discuss the latest Android features and enhancements for developers.",
                        selected = false
                    ),
                    Event(
                        id = 4,
                        name = "What's new in Machine Learning",
                        color = Color(0xFFF4BFDB).colorToString(),
                        start = createTimeForSampleEvents(currentTime, 18, 30, 0),
                        end = createTimeForSampleEvents(currentTime, 19, 45, 0),
                        description = "Learn about the latest and greatest in ML from Google. We’ll cover what’s available to developers when it comes to creating, understanding, and deploying models for a variety of different applications.",
                        selected = false
                    ),
                    Event(
                        id = 5,
                        name = "What's new in Material Design",
                        color = Color(0xFF6DD3CE).colorToString(),
                        start = createTimeForSampleEvents(currentTime, 21, 30, 0),
                        end = createTimeForSampleEvents(currentTime, 23, 0, 0),
                        description = "Learn about the latest design improvements to help you build personal dynamic experiences with Material Design.",
                        selected = false
                    ),
                    Event(
                        id = 6,
                        name = "Jetpack Compose Basics",
                        color = Color(0xFF1B998B).colorToString(),
                        start = createTimeForSampleEvents(currentTime, 13, 30, 0).plusDays(1),
                        end = createTimeForSampleEvents(currentTime, 15, 45, 0).plusDays(1),
                        description = "This Workshop will take you through the basics of building your first app with Jetpack Compose, Android's new modern UI toolkit that simplifies and accelerates UI development on Android.",
                        selected = false
                    ),
                    Event(
                        id = 7,
                        name = "test to other day",
                        color = Color(0xFF1B998B).colorToString(),
                        start = createTimeForSampleEvents(currentTime, 18, 30, 0).plusDays(1),
                        end = createTimeForSampleEvents(currentTime, 20, 0, 0).plusDays(1),
                        description = "This Workshop will take you through the basics of building your first app with Jetpack Compose, Android's new modern UI toolkit that simplifies and accelerates UI development on Android.",
                        selected = false
                    ),
                )
                viewModelScope.launch {
                    eventRepository.insertEvent(*sampleEvents)
                }
                viewModelScope.launch {
                    appSystemInfoRepository.insertAppSystemInfo(AppSystemInfo(id = 1))
                }
            }
            job1.await()


            if (job1.isCompleted) {
                val job2 = async {

                    val eventList = eventRepository.getAllEvents()
                    _dashboardUiState.update { dashboardUiState ->
                        dashboardUiState.copy(
                            eventList = eventList,
                        )
                    }
                }
                job2.await()
                if (job1.isCompleted && job2.isCompleted) {
                    val job3 = async {
                        _dashboardUiState.update { dashboardUiState ->
                            dashboardUiState.copy(
                                isDataLoaded = true
                            )
                        }
                    }
                    job3.await()
                }
            }


        }
    }


    fun changeEventStartEndTimes(
        activeEventId: Int?,
        totalDragAmount: Float,
        hourHeight: Float,
        changeStart: Boolean = false,
        changeEnd: Boolean = false,
        changeStartEnd: Boolean = false,
        dragEnd: Boolean = false, // اضافه کردن پارامتر dragEnd
    ) {

        val oneMinutePx: Float = (hourHeight / 60)
        val totalMinute = ((((totalDragAmount / oneMinutePx).toLong()) + 4) / 5) * 5


        val oldList = _dashboardUiState.value.eventList
        val activatedEvent = oldList.first { it.id == activeEventId }
        val eventIndex = oldList.indexOfFirst { it.id == activeEventId }

        if (!dragEnd && _dashboardUiState.value.initialStartTime == null) {
            _dashboardUiState.update { dashboardUiState ->
                dashboardUiState.copy(
                    initialStartTime = activatedEvent.start
                )
            }
        }
        if (!dragEnd && _dashboardUiState.value.initialEndTime == null) {
            _dashboardUiState.update { dashboardUiState ->
                dashboardUiState.copy(
                    initialEndTime = activatedEvent.end
                )
            }
        }

        val minimumTimeStartCanReach: LocalDateTime = LocalDateTime.of(
            activatedEvent.start.year,
            activatedEvent.start.month,
            activatedEvent.start.dayOfMonth,
            0,
            0
        )
        val maximumTimeStartCanReach: LocalDateTime = activatedEvent.end.minusMinutes(5)
        val minimumTimeEndCanReach: LocalDateTime = activatedEvent.start.plusMinutes(5)
        val maximumTimeEndCanReach: LocalDateTime = LocalDateTime.of(
            activatedEvent.end.year,
            activatedEvent.end.month,
            activatedEvent.end.dayOfMonth,
            23,
            55
        )



        if (changeStart) {

            var newStartTime =
                _dashboardUiState.value.initialStartTime?.plusMinutes(totalMinute)

            if (newStartTime != null) {
                newStartTime =
                    when {
                        newStartTime.isAfter(minimumTimeStartCanReach) && newStartTime.isBefore(
                            maximumTimeStartCanReach
                        ) ->
                            newStartTime

                        !newStartTime.isAfter(minimumTimeStartCanReach) && newStartTime.isBefore(
                            maximumTimeStartCanReach
                        ) ->
                            newStartTime.withHour(0).withMinute(0)
                                .withDayOfMonth(activatedEvent.start.dayOfMonth)

                        newStartTime.isAfter(minimumTimeStartCanReach) && !newStartTime.isBefore(
                            maximumTimeStartCanReach
                        ) ->
                            activatedEvent.end.minusMinutes(5)

                        else -> return
                    }

            }

            if (newStartTime != _dashboardUiState.value.initialStartTime) {
                val changedEvent = newStartTime?.let {
                    activatedEvent.copy(
                        start = it
                    )
                }
                if (!dragEnd && _dashboardUiState.value.initialStartTime != null) {
                    val updatedList = oldList.toMutableList().apply {
                        if (changedEvent != null) {
                            set(eventIndex, changedEvent)
                        }
                    }
                    _dashboardUiState.update { dashboardUiState ->
                        dashboardUiState.copy(
                            eventList = updatedList
                        )
                    }
                }
                if (dragEnd) {
                    _dashboardUiState.update { dashboardUiState ->
                        dashboardUiState.copy(
                            initialStartTime = null,
                            initialEndTime = null
                        )
                    }
                    viewModelScope.launch {
                        if (changedEvent != null) {

                            eventRepository.updateEvent(changedEvent)
                        }
                    }

                }
            }

        }
        if (changeEnd) {

            var newEndTime =
                _dashboardUiState.value.initialEndTime?.plusMinutes(totalMinute)

            if (newEndTime != null) {
                newEndTime =
                    when {
                        newEndTime.isAfter(minimumTimeEndCanReach) && newEndTime.isBefore(
                            maximumTimeEndCanReach
                        ) ->
                            newEndTime

                        !newEndTime.isAfter(minimumTimeEndCanReach) && newEndTime.isBefore(
                            maximumTimeEndCanReach
                        ) ->
                            activatedEvent.start.plusMinutes(5)

                        newEndTime.isAfter(minimumTimeEndCanReach) && !newEndTime.isBefore(
                            maximumTimeEndCanReach
                        ) ->
                            newEndTime.withHour(23).withMinute(55)
                                .withDayOfMonth(activatedEvent.start.dayOfMonth)

                        else -> return
                    }

            }

            if (newEndTime != _dashboardUiState.value.initialEndTime) {
                val changedEvent = newEndTime?.let {
                    activatedEvent.copy(
                        end = it
                    )
                }
                if (!dragEnd && _dashboardUiState.value.initialEndTime != null) {
                    val updatedList = oldList.toMutableList().apply {
                        if (changedEvent != null) {
                            set(eventIndex, changedEvent)
                        }
                    }
                    _dashboardUiState.update { dashboardUiState ->
                        dashboardUiState.copy(
                            eventList = updatedList
                        )
                    }
                }
                if (dragEnd) {
                    _dashboardUiState.update { dashboardUiState ->
                        dashboardUiState.copy(
                            initialStartTime = null,
                            initialEndTime = null
                        )
                    }
                    viewModelScope.launch {
                        if (changedEvent != null) {

                            eventRepository.updateEvent(changedEvent)
                        }
                    }

                }
            }

        }
        if (changeStartEnd) {

            var newStartTime =
                _dashboardUiState.value.initialStartTime?.plusMinutes(totalMinute)

            var newEndTime =
                _dashboardUiState.value.initialEndTime?.plusMinutes(totalMinute)

            if (newEndTime != null) {
                newEndTime =
                    when {
                        newEndTime.isBefore(maximumTimeEndCanReach) ->
                            newEndTime

                        !newEndTime.isBefore(maximumTimeEndCanReach) ->
                            newEndTime.withHour(23).withMinute(55)
                                .withDayOfMonth(activatedEvent.start.dayOfMonth)

                        else -> return
                    }
            }
            if ( newStartTime != null) {
                newStartTime =
                    when {
                        newStartTime.isAfter(minimumTimeStartCanReach) ->
                            newStartTime

                        !newStartTime.isAfter(minimumTimeStartCanReach) ->
                            newStartTime.withHour(0).withMinute(0)
                                .withDayOfMonth(activatedEvent.start.dayOfMonth)

                        else -> return
                    }
            }


            if (
                newEndTime != null &&
                newStartTime != null &&
                newEndTime != _dashboardUiState.value.initialEndTime &&
                newStartTime != _dashboardUiState.value.initialStartTime
            ) {

                val changedEvent =
                    activatedEvent.copy(
                        end = newEndTime,
                        start = newStartTime
                    )


                if (!dragEnd && _dashboardUiState.value.initialStartTime != null && _dashboardUiState.value.initialEndTime != null) {
                    val updatedList = oldList.toMutableList().apply {

                        set(eventIndex, changedEvent)

                    }
                    _dashboardUiState.update { dashboardUiState ->
                        dashboardUiState.copy(
                            eventList = updatedList
                        )
                    }
                }
                if (dragEnd) {
                    _dashboardUiState.update { dashboardUiState ->
                        dashboardUiState.copy(
                            initialStartTime = null,
                            initialEndTime = null
                        )
                    }
                    viewModelScope.launch {
                            eventRepository.updateEvent(changedEvent)

                    }

                }
            }

        }

        Log.i("TEST", "---------------")
    }


}

// Data class for UI

data class EventUiState(
    val eventList: List<Event> = listOf(),
    var initialStartTime: LocalDateTime? = null,
    var initialEndTime: LocalDateTime? = null,
    val isDataLoaded: Boolean = false

)



