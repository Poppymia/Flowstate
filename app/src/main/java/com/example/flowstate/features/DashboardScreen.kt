package com.example.flowstate.features

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.flowstate.Components.AssignmentCard
import com.example.flowstate.Components.DateChip
import com.example.flowstate.data.FlowstateDatabaseHelper
import com.example.flowstate.models.Assignment
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun DashboardScreen(
    navController: NavController,
    dbHelper: FlowstateDatabaseHelper,
    modifier: Modifier = Modifier
) {
    // Load assignments once
    val assignments = remember { dbHelper.getAllAssignments() }

    DashboardContent(
        assignments = assignments,
        navController = navController,
        modifier = modifier
    )
}

@Composable
fun DashboardContent(
    assignments: List<Assignment>,
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val upcomingAssignments = assignments
        .filter { !it.isCompleted }
        .sortedBy { it.dueDate }
        .take(5)

    val calendar = Calendar.getInstance()
    val selectedDate = remember { mutableStateOf(calendar.get(Calendar.DAY_OF_MONTH)) }

    val weekDates = remember {
        val dates = mutableListOf<Pair<Int, String>>()
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
        repeat(5) {
            dates.add(
                calendar.get(Calendar.DAY_OF_MONTH) to
                        SimpleDateFormat("EEE", Locale.getDefault()).format(calendar.time)
            )
            calendar.add(Calendar.DAY_OF_MONTH, 1)
        }
        dates
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp)
    ) {

        Spacer(modifier = Modifier.height(8.dp))

        // Greeting
        Column {
            Text(
                text = "Good ${getTimeOfDay()}, Bob",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            )
            Text(
                text = "The best way to get started is to quit talking and begin doing",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = Color.DarkGray
                )
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // WEEK ROW
        LazyRow(horizontalArrangement = Arrangement.spacedBy(26.dp)) {
            items(weekDates) { (day, dayName) ->
                DateChip(
                    day = day,
                    dayName = dayName,
                    isSelected = day == selectedDate.value,
                    onClick = { selectedDate.value = day }
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "Upcoming / Recent / Activity",
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        // ASSIGNMENT LIST
        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(upcomingAssignments) { assignment ->
                AssignmentCard(
                    assignment = assignment,
                    onClick = {
                        //navigate to assignment card
                        navController.navigate("details/${assignment.id}")
                    }
                )
            }

            if (upcomingAssignments.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "No upcoming assignments",
                            color = Color.Gray
                        )
                    }
                }
            }
        }

        // FAB
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.BottomEnd
        ) {
            FloatingActionButton(
                onClick = {
                    // navigate to add assignment screen
                    navController.navigate("AssignmentDetailsScreen")
                },
                containerColor = Color(0xFFFBDE98),
                modifier = Modifier.padding(16.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Assignment", tint = Color.Black)
            }
        }
    }
}

private fun getTimeOfDay(): String {
    return when (Calendar.getInstance().get(Calendar.HOUR_OF_DAY)) {
        in 0..11 -> "Morning"
        in 12..16 -> "Afternoon"
        else -> "Evening"
    }
}
