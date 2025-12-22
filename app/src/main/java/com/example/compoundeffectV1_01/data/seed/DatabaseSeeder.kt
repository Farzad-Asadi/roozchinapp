package com.example.compoundeffectV1_01.data.seed

import com.example.compoundeffectV1_01.data.dataStore.AppPreferences
import com.example.compoundeffectV1_01.data.dataBaseRoom.tables.appSystemInfo.SystemDao
import com.example.compoundeffectV1_01.data.dataBaseRoom.tables.category.CategoryDao
import com.example.compoundeffectV1_01.data.dataBaseRoom.tables.event.EventDao
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DatabaseSeeder @Inject constructor(
    private val categoryDao: CategoryDao,
    private val eventDao: EventDao,
    private val systemDao: SystemDao,
    private val prefs: AppPreferences
) {
    suspend fun seedIfNeeded() {
        // فعلاً فقط بدنه خالی برای اینکه Application ارور نده
    }
}

