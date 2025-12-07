package com.example.flowstate.navigation

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Assignment
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.flowstate.data.FlowstateDatabaseHelper
import com.example.flowstate.features.*
import com.example.flowstate.models.AssignmentsListViewModel

// Sealed class for type-safe navigation routes
sealed class Screen(val route: String) {
    object Dashboard : Screen("dashboard")
    object Calendar : Screen("calendar")
    object Assignments : Screen("assignments")
    object Profile : Screen("profile")
    object AssignmentDetails : Screen("details/{id}") {
        fun createRoute(id: String) = "details/$id"
    }
    object AssignmentEdit : Screen("edit/{assignmentId}") {
        fun createRoute(assignmentId: String) = "edit/$assignmentId"
    }
}

// Bottom navigation item data class
data class BottomNavItem(
    val screen: Screen,
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
)

// List of bottom navigation items
val bottomNavItems = listOf(
    BottomNavItem(
        screen = Screen.Dashboard,
        label = "Home",
        selectedIcon = Icons.Filled.Home,
        unselectedIcon = Icons.Outlined.Home
    ),
    BottomNavItem(
        screen = Screen.Calendar,
        label = "Calendar",
        selectedIcon = Icons.Filled.CalendarMonth,
        unselectedIcon = Icons.Outlined.CalendarMonth
    ),
    BottomNavItem(
        screen = Screen.Assignments,
        label = "Assignments",
        selectedIcon = Icons.Filled.Assignment,
        unselectedIcon = Icons.Outlined.Assignment
    ),
    BottomNavItem(
        screen = Screen.Profile,
        label = "Profile",
        selectedIcon = Icons.Filled.Person,
        unselectedIcon = Icons.Outlined.Person
    )
)

@Composable
fun FlowstateApp(
    dbHelper: FlowstateDatabaseHelper,
    assignmentsListViewModel: AssignmentsListViewModel
) {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = {
            BottomNavigationBar(navController = navController)
        }
    ) { innerPadding ->
        NavigationGraph(
            navController = navController,
            dbHelper = dbHelper,
            assignmentsListViewModel = assignmentsListViewModel,
            modifier = Modifier.padding(innerPadding)
        )
    }
}

@Composable
fun BottomNavigationBar(navController: NavHostController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    // Only show bottom bar on main screens (not on detail/edit screens)
    val showBottomBar = currentDestination?.route in listOf(
        Screen.Dashboard.route,
        Screen.Calendar.route,
        Screen.Assignments.route,
        Screen.Profile.route
    )

    if (showBottomBar) {
        NavigationBar(
            containerColor = MaterialTheme.colorScheme.surface,
            tonalElevation = 8.dp
        ) {
            bottomNavItems.forEach { item ->
                val selected = currentDestination?.hierarchy?.any {
                    it.route == item.screen.route
                } == true

                NavigationBarItem(
                    selected = selected,
                    onClick = {
                        navController.navigate(item.screen.route) {
                            // Pop up to the start destination of the graph to
                            // avoid building up a large stack of destinations
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            // Avoid multiple copies of the same destination
                            launchSingleTop = true
                            // Restore state when reselecting a previously selected item
                            restoreState = true
                        }
                    },
                    icon = {
                        Icon(
                            imageVector = if (selected) item.selectedIcon else item.unselectedIcon,
                            contentDescription = item.label
                        )
                    },
                    label = { Text(item.label) },
                    alwaysShowLabel = true
                )
            }
        }
    }
}

@Composable
fun NavigationGraph(
    navController: NavHostController,
    dbHelper: FlowstateDatabaseHelper,
    assignmentsListViewModel: AssignmentsListViewModel,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Dashboard.route,
        modifier = modifier
    ) {
        // Dashboard Screen
        composable(Screen.Dashboard.route) {
            DashboardScreen(
                navController = navController,
                dbHelper = dbHelper
            )
        }

        // Calendar Screen (placeholder for now)
        composable(Screen.Calendar.route) {
            CalendarScreen()
        }

        // Assignments List Screen
        composable(Screen.Assignments.route) {
            AssignmentsListScreen(viewModel = assignmentsListViewModel)
        }

        // Profile Screen - NOW WITH DATABASE HELPER
        composable(Screen.Profile.route) {
            ProfileScreen(
                navController = navController,
                dbHelper = dbHelper
            )
        }

        // Assignment Details Screen
        composable(
            route = Screen.AssignmentDetails.route,
            arguments = listOf(navArgument("id") { type = NavType.StringType })
        ) { backStackEntry ->
            val assignmentId = backStackEntry.arguments?.getString("id") ?: return@composable
            AssignmentDetailsScreen(
                assignmentId = assignmentId,
                navController = navController,
                dbHelper = dbHelper
            )
        }

        // Assignment Edit Screen
        composable(
            route = Screen.AssignmentEdit.route,
            arguments = listOf(navArgument("assignmentId") { type = NavType.StringType })
        ) { backStackEntry ->
            val assignmentId = backStackEntry.arguments?.getString("assignmentId") ?: return@composable
            AssignmentDetailsEditScreen(
                assignmentId = assignmentId,
                navController = navController,
                dbHelper = dbHelper
            )
        }
    }
}

// Placeholder Calendar Screen
@Composable
fun CalendarScreen() {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Calendar View Coming Soon",
                style = MaterialTheme.typography.headlineMedium
            )
        }
    }
}