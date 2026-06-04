package com.example.data.local.database

import androidx.room.TypeConverter
import com.example.domain.model.RouterAuthType

class Converters {
    @TypeConverter
    fun fromStringList(value: List<String>?): String? =
        value?.joinToString(separator = "|||")

    @TypeConverter
    fun toStringList(value: String?): List<String>? =
        value?.split("|||")?.filter { it.isNotEmpty() }

    @TypeConverter
    fun fromAuthType(value: RouterAuthType): String = value.name

    @TypeConverter
    fun toAuthType(value: String): RouterAuthType =
        RouterAuthType.entries.find { it.name == value } ?: RouterAuthType.FORM
}
