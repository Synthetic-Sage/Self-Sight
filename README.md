# Mirror of Truth (Codename)

An offline-first, private-by-design journaling and goal-tracking Android application built with Kotlin and Jetpack Compose. 

> *Note: "Mirror of Truth" is a codename. Feel free to rename this project when compiling for your own use.*

## Features
- **Offline First**: All your data is stored locally using Room Database. Your deeply personal diary never leaves your device unless you choose to export it.
- **Journaling**: Daily reflections, accomplishments, and areas of improvement.
- **Goal Tracking**: Break your life into "Big Steps" and "Small Steps". Track progress with engaging UI and celebratory animations.
- **Daily Fortune Cookie**: Crack open a daily motivational quote to start your day right.
- **Privacy First**: Secure, encrypted JSON backup import/export functionality to keep your data yours.
- **Push Notifications**: Set a daily reminder at your preferred time to write your entry.

## Tech Stack
- **Language**: Kotlin
- **UI Toolkit**: Jetpack Compose (Material 3)
- **Architecture**: MVVM + Clean Architecture
- **Dependency Injection**: Dagger Hilt
- **Local Storage**: Room Database
- **Background Processing**: WorkManager (for daily reminders)

## Building the Project
1. Clone the repository: `git clone https://github.com/yourusername/mirror-of-truth.git`
2. Open the project in Android Studio.
3. Sync Gradle and run on an emulator or physical device.

## License
This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.
