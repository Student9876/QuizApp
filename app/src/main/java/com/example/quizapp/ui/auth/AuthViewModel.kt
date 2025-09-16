package com.example.quizapp.ui.auth

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.quizapp.data.AuthRepository
import com.example.quizapp.data.Result
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// UI State data class
data class AuthUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val isAuthenticated: Boolean = false
)

// The ViewModel now correctly accepts the repository.
class AuthViewModel(private val authRepository: AuthRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState = _uiState.asStateFlow()

    init {
        checkInitialAuthState()
    }

    private fun checkInitialAuthState() {
        val isLoggedIn = authRepository.isUserLoggedIn()
        _uiState.value = AuthUiState(isAuthenticated = isLoggedIn)
    }

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
                    _uiState.value = AuthUiState(isAuthenticated = true)
                }
                is Result.Error -> {
                    _uiState.value = AuthUiState(error = result.message)
                }
            }
        }
    }

    fun logout() {
        authRepository.logout()
        _uiState.value = AuthUiState(isAuthenticated = false)
    }
    // FIX: New function to be called by other ViewModels when a 401 error is detected.
    fun forceLogout() {
        // This reuses the existing logout logic, which will trigger the navigation
        // change in AppNavigation due to the state update.
        logout()
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}

// THIS IS THE FIX: The factory's constructor now correctly
// requires an AuthRepository, matching how it's called in AppNavigation.
class AuthViewModelFactory(private val authRepository: AuthRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AuthViewModel(authRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

