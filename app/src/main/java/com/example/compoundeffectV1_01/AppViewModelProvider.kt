package com.example.compoundeffectV1_01

import android.app.Application
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.compoundeffectV1_01.ui.categoryScreen.CategoryScreenViewModel
import com.example.compoundeffectV1_01.ui.mainScreenUi.MainScreenViewModel
import com.example.compoundeffectV1_01.ui.scheduleScreen.ScheduleScreenViewModel

object AppViewModelProvider {
    val factory = viewModelFactory {
        initializer {
            val application =
                (this[AndroidViewModelFactory.APPLICATION_KEY] as CompoundEffectApplication)
            ScheduleScreenViewModel(
                eventRepository = compoundEffectApplication().container.eventRepository,
                appSystemInfoRepository = compoundEffectApplication().container.appSystemInfoRepository,

                )
        }
        initializer {
            CategoryScreenViewModel(
                categoryRepository = compoundEffectApplication().container.categoryRepository,
                eventRepository = compoundEffectApplication().container.eventRepository,
                appSystemInfoRepository = compoundEffectApplication().container.appSystemInfoRepository,

                )
        }
        initializer {
            MainScreenViewModel(
                eventRepository = compoundEffectApplication().container.eventRepository,
                appSystemInfoRepository = compoundEffectApplication().container.appSystemInfoRepository,

                )
        }
    }


}

/**
 * Extension function to queries for [Application] object and returns an instance of
 * [CompoundEffectApplication].
 */
fun CreationExtras.compoundEffectApplication(): CompoundEffectApplication =
    (this[AndroidViewModelFactory.APPLICATION_KEY] as CompoundEffectApplication)