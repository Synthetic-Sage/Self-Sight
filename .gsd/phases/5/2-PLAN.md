---
phase: 5
plan: 2
wave: 1
---

# Plan 5.2: InsightsViewModel + State + Data Aggregation

## Objective
Implement the MVI state layer for the Insights screen. The ViewModel fetches all journal entries from the current month from the repository, then aggregates them into heatmap data and mood distribution counts for the UI.

## Context
- .gsd/ARCHITECTURE.md
- app/src/main/java/com/diary/mirroroftruth/domain/model/JournalEntry.kt
- app/src/main/java/com/diary/mirroroftruth/domain/repository/JournalEntryRepository.kt
- app/src/main/java/com/diary/mirroroftruth/presentation/journal/JournalViewModel.kt (pattern reference)

## Tasks

<task type="auto">
  <name>Define InsightsState and InsightsEvent</name>
  <files>
    - app/src/main/java/com/diary/mirroroftruth/presentation/insights/InsightsState.kt
    - app/src/main/java/com/diary/mirroroftruth/presentation/insights/InsightsEvent.kt
  </files>
  <action>
    - Create data class InsightsState:
        entriesByDay: Map<Int, Boolean> = emptyMap()  // day-of-month -> hasEntry
        moodCounts: Map<String, Int> = emptyMap()     // mood label -> count
        totalEntries: Int = 0
        currentStreak: Int = 0
        isLoading: Boolean = true
    - Create sealed interface InsightsEvent (minimal — screen is read-only):
        object Refresh : InsightsEvent
  </action>
  <verify>Get-ChildItem -Path app/src/main/java/com/diary/mirroroftruth/presentation/insights/InsightsState.kt</verify>
  <done>InsightsState and InsightsEvent contracts defined.</done>
</task>

<task type="auto">
  <name>Create InsightsViewModel</name>
  <files>
    - app/src/main/java/com/diary/mirroroftruth/presentation/insights/InsightsViewModel.kt
  </files>
  <action>
    - Create InsightsViewModel @Inject constructor(private val journalRepo: JournalEntryRepository) : ViewModel().
    - In init, calculate start and end of current month using Calendar:
        startOfMonth: first day at 00:00:00
        endOfMonth: last day at 23:59:59
    - Collect journalRepo.getJournalEntriesBetween(startOfMonth, endOfMonth) in a viewModelScope coroutine.
    - On each emission, aggregate:
        entriesByDay: For each day 1..lastDayOfMonth, set true if any entry's date falls on that day.
        moodCounts: Count occurrences of each mood string across all entries.
        totalEntries: entries.size
        currentStreak: Count backwards from today; stop at the first day with no entry.
    - Update _state with aggregated data and set isLoading = false.
    - Implement fun onEvent(event: InsightsEvent): handle InsightsEvent.Refresh by re-triggering collection.
    - Use Calendar.getInstance() to get day-of-month from a Long timestamp.
  </action>
  <verify>Get-ChildItem -Path app/src/main/java/com/diary/mirroroftruth/presentation/insights/InsightsViewModel.kt</verify>
  <done>InsightsViewModel loads and aggregates current month's entries into InsightsState.</done>
</task>

## Success Criteria
- [ ] InsightsState models all aggregated data fields.
- [ ] InsightsViewModel queries current month's entries and computes heatmap + mood counts + streak.
