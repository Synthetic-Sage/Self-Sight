---
phase: 2
plan: 2
wave: 1
---

# Plan 2.2: Room DAOs and Database

## Objective
Implement Data Access Objects (DAOs) for the core entities and initialize the Room database instance along with Hilt provision.

## Context
- .gsd/SPEC.md
- app/src/main/java/com/diary/mirroroftruth/data/local/dao
- app/src/main/java/com/diary/mirroroftruth/data/local/database

## Tasks

<task type="auto">
  <name>Create DAOs</name>
  <files>
    - app/src/main/java/com/diary/mirroroftruth/data/local/dao/TaskDao.kt
    - app/src/main/java/com/diary/mirroroftruth/data/local/dao/GoalDao.kt
    - app/src/main/java/com/diary/mirroroftruth/data/local/dao/JournalEntryDao.kt
  </files>
  <action>
    - Create `@Dao` interfaces for Task, Goal, and JournalEntry.
    - Implement standard CRUD operations (`@Insert`, `@Update`, `@Delete`).
    - Add query methods returning `Flow<List<T>>` (e.g., `getTasksForDate(date)`, `getAllGoals()`, `getJournalEntriesBetween(start, end)`).
  </action>
  <verify>Get-ChildItem -Path app/src/main/java/com/diary/mirroroftruth/data/local/dao/ -Filter *Dao.kt</verify>
  <done>DAOs are created with Room annotations and Flow return types for observation.</done>
</task>

<task type="auto">
  <name>Initialize Database and Hilt Module</name>
  <files>
    - app/src/main/java/com/diary/mirroroftruth/data/local/database/MirrorDatabase.kt
    - app/src/main/java/com/diary/mirroroftruth/di/DatabaseModule.kt
  </files>
  <action>
    - Create abstract `MirrorDatabase` extending `RoomDatabase` enclosing all 3 entities.
    - Define abstract functions for each DAO.
    - Create a `DatabaseModule` object in `com.diary.mirroroftruth.di` annotated with `@Module` and `@InstallIn(SingletonComponent::class)`.
    - Provide the `MirrorDatabase` as a Singleton using `Room.databaseBuilder`.
    - Provide each DAO as a Singleton extracted from the database.
  </action>
  <verify>./gradlew app:assembleDebug</verify>
  <done>Room database compiles successfully and Hilt DI graph is valid.</done>
</task>

## Success Criteria
- [ ] 3 DAOs are fully implemented.
- [ ] `MirrorDatabase.kt` defines the schema (version 1).
- [ ] `DatabaseModule.kt` correctly injects DAOs and Database via Hilt.
