package com.example.flowstate.data

import android.content.Context
import android.database.sqlite.SQLiteOpenHelper
import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import androidx.compose.ui.graphics.Color
import com.example.flowstate.models.Assignment
import com.example.flowstate.models.Subtask

class FlowstateDatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, "flowstate.db", null, 1) {

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL("""
            CREATE TABLE assignments (
                id TEXT PRIMARY KEY,
                title TEXT,
                courseId TEXT,
                dueDate INTEGER,
                priority INTEGER,
                progress INTEGER,
                notes TEXT,
                estimatedTimeMinutes INTEGER,
                expectedGrade INTEGER,
                actualGrade INTEGER,
                isCompleted INTEGER,
                colorHex TEXT DEFAULT '#FBDE98'
            );

        """)

        db?.execSQL("""
            CREATE TABLE subtasks (
                id TEXT PRIMARY KEY,
                assignmentId TEXT,
                text TEXT,
                isChecked INTEGER,
                weight INTEGER
            )
        """)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS assignments")
        db?.execSQL("DROP TABLE IF EXISTS subtasks")
        onCreate(db)
    }

    ///assignment CRUD functions
    fun insertAssignment(a: Assignment) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("id", a.id)
            put("title", a.title)
            put("courseId", a.courseId)
            put("dueDate", a.dueDate)
            put("priority", a.priority)
            put("progress", a.progress)
            put("notes", a.notes)
            put("estimatedTimeMinutes", a.estimatedTimeMinutes)
            put("expectedGrade", a.expectedGrade)
            put("actualGrade", a.actualGrade)
            put("isCompleted", if (a.isCompleted) 1 else 0)
        }
        db.insert("assignments", null, values)

        // insert subtasks
        a.subtasks.forEach { subtask ->
            insertSubtask(subtask, a.id)
        }

        //db.close()
    }

    fun getAllAssignments(): List<Assignment> {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM assignments", null)
        val assignments = mutableListOf<Assignment>()

        while (cursor.moveToNext()) {
            val id = cursor.getString(0)
            val title = cursor.getString(1)
            val courseId = cursor.getString(2)
            val dueDate = cursor.getLong(3)
            val priority = cursor.getInt(4)
            val progress = cursor.getInt(5)
            val notes = cursor.getString(6)
            val estimated = cursor.getInt(7)
            val expectedGrade = cursor.getInt(8)
            val actualGrade = cursor.getInt(9)
            val isCompleted = cursor.getInt(10) == 1

            //
            val subtasks = getSubtasksForAssignment(id)

            assignments.add(
                Assignment(
                    id = id,
                    title = title,
                    courseId = courseId,
                    dueDate = dueDate,
                    priority = priority,
                    progress = progress,
                    notes = notes,
                    estimatedTimeMinutes = estimated,
                    expectedGrade = expectedGrade,
                    actualGrade = actualGrade,
                    subtasks = subtasks,
                    isCompleted = isCompleted,
                    color = Color(0xFFFBDE98)

                )
            )
        }

        cursor.close()
        //db.close()
        return assignments
    }

    fun deleteAssignment(id: String) {
        val db = writableDatabase
        db.delete("assignments", "id=?", arrayOf(id))
        db.delete("subtasks", "assignmentId=?", arrayOf(id))
        //db.close()
    }

    ///subtask CRUD functions
    fun insertSubtask(subtask: Subtask, assignmentId: String) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("id", subtask.id)
            put("assignmentId", assignmentId)
            put("text", subtask.text)
            put("isChecked", if (subtask.isChecked) 1 else 0)
            put("weight", subtask.weight)
        }
        db.insert("subtasks", null, values)
        //db.close()
    }

    fun getSubtasksForAssignment(assignmentId: String): List<Subtask> {
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT * FROM subtasks WHERE assignmentId=?",
            arrayOf(assignmentId)
        )
        val subtasks = mutableListOf<Subtask>()

        while (cursor.moveToNext()) {
            val id = cursor.getString(0)
            val text = cursor.getString(2)
            val isChecked = cursor.getInt(3) == 1

            subtasks.add(
                Subtask(
                    id = id,
                    assignmentId = assignmentId,
                    text = text,
                    isChecked = isChecked
                )
            )
        }

        cursor.close()
        //db.close()
        return subtasks
    }

    //for testing
    fun insertAssignment(
        id: String,
        title: String,
        courseId: String,
        dueDate: Long,
        priority: Int,
        progress: Int,
        notes: String?
    ) {
        val db = writableDatabase
        val cv = ContentValues()

        cv.put("id", id)
        cv.put("title", title)
        cv.put("courseId", courseId)
        cv.put("dueDate", dueDate)
        cv.put("priority", priority)
        cv.put("progress", progress)
        cv.put("notes", notes)

        db.insert("assignments", null, cv)
    }

    fun insertSubtask(id: String, assignmentId: String, text: String, isChecked: Boolean) {
        val db = writableDatabase
        val cv = ContentValues()

        cv.put("id", id)
        cv.put("assignmentId", assignmentId)
        cv.put("text", text)
        cv.put("isChecked", if (isChecked) 1 else 0)

        db.insert("subtasks", null, cv)
    }

    fun hasAssignments(): Boolean {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT COUNT(*) FROM assignments", null)
        cursor.moveToFirst()
        val count = cursor.getInt(0)
        cursor.close()
        return count > 0
    }
}
