package com.example.compoundeffectV1_01.ui.settingsScreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.compoundeffectV1_01.data.dataStore.AppPreferences
import com.example.compoundeffectV1_01.ui.navigation.AppRoutes
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val appPreferences: AppPreferences
) : ViewModel() {

    val defaultStartDestination =
        appPreferences.defaultStartDestination
            .map { route ->
                when (route) {
                    AppRoutes.CATEGORY -> AppRoutes.CATEGORY
                    AppRoutes.SCHEDULE -> AppRoutes.SCHEDULE
                    else -> AppRoutes.CATEGORY
                }
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = AppRoutes.CATEGORY
            )

    fun setDefaultStartDestination(route: String) {
        val cleanRoute = when (route) {
            AppRoutes.CATEGORY -> AppRoutes.CATEGORY
            AppRoutes.SCHEDULE -> AppRoutes.SCHEDULE
            else -> AppRoutes.CATEGORY
        }

        viewModelScope.launch {
            appPreferences.setDefaultStartDestination(cleanRoute)
        }
    }
}