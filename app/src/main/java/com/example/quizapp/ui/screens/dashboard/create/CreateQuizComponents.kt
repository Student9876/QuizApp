package com.example.quizapp.ui.screens.dashboard.create

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

@Composable
fun QuizCreationForm(viewModel: CreateQuizViewModel) {
    LazyColumn(modifier = Modifier.padding(16.dp)) {
        item {
            Text("Create Your Quiz", style = MaterialTheme.typography.headlineSmall)
            Spacer(modifier = Modifier.height(16.dp))

            // Quiz Details
            OutlinedTextField(value = viewModel.title.value, onValueChange = { viewModel.title.value = it }, label = { Text("Quiz Title") }, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(value = viewModel.description.value, onValueChange = { viewModel.description.value = it }, label = { Text("Description (Optional)") }, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(value = viewModel.duration.value, onValueChange = { viewModel.duration.value = it }, label = { Text("Duration (minutes)") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(24.dp))
        }

        // Questions List
        itemsIndexed(viewModel.questions) { index, question ->
            QuestionCard(
                questionIndex = index,
                questionState = question,
                viewModel = viewModel
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Action Buttons
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(onClick = { viewModel.addQuestion() }) {
                    Text("Add Question")
                }
                FilledTonalButton(onClick = { viewModel.createQuiz() }) {
                    Text("Finish & Create Quiz")
                }
            }
        }
    }
}

@Composable
fun QuestionCard(
    questionIndex: Int,
    questionState: QuestionState,
    viewModel: CreateQuizViewModel
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Question ${questionIndex + 1}", style = MaterialTheme.typography.titleMedium, modifier = Modifier.weight(1f))
                if (viewModel.questions.size > 1) {
                    IconButton(onClick = { viewModel.removeQuestion(questionIndex) }) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete Question")
                    }
                }
            }
            OutlinedTextField(value = questionState.text.value, onValueChange = { questionState.text.value = it }, label = { Text("Question Text") }, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(value = questionState.marks.value, onValueChange = { questionState.marks.value = it }, label = { Text("Marks") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.fillMaxWidth())

            // Options for Multiple Choice
            if (questionState.type.value == "MULTIPLE_CHOICE") {
                Spacer(modifier = Modifier.height(16.dp))
                Text("Options (select the correct one)", style = MaterialTheme.typography.labelLarge)
                questionState.options.forEachIndexed { optionIndex, option ->
                    OptionEditor(
                        questionIndex = questionIndex,
                        optionIndex = optionIndex,
                        optionState = option,
                        viewModel = viewModel
                    )
                }
                TextButton(onClick = { viewModel.addOption(questionIndex) }) {
                    Text("Add Option")
                }
            }
        }
    }
}

@Composable
fun OptionEditor(
    questionIndex: Int,
    optionIndex: Int,
    optionState: OptionState,
    viewModel: CreateQuizViewModel
) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
        RadioButton(
            selected = optionState.isCorrect.value,
            onClick = { viewModel.setCorrectOption(questionIndex, optionIndex) }
        )
        OutlinedTextField(
            value = optionState.text.value,
            onValueChange = { optionState.text.value = it },
            modifier = Modifier.weight(1f),
            label = { Text("Option ${optionIndex + 1}") }
        )
        if (viewModel.questions[questionIndex].options.size > 2) {
            IconButton(onClick = { viewModel.removeOption(questionIndex, optionIndex) }) {
                Icon(Icons.Default.Delete, contentDescription = "Delete Option")
            }
        }
    }
}

@Composable
fun CreateQuizIntro(onStartCreation: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Create a New Quiz", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))
        Text("Get started by setting up your quiz details and adding questions.", textAlign = androidx.compose.ui.text.style.TextAlign.Center)
        Spacer(modifier = Modifier.height(32.dp))
        Button(onClick = onStartCreation) {
            Text("Let's Create!")
        }
    }
}