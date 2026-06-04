package com.example.presentation.history

import android.content.Context
import androidx.lifecycle.viewModelScope
import com.example.data.local.entity.TestResultEntity
import com.example.data.local.entity.TestSessionEntity
import com.example.domain.repository.ISessionRepository
import com.example.domain.repository.ITestResultRepository
import com.example.domain.usecase.ExportResultsUseCase
import com.example.presentation.common.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.io.File

@OptIn(ExperimentalCoroutinesApi::class)
class HistoryViewModel(
    private val sessionRepository: ISessionRepository,
    private val testResultRepository: ITestResultRepository,
    private val exportResultsUseCase: ExportResultsUseCase
) : BaseViewModel() {

    private val _selectedSessionId = MutableStateFlow<Long?>(null)
    val selectedSessionId: StateFlow<Long?> = _selectedSessionId.asStateFlow()

    private val _currentFilter = MutableStateFlow("all") // "all"/"success"/"failure"
    val currentFilter: StateFlow<String> = _currentFilter.asStateFlow()

    val sessions: StateFlow<List<TestSessionEntity>> = sessionRepository.allSessions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val filteredResults: StateFlow<List<TestResultEntity>> = combine(
        _selectedSessionId.flatMapLatest { id ->
            if (id != null) testResultRepository.getResultsBySession(id)
            else flowOf(emptyList())
        },
        _currentFilter
    ) { results, filter ->
        when (filter) {
            "success" -> results.filter { it.state == "Success" }
            "failure" -> results.filter { it.state != "Success" }
            else -> results
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _exportStatus = MutableSharedFlow<String>()
    val exportStatus: SharedFlow<String> = _exportStatus.asSharedFlow()

    fun selectSession(id: Long) {
        _selectedSessionId.value = id
    }

    fun setFilter(filter: String) {
        _currentFilter.value = filter
    }

    fun exportToFile(context: Context, fileName: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val dir = context.getExternalFilesDir("exports") ?: context.filesDir
            val result = exportResultsUseCase(dir, fileName)
            val message = when (result) {
                is ExportResultsUseCase.Result.Success -> "تم تصدير النتائج بنجاح إلى: ${result.path}"
                is ExportResultsUseCase.Result.Error -> "فشل عملية التصدير: ${result.message}"
            }
            _exportStatus.emit(message)
        }
    }
}
