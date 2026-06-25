package com.example.compoundeffectV1_01.data.dataStore

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

private val Context.dataStore by preferencesDataStore(name = "app_prefs")

class AppPreferencesImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : AppPreferences {


    private object Keys {
        val SEED_DONE = booleanPreferencesKey("seed_done")
        val ASKED_SCHEDULE_PERMISSIONS = booleanPreferencesKey("asked_schedule_permissions")
        val SCHEDULE_VERTICAL_ZOOM = floatPreferencesKey("schedule_vertical_zoom")
    }

    override val isSeedDone: Flow<Boolean> =
        context.dataStore.data.map { it[Keys.SEED_DONE] ?: false }

    override suspend fun setSeedDone(value: Boolean) {
        context.dataStore.edit { it[Keys.SEED_DONE] = value }
    }

    override val hasAskedSchedulePermissions: Flow<Boolean> =
        context.dataStore.data.map { it[Keys.ASKED_SCHEDULE_PERMISSIONS] ?: false }

    override suspend fun setHasAskedSchedulePermissions(value: Boolean) {
        context.dataStore.edit {
            it[Keys.ASKED_SCHEDULE_PERMISSIONS] = value
        }
    }

    override val scheduleVerticalZoom: Flow<Float> =
        context.dataStore.data.map { prefs ->
            prefs[Keys.SCHEDULE_VERTICAL_ZOOM] ?: 1f
        }

    override suspend fun setScheduleVerticalZoom(value: Float) {
        context.dataStore.edit { prefs ->
            prefs[Keys.SCHEDULE_VERTICAL_ZOOM] = value
        }
    }
}
