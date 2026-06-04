package com.example.presentation.home

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import androidx.lifecycle.viewModelScope
import com.example.data.local.entity.RouterProfileEntity
import com.example.data.local.preferences.AppPreferences
import com.example.domain.model.LogEntry
import com.example.domain.model.Statistics
import com.example.domain.repository.ISessionRepository
import com.example.domain.repository.ITestResultRepository
import com.example.domain.usecase.GenerateCardsUseCase
import com.example.domain.usecase.ManageRoutersUseCase
import com.example.presentation.common.BaseViewModel
import com.example.service.TestService
import com.example.data.mapper.TestResultMapper.toLogEntries
import com.example.data.mapper.TestResultMapper.toStatistics
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import timber.log.Timber

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModel(
    private val generateCardsUseCase: GenerateCardsUseCase,
    private val manageRoutersUseCase: ManageRoutersUseCase,
    private val sessionRepository: ISessionRepository,
    private val testResultRepository: ITestResultRepository,
    private val appPreferences: AppPreferences
) : BaseViewModel() {

    sealed class UiState {
        object Idle : UiState()
        object Testing : UiState()
        data class Finished(val message: String) : UiState()
        data class Error(val error: String) : UiState()
    }

    sealed class ConnectionState {
        object CONNECTED : ConnectionState()
        object DISCONNECTED : ConnectionState()
    }

    private val _selectedRouterId = MutableStateFlow<Long>(-1L)
    val selectedRouterId: StateFlow<Long> = _selectedRouterId.asStateFlow()

    val routers: StateFlow<List<RouterProfileEntity>> = manageRoutersUseCase.allRouters
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _uiState = MutableStateFlow<UiState>(UiState.Idle)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private val _connectionState = MutableStateFlow<ConnectionState>(ConnectionState.DISCONNECTED)
    val connectionState: StateFlow<ConnectionState> = _connectionState.asStateFlow()

    private val _logEntries = MutableStateFlow<List<LogEntry>>(emptyList())
    val logEntries: StateFlow<List<LogEntry>> = _logEntries.asStateFlow()

    private val _statistics = MutableStateFlow(Statistics())
    val statistics: StateFlow<Statistics> = _statistics.asStateFlow()

    private val _startTestEvent = MutableSharedFlow<TestStartConfig>()
    val startTestEvent: SharedFlow<TestStartConfig> = _startTestEvent.asSharedFlow()

    private var connectivityManager: ConnectivityManager? = null
    private var networkCallback: ConnectivityManager.NetworkCallback? = null

    init {
        observeLatestSession()
    }

    private fun observeLatestSession() {
        viewModelScope.launch {
            kotlinx.coroutines.flow.combine(
                sessionRepository.allSessions,
                TestService.isRunning
            ) { sessions, isServiceRunning ->
                Pair(sessions, isServiceRunning)
            }.flatMapLatest { (sessions, isServiceRunning) ->
                val latest = sessions.firstOrNull()
                when {
                    latest == null -> flowOf(Triple(emptyList<LogEntry>(), Statistics(), UiState.Idle))
                    latest.isRunning && isServiceRunning -> testResultRepository.getResultsBySession(latest.id)
                        .map { results -> Triple(results.toLogEntries(), results.toStatistics(), UiState.Testing) }
                    else -> flowOf(Triple(emptyList<LogEntry>(), Statistics(), UiState.Idle))
                }
            }.collect { (logs, stats, state) ->
                _logEntries.value = logs
                _statistics.value = stats
                _uiState.value = state
            }
        }
    }

    fun clearLogs() {
        viewModelScope.launch {
            try {
                testResultRepository.deleteAll()
                sessionRepository.deleteAll()
                _logEntries.value = emptyList()
                _statistics.value = Statistics()
                _uiState.value = UiState.Idle
            } catch (e: Exception) {
                Timber.e(e, "Failed to clear logs")
            }
        }
    }

    fun selectRouter(id: Long) {
        _selectedRouterId.value = id
    }

    fun isServiceRunning(): StateFlow<Boolean> {
        return TestService.isRunning
    }

    fun startMonitoringConnection(context: Context) {
        if (connectivityManager != null) return
        connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val request = NetworkRequest.Builder()
            .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
            .build()

        val callback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                _connectionState.value = ConnectionState.CONNECTED
            }

            override fun onLost(network: Network) {
                _connectionState.value = ConnectionState.DISCONNECTED
            }
        }
        networkCallback = callback
        try {
            connectivityManager?.registerNetworkCallback(request, callback)
        } catch (e: Throwable) {
            Timber.e(e, "Failed to register network callback")
        }

        // Initial check
        try {
            val activeNetwork = connectivityManager?.activeNetwork
            val caps = connectivityManager?.getNetworkCapabilities(activeNetwork)
            if (caps != null && caps.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                _connectionState.value = ConnectionState.CONNECTED
            } else {
                _connectionState.value = ConnectionState.DISCONNECTED
            }
        } catch (e: Throwable) {
            Timber.e(e, "Failed to get initial network capabilities")
            _connectionState.value = ConnectionState.DISCONNECTED
        }
    }

    fun stopMonitoringConnection() {
        networkCallback?.let {
            try {
                connectivityManager?.unregisterNetworkCallback(it)
            } catch (e: Throwable) {
                Timber.e(e, "Failed to unregister network callback")
            }
        }
        networkCallback = null
        connectivityManager = null
    }

    fun generateAndStart(
        prefix: String,
        length: Int,
        count: Int,
        charset: String,
        delayMs: Long
    ) {
        val routerId = _selectedRouterId.value
        if (routerId == -1L) {
            viewModelScope.launch {
                _errorFlow.emit("الرجاء اختيار راوتر أولاً")
            }
            return
        }

        viewModelScope.launch {
            try {
                // Generate
                val cards = generateCardsUseCase(prefix, length, count, charset)
                if (cards.isEmpty()) {
                    _errorFlow.emit("فشل توليد البطاقات")
                    return@launch
                }
                
                val plist = ArrayList(cards.map { it.code })
                _startTestEvent.emit(TestStartConfig(routerId, plist, delayMs))
            } catch (e: Exception) {
                _errorFlow.emit("حدث خطأ أثناء البدء: ${e.message}")
            }
        }
    }

    data class TestStartConfig(
        val routerId: Long,
        val cardList: ArrayList<String>,
        val delayMs: Long
    )
}
