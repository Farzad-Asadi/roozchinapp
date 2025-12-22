package com.example.compoundeffectV1_01.data.dataBaseRoom.tables.event

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity(tableName = "event")
data class Event(

    @PrimaryKey(autoGenerate = true)
    val id : Int?=null,

    val name: String,
    val color: String ,
    val start: LocalDateTime,
    val end: LocalDateTime,
    val description: String,
    val inPallet:Boolean=false,
    val inSchedule:Boolean=false,
    val durationOverlap:Int=0,
    val selected:Boolean,
    val changed:Boolean=false,
)




