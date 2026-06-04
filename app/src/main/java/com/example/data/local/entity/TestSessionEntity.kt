package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "test_sessions")
data class TestSessionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val routerId: Long,
    val routerName: String,
    val totalCards: Int = 0,
    val successCount: Int = 0,
    val failureCount: Int = 0,
    val isRunning: Boolean = false,
    val startedAt: Long = System.currentTimeMillis(),
    val finishedAt: Long = 0
)
