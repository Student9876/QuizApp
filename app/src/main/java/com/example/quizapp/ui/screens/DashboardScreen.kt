package com.example.quizapp.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.quizapp.ui.screens.dashboard.CreateQuizScreen
import com.example.quizapp.ui.screens.dashboard.OptionsScreen
import com.example.quizapp.ui.screens.dashboard.ResultsScreen
import com.example.quizapp.ui.screens.dashboard.components.DashboardBottomBar

@Composable
fun DashboardScreen(
    onLogoutClicked: () -> Unit
) {
    // This is a new, nested NavController just for the dashboard screens
    val dashboardNavController = rememberNavController()

    Scaffold(
        bottomBar = {
            DashboardBottomBar(navController = dashboardNavController)
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            // The nested NavHost will display the content for the selected bottom bar item
            NavHost(
                navController = dashboardNavController,
                startDestination = "create" // The default screen is "Create"
            ) {
                composable("create") { CreateQuizScreen() }
                composable("results") { ResultsScreen() }
                composable("options") { OptionsScreen(onLogoutClicked = onLogoutClicked) }
            }
        }
    }
}