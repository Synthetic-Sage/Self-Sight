package com.diary.mirroroftruth.presentation.journal

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.diary.mirroroftruth.domain.model.JournalEntry
import com.diary.mirroroftruth.domain.repository.JournalEntryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class JournalViewModel @Inject constructor(
    private val journalRepo: JournalEntryRepository
) : ViewModel() {

    private val _state = MutableStateFlow(JournalState())
    val state: StateFlow<JournalState> = _state.asStateFlow()

    private val startOfToday: Long
        get() = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis

    init {
        _state.update { it.copy(currentDate = startOfToday) }
        loadTodaysEntry()
    }

    private fun loadTodaysEntry() {
        viewModelScope.launch {
            journalRepo.getJournalEntryForDate(startOfToday).collect { entry ->
                if (entry != null) {
                    val parts = entry.promptResponses.split("|")
                    _state.update {
                        it.copy(
                            selectedMood = entry.mood,
                            wentWell = parts.getOrElse(0) { "" },
                            challenges = parts.getOrElse(1) { "" },
                            gratitude = parts.getOrElse(2) { "" },
                            tomorrowsTask = entry.content
                        )
                    }
                }
            }
        }
    }

    fun onEvent(event: JournalEvent) {
        when (event) {
            is JournalEvent.OnMoodSelected -> _state.update { it.copy(selectedMood = event.mood, isSaved = false) }
            is JournalEvent.OnWentWellChanged -> _state.update { it.copy(wentWell = event.text, isSaved = false) }
            is JournalEvent.OnChallengesChanged -> _state.update { it.copy(challenges = event.text, isSaved = false) }
            is JournalEvent.OnGratitudeChanged -> _state.update { it.copy(gratitude = event.text, isSaved = false) }
            is JournalEvent.OnTomorrowsTaskChanged -> _state.update { it.copy(tomorrowsTask = event.text, isSaved = false) }
            is JournalEvent.OnSaveEntry -> saveEntry()
        }
    }

    private fun saveEntry() {
        val current = _state.value
        viewModelScope.launch {
            val entry = JournalEntry(
                date = startOfToday,
                mood = current.selectedMood,
                content = current.tomorrowsTask,
                promptResponses = "${current.wentWell}|${current.challenges}|${current.gratitude}"
            )
            journalRepo.insertJournalEntry(entry)
            _state.update { it.copy(isSaved = true) }
        }
    }
}
