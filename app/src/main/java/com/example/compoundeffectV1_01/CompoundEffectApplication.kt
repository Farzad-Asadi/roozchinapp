package com.example.compoundeffectV1_01

import android.app.Application
import com.example.compoundeffectV1_01.data.AppContainer
import com.example.compoundeffectV1_01.data.AppDataContainer

//private const val LAYOUT_PREFERENCE_NAME = "layout_preferences"
//private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
//    name = LAYOUT_PREFERENCE_NAME
//)

class CompoundEffectApplication : Application() {


    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = AppDataContainer(this)

    }
}