package com.example.studyapp.presentation.note

import com.example.studyapp.domain.model.Note

data class NoteScreenUIState(
    val isLoading: Boolean = false,
    val notes: List<Note> = emptyList(),
    val errorMessage: String? = null,
    val noteToBeUpdated: Note? = null,
    val isShowAddNoteDialog: Boolean = false,
    val isShowUpdateNoteDialog: Boolean = false,
    val currentTextFieldTitle: String = "",
    val currentTextFieldBody: String = "",
)