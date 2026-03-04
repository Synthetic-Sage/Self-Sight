---
phase: 5
plan: 1
wave: 1
---

# Plan 5.1: Insights Screen UI Components

## Objective
Build the Insights screen with a custom calendar heatmap (Compose Canvas), summary stat cards, and a mood distribution bar — all using the existing dark glassmorphic theme. No external chart libraries added.

## Context
- .gsd/SPEC.md
- .gsd/ARCHITECTURE.md
- app/src/main/java/com/diary/mirroroftruth/domain/model/JournalEntry.kt
- app/src/main/java/com/diary/mirroroftruth/core/theme/Color.kt
- app/src/main/java/com/diary/mirroroftruth/presentation/journal/JournalScreen.kt (pattern reference)

## Tasks

<task type="auto">
  <name>Create JournalHeatmap Component</name>
  <files>
    - app/src/main/java/com/diary/mirroroftruth/presentation/insights/components/JournalHeatmap.kt
  </files>
  <action>
    - Create @Composable fun JournalHeatmap(entriesByDay: Map<Int, Boolean>, modifier: Modifier = Modifier).
    - entriesByDay is a Map of day-of-month (1..31) to hasEntry (true/false).
    - Render a 7-column grid (using LazyVerticalGrid or a manual Row/Column layout).
    - Each day cell is a small Square Box (24.dp x 24.dp, 4.dp rounded corner):
        - hasEntry=true: filled with MaterialTheme.colorScheme.primary (neon glow).
        - hasEntry=false: filled with MaterialTheme.colorScheme.surfaceVariant.
    - Show a month label (e.g., "March 2026") above the grid.
    - Show abbreviated weekday headers (M T W T F S S) above the day cells.
  </action>
  <verify>Get-ChildItem -Path app/src/main/java/com/diary/mirroroftruth/presentation/insights/components/JournalHeatmap.kt</verify>
  <done>JournalHeatmap.kt exists with 7-column grid rendering colored day cells.</done>
</task>

<task type="auto">
  <name>Create MoodSummaryCard Component</name>
  <files>
    - app/src/main/java/com/diary/mirroroftruth/presentation/insights/components/MoodSummaryCard.kt
  </files>
  <action>
    - Create @Composable fun MoodSummaryCard(moodCounts: Map<String, Int>, modifier: Modifier = Modifier).
    - moodCounts maps mood label (e.g. "😄 Great") to count.
    - Render as a Card with glassmorphic style (surfaceVariant, 12dp corner radius).
    - For each mood, show a Row: emoji + label, then a LinearProgressIndicator (progress = count / maxCount), then count number.
    - Sort moods in ascending order: Low -> Meh -> Okay -> Good -> Great.
  </action>
  <verify>Get-ChildItem -Path app/src/main/java/com/diary/mirroroftruth/presentation/insights/components/MoodSummaryCard.kt</verify>
  <done>MoodSummaryCard.kt exists with progress bars for each mood.</done>
</task>

<task type="auto">
  <name>Create InsightsScreen Layout</name>
  <files>
    - app/src/main/java/com/diary/mirroroftruth/presentation/insights/InsightsScreen.kt
  </files>
  <action>
    - Create @Composable fun InsightsScreen(state: InsightsState, onEvent: (InsightsEvent) -> Unit).
    - Use Scaffold with TopAppBar showing "Your Insights".
    - Body is a LazyColumn with:
        1. A stats Row: two ElevatedCard items showing "Total Entries" and "Current Streak (days)".
        2. JournalHeatmap bound to state.entriesByDay.
        3. A heading "Mood Distribution" and then MoodSummaryCard bound to state.moodCounts.
    - InsightsState and InsightsEvent stubs (will be fully defined in Plan 5.2).
  </action>
  <verify>Get-ChildItem -Path app/src/main/java/com/diary/mirroroftruth/presentation/insights/InsightsScreen.kt</verify>
  <done>InsightsScreen.kt exists with heatmap and mood summary sections.</done>
</task>

## Success Criteria
- [ ] JournalHeatmap component renders a day-of-month grid.
- [ ] MoodSummaryCard displays mood counts with progress bars.
- [ ] InsightsScreen aggregates all components in a LazyColumn.
