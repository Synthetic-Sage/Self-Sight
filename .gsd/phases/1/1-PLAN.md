---
phase: 1
plan: 1
wave: 1
---

# Plan 1.1: Core Architecture & Dependencies

## Objective
Set up the fundamental Android project architecture, including Dependency Injection (Hilt), Room Database scaffold, and Navigation setup.

## Context
- .gsd/SPEC.md
- .gsd/ARCHITECTURE.md
- app/build.gradle.kts
- app/src/main/AndroidManifest.xml

## Tasks

<task type="auto">
  <name>Setup Hilt & Room Dependencies</name>
  <files>
    - build.gradle.kts
    - app/build.gradle.kts
  </files>
  <action>
    - Add Dagger Hilt plugin and dependencies.
    - Add Room runtime, ktx, and compiler dependencies.
    - Add Compose Navigation dependency.
    - Sync Gradle.
  </action>
  <verify>./gradlew app:assembleDebug</verify>
  <done>Gradle syncs successfully and the app builds with new dependencies.</done>
</task>

<task type="auto">
  <name>Initialize Hilt Application Node</name>
  <files>
    - app/src/main/java/com/diary/mirroroftruth/MirrorOfTruthApp.kt
    - app/src/main/AndroidManifest.xml
    - app/src/main/java/com/diary/mirroroftruth/MainActivity.kt
  </files>
  <action>
    - Create MirrorOfTruthApp.kt extending Application and annotate with @HiltAndroidApp.
    - Update AndroidManifest.xml to use .MirrorOfTruthApp in the <application> tag.
    - Annotate MainActivity.kt with @AndroidEntryPoint.
  </action>
  <verify>./gradlew app:assembleDebug</verify>
  <done>App compiles with Hilt annotations and custom Application class registered.</done>
</task>

## Success Criteria
- [ ] Hilt, Room, and Navigation libraries are integrated.
- [ ] Application class manages Dagger Hilt dependency graph.
