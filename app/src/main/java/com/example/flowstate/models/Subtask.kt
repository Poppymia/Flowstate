package com.example.flowstate.models

import java.util.UUID

data class Subtask (
    val id: String = UUID.randomUUID().toString(),
    val text: String,
    val isChecked: Boolean = false
)
