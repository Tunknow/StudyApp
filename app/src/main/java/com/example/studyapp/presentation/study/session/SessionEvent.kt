package com.example.studyapp.presentation.study.session

import com.example.studyapp.domain.model.Session
import com.example.studyapp.domain.model.Subject

sealed class SessionEvent {
    data class OnRelatedSubjectChange(val subject: Subject) : SessionEvent()
    data class SaveSession(val duration: Long) : SessionEvent()
    data class OnDeleteSessionButtonClick(val session: Session) : SessionEvent()
    data object DeleteSession : SessionEvent()
    data object NotifyToUpdateSubject : SessionEvent()
    data class UpdateSubjectIdAndRelatedSubject(
        val subjectId: String?,
        val relatedToSubject: String?
    ) : SessionEvent()
}