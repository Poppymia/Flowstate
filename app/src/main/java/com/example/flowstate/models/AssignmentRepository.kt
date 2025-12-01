package com.example.flowstate.models

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
                    isChecked = cursor.getInt(cursor.getColumnIndexOrThrow("isChecked")) == 1
                )
            )
        }

        cursor.close()
        return subtasks
    }

    fun toggleSubtask(subtaskId: String, newValue: Boolean) {
        val db = dbHelper.writableDatabase

        db.execSQL(
            "UPDATE subtasks SET isChecked = ? WHERE id = ?",
            arrayOf(if (newValue) 1 else 0, subtaskId)
        )
    }
}
