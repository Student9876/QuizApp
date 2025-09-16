package com.example.quizapp.data

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.android.*
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json


// --- Data Classes for API Communication ---
@Serializable
data class RegisterRequest(val name: String, val email: String, val password: String)
@Serializable
data class LoginRequest(val email: String, val password: String)
@Serializable
data class AuthResponse(val message: String? = null, val user: User? = null, val session: Session? = null, val error: String? = null)
@Serializable
data class User(val id: String, val email: String)
@Serializable
data class Session(val access_token: String)
@Serializable
data class CreateQuizRequest(val title: String, val description: String?, val duration_minutes: Int, val questions: List<QuestionRequest>)
@Serializable
data class QuestionRequest(val text: String, val type: String, val order_number: Int, val marks: Int, val correct_answer_text: String?, val options: List<OptionRequest>?)
@Serializable
data class OptionRequest(val text: String, val is_correct: Boolean)
@Serializable
data class CreateQuizResponse(val message: String, val quiz: QuizInfo)
@Serializable
data class QuizInfo(val id: String, val quiz_code: String)




//// --- NEW Data Classes for Quiz Creation ---
//@Serializable
//data class CreateQuizRequest(
//    val title: String,
//    val description: String?,
//    val duration_minutes: Int,
//    val questions: List<QuestionRequest>
//)
//@Serializable
//data class QuestionRequest(
//    val text: String,
//    val type: String, // "MULTIPLE_CHOICE" or "FILL_IN_THE_BLANK"
//    val order_number: Int,
//    val marks: Int,
//    val correct_answer_text: String?, // For fill-in-the-blank
//    val options: List<OptionRequest>? // For multiple-choice
//)
//@Serializable
//data class OptionRequest(
//    val text: String,
//    val is_correct: Boolean
//)
//@Serializable
//data class CreateQuizResponse(
//    val message: String,
//    val quiz: QuizInfo
//)
//@Serializable
//data class QuizInfo(
//    val id: String,
//    val quiz_code: String
//)
//
//
//// NEW Data Class for the My Quizzes screen
@Serializable
data class MyQuiz(
    val id: String,
    val title: String,
    val description: String?,
    val quiz_code: String,
    val duration_minutes: Int,
    val created_at: String
)


// Result class (Unchanged)
sealed class Result<out T> {
    data class Success<out T>(val data: T) : Result<T>()
    data class Error(val message: String, val isSessionExpired: Boolean = false) : Result<Nothing>()
}

class AuthRepository(private val context: Context) {
    // Use EncryptedSharedPreferences for secure storage
    private val sharedPreferences: SharedPreferences

    companion object {
        private const val KEY_AUTH_TOKEN = "auth_token"
    }

    init {
        // Initialize EncryptedSharedPreferences
        val masterKey = MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()

        sharedPreferences = EncryptedSharedPreferences.create(
            context,
            "secure_auth_prefs",
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

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
                val authResponse: AuthResponse = response.body()
                // Save the secure token on successful registration
                authResponse.session?.access_token?.let { saveToken(it) }
                Result.Success(authResponse)
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
                val authResponse: AuthResponse = response.body()
                // Save the secure token on successful login
                authResponse.session?.access_token?.let { saveToken(it) }
                Result.Success(authResponse)
            } else {
                val errorBody: AuthResponse = response.body()
                Result.Error(errorBody.error ?: "Login failed")
            }
        } catch (e: Exception) {
            Log.e("AuthRepository", "Login failed", e)
            Result.Error("A network error occurred. Please try again.")
        }
    }

    // --- NEW Function for Creating a Quiz ---
    suspend fun createQuiz(quizRequest: CreateQuizRequest): Result<CreateQuizResponse> {
        val token = getToken() ?: return Result.Error("User is not authenticated.")

        return try {
            val response: CreateQuizResponse = client.post("$baseUrl/api/quizapp/quizzes/create") {
                contentType(ContentType.Application.Json)
                bearerAuth(token)
                setBody(quizRequest)
            }.body()
            Result.Success(response)
        } catch (e: ClientRequestException) {
            // FIX: Specifically catch 401 Unauthorized errors
            if (e.response.status == HttpStatusCode.Unauthorized) {
                Result.Error("Session expired. Please log in again.", isSessionExpired = true)
            } else {
                Log.e("AuthRepository", "Create quiz failed", e)
                Result.Error("An error occurred while creating the quiz.")
            }
        } catch (e: Exception) {
            Log.e("AuthRepository", "Create quiz failed", e)
            Result.Error("A network error occurred.")
        }
    }

    // --- NEW Function for Fetching User's Quizzes ---
    suspend fun getMyQuizzes(): Result<List<MyQuiz>> {
        val token = getToken() ?: return Result.Error("User is not authenticated.")

        return try {
            val response: List<MyQuiz> = client.get("$baseUrl/api/quizapp/my-quizzes") {
                bearerAuth(token)
            }.body()
            Result.Success(response)
        } catch (e: ClientRequestException) {
            // FIX: Specifically catch 401 Unauthorized errors
            if (e.response.status == HttpStatusCode.Unauthorized) {
                Result.Error("Session expired. Please log in again.", isSessionExpired = true)
            } else {
                Log.e("AuthRepository", "Get My Quizzes failed", e)
                Result.Error("An error occurred while fetching quizzes.")
            }
        } catch (e: Exception) {
            Log.e("AuthRepository", "Get My Quizzes failed", e)
            Result.Error("A network error occurred.")
        }
    }




    // Function now checks for the existence of a token
    fun isUserLoggedIn(): Boolean {
        return getToken() != null
    }

    // Function now clears the token
    fun logout() {
        clearToken()
    }

    // Helper function to save the token
    private fun saveToken(token: String) {
        sharedPreferences.edit().putString(KEY_AUTH_TOKEN, token).apply()
    }

    // Helper function to retrieve the token
    private fun getToken(): String? {
        return sharedPreferences.getString(KEY_AUTH_TOKEN, null)
    }

    // Helper function to clear the token
    private fun clearToken() {
        sharedPreferences.edit().remove(KEY_AUTH_TOKEN).apply()
    }
}

