package com.example.studyapp.presentation.study.task

import com.example.studyapp.domain.model.Subject
import com.example.studyapp.util.Priority

data class TaskScreenState(
    val title: String = "",
    val description: String = "",
    val dueDate: Long? = null,
    val isTaskComplete: Boolean = false,
    val priority: Priority = Priority.LOW,
    val relatedToSubject: String? = null,
    val subjects: List<Subject> = emptyList(),
    val subjectId: String? = null,
    val currentTaskId: String? = null
)