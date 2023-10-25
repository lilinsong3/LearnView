package com.github.lilinsong3.learnview

sealed class Event {
    data class AppBarVisibilityEvent(val shown: Boolean) : Event()
}