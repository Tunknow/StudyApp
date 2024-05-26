package com.example.studyapp.presentation.study

import com.example.studyapp.domain.model.Session
import com.example.studyapp.domain.model.Subject
import com.example.studyapp.domain.model.Task

data class StudyScreenState(
    var totalSubjectCount: Int = 0,
    var totalStudiedHours: Float = 0f,
    var totalGoalStudyHours: Float = 0f,
    var subjects: List<Subject> = emptyList(),
    var subjectName: String = "",
    var goalStudyHours: String = "",
    var session: Session? = null,
    var tasks: List<Task> = emptyList(),
    var recentSessions: List<Session> = emptyList()
)