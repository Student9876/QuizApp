package com.example.quizapp.ui.screens.dashboard.myquizzes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.quizapp.data.AuthRepository
import com.example.quizapp.data.MyQuiz
import com.example.quizapp.data.Result
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.example.quizapp.ui.auth.AuthViewModel

data class MyQuizzesUiState(
    val quizzes: List<MyQuiz> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

class MyQuizzesViewModel(
    private val authRepository: AuthRepository,
    private val authViewModel: AuthViewModel
) : ViewModel() {

    private val _uiState = MutableStateFlow(MyQuizzesUiState())
    val uiState = _uiState.asStateFlow()

    init {
        fetchMyQuizzes()
    }

    fun fetchMyQuizzes() {
        viewModelScope.launch {
            _uiState.value = MyQuizzesUiState(isLoading = true)
            when (val result = authRepository.getMyQuizzes()) {
                is Result.Success -> {
                    _uiState.value = MyQuizzesUiState(quizzes = result.data)
                }
                is Result.Error -> {
                    // FIX: Check for session expiry
                    if (result.isSessionExpired) {
                        authViewModel.forceLogout()
                    } else {
                        _uiState.value = MyQuizzesUiState(error = result.message)
                    }
                }
            }
        }
    }
}

class MyQuizzesViewModelFactory(
    private val authRepository: AuthRepository,
    private val authViewModel: AuthViewModel
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MyQuizzesViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MyQuizzesViewModel(authRepository, authViewModel) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
