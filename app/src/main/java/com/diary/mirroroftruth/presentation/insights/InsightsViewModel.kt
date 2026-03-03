package com.diary.mirroroftruth.presentation.insights

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
class InsightsViewModel @Inject constructor(
    private val journalRepo: JournalEntryRepository
) : ViewModel() {

    private val _state = MutableStateFlow(InsightsState())
    val state: StateFlow<InsightsState> = _state.asStateFlow()

    private val calendar = Calendar.getInstance()

    private val startOfMonth: Long
        get() = Calendar.getInstance().apply {
            set(Calendar.DAY_OF_MONTH, 1)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis

    private val endOfMonth: Long
        get() {
            val lastDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
            return Calendar.getInstance().apply {
                set(Calendar.DAY_OF_MONTH, lastDay)
                set(Calendar.HOUR_OF_DAY, 23)
                set(Calendar.MINUTE, 59)
                set(Calendar.SECOND, 59)
                set(Calendar.MILLISECOND, 999)
            }.timeInMillis
        }

    init {
        loadInsights()
    }

    private fun loadInsights() {
        viewModelScope.launch {
            journalRepo.getJournalEntriesBetween(startOfMonth, endOfMonth).collect { entries ->
                // Heatmap: day-of-month -> hasEntry
                val entriesByDay = mutableMapOf<Int, Boolean>()
                val lastDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
                for (day in 1..lastDay) {
                    entriesByDay[day] = false
                }
                entries.forEach { entry ->
                    val entryCalendar = Calendar.getInstance().apply { timeInMillis = entry.date }
                    val day = entryCalendar.get(Calendar.DAY_OF_MONTH)
                    entriesByDay[day] = true
                }

                // Mood counts
                val moodCounts = entries
                    .filter { it.mood.isNotEmpty() }
                    .groupBy { it.mood }
                    .mapValues { it.value.size }

                // Streak: count backwards from today
                val todayDay = calendar.get(Calendar.DAY_OF_MONTH)
                var streak = 0
                for (day in todayDay downTo 1) {
                    if (entriesByDay[day] == true) streak++ else break
                }

                _state.update {
                    it.copy(
                        entriesByDay = entriesByDay,
                        moodCounts = moodCounts,
                        totalEntries = entries.size,
                        currentStreak = streak,
                        isLoading = false
                    )
                }
            }
        }
    }

    fun onEvent(event: InsightsEvent) {
        when (event) {
            is InsightsEvent.Refresh -> {
                _state.update { it.copy(isLoading = true) }
                loadInsights()
            }
        }
    }
}
