package com.example.flowstate.models

//for single user for now

data class UserProfile (
    val id: String = "user",
    val name: String = "Student",
    val theme: String = "light", //between light or dark
    val notificationsEnabled: Boolean = true
)
