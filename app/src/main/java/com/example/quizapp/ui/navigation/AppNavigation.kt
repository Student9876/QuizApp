package com.example.quizapp.ui.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.IntOffset
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.quizapp.ui.screens.LandingScreen
import com.example.quizapp.ui.screens.LoginScreen
import com.example.quizapp.ui.screens.SignUpScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val animationSpec = tween<IntOffset>(durationMillis = 300)

    NavHost(navController = navController, startDestination = "landing") {
        // Landing Screen Route
        composable(
            "landing",
            enterTransition = { slideInHorizontally(initialOffsetX = { -it / 3 }, animationSpec = animationSpec) },
            exitTransition = { slideOutHorizontally(targetOffsetX = { -it / 3 }, animationSpec = animationSpec) },
            popEnterTransition = { slideInHorizontally(initialOffsetX = { -it / 3 }, animationSpec = animationSpec) }
        ) {
            LandingScreen(
                onLoginClicked = { navController.navigate("login") },
                onSignUpClicked = { navController.navigate("signup") }
            )
        }

        // Login Screen Route
        composable(
            "login",
            enterTransition = { slideInHorizontally(initialOffsetX = { it }, animationSpec = animationSpec) },
            exitTransition = { slideOutHorizontally(targetOffsetX = { it }, animationSpec = animationSpec) },
            popExitTransition = { slideOutHorizontally(targetOffsetX = { it }, animationSpec = animationSpec) }
        ) {
            LoginScreen(
                onLoginSuccess = { /* TODO: Navigate to Home Screen */ },
                navController = navController
            )
        }

        // Sign Up Screen Route
        composable(
            "signup",
            enterTransition = { slideInHorizontally(initialOffsetX = { it }, animationSpec = animationSpec) },
            exitTransition = { slideOutHorizontally(targetOffsetX = { it }, animationSpec = animationSpec) },
            popExitTransition = { slideOutHorizontally(targetOffsetX = { it }, animationSpec = animationSpec) }
        ) {
            SignUpScreen(
                onSignUpSuccess = { /* TODO: Navigate to Home Screen */ },
                navController = navController
            )
        }
    }
}
