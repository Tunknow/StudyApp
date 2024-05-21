package com.example.studyapp.data.repositories

import com.example.studyapp.domain.model.Task
import kotlinx.coroutines.flow.Flow

interface TaskRepository {
    suspend fun insertTask(task: Task)

    suspend fun updateTask(task: Task)

    suspend fun deleteTask(task: Task)

    suspend fun getTaskById(taskId: String, subjectId: String): Task?

    fun getUpcomingTasksForSubject(subjectId: String): Flow<List<Task>>

    fun getCompletedTasksForSubject(subjectId: String): Flow<List<Task>>

    fun getAllUpcomingTasks(): Flow<List<Task>>
}