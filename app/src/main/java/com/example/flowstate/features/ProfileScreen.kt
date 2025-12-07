package com.example.flowstate.features

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import com.example.flowstate.data.FlowstateDatabaseHelper
import com.example.flowstate.models.Course
import com.example.flowstate.models.ProfileViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    navController: NavController,
    dbHelper: FlowstateDatabaseHelper,
    modifier: Modifier = Modifier
) {
    val viewModel = remember { ProfileViewModel(dbHelper) }
    val pastTermCourses by viewModel.pastTermCourses.collectAsState()
    val currentTermProgress by viewModel.currentTermProgress

    var isDarkTheme by remember { mutableStateOf(false) }
    var showMoreMenu by remember { mutableStateOf(false) }
    var showAddCourseDialog by remember { mutableStateOf(false) }

    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(scrollState)
    ) {
        // Top Bar with Menu Icon
        TopAppBar(
            title = { Text("") },
            navigationIcon = {
                IconButton(onClick = { showMoreMenu = !showMoreMenu }) {
                    Icon(
                        imageVector = Icons.Default.Menu,
                        contentDescription = "More options"
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color.Transparent
            )
        )

        // Profile Header
        ProfileHeader()

        Spacer(modifier = Modifier.height(24.dp))

        // Current Term Progress
        CurrentTermProgress(progress = currentTermProgress)

        Spacer(modifier = Modifier.height(32.dp))

        // Past Terms Section
        PastTermsSection(
            courses = pastTermCourses,
            onAddClick = { showAddCourseDialog = true },
            onDeleteCourse = { courseId -> viewModel.deleteCourse(courseId) }
        )

        Spacer(modifier = Modifier.height(24.dp))
    }

    // Add Course Dialog
    if (showAddCourseDialog) {
        AddCourseDialog(
            onDismiss = { showAddCourseDialog = false },
            onConfirm = { course ->
                viewModel.addCourse(course)
                showAddCourseDialog = false
            }
        )
    }
}

@Composable
fun AddCourseDialog(
    onDismiss: () -> Unit,
    onConfirm: (Course) -> Unit
) {
    var courseCode by remember { mutableStateOf("") }
    var courseName by remember { mutableStateOf("") }
    var term by remember { mutableStateOf("") }
    var isCurrentTerm by remember { mutableStateOf(false) }
    var caseStudyInput by remember { mutableStateOf("") }
    var caseStudies by remember { mutableStateOf(listOf<String>()) }
    var progress by remember { mutableStateOf(0) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = "Add Course/Term",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Course Code Input
                OutlinedTextField(
                    value = courseCode,
                    onValueChange = { courseCode = it },
                    label = { Text("Course Code") },
                    placeholder = { Text("e.g., INFO3130") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Course Name Input
                OutlinedTextField(
                    value = courseName,
                    onValueChange = { courseName = it },
                    label = { Text("Course Name") },
                    placeholder = { Text("e.g., Systems Analysis and Design") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Term Input
                OutlinedTextField(
                    value = term,
                    onValueChange = { term = it },
                    label = { Text("Term") },
                    placeholder = { Text("e.g., Fall 2024") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Current Term Checkbox
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = isCurrentTerm,
                        onCheckedChange = { isCurrentTerm = it }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("This is a current term course")
                }

                // Progress Slider (only show if current term)
                if (isCurrentTerm) {
                    Spacer(modifier = Modifier.height(12.dp))

                    Text("Course Progress: $progress%")
                    Slider(
                        value = progress.toFloat(),
                        onValueChange = { progress = it.toInt() },
                        valueRange = 0f..100f,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Case Studies Section
                Text(
                    text = "Case Studies (Optional)",
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.SemiBold
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Display existing case studies
                caseStudies.forEach { caseStudy ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "• $caseStudy",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.weight(1f)
                        )
                        IconButton(
                            onClick = {
                                caseStudies = caseStudies.filter { it != caseStudy }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Remove case study",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }

                // Add case study input
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = caseStudyInput,
                        onValueChange = { caseStudyInput = it },
                        label = { Text("Case Study Name") },
                        placeholder = { Text("e.g., Case Study One") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    IconButton(
                        onClick = {
                            if (caseStudyInput.isNotBlank()) {
                                caseStudies = caseStudies + caseStudyInput.trim()
                                caseStudyInput = ""
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add case study"
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            if (courseCode.isNotBlank() && courseName.isNotBlank() && term.isNotBlank()) {
                                val course = Course(
                                    courseCode = courseCode.trim(),
                                    courseName = courseName.trim(),
                                    term = term.trim(),
                                    caseStudies = caseStudies,
                                    isCurrentTerm = isCurrentTerm,
                                    progress = if (isCurrentTerm) progress else 0
                                )
                                onConfirm(course)
                            }
                        },
                        enabled = courseCode.isNotBlank() && courseName.isNotBlank() && term.isNotBlank()
                    ) {
                        Text("Add Course")
                    }
                }
            }
        }
    }
}

@Composable
fun ProfileHeader() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Profile Avatar
        Box(
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
                .background(Color(0xFFEC4899)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = "Profile Picture",
                modifier = Modifier.size(60.dp),
                tint = Color.White
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // User Name
        Text(
            text = "Hi, Bob!",
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        )
    }
}

@Composable
fun CurrentTermProgress(progress: Int) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
    ) {
        Text(
            text = "Current Term Progress",
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Progress Circle
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(
                progress = progress / 100f,
                modifier = Modifier.size(150.dp),
                strokeWidth = 12.dp,
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )

            Text(
                text = "$progress%",
                style = MaterialTheme.typography.displaySmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            )
        }
    }
}

@Composable
fun PastTermsSection(
    courses: List<Course>,
    onAddClick: () -> Unit,
    onDeleteCourse: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
    ) {
        Text(
            text = "Past Terms",
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Past Term Cards
        if (courses.isEmpty()) {
            Text(
                text = "No past terms added yet",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(vertical = 16.dp)
            )
        } else {
            courses.forEach { course ->
                PastTermCard(
                    course = course,
                    onDelete = { onDeleteCourse(course.id) }
                )
                Spacer(modifier = Modifier.height(12.dp))
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Add Button
        Button(
            onClick = onAddClick,
            modifier = Modifier
                .size(56.dp)
                .align(Alignment.End),
            shape = CircleShape,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFFBDE98)
            )
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Add Term",
                tint = Color.Black
            )
        }
    }
}

@Composable
fun PastTermCard(
    course: Course,
    onDelete: () -> Unit
) {
    var isExpanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { isExpanded = !isExpanded },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFB8DADE)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "${course.courseCode} - ${course.courseName}",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF1E3A8A)
                        )
                    )
                    Text(
                        text = course.term,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = Color(0xFF1E3A8A).copy(alpha = 0.7f)
                        )
                    )
                }

                // Delete button
                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete course",
                        tint = Color(0xFF1E3A8A),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            if (isExpanded && course.caseStudies.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))

                course.caseStudies.forEach { caseStudy ->
                    Text(
                        text = "• $caseStudy",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = Color(0xFF1E3A8A)
                        ),
                        modifier = Modifier.padding(start = 8.dp, top = 4.dp)
                    )
                }
            }
        }
    }
}

