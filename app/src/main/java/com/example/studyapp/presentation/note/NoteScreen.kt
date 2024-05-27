package com.example.studyapp.presentation.note

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AddCircle
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.studyapp.presentation.note.components.AddNoteDialogComponent
import com.example.studyapp.presentation.note.components.EmptyComponent
import com.example.studyapp.presentation.note.components.LoadingComponent
import com.example.studyapp.presentation.note.components.NoteCardComponent
import com.example.studyapp.presentation.note.components.UpdateNoteDialogComponent
import com.example.studyapp.presentation.note.components.WelcomeMessageComponent
import kotlinx.coroutines.flow.onEach

@Composable
fun NoteScreen() {

    val noteViewModel: NoteViewModel = hiltViewModel()

    val uiState = noteViewModel.state.collectAsState().value
    val effectFlow = noteViewModel.effect

    val snackBarHostState = remember { SnackbarHostState() }

    LaunchedEffect(key1 = "side-effects_key") {
        effectFlow.onEach { effect ->
            when (effect) {
                is NoteScreenSideEffects.ShowSnackBarMessage -> {
                    snackBarHostState.showSnackbar(
                        message = effect.message,
                        duration = SnackbarDuration.Short,
                        actionLabel = "DISMISS",
                    )
                }
            }
        }
    }
    if (uiState.isShowAddNoteDialog) {
        AddNoteDialogComponent(
            uiState = uiState,
            setTaskTitle = { title ->
                noteViewModel.sendEvent(
                    event = NoteScreenUIEvent.OnChangeNoteTitle(title = title),
                )
            },
            setTaskBody = { body ->
                noteViewModel.sendEvent(
                    event = NoteScreenUIEvent.OnChangeNoteBody(body = body),
                )
            },
            saveTask = {
                noteViewModel.sendEvent(
                    event = NoteScreenUIEvent.AddNote(
                        title = uiState.currentTextFieldTitle,
                        body = uiState.currentTextFieldBody,
                    ),
                )
            },
            closeDialog = {
                noteViewModel.sendEvent(
                    event = NoteScreenUIEvent.OnChangeAddNoteDialogState(show = false),
                )
            },
        )
    }

    if (uiState.isShowUpdateNoteDialog) {
        UpdateNoteDialogComponent(
            uiState = uiState,
            setTaskTitle = { title ->
                noteViewModel.sendEvent(
                    event = NoteScreenUIEvent.OnChangeNoteTitle(title = title),
                )
            },
            setTaskBody = { body ->
                noteViewModel.sendEvent(
                    event = NoteScreenUIEvent.OnChangeNoteBody(body = body),
                )
            },
            saveTask = {
                noteViewModel.sendEvent(event = NoteScreenUIEvent.UpdateNote)
            },
            closeDialog = {
                noteViewModel.sendEvent(
                    event = NoteScreenUIEvent.OnChangeUpdateNoteDialogState(show = false),
                )
            },
            note = uiState.noteToBeUpdated,
        )
    }

    Scaffold(
        snackbarHost = {
                       SnackbarHost(snackBarHostState)
        },
        topBar = { NoteScreenTopBar()},
        floatingActionButton = {
            Column {
                ExtendedFloatingActionButton(
                    icon = {
                        Icon(
                            Icons.Rounded.AddCircle,
                            contentDescription = "Add Task",
                            tint = Color.Black,
                        )
                    },
                    text = {
                        Text(
                            text = "Thêm ghi chú",
                            color = Color.Black,
                        )
                    },
                    onClick = {
                        noteViewModel.sendEvent(
                            event = NoteScreenUIEvent.OnChangeAddNoteDialogState(show = true),
                        )
                    },
                    modifier = Modifier.padding(horizontal = 12.dp),
                    elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 8.dp),
                )
            }
        },
        containerColor = Color(0XFFFAFAFA)
    ) {
        Box(modifier = Modifier.padding(it)) {
            when {
                uiState.isLoading -> {
                    LoadingComponent()
                }

                !uiState.isLoading && uiState.notes.isNotEmpty() -> {
                    LazyColumn(contentPadding = PaddingValues(14.dp)) {
                        item {
                            WelcomeMessageComponent()

                            androidx.compose.foundation.layout.Spacer(
                                modifier = Modifier.height(
                                    30.dp,
                                ),
                            )
                        }

                        items(uiState.notes) { task ->
                            NoteCardComponent(
                                note = task,
                                deleteNote = { taskId ->
                                    Log.d("TASK_ID: ", taskId)
                                    noteViewModel.sendEvent(
                                        event = NoteScreenUIEvent.DeleteNote(noteId = taskId),
                                    )
                                },
                                updateNote = { taskToBeUpdated ->
                                    noteViewModel.sendEvent(
                                        NoteScreenUIEvent.OnChangeUpdateNoteDialogState(
                                            show = true,
                                        ),
                                    )

                                    noteViewModel.sendEvent(
                                        event = NoteScreenUIEvent.SetNoteToBeUpdated(
                                            noteToBeUpdated = taskToBeUpdated,
                                        ),
                                    )
                                },
                            )
                        }
                    }
                }

                !uiState.isLoading && uiState.notes.isEmpty() -> {
                    EmptyComponent()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun NoteScreenTopBar() {
    CenterAlignedTopAppBar(
        title = {
            Text(
                text = "Ghi chú",
                style = MaterialTheme.typography.headlineMedium
            )
        }
    )
}