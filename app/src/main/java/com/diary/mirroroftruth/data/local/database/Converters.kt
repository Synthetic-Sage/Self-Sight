package com.diary.mirroroftruth.data.local.database

import androidx.room.TypeConverter
import com.diary.mirroroftruth.domain.model.StepType

class Converters {
    @TypeConverter
    fun fromStringList(value: List<String>?): String {
        return value?.joinToString(separator = ",") ?: ""
    }

    @TypeConverter
    fun toStringList(value: String?): List<String> {
        if (value.isNullOrBlank()) return emptyList()
        return value.split(",").map { it.trim() }.filter { it.isNotEmpty() }
    }

    @TypeConverter
    fun fromStepType(value: StepType): String {
        return value.name
    }

    @TypeConverter
    fun toStepType(value: String): StepType {
        return try {
            StepType.valueOf(value)
        } catch (e: IllegalArgumentException) {
            StepType.BIG
        }
    }
}
