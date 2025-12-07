package com.example.flowstate.features

//package com.example.flowstate.features

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

//enhance:
//different random colour per course code
//validation not working properly
/*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AssignmentAddScreen(
    navController: NavController,
    viewModel: AssignmentAddViewModel
) {
    //val viewModel = remember { AssignmentAddViewModel(dbHelper) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add Assignment") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { inner ->
        Column(
            modifier = Modifier
                .padding(inner)
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            OutlinedTextField(
                value = viewModel.title.value,
                onValueChange = { viewModel.title.value = it },
                label = { Text("Title") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = viewModel.courseId.value,
                onValueChange = { viewModel.courseId.value = it },
                label = { Text("Course ID") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = viewModel.notes.value,
                onValueChange = { viewModel.notes.value = it },
                label = { Text("Notes") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                OutlinedButton(onClick = { navController.popBackStack() }) {
                    Text("Cancel")
                }

                Button(onClick = {
                    viewModel.saveAssignment()
                    navController.popBackStack()
                }) {
                    Text("Add")
                }
            }
        }
    }
}
*/

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
                text = "Add Assignment",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(start = 8.dp)
            )
        }

        Spacer(Modifier.height(16.dp))

        OutlinedTextField(
            value = viewModel.title.value,
            onValueChange = { viewModel.title.value = it },
            label = { Text("Title") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = viewModel.courseId.value,
            onValueChange = { viewModel.courseId.value = it },
            label = { Text("Course ID") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = viewModel.notes.value,
            onValueChange = { viewModel.notes.value = it },
            label = { Text("Notes (optional)") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = viewModel.estimatedMinutesInput.value,
            onValueChange = { viewModel.estimatedMinutesInput.value = it },
            label = { Text("Estimated Minutes (optional)") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = viewModel.expectedGradeInput.value,
            onValueChange = { viewModel.expectedGradeInput.value = it },
            label = { Text("Expected Grade (optional)") },
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
                Text("Cancel")
            }

            Button(
                onClick = {
                    if (viewModel.saveAssignment()) {
                        navController.popBackStack()
                    }
                }
            ) {
                Text("Save")
            }
        }
    }
}
