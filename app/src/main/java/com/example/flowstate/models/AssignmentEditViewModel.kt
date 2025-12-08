package com.example.flowstate.models

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class AssignmentEditViewModel(
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

    fun save(updated: Assignment) {
        repo.updateAssignment(updated)
        load()
    }

    fun delete() {
        repo.deleteAssignment(assignmentId)
    }

    fun updateAssignment(updatedAssignment: Assignment) {
        repo.updateAssignment(updatedAssignment)
        // after updating, refresh the vm's state from the database to ensure the UI is consistent
        load()
    }
}
