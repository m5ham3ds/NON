package com.example.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "successful_patterns",
    foreignKeys = [ForeignKey(
        entity = RouterProfileEntity::class,
        parentColumns = ["id"],
        childColumns = ["router_id"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index(value = ["router_id"])]
)
data class SuccessfulPatternEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "router_id") val routerId: Long,
    @ColumnInfo(name = "pattern") val pattern: String,
    @ColumnInfo(name = "confidence") val confidence: Float = 0f,
    @ColumnInfo(name = "discovered_at") val discoveredAt: Long = System.currentTimeMillis()
)
