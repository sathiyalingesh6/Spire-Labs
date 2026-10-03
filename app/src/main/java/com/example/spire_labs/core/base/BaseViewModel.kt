package com.example.spire_labs.core.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Base ViewModel supporting unidirectional data flow (MVI).
 */
abstract class BaseViewModel<State : UiState, Intent : UiIntent, Effect : UiEffect> : ViewModel() {

    abstract fun createInitialState(): State

    private val _uiState: MutableStateFlow<State> by lazy { MutableStateFlow(createInitialState()) }
    val uiState: StateFlow<State> by lazy { _uiState.asStateFlow() }

    private val _intent: MutableSharedFlow<Intent> = MutableSharedFlow()
    val intent: SharedFlow<Intent> = _intent.asSharedFlow()

    private val _effect: Channel<Effect> = Channel()
    val effect = _effect.receiveAsFlow()

    val currentState: State
        get() = uiState.value

    init {
        subscribeEvents()
    }

    private fun subscribeEvents() {
        viewModelScope.launch {
            intent.collect {
                handleIntent(it)
            }
        }
    }

    fun setIntent(intent: Intent) {
        viewModelScope.launch {
            _intent.emit(intent)
        }
    }

    protected fun setState(reduce: State.() -> State) {
        _uiState.update { it.reduce() }
    }

    protected fun setEffect(builder: () -> Effect) {
        val effectValue = builder()
        viewModelScope.launch { _effect.send(effectValue) }
    }

    abstract fun handleIntent(intent: Intent)
}
