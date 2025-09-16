package com.example.quizapp.ui.screens.dashboard.create

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.quizapp.data.AuthRepository
import com.example.quizapp.data.CreateQuizRequest
import com.example.quizapp.data.OptionRequest
import com.example.quizapp.data.QuestionRequest
import com.example.quizapp.data.Result
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// Represents the different steps in the quiz creation UI
sealed class CreateQuizStep {
    object Intro : CreateQuizStep()
    object Form : CreateQuizStep()
}

// Represents the overall state of the Create Quiz screen
data class CreateQuizUiState(
    val currentStep: CreateQuizStep = CreateQuizStep.Intro,
    val isLoading: Boolean = false,
    val error: String? = null,
    val successMessage: String? = null
)

// Data classes to hold the form state in the ViewModel
data class QuestionState(
    // FIX: Corrected type from mutableStateOf<String> to MutableState<String>
    val text: MutableState<String> = mutableStateOf(""),
    val type: MutableState<String> = mutableStateOf("MULTIPLE_CHOICE"),
    val marks: MutableState<String> = mutableStateOf("1"),
    val correctAnswerText: MutableState<String> = mutableStateOf(""),
    val options: SnapshotStateList<OptionState> = mutableStateListOf(OptionState(), OptionState())
)

data class OptionState(
    // FIX: Corrected type from mutableStateOf<String> to MutableState<String>
    val text: MutableState<String> = mutableStateOf(""),
    // FIX: Corrected type and changed from var to val for consistency
    val isCorrect: MutableState<Boolean> = mutableStateOf(false)
)

class CreateQuizViewModel(private val authRepository: AuthRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(CreateQuizUiState())
    val uiState = _uiState.asStateFlow()

    // Form state
    val title = mutableStateOf("")
    val description = mutableStateOf("")
    val duration = mutableStateOf("")
    val questions = mutableStateListOf<QuestionState>()

    init {
        // Start with one default question
        addQuestion()
    }

    fun startQuizCreation() {
        _uiState.value = _uiState.value.copy(currentStep = CreateQuizStep.Form)
    }

    fun addQuestion() {
        questions.add(QuestionState())
    }

    fun removeQuestion(index: Int) {
        if (questions.size > 1) { // Always keep at least one question
            questions.removeAt(index)
        }
    }

    fun addOption(questionIndex: Int) {
        questions[questionIndex].options.add(OptionState())
    }

    fun removeOption(questionIndex: Int, optionIndex: Int) {
        if (questions[questionIndex].options.size > 2) { // Always keep at least two options
            questions[questionIndex].options.removeAt(optionIndex)
        }
    }

    fun setCorrectOption(questionIndex: Int, correctOptionIndex: Int) {
        questions[questionIndex].options.forEachIndexed { index, option ->
            option.isCorrect.value = (index == correctOptionIndex)
        }
    }

    fun createQuiz() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            // 1. Build the request object from the current state
            val questionRequests = questions.mapIndexed { index, qState ->
                QuestionRequest(
                    text = qState.text.value,
                    type = qState.type.value,
                    order_number = index + 1,
                    marks = qState.marks.value.toIntOrNull() ?: 1,
                    correct_answer_text = if (qState.type.value == "FILL_IN_THE_BLANK") qState.correctAnswerText.value else null,
                    options = if (qState.type.value == "MULTIPLE_CHOICE") {
                        qState.options.map { oState ->
                            OptionRequest(text = oState.text.value, is_correct = oState.isCorrect.value)
                        }
                    } else null
                )
            }

            val request = CreateQuizRequest(
                title = title.value,
                description = description.value.takeIf { it.isNotBlank() },
                duration_minutes = duration.value.toIntOrNull() ?: 10,
                questions = questionRequests
            )

            // 2. Call the repository
            when(val result = authRepository.createQuiz(request)) {
                is Result.Success -> {
                    _uiState.value = _uiState.value.copy(isLoading = false, successMessage = "Quiz created with code: ${result.data.quiz.quiz_code}")
                }
                is Result.Error -> {
                    _uiState.value = _uiState.value.copy(isLoading = false, error = result.message)
                }
            }
        }
    }
}

