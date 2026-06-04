package com.example.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
data class TestResult(
    val id: Long = 0,
    val sessionId: Long = 0,
    val cardCode: String = "",
    val routerId: Long = 0,
    val routerName: String = "",
    val state: String = "",
    val message: String = "",
    val durationMs: Long = 0,
    val testedAt: Long = System.currentTimeMillis()
) : Parcelable
