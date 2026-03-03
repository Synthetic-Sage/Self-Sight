package com.diary.mirroroftruth.presentation.insights

data class InsightsState(
    val entriesByDay: Map<Int, Boolean> = emptyMap(),
    val moodCounts: Map<String, Int> = emptyMap(),
    val totalEntries: Int = 0,
    val currentStreak: Int = 0,
    val isLoading: Boolean = true
)
