package com.example.domain.repository

import com.example.data.local.entity.SuccessfulPatternEntity
import kotlinx.coroutines.flow.Flow

interface IPatternRepository {
    val allPatterns: Flow<List<SuccessfulPatternEntity>>
    suspend fun insert(pattern: SuccessfulPatternEntity)
    suspend fun delete(pattern: SuccessfulPatternEntity)
    suspend fun deleteByRouter(routerId: Long)
}
