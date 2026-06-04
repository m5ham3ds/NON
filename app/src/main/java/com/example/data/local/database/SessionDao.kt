package com.example.data.local.database

import androidx.room.*
import com.example.data.local.entity.TestSessionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SessionDao {
    @Query("SELECT * FROM test_sessions ORDER BY startedAt DESC")
    fun getAllSessions(): Flow<List<TestSessionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(session: TestSessionEntity): Long

    @Update
    suspend fun update(session: TestSessionEntity)

    @Query("SELECT * FROM test_sessions WHERE id = :id")
    suspend fun getById(id: Long): TestSessionEntity?

    @Query("SELECT * FROM test_sessions WHERE isRunning = 1 LIMIT 1")
    suspend fun getRunningSession(): TestSessionEntity?

    @Query("UPDATE test_sessions SET isRunning = 0 WHERE isRunning = 1")
    suspend fun cleanUpOrphanedSessions()

    @Query("UPDATE test_sessions SET isRunning = 0, successCount = :successCount, failureCount = :failureCount, finishedAt = :finishedAt WHERE id = :sessionId")
    suspend fun markFinished(sessionId: Long, successCount: Int, failureCount: Int, finishedAt: Long)

    @Query("DELETE FROM test_sessions")
    suspend fun deleteAll()
}
