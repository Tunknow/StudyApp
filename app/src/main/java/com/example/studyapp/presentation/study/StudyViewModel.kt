package com.example.studyapp.presentation.study

import android.util.Log
import androidx.compose.material3.SnackbarDuration
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studyapp.data.repositories.SessionRepository
import com.example.studyapp.data.repositories.SubjectRepository
import com.example.studyapp.data.repositories.TaskRepository
import com.example.studyapp.domain.model.Session
import com.example.studyapp.domain.model.Subject
import com.example.studyapp.domain.model.Task
import com.example.studyapp.util.SnackbarEvent
import com.example.studyapp.util.toHours
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StudyViewModel @Inject constructor(
    private val subjectRepository: SubjectRepository,
    private val sessionRepository: SessionRepository,
    private val taskRepository: TaskRepository
) : ViewModel() {

    val studyScreenState = mutableStateOf(StudyScreenState())

//    Khoi tao UI
    init {
        updateUI()
    }
    val subjects: StateFlow<List<Subject>> = subjectRepository.getAllSubjects()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val tasks: StateFlow<List<Task>> = taskRepository.getAllUpcomingTasks()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val recentSession: StateFlow<List<Session>> = sessionRepository.getRecentFiveSessions()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _snackbarEventFlow = MutableSharedFlow<SnackbarEvent>()
    val snackbarEventFlow = _snackbarEventFlow.asSharedFlow()
    fun onEvent(event: StudyScreenEvent) {
        when (event) {
            StudyScreenEvent.DeleteSession -> {
                deleteSession()
            }
            is StudyScreenEvent.OnDeleteSessionButtonClick -> {
                studyScreenState.value = studyScreenState.value.copy(
                    session = event.session
                )
                Log.d("SVM log", "session: ${event.session}")
            }
            is StudyScreenEvent.OnGoalStudyHoursChange -> {
                studyScreenState.value = studyScreenState.value.copy(
                    goalStudyHours = event.hours
                )
                Log.d("SVM log", "goalStudyHours: ${event.hours}")
            }
            is StudyScreenEvent.OnSubjectNameChange -> {
                studyScreenState.value = studyScreenState.value.copy(
                    subjectName = event.name
                )
                Log.d("SVM log", "subjectName: ${event.name}")
            }
            is StudyScreenEvent.OnTaskIsCompleteChange -> {
                updateTask(event.task)
            }
            StudyScreenEvent.SaveSubject -> {
                saveSubject()
            }
        }
        updateUI()
    }

    private fun deleteSession() {
        viewModelScope.launch {
            try {
                studyScreenState.value.session?.let {
                    sessionRepository.deleteSession(it)
                    _snackbarEventFlow.emit(
                        SnackbarEvent.ShowSnackbar(message = "Xóa phiên học thành công")
                    )
                }
            } catch (e: Exception) {
                _snackbarEventFlow.emit(
                    SnackbarEvent.ShowSnackbar(
                        message = "Không thể xóa phiên. ${e.message}",
                        duration = SnackbarDuration.Long
                    )
                )
            }
        }
    }

    private fun updateUI() {
        viewModelScope.launch {
            val subjects = subjectRepository.getAllSubjects().first()
            val sessions = sessionRepository.getAllSessions().first()
            Log.d("SVM log", "sessions: $sessions")
            val totalStudiedHours = sessions.sumOf { it.duration }

            studyScreenState.value = studyScreenState.value.copy(
                subjects = subjects,
                totalSubjectCount = subjects.size,
                totalGoalStudyHours = subjects.sumByDouble { it.goalHours.toDouble() }.toFloat(),
                totalStudiedHours = totalStudiedHours.toHours()
            )
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
                        name = studyScreenState.value.subjectName,
                        goalHours = studyScreenState.value.goalStudyHours.toFloatOrNull() ?: 2f,
                        uid = FirebaseAuth.getInstance().currentUser?.uid ?: "",
                    )
                )
                studyScreenState.value = studyScreenState.value.copy(
                    subjectName = "",
                    goalStudyHours = ""
                )
                _snackbarEventFlow.emit(SnackbarEvent.ShowSnackbar("Lưu thông tin môn học thành công"))
            } catch (e: Exception) {
                _snackbarEventFlow.emit(
                    SnackbarEvent.ShowSnackbar(
                        "Không thể lưu môn học",
                        SnackbarDuration.Long
                   )
                )
            }
            updateUI()
        }
    }

}