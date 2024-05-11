package com.example.studyapp.presentation.study

import com.example.studyapp.domain.model.Session
import com.example.studyapp.domain.model.Task

sealed class StudyScreenEvent {

    data object SaveSubject : StudyScreenEvent()
    data object DeleteSession : StudyScreenEvent()
    data class OnDeleteSessionButtonClick(val session: Session): StudyScreenEvent()
    data class OnTaskIsCompleteChange(val task: Task): StudyScreenEvent()
    data class OnSubjectNameChange(val name: String): StudyScreenEvent()
    data class OnGoalStudyHoursChange(val hours: String): StudyScreenEvent()
}