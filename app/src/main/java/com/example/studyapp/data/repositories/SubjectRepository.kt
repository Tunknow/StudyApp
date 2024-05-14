package com.example.studyapp.data.repositories

import com.example.studyapp.domain.model.Subject
import kotlinx.coroutines.flow.Flow

interface SubjectRepository {

    suspend fun insertSubject(subject: Subject) : String

    suspend fun  updateSubject(subject: Subject)

    fun getTotalSubjectCount(): Flow<Int>

    suspend fun deleteSubject(subjectId: String)

    suspend fun getSubjectById(subjectId: String): Subject?

    fun getAllSubjects(): Flow<List<Subject>>
}