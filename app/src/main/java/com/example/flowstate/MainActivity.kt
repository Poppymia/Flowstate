/*
package com.example.flowstate

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.flowstate.features.DashboardScreen
import com.example.flowstate.ui.theme.FlowstateTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FlowstateTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    DashboardScreen(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}
*/
package com.example.flowstate

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
//import androidx.navigation.compose.navArgument
import androidx.navigation.compose.rememberNavController
import com.example.flowstate.data.FlowstateDatabaseHelper
import com.example.flowstate.features.DashboardScreen
import com.example.flowstate.features.AssignmentDetailsScreen
import com.example.flowstate.ui.theme.FlowstateTheme
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.example.flowstate.features.AssignmentDetailsEditScreen
import com.example.flowstate.features.AssignmentsListScreen
import com.example.flowstate.models.AssignmentsListViewModel

/*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {

            FlowstateTheme {
                val navController = rememberNavController()
                val dbHelper = FlowstateDatabaseHelper(this)

                // Seed sample data on first launch
                if (!dbHelper.hasAssignments()) {
                    val sampleId = "test-assignment-1"

                    // Insert assignment
                    dbHelper.insertAssignment(
                        id = sampleId,
                        title = "PROG3211 Final Project",
                        courseId = "PROG3211",
                        dueDate = System.currentTimeMillis() + (86400000 * 3), // +3 days
                        priority = 2,
                        progress = 0,
                        notes = "Start working on UI & database"
                    )

                    // Insert subtasks
                    dbHelper.insertSubtask(
                        id = "st-1",
                        assignmentId = sampleId,
                        text = "Design UI screens",
                        isChecked = false
                    )

                    dbHelper.insertSubtask(
                        id = "st-2",
                        assignmentId = sampleId,
                        text = "Implement local DB",
                        isChecked = false
                    )

                    dbHelper.insertSubtask(
                        id = "st-3",
                        assignmentId = sampleId,
                        text = "Test navigation",
                        isChecked = false
                    )
                }

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    */
/*NavHost(
                        navController = navController,
                        startDestination = "dashboard",
                        modifier = Modifier.padding(innerPadding)
                    ) {

                        // DASHBOARD SCREEN
                        composable("dashboard") {
                            DashboardScreen(
                                navController = navController,
                                dbHelper = dbHelper
                            )
                        }

                        // ASSIGNMENT DETAILS SCREEN
                        composable(
                            route = "details/{id}",
                            arguments = listOf(navArgument("id") { type = NavType.StringType })
                        ) { backStackEntry ->
                            val assignmentId =
                                backStackEntry.arguments?.getString("id") ?: return@composable

                            AssignmentDetailsScreen(
                                assignmentId = assignmentId,
                                navController = navController,
                                dbHelper = dbHelper
                            )
                        }

                        // In your NavHost
                        composable("edit/{assignmentId}") { backStackEntry ->
                            val assignmentId = backStackEntry.arguments?.getString("assignmentId")
                            if (assignmentId != null) {
                                AssignmentDetailsEditScreen(
                                    assignmentId = assignmentId,
                                    navController = navController,
                                    dbHelper = dbHelper
                                )
                            }
                        }


                    }*//*


                    NavHost(
                        navController = navController,
                        startDestination = "dashboard",
                        modifier = Modifier.padding(innerPadding)
                    ) {

                        composable("dashboard") {
                            DashboardScreen(
                                navController = navController,
                                dbHelper = dbHelper
                            )
                        }

                        composable(
                            route = "details/{id}",
                            arguments = listOf(navArgument("id") { type = NavType.StringType })
                        ) { backStackEntry ->
                            val assignmentId = backStackEntry.arguments?.getString("id") ?: return@composable

                            AssignmentDetailsScreen(
                                assignmentId = assignmentId,
                                navController = navController,
                                dbHelper = dbHelper
                            )
                        }

                        // NEW: Assignments List Screen
                        composable("assignments") {
                            AssignmentsListScreen(assignmentsListViewModel)
                        }
                    }
                }
            }
        }
    }
}
*/

class MainActivity : ComponentActivity() {

    private lateinit var assignmentsListViewModel: AssignmentsListViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val dbHelper = FlowstateDatabaseHelper(this)

        // ✔ Create ViewModel OUTSIDE Compose
        assignmentsListViewModel = AssignmentsListViewModel(dbHelper)

        // ✔ Seed sample data BEFORE Compose
        if (!dbHelper.hasAssignments()) {
            val sampleId = "test-assignment-1"

            dbHelper.insertAssignment(
                id = sampleId,
                title = "PROG3211 Final Project",
                courseId = "PROG3211",
                dueDate = System.currentTimeMillis() + (86400000 * 3),
                priority = 2,
                progress = 0,
                notes = "Start working on UI & database"
            )

            dbHelper.insertSubtask("st-1", sampleId, "Design UI screens", false)
            dbHelper.insertSubtask("st-2", sampleId, "Implement local DB", false)
            dbHelper.insertSubtask("st-3", sampleId, "Test navigation", false)
        }

        setContent {
            FlowstateTheme {
                val navController = rememberNavController()

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = "dashboard",
                        modifier = Modifier.padding(innerPadding)
                    ) {

                        composable("dashboard") {
                            DashboardScreen(
                                navController = navController,
                                dbHelper = dbHelper
                            )
                        }

                        composable(
                            route = "details/{id}",
                            arguments = listOf(navArgument("id") { type = NavType.StringType })
                        ) { backStackEntry ->
                            val assignmentId =
                                backStackEntry.arguments?.getString("id") ?: return@composable

                            AssignmentDetailsScreen(
                                assignmentId = assignmentId,
                                navController = navController,
                                dbHelper = dbHelper
                            )
                        }

                        composable("assignments") {
                            // ✔ Pass already-created ViewModel
                            AssignmentsListScreen(assignmentsListViewModel)
                        }
                    }
                }
            }
        }
    }
}
