package com.diary.mirroroftruth.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.diary.mirroroftruth.presentation.home.HomeScreen
import com.diary.mirroroftruth.presentation.home.HomeViewModel
import com.diary.mirroroftruth.presentation.insights.InsightsScreen
import com.diary.mirroroftruth.presentation.insights.InsightsViewModel
import com.diary.mirroroftruth.presentation.journal.JournalScreen
import com.diary.mirroroftruth.presentation.journal.JournalViewModel
import com.diary.mirroroftruth.presentation.settings.SettingsScreen
import com.diary.mirroroftruth.presentation.settings.SettingsViewModel

import com.diary.mirroroftruth.presentation.splash.SplashScreen

@Composable
fun MirrorNavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route,
        modifier = modifier
    ) {
        composable(route = Screen.Splash.route) {
            SplashScreen(
                onSplashFinished = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            )
        }

        composable(route = Screen.Home.route) {
            val viewModel: HomeViewModel = hiltViewModel()
            val state by viewModel.state.collectAsState()
            HomeScreen(state = state, onEvent = viewModel::onEvent)
        }

        composable(
            route = Screen.Journal.route,
            deepLinks = listOf(androidx.navigation.navDeepLink { uriPattern = "mirror://journal" })
        ) {
            val viewModel: JournalViewModel = hiltViewModel()
            val state by viewModel.state.collectAsState()
            // Provide live settings so font-size / prompt visibility is respected
            val settingsViewModel: SettingsViewModel = hiltViewModel(
                viewModelStoreOwner = it  // scope to this destination
            )
            val settingsState by settingsViewModel.state.collectAsState()
            JournalScreen(
                state = state,
                onEvent = viewModel::onEvent,
                largeFontEnabled = settingsState.largeFontEnabled,
                journalPrompts = settingsState.journalPrompts,
                showWentWell = settingsState.showWentWellPrompt,
                showToImprove = settingsState.showToImprovePrompt,
                showLearning = settingsState.showLearningPrompt
            )
        }

        composable(route = Screen.Insights.route) {
            val viewModel: InsightsViewModel = hiltViewModel()
            val state by viewModel.state.collectAsState()
            InsightsScreen(state = state, onEvent = viewModel::onEvent)
        }

        composable(route = Screen.Settings.route) {
            val viewModel: SettingsViewModel = hiltViewModel()
            val state by viewModel.state.collectAsState()
            SettingsScreen(state = state, onEvent = viewModel::onEvent)
        }
    }
}
