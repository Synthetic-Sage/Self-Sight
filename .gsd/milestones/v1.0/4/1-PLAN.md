---
phase: 4
plan: 1
wave: 1
---

# Plan 4.1: Journal Screen UI Components

## Objective
Build the Journal entry screen composable with a mood selector, structured reflection prompts, and a "tomorrow's tasks" input section — following the dark glassmorphic aesthetics.

## Context
- .gsd/SPEC.md
- .gsd/ARCHITECTURE.md
- app/src/main/java/com/diary/mirroroftruth/domain/model/JournalEntry.kt
- app/src/main/java/com/diary/mirroroftruth/core/theme/Color.kt
- app/src/main/java/com/diary/mirroroftruth/presentation/home/HomeScreen.kt (pattern reference)

## Tasks

<task type="auto">
  <name>Create MoodSelector Component</name>
  <files>
    - app/src/main/java/com/diary/mirroroftruth/presentation/journal/components/MoodSelector.kt
  </files>
  <action>
    - Create @Composable fun MoodSelector(selectedMood: String, onMoodSelected: (String) -> Unit).
    - Moods: ["😔 Low", "😕 Meh", "😐 Okay", "🙂 Good", "😄 Great"].
    - Render as a Row of selectable chips (FilterChip or custom styled Row items).
    - Selected chip uses MaterialTheme.colorScheme.primary background (neon glow accent).
    - Unselected chip uses surfaceVariant background.
  </action>
  <verify>Get-ChildItem -Path app/src/main/java/com/diary/mirroroftruth/presentation/journal/components/MoodSelector.kt</verify>
  <done>MoodSelector.kt exists with 5 mood options and correct selection callback.</done>
</task>

<task type="auto">
  <name>Create JournalPromptField Component</name>
  <files>
    - app/src/main/java/com/diary/mirroroftruth/presentation/journal/components/JournalPromptField.kt
  </files>
  <action>
    - Create @Composable fun JournalPromptField(prompt: String, value: String, onValueChange: (String) -> Unit).
    - Renders a visible prompt label (e.g., "What went well today?") styled with secondary color.
    - Below it, a multi-line OutlinedTextField with transparent background, rounded 12dp corners, and max 5 lines visible.
    - Placeholder text should be "Write your thoughts...".
  </action>
  <verify>Get-ChildItem -Path app/src/main/java/com/diary/mirroroftruth/presentation/journal/components/JournalPromptField.kt</verify>
  <done>JournalPromptField.kt exists with prompt label and a text field.</done>
</task>

<task type="auto">
  <name>Create JournalScreen Layout</name>
  <files>
    - app/src/main/java/com/diary/mirroroftruth/presentation/journal/JournalScreen.kt
  </files>
  <action>
    - Create @Composable fun JournalScreen(state: JournalState, onEvent: (JournalEvent) -> Unit).
    - Use Scaffold with a TopAppBar showing "Today's Reflection" and today's formatted date.
    - Body is a LazyColumn containing:
      1. MoodSelector bound to state.selectedMood.
      2. Three JournalPromptFields with prompts:
         - "What went well today?"
         - "What challenged you?"
         - "What are you grateful for?"
      3. A divider with heading "Tomorrow's Intentions".
      4. A single OutlinedTextField for tomorrows tasks (state.tomorrowsTask).
      5. A "Save Entry" ElevatedButton at the bottom that fires JournalEvent.OnSaveEntry.
    - JournalState and JournalEvent will be defined in Plan 4.2; use stubs if needed.
  </action>
  <verify>Get-ChildItem -Path app/src/main/java/com/diary/mirroroftruth/presentation/journal/JournalScreen.kt</verify>
  <done>JournalScreen.kt layout fully defined with all sections.</done>
</task>

## Success Criteria
- [ ] MoodSelector component created.
- [ ] JournalPromptField component created.
- [ ] JournalScreen aggregates all sections into a scrollable layout.
