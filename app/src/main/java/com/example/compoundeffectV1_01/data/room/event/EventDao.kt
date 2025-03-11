package com.example.compoundeffectV1_01.data.room.event

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime

@Dao
interface EventDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertEvent(vararg event: Event)

    @Delete
    suspend fun deleteEvent(event: Event)

    @Update
    suspend fun updateEvent(event: Event)

    @Query("SELECT * FROM event ORDER By start ASC")
    suspend fun getAllEvents() : List<Event>

    @Query("SELECT * FROM event ORDER By start ASC")
    fun getAllEventsStream() : Flow<List<Event>>

    @Query("Update event Set start =:changedStartDuration Where id = :eventId ")
    suspend fun changeEventStartTimeDuration(eventId : Int , changedStartDuration : LocalDateTime)

    @Query("Update event Set selected =:selected Where id = :eventId ")
    suspend fun changeSelectedEvent(eventId : Int , selected : Boolean)

    @Query("Update event Set changed =:eventChangeState Where id = :eventId ")
    suspend fun eventChangeHandling(eventId:Int,eventChangeState:Boolean)
}