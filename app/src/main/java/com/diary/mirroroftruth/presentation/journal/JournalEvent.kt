package com.diary.mirroroftruth.presentation.journal

sealed interface JournalEvent {
    data class OnTagToggled(val tag: String) : JournalEvent
    data class OnWentWellChanged(val text: String) : JournalEvent
    data class OnToImproveChanged(val text: String) : JournalEvent
    data class OnLearningChanged(val text: String) : JournalEvent
    data class OnAdditionalAnswerChanged(val index: Int, val text: String) : JournalEvent
    data class OnAddNewTask(val title: String) : JournalEvent
    data class OnRemoveNewTask(val title: String) : JournalEvent
    object OnSaveEntry : JournalEvent
    object NavigateToPreviousDay : JournalEvent
    object NavigateToNextDay : JournalEvent
}
