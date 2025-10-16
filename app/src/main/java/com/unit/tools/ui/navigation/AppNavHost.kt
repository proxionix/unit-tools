package com.unit.tools.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.unit.tools.ui.components.BottomBar
import com.unit.tools.ui.components.HomeTopBarWithLogo
import com.unit.tools.ui.components.SettingsTopBarTitle
import com.unit.tools.ui.screens.HomeScreen
import com.unit.tools.ui.screens.SettingsScreen

@Composable
fun AppNavHost() {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()

    // IMPORTANT: ne pas lire navController.graph ici; fallback statique sur HOME
    val currentRoute = backStackEntry?.destination?.route ?: Routes.HOME

    Scaffold(
        contentWindowInsets = WindowInsets.safeDrawing,
        topBar = {
            when (currentRoute) {
                Routes.HOME -> HomeTopBarWithLogo()
                Routes.SETTINGS -> SettingsTopBarTitle()
                else -> {}
            }
        },
        bottomBar = {
            BottomBar(
                currentRoute = currentRoute,
                onNavigate = { route ->
                    if (route == currentRoute) return@BottomBar
                    navigateSingleTopPreserveState(navController, route)
                }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .consumeWindowInsets(innerPadding)
        ) {
            NavHost(
                navController = navController,
                startDestination = Routes.HOME
            ) {
                composable(Routes.HOME) { HomeScreen() }
                composable(Routes.SETTINGS) { SettingsScreen() }
            }
        }
    }
}

// Helper qui n’accède pas à navController.graph
private fun navigateSingleTopPreserveState(nav: NavController, route: String) {
    nav.navigate(route) {
        // Utiliser route de départ symbolique, pas graph.startDestinationId
        popUpTo(nav.graph.startDestinationRoute ?: Routes.HOME) {
            saveState = true
            inclusive = false
        }
        launchSingleTop = true
        restoreState = true
    }
}
