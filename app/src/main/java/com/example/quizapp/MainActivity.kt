package com.example.quizapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.quizapp.ui.theme.QuizAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            QuizAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    QuizAppNavigation()
                }
            }
        }
    }
}


@Composable
fun QuizAppNavigation() {
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

@Composable
fun LandingScreen(
    onLoginClicked: () -> Unit,
    onSignUpClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Quiz Pro",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Create, share, and take quizzes with ease.",
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(modifier = Modifier.height(48.dp))

        Button(
            onClick = onLoginClicked,
            modifier = Modifier.padding(8.dp)
        ) {
            Text(text = "Login")
        }

        OutlinedButton(
            onClick = onSignUpClicked,
            modifier = Modifier.padding(8.dp)
        ) {
            Text(text = "Sign Up")
        }
    }
}

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    navController: NavController,
    modifier: Modifier = Modifier
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Login", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(32.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email Address") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = {
                // TODO: Add login logic
                onLoginSuccess()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Login")
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = {
                navController.popBackStack()
            },
            modifier = Modifier.align(Alignment.End)
        ) {
            Text("Go Back")
        }
    }
}

@Composable
fun SignUpScreen(
    onSignUpSuccess: () -> Unit,
    navController: NavController,
    modifier: Modifier = Modifier
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Create Account", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(32.dp))

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Full Name") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email Address") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = {
                // TODO: Add sign up logic
                onSignUpSuccess()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Sign Up")
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = {
                navController.popBackStack()
            },
            modifier = Modifier.align(Alignment.End)
        ) {
            Text("Go Back")
        }
    }
}

@Composable
@Preview(showBackground = true)
fun LandingScreenPreview() {
    QuizAppTheme {
        LandingScreen(onLoginClicked = {}, onSignUpClicked = {})
    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    QuizAppTheme {
        // We pass a dummy NavController for the preview
        val navController = rememberNavController()
        LoginScreen(onLoginSuccess = {}, navController = navController)
    }
}

@Preview(showBackground = true)
@Composable
fun SignUpScreenPreview() {
    QuizAppTheme {
        val navController = rememberNavController()
        SignUpScreen(onSignUpSuccess = {}, navController = navController)
    }
}
