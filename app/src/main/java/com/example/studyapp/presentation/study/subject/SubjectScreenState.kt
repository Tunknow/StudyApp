package com.example.studyapp.presentation.study.subject

import com.example.studyapp.domain.model.Session
import com.example.studyapp.domain.model.Subject
import com.example.studyapp.domain.model.Task

data class SubjectScreenState(
    val currentSubjectId: String? = null,
    val currentSubject: Subject? = null,
    val subjectName: String = "",
    val goalStudyHours: String = "",
    val studiedHours: Float = 0f,
    val progress: Float = 0f,
    val allSession: List<Session> = emptyList(),
    val recentSessions: List<Session> = emptyList(),
    val upcomingTasks: List<Task> = emptyList(),
    val completedTasks: List<Task> = emptyList(),
    val session: Session? = null
)