---
phase: 3
plan: 2
wave: 1
---

# Plan 3.2: HomeViewModel and State Management

## Objective
Implement the MVI (Model-View-Intent) pattern for the Home Screen using a ViewModel to handle asynchronous reads from the Task and Goal Repositories.

## Context
- .gsd/SPEC.md
- app/src/main/java/com/diary/mirroroftruth/presentation/home/

## Tasks

<task type="auto">
  <name>Define State and Events</name>
  <files>
    - app/src/main/java/com/diary/mirroroftruth/presentation/home/HomeState.kt
    - app/src/main/java/com/diary/mirroroftruth/presentation/home/HomeEvent.kt
  </files>
  <action>
    - Create `data class HomeState`. Fields: `tasks: List<Task> = emptyList()`, `goals: List<Goal> = emptyList()`, `currentDate: Long = ...`, `isLoading: Boolean = false`.
    - Create `sealed interface HomeEvent`. Events: `OnToggleTaskCompletion(val task: Task, val isCompleted: Boolean)`, `OnAddSampleData` (for testing UI).
  </action>
  <verify>Get-ChildItem -Path app/src/main/java/com/diary/mirroroftruth/presentation/home/HomeState.kt</verify>
  <done>State and Event contracts are strictly defined.</done>
</task>

<task type="auto">
  <name>Create HomeViewModel</name>
  <files>
    - app/src/main/java/com/diary/mirroroftruth/presentation/home/HomeViewModel.kt
  </files>
  <action>
    - Create `HomeViewModel @Inject constructor(private val taskRepo: TaskRepository, private val goalRepo: GoalRepository) : ViewModel()`.
    - Use `MutableStateFlow<HomeState>` exposed as `StateFlow`.
    - In `init`, launch coroutines to collect `taskRepo.getTasksForDate(today)` and `goalRepo.getAllGoals()` and update the state flow.
    - Implement `fun onEvent(event: HomeEvent)` to handle the interactions, including toggling task completion.
  </action>
  <verify>Get-ChildItem -Path app/src/main/java/com/diary/mirroroftruth/presentation/home/HomeViewModel.kt</verify>
  <done>ViewModel successfully extracts data from Repositories and maps them to HomeState.</done>
</task>

## Success Criteria
- [ ] `HomeState` encapsulates all UI data.
- [ ] `HomeEvent` represents user intents.
- [ ] `HomeViewModel` binds Repositories to the UI State via Flow.
