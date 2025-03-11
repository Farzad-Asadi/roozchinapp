package com.example.compoundeffectV1_01.data

import android.content.Context
import com.example.compoundeffectV1_01.data.room.AppDatabase
import com.example.compoundeffectV1_01.data.room.appSystemInfo.AppSystemInfoRepository
import com.example.compoundeffectV1_01.data.room.appSystemInfo.OfflineAppSystemInfoRepository
import com.example.compoundeffectV1_01.data.room.event.EventRepository
import com.example.compoundeffectV1_01.data.room.event.OfflineEventRepository

interface AppContainer {

    val eventRepository: EventRepository
    val appSystemInfoRepository: AppSystemInfoRepository

}


class AppDataContainer(private val context: Context) : AppContainer {

    override val eventRepository: EventRepository by lazy {
        OfflineEventRepository(
            AppDatabase.getDatabase(context).eventDao()

           )
    }
    override val appSystemInfoRepository: AppSystemInfoRepository by lazy {
        OfflineAppSystemInfoRepository(
            AppDatabase.getDatabase(context).systemDao()

        )
    }
}