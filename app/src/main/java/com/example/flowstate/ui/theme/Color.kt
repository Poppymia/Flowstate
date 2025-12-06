package com.example.flowstate.ui.theme

import androidx.compose.ui.graphics.Color

// Original Material Theme Colors
val Purple80 = Color(0xFFD0BCFF)
val PurpleGrey80 = Color(0xFFCCC2DC)
val Pink80 = Color(0xFFEFB8C8)

val Purple40 = Color(0xFF6650a4)
val PurpleGrey40 = Color(0xFF625b71)
val Pink40 = Color(0xFF7D5260)

// Flowstate App Colors (from Figma Design)

// Primary Colors
val PrimaryPink = Color(0xFFEC4899)  // Used for selected items, profile avatar
val PrimaryBlue = Color(0xFF3B82F6)  // Used for text, headers

// Accent Colors
val AccentYellow = Color(0xFFFBDE98)  // Used for FABs, add buttons
val AccentGold = Color(0xFFF59E0B)    // Used for highlights

// Card Background Colors
val CardPurple = Color(0xFFE9D5FF)    // Programming assignments
val CardYellow = Color(0xFFFDE68A)    // Info/Systems assignments
val CardLightPurple = Color(0xFFDDD6FE)  // Psychology assignments
val CardPink = Color(0xFFFCACAF)      // Default assignment color
val CardBlue = Color(0xFFB8DADE)      // Past terms
val CardRed = Color(0xFFFFCACA)       // High priority items

// Text Colors
val TextPrimary = Color(0xFF1E3A8A)   // Dark blue for primary text
val TextSecondary = Color(0xFF64748B)  // Gray for secondary text
val TextOnCard = Color(0xFF1E293B)    // Text on colored cards

// Background Colors
val BackgroundLight = Color(0xFFFAFAFA)
val BackgroundDark = Color(0xFF0F172A)

// Surface Colors
val SurfaceLight = Color(0xFFFFFFFF)
val SurfaceDark = Color(0xFF1E293B)

// Priority Colors
val PriorityHigh = Color(0xFFB03030)
val PriorityMedium = Color(0xFFF59E0B)
val PriorityLow = Color(0xFF10B981)

// Status Colors
val StatusCompleted = Color(0xFF10B981)
val StatusInProgress = Color(0xFF3B82F6)
val StatusOverdue = Color(0xFFEF4444)
val StatusPending = Color(0xFF8B5CF6)

// Course-Specific Colors (for easy extension)
val CoursePROG = Color(0xFFE9D5FF)   // Purple for Programming
val CourseINFO = Color(0xFFFDE68A)   // Yellow for Information Systems
val CoursePSYC = Color(0xFFDDD6FE)   // Light Purple for Psychology
val CourseMATH = Color(0xFFBFDBFE)   // Light Blue for Math
val CourseDESIGN = Color(0xFFFBCAF9) // Pink for Design

// Helper function to get course color
fun getCourseColor(courseCode: String): Color {
    return when (courseCode.take(4).uppercase()) {
        "PROG" -> CoursePROG
        "INFO" -> CourseINFO
        "PSYC" -> CoursePSYC
        "MATH" -> CourseMATH
        "DESI" -> CourseDESIGN
        else -> CardPink
    }
}

// Helper function to get priority color
fun getPriorityColor(priority: Int): Color {
    return when (priority) {
        0 -> PriorityLow
        1 -> PriorityMedium
        2 -> PriorityHigh
        else -> PriorityMedium
    }
}

// Helper function to get status color
fun getStatusColor(isCompleted: Boolean, isOverdue: Boolean): Color {
    return when {
        isCompleted -> StatusCompleted
        isOverdue -> StatusOverdue
        else -> StatusPending
    }
}