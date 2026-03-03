package com.diary.mirroroftruth.presentation.journal

sealed interface JournalEvent {
    data class OnMoodSelected(val mood: String) : JournalEvent
    data class OnWentWellChanged(val text: String) : JournalEvent
    data class OnChallengesChanged(val text: String) : JournalEvent
    data class OnGratitudeChanged(val text: String) : JournalEvent
    data class OnTomorrowsTaskChanged(val text: String) : JournalEvent
    object OnSaveEntry : JournalEvent
}
