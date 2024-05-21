package com.example.studyapp.presentation.profile

sealed class ProfileUIEvent {
    object LogoutButtonClicked : ProfileUIEvent()
    object ForgotPasswordClicked : ProfileUIEvent()

    object GetProfile : ProfileUIEvent()
}