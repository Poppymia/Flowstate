package com.example.flowstate.models

import java.util.UUID

data class Course(
    val id: String = UUID.randomUUID().toString(),
    val courseCode: String,          // e.g., "INFO3130"
    val courseName: String,          // e.g., "Systems Analysis and Design"
    val term: String,                // e.g., "Fall 2024"
    val caseStudies: List<String> = emptyList(),  // List of case study names
    val isCurrentTerm: Boolean = false,
    val progress: Int = 0            // 0-100 for current term courses
)