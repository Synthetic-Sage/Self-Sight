---
phase: 3
plan: 3
wave: 2
---

# Plan 3.3: Dashboard Navigation & MainActivity Integration

## Objective
Set up Jetpack Compose Navigation to route to the new HomeScreen and wire it into the `MainActivity` entry point.

## Context
- .gsd/ARCHITECTURE.md
- app/src/main/java/com/diary/mirroroftruth/MainActivity.kt
- app/src/main/java/com/diary/mirroroftruth/presentation/navigation/

## Tasks

<task type="auto">
  <name>Create Navigation Graph</name>
  <files>
    - app/src/main/java/com/diary/mirroroftruth/presentation/navigation/Screen.kt
    - app/src/main/java/com/diary/mirroroftruth/presentation/navigation/MirrorNavGraph.kt
  </files>
  <action>
    - Create a `sealed class Screen(val route: String)` with `object Home : Screen("home")`.
    - Create `@Composable fun MirrorNavGraph(navController: NavHostController)`.
    - Set up the `NavHost` starting at `Screen.Home.route`.
    - Add a `composable(route = Screen.Home.route)` that instantiates `val viewModel: HomeViewModel = hiltViewModel()` and passes its state/events to `HomeScreen`.
  </action>
  <verify>Get-ChildItem -Path app/src/main/java/com/diary/mirroroftruth/presentation/navigation/MirrorNavGraph.kt</verify>
  <done>NavGraph orchestrates the Compose screens.</done>
</task>

<task type="auto">
  <name>Integrate with MainActivity</name>
  <files>
    - app/src/main/java/com/diary/mirroroftruth/MainActivity.kt
  </files>
  <action>
    - Modify `MainActivity.onCreate` to utilize `MirrorOfTruthTheme`.
    - Place the `MirrorNavGraph(navController = rememberNavController())` inside a standard Surface container taking up the full screen.
  </action>
  <verify>./gradlew app:assembleDebug</verify>
  <done>Application boots and renders the Home tracking screen flawlessly.</done>
</task>

## Success Criteria
- [ ] `Screen.kt` and `MirrorNavGraph.kt` are correctly defining routes.
- [ ] `MainActivity` sets the navigation entry point.
- [ ] Project compiles seamlessly resulting in a testable App UI.
