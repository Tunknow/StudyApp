package com.example.studyapp.presentation.study

import android.util.Log
import androidx.compose.material3.SnackbarDuration
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studyapp.data.repositories.SessionRepository
import com.example.studyapp.data.repositories.SubjectRepository
import com.example.studyapp.data.repositories.TaskRepository
import com.example.studyapp.domain.model.Subject
import com.example.studyapp.domain.model.Task
import com.example.studyapp.util.SnackbarEvent
import com.example.studyapp.util.toHours
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StudyViewModel @Inject constructor(
    private val subjectRepository: SubjectRepository,
    private val sessionRepository: SessionRepository,
    private val taskRepository: TaskRepository
) : ViewModel() {

    private val _state = MutableStateFlow(StudyScreenState())
    val state = combine(
        _state,
        subjectRepository.getAllSubjects(),
        sessionRepository.getTotalSessionsDuration(),
        taskRepository.getAllUpcomingTasks(),
        sessionRepository.getRecentFiveSessions()
    ) { state, subjects, totalSessionDuration, upcomingtasks, recentSessions  ->
        Log.d("StudyViewModel", "Subjects: $subjects, TotalSessionDuration: $totalSessionDuration")
        state.copy(
            totalSubjectCount = subjects.size,
            totalGoalStudyHours = subjects.sumByDouble { it.goalHours.toDouble() }.toFloat(),
            subjects = subjects,
            totalStudiedHours = totalSessionDuration.toHours(),
            tasks = upcomingtasks,
            recentSessions = recentSessions
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000),
        initialValue = StudyScreenState()
    )

    private val _snackbarEventFlow = MutableSharedFlow<SnackbarEvent>()
    val snackbarEventFlow = _snackbarEventFlow.asSharedFlow()
    fun onEvent(event: StudyScreenEvent) {
        when (event) {
            is StudyScreenEvent.OnSubjectNameChange -> {
                _state.update {
                    it.copy(subjectName = event.name)
                }
            }

            is StudyScreenEvent.OnGoalStudyHoursChange -> {
                _state.update {
                    it.copy(goalStudyHours = event.hours)
                }
            }

            is StudyScreenEvent.OnDeleteSessionButtonClick -> {
                _state.update {
                    it.copy(session = event.session)
                }
            }

            StudyScreenEvent.SaveSubject -> saveSubject()
            StudyScreenEvent.DeleteSession -> deleteSession()
            is StudyScreenEvent.OnTaskIsCompleteChange -> {
                updateTask(event.task)
            }
        }
    }

    private fun deleteSession() {
        viewModelScope.launch {
            try {
                state.value.session?.let {
                    sessionRepository.deleteSession(it)
                    _snackbarEventFlow.emit(
                        SnackbarEvent.ShowSnackbar(message = "Xóa phiên học thành công")
                    )
                }
            } catch (e: Exception) {
                _snackbarEventFlow.emit(
                    SnackbarEvent.ShowSnackbar(
                        message = "Không thể thực hiện. ${e.message}",
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
                _snackbarEventFlow.emit(
                    SnackbarEvent.ShowSnackbar(message = "Nhiệm vụ cập nhật thành công")
                )
            } catch (e: Exception) {
                _snackbarEventFlow.emit(
                    SnackbarEvent.ShowSnackbar(
                        "Không thể cập nhật. ${e.message}",
                        SnackbarDuration.Long
                    )
                )
            }
        }
    }

    private fun saveSubject() {
        viewModelScope.launch {
            try {
                subjectRepository.insertSubject(
                    subject = Subject(
                        uid = FirebaseAuth.getInstance().currentUser!!.uid,
                        name = state.value.subjectName,
                        goalHours = state.value.goalStudyHours.toFloatOrNull() ?: 1f,
                    )
                )
                _state.update {
                    it.copy(
                        subjectName = "",
                        goalStudyHours = ""
                    )
                }
                _snackbarEventFlow.emit(
                    SnackbarEvent.ShowSnackbar(message = "Môn học đã được lưu thành công")
                )
            } catch (e: Exception) {
                _snackbarEventFlow.emit(
                    SnackbarEvent.ShowSnackbar(
                        message = "Không thể thực hiện. ${e.message}",
                        duration = SnackbarDuration.Long
                    )
                )
            }
        }
    }

}