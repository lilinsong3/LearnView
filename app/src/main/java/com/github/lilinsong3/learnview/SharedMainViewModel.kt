package com.github.lilinsong3.learnview

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class SharedMainViewModel: ViewModel() {
    private val _event: MutableSharedFlow<Event> = MutableSharedFlow()
    val event = _event.asSharedFlow()
    fun send(e: Event) {
        viewModelScope.launch {
            _event.emit(e)
        }
    }
}