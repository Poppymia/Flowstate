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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.flowstate.Components.AssignmentCard
import com.example.flowstate.Components.DateChip
import com.example.flowstate.data.FlowstateDatabaseHelper
import com.example.flowstate.models.Assignment
import com.example.flowstate.R
import com.example.flowstate.navigation.Screen
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun DashboardScreen(
    navController: NavController,
    dbHelper: FlowstateDatabaseHelper,
    modifier: Modifier = Modifier
) {
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
    val colorScheme = MaterialTheme.colorScheme

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

    val quoteViewModel = remember { com.example.flowstate.models.QuoteViewModel() }
    val quote = quoteViewModel.quote.value
    val author = quoteViewModel.author.value

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(colorScheme.background)
            .padding(16.dp)
    ) {

        Spacer(modifier = Modifier.height(8.dp))

        // Greeting
        Column {
            Text(
                text = stringResource(R.string.greeting, getTimeOfDay(), "Bob"),
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = colorScheme.onBackground
                )
            )
            Text(
                text = quote,
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = Color.DarkGray
                )
            )

            //api call for quote
            if (author.isNotEmpty()) {
                Text(
                    text = "- $author",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = Color.Gray
                    )
                )
            }
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
            text = stringResource(R.string.upcoming_recent_activity),
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold,
                color = colorScheme.onBackground
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        // ASSIGNMENTS LIST
        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(upcomingAssignments) { assignment ->
                AssignmentCard(
                    assignment = assignment,
                    onClick = {
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
                            stringResource(R.string.no_upcoming_assignments),
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

private fun getTimeOfDay(): String {
    return when (Calendar.getInstance().get(Calendar.HOUR_OF_DAY)) {
        in 0..11 -> "Morning"
        in 12..16 -> "Afternoon"
        else -> "Evening"
    }
}
