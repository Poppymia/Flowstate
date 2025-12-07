/*
package com.example.flowstate.models

class AssignmentAddViewModel {

}
*/package com.example.flowstate.features.add

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.flowstate.data.FlowstateDatabaseHelper
import com.example.flowstate.models.Assignment
import java.lang.NumberFormatException
import java.util.*

// Simple ViewModel for the Add Assignment screen.
// Keeps fields as mutableStateOf so Jetpack Compose can observe them directly.
class AssignmentAddViewModel(private val db: FlowstateDatabaseHelper) : ViewModel() {

    // Form fields (bind these to OutlinedTextField values in UI)
    val title = mutableStateOf("")
    val courseId = mutableStateOf("")
    // store due date as millis; default to now + 1 day
    val dueDateMillis = mutableStateOf(System.currentTimeMillis() + 24L * 60 * 60 * 1000)
    val priority = mutableStateOf(1)

    // Optional numeric inputs kept as strings for easier text binding/parsing
    val estimatedMinutesInput = mutableStateOf("")   // parse to Int?
    val expectedGradeInput = mutableStateOf("")      // parse to Int?
    val notes = mutableStateOf("")

    // UI feedback
    val errorMessage = mutableStateOf<String?>(null)
    val isSaving = mutableStateOf(false)

    /**
     * Validate inputs and insert a new Assignment into the DB.
     * Returns true if save was successful, false otherwise.
     */
    fun saveAssignment(): Boolean {
        // Basic validation
        if (title.value.trim().isEmpty()) {
            errorMessage.value = "Please enter a title."
            return false
        }

        if (courseId.value.trim().isEmpty()) {
            errorMessage.value = "Please enter a course ID."
            return false
        }

        // Due date should be a positive millisecond value (optionally require future date)
        if (dueDateMillis.value <= 0L) {
            errorMessage.value = "Please enter a valid due date."
            return false
        }

        // Priority validation (example: allowed 0..3)
        if (priority.value < 0 || priority.value > 10) {
            errorMessage.value = "Priority must be between 0 and 10."
            return false
        }

        // Parse optional numeric values (safely)
        val estimatedMinutes: Int? = try {
            if (estimatedMinutesInput.value.trim().isEmpty()) null
            else estimatedMinutesInput.value.trim().toInt()
        } catch (e: NumberFormatException) {
            errorMessage.value = "Estimated minutes must be a number."
            return false
        }

        val expectedGrade: Int? = try {
            if (expectedGradeInput.value.trim().isEmpty()) null
            else expectedGradeInput.value.trim().toInt()
        } catch (e: NumberFormatException) {
            errorMessage.value = "Expected grade must be a number."
            return false
        }

        // Passed validation — build Assignment and insert
        isSaving.value = true
        try {
            val assignment = Assignment(
                title = title.value.trim(),
                courseId = courseId.value.trim(),
                dueDate = dueDateMillis.value,
                priority = priority.value,
                progress = 0, // new assignment starts at 0%
                notes = notes.value.trim().ifEmpty { null },
                estimatedTimeMinutes = estimatedMinutes,
                expectedGrade = expectedGrade,
                actualGrade = null,
                isCompleted = false
                // color will use default from Assignment model if not provided
            )

            db.insertAssignment(assignment)

            // success — clear any error and optionally reset fields
            errorMessage.value = null
            return true
        } catch (t: Throwable) {
            // DB error
            errorMessage.value = "Failed to save assignment: ${t.message ?: "unknown error"}"
            return false
        } finally {
            isSaving.value = false
        }
    }

    // Helper: set due date from a parsed millis value (UI can call this)
    fun setDueDateMillis(millis: Long) {
        dueDateMillis.value = millis
    }
}
