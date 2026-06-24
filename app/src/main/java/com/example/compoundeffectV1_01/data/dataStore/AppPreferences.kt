package com.example.compoundeffectV1_01.data.dataStore

import kotlinx.coroutines.flow.Flow

interface AppPreferences {
    val isSeedDone: Flow<Boolean>
    suspend fun setSeedDone(value: Boolean)

    val hasAskedSchedulePermissions: Flow<Boolean>
    suspend fun setHasAskedSchedulePermissions(value: Boolean)
}