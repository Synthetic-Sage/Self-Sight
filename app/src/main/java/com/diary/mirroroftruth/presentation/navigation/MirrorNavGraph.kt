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

@Composable
fun MirrorNavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        composable(route = Screen.Home.route) {
            val viewModel: HomeViewModel = hiltViewModel()
            val state by viewModel.state.collectAsState()
            
            HomeScreen(
                state = state,
                onEvent = viewModel::onEvent
            )
        }
    }
}
