package com.example.studyapp.data.repositories.impl

import android.util.Log
import com.example.studyapp.data.repositories.NoteRepository
import com.example.studyapp.di.IoDispatcher
import com.example.studyapp.domain.model.Note
import com.example.studyapp.util.Result
import com.example.studyapp.util.convertDateFormat
import com.example.studyapp.util.getCurrentTimeAsString
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull
import javax.inject.Inject

class NoteRepositoryImpl @Inject constructor(
    private val studyAppDb: FirebaseFirestore,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
) : NoteRepository {

    val userId = FirebaseAuth.getInstance().currentUser!!.uid
    override suspend fun addNote(title: String, body: String): Result<Unit> {
        return try {
            withContext(ioDispatcher) {
                val note = hashMapOf(
                    "title" to title,
                    "body" to body,
                    "createdAt" to getCurrentTimeAsString(),
                )

                val addTaskTimeout = withTimeoutOrNull(10000L) {
                    studyAppDb.collection("users").document(userId).collection("notes")
                        .add(note)
                }

                if (addTaskTimeout == null) {
                    Log.d("ERROR: ", "Timeout")

                    Result.Failure(IllegalStateException("Timeout"))
                }

                Result.Success(Unit)
            }
        } catch (exception: Exception) {
            Log.d("ERROR: ", "$exception")

            Result.Failure(exception = exception)
        }
    }

    override suspend fun getAllNotes(): Result<List<Note>> {
        return try {
            withContext(ioDispatcher) {
                val fetchingTasksTimeout = withTimeoutOrNull(10000L) {
                    studyAppDb.collection("users").document(userId).collection("notes")
                        .get()
                        .await()
                        .documents.map { document ->
                            Note(
                                id = document.id,
                                sid = userId,
                                title = document.getString("title") ?: "",
                                body = document.getString("body") ?: "",
                                createdAt = convertDateFormat(
                                    document.getString("createdAt") ?: "",
                                ),
                            )
                        }
                }

                if (fetchingTasksTimeout == null) {
                    Log.d("ERROR: ", "Timeout")

                    Result.Failure(IllegalStateException("Timeout"))
                }

                Log.d("TASKS: ", "${fetchingTasksTimeout?.toList()}")

                Result.Success(fetchingTasksTimeout?.toList() ?: emptyList())
            }
        } catch (exception: Exception) {
            Log.d("ERROR: ", "$exception")

            Result.Failure(exception = exception)
        }
    }

    override suspend fun deleteNote(noteId: String): Result<Unit> {
        return try {
            withContext(ioDispatcher) {
                val deleteTaskTimeout = withTimeoutOrNull(10000L) {
                    studyAppDb.collection("users").document(userId).collection("notes")
                        .document(noteId)
                        .delete()
                        .await()
                }

                if (deleteTaskTimeout == null) {
                    Log.d("ERROR: ", "Timeout")

                    Result.Failure(IllegalStateException("Timeout"))
                }

                Result.Success(Unit)
            }
        } catch (exception: Exception) {
            Log.d("ERROR: ", "$exception")

            Result.Failure(exception = exception)
        }
    }

    override suspend fun updateNote(title: String, body: String, noteId: String): Result<Unit> {
        return try {
            withContext(ioDispatcher) {
                val updateTaskTimeout = withTimeoutOrNull(10000L) {
                    studyAppDb.collection("users").document(userId).collection("notes")
                        .document(noteId)
                        .update(
                            mapOf(
                                "title" to title,
                                "body" to body,
                                "createdAt" to getCurrentTimeAsString(),
                            )
                        )
                        .await()
                }

                if (updateTaskTimeout == null) {
                    Log.d("ERROR: ", "Timeout")

                    Result.Failure(IllegalStateException("Timeout"))
                }

                Result.Success(Unit)
            }
        } catch (exception: Exception) {
            Log.d("ERROR: ", "$exception")

            Result.Failure(exception = exception)
        }
    }

}