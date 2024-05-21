package com.example.studyapp.domain.model

data class Task(
    val id: String = "",
    val sid: String = "",
    val title: String = "",
    val description: String = "",
    val dueDate: Long = 0L,
    val priority: Int = 1,
    val relatedToSubject: String = "",
    val isCompleted: Boolean = false
)
