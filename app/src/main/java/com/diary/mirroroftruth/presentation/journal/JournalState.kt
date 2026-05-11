package com.diary.mirroroftruth.presentation.journal

import com.diary.mirroroftruth.domain.model.JournalEntry

data class JournalState(
    val selectedTags: List<String> = emptyList(),
    val wentWell: String = "",
    val toImprove: String = "",
    val learning: String = "",
    val additionalAnswers: List<String> = emptyList(), // for extra custom prompts (index 3+)
    val newTasks: List<String> = emptyList(),
    val currentDate: Long = System.currentTimeMillis(),
    val isSaved: Boolean = false,
    val isLoading: Boolean = false,
    val isPastDate: Boolean = false
)
