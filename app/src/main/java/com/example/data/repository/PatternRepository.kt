package com.example.data.repository

import com.example.data.local.database.PatternDao
import com.example.data.local.entity.SuccessfulPatternEntity
import com.example.domain.repository.IPatternRepository
import kotlinx.coroutines.flow.Flow

class PatternRepository(private val patternDao: PatternDao) : IPatternRepository {
    override val allPatterns: Flow<List<SuccessfulPatternEntity>> = patternDao.getAllPatterns()

    override suspend fun insert(pattern: SuccessfulPatternEntity) {
        patternDao.insert(pattern)
    }

    override suspend fun delete(pattern: SuccessfulPatternEntity) {
        patternDao.delete(pattern)
    }

    override suspend fun deleteByRouter(routerId: Long) {
        patternDao.deleteByRouter(routerId)
    }
}
