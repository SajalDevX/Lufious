package ai.lufious.app.core.utils

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

abstract class BaseViewModel<Event, State>(
    initialState: State,
    private val dispatchers: DispatcherProvider
) : ViewModel() {

    private val _state = MutableStateFlow(initialState)
    val state: StateFlow<State> = _state.asStateFlow()

    private val _effects = MutableSharedFlow<UiEffect>()
    val effects: SharedFlow<UiEffect> = _effects.asSharedFlow()

    /** UI calls this to send events */
    fun send(event: Event) = viewModelScope.launch { handleEvent(event) }

    /** Subclasses implement this to update `_state` and/or `_effects` */
    protected abstract suspend fun handleEvent(event: Event)

    protected fun setState(reducer: State.() -> State) {
        _state.update { it.reducer() }
    }

    protected suspend fun emitEffect(effect: UiEffect) {
        _effects.emit(effect)
    }

    protected fun ioLaunch(block: suspend ()->Unit) =
        viewModelScope.launch(dispatchers.io) { block() }
}