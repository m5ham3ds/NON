package com.example.domain.repository

import com.example.data.local.entity.TestResultEntity
import kotlinx.coroutines.flow.Flow

interface ITestResultRepository {
    val allResults: Flow<List<TestResultEntity>>
    fun getResultsBySession(sessionId: Long): Flow<List<TestResultEntity>>
    suspend fun insertResult(result: TestResultEntity)
    suspend fun insertResults(results: List<TestResultEntity>)
    suspend fun deleteBySession(sessionId: Long)
    suspend fun deleteAll()
}
