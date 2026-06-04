package com.example.domain.repository

import com.example.data.local.entity.TestSessionEntity
import kotlinx.coroutines.flow.Flow

interface ISessionRepository {
    val allSessions: Flow<List<TestSessionEntity>>
    suspend fun insert(session: TestSessionEntity): Long
    suspend fun update(session: TestSessionEntity)
    suspend fun getById(id: Long): TestSessionEntity?
    suspend fun getRunningSession(): TestSessionEntity?
    suspend fun cleanUpOrphanedSessions()
    suspend fun markFinished(sessionId: Long, successCount: Int, failureCount: Int)
    suspend fun createSession(routerId: Long, routerName: String): Long
    suspend fun finishSession(id: Long)
    suspend fun deleteAll()
}
