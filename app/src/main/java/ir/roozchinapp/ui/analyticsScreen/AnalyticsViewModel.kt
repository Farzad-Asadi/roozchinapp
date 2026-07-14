package ir.roozchinapp.ui.analyticsScreen

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class AnalyticsViewModel @Inject constructor() : ViewModel() {

    private val _uiState =
        MutableStateFlow(AnalyticsUiState())

    val uiState: StateFlow<AnalyticsUiState> =
        _uiState.asStateFlow()

    fun selectPeriod(period: AnalyticsPeriod) {
        _uiState.update { current ->
            if (current.selectedPeriod == period) {
                current
            } else {
                current.copy(
                    selectedPeriod = period,
                    errorMessage = null
                )
            }
        }
    }
}