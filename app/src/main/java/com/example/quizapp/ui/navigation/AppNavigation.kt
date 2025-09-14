package com.example.quizapp.ui.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.unit.IntOffset
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.quizapp.ui.auth.AuthViewModel
import com.example.quizapp.ui.screens.DashboardScreen
import com.example.quizapp.ui.screens.LandingScreen
import com.example.quizapp.ui.screens.LoginScreen
import com.example.quizapp.ui.screens.SignUpScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val authViewModel: AuthViewModel = viewModel()
    val authState by authViewModel.uiState.collectAsState()

    // Determine the starting screen based on the initial auth state
    val startDestination = if (authState.isAuthenticated) "dashboard" else "landing"

    // This effect handles navigation when the auth state changes
    LaunchedEffect(authState.isAuthenticated) {
        if (authState.isAuthenticated) {
            // If authenticated, go to dashboard
            navController.navigate("dashboard") {
                popUpTo("landing") { inclusive = true }
            }
        } else {
            // If not authenticated (e.g., after logout), go to landing
            // and clear the back stack to prevent going back to the dashboard
            if (navController.currentDestination?.route != "landing") {
                navController.navigate("landing") {
                    popUpTo(navController.graph.findStartDestination().id) { inclusive = true }
                }
            }
        }
    }

    val animationSpec = tween<IntOffset>(durationMillis = 300)

    // Pass the dynamic startDestination to the NavHost
    NavHost(navController = navController, startDestination = startDestination) {
        composable("landing") {
            LandingScreen(
                onLoginClicked = { navController.navigate("login") },
                onSignUpClicked = { navController.navigate("signup") }
            )
        }
        composable(
            "login",
            enterTransition = { slideInHorizontally(initialOffsetX = { it }, animationSpec = animationSpec) },
            exitTransition = { slideOutHorizontally(targetOffsetX = { it }, animationSpec = animationSpec) }
        ) {
            LoginScreen(navController = navController, authViewModel = authViewModel)
        }
        composable(
            "signup",
            enterTransition = { slideInHorizontally(initialOffsetX = { it }, animationSpec = animationSpec) },
            exitTransition = { slideOutHorizontally(targetOffsetX = { it }, animationSpec = animationSpec) }
        ) {
            SignUpScreen(navController = navController, authViewModel = authViewModel)
        }
        composable("dashboard") {
            // Pass the logout function from the ViewModel to the Dashboard
            DashboardScreen(onLogoutClicked = { authViewModel.logout() })
        }
    }
}

