package com.example.domain.model

sealed class TestState {
    data object IDLE : TestState()
    data object RUNNING : TestState()
    data object PAUSED : TestState()
    data object LOAD_ERROR : TestState()
    data object DONE : TestState()
    data object CANCELLED : TestState()

    val name: String get() = this::class.simpleName ?: ""
}
