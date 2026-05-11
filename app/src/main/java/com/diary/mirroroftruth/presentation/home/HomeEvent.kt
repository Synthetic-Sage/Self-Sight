package com.diary.mirroroftruth.presentation.home

import com.diary.mirroroftruth.domain.model.Goal
import com.diary.mirroroftruth.domain.model.StepType
import com.diary.mirroroftruth.domain.model.Task

sealed interface HomeEvent {
    data class OnToggleTaskCompletion(val task: Task, val isCompleted: Boolean) : HomeEvent
    data class OnAddTask(val title: String) : HomeEvent
    data class OnDeleteTask(val task: Task) : HomeEvent
    data class OnReorderTasks(val fromIndex: Int, val toIndex: Int) : HomeEvent
    object OnAddSampleData : HomeEvent

    // Step Events
    data class OnAddStep(val title: String, val type: StepType, val target: Float, val description: String? = null) : HomeEvent
    data class OnStepProgress(val goal: Goal, val progress: Float) : HomeEvent
    data class OnDeleteStep(val goal: Goal) : HomeEvent
    object OnDismissCelebration : HomeEvent
}