// More Menu Items
data class MenuItem(
    val icon: ImageVector,
    val title: String,
    val badge: Int? = null,
    val onClick: () -> Unit = {}
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoreMenuDrawer(
    isOpen: Boolean,
    onDismiss: () -> Unit,
    isDarkTheme: Boolean,
    onThemeToggle: (Boolean) -> Unit
) {
    if (isOpen) {
        ModalNavigationDrawer(
            drawerState = rememberDrawerState(DrawerValue.Open),
            drawerContent = {
                ModalDrawerSheet(
                    modifier = Modifier.fillMaxWidth(0.75f)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "More",
                            style = MaterialTheme.typography.headlineSmall.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            modifier = Modifier.padding(vertical = 16.dp)
                        )

                        HorizontalDivider()

                        Spacer(modifier = Modifier.height(16.dp))

                        // Notifications
                        MenuItemRow(
                            icon = Icons.Outlined.Notifications,
                            title = "Notifications",
                            badge = 24,
                            tint = Color(0xFFEC4899)
                        )

                        // Calendar
                        MenuItemRow(
                            icon = Icons.Outlined.CalendarMonth,
                            title = "Calendar",
                            tint = MaterialTheme.colorScheme.primary
                        )

                        // Courses
                        MenuItemRow(
                            icon = Icons.Outlined.FavoriteBorder,
                            title = "Courses",
                            tint = MaterialTheme.colorScheme.primary
                        )

                        // Trash
                        MenuItemRow(
                            icon = Icons.Outlined.Delete,
                            title = "Trash",
                            tint = MaterialTheme.colorScheme.primary
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        // Subjects Section
                        Text(
                            text = "Subjects",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.SemiBold
                            ),
                            modifier = Modifier.padding(vertical = 8.dp)
                        )

                        // Subject Labels (placeholder)
                        repeat(3) {
                            SubjectLabelRow()
                            Spacer(modifier = Modifier.height(8.dp))
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "Labels",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.SemiBold
                            ),
                            modifier = Modifier.padding(vertical = 8.dp)
                        )

                        // Labels (placeholder)
                        repeat(3) {
                            SubjectLabelRow()
                            Spacer(modifier = Modifier.height(8.dp))
                        }

                        Spacer(modifier = Modifier.weight(1f))

                        // Add button at bottom
                        FloatingActionButton(
                            onClick = { /* TODO: Add label/subject */ },
                            modifier = Modifier.align(Alignment.End),
                            containerColor = Color(0xFFFBDE98)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Add",
                                tint = Color.Black
                            )
                        }
                    }
                }
            },
            content = {}
        )
    }
}

@Composable
fun MenuItemRow(
    icon: ImageVector,
    title: String,
    badge: Int? = null,
    tint: Color = MaterialTheme.colorScheme.onSurface
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { /* TODO: Handle click */ }
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = tint,
            modifier = Modifier.size(24.dp)
        )

        Spacer(modifier = Modifier.width(16.dp))

        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f)
        )

        if (badge != null) {
            Badge(
                containerColor = tint
            ) {
                Text(
                    text = badge.toString(),
                    color = Color.White
                )
            }
        }
    }
}

@Composable
fun SubjectLabelRow() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { /* TODO: Handle click */ }
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Outlined.Folder,
            contentDescription = "Folder",
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(24.dp)
        )

        Spacer(modifier = Modifier.width(16.dp))

        Text(
            text = "Label",
            style = MaterialTheme.typography.bodyLarge
        )
    }
}