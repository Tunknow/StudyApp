package com.example.studyapp.data.repositories

import com.example.studyapp.domain.model.Note
import com.example.studyapp.util.Result

interface NoteRepository {
    suspend fun addNote(title: String, body: String): Result<Unit>

    suspend fun getAllNotes(): Result<List<Note>>

    suspend fun deleteNote(noteId: String): Result<Unit>

    suspend fun updateNote(title: String, body: String, noteId: String): Result<Unit>
}