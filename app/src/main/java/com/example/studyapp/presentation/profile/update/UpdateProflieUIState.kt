package com.example.studyapp.presentation.profile.update

data class UpdateProflieUIState(
    val isLoading: Boolean = false,
    val fullname: String = "",
    val studyAt: String = "",
    val dob: Long? = 0L
)