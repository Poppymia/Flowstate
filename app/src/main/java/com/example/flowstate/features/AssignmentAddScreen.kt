package com.example.flowstate.features

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.flowstate.features.add.AssignmentAddViewModel
import androidx.compose.ui.res.stringResource
import com.example.flowstate.R

@Composable
fun AssignmentAddScreen(
    navController: NavController,
    viewModel: AssignmentAddViewModel
) {
    val error = viewModel.errorMessage.value

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        // Top Row: Back + Title
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back")
            }
            Text(
                text = stringResource(id = R.string.AssignmentAdd_title),
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(start = 8.dp)
            )
        }
        Spacer(Modifier.height(16.dp))

        OutlinedTextField(
            value = viewModel.title.value,
            onValueChange = { viewModel.title.value = it },
            label = { Text(stringResource(id = R.string.label_title)) },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = viewModel.courseId.value,
            onValueChange = { viewModel.courseId.value = it },
            label = { Text(stringResource(id = R.string.label_course_id)) },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = viewModel.notes.value,
            onValueChange = { viewModel.notes.value = it },
            label = { Text(stringResource(id = R.string.label_notes)) },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = viewModel.estimatedMinutesInput.value,
            onValueChange = { viewModel.estimatedMinutesInput.value = it },
            label = { Text(stringResource(id = R.string.label_estimated_minutes)) },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = viewModel.expectedGradeInput.value,
            onValueChange = { viewModel.expectedGradeInput.value = it },
            label = { Text(stringResource(id = R.string.label_expected_grade)) },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(16.dp))

        // Error Message
        if (error != null) {
            Text(
                text = error,
                color = Color.Red,
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(Modifier.height(8.dp))
        }
        Spacer(Modifier.weight(1f))

        // Buttons
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedButton(onClick = { navController.popBackStack() }) {
                Text(stringResource(id = R.string.cancel))
            }

            Button(
                onClick = {
                    if (viewModel.saveAssignment()) {
                        navController.popBackStack()
                    }
                }
            ) {
                Text(stringResource(id = R.string.save))
            }
        }
    }
}
