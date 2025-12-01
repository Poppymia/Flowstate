package com.example.flowstate.models

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class AssignmentDetailsViewModel(
    private val repo: AssignmentRepository,
    private val assignmentId: String
) : ViewModel() {

    var assignment by mutableStateOf<Assignment?>(null)
        private set

    init {
        load()
    }

    private fun load() {
        assignment = repo.getAssignment(assignmentId)
    }

    fun toggleSubtask(subtask: Subtask) {
        repo.toggleSubtask(subtask.id, !subtask.isChecked)
        // refresh from db
        load()
    }

    val progress: Float
        get() = assignment?.subtasks?.count { it.isChecked }?.toFloat().let { checked ->
            val total = assignment?.subtasks?.size ?: 1
            checked!! / total
        }
}
