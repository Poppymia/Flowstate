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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    var isDarkTheme by remember { mutableStateOf(false) }
    var showMoreMenu by remember { mutableStateOf(false) }

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
        CurrentTermProgress()

        Spacer(modifier = Modifier.height(32.dp))

        // Past Terms Section
        PastTermsSection()

        Spacer(modifier = Modifier.height(24.dp))
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
fun CurrentTermProgress() {
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
                progress = 0.75f,
                modifier = Modifier.size(150.dp),
                strokeWidth = 12.dp,
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )

            Text(
                text = "75%",
                style = MaterialTheme.typography.displaySmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            )
        }
    }
}

@Composable
fun PastTermsSection() {
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
        PastTermCard(
            termName = "INFO - Systems Analysis and Design",
            expanded = false
        )

        Spacer(modifier = Modifier.height(12.dp))

        PastTermCard(
            termName = "INFO - Systems Analysis and Design",
            expanded = true,
            caseStudies = listOf("Case Study One", "Case Study Two")
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Add Button
        Button(
            onClick = { /* TODO: Add new term */ },
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
    termName: String,
    expanded: Boolean,
    caseStudies: List<String> = emptyList()
) {
    var isExpanded by remember { mutableStateOf(expanded) }

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
            Text(
                text = termName,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF1E3A8A)
                )
            )

            if (isExpanded && caseStudies.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))

                caseStudies.forEach { caseStudy ->
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

@Preview(showBackground = true)
@Composable
fun ProfileScreenPreview() {
    MaterialTheme {
        ProfileScreen(navController = rememberNavController())
    }
}

@Preview(showBackground = true, uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
fun ProfileScreenDarkPreview() {
    MaterialTheme {
        ProfileScreen(navController = rememberNavController())
    }
}