package com.diary.mirroroftruth.presentation.home

import com.diary.mirroroftruth.domain.model.Task

sealed interface HomeEvent {
    data class OnToggleTaskCompletion(val task: Task, val isCompleted: Boolean) : HomeEvent
    object OnAddSampleData : HomeEvent
}
