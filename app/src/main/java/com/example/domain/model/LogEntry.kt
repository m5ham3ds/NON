package com.example.domain.model

enum class LogLevel {
    SUCCESS,
    ERROR,
    WARNING,
    INFO
}

data class LogEntry(
    val level: LogLevel = LogLevel.INFO,
    val message: String = "",
    val timestamp: Long = System.currentTimeMillis()
)
