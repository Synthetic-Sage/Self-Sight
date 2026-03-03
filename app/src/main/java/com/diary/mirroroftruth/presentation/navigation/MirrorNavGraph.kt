package com.diary.mirroroftruth.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.diary.mirroroftruth.presentation.home.HomeScreen
import com.diary.mirroroftruth.presentation.home.HomeViewModel
import com.diary.mirroroftruth.presentation.journal.JournalScreen
import com.diary.mirroroftruth.presentation.journal.JournalViewModel
import com.diary.mirroroftruth.presentation.insights.InsightsScreen
import com.diary.mirroroftruth.presentation.insights.InsightsViewModel

import androidx.compose.ui.Modifier

@Composable
fun MirrorNavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route,
        modifier = modifier
    ) {
        composable(route = Screen.Home.route) {
            val viewModel: HomeViewModel = hiltViewModel()
            val state by viewModel.state.collectAsState()
            HomeScreen(
                state = state,
                onEvent = viewModel::onEvent
            )
        }

        composable(route = Screen.Journal.route) {
            val viewModel: JournalViewModel = hiltViewModel()
            val state by viewModel.state.collectAsState()
            JournalScreen(
                state = state,
                onEvent = viewModel::onEvent
            )
        }

        composable(route = Screen.Insights.route) {
            val viewModel: InsightsViewModel = hiltViewModel()
            val state by viewModel.state.collectAsState()
            InsightsScreen(
                state = state,
                onEvent = viewModel::onEvent
            )
        }
    }
}
