package com.example.studyapp.data.repositories

import com.example.studyapp.domain.model.Session
import kotlinx.coroutines.flow.Flow

interface SessionRepository {

    suspend fun insertSession(session: Session)

    suspend fun deleteSession(session: Session)

    fun getAllSessions(): Flow<List<Session>>

    fun getRecentFiveSessions(): Flow<List<Session>>

    fun getRecentTenSessionsForSubject(subjectId: String): Flow<List<Session>>

    fun getTotalSessionsDuration(): Flow<Long>

    fun getTotalSessionsDurationBySubject(subjectId: String): Flow<Long>
}