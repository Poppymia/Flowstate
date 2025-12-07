package com.example.flowstate.features

import androidx.compose.runtime.Composable
import com.example.flowstate.models.AssignmentsListViewModel
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.flowstate.Components.AssignmentCard
//import com.example.flowstate.viewmodel.AssignmentsListViewModel
import com.example.flowstate.models.Assignment
import com.example.flowstate.navigation.Screen
import java.text.SimpleDateFormat
import java.util.*


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AssignmentsListScreen(
    viewModel: AssignmentsListViewModel,
    navController: NavController
) {
    val assignments by viewModel.assignments.collectAsState()

    val selectedFilter by viewModel.selectedFilter
    val selectedCourse by viewModel.selectedCourse

    val courseList = listOf("All Courses") +
            assignments.map { it.courseId }.distinct()

    val filteredAssignments = assignments.filter { a ->

        val matchStatus = when (selectedFilter) {
            "Pending" -> !a.isCompleted
            "Completed" -> a.isCompleted
            "Overdue" -> a.dueDate < System.currentTimeMillis()
            else -> true
        }

        val matchCourse = if (selectedCourse == "All Courses")
            true else a.courseId == selectedCourse

        matchStatus && matchCourse
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Assignments") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }

    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {

            // Filters
            FilterSection(
                selectedFilter = selectedFilter,
                onSelect = { viewModel.setFilter(it) }
            )

            Spacer(Modifier.height(12.dp))

            // Course dropdown
            CourseDropdown(
                selectedCourse = selectedCourse,
                courseList = courseList,
                onCourseSelected = { viewModel.setCourseFilter(it) }
            )

            Spacer(Modifier.height(16.dp))

            // Assignment List (scrollable)
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(filteredAssignments) { assignment ->
                    AssignmentCard(
                        assignment = assignment,
                        onClick = {
                            navController.navigate("details/${assignment.id}")
                        }
                    )
                }
            }

            // FAB
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.BottomEnd
            ) {
                /*FloatingActionButton(
                    onClick = {
                        // navigate to add assignment screen
                        navController.navigate("AssignmentDetailsScreen")
                    },
                    containerColor = Color(0xFFFBDE98),
                    modifier = Modifier.padding(16.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add Assignment", tint = Color.Black)
                }*/
                FloatingActionButton(
                    onClick = {
                        navController.navigate(Screen.AssignmentAdd.route)
                    },
                    containerColor = Color(0xFFFBDE98),
                    modifier = Modifier.padding(16.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add Assignment", tint = Color.Black)
                }

            }

        }

    }
}

@Composable
fun FilterSection(selectedFilter: String, onSelect: (String) -> Unit) {
    val filters = listOf("All", "Pending", "Completed", "Overdue")

    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        filters.forEach { filter ->
            FilterChip(
                selected = selectedFilter == filter,
                onClick = { onSelect(filter) },
                label = { Text(filter) }
            )
        }
    }
}

@Composable
fun CourseDropdown(
    selectedCourse: String,
    courseList: List<String>,
    onCourseSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        OutlinedButton(onClick = { expanded = true }) {
            Text(selectedCourse)
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            courseList.forEach { course ->
                DropdownMenuItem(
                    text = { Text(course) },
                    onClick = {
                        onCourseSelected(course)
                        expanded = false
                    }
                )
            }
        }
    }
}

//assignment cards
@Composable
fun AssignmentListItem(
    a: Assignment,
    onClick: () -> Unit
) {
    val formatter = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
    val formattedDate = formatter.format(Date(a.dueDate))

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }     // <-- Tap goes to details
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(Modifier.padding(16.dp)) {

            Text(text = a.title, style = MaterialTheme.typography.titleMedium)

            Spacer(Modifier.height(4.dp))

            Text("Course: ${a.courseId}", style = MaterialTheme.typography.bodyMedium)
            Text("Due: $formattedDate", style = MaterialTheme.typography.bodyMedium)
        }
    }
}
