package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cards")
data class CardEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val code: String,
    val prefix: String = "",
    val length: Int = 0,
    val charset: String = "",
    val createdAt: Long = System.currentTimeMillis()
)
