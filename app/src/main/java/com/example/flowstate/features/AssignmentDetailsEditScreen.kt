package com.example.flowstate.features

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.flowstate.data.FlowstateDatabaseHelper
import com.example.flowstate.models.Assignment
import com.example.flowstate.models.AssignmentDetailsViewModel
import com.example.flowstate.models.AssignmentRepository
import com.example.flowstate.models.Subtask
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerDialog
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import java.util.Calendar
import java.util.TimeZone


import kotlin.text.format

@Composable
fun AssignmentDetailsEditScreen(
    assignmentId: String,
    navController: NavController,
    dbHelper: FlowstateDatabaseHelper
) {
    // Re-use repository and ViewModel to manage state
    val repo = remember { AssignmentRepository(dbHelper) }
    val viewModel = remember { AssignmentDetailsViewModel(repo, assignmentId) }
    val assignment = viewModel.assignment ?: return

    // Local state for UI edits, initialized from the ViewModel
    var title by remember { mutableStateOf(assignment.title) }
    var notes by remember { mutableStateOf(assignment.notes ?: "") }
    var selectedCourse by remember { mutableStateOf(assignment.courseId) }
    var priority by remember { mutableStateOf(assignment.priority) }
    var subtasks by remember { mutableStateOf(assignment.subtasks) }
    var dueDate by remember { mutableStateOf(assignment.dueDate) }


    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    // Create an updated assignment object from the local state
                    val updatedAssignment = assignment.copy(
                        title = title,
                        notes = notes,
                        courseId = selectedCourse,
                        priority = priority,
                        subtasks = subtasks,
                        dueDate = dueDate
                    )
                    // Call ViewModel to save changes
                    viewModel.updateAssignment(updatedAssignment)
                    // Navigate back to the details screen
                    navController.popBackStack()
                }
            ) {
                Icon(Icons.Default.Check, contentDescription = "Save Changes")
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            item {
                EditableAssignmentCard(
                    title = title,
                    onTitleChange = { title = it },
                    notes = notes,
                    onNotesChange = { notes = it },
                    selectedCourse = selectedCourse,
                    onCourseSelected = { selectedCourse = it },
                    priority = priority,
                    onPriorityChange = { priority = it },
                    subtasks = subtasks,
                    onSubtasksChange = { subtasks = it },
                    dueDate = dueDate,
                    onDueDateChange = { newDate -> dueDate = newDate },
                    onAddSubtask = {
                        subtasks = subtasks + Subtask(assignmentId = assignment.id, text = "New Subtask")
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditableAssignmentCard(
    title: String,
    onTitleChange: (String) -> Unit,
    notes: String,
    onNotesChange: (String) -> Unit,
    selectedCourse: String,
    onCourseSelected: (String) -> Unit,
    priority: Int,
    onPriorityChange: (Int) -> Unit,
    subtasks: List<Subtask>,
    onSubtasksChange: (List<Subtask>) -> Unit,
    onAddSubtask: () -> Unit,
    dueDate: Long,
    onDueDateChange: (Long) -> Unit

) {
    val cs = MaterialTheme.colorScheme
    // State for managing the DatePickerDialog's visibility
    val showDatePicker = remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(26.dp))
            .background(cs.surfaceVariant)
            .padding(24.dp)
            .fillMaxWidth()
    ) {
        //Editable Title
        OutlinedTextField(
            value = title,
            onValueChange = onTitleChange,
            label = { Text("Assignment Title") },
            modifier = Modifier.fillMaxWidth(),
            textStyle = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
            keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences, imeAction = ImeAction.Next),
            singleLine = true
        )

        Spacer(Modifier.height(20.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                // .clickable { ... } (managed by the composable for datetimepiecker)
                .padding(vertical = 8.dp)
        ) {
            Icon(Icons.Default.CalendarMonth, contentDescription = "Change due date")
            Spacer(Modifier.width(8.dp))
            DateTimePicker( // Use the new composable here
                initialDateTime = dueDate,
                onDateTimeSelected = onDueDateChange
            )
        }

        // DatePickerDialog when showDatePicker.value is true
        if (showDatePicker.value) {
            val datePickerState = rememberDatePickerState(initialSelectedDateMillis = dueDate)
            DatePickerDialog(
                onDismissRequest = { showDatePicker.value = false },
                confirmButton = {
                    TextButton(
                        onClick = {
                            // When confirmed, update the state with the new date
                            val selectedDate = datePickerState.selectedDateMillis ?: dueDate
                            onDueDateChange(selectedDate)
                            showDatePicker.value = false
                        }
                    ) {
                        Text("OK")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDatePicker.value = false }) {
                        Text("Cancel")
                    }
                }
            ) {
                DatePicker(state = datePickerState)
            }
        }

        Spacer(Modifier.height(16.dp))

        // Course Dropdown and Priority Dropdown
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            CourseDropdown(
                selectedCourse = selectedCourse,
                onCourseSelected = onCourseSelected,
                modifier = Modifier.weight(1f)
            )
            PriorityDropdown(
                selectedPriority = priority,
                onPriorityChange = onPriorityChange,
                modifier = Modifier.width(140.dp)
            )
        }

        Spacer(Modifier.height(20.dp))

        // Editable Subtasks
        Text("Subtasks", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(8.dp))

        subtasks.forEachIndexed { index, subtask ->
            EditableSubtaskRow(
                subtask = subtask,
                onTextChange = { newText ->
                    val newList = subtasks.toMutableList()
                    newList[index] = subtask.copy(text = newText)
                    onSubtasksChange(newList)
                },
                onWeightChange = { newWeightString ->
                    // Convert the input string to an integer, defaulting to 0 if it's empty or invalid
                    // there may still be some places where float is used instead of INT, also Date Time is still using LocalDate not string
                    val newWeightInt = newWeightString.toIntOrNull() ?: 0

                    val newList = subtasks.toMutableList()
                    newList[index] = subtask.copy(weight = newWeightInt)
                    onSubtasksChange(newList)
                },
                onDelete = {
                    onSubtasksChange(subtasks.filterNot { it.id == subtask.id })
                }
            )
            Spacer(Modifier.height(12.dp))
        }

        // Add Subtask Button
        TextButton(onClick = onAddSubtask) {
            Icon(Icons.Default.Add, contentDescription = "Add Subtask")
            Spacer(Modifier.width(4.dp))
            Text("Add Subtask")
        }

        Spacer(Modifier.height(20.dp))

        // Attachments Section
        Text("Attachments", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(8.dp))
        Button(onClick = { /* TODO: Open file picker */ }) {
            Text("Add Attachment")
        }

        Spacer(Modifier.height(20.dp))

        // Editable Notes
        Text("Notes", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = notes,
            onValueChange = onNotesChange,
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp),
            placeholder = { Text("Add your notes here...") }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateTimePicker(
    initialDateTime: Long,onDateTimeSelected: (Long) -> Unit
) {
    val showDatePicker = remember { mutableStateOf(false) }
    val showTimePicker = remember { mutableStateOf(false) }
    val selectedDate = remember { mutableStateOf(initialDateTime) }

    // Clickable Text to launch the process
    // Inside the DateTimePicker composable
    Text(
        text = remember(initialDateTime) {
            val formatter = SimpleDateFormat("MMM dd, yyyy 'at' hh:mm a", Locale.getDefault())
            formatter.timeZone = TimeZone.getTimeZone("America/New_York")
            formatter.format(Date(initialDateTime))
        },
        style = MaterialTheme.typography.bodyLarge,
        modifier = Modifier.clickable { showDatePicker.value = true }
    )

// Date Picker Dialog
    if (showDatePicker.value) {
        val datePickerState = rememberDatePickerState(initialSelectedDateMillis = initialDateTime)
        DatePickerDialog(
            onDismissRequest = { showDatePicker.value = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { utcMillis ->
                            // Calendar object to inspect the UTC timestamp
                            val utcCalendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
                            utcCalendar.timeInMillis = utcMillis

                            // New calendar instance for target EST timezone
                            val estCalendar = Calendar.getInstance(TimeZone.getTimeZone("America/New_York"))

                            // Manually transfer the year, month, and day from the UTC calendar to the EST calendar. This forces the date to be what the user actually picked.
                            estCalendar.set(
                                utcCalendar.get(Calendar.YEAR),
                                utcCalendar.get(Calendar.MONTH),
                                utcCalendar.get(Calendar.DAY_OF_MONTH),
                                // Set time to midnight in EST
                                0, 0, 0
                            )
                            estCalendar.set(Calendar.MILLISECOND, 0)

                            // Save the corrected timestamp
                            selectedDate.value = estCalendar.timeInMillis
                            showDatePicker.value = false
                            showTimePicker.value = true
                        } ?: run {
                            // If no date was selected, just dismiss
                            showDatePicker.value = false
                        }
                    }
                ) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker.value = false }) { Text("Cancel") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }


    // Time Picker Dialog
    if (showTimePicker.value) {
        val calendar =
            Calendar.getInstance().apply { timeInMillis = initialDateTime }
        val timePickerState = rememberTimePickerState(
            initialHour = calendar.get(Calendar.HOUR_OF_DAY),
            initialMinute = calendar.get(Calendar.MINUTE)
        )

        TimePickerDialog(
            onDismissRequest = { showTimePicker.value = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        val utcMillis = selectedDate.value
                        val estCalendar = Calendar.getInstance(TimeZone.getTimeZone("America/New_York"))
                        // Calendar object handles the conversion
                        estCalendar.timeInMillis = utcMillis

                        estCalendar.set(Calendar.HOUR_OF_DAY, timePickerState.hour)
                        estCalendar.set(Calendar.MINUTE, timePickerState.minute)
                        estCalendar.set(Calendar.SECOND, 0)
                        estCalendar.set(Calendar.MILLISECOND, 0)

                        onDateTimeSelected(estCalendar.timeInMillis)
                        showTimePicker.value = false
                    }
                ) { Text("OK") }
            },
            title = {
                Text(
                    text = "Select Due Time",
                    modifier = Modifier.padding(start = 24.dp, top = 24.dp, end = 24.dp)
                )
            }
        ) {
            TimePicker(state = timePickerState)
        }
    }
}

