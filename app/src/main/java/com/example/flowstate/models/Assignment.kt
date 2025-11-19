package com.example.flowstate.models

import java.util.UUID

data class Assignment (
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val courseId: String,
    val dueDate: Long,
    val priority: Int,
    val progress: Int,
    val notes: String? = null,
    val estimatedTimeMinutes: Int? = null,
    val expectedGrade: Int? = null,
    val actualGrade: Int? = null,
    val subtasks: List<Subtask> = emptyList(),
    val isCompleted: Boolean = false
)
