package com.example.flowstate.models


import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.State
import androidx.lifecycle.ViewModel
import com.example.flowstate.data.FlowstateDatabaseHelper
import com.example.flowstate.models.Assignment
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
/*

class AssignmentsListViewModel(private val dbHelper: FlowstateDatabaseHelper) : ViewModel() {

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
*/

class AssignmentsListViewModel(private val dbHelper: FlowstateDatabaseHelper) : ViewModel() {

    private val _assignments = MutableStateFlow<List<Assignment>>(emptyList())
    val assignments: StateFlow<List<Assignment>> = _assignments

    // UI filter states
    private val _selectedFilter = mutableStateOf("All")
    val selectedFilter: State<String> = _selectedFilter

    private val _selectedCourse = mutableStateOf("All Courses")
    val selectedCourse: State<String> = _selectedCourse

    init {
        loadAssignmentsFromDb()
    }

    fun loadAssignmentsFromDb() {
        val db = dbHelper.readableDatabase

        val cursor = db.rawQuery("SELECT * FROM assignments", null)
        val list = mutableListOf<Assignment>()

        while (cursor.moveToNext()) {

            val id = cursor.getString(cursor.getColumnIndexOrThrow("id"))

            list.add(
                Assignment(
                    id = id,
                    title = cursor.getString(cursor.getColumnIndexOrThrow("title")),
                    courseId = cursor.getString(cursor.getColumnIndexOrThrow("courseId")),
                    dueDate = cursor.getLong(cursor.getColumnIndexOrThrow("dueDate")),
                    priority = cursor.getInt(cursor.getColumnIndexOrThrow("priority")),
                    progress = cursor.getInt(cursor.getColumnIndexOrThrow("progress")),
                    notes = cursor.getString(cursor.getColumnIndexOrThrow("notes")),
                    estimatedTimeMinutes = cursor.getInt(cursor.getColumnIndexOrThrow("estimatedTimeMinutes")),
                    expectedGrade = cursor.getInt(cursor.getColumnIndexOrThrow("expectedGrade")),
                    actualGrade = cursor.getInt(cursor.getColumnIndexOrThrow("actualGrade")),
                    isCompleted = cursor.getInt(cursor.getColumnIndexOrThrow("isCompleted")) == 1,
                    subtasks = loadSubtasks(id) // load subtasks too
                )
            )
        }

        cursor.close()
        _assignments.value = list
    }

    private fun loadSubtasks(assignmentId: String): List<Subtask> {
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery(
            "SELECT * FROM subtasks WHERE assignmentId = ?",
            arrayOf(assignmentId)
        )

        val subtasks = mutableListOf<Subtask>()
        while (cursor.moveToNext()) {
            subtasks.add(
                Subtask(
                    id = cursor.getString(cursor.getColumnIndexOrThrow("id")),
                    text = cursor.getString(cursor.getColumnIndexOrThrow("text")),
                    isChecked = cursor.getInt(cursor.getColumnIndexOrThrow("isChecked")) == 1,
                    assignmentId = cursor.getString(cursor.getColumnIndexOrThrow("assignmentId"))
                )
            )
        }
        cursor.close()
        return subtasks
    }

    // Filters
    fun setFilter(filter: String) {
        _selectedFilter.value = filter
    }

    fun setCourseFilter(course: String) {
        _selectedCourse.value = course
    }
}
