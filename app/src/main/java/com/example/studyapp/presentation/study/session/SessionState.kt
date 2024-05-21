package com.example.studyapp.presentation.study.session

import com.example.studyapp.domain.model.Session
import com.example.studyapp.domain.model.Subject

data class SessionState(
    val subjects: List<Subject> = emptyList(),
    val sessions: List<Session> = emptyList(),
    val relatedToSubject: String? = null,
    val subjectId: String? = null,
    val session: Session? = null
)