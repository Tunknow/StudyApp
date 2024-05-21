package com.example.studyapp.presentation.study.task

import com.example.studyapp.domain.model.Subject
import com.example.studyapp.util.Priority

sealed class TaskScreenEvent {
    data class OnTitleChange(val title: String) : TaskScreenEvent()
    data class OnDescriptionChange(val description: String) : TaskScreenEvent()
    data class OnDateChange(val millis: Long?) : TaskScreenEvent()
    data class OnPriorityChange(val priority: Priority) : TaskScreenEvent()
    data class OnRelatedSubjectSelect(val subject: Subject) : TaskScreenEvent()
    data object OnIsCompleteChange : TaskScreenEvent()
    data object SaveTask : TaskScreenEvent()
    data object DeleteTask : TaskScreenEvent()
}