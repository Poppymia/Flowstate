package com.example.flowstate.models

import android.content.ContentValues
import com.example.flowstate.data.FlowstateDatabaseHelper

class AssignmentRepository(private val dbHelper: FlowstateDatabaseHelper) {

    fun getAssignment(id: String): Assignment? {
        val db = dbHelper.readableDatabase

        val cursor = db.rawQuery(
            "SELECT * FROM assignments WHERE id = ?",
            arrayOf(id)
        )

        if (!cursor.moveToFirst()) {
            cursor.close()
            return null
        }

        val assignment = Assignment(
            id = cursor.getString(cursor.getColumnIndexOrThrow("id")),
            title = cursor.getString(cursor.getColumnIndexOrThrow("title")),
            courseId = cursor.getString(cursor.getColumnIndexOrThrow("courseId")),
            dueDate = cursor.getLong(cursor.getColumnIndexOrThrow("dueDate")),
            priority = cursor.getInt(cursor.getColumnIndexOrThrow("priority")),
            notes = cursor.getString(cursor.getColumnIndexOrThrow("notes")),
            estimatedTimeMinutes = cursor.getInt(cursor.getColumnIndexOrThrow("estimatedTimeMinutes")),
            expectedGrade = cursor.getInt(cursor.getColumnIndexOrThrow("expectedGrade")),
            actualGrade = cursor.getInt(cursor.getColumnIndexOrThrow("actualGrade")),
            progress = cursor.getInt(cursor.getColumnIndexOrThrow("progress")),
            isCompleted = cursor.getInt(cursor.getColumnIndexOrThrow("isCompleted")) == 1,
            subtasks = getSubtasksByAssignmentId(id)
        )

        cursor.close()
        return assignment
    }

    fun getSubtasksByAssignmentId(assignmentId: String): List<Subtask> {
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery(
            "SELECT * FROM subtasks WHERE assignmentId = ?",
            arrayOf(assignmentId)
        )

        val subtasks = mutableListOf<Subtask>()
        while (cursor.moveToNext()) {
            subtasks.add(
                Subtask(
                    id = cursor.getString(cursor.getColumnIndexOrThrow("id")),
                    assignmentId = assignmentId,
                    text = cursor.getString(cursor.getColumnIndexOrThrow("text")),
                    isChecked = cursor.getInt(cursor.getColumnIndexOrThrow("isChecked")) == 1,
                    weight = cursor.getInt(cursor.getColumnIndexOrThrow("weight"))
                )
            )
        }

        cursor.close()
        return subtasks
    }

    fun updateAssignment(assignment: Assignment) {
        val db = dbHelper.writableDatabase

        // Begin a database transaction
        db.beginTransaction()
        try {
            //Update the main assignment details in the 'assignments' table
            val assignmentValues = ContentValues().apply {
                put("title", assignment.title)
                put("courseId", assignment.courseId)
                put("priority", assignment.priority)
                put("notes", assignment.notes)
                put("dueDate", assignment.dueDate)
            }
            db.update("assignments", assignmentValues, "id = ?", arrayOf(assignment.id))

            // Clear all existing subtasks for this assignment to handle deletions
            db.delete("subtasks", "assignmentId = ?", arrayOf(assignment.id))

            // Insert the new/updated list of subtasks
            assignment.subtasks.forEach { subtask ->
                val subtaskValues = ContentValues().apply {
                    // Use existing ID to be safe
                    put("id", subtask.id)
                    put("assignmentId", assignment.id)
                    put("text", subtask.text)
                    put("isChecked", if (subtask.isChecked) 1 else 0)
                    put("weight", subtask.weight)
                }
                db.insert("subtasks", null, subtaskValues)
            }

            // Mark the transaction as successful
            db.setTransactionSuccessful()
        } finally {
            // End the transaction. If it wasn't successful, changes will be rolled back.
            db.endTransaction()
        }
    }



    fun toggleSubtask(subtaskId: String, newValue: Boolean) {
        val db = dbHelper.writableDatabase

        db.execSQL(
            "UPDATE subtasks SET isChecked = ? WHERE id = ?",
            arrayOf(if (newValue) 1 else 0, subtaskId)
        )
    }
}
