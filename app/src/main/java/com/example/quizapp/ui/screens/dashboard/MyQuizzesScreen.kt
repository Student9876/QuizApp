package com.example.quizapp.ui.screens.dashboard

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.quizapp.data.AuthRepository
import com.example.quizapp.data.MyQuiz
import com.example.quizapp.ui.auth.AuthViewModel
// FIX: Import the ViewModel from its new, separate file
import com.example.quizapp.ui.screens.dashboard.myquizzes.MyQuizzesViewModel
import com.example.quizapp.ui.screens.dashboard.myquizzes.MyQuizzesViewModelFactory
import androidx.compose.runtime.LaunchedEffect

@Composable
fun MyQuizzesScreen(authRepository: AuthRepository, authViewModel: AuthViewModel) {
    // The ViewModel is instantiated here using its factory
    val viewModel: MyQuizzesViewModel = viewModel(factory = MyQuizzesViewModelFactory(authRepository, authViewModel))
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.fetchMyQuizzes()
    }

    Box(modifier = Modifier.fillMaxSize()) {
        if (uiState.isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        } else if (uiState.error != null) {
            Text(
                text = "Error: ${uiState.error}",
                modifier = Modifier.align(Alignment.Center).padding(16.dp),
                color = MaterialTheme.colorScheme.error
            )
        } else if (uiState.quizzes.isEmpty()) {
            Text(
                text = "You haven't created any quizzes yet.",
                modifier = Modifier.align(Alignment.Center).padding(16.dp)
            )
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(uiState.quizzes) { quiz ->
                    QuizListItem(quiz = quiz)
                }
            }
        }
    }
}

@Composable
fun QuizListItem(quiz: MyQuiz) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(quiz.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))
            Text(quiz.description ?: "No description", style = MaterialTheme.typography.bodyMedium, maxLines = 2)
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Code: ${quiz.quiz_code}", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
                Text("${quiz.duration_minutes} min", style = MaterialTheme.typography.labelMedium)
            }
        }
    }
}

