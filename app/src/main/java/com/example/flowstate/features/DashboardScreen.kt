package com.example.flowstate.features

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.flowstate.data.FlowstateDatabaseHelper
import com.example.flowstate.models.Assignment
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    navController: NavController,
    dbHelper: FlowstateDatabaseHelper,
    modifier: Modifier = Modifier
) {
    // Load assignments from database
    val assignments = remember {
        mutableStateOf(dbHelper.getAllAssignments())
    }

    // Get upcoming assignments (not completed, sorted by due date)
    val upcomingAssignments = remember(assignments.value) {
        assignments.value
            .filter { !it.isCompleted }
            .sortedBy { it.dueDate }
            .take(5)
    }

    // Current date info
    val calendar = Calendar.getInstance()
    val currentDate = calendar.time
    val selectedDate = remember { mutableStateOf(calendar.get(Calendar.DAY_OF_MONTH)) }

    // Generate week dates
    val weekDates = remember {
        val dates = mutableListOf<Pair<Int, String>>()
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
        for (i in 0..4) {
            dates.add(
                Pair(
                    calendar.get(Calendar.DAY_OF_MONTH),
                    SimpleDateFormat("EEE", Locale.getDefault()).format(calendar.time)
                )
            )
            calendar.add(Calendar.DAY_OF_MONTH, 1)
        }
        dates
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        // Top Bar with Menu
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { /* TODO: Open drawer */ }) {
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = "Menu"
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Greeting Section
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Good ${getTimeOfDay()}, Bob",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "The best way to get started is to quit talking and begin doing",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                )
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Week Date Selector
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
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

        // Upcoming/Recent/Activity Section Title
        Text(
            text = "Upcoming/Recent/Activity",
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Assignments List
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

            // Empty state
            if (upcomingAssignments.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No upcoming assignments",
                            style = MaterialTheme.typography.bodyLarge.copy(
                                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                            )
                        )
                    }
                }
            }
        }

        // FAB for adding new assignment
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.BottomEnd
        ) {
            FloatingActionButton(
                onClick = { /* TODO: Add new assignment */ },
                containerColor = Color(0xFFFBDE98),
                modifier = Modifier.padding(16.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Assignment",
                    tint = Color.Black
                )
            }
        }
    }
}

@Composable
fun DateChip(
    day: Int,
    dayName: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor = when {
        isSelected -> Color(0xFFEC4899) // Pink
        else -> MaterialTheme.colorScheme.surfaceVariant
    }

    val contentColor = when {
        isSelected -> Color.White
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }

    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(backgroundColor)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = day.toString(),
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                color = contentColor
            )
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = dayName,
            style = MaterialTheme.typography.bodySmall.copy(
                color = contentColor
            )
        )
    }
}

@Composable
fun AssignmentCard(
    assignment: Assignment,
    onClick: () -> Unit
) {
    // Determine card color based on course (you can enhance this)
    val cardColor = when (assignment.courseId.take(4).uppercase()) {
        "PROG" -> Color(0xFFE9D5FF) // Purple
        "INFO" -> Color(0xFFFDE68A) // Yellow
        "PSYC" -> Color(0xFFDDD6FE) // Light purple
        else -> Color(0xFFFCACAF) // Pink
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = cardColor
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Due:",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = Color.Black.copy(alpha = 0.7f)
                        )
                    )

                    Text(
                        text = formatDueDate(assignment.dueDate),
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = assignment.title,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                ),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = assignment.courseId,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = Color.Black.copy(alpha = 0.7f)
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
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

private fun formatDueDate(timestamp: Long): String {
    val formatter = SimpleDateFormat("MMM dd", Locale.getDefault())
    return formatter.format(Date(timestamp))
}

@Preview(showBackground = true)
@Composable
fun DashboardScreenPreview() {
    MaterialTheme {
        // Note: This preview won't work perfectly without a real NavController and DbHelper
        // but gives you an idea of the layout
    }
}

@Preview(showBackground = true, uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
fun DashboardScreenDarkPreview() {
    MaterialTheme {
        // Dark mode preview
    }
}