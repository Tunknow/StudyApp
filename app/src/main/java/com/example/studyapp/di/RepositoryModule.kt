package com.example.studyapp.di

import com.example.studyapp.data.repositories.NoteRepository
import com.example.studyapp.data.repositories.ProfileRepository
import com.example.studyapp.data.repositories.SessionRepository
import com.example.studyapp.data.repositories.SubjectRepository
import com.example.studyapp.data.repositories.TaskRepository
import com.example.studyapp.data.repositories.impl.NoteRepositoryImpl
import com.example.studyapp.data.repositories.impl.ProfileRepositoryImpl
import com.example.studyapp.data.repositories.impl.SessionRepositoryImpl
import com.example.studyapp.data.repositories.impl.SubjectRepositoryImpl
import com.example.studyapp.data.repositories.impl.TaskRepositoryImpl
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

    @Provides
    @Singleton
    fun provideTaskRepository(
        firestore: FirebaseFirestore,
        @IoDispatcher ioDispatcher: CoroutineDispatcher
    ): TaskRepository {
        return TaskRepositoryImpl(firestore, ioDispatcher)
    }

    @Provides
    @Singleton
    fun provideNoteRepository(
        firestore: FirebaseFirestore,
        @IoDispatcher ioDispatcher: CoroutineDispatcher
    ): NoteRepository {
        return NoteRepositoryImpl(firestore, ioDispatcher)
    }

    @Provides
    @Singleton
    fun provideProfileRepository(
        firestore: FirebaseFirestore,
        @IoDispatcher ioDispatcher: CoroutineDispatcher
    ): ProfileRepository {
        return ProfileRepositoryImpl(firestore, ioDispatcher)
    }
}