---
phase: 6
plan: 3
wave: 2
---

# Plan 6.3: Navigation (Settings Tab) + ROADMAP Update

## Objective
Add the Settings screen as a 4th tab in the bottom navigation bar, wire it to the NavGraph, and mark the ROADMAP.md as v1.0 complete.

## Context
- app/src/main/java/com/diary/mirroroftruth/presentation/navigation/Screen.kt
- app/src/main/java/com/diary/mirroroftruth/presentation/navigation/MirrorNavGraph.kt
- app/src/main/java/com/diary/mirroroftruth/MainActivity.kt
- .gsd/ROADMAP.md

## Tasks

<task type="auto">
  <name>Add Settings Route to NavGraph</name>
  <files>
    - app/src/main/java/com/diary/mirroroftruth/presentation/navigation/Screen.kt
    - app/src/main/java/com/diary/mirroroftruth/presentation/navigation/MirrorNavGraph.kt
  </files>
  <action>
    - In Screen.kt add: object Settings : Screen("settings")
    - In MirrorNavGraph.kt add composable block for Screen.Settings.route:
        val viewModel: SettingsViewModel = hiltViewModel()
        val state by viewModel.state.collectAsState()
        SettingsScreen(state = state, onEvent = viewModel::onEvent)
  </action>
  <verify>Get-Content app/src/main/java/com/diary/mirroroftruth/presentation/navigation/Screen.kt</verify>
  <done>Settings route defined and wired in NavGraph.</done>
</task>

<task type="auto">
  <name>Add Settings Tab to Bottom Navigation Bar</name>
  <files>
    - app/src/main/java/com/diary/mirroroftruth/MainActivity.kt
  </files>
  <action>
    - Add a 4th entry to bottomNavItems:
        BottomNavItem("Settings", Icons.Default.Settings, Screen.Settings.route)
    - Icons.Default.Settings is available in material-icons-core (no extended needed).
    - The full list is now: Home (Home), Journal (MenuBook), Insights (Timeline), Settings (Settings).
  </action>
  <verify>./gradlew app:assembleDebug</verify>
  <done>4-tab bottom navigation bar compiled and assembleDebug succeeds.</done>
</task>

<task type="auto">
  <name>Update ROADMAP.md to v1.0 Complete</name>
  <files>
    - .gsd/ROADMAP.md
  </files>
  <action>
    - Update Phase 4, 5, and 6 status fields to "Complete".
    - Update the Current Phase header to "v1.0 MVP Complete".
  </action>
  <verify>Get-Content .gsd/ROADMAP.md | Select-String "Phase 6"</verify>
  <done>ROADMAP.md shows Phase 6 as Complete and milestone as v1.0 MVP Complete.</done>
</task>

## Success Criteria
- [ ] Settings route in Screen.kt and MirrorNavGraph.
- [ ] 4-tab bottom nav: Home, Journal, Insights, Settings.
- [ ] assembleDebug PASS.
- [ ] ROADMAP.md reflects v1.0 completion.
