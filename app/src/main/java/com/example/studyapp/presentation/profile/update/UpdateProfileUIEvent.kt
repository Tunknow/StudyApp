package com.example.studyapp.presentation.profile.update

sealed class UpdateProfileUIEvent {
    object UpdateProfileButtonClicked : UpdateProfileUIEvent()
    class OnFullnameChanged(val fullname: String) : UpdateProfileUIEvent()
    class OnStudyAtChanged(val studyAt: String) : UpdateProfileUIEvent()
    class OnDobChanged(val dob: Long?) : UpdateProfileUIEvent()
}