package com.example.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
data class Card(
    val id: Long = 0,
    val code: String = "",
    val prefix: String = "",
    val length: Int = 0,
    val charset: String = "",
    val createdAt: Long = System.currentTimeMillis()
) : Parcelable {
    val value: String get() = code
}
