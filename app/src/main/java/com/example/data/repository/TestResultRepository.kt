package com.example.data.repository

import com.example.data.local.database.TestResultDao
import com.example.data.local.entity.TestResultEntity
import com.example.domain.repository.ITestResultRepository
import kotlinx.coroutines.flow.Flow

class TestResultRepository(private val testResultDao: TestResultDao) : ITestResultRepository {
    override val allResults: Flow<List<TestResultEntity>> = testResultDao.getAllResults()

    override fun getResultsBySession(sessionId: Long): Flow<List<TestResultEntity>> {
        return testResultDao.getResultsBySession(sessionId)
    }

    override suspend fun insertResult(result: TestResultEntity) {
        testResultDao.insertResult(result)
    }

    override suspend fun insertResults(results: List<TestResultEntity>) {
        testResultDao.insertResults(results)
    }

    override suspend fun deleteBySession(sessionId: Long) {
        testResultDao.deleteBySession(sessionId)
    }

    override suspend fun deleteAll() {
        testResultDao.deleteAll()
    }
}
