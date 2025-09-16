package com.example.quizapp.ui.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.quizapp.data.AuthRepository
import com.example.quizapp.ui.auth.AuthViewModel
import com.example.quizapp.ui.auth.AuthViewModelFactory
import com.example.quizapp.ui.screens.DashboardScreen
import com.example.quizapp.ui.screens.LandingScreen
import com.example.quizapp.ui.screens.LoginScreen
import com.example.quizapp.ui.screens.SignUpScreen

// Animation constants
private const val ANIMATION_DURATION = 400

// Animation extension functions (Your excellent implementation)
fun AnimatedContentTransitionScope<*>.slideInFromLeft(): EnterTransition =
    slideIntoContainer(
        towards = AnimatedContentTransitionScope.SlideDirection.Right,
        animationSpec = tween(ANIMATION_DURATION)
    )

fun AnimatedContentTransitionScope<*>.slideInFromRight(): EnterTransition =
    slideIntoContainer(
        towards = AnimatedContentTransitionScope.SlideDirection.Left,
        animationSpec = tween(ANIMATION_DURATION)
    )

fun AnimatedContentTransitionScope<*>.slideOutToLeft(): ExitTransition =
    slideOutOfContainer(
        towards = AnimatedContentTransitionScope.SlideDirection.Left,
        animationSpec = tween(ANIMATION_DURATION)
    )

fun AnimatedContentTransitionScope<*>.slideOutToRight(): ExitTransition =
    slideOutOfContainer(
        towards = AnimatedContentTransitionScope.SlideDirection.Right,
        animationSpec = tween(ANIMATION_DURATION)
    )

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    // --- FIX: Centralized Dependency Creation ---
    // 1. Get the application context safely.
    val context = LocalContext.current.applicationContext
    // 2. Create a single instance of the AuthRepository that will be shared.
    val authRepository = remember { AuthRepository(context) }
    // 3. Create a factory to provide the repository to the ViewModel.
    val authViewModelFactory = AuthViewModelFactory(authRepository)
    // 4. Instantiate the ViewModel using the factory.
    val authViewModel: AuthViewModel = viewModel(factory = authViewModelFactory)
    // -----------------------------------------

    val authState by authViewModel.uiState.collectAsState()
    val startDestination = if (authState.isAuthenticated) "dashboard" else "landing"

    LaunchedEffect(authState.isAuthenticated) {
        val targetRoute = if (authState.isAuthenticated) "dashboard" else "landing"
        if (navController.currentDestination?.route != targetRoute) {
            navController.navigate(targetRoute) {
                popUpTo(navController.graph.findStartDestination().id) {
                    inclusive = true
                }
                launchSingleTop = true
            }
        }
    }

    NavHost(navController = navController, startDestination = startDestination) {
        composable(
            route = "landing",
            enterTransition = { slideInFromLeft() },
            exitTransition = { slideOutToLeft() },
            popEnterTransition = { slideInFromLeft() },
            popExitTransition = { slideOutToRight() }
        ) {
            LandingScreen(
                onLoginClicked = { navController.navigate("login") },
                onSignUpClicked = { navController.navigate("signup") }
            )
        }

        composable(
            route = "login",
            enterTransition = { slideInFromRight() },
            exitTransition = { slideOutToLeft() },
            popEnterTransition = { slideInFromLeft() },
            popExitTransition = { slideOutToRight() }
        ) {
            LoginScreen(navController = navController, authViewModel = authViewModel)
        }

        composable(
            route = "signup",
            enterTransition = { slideInFromRight() },
            exitTransition = { slideOutToLeft() },
            popEnterTransition = { slideInFromLeft() },
            popExitTransition = { slideOutToRight() }
        ) {
            SignUpScreen(navController = navController, authViewModel = authViewModel)
        }

        composable(
            route = "dashboard",
            enterTransition = { slideInFromRight() },
            // FIX: Corrected logout animation to slide left, matching the landing screen's entrance.
            exitTransition = { slideOutToLeft() },
            popEnterTransition = { slideInFromLeft() },
            popExitTransition = { slideOutToRight() }
        ) {
            // FIX: Pass the repository and logout function to the DashboardScreen.
            DashboardScreen(
                authRepository = authRepository,
                authViewModel = authViewModel,
                onLogoutClicked = { authViewModel.logout() }
            )
        }
    }
}

