package com.example.data.local.database

import androidx.room.*
import com.example.data.local.entity.SuccessfulPatternEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PatternDao {
    @Query("SELECT * FROM successful_patterns ORDER BY discovered_at DESC")
    fun getAllPatterns(): Flow<List<SuccessfulPatternEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(pattern: SuccessfulPatternEntity)

    @Delete
    suspend fun delete(pattern: SuccessfulPatternEntity)

    @Query("DELETE FROM successful_patterns WHERE router_id = :routerId")
    suspend fun deleteByRouter(routerId: Long)
}
