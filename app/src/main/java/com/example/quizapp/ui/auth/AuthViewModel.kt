package com.example.quizapp.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.quizapp.data.AuthRepository
import com.example.quizapp.data.Result
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// Represents the state of our authentication UI
data class AuthUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val isAuthenticated: Boolean = false
)

class AuthViewModel(private val authRepository: AuthRepository = AuthRepository()) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState = _uiState.asStateFlow()

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _uiState.value = AuthUiState(isLoading = true)
            when (val result = authRepository.login(email, password)) {
                is Result.Success -> {
                    _uiState.value = AuthUiState(isAuthenticated = true)
                }
                is Result.Error -> {
                    _uiState.value = AuthUiState(error = result.message)
                }
            }
        }
    }

    fun register(name: String, email: String, password: String) {
        viewModelScope.launch {
            _uiState.value = AuthUiState(isLoading = true)
            when (val result = authRepository.register(name, email, password)) {
                is Result.Success -> {
                    // Assuming successful registration also logs the user in
                    _uiState.value = AuthUiState(isAuthenticated = true)
                }
                is Result.Error -> {
                    _uiState.value = AuthUiState(error = result.message)
                }
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}
