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

    // This effect observes the authentication state. If the user becomes
    // authenticated, it navigates to the dashboard and clears the back stack.
    LaunchedEffect(authState.isAuthenticated) {
        if (authState.isAuthenticated) {
            navController.navigate("dashboard") {
                popUpTo("landing") { inclusive = true }
            }
        }
    }

    val animationSpec = tween<IntOffset>(durationMillis = 300)

    NavHost(navController = navController, startDestination = "landing") {
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
            // Pass the single ViewModel instance to the LoginScreen
            LoginScreen(navController = navController, authViewModel = authViewModel)
        }
        composable(
            "signup",
            enterTransition = { slideInHorizontally(initialOffsetX = { it }, animationSpec = animationSpec) },
            exitTransition = { slideOutHorizontally(targetOffsetX = { it }, animationSpec = animationSpec) }
        ) {
            // Pass the same ViewModel instance to the SignUpScreen
            SignUpScreen(navController = navController, authViewModel = authViewModel)
        }
        composable("dashboard") {
            DashboardScreen()
        }
    }
}

