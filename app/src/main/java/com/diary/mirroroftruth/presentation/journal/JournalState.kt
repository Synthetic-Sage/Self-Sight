package com.diary.mirroroftruth.presentation.journal

import com.diary.mirroroftruth.domain.model.JournalEntry

data class JournalState(
    val selectedMood: String = "",
    val wentWell: String = "",
    val challenges: String = "",
    val gratitude: String = "",
    val tomorrowsTask: String = "",
    val currentDate: Long = System.currentTimeMillis(),
    val isSaved: Boolean = false,
    val isLoading: Boolean = false
)
