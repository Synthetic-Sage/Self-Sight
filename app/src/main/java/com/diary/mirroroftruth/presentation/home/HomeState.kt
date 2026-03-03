package com.diary.mirroroftruth.presentation.home

import com.diary.mirroroftruth.domain.model.Goal
import com.diary.mirroroftruth.domain.model.Task

data class HomeState(
    val tasks: List<Task> = emptyList(),
    val goals: List<Goal> = emptyList(),
    val currentDate: Long = System.currentTimeMillis(),
    val isLoading: Boolean = false
)
