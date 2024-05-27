package com.example.studyapp.presentation.profile.components

sealed class UpdateProfileUIEffect {
    data class ShowSnackBarMessage(val message: String) : UpdateProfileUIEffect()

}