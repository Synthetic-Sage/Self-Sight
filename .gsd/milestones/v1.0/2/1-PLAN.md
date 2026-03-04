---
phase: 2
plan: 1
wave: 1
---

# Plan 2.1: Core Data Entities

## Objective
Define the local database schemas for Tasks, Goals, and Journal Entries using Room `@Entity` annotations.

## Context
- .gsd/SPEC.md
- app/src/main/java/com/diary/mirroroftruth/data/local/entity (Package created in Phase 1)

## Tasks

<task type="auto">
  <name>Create Task and Goal Entities</name>
  <files>
    - app/src/main/java/com/diary/mirroroftruth/data/local/entity/TaskEntity.kt
    - app/src/main/java/com/diary/mirroroftruth/data/local/entity/GoalEntity.kt
  </files>
  <action>
    - Create `TaskEntity` data class annotated with `@Entity(tableName = "tasks")`. Fields: `id` (PrimaryKey autoGenerate), `title` (String), `description` (String?), `isCompleted` (Boolean), `createdAt` (Long), `dueDate` (Long?).
    - Create `GoalEntity` data class annotated with `@Entity(tableName = "goals")`. Fields: `id` (PrimaryKey autoGenerate), `title` (String), `description` (String?), `progress` (Float), `target` (Float), `createdAt` (Long), `deadline` (Long?).
  </action>
  <verify>Get-ChildItem -Path app/src/main/java/com/diary/mirroroftruth/data/local/entity/ -Filter *Entity.kt</verify>
  <done>TaskEntity and GoalEntity exist with correct Room annotations and fields.</done>
</task>

<task type="auto">
  <name>Create Journal Entry Entity</name>
  <files>
    - app/src/main/java/com/diary/mirroroftruth/data/local/entity/JournalEntryEntity.kt
  </files>
  <action>
    - Create `JournalEntryEntity` data class annotated with `@Entity(tableName = "journal_entries")`.
    - Fields: `id` (PrimaryKey autoGenerate), `date` (Long - start of day timestamp), `mood` (String), `content` (String), `promptResponses` (String - JSON serialized).
  </action>
  <verify>Get-ChildItem -Path app/src/main/java/com/diary/mirroroftruth/data/local/entity/JournalEntryEntity.kt</verify>
  <done>JournalEntryEntity exists with correct fields representing daily reflections.</done>
</task>

## Success Criteria
- [ ] `TaskEntity.kt` matches the schema.
- [ ] `GoalEntity.kt` matches the schema.
- [ ] `JournalEntryEntity.kt` matches the schema.
