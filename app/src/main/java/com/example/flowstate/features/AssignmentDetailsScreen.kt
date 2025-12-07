package com.example.flowstate.features

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Checkbox
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.flowstate.data.FlowstateDatabaseHelper
import com.example.flowstate.models.Subtask
import com.example.flowstate.models.Assignment
import com.example.flowstate.models.AssignmentDetailsViewModel
import com.example.flowstate.models.AssignmentRepository

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

//TODO: create assignment details edit screen and carry editable composable components over there

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AssignmentDetailsScreen(
    assignmentId: String,
    navController: NavController,
    dbHelper: FlowstateDatabaseHelper
) {
    // create repository and viewmodel
    val repo = remember { AssignmentRepository(dbHelper) }
    val viewModel = remember { AssignmentDetailsViewModel(repo, assignmentId) }

    // If assignment failed to load (null), show nothing for now (in future sned user error or a default add new assignment screen)
    val assignment = viewModel.assignment ?: return
    if (assignment == null) {
        LaunchedEffect(Unit) {
            navController.popBackStack()
        }
        return
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(assignment.title) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate("edit/$assignmentId") }
            ) {
                Icon(Icons.Default.Edit, contentDescription = "Edit Assignment")
            }
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            AssignmentCard(
                assignment = assignment,
                progress = viewModel.progress,
                onToggleSubtask = { subtask -> viewModel.toggleSubtask(subtask) },
                readOnly = false,
                onEditClick = { navController.navigate("edit/$assignmentId") }
            )
        }
    }
}

@Composable
fun AssignmentCard(
assignment: Assignment,
progress: Float,
onToggleSubtask: (Subtask) -> Unit,
readOnly: Boolean,
onEditClick: () -> Unit,

) {
    val cs = MaterialTheme.colorScheme

    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(26.dp))
            .background(cs.surfaceVariant)
            .padding(24.dp)
            .fillMaxWidth()
    ) {

        // Header
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                assignment.title,
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
            )

//            Icon(
//                Icons.Default.Edit,
//                contentDescription = "Edit",
//                modifier = Modifier
//                    .size(24.dp)
//                    .padding(4.dp),
//                tint = cs.onSurface
//            )
        }

        Spacer(Modifier.height(16.dp))

        // Progress
        LinearProgressIndicator(
            progress = progress,
            modifier = Modifier
                .height(14.dp)
                .clip(RoundedCornerShape(50))
                .fillMaxWidth()
        )

        Spacer(Modifier.height(20.dp))

        // Date
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.CalendarMonth, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text(
                text = remember(assignment.dueDate) {
                    val formatter = SimpleDateFormat("MMM dd, yyyy 'at' hh:mm a", Locale.getDefault())
                    // Change(d) the TimeZone to EST/EDT
                    formatter.timeZone = TimeZone.getTimeZone("America/New_York")
                    formatter.format(Date(assignment.dueDate))
                },
                style = MaterialTheme.typography.bodyLarge
            )
        }

        Spacer(Modifier.height(16.dp))

        // Course and Priority
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Course: ${assignment.courseId}")
            PriorityChip(label = when (assignment.priority) {
                0 -> "low"
                1 -> "medium"
                2 -> "high"
                else -> "unknown"
            })
        }

        Spacer(Modifier.height(20.dp))

        // Subtasks
        assignment.subtasks.forEach { subtask ->
            SubtaskRow(
                subtask = subtask,
                onToggle = { onToggleSubtask(subtask) },
                isReadOnly = readOnly
            )
            Spacer(Modifier.height(12.dp))
        }

        Spacer(Modifier.height(20.dp))

        // Notes
        Text(
            "Notes:",
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(Modifier.height(8.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp)
                .background(Color.White, RoundedCornerShape(16.dp))
                .padding(16.dp)
        ) {
            Text(
                assignment.notes ?: "No notes added.",
                color = Color.Gray
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryDropdown() {
    var expanded by remember { mutableStateOf(false) }
    var selected by remember { mutableStateOf("Programming") }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            readOnly = true,
            value = selected,
            onValueChange = {},
            label = null,
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
            modifier = Modifier
                .menuAnchor()
                .width(150.dp)
                .height(48.dp)
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            listOf("Programming", "Math", "Writing", "Design").forEach {
                DropdownMenuItem(
                    text = { Text(it) },
                    onClick = {
                        selected = it
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
fun PriorityChip(label: String) {
    Box(
        modifier = Modifier
            .background(Color(0xFFFFCACA), RoundedCornerShape(12.dp))
            .padding(horizontal = 16.dp, vertical = 6.dp)
    ) {
        Text(label, color = Color(0xFFB03030))
    }
}

@Composable
fun ChecklistRow(title: String, weight: Float, checked: Boolean = false) {
    Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {

        Row(verticalAlignment = Alignment.CenterVertically) {

            // Checkbox box
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(if (checked) Color(0xFF5567A8) else Color.White)
            )

            Spacer(Modifier.width(16.dp))
            Text(title, fontSize = 16.sp)
        }

        Text("${(weight * 100).toInt()}%")
    }

    Spacer(Modifier.height(12.dp))
}

@Composable
fun AttachmentPill(text: String) {
    Box(
        modifier = Modifier
            .background(Color(0xFFF3F7A7), RoundedCornerShape(10.dp))
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(text)
    }
}
@Composable
fun NotesBox() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp)
            .background(Color.White, RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Text("Notes...", color = Color.Gray)
    }
}

@Composable
fun SubtaskRow(
    subtask: Subtask,
    onToggle: () -> Unit,
    isReadOnly: Boolean
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                // The row is only clickable if it's NOT read-only
                enabled = !isReadOnly,
                onClick = onToggle
            )
    ) {
        Checkbox(
            checked = subtask.isChecked,
            // When checked state changes call the onToggle lambda
            onCheckedChange = { onToggle() },
            enabled = !isReadOnly
        )
        Spacer(Modifier.width(12.dp))
        Text(
            text = subtask.text,
            style = if (subtask.isChecked) {
                LocalTextStyle.current.copy(textDecoration = TextDecoration.LineThrough, color = Color.Gray)
            } else {
                LocalTextStyle.current
            }
        )
    }
}
