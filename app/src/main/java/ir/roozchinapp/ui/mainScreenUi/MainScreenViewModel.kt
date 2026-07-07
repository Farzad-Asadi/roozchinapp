package ir.roozchinapp.ui.mainScreenUi

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.roozchinapp.data.dataStore.AppPreferences
import ir.roozchinapp.ui.navigation.AppRoutes
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class MainScreenViewModel @Inject constructor(
    appPreferences: AppPreferences
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
                initialValue = null
            )
}