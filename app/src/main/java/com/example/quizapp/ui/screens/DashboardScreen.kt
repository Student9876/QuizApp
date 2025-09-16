package com.example.quizapp.ui.screens

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
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
import com.example.quizapp.ui.screens.dashboard.JoinScreen
import com.example.quizapp.ui.screens.dashboard.MyQuizzesScreen
import com.example.quizapp.ui.screens.dashboard.components.DashboardBottomBar
import com.example.quizapp.data.AuthRepository

// Animation constants for dashboard
private const val DASHBOARD_ANIMATION_DURATION = 300 // Slightly faster for tab switching

// Dashboard animation extension functions
fun AnimatedContentTransitionScope<*>.dashboardSlideInFromLeft(): EnterTransition =
    slideIntoContainer(
        towards = AnimatedContentTransitionScope.SlideDirection.Right,
        animationSpec = tween(DASHBOARD_ANIMATION_DURATION)
    )

fun AnimatedContentTransitionScope<*>.dashboardSlideInFromRight(): EnterTransition =
    slideIntoContainer(
        towards = AnimatedContentTransitionScope.SlideDirection.Left,
        animationSpec = tween(DASHBOARD_ANIMATION_DURATION)
    )

fun AnimatedContentTransitionScope<*>.dashboardSlideOutToLeft(): ExitTransition =
    slideOutOfContainer(
        towards = AnimatedContentTransitionScope.SlideDirection.Left,
        animationSpec = tween(DASHBOARD_ANIMATION_DURATION)
    )

fun AnimatedContentTransitionScope<*>.dashboardSlideOutToRight(): ExitTransition =
    slideOutOfContainer(
        towards = AnimatedContentTransitionScope.SlideDirection.Right,
        animationSpec = tween(DASHBOARD_ANIMATION_DURATION)
    )

@Composable
fun DashboardScreen(
    authRepository: AuthRepository,
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
                startDestination = "join" // The default screen is "Join"
            ) {
                composable(
                    route = "join",
                    enterTransition = {
                        when (initialState.destination.route) {
                            "create", "my_quizzes", "options" -> dashboardSlideInFromLeft()
                            else -> dashboardSlideInFromRight()
                        }
                    },
                    exitTransition = {
                        when (targetState.destination.route) {
                            "create", "my_quizzes", "options" -> dashboardSlideOutToLeft()
                            else -> dashboardSlideOutToRight()
                        }
                    }
                ) {
                    JoinScreen()
                }

                composable(
                    route = "create",
                    enterTransition = {
                        when (initialState.destination.route) {
                            "join" -> dashboardSlideInFromRight()
                            "my_quizzes", "options" -> dashboardSlideInFromLeft()
                            else -> dashboardSlideInFromRight()
                        }
                    },
                    exitTransition = {
                        when (targetState.destination.route) {
                            "join" -> dashboardSlideOutToRight()
                            "my_quizzes", "options" -> dashboardSlideOutToLeft()
                            else -> dashboardSlideOutToRight()
                        }
                    }
                ) {
                    CreateQuizScreen(authRepository = authRepository)
                }

                composable(
                    route = "my_quizzes",
                    enterTransition = {
                        when (initialState.destination.route) {
                            "join", "create" -> dashboardSlideInFromRight()
                            "options" -> dashboardSlideInFromLeft()
                            else -> dashboardSlideInFromRight()
                        }
                    },
                    exitTransition = {
                        when (targetState.destination.route) {
                            "join", "create" -> dashboardSlideOutToRight()
                            "options" -> dashboardSlideOutToLeft()
                            else -> dashboardSlideOutToRight()
                        }
                    }
                ) {
                    MyQuizzesScreen()
                }

                composable(
                    route = "options",
                    enterTransition = {
                        when (initialState.destination.route) {
                            "join", "create", "my_quizzes" -> dashboardSlideInFromRight()
                            else -> dashboardSlideInFromLeft()
                        }
                    },
                    exitTransition = {
                        when (targetState.destination.route) {
                            "join", "create", "my_quizzes" -> dashboardSlideOutToRight()
                            else -> dashboardSlideOutToLeft()
                        }
                    }
                ) {
                    OptionsScreen(onLogoutClicked = onLogoutClicked)
                }
            }
        }
    }
}