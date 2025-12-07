package com.example.flowstate.data

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import androidx.compose.ui.graphics.Color
import com.example.flowstate.models.Assignment
import com.example.flowstate.models.Course
import com.example.flowstate.models.Subtask

class FlowstateDatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, "flowstate.db", null, 2) {

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

        db?.execSQL("""
            CREATE TABLE courses (
                id TEXT PRIMARY KEY,
                courseCode TEXT NOT NULL,
                courseName TEXT NOT NULL,
                term TEXT NOT NULL,
                isCurrentTerm INTEGER DEFAULT 0,
                progress INTEGER DEFAULT 0
            )
        """)

        db?.execSQL("""
            CREATE TABLE case_studies (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                courseId TEXT NOT NULL,
                caseStudyName TEXT NOT NULL,
                FOREIGN KEY(courseId) REFERENCES courses(id)
            )
        """)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        if (oldVersion < 2) {
            db?.execSQL("""
                CREATE TABLE IF NOT EXISTS courses (
                    id TEXT PRIMARY KEY,
                    courseCode TEXT NOT NULL,
                    courseName TEXT NOT NULL,
                    term TEXT NOT NULL,
                    isCurrentTerm INTEGER DEFAULT 0,
                    progress INTEGER DEFAULT 0
                )
            """)

            db?.execSQL("""
                CREATE TABLE IF NOT EXISTS case_studies (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    courseId TEXT NOT NULL,
                    caseStudyName TEXT NOT NULL,
                    FOREIGN KEY(courseId) REFERENCES courses(id)
                )
            """)
        }
    }

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
        a.subtasks.forEach { subtask ->
            insertSubtask(subtask, a.id)
        }
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
                    color = Color(
                        red = (100..255).random(),
                        green = (100..255).random(),
                        blue = (100..255).random()
                    )
                )
            )
        }
        cursor.close()
        return assignments
    }

    fun deleteAssignment(id: String) {
        val db = writableDatabase
        db.delete("assignments", "id=?", arrayOf(id))
        db.delete("subtasks", "assignmentId=?", arrayOf(id))
    }

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
        return subtasks
    }

    fun insertCourse(course: Course) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("id", course.id)
            put("courseCode", course.courseCode)
            put("courseName", course.courseName)
            put("term", course.term)
            put("isCurrentTerm", if (course.isCurrentTerm) 1 else 0)
            put("progress", course.progress)
        }
        db.insert("courses", null, values)
        course.caseStudies.forEach { caseStudyName ->
            insertCaseStudy(course.id, caseStudyName)
        }
    }

    fun getAllCourses(): List<Course> {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM courses ORDER BY isCurrentTerm DESC, term DESC", null)
        val courses = mutableListOf<Course>()

        while (cursor.moveToNext()) {
            val id = cursor.getString(cursor.getColumnIndexOrThrow("id"))
            val courseCode = cursor.getString(cursor.getColumnIndexOrThrow("courseCode"))
            val courseName = cursor.getString(cursor.getColumnIndexOrThrow("courseName"))
            val term = cursor.getString(cursor.getColumnIndexOrThrow("term"))
            val isCurrentTerm = cursor.getInt(cursor.getColumnIndexOrThrow("isCurrentTerm")) == 1
            val progress = cursor.getInt(cursor.getColumnIndexOrThrow("progress"))
            val caseStudies = getCaseStudiesForCourse(id)

            courses.add(
                Course(
                    id = id,
                    courseCode = courseCode,
                    courseName = courseName,
                    term = term,
                    caseStudies = caseStudies,
                    isCurrentTerm = isCurrentTerm,
                    progress = progress
                )
            )
        }
        cursor.close()
        return courses
    }

    fun getPastTermCourses(): List<Course> {
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT * FROM courses WHERE isCurrentTerm = 0 ORDER BY term DESC",
            null
        )
        val courses = mutableListOf<Course>()

        while (cursor.moveToNext()) {
            val id = cursor.getString(cursor.getColumnIndexOrThrow("id"))
            val courseCode = cursor.getString(cursor.getColumnIndexOrThrow("courseCode"))
            val courseName = cursor.getString(cursor.getColumnIndexOrThrow("courseName"))
            val term = cursor.getString(cursor.getColumnIndexOrThrow("term"))
            val isCurrentTerm = cursor.getInt(cursor.getColumnIndexOrThrow("isCurrentTerm")) == 1
            val progress = cursor.getInt(cursor.getColumnIndexOrThrow("progress"))
            val caseStudies = getCaseStudiesForCourse(id)

            courses.add(
                Course(
                    id = id,
                    courseCode = courseCode,
                    courseName = courseName,
                    term = term,
                    caseStudies = caseStudies,
                    isCurrentTerm = isCurrentTerm,
                    progress = progress
                )
            )
        }
        cursor.close()
        return courses
    }

    fun getCurrentTermProgress(): Int {
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT AVG(progress) FROM courses WHERE isCurrentTerm = 1",
            null
        )
        var avgProgress = 0
        if (cursor.moveToFirst()) {
            avgProgress = cursor.getInt(0)
        }
        cursor.close()
        return avgProgress
    }

    fun deleteCourse(id: String) {
        val db = writableDatabase
        db.delete("courses", "id=?", arrayOf(id))
        db.delete("case_studies", "courseId=?", arrayOf(id))
    }

    private fun insertCaseStudy(courseId: String, caseStudyName: String) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("courseId", courseId)
            put("caseStudyName", caseStudyName)
        }
        db.insert("case_studies", null, values)
    }

    private fun getCaseStudiesForCourse(courseId: String): List<String> {
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT caseStudyName FROM case_studies WHERE courseId=?",
            arrayOf(courseId)
        )
        val caseStudies = mutableListOf<String>()
        while (cursor.moveToNext()) {
            caseStudies.add(cursor.getString(0))
        }
        cursor.close()
        return caseStudies
    }

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

    fun hasCourses(): Boolean {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT COUNT(*) FROM courses", null)
        cursor.moveToFirst()
        val count = cursor.getInt(0)
        cursor.close()
        return count > 0
    }
}