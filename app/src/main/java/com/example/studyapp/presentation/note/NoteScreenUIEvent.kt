package com.example.studyapp.presentation.note

import com.example.studyapp.domain.model.Note

sealed class NoteScreenUIEvent {
    object GetNotes : NoteScreenUIEvent()

    data class AddNote(val title: String, val body: String) : NoteScreenUIEvent()

    object UpdateNote : NoteScreenUIEvent()

    data class DeleteNote(val noteId: String) : NoteScreenUIEvent()

    data class OnChangeNoteTitle(val title: String) : NoteScreenUIEvent()

    data class OnChangeNoteBody(val body: String) : NoteScreenUIEvent()

    data class OnChangeAddNoteDialogState(val show: Boolean) : NoteScreenUIEvent()

    data class OnChangeUpdateNoteDialogState(val show: Boolean) :
        NoteScreenUIEvent()

    data class SetNoteToBeUpdated(val noteToBeUpdated: Note) : NoteScreenUIEvent()
}