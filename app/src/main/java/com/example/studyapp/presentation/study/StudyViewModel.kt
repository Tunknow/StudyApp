package com.example.studyapp.presentation.study

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studyapp.data.repositories.SessionRepository
import com.example.studyapp.data.repositories.SubjectRepository
import com.example.studyapp.domain.model.Subject
import com.example.studyapp.domain.model.Task
import com.example.studyapp.util.toHours
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StudyViewModel @Inject constructor(
    private val subjectRepository: SubjectRepository,
    private val sessionRepository: SessionRepository
) : ViewModel() {

    private val _state = MutableStateFlow(StudyScreenState())

    val state = combine(
        _state,
        subjectRepository.getTotalSubjectCount(),
        subjectRepository.getTotalGoalHours(),
        subjectRepository.getAllSubjects(),
        sessionRepository.getTotalSessionsDuration()
    ) { state, subjectCount, goalHours, subjects, totalSessionDuration ->
        state.copy(
            totalSubjectCount = subjectCount,
            totalGoalStudyHours = goalHours,
            subjects = subjects,
            totalStudiedHours = totalSessionDuration.toHours()
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = StudyScreenState()
    )

    fun onEvent(event: StudyScreenEvent) {
        when (event) {
            StudyScreenEvent.DeleteSession -> TODO()
            is StudyScreenEvent.OnDeleteSessionButtonClick -> {
                _state.update {
                    it.copy(session = event.session)
                }
                Log.d("SVM log", "session: ${event.session}")
            }
            is StudyScreenEvent.OnGoalStudyHoursChange -> {
                _state.update {
                    it.copy(goalStudyHours = event.hours)
                }
                Log.d("SVM log", "goalStudyHours: ${event.hours}")
            }
            is StudyScreenEvent.OnSubjectNameChange -> {
                _state.update {
                    it.copy(subjectName = event.name)
                }
                Log.d("SVM log", "subjectName: ${event.name}")
            }
            is StudyScreenEvent.OnTaskIsCompleteChange -> TODO()
            StudyScreenEvent.SaveSubject -> saveSubject()
        }
    }

    private fun updateTask(task: Task) {

    }

    private fun saveSubject() {
        viewModelScope.launch {
            subjectRepository.upsertSubject(
                subject = Subject(
                    name = state.value.subjectName,
                    goalHours = state.value.goalStudyHours.toFloatOrNull() ?: 2f,
                    uid = FirebaseAuth.getInstance().currentUser?.uid ?: "",
                )
            )
        }
    }

}