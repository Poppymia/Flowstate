package com.example.flowstate.models


import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.State
import androidx.lifecycle.ViewModel
import com.example.flowstate.models.Assignment
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class AssignmentsListViewModel : ViewModel() {

    private val _assignments = MutableStateFlow<List<Assignment>>(emptyList())
    val assignments: StateFlow<List<Assignment>> = _assignments

    // UI filter state
    private val _selectedFilter = mutableStateOf("All")
    val selectedFilter: State<String> = _selectedFilter

    private val _selectedCourse = mutableStateOf("All Courses")
    val selectedCourse: State<String> = _selectedCourse

    // Update filter chip selection
    fun setFilter(filter: String) {
        _selectedFilter.value = filter
    }

    // Update dropdown selection
    fun setCourseFilter(course: String) {
        _selectedCourse.value = course
    }

    // TEMP: Populate sample data remove this and load from SQLite repo
    fun loadSampleData() {
        _assignments.value = listOf(
            Assignment(
                title = "Math Homework",
                courseId = "Math101",
                dueDate = System.currentTimeMillis() + 86400000L, // tomorrow
                priority = 1,
                progress = 0
            ),
            Assignment(
                title = "English Essay",
                courseId = "Eng202",
                dueDate = System.currentTimeMillis() - 86400000L, // overdue
                priority = 2,
                progress = 30
            ),
            Assignment(
                title = "Biology Lab",
                courseId = "Bio301",
                dueDate = System.currentTimeMillis() + 200000000L,
                priority = 1,
                progress = 100,
                isCompleted = true
            )
        )
    }
}
