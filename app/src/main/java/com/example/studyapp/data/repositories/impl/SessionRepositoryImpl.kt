package com.example.studyapp.data.repositories.impl

import com.example.studyapp.data.repositories.SessionRepository
import com.example.studyapp.di.IoDispatcher
import com.example.studyapp.domain.model.Session
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class SessionRepositoryImpl @Inject constructor(
    private val studyAppDB: FirebaseFirestore,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : SessionRepository {

    val userId = FirebaseAuth.getInstance().currentUser?.uid
    override suspend fun insertSession(session: Session) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteSession(session: Session) {
        TODO("Not yet implemented")
    }

    override fun getAllSessions(): Flow<List<Session>> {
        TODO("Not yet implemented")
    }

    override fun getRecentFiveSessions(): Flow<List<Session>> {
        TODO("Not yet implemented")
    }

    override fun getRecentTenSessionsForSubject(subjectId: String): Flow<List<Session>> {
        TODO("Not yet implemented")
    }

    override fun getTotalSessionsDuration(): Flow<Long> {
        return flow {
            emit(0L)
        }
    }


    override fun getTotalSessionsDurationBySubject(subjectId: String): Flow<Long> {
        TODO("Not yet implemented")
    }
}