@Composable
fun EditableSubtaskRow(
    subtask: Subtask,
    onTextChange: (String) -> Unit,
    onWeightChange: (String) -> Unit,
    onDelete: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Subtask Text Input
        OutlinedTextField(
            value = subtask.text,
            onValueChange = onTextChange,
            modifier = Modifier.weight(1f),
            keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences, imeAction = ImeAction.Done),
            singleLine = true,
            label = { Text("Subtask Name") }
        )

        // Subtask Weight Input
        OutlinedTextField(
            value = if (subtask.weight > 0) subtask.weight.toString() else "",
            onValueChange = onWeightChange,
            label = { Text("Wgt %") },
            modifier = Modifier.width(80.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Done),
            singleLine = true,
            textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Center),
            placeholder = {Text("0")}
        )

        IconButton(onClick = onDelete) {
            Icon(Icons.Default.Delete, contentDescription = "Delete Subtask")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CourseDropdown(
    selectedCourse: String,
    onCourseSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    val courses = remember { mutableStateListOf("Programming", "Math", "Writing", "Design") }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = modifier
    ) {
        OutlinedTextField(
            readOnly = true,
            value = selectedCourse,
            onValueChange = {},
            label = { Text("Course") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
            modifier = Modifier.menuAnchor()
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            courses.forEach { course ->
                DropdownMenuItem(
                    text = { Text(course) },
                    onClick = {
                        onCourseSelected(course)
                        expanded = false
                    }
                )
            }
            DropdownMenuItem(
                text = { Text("Add New Course...", fontWeight = FontWeight.Bold) },
                onClick = { /* TODO: Show a dialog to add a new course */
                    expanded = false
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PriorityDropdown(
    selectedPriority: Int,
    onPriorityChange: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    val priorityMap = mapOf(0 to "Low", 1 to "Medium", 2 to "High")

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = modifier
    ) {
        OutlinedTextField(
            readOnly = true,
            value = priorityMap[selectedPriority] ?: "Unknown",
            onValueChange = {},
            label = { Text("Priority") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
            modifier = Modifier.menuAnchor()
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            priorityMap.forEach { (priorityValue, priorityName) ->
                DropdownMenuItem(
                    text = { Text(priorityName) },
                    onClick = {
                        onPriorityChange(priorityValue)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AssignmentDetailsEditScreenPreview() {
    val sampleAssignment = Assignment(
        id = "1",
        title = "Finish Compose UI",
        dueDate = System.currentTimeMillis(),
        courseId = "Programming",
        priority = 2,
        notes = "Remember to handle all the edge cases.",
        subtasks = listOf(
            Subtask(id = "s1", assignmentId = "1", text = "Create UI layout"),
            Subtask(id = "s2", assignmentId = "1", text = "Implement ViewModel")
            //TODO add placeholder weights for preview
        ),
        progress = 0
    )

    // Local state for preview
    var title by remember { mutableStateOf(sampleAssignment.title) }
    var notes by remember { mutableStateOf(sampleAssignment.notes ?: "") }
    var selectedCourse by remember { mutableStateOf(sampleAssignment.courseId) }
    var priority by remember { mutableStateOf(sampleAssignment.priority) }
    var subtasks by remember { mutableStateOf(sampleAssignment.subtasks) }
    var selectedDueDate by remember { mutableStateOf(sampleAssignment.dueDate) }


    Scaffold { padding ->
        Column(Modifier
            .padding(padding)
            .padding(16.dp)) {
            EditableAssignmentCard(
                title = title,
                onTitleChange = { title = it },
                notes = notes,
                onNotesChange = { notes = it },
                selectedCourse = selectedCourse,
                onCourseSelected = { selectedCourse = it },
                priority = priority,
                onPriorityChange = { priority = it },
                subtasks = subtasks,
                onSubtasksChange = { subtasks = it },
                onAddSubtask = { subtasks = subtasks + Subtask(assignmentId = "1", text = "New Subtask") },
                dueDate = selectedDueDate,
                onDueDateChange = { newDate -> selectedDueDate = newDate }
            )
        }
    }
}
