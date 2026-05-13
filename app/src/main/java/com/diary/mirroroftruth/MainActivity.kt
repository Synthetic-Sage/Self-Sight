package com.diary.mirroroftruth

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.fragment.app.FragmentActivity
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.diary.mirroroftruth.core.theme.MirrorOfTruthTheme
import com.diary.mirroroftruth.presentation.lock.LockScreen
import com.diary.mirroroftruth.presentation.navigation.MirrorNavGraph
import com.diary.mirroroftruth.presentation.navigation.Screen
import com.diary.mirroroftruth.presentation.settings.SettingsViewModel
import dagger.hilt.android.AndroidEntryPoint

private data class BottomNavItem(
    val label: String,
    val icon: ImageVector,
    val route: String
)

private val bottomNavItems = listOf(
    BottomNavItem("Home", Icons.Default.Home, Screen.Home.route),
    BottomNavItem("Journal", Icons.Default.MenuBook, Screen.Journal.route),
    BottomNavItem("Insights", Icons.Default.Timeline, Screen.Insights.route),
    BottomNavItem("Settings", Icons.Default.Settings, Screen.Settings.route)
)

@AndroidEntryPoint
class MainActivity : FragmentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MirrorOfTruthTheme {
                val settingsViewModel: SettingsViewModel = hiltViewModel()
                var isLocked by remember { mutableStateOf(true) }
                var needsLockCheck by remember { mutableStateOf(true) }

                // Check if PIN lock is even enabled on launch
                LaunchedEffect(Unit) {
                    if (settingsViewModel.isPinEnabled()) {
                        isLocked = true
                    } else {
                        isLocked = false
                    }
                    needsLockCheck = false
                }

                if (needsLockCheck) {
                    // Show blank background or splash-like view while checking DataStore
                    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {}
                } else if (isLocked) {
                    LockScreen(
                        viewModel = settingsViewModel,
                        onUnlocked = { isLocked = false }
                    )
                } else {
                    val navController = rememberNavController()
                    val backStackEntry by navController.currentBackStackEntryAsState()
                    val currentRoute = backStackEntry?.destination?.route

                    Scaffold(
                        modifier = Modifier.fillMaxSize(),
                        containerColor = MaterialTheme.colorScheme.background,
                        bottomBar = {
                            if (currentRoute != Screen.Splash.route) {
                                NavigationBar(
                                    containerColor = MaterialTheme.colorScheme.surface,
                                    tonalElevation = NavigationBarDefaults.Elevation
                                ) {
                                    bottomNavItems.forEach { item ->
                                        NavigationBarItem(
                                            selected = currentRoute == item.route,
                                            onClick = {
                                                navController.navigate(item.route) {
                                                    popUpTo(Screen.Home.route) { saveState = true }
                                                    launchSingleTop = true
                                                    restoreState = true
                                                }
                                            },
                                            icon = {
                                                Icon(
                                                    imageVector = item.icon,
                                                    contentDescription = item.label
                                                )
                                            },
                                            label = { Text(item.label) },
                                            colors = NavigationBarItemDefaults.colors(
                                                selectedIconColor = MaterialTheme.colorScheme.primary,
                                                selectedTextColor = MaterialTheme.colorScheme.primary,
                                                indicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                                                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        )
                                    }
                                }
                            }
                        }
                    ) { innerPadding ->
                        MirrorNavGraph(
                            navController = navController,
                            modifier = Modifier.padding(innerPadding)
                        )
                    }
                }
            }
        }
    }
}