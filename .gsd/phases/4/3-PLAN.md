---
phase: 4
plan: 3
wave: 2
---

# Plan 4.3: Journal Navigation Route & Bottom Navigation Bar

## Objective
Add a Journal route to the navigation graph and introduce a bottom navigation bar so users can switch between the Home and Journal screens.

## Context
- .gsd/ARCHITECTURE.md
- app/src/main/java/com/diary/mirroroftruth/presentation/navigation/Screen.kt
- app/src/main/java/com/diary/mirroroftruth/presentation/navigation/MirrorNavGraph.kt
- app/src/main/java/com/diary/mirroroftruth/MainActivity.kt

## Tasks

<task type="auto">
  <name>Add Journal Route to Screen.kt and MirrorNavGraph.kt</name>
  <files>
    - app/src/main/java/com/diary/mirroroftruth/presentation/navigation/Screen.kt
    - app/src/main/java/com/diary/mirroroftruth/presentation/navigation/MirrorNavGraph.kt
  </files>
  <action>
    - In Screen.kt add: object Journal : Screen("journal").
    - In MirrorNavGraph.kt add a composable block for Screen.Journal.route.
      Inside it: instantiate JournalViewModel via hiltViewModel(), collect state, and pass to JournalScreen(state, onEvent).
  </action>
  <verify>Get-Content -Path app/src/main/java/com/diary/mirroroftruth/presentation/navigation/Screen.kt</verify>
  <done>Journal route is defined and wired to JournalScreen in NavGraph.</done>
</task>

<task type="auto">
  <name>Add Bottom Navigation Bar to MainActivity</name>
  <files>
    - app/src/main/java/com/diary/mirroroftruth/MainActivity.kt
  </files>
  <action>
    - Wrap MirrorNavGraph inside a Scaffold that has a bottomBar.
    - The bottomBar is a NavigationBar with two items: Home (Icons.Default.Home) and Journal (Icons.Default.Book).
    - Track selected item using navController.currentBackStackEntryAsState().
    - On item click, navigate to the corresponding route using navController.navigate(route) { popUpTo(Screen.Home.route) { saveState = true }; launchSingleTop = true; restoreState = true }.
    - Move the navController creation up to the Scaffold level so both MirrorNavGraph and the bottom bar share it.
  </action>
  <verify>./gradlew app:assembleDebug</verify>
  <done>App compiles with a bottom navigation bar enabling Home/Journal switching.</done>
</task>

## Success Criteria
- [ ] Journal route registered in Screen.kt and MirrorNavGraph.kt.
- [ ] Bottom navigation bar visible with Home and Journal tabs.
- [ ] assembleDebug succeeds with zero errors.
