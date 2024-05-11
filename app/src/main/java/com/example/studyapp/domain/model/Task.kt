package com.example.studyapp.domain.model

data class Task(
    val id: String,
    val sid: String,
    val title: String,
    val description: String,
    val dueDate: Long,
    val priority: Int,
    val relatedToSubject: String = "",
    val isCompleted: Boolean
)
