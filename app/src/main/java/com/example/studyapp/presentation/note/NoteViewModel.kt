package com.example.studyapp.presentation.note

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studyapp.data.repositories.NoteRepository
import com.example.studyapp.domain.model.Note
import com.example.studyapp.util.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NoteViewModel @Inject constructor(private val noteRepository: NoteRepository) : ViewModel(){
    private val _state: MutableStateFlow<NoteScreenUIState> =
        MutableStateFlow(NoteScreenUIState())
    val state: StateFlow<NoteScreenUIState> = _state.asStateFlow()

    private val _effect: Channel<NoteScreenSideEffects> = Channel()
    val effect = _effect.receiveAsFlow()

    init {
        sendEvent(NoteScreenUIEvent.GetNotes)
    }

    fun sendEvent(event: NoteScreenUIEvent) {
        reduce(oldState = _state.value, event = event)
    }

    private fun setEffect(builder: () -> NoteScreenSideEffects) {
        val effectValue = builder()
        viewModelScope.launch { _effect.send(effectValue) }
    }

    private fun setState(newState: NoteScreenUIState) {
        _state.value = newState
    }

    private fun reduce(oldState: NoteScreenUIState, event: NoteScreenUIEvent) {
        when (event) {
            is NoteScreenUIEvent.AddNote -> {
                addNote(oldState = oldState, title = event.title, body = event.body)
            }

            is NoteScreenUIEvent.DeleteNote -> {
                deleteNote(oldState = oldState, noteId = event.noteId)
            }

            NoteScreenUIEvent.GetNotes -> {
                getNote(oldState = oldState)
            }

            is NoteScreenUIEvent.OnChangeAddNoteDialogState -> {
                onChangeAddNoteDialog(oldState = oldState, isShown = event.show)
            }

            is NoteScreenUIEvent.OnChangeUpdateNoteDialogState -> {
                onUpdateAddNoteDialog(oldState = oldState, isShown = event.show)
            }

            is NoteScreenUIEvent.OnChangeNoteBody -> {
                onChangeNoteBody(oldState = oldState, body = event.body)
            }

            is NoteScreenUIEvent.OnChangeNoteTitle -> {
                onChangeNoteTitle(oldState = oldState, title = event.title)
            }

            is NoteScreenUIEvent.SetNoteToBeUpdated -> {
                setTaskToBeUpdated(oldState = oldState, note = event.noteToBeUpdated)
            }

            NoteScreenUIEvent.UpdateNote -> {
                updateNote(oldState = oldState)
            }
        }
    }

    private fun addNote(title: String, body: String, oldState:NoteScreenUIState) {
        viewModelScope.launch {
            setState(oldState.copy(isLoading = true))

            when (val result = noteRepository.addNote(title = title, body = body)) {
                is Result.Failure -> {
                    setState(oldState.copy(isLoading = false))

                    val errorMessage =
                        result.exception.message ?: "An error occurred when adding task"
                    setEffect { NoteScreenSideEffects.ShowSnackBarMessage(message = errorMessage) }
                }

                is Result.Success -> {
                    setState(
                        oldState.copy(
                            isLoading = false,
                            currentTextFieldTitle = "",
                            currentTextFieldBody = "",
                        ),
                    )

                    sendEvent(NoteScreenUIEvent.OnChangeAddNoteDialogState(show = false))

                    sendEvent(NoteScreenUIEvent.GetNotes)

                    setEffect { NoteScreenSideEffects.ShowSnackBarMessage(message = "Task added successfully") }
                }
            }
        }
    }

    private fun getNote(oldState: NoteScreenUIState) {
        viewModelScope.launch {
            setState(oldState.copy(isLoading = true))

            when (val result = noteRepository.getAllNotes()) {
                is Result.Failure -> {
                    setState(oldState.copy(isLoading = false))

                    val errorMessage =
                        result.exception.message ?: "An error occurred when getting your task"
                    setEffect { NoteScreenSideEffects.ShowSnackBarMessage(message = errorMessage) }
                }

                is Result.Success -> {
                    val notes = result.data
                    setState(oldState.copy(isLoading = false, notes = notes))
                }
            }
        }
    }

    private fun deleteNote(oldState: NoteScreenUIState, noteId: String) {
        viewModelScope.launch {
            setState(oldState.copy(isLoading = true))

            when (val result = noteRepository.deleteNote(noteId = noteId)) {
                is Result.Failure -> {
                    setState(oldState.copy(isLoading = false))

                    val errorMessage =
                        result.exception.message ?: "An error occurred when deleting task"
                    setEffect { NoteScreenSideEffects.ShowSnackBarMessage(message = errorMessage) }
                }

                is Result.Success -> {
                    setState(oldState.copy(isLoading = false))

                    setEffect { NoteScreenSideEffects.ShowSnackBarMessage(message = "Task deleted successfully") }

                    sendEvent(NoteScreenUIEvent.GetNotes)
                }
            }
        }
    }

    private fun updateNote(oldState: NoteScreenUIState) {
        viewModelScope.launch {
            setState(oldState.copy(isLoading = true))

            val title = oldState.currentTextFieldTitle
            val body = oldState.currentTextFieldBody
            val noteToBeUpdated = oldState.noteToBeUpdated

            when (
                val result = noteRepository.updateNote(
                    title = title,
                    body = body,
                    noteId = noteToBeUpdated?.id ?: "",
                )
            ) {
                is Result.Failure -> {
                    setState(oldState.copy(isLoading = false))

                    val errorMessage =
                        result.exception.message ?: "An error occurred when updating task"
                    setEffect { NoteScreenSideEffects.ShowSnackBarMessage(message = errorMessage) }
                }

                is Result.Success -> {
                    setState(
                        oldState.copy(
                            isLoading = false,
                            currentTextFieldTitle = "",
                            currentTextFieldBody = "",
                        ),
                    )

                    sendEvent(NoteScreenUIEvent.OnChangeUpdateNoteDialogState(show = false))

                    setEffect { NoteScreenSideEffects.ShowSnackBarMessage(message = "Ghi chú cập nhật thành công") }

                    sendEvent(NoteScreenUIEvent.GetNotes)
                }
            }
        }
    }

    private fun onChangeAddNoteDialog(oldState: NoteScreenUIState, isShown: Boolean) {
        setState(oldState.copy(isShowAddNoteDialog = isShown))
    }

    private fun onUpdateAddNoteDialog(oldState: NoteScreenUIState, isShown: Boolean) {
        setState(oldState.copy(isShowUpdateNoteDialog = isShown))
    }

    private fun onChangeNoteBody(oldState: NoteScreenUIState, body: String) {
        setState(oldState.copy(currentTextFieldBody = body))
    }

    private fun onChangeNoteTitle(oldState: NoteScreenUIState, title: String) {
        setState(oldState.copy(currentTextFieldTitle = title))
    }

    private fun setTaskToBeUpdated(oldState: NoteScreenUIState, note: Note) {
        setState(oldState.copy(noteToBeUpdated = note))
    }
}