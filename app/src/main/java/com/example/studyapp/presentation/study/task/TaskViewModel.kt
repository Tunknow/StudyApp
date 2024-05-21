package com.example.studyapp.presentation.study.task

import androidx.compose.material3.SnackbarDuration
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studyapp.data.repositories.SubjectRepository
import com.example.studyapp.data.repositories.TaskRepository
import com.example.studyapp.domain.model.Task
import com.example.studyapp.util.Priority
import com.example.studyapp.util.SnackbarEvent
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
import java.time.Instant
import javax.inject.Inject

@HiltViewModel
class TaskViewModel @Inject constructor(
    private val taskRepository: TaskRepository,
    private val subjectRepository: SubjectRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val subjectId: String = savedStateHandle.get<String>("subjectId")!!
    private val taskId: String = savedStateHandle.get<String>("id")?: ""

    private val _state = MutableStateFlow(TaskScreenState())
    val state = combine(
        _state,
        subjectRepository.getAllSubjects()
    ) { state, subjects ->
        state.copy(subjects = subjects)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000),
        initialValue = TaskScreenState()
    )

    private val _snackbarEventFlow = MutableSharedFlow<SnackbarEvent>()
    val snackbarEventFlow = _snackbarEventFlow.asSharedFlow()

    init {
        fetchTask()
        fetchSubject()
    }

    fun onEvent(event: TaskScreenEvent) {
        when (event) {
            is TaskScreenEvent.OnTitleChange -> {
                _state.update {
                    it.copy(title = event.title)
                }
            }

            is TaskScreenEvent.OnDescriptionChange -> {
                _state.update {
                    it.copy(description = event.description)
                }
            }

            is TaskScreenEvent.OnDateChange -> {
                _state.update {
                    it.copy(dueDate = event.millis)
                }
            }

            is TaskScreenEvent.OnPriorityChange -> {
                _state.update {
                    it.copy(priority = event.priority)
                }
            }

            TaskScreenEvent.OnIsCompleteChange -> {
                _state.update {
                    it.copy(isTaskComplete = !_state.value.isTaskComplete)
                }
            }

            is TaskScreenEvent.OnRelatedSubjectSelect -> {
                _state.update {
                    it.copy(
                        relatedToSubject = event.subject.name,
                        subjectId = event.subject.id
                    )
                }
            }

            TaskScreenEvent.SaveTask -> saveTask()
            TaskScreenEvent.DeleteTask -> deleteTask()
        }
    }

    private fun deleteTask() {
        viewModelScope.launch {
            try {
                val currentTaskId = state.value.currentTaskId
                if (currentTaskId != null) {
                    withContext(Dispatchers.IO) {
                        taskRepository.deleteTask(task = Task(id = currentTaskId, sid = subjectId))
                    }
                    _snackbarEventFlow.emit(
                        SnackbarEvent.ShowSnackbar(message = "Nhiệm vụ đã được xóa thành công.")
                    )
                    _snackbarEventFlow.emit(SnackbarEvent.NavigateUp)
                } else {
                    _snackbarEventFlow.emit(
                        SnackbarEvent.ShowSnackbar(message = "Không có nhiệm vụ để xóa.")
                    )
                }
            } catch (e: Exception) {
                _snackbarEventFlow.emit(
                    SnackbarEvent.ShowSnackbar(
                        message = "Không thể xóa nhiệm vụ. ${e.message}",
                        duration = SnackbarDuration.Long
                    )
                )
            }
        }
    }

    private fun saveTask() {
        viewModelScope.launch {
            val state = _state.value
            if (state.subjectId == null || state.relatedToSubject == null) {
                _snackbarEventFlow.emit(
                    SnackbarEvent.ShowSnackbar(
                        message = "Vui lòng chọn môn học liên quan trước khi lưu nhiệm vụ."
                    )
                )
                return@launch
            }
            try {
                if(state.currentTaskId == null) {
                    taskRepository.insertTask(
                        task = Task(
                            title = state.title,
                            description = state.description,
                            dueDate = state.dueDate ?: Instant.now().toEpochMilli(),
                            relatedToSubject = state.relatedToSubject,
                            priority = state.priority.value,
                            isCompleted = state.isTaskComplete,
                            sid = state.subjectId,
                        )
                    )
                    _snackbarEventFlow.emit(
                        SnackbarEvent.ShowSnackbar(message = "Nhiệm vụ đã được lưu thành công.")
                    )
                    _snackbarEventFlow.emit(SnackbarEvent.NavigateUp)
                } else {
                    taskRepository.updateTask(
                        task = Task(
                            title = state.title,
                            description = state.description,
                            dueDate = state.dueDate ?: Instant.now().toEpochMilli(),
                            relatedToSubject = state.relatedToSubject,
                            priority = state.priority.value,
                            isCompleted = state.isTaskComplete,
                            sid = state.subjectId,
                            id = state.currentTaskId!!
                        )
                    )
                    _snackbarEventFlow.emit(
                        SnackbarEvent.ShowSnackbar(message = "Nhiệm vụ đã được lưu thành công.")
                    )
                    _snackbarEventFlow.emit(SnackbarEvent.NavigateUp)
                }
            } catch (e: Exception) {
                _snackbarEventFlow.emit(
                    SnackbarEvent.ShowSnackbar(
                        message = "Không thể lưu nhiệm vụ. ${e.message}",
                        duration = SnackbarDuration.Long
                    )
                )
            }
        }
    }

    private fun fetchTask() {
        viewModelScope.launch {
            taskId?.let { id ->
                taskRepository.getTaskById(id, subjectId)?.let { task ->
                    val subjectName = subjectRepository.getSubjectById(subjectId)?.name
                    _state.update {
                        it.copy(
                            title = task.title,
                            description = task.description,
                            dueDate = task.dueDate,
                            isTaskComplete = task.isCompleted,
                            relatedToSubject = subjectName ?: "",
                            priority = Priority.fromInt(task.priority),
                            subjectId = subjectId,
                            currentTaskId = task.id
                        )
                    }
                }
            }
        }
    }

    private fun fetchSubject() {
        viewModelScope.launch {
            subjectId?.let { id ->
                subjectRepository.getSubjectById(id)?.let { subject ->
                    _state.update {
                        it.copy(
                            subjectId = subject.id,
                            relatedToSubject = subject.name
                        )
                    }
                }
            }
        }
    }
}