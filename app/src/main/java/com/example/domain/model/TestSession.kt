package com.example.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
data class TestSession(
    val id: Long = 0,
    val routerId: Long = 0,
    val routerName: String = "",
    val totalCards: Int = 0,
    val successCount: Int = 0,
    val failureCount: Int = 0,
    val isRunning: Boolean = false,
    val startedAt: Long = System.currentTimeMillis(),
    val finishedAt: Long = 0
) : Parcelable
