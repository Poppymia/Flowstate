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

    // subtasks for the UI from the main assignment object
    var subtasks by mutableStateOf<List<Subtask>>(emptyList())


    init {
        load()
    }

    private fun load() {
        assignment = repo.getAssignment(assignmentId)
        // Also update the subtasks state when loading
        subtasks = assignment?.subtasks ?: emptyList()
    }

    fun toggleSubtask(subtask: Subtask) {
        repo.toggleSubtask(subtask.id, !subtask.isChecked)
        // refresh from db
        load()
    }

    fun updateAssignment(updatedAssignment: Assignment) {
        repo.updateAssignment(updatedAssignment)
        // after updating, refresh the vm's state from the database to ensure the UI is consistent
        load()
    }


    val progress: Float
        get() = assignment?.subtasks?.count { it.isChecked }?.toFloat().let { checked ->
            val total = assignment?.subtasks?.size ?: 1
            checked!! / total
        }
}
