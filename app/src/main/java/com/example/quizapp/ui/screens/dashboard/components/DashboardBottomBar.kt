package com.example.quizapp.ui.screens.dashboard.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AddCircleOutline
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Input
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState

sealed class BottomBarScreen(
    val route: String,
    val title: String,
    val icon: ImageVector
) {
    object Join : BottomBarScreen("join", "Join", Icons.Outlined.Input)
    object Create : BottomBarScreen("create", "Create", Icons.Outlined.AddCircleOutline)
    object MyQuizzes : BottomBarScreen("my_quizzes", "My Quizzes", Icons.Outlined.History)
    object Options : BottomBarScreen("options", "Options", Icons.Outlined.Settings)
}

@Composable
fun DashboardBottomBar(navController: NavController) {
    val screens = listOf(
        BottomBarScreen.Join,
        BottomBarScreen.Create,
        BottomBarScreen.MyQuizzes,
        BottomBarScreen.Options,
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    NavigationBar {
        screens.forEach { screen ->
            NavigationBarItem(
                label = { Text(text = screen.title) },
                icon = { Icon(imageVector = screen.icon, contentDescription = screen.title) },
                selected = currentDestination?.route == screen.route,
                onClick = {
                    navController.navigate(screen.route) {
                        popUpTo(navController.graph.startDestinationId) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }
}
