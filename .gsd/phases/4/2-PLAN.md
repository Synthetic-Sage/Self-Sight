---
phase: 4
plan: 2
wave: 1
---

# Plan 4.2: JournalViewModel + State + Events

## Objective
Implement the MVI state management layer for the Journal screen: define the state, events, and the ViewModel that reads today's existing entry (if any) and persists a new/updated entry via the repository.

## Context
- .gsd/ARCHITECTURE.md
- app/src/main/java/com/diary/mirroroftruth/domain/model/JournalEntry.kt
- app/src/main/java/com/diary/mirroroftruth/domain/repository/JournalEntryRepository.kt
- app/src/main/java/com/diary/mirroroftruth/presentation/home/HomeViewModel.kt (pattern reference)

## Tasks

<task type="auto">
  <name>Define JournalState and JournalEvent</name>
  <files>
    - app/src/main/java/com/diary/mirroroftruth/presentation/journal/JournalState.kt
    - app/src/main/java/com/diary/mirroroftruth/presentation/journal/JournalEvent.kt
  </files>
  <action>
    - Create data class JournalState. Fields:
        selectedMood: String = ""
        wentWell: String = ""
        challenges: String = ""
        gratitude: String = ""
        tomorrowsTask: String = ""
        currentDate: Long = System.currentTimeMillis()
        isSaved: Boolean = false
        isLoading: Boolean = false
    - Create sealed interface JournalEvent. Events:
        OnMoodSelected(val mood: String)
        OnWentWellChanged(val text: String)
        OnChallengesChanged(val text: String)
        OnGratitudeChanged(val text: String)
        OnTomorrowsTaskChanged(val text: String)
        OnSaveEntry
  </action>
  <verify>Get-ChildItem -Path app/src/main/java/com/diary/mirroroftruth/presentation/journal/JournalState.kt</verify>
  <done>JournalState and JournalEvent contracts defined with all fields and intents.</done>
</task>

<task type="auto">
  <name>Create JournalViewModel</name>
  <files>
    - app/src/main/java/com/diary/mirroroftruth/presentation/journal/JournalViewModel.kt
  </files>
  <action>
    - Create JournalViewModel @Inject constructor(private val journalRepo: JournalEntryRepository) : ViewModel().
    - Use MutableStateFlow<JournalState> exposed as StateFlow.
    - In init: calculate start of today (Calendar), then launch a coroutine to collect journalRepo.getEntryForDate(today). If an entry is found, pre-populate all state fields from it so users can edit existing entries.
    - Implement fun onEvent(event: JournalEvent): handle field changes with _state.update { }, and handle OnSaveEntry by calling journalRepo.insertJournalEntry() with a new JournalEntry constructed from current state. Set isSaved = true after successful save.
    - The promptResponses field in JournalEntry should store all 3 responses serialized as a simple pipe-delimited string: "wentWell|challenges|gratitude".
  </action>
  <verify>Get-ChildItem -Path app/src/main/java/com/diary/mirroroftruth/presentation/journal/JournalViewModel.kt</verify>
  <done>JournalViewModel loads today's entry on init and persists on OnSaveEntry event.</done>
</task>

## Success Criteria
- [ ] JournalState models all text field values and UI flags.
- [ ] JournalEvent covers every user interaction.
- [ ] JournalViewModel pre-loads today's entry and saves to the database.
