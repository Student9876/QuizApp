package com.example.quizapp.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest

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
        // --- INTERNET TEST ---
        // This AsyncImage will load an image from the internet.
        // If it appears, your internet connection and permissions are working.
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data("https://placehold.co/400x200/3498db/ffffff?text=Internet+OK")
                .crossfade(true)
                .build(),
            contentDescription = "Internet Connectivity Test Image",
            modifier = Modifier.size(width = 200.dp, height = 100.dp)
        )
        Spacer(modifier = Modifier.height(32.dp))

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
