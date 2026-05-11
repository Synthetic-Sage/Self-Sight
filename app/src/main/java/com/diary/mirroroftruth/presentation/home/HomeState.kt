package com.diary.mirroroftruth.presentation.home

import com.diary.mirroroftruth.domain.model.Goal
import com.diary.mirroroftruth.domain.model.Task

data class HomeState(
    val tasks: List<Task> = emptyList(),
    val bigSteps: List<Goal> = emptyList(),
    val smallSteps: List<Goal> = emptyList(),
    val currentDate: Long = System.currentTimeMillis(),
    val isLoading: Boolean = false,
    val dailyQuote: String = "",
    val showBigStepCelebration: Boolean = false,
    val showSmallStepCelebration: Boolean = false
)
