package com.example.presentation.test

import androidx.lifecycle.viewModelScope
import com.example.domain.model.TestConfig
import com.example.domain.repository.ISessionRepository
import com.example.domain.repository.ITestResultRepository
import com.example.presentation.common.BaseViewModel
import com.example.service.ServiceState
import com.example.service.TestService
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class TestViewModel(
    private val sessionRepository: ISessionRepository,
    private val testResultRepository: ITestResultRepository
) : BaseViewModel() {

    sealed class UiEvent {
        data class ShowRetryDialog(val message: String) : UiEvent()
        object ShowCancelDialog : UiEvent()
        object NavigateBack : UiEvent()
    }

    private val _uiEvent = MutableSharedFlow<UiEvent>()
    val uiEvent: SharedFlow<UiEvent> = _uiEvent.asSharedFlow()

    private val _testConfig = MutableStateFlow<TestConfig?>(null)
    val testConfig: StateFlow<TestConfig?> = _testConfig.asStateFlow()

    val serviceState: StateFlow<ServiceState> = TestService.serviceState

    fun saveTestConfig(routerId: Long, cardList: List<String>, delayMs: Long) {
        _testConfig.value = TestConfig(routerId, cardList, delayMs)
    }

    fun isTestActive(): Boolean {
        val status = serviceState.value.status
        return status == "RUNNING" || status == "PAUSED" || status == "LOAD_ERROR"
    }

    fun pauseTest() {
        // Handled by posting custom pauses or service triggers handled in startForegroundService from Fragment.
        // We will expose this status properly.
    }

    fun requestCancelTest() {
        viewModelScope.launch {
            _uiEvent.emit(UiEvent.ShowCancelDialog)
        }
    }

    fun onLoadError(error: String) {
        viewModelScope.launch {
            _uiEvent.emit(UiEvent.ShowRetryDialog(error))
        }
    }

    fun navigateBack() {
        viewModelScope.launch {
            _uiEvent.emit(UiEvent.NavigateBack)
        }
    }
}
