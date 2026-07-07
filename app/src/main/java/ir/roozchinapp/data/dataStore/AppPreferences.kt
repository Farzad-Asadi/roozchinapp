package ir.roozchinapp.data.dataStore

import kotlinx.coroutines.flow.Flow

interface AppPreferences {
    val isSeedDone: Flow<Boolean>
    suspend fun setSeedDone(value: Boolean)

    val hasAskedSchedulePermissions: Flow<Boolean>
    suspend fun setHasAskedSchedulePermissions(value: Boolean)

    val scheduleVerticalZoom: Flow<Float>
    suspend fun setScheduleVerticalZoom(value: Float)

    val defaultStartDestination: Flow<String>
    suspend fun setDefaultStartDestination(route: String)
}