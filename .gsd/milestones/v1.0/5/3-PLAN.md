---
phase: 5
plan: 3
wave: 2
---

# Plan 5.3: Insights Navigation Tab

## Objective
Add the Insights screen as a third tab in the bottom navigation bar, connecting it to the NavGraph with its ViewModel.

## Context
- .gsd/ARCHITECTURE.md
- app/src/main/java/com/diary/mirroroftruth/presentation/navigation/Screen.kt
- app/src/main/java/com/diary/mirroroftruth/presentation/navigation/MirrorNavGraph.kt
- app/src/main/java/com/diary/mirroroftruth/MainActivity.kt

## Tasks

<task type="auto">
  <name>Add Insights Route and Update NavGraph</name>
  <files>
    - app/src/main/java/com/diary/mirroroftruth/presentation/navigation/Screen.kt
    - app/src/main/java/com/diary/mirroroftruth/presentation/navigation/MirrorNavGraph.kt
  </files>
  <action>
    - In Screen.kt add: object Insights : Screen("insights")
    - In MirrorNavGraph.kt add a composable block for Screen.Insights.route:
        val viewModel: InsightsViewModel = hiltViewModel()
        val state by viewModel.state.collectAsState()
        InsightsScreen(state = state, onEvent = viewModel::onEvent)
  </action>
  <verify>Get-Content app/src/main/java/com/diary/mirroroftruth/presentation/navigation/Screen.kt</verify>
  <done>Insights route defined and wired in NavGraph.</done>
</task>

<task type="auto">
  <name>Add Insights Tab to Bottom Navigation Bar</name>
  <files>
    - app/src/main/java/com/diary/mirroroftruth/MainActivity.kt
  </files>
  <action>
    - Import Icons.Default.BarChart (which is available in the standard material-icons set, unlike Book).
    - Add a third entry to the bottomNavItems list:
        BottomNavItem("Insights", Icons.Default.BarChart, Screen.Insights.route)
    - The rest of the NavigationBar loop is unchanged — it already iterates over bottomNavItems.
  </action>
  <verify>./gradlew app:assembleDebug</verify>
  <done>App compiles cleanly with a 3-tab bottom navigation bar; Insights tab navigates to InsightsScreen.</done>
</task>

## Success Criteria
- [ ] Insights route registered in Screen.kt and MirrorNavGraph.kt.
- [ ] Bottom nav has 3 tabs: Home, Journal, Insights.
- [ ] assembleDebug succeeds with zero errors.
