package ir.roozchinapp.data.sharedViewModel

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject

@HiltViewModel
class MainSharedViewModel @Inject constructor() : ViewModel() {

    private val _events = MutableSharedFlow<Event>(extraBufferCapacity = 1)
    val events = _events.asSharedFlow()

    sealed class Event {
        data object AddCategoryClicked : Event()
    }

    fun onAddCategoryClicked() {
        _events.tryEmit(Event.AddCategoryClicked)
    }
}