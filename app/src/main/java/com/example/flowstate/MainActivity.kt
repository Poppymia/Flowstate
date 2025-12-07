package com.example.flowstate

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.flowstate.data.FlowstateDatabaseHelper
import com.example.flowstate.models.AssignmentsListViewModel
import com.example.flowstate.models.Course
import com.example.flowstate.navigation.FlowstateApp
import com.example.flowstate.ui.theme.FlowstateTheme


class MainActivity : ComponentActivity() {

    private lateinit var assignmentsListViewModel: AssignmentsListViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val dbHelper = FlowstateDatabaseHelper(this)

        // Create ViewModel OUTSIDE Compose
        assignmentsListViewModel = AssignmentsListViewModel(dbHelper)

        // Seed sample data BEFORE Compose if database is empty
        if (!dbHelper.hasAssignments()) {
            seedSampleAssignments(dbHelper)
        }

        // Seed sample courses if database is empty
        if (!dbHelper.hasCourses()) {
            seedSampleCourses(dbHelper)
        }

        setContent {
            FlowstateTheme {
                FlowstateApp(
                    dbHelper = dbHelper,
                    assignmentsListViewModel = assignmentsListViewModel
                )
            }
        }
    }

    private fun seedSampleAssignments(dbHelper: FlowstateDatabaseHelper) {
        val sampleId = "test-assignment-1"

        dbHelper.insertAssignment(
            id = sampleId,
            title = "PROG3211 Final Project",
            courseId = "PROG3211",
            dueDate = System.currentTimeMillis() + (86400000 * 3), // +3 days
            priority = 2,
            progress = 0,
            notes = "Start working on UI & database"
        )

        dbHelper.insertSubtask("st-1", sampleId, "Design UI screens", false)
        dbHelper.insertSubtask("st-2", sampleId, "Implement local DB", false)
        dbHelper.insertSubtask("st-3", sampleId, "Test navigation", false)
    }

    private fun seedSampleCourses(dbHelper: FlowstateDatabaseHelper) {
        // Sample current term course
        val currentCourse = Course(
            courseCode = "PROG3211",
            courseName = "Mobile Application Development",
            term = "Fall 2024",
            isCurrentTerm = true,
            progress = 75,
            caseStudies = listOf()
        )
        dbHelper.insertCourse(currentCourse)

        // Sample past term courses
        val pastCourse1 = Course(
            courseCode = "INFO3130",
            courseName = "Systems Analysis and Design",
            term = "Winter 2024",
            isCurrentTerm = false,
            progress = 100,
            caseStudies = listOf("Case Study One", "Case Study Two")
        )
        dbHelper.insertCourse(pastCourse1)

        val pastCourse2 = Course(
            courseCode = "MATH2210",
            courseName = "Discrete Mathematics",
            term = "Fall 2023",
            isCurrentTerm = false,
            progress = 100,
            caseStudies = listOf()
        )
        dbHelper.insertCourse(pastCourse2)
    }
}