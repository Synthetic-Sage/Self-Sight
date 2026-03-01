---
phase: 1
plan: 2
wave: 1
---

# Plan 1.2: Core Component Folders & Theme Scaffold

## Objective
Set up the required Clean Architecture package structure and apply the dark, glassmorphism theme specified in the requirements.

## Context
- .gsd/ARCHITECTURE.md
- app/src/main/java/com/diary/mirroroftruth/ui/theme/Theme.kt
- app/src/main/java/com/diary/mirroroftruth/MainActivity.kt

## Tasks

<task type="auto">
  <name>Create Package Structure</name>
  <files>
    - app/src/main/java/com/diary/mirroroftruth/core/theme
    - app/src/main/java/com/diary/mirroroftruth/core/util
    - app/src/main/java/com/diary/mirroroftruth/data/local/dao
    - app/src/main/java/com/diary/mirroroftruth/data/local/entity
    - app/src/main/java/com/diary/mirroroftruth/data/local/database
    - app/src/main/java/com/diary/mirroroftruth/data/repository
    - app/src/main/java/com/diary/mirroroftruth/domain/model
    - app/src/main/java/com/diary/mirroroftruth/domain/repository
    - app/src/main/java/com/diary/mirroroftruth/domain/usecase
    - app/src/main/java/com/diary/mirroroftruth/presentation/home
    - app/src/main/java/com/diary/mirroroftruth/presentation/journal
    - app/src/main/java/com/diary/mirroroftruth/presentation/insights
    - app/src/main/java/com/diary/mirroroftruth/presentation/settings
    - app/src/main/java/com/diary/mirroroftruth/presentation/navigation
  </files>
  <action>
    - Create the required directory structure under the main package.
    - Move Theme.kt, Color.kt, and Type.kt into core/theme (if they exist in the default ui.theme location).
    - Delete the ui.theme folder if it becomes empty.
    - Update package declarations in moved files.
  </action>
  <verify>Get-ChildItem -Path app/src/main/java/com/diary/mirroroftruth/ -Recurse -Directory</verify>
  <done>All required packages are present and the project still compiles.</done>
</task>

<task type="auto">
  <name>Configure Core Theme</name>
  <files>
    - app/src/main/java/com/diary/mirroroftruth/core/theme/Color.kt
    - app/src/main/java/com/diary/mirroroftruth/core/theme/Theme.kt
  </files>
  <action>
    - Update Color.kt to include the dark/neon accent colors (#0A0A0A background).
    - Update Theme.kt to enforce the dark theme (darkColorScheme).
  </action>
  <verify>./gradlew app:assembleDebug</verify>
  <done>Theme configuration is updated to the spec's dark aesthetic.</done>
</task>

## Success Criteria
- [ ] Clean Architecture folder structure is visible in the project.
- [ ] Base theme colors and configurations are set.
