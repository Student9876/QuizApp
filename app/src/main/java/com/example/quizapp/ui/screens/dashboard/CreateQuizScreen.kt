package com.example.quizapp.ui.screens.dashboard

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.quizapp.data.AuthRepository
import com.example.quizapp.ui.screens.dashboard.create.CreateQuizIntro
import com.example.quizapp.ui.screens.dashboard.create.CreateQuizUiState
import com.example.quizapp.ui.screens.dashboard.create.CreateQuizViewModel
import com.example.quizapp.ui.screens.dashboard.create.QuizCreationForm

// We need a ViewModel Factory to pass the repository to the ViewModel
class CreateQuizViewModelFactory(private val authRepository: AuthRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CreateQuizViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CreateQuizViewModel(authRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

@Composable
fun CreateQuizScreen(authRepository: AuthRepository) {
    val viewModel: CreateQuizViewModel = viewModel(factory = CreateQuizViewModelFactory(authRepository))
    val uiState by viewModel.uiState.collectAsState()

    Box(modifier = Modifier.fillMaxSize()) {
        when (uiState.currentStep) {
            is com.example.quizapp.ui.screens.dashboard.create.CreateQuizStep.Intro -> {
                CreateQuizIntro(onStartCreation = { viewModel.startQuizCreation() })
            }
            is com.example.quizapp.ui.screens.dashboard.create.CreateQuizStep.Form -> {
                QuizCreationForm(viewModel = viewModel)
            }
        }

        if (uiState.isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }

        uiState.error?.let {
            Snackbar(modifier = Modifier.align(Alignment.BottomCenter).padding(16.dp)) {
                Text(it)
            }
        }

        uiState.successMessage?.let {
            Snackbar(modifier = Modifier.align(Alignment.BottomCenter).padding(16.dp)) {
                Text(it)
            }
        }
    }
}