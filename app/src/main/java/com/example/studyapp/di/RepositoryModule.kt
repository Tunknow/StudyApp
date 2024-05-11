package com.example.studyapp.di

import com.example.studyapp.data.repositories.SessionRepository
import com.example.studyapp.data.repositories.SubjectRepository
import com.example.studyapp.data.repositories.impl.SessionRepositoryImpl
import com.example.studyapp.data.repositories.impl.SubjectRepositoryImpl
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {
    @Provides
    @Singleton
    fun provideSubjectRepository(
        firestore: FirebaseFirestore,
        @IoDispatcher ioDispatcher: CoroutineDispatcher
    ): SubjectRepository {
        return SubjectRepositoryImpl(firestore, ioDispatcher)
    }

    @Provides
    @Singleton
    fun provideSessionRepository(
        firestore: FirebaseFirestore,
        @IoDispatcher ioDispatcher: CoroutineDispatcher
    ): SessionRepository {
        return SessionRepositoryImpl(firestore, ioDispatcher)
    }
}