package com.example.compoundeffectV1_01.data.dataBaseRoom.tables.task

import androidx.room.Entity

@Entity(
    tableName = "pomodoro_daily_adjustment",
    primaryKeys = ["taskId", "dateEpochDay"]
)
data class PomodoroDailyAdjustmentEntity(
    val taskId: Int,
    val dateEpochDay: Long,
    val delta: Int
)