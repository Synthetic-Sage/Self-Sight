# SPEC.md — Project Specification

> **Status**: FINALIZED

## Vision
A structured daily reflection and productivity journal designed to help users track tasks, analyze mood patterns, and maintain long-term goals without feeling overwhelmed, in a distraction-free, privacy-first, and aesthetically dark and minimalist environment.

## Goals
1. Provide a quick dashboard for daily tasks and overarching goals.
2. Enable low-friction, structured daily journaling with predefined prompts.
3. Visualize consistency, mood trends, and journaling activity.
4. Offer data control through local backups, exports, and theme customization.

## Non-Goals (Out of Scope)
- No Cloud Authentication (No Firebase, Google Sign-in, or user accounts)
- No Cloud Syncing (No remote databases, strictly local)
- No AI Generation/Chatbots (Static tool for the user's mind)
- No Social Features (No sharing, no friends lists)

## Users
Students, self-improvement enthusiasts, and individuals prioritizing mental organization in a private, distraction-free app.

## Constraints
- **Platform:** Native Android (Kotlin, Jetpack Compose)
- **Architecture:** Local-first, MVP-first approach
- **Aesthetic:** Dark, minimalist, high-tech (charcoal/true black #0A0A0A to #121212, glowing gradients, glassmorphism, sans-serif)

## Success Criteria
- [ ] Working Home dashboard with task lists and goal tracking
- [ ] Functional Journal screen with reflection prompts and tomorrow's task input
- [ ] Insights screen displaying a calendar heatmap, stats, and trend charts
- [ ] Settings screen with theme toggles, gradient pickers, and local export/backup functionality
