# IMPLEMENTATION PLAN & ARCHITECTURE

## 1. Tech Stack Specifications
- **Language:** Kotlin (1.9+)
- **UI Framework:** Jetpack Compose (Material 3) — Strictly NO XML layouts.
- **Architecture Pattern:** MVVM (Model-View-ViewModel) with Clean Architecture principles.
- **Local Database:** Room Database.
- **Asynchronous Programming:** Kotlin Coroutines & Kotlin Flow.
- **Dependency Injection:** Dagger Hilt.
- **Navigation:** Jetpack Compose Navigation.
- **SDK Versions:** Min SDK 28 (Android 9.0) | Target SDK 36.

## 2. Project Directory Structure
com.diary.mirroroftruth
+-- core                  # Shared utilities, theme, and constants
¦   +-- theme             # Color, Typography, Theme (Dark/Neon accents)
¦   +-- util              # Date formatters, generic extensions
+-- data                  # Data layer
¦   +-- local             # Room DB implementation
¦   ¦   +-- dao           # Data Access Objects (TaskDao, JournalDao, GoalDao)
¦   ¦   +-- entity        # Database tables
¦   ¦   +-- database      # AppDatabase setup
¦   +-- repository        # Repository implementations
+-- domain                # Business logic
¦   +-- model             # Clean data classes
¦   +-- repository        # Repository interfaces
¦   +-- usecase           # Single-action classes
+-- presentation          # UI Layer (Jetpack Compose)
¦   +-- home              # HomeScreen, HomeViewModel, components
¦   +-- journal           # JournalScreen, JournalViewModel, components
¦   +-- insights          # InsightsScreen, InsightsViewModel, charts
¦   +-- settings          # SettingsScreen, SettingsViewModel
¦   +-- navigation        # NavGraph, Screen routes
+-- MainActivity.kt       # Entry point

## 3. Database Schema (Room Entities)
**Entity 1: TaskEntity**
- id: Int (PrimaryKey, AutoGenerate)
- title: String
- date: Long (Timestamp for the day the task is assigned)
- isCompleted: Boolean
- isTomorrowTask: Boolean

**Entity 2: JournalEntryEntity**
- id: Int (PrimaryKey, AutoGenerate)
- date: Long (Timestamp - unique per day)
- moodScore: Int (1-10 scale)
- smilePrompt: String
- learnedPrompt: String
- gratefulPrompt: String

**Entity 3: GoalEntity**
- id: Int (PrimaryKey, AutoGenerate)
- title: String
- type: String ("SHORT_TERM" or "LONG_TERM")
- isCompleted: Boolean

## 4. State Management & Data Flow
- **Unidirectional Data Flow (UDF):** UI passes events to ViewModel -> UseCase -> Room. Room emits Flow -> ViewModel maps to UI State -> Compose UI.
- **UI State Classes:** Sealed class or data class for state (e.g., HomeUiState).
- **No UI-to-DB direct calls:** Composables never interact with the database directly.

## 5. UI & Theming Implementation Directives
- **Theme:** Dark theme baseline (Background colors: #0A0A0A or #121212).
- **Components:** Reusable, glassmorphism-style Compose cards.
- **Charts:** Custom Canvas for mood trend line and calendar heatmap (no third-party charting libraries for MVP).
