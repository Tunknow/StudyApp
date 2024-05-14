package com.example.studyapp.presentation.note

sealed class NoteScreenSideEffects {
    data class ShowSnackBarMessage(val message: String) : NoteScreenSideEffects()
}