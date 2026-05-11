package com.diary.mirroroftruth.presentation.navigation

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Home : Screen("home")
    object Journal : Screen("journal")
    object Insights : Screen("insights")
    object Settings : Screen("settings")
}
