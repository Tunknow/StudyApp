package com.example.studyapp.data.repositories

import com.example.studyapp.domain.model.Subject
import kotlinx.coroutines.flow.Flow

interface SubjectRepository {

    suspend fun upsertSubject(subject: Subject)

    fun getTotalSubjectCount(): Flow<Int>

    fun getTotalGoalHours(): Flow<Float>

    suspend fun deleteSubject(subjectId: String)

    suspend fun getSubjectById(subjectId: String): Subject?

    fun getAllSubjects(): Flow<List<Subject>>
}