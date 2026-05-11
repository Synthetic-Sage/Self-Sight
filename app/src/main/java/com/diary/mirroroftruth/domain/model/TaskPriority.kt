package com.diary.mirroroftruth.domain.model

enum class TaskPriority(val colorHex: String, val label: String) {
    HIGH("#FF5252", "High"),
    MEDIUM("#FFC107", "Medium"),
    LOW("#4CAF50", "Low"),
    NONE("#00000000", "None")
}
