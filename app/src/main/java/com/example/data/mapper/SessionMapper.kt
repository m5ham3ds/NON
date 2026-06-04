package com.example.data.mapper

import com.example.data.local.entity.TestSessionEntity
import com.example.domain.model.TestSession

object SessionMapper {
    fun TestSessionEntity.toDomain(): TestSession = TestSession(
        id = id,
        routerId = routerId,
        routerName = routerName,
        totalCards = totalCards,
        successCount = successCount,
        failureCount = failureCount,
        isRunning = isRunning,
        startedAt = startedAt,
        finishedAt = finishedAt
    )

    fun List<TestSessionEntity>.toDomainList(): List<TestSession> = map { it.toDomain() }
}
