package com.example.flowstate.features

import androidx.compose.runtime.Composable
import com.example.flowstate.models.AssignmentsListViewModel
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
//import com.example.flowstate.viewmodel.AssignmentsListViewModel
import com.example.flowstate.models.Assignment
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun AssignmentsListScreen(viewModel: AssignmentsListViewModel) {

    // Collect live assignment list from ViewModel
    val assignments by viewModel.assignments.collectAsState()

    //read UI filter state
    val selectedFilter by viewModel.selectedFilter
    val selectedCourse by viewModel.selectedCourse

    val courseList = listOf("All Courses") +
            assignments.map { it.courseId }.distinct()

    val filteredAssignments = assignments.filter { a ->

        val matchStatus = when (selectedFilter) {
            "Pending" -> !a.isCompleted
            "Completed" -> a.isCompleted
            "Overdue" -> a.dueDate < System.currentTimeMillis()
            "All" -> true
            else -> true
        }

        val matchCourse = when (selectedCourse) {
            "All Courses" -> true
            else -> a.courseId == selectedCourse
        }

        matchStatus && matchCourse
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        FilterSection(
            selectedFilter = selectedFilter,
            onSelect = { viewModel.setFilter(it) }
        )

        Spacer(Modifier.height(12.dp))

        CourseDropdown(
            selectedCourse = selectedCourse,
            courseList = courseList,
            onCourseSelected = { viewModel.setCourseFilter(it) }
        )

        Spacer(Modifier.height(16.dp))

        //assignments list
        LazyColumn {
            items(filteredAssignments) { assignment ->
                AssignmentListItem(assignment)
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

@Composable
fun AssignmentListItem(a: Assignment) {

    val formatter = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
    val formattedDate = formatter.format(Date(a.dueDate))

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
    ) {
        Column(Modifier.padding(16.dp)) {

            Text(text = a.title, style = MaterialTheme.typography.titleMedium)

            Spacer(Modifier.height(4.dp))

            Text("Course: ${a.courseId}", style = MaterialTheme.typography.bodyMedium)
            Text("Due: $formattedDate", style = MaterialTheme.typography.bodyMedium)
        }
    }
}
