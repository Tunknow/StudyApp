package com.example.studyapp.presentation.study.subject

import android.util.Log
import androidx.compose.material3.SnackbarDuration
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studyapp.data.repositories.SessionRepository
import com.example.studyapp.data.repositories.SubjectRepository
import com.example.studyapp.data.repositories.TaskRepository
import com.example.studyapp.domain.model.Subject
import com.example.studyapp.domain.model.Task
import com.example.studyapp.util.SnackbarEvent
import com.example.studyapp.util.toHours
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class SubjectViewModel @Inject constructor(
    private val subjectRepository: SubjectRepository,
    private val taskRepository: TaskRepository,
    private val sessionRepository: SessionRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val subjectId: String = savedStateHandle.get<String>("subjectId")!!

    private val _state = MutableStateFlow(SubjectScreenState())
    val state = combine(
        _state,
        taskRepository.getUpcomingTasksForSubject(subjectId),
        taskRepository.getCompletedTasksForSubject(subjectId),
        sessionRepository.getRecentTenSessionsForSubject(subjectId),
        sessionRepository.getTotalSessionsDurationBySubject(subjectId)
    ) { state, upcomingTasks, completedTask, recentSessions, totalSessionsDuration ->
        state.copy(
            upcomingTasks = upcomingTasks,
            completedTasks = completedTask,
            recentSessions = recentSessions,
            studiedHours = totalSessionsDuration.toHours(),
            progress = (totalSessionsDuration.toHours() / state.goalStudyHours.toFloatOrNull()!!).coerceIn(0f, 1f)
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000),
        initialValue = SubjectScreenState()
    )



    private val _snackbarEventFlow = MutableSharedFlow<SnackbarEvent>()
    val snackbarEventFlow = _snackbarEventFlow.asSharedFlow()

    init {
        if (subjectId.isEmpty() || subjectId == null) {
            Log.d("SubjectViewModel", "Subject ID is empty")
        } else {
            fetchSubject()
        }

    }



    fun onEvent(event: SubjectScreenEvent) {
        when (event) {

            is SubjectScreenEvent.OnSubjectNameChange -> {
                _state.update {
                    it.copy(subjectName = event.name)
                }
            }

            is SubjectScreenEvent.OnGoalStudyHoursChange -> {
                _state.update {
                    it.copy(goalStudyHours = event.hours)
                }
            }

            is SubjectScreenEvent.OnDeleteSessionButtonClick -> {
                _state.update {
                    it.copy(session = event.session)
                }
            }
            is SubjectScreenEvent.OnTaskIsCompleteChange -> {
                updateTask(event.task)
            }

            SubjectScreenEvent.UpdateSubject -> updateSubject()
            SubjectScreenEvent.DeleteSubject -> deleteSubject()
            SubjectScreenEvent.DeleteSession -> deleteSession()

            SubjectScreenEvent.UpdateProgress -> {
                val goalStudyHours = state.value.goalStudyHours.toFloatOrNull() ?: 1f
                _state.update {
                    it.copy(
                        progress = (state.value.studiedHours / goalStudyHours).coerceIn(0f, 1f)
                    )
                }
            }
        }
    }

    private fun fetchSubject() {
        viewModelScope.launch {
            subjectRepository
                .getSubjectById(subjectId)?.let { subject ->
                    _state.update {
                        it.copy(
                            subjectName = subject.name,
                            goalStudyHours = subject.goalHours.toString(),
                            currentSubjectId = subject.id,
                            currentSubject = subject,
                            progress = (state.value.studiedHours / subject.goalHours).coerceIn(0f, 1f)
                        )
                    }

                }
        }
    }

    private fun deleteSession() {
        viewModelScope.launch {
            try {
                state.value.session?.let {
                    sessionRepository.deleteSession(it)
                    _snackbarEventFlow.emit(
                        SnackbarEvent.ShowSnackbar(message = "Xóa phiên học thành công.")
                    )
                }
            } catch (e: Exception) {
                _snackbarEventFlow.emit(
                    SnackbarEvent.ShowSnackbar(
                        message = "Không thể xóa phiên học. ${e.message}",
                        duration = SnackbarDuration.Long
                    )
                )
            }
        }
    }

    private fun deleteSubject() {
        viewModelScope.launch {
            try {
                val currentSubjectId = state.value.currentSubjectId
                if (currentSubjectId != null) {
                    withContext(Dispatchers.IO) {
                        subjectRepository.deleteSubject(subjectId = currentSubjectId)
                    }
                    _snackbarEventFlow.emit(
                        SnackbarEvent.ShowSnackbar(message = "Môn học đã được xóa thành công")
                    )
                    _snackbarEventFlow.emit(SnackbarEvent.NavigateUp)
                } else {
                    _snackbarEventFlow.emit(
                        SnackbarEvent.ShowSnackbar(message = "Không có môn học để xóa")
                    )
                }
            } catch (e: Exception) {
                _snackbarEventFlow.emit(
                    SnackbarEvent.ShowSnackbar(
                        message = "Không thể xóa môn học. ${e.message}",
                        duration = SnackbarDuration.Long
                    )
                )
            }
        }
    }

    private fun updateSubject() {
        viewModelScope.launch {
            try {
                subjectRepository.updateSubject(
                    subject = Subject(
                        id = state.value.currentSubjectId,
                        uid = "",
                        name = state.value.subjectName,
                        goalHours = state.value.goalStudyHours.toFloatOrNull() ?: 1f,
                    )
                )
                _snackbarEventFlow.emit(
                    SnackbarEvent.ShowSnackbar(message = "Môn học được cập nhật thành công.")
                )
            } catch (e: Exception) {
                _snackbarEventFlow.emit(
                    SnackbarEvent.ShowSnackbar(
                        message = "Không thể cập nhật. ${e.message}",
                        duration = SnackbarDuration.Long
                    )
                )
            }
        }
    }

    private fun updateTask(task: Task) {
        viewModelScope.launch {
            try {
                taskRepository.updateTask(
                    task = task.copy(isCompleted = !task.isCompleted)
                )

                if (task.isCompleted) {
                    _snackbarEventFlow.emit(
                        SnackbarEvent.ShowSnackbar(message = "Lưu thành nhiệm vụ sắp tới.")
                    )
                } else {
                    _snackbarEventFlow.emit(
                        SnackbarEvent.ShowSnackbar(message = "Lưu thành nhiệm vụ hoàn thành.")
                    )
                }
            } catch (e: Exception) {
                _snackbarEventFlow.emit(
                    SnackbarEvent.ShowSnackbar(
                        message = "Không thể cập nhật nhiệm vụ. ${e.message}",
                        duration = SnackbarDuration.Long
                    )
                )
            }
        }
    }


}