/*
package com.example.flowstate.models

class AssignmentAddViewModel {

}
*/
package com.example.flowstate.features.add

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.flowstate.data.FlowstateDatabaseHelper
import com.example.flowstate.models.Assignment

class AssignmentAddViewModel(private val db: FlowstateDatabaseHelper) : ViewModel() {

    val title = mutableStateOf("")
    val courseId = mutableStateOf("")
    val notes = mutableStateOf("")
    val priority = mutableStateOf(1)

    fun saveAssignment() {
        val a = Assignment(
            title = title.value,
            courseId = courseId.value,
            notes = notes.value
        )
        db.insertAssignment(a)
    }
}
