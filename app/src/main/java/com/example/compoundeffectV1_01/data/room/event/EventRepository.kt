package com.example.compoundeffectV1_01.data.room.event

import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime


interface EventRepository {

    suspend fun insertEvent(vararg events: Event)
    suspend fun updateEvent(event: Event)

    suspend fun getAllEvents() : List<Event>
    suspend fun changeEventStartTimeDuration(eventId : Int , changedStartDuration :LocalDateTime)

    suspend fun changeSelectedEvent(eventId : Int , selected : Boolean)

    suspend fun eventChangeHandling(eventId:Int,eventChangeState:Boolean)

    fun getAllEventStream(): Flow<List<Event>>



//    fun getAllAirportList():Flow<List<Task>>
//    fun selectAirportByNameOrCode(searchedText:String) :Flow<List<Task>>
//    fun selectAirportByIataCode(iataCode: String) :Flow<Task>
//    fun getAllAirportListExceptOne(iataCode: String):Flow<List<Task>>





//    fun getAllFavoriteList():Flow<List<Favorite>>
//    fun findFavoriteBy(selectedAirportIataCode :String,arriveAirportIataCode :String):Flow<Favorite>
//    suspend fun insertFavorite(favorite: Favorite)
//    suspend fun deleteFavorite(departureCode :String,destinationCode :String)

}