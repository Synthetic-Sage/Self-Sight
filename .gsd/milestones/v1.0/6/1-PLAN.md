---
phase: 6
plan: 1
wave: 1
---

# Plan 6.1: Settings Screen UI + material-icons-extended

## Objective
Build a Settings screen with theme display info, app version, and action rows for data export and database clearing. Also add `material-icons-extended` dependency to get proper chart icon for the Insights tab and Settings icon for the nav bar.

## Context
- app/build.gradle.kts
- app/src/main/java/com/diary/mirroroftruth/core/theme/Theme.kt
- app/src/main/java/com/diary/mirroroftruth/presentation/journal/JournalScreen.kt (pattern)

## Tasks

<task type="auto">
  <name>Add material-icons-extended dependency</name>
  <files>
    - app/build.gradle.kts
  </files>
  <action>
    - In the dependencies block, add:
        implementation("androidx.compose.material:material-icons-extended")
    - This enables Icons.Default.Timeline, Icons.Default.Settings, Icons.Default.IosShare, Icons.Default.DeleteForever etc.
    - After adding, update MainActivity.kt:
        - Replace Icons.Default.Star with Icons.Default.Timeline for the Insights tab.
        - Replace Icons.Default.Edit with Icons.Default.MenuBook for the Journal tab.
        - Add import for Icons.Default.Settings to use in a new Settings tab.
  </action>
  <verify>./gradlew app:assembleDebug</verify>
  <done>Build passes after adding material-icons-extended; new icon imports resolve without error.</done>
</task>

<task type="auto">
  <name>Create SettingsScreen</name>
  <files>
    - app/src/main/java/com/diary/mirroroftruth/presentation/settings/SettingsScreen.kt
  </files>
  <action>
    - Create @Composable fun SettingsScreen(onExportClick: () -> Unit, onClearDataClick: () -> Unit).
    - Use Scaffold with TopAppBar showing "Settings".
    - Body is a LazyColumn with distinct sections separated by HorizontalDividers:
        Section 1 — "Appearance":
            - Non-interactive row showing current theme: "Dark Mode" / NeonPurple accent (display only).
        Section 2 — "Data":
            - Clickable SettingsRow("Export Journal Data", Icons.Default.IosShare) { onExportClick() }
            - Clickable SettingsRow("Clear All Data", Icons.Default.DeleteForever, isDestructive = true) { onClearDataClick() }
        Section 3 — "About":
            - Non-interactive row: "Mirror of Truth" + "v1.0"
    - Create a private @Composable fun SettingsRow(label: String, icon: ImageVector, isDestructive: Boolean = false, onClick: () -> Unit)
        that renders a full-width clickable Row with icon + label, matching the dark glassmorphic style.
  </action>
  <verify>Get-ChildItem -Path app/src/main/java/com/diary/mirroroftruth/presentation/settings/SettingsScreen.kt</verify>
  <done>SettingsScreen.kt exists with Export, Clear Data, and About rows.</done>
</task>

## Success Criteria
- [ ] material-icons-extended added to dependencies.
- [ ] Icons updated in MainActivity (Timeline for Insights, MenuBook for Journal, Settings for Settings tab).
- [ ] SettingsScreen.kt with 3 sections created.
