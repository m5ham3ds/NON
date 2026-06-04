package com.example.presentation.settings

import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.viewModelScope
import com.example.R
import com.example.data.local.preferences.AppPreferences
import com.example.data.local.preferences.ThemePreferences
import com.example.domain.repository.ISessionRepository
import com.example.domain.repository.ITestResultRepository
import com.example.presentation.common.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val appPreferences: AppPreferences,
    private val themePreferences: ThemePreferences,
    private val sessionRepository: ISessionRepository,
    private val testResultRepository: ITestResultRepository
) : BaseViewModel() {

    sealed class UiEvent {
        data class ShowMessage(val resId: Int) : UiEvent()
    }

    private val _uiEvent = MutableSharedFlow<UiEvent>()
    val uiEvent: SharedFlow<UiEvent> = _uiEvent.asSharedFlow()

    fun setThemeMode(mode: String) {
        viewModelScope.launch {
            themePreferences.setThemeMode(mode)
            val nightMode = when (mode) {
                "dark" -> AppCompatDelegate.MODE_NIGHT_YES
                "light" -> AppCompatDelegate.MODE_NIGHT_NO
                else -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
            }
            AppCompatDelegate.setDefaultNightMode(nightMode)
        }
    }

    fun setAppLanguage(lang: String) {
        viewModelScope.launch {
            appPreferences.setAppLanguage(lang)
        }
    }

    fun confirmClearHistory() {
        viewModelScope.launch(Dispatchers.IO) {
            testResultRepository.deleteAll()
            sessionRepository.deleteAll()
            _uiEvent.emit(UiEvent.ShowMessage(R.string.history_cleared))
        }
    }
}
