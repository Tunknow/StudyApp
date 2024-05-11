package com.example.studyapp.presentation.study

import com.example.studyapp.domain.model.Session
import com.example.studyapp.domain.model.Subject

data class StudyScreenState(
    val totalSubjectCount: Int = 0,
    val totalStudiedHours: Float = 0f,
    val totalGoalStudyHours: Float = 0f,
    val subjects: List<Subject> = emptyList(),
    val subjectName: String = "",
    val goalStudyHours: String = "",
    val session: Session? = null
)