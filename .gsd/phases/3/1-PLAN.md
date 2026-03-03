---
phase: 3
plan: 1
wave: 1
---

# Plan 3.1: Home Dashboard UI Components

## Objective
Build the presentation/UI skeletons for the Home Screen following the dark, glassmorphic aesthetic defined in the SPEC.

## Context
- .gsd/SPEC.md
- app/src/main/java/com/diary/mirroroftruth/presentation/home/
- app/src/main/java/com/diary/mirroroftruth/core/theme/Color.kt

## Tasks

<task type="auto">
  <name>Create TaskItem Component</name>
  <files>
    - app/src/main/java/com/diary/mirroroftruth/presentation/home/components/TaskItem.kt
  </files>
  <action>
    - Create a reusable `@Composable fun TaskItem(task: Task, onCheckedChange: (Boolean) -> Unit)`.
    - Apply dark theme styling: transparent background with very subtle border, glowing icon when checked.
    - Fields: Title, description (if any), and a checkbox/radio indicator.
  </action>
  <verify>Get-ChildItem -Path app/src/main/java/com/diary/mirroroftruth/presentation/home/components/TaskItem.kt</verify>
  <done>TaskItem.kt is created and compiles successfully.</done>
</task>

<task type="auto">
  <name>Create GoalCard Component</name>
  <files>
    - app/src/main/java/com/diary/mirroroftruth/presentation/home/components/GoalCard.kt
  </files>
  <action>
    - Create `@Composable fun GoalCard(goal: Goal)`.
    - Implement a glassmorphic look: Dark charcoal card with rounded corners, neon accent borders/shadows.
    - Fields: Goal title, `LinearProgressIndicator` showing progress against target, deadline formatted nicely.
  </action>
  <verify>Get-ChildItem -Path app/src/main/java/com/diary/mirroroftruth/presentation/home/components/GoalCard.kt</verify>
  <done>GoalCard.kt is created and compiles successfully.</done>
</task>

<task type="auto">
  <name>Create HomeScreen Layout</name>
  <files>
    - app/src/main/java/com/diary/mirroroftruth/presentation/home/HomeScreen.kt
  </files>
  <action>
    - Create `@Composable fun HomeScreen(state: HomeState, onEvent: (HomeEvent) -> Unit)`.
    - Layout: A `Scaffold` with a top bar showing today's date and a greeting.
    - Content: A vertically scrollable list containing a horizontally scrolling section for Goals (`GoalCard`), followed by a vertical column for Today's Tasks (`TaskItem`).
    - Note: `HomeState` and `HomeEvent` will be stubbed or created in the next plan, but lay out the architecture predicting MVI state injection.
  </action>
  <verify>Get-ChildItem -Path app/src/main/java/com/diary/mirroroftruth/presentation/home/HomeScreen.kt</verify>
  <done>HomeScreen.kt layout is defined.</done>
</task>

## Success Criteria
- [ ] TaskItem UI created.
- [ ] GoalCard UI created.
- [ ] HomeScreen aggregates them into a dashboard.
