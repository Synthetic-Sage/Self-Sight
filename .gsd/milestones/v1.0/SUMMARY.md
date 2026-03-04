# Milestone: v1.0 MVP

## Completed: 2026-03-04

## Deliverables
- ✅ Local-only data storage (Room + Hilt)
- ✅ Dark, minimalist, glassmorphic UI (NeonPurple/NeonBlue palette)
- ✅ Home Dashboard (daily tasks + goal cards + progress)
- ✅ Journaling Flow (mood selector, 3 reflection prompts, tomorrow's intentions)
- ✅ Insights & Tracking (calendar heatmap, mood distribution, streak counter)
- ✅ Settings & Data Export (CSV via Android SAF, Clear Data dialog)

## Phases Completed
1. **Phase 1: Foundation & Architecture** — Project setup, Clean Architecture, Hilt DI, Room, Navigation
2. **Phase 2: Core Data Models & Repository** — Entities, DAOs, Mappers, Repository implementations
3. **Phase 3: Dashboard & Goals UI** — HomeScreen, GoalCard, TaskItem, HomeViewModel
4. **Phase 4: Journaling Flow** — JournalScreen, MoodSelector, JournalPromptField, JournalViewModel
5. **Phase 5: Insights & Visualization** — InsightsScreen, JournalHeatmap, MoodSummaryCard, InsightsViewModel
6. **Phase 6: Settings, Backup & Polish** — SettingsScreen, SettingsViewModel (CSV export), 4-tab nav, proper icons

## Metrics
- **Total commits**: 33
- **Architecture**: Clean Architecture (4-layer: Presentation / Domain / Data / DI)
- **Navigation**: 4-tab Bottom Navigation Bar (Home · Journal · Insights · Settings)
- **State management**: MVI-lite (XxxState + XxxEvent + @HiltViewModel) for all screens
- **Data**: Room + Kotlin Flow — fully local, no network dependencies

## Key Technical Decisions
- **No external chart library** — JournalHeatmap built in pure Compose (LazyColumn + Box grid)
- **SAF for export** — No WRITE_EXTERNAL_STORAGE permission needed; system file picker handles location
- **material-icons-extended** added for proper icons (MenuBook / Timeline / Settings)
- **Repository method naming** — Discovered mismatch between plan names and actual interface: `insertJournalEntry` / `getJournalEntryForDate` (not `insertEntry`/`getEntryForDate`)
- **Icon availability** — `BarChart`, `ShowChart` not in material-icons-core; solved by adding material-icons-extended

## Lessons Learned
- Always verify icon names against the actual dependency version before planning — material-icons-core has a subset only
- Fresh context sessions after long executions significantly improved debugging speed
- Pipe-delimited storage for multi-field prompt responses is effective for Phase 4 data; consider migrating to separate columns if extending
