package com.example.studyapp.presentation.study.subject

import com.example.studyapp.domain.model.Session
import com.example.studyapp.domain.model.Task

sealed class SubjectScreenEvent {
    data object UpdateSubject : SubjectScreenEvent()
    data object DeleteSubject : SubjectScreenEvent()
    data object DeleteSession : SubjectScreenEvent()
    data object UpdateProgress : SubjectScreenEvent()
    data class OnTaskIsCompleteChange(val task: Task): SubjectScreenEvent()
    data class OnSubjectNameChange(val name: String): SubjectScreenEvent()
    data class OnGoalStudyHoursChange(val hours: String): SubjectScreenEvent()
    data class OnDeleteSessionButtonClick(val session: Session): SubjectScreenEvent()
}