package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "test_results")
data class TestResultEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val sessionId: Long,
    val cardCode: String,
    val routerId: Long,
    val routerName: String,
    val state: String,
    val message: String,
    val durationMs: Long = 0,
    val testedAt: Long = System.currentTimeMillis()
)
