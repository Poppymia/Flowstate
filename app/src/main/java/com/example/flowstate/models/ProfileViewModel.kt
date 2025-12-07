package com.example.flowstate.models

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.flowstate.data.FlowstateDatabaseHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class ProfileViewModel(private val dbHelper: FlowstateDatabaseHelper) : ViewModel() {

    private val _pastTermCourses = MutableStateFlow<List<Course>>(emptyList())
    val pastTermCourses: StateFlow<List<Course>> = _pastTermCourses

    private val _currentTermProgress = mutableStateOf(0)
    val currentTermProgress = _currentTermProgress

    init {
        loadCourses()
        loadCurrentTermProgress()
    }

    fun loadCourses() {
        _pastTermCourses.value = dbHelper.getPastTermCourses()
    }

    fun loadCurrentTermProgress() {
        _currentTermProgress.value = dbHelper.getCurrentTermProgress()
    }

    fun addCourse(course: Course) {
        dbHelper.insertCourse(course)
        loadCourses()
        if (course.isCurrentTerm) {
            loadCurrentTermProgress()
        }
    }

    fun deleteCourse(courseId: String) {
        dbHelper.deleteCourse(courseId)
        loadCourses()
        loadCurrentTermProgress()
    }

    fun getAllCourses(): List<Course> {
        return dbHelper.getAllCourses()
    }
}