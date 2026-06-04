package com.example.service

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ServiceState(
    val progress: Int = 0,
    val total: Int = 0,
    val currentCard: String = "",
    val successCount: Int = 0,
    val failureCount: Int = 0,
    val isPaused: Boolean = false,
    val status: String = "IDLE", // IDLE/RUNNING/PAUSED/DONE/LOAD_ERROR/CANCELLED
    val error: String? = null,
    val screenshotBytes: ByteArray? = null
) : Parcelable {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ServiceState

        if (progress != other.progress) return false
        if (total != other.total) return false
        if (currentCard != other.currentCard) return false
        if (successCount != other.successCount) return false
        if (failureCount != other.failureCount) return false
        if (isPaused != other.isPaused) return false
        if (status != other.status) return false
        if (error != other.error) return false
        if (screenshotBytes != null) {
            if (other.screenshotBytes == null) return false
            if (!screenshotBytes.contentEquals(other.screenshotBytes)) return false
        } else if (other.screenshotBytes != null) return false

        return true
    }

    override fun hashCode(): Int {
        var result = progress
        result = 31 * result + total
        result = 31 * result + currentCard.hashCode()
        result = 31 * result + successCount
        result = 31 * result + failureCount
        result = 31 * result + isPaused.hashCode()
        result = 31 * result + status.hashCode()
        result = 31 * result + (error?.hashCode() ?: 0)
        result = 31 * result + (screenshotBytes?.contentHashCode() ?: 0)
        return result
    }
}
