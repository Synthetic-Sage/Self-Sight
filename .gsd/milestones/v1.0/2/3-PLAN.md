---
phase: 2
plan: 3
wave: 2
---

# Plan 2.3: Repository Layer

## Objective
Establish the Clean Architecture repository pattern by creating domain models, interfaces, and their data-layer implementations.

## Context
- .gsd/ARCHITECTURE.md
- app/src/main/java/com/diary/mirroroftruth/domain/model
- app/src/main/java/com/diary/mirroroftruth/domain/repository
- app/src/main/java/com/diary/mirroroftruth/data/repository
- app/src/main/java/com/diary/mirroroftruth/data/mapper

## Tasks

<task type="auto">
  <name>Define Domain Models & Repository Interfaces</name>
  <files>
    - app/src/main/java/com/diary/mirroroftruth/domain/model/Task.kt
    - app/src/main/java/com/diary/mirroroftruth/domain/model/Goal.kt
    - app/src/main/java/com/diary/mirroroftruth/domain/model/JournalEntry.kt
    - app/src/main/java/com/diary/mirroroftruth/domain/repository/TaskRepository.kt
    - app/src/main/java/com/diary/mirroroftruth/domain/repository/GoalRepository.kt
    - app/src/main/java/com/diary/mirroroftruth/domain/repository/JournalEntryRepository.kt
  </files>
  <action>
    - Create pure domain data classes (`Task`, `Goal`, `JournalEntry`) without Room or Android dependencies.
    - Create corresponding repository interfaces exposing suspend functions for writes and `Flow` for reads (e.g., `fun getTasksForDate(date: Long): Flow<List<Task>>`).
  </action>
  <verify>Get-ChildItem -Path app/src/main/java/com/diary/mirroroftruth/domain/repository/ -Filter *Repository.kt</verify>
  <done>Domain models and interfaces are defined cleanly.</done>
</task>

<task type="auto">
  <name>Implement Repositories and Mapper & DI Modules</name>
  <files>
    - app/src/main/java/com/diary/mirroroftruth/data/mapper/EntityMapper.kt
    - app/src/main/java/com/diary/mirroroftruth/data/repository/TaskRepositoryImpl.kt
    - app/src/main/java/com/diary/mirroroftruth/data/repository/GoalRepositoryImpl.kt
    - app/src/main/java/com/diary/mirroroftruth/data/repository/JournalEntryRepositoryImpl.kt
    - app/src/main/java/com/diary/mirroroftruth/di/RepositoryModule.kt
  </files>
  <action>
    - Create `EntityMapper.kt` with extension functions mapping Entities to Domain Models and vice-versa (e.g., `fun TaskEntity.toDomain(): Task`).
    - Implement `TaskRepositoryImpl`, `GoalRepositoryImpl`, and `JournalEntryRepositoryImpl` bridging the DAOs and Domain interfaces, mapping between types.
    - Create `RepositoryModule.kt` (`@Module @InstallIn(SingletonComponent::class)`) using `@Binds` or `@Provides` to wire the Impl classes to the matching Domain Interfaces.
  </action>
  <verify>./gradlew app:assembleDebug</verify>
  <done>Repositories compile, mapping logic is sound, and Hilt graph is complete.</done>
</task>

## Success Criteria
- [ ] Domain models contain no Room annotations.
- [ ] Mapper extensions are converting objects correctly.
- [ ] Repository Interfaces are properly wired to Implementations using Hilt.
