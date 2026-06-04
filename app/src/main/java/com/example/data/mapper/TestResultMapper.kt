package com.example.data.mapper

import com.example.data.local.entity.TestResultEntity
import com.example.domain.model.TestResult
import com.example.domain.model.LogEntry
import com.example.domain.model.LogLevel
import com.example.domain.model.Statistics

object TestResultMapper {
    fun TestResultEntity.toDomain(): TestResult = TestResult(
        id = id,
        sessionId = sessionId,
        cardCode = cardCode,
        routerId = routerId,
        routerName = routerName,
        state = state,
        message = message,
        durationMs = durationMs,
        testedAt = testedAt
    )

    fun List<TestResultEntity>.toDomainList(): List<TestResult> = map { it.toDomain() }

    fun List<TestResultEntity>.toLogEntries(): List<LogEntry> = map { entity ->
        LogEntry(
            level = if (entity.state == "Success") LogLevel.SUCCESS else LogLevel.ERROR,
            message = "${entity.cardCode}: ${entity.message}",
            timestamp = entity.testedAt
        )
    }.sortedBy { it.timestamp }

    fun List<TestResultEntity>.toStatistics(): Statistics {
        val success = count { it.state == "Success" }
        val failure = count { it.state != "Success" }
        return Statistics(
            total = size,
            success = success,
            failure = failure,
            successRate = if (size > 0) (success.toFloat() / size * 100f) else 0f
        )
    }
}
