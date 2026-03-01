# Technology Stack

> Auto-generated and refined by Implementation Plan PRD

## Runtime
| Technology | Version | Purpose |
|------------|---------|---------|
| Android SDK| 36      | Minimum API 28, Target SDK 36 |
| Kotlin     | 1.9+    | Primary language |
| Gradle     | 8.x     | Build tool     |

## Dependencies

### Production
| Package | Version | Purpose |
|---------|---------|---------|
| androidx.core.ktx | libs.versions.core | Kotlin extensions |
| androidx.lifecycle.runtime.ktx | libs.versions.lifecycle | Lifecycle management |
| androidx.activity.compose | libs.versions.compose | Compose integration |
| androidx.compose.ui | BOM | UI framework |
| androidx.compose.material3 | BOM | Material Design 3 |
| androidx.room:room-runtime | TBD | Room Local Database |
| androidx.room:room-ktx | TBD | Room Coroutines support |
| com.google.dagger:hilt-android | TBD | Dependency Injection |
| androidx.navigation:navigation-compose | TBD | Compose Navigation |

### Development
| Package | Version | Purpose |
|---------|---------|---------|
| junit | libs.versions.junit | Unit testing |
| androidx.junit | libs.versions.androidx_junit | Android testing |
| androidx.espresso.core | libs.versions.espresso | UI testing |
| androidx.compose.ui.tooling | BOM | Compose previews |
| androidx.compose.ui.test.manifest | BOM | Compose testing |
| androidx.room:room-compiler | TBD | Room Compiler |
| com.google.dagger:hilt-compiler | TBD | Hilt Compiler |
