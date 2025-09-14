package com.example.quizapp.data

import android.util.Log
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.android.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

// Data classes for API communication
// ADDING @Serializable is the second key fix.
@Serializable
data class RegisterRequest(val name: String, val email: String, val password: String)

@Serializable
data class LoginRequest(val email: String, val password: String)

@Serializable
data class AuthResponse(val message: String? = null, val user: User? = null, val error: String? = null)

@Serializable
data class User(val id: String, val email: String)

// Sealed class to represent API call results
sealed class Result<out T> {
    data class Success<out T>(val data: T) : Result<T>()
    data class Error(val message: String) : Result<Nothing>()
}

class AuthRepository {
    private val client = HttpClient(Android) {
        install(ContentNegotiation) {
            json(Json {
                prettyPrint = true
                isLenient = true
                ignoreUnknownKeys = true
            })
        }
    }

    private val baseUrl = "https://apiverc.vercel.app"

    suspend fun register(name: String, email: String, password: String): Result<AuthResponse> {
        return try {
            val response: HttpResponse = client.post("$baseUrl/api/quizapp/auth/register") {
                contentType(ContentType.Application.Json)
                setBody(RegisterRequest(name, email, password))
            }
            if (response.status.isSuccess()) {
                Result.Success(response.body())
            } else {
                val errorBody: AuthResponse = response.body()
                Result.Error(errorBody.error ?: "Registration failed")
            }
        } catch (e: Exception) {
            Log.e("AuthRepository", "Registration failed", e)
            Result.Error("A network error occurred. Please try again.")
        }
    }

    suspend fun login(email: String, password: String): Result<AuthResponse> {
        return try {
            val response: HttpResponse = client.post("$baseUrl/api/quizapp/auth/login") {
                contentType(ContentType.Application.Json)
                setBody(LoginRequest(email, password))
            }
            if (response.status.isSuccess()) {
                Result.Success(response.body())
            } else {
                val errorBody: AuthResponse = response.body()
                Result.Error(errorBody.error ?: "Login failed")
            }
        } catch (e: Exception) {
            Log.e("AuthRepository", "Login failed", e)
            Result.Error("A network error occurred. Please try again.")
        }
    }
}

