package com.example.quizapp.ui.auth

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.quizapp.data.AuthRepository
import com.example.quizapp.data.Result
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// UI State data class (Unchanged)
data class AuthUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val isAuthenticated: Boolean = false
)

// ViewModel now extends AndroidViewModel to access the application context
class AuthViewModel(application: Application) : AndroidViewModel(application) {

    // Pass the context to the repository
    private val authRepository: AuthRepository = AuthRepository(application.applicationContext)

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState = _uiState.asStateFlow()

    // init block runs when the ViewModel is created
    init {
        // Check the initial authentication state when the app starts
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

    // New function to handle logout
    fun logout() {
        authRepository.logout()
        // Reset the UI state to reflect the user is logged out
        _uiState.value = AuthUiState(isAuthenticated = false)
    }


    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}

