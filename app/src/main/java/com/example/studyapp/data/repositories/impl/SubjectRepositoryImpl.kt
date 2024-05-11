package com.example.studyapp.data.repositories.impl

import android.util.Log
import com.example.studyapp.data.repositories.SubjectRepository
import com.example.studyapp.di.IoDispatcher
import com.example.studyapp.domain.model.Subject
import com.example.studyapp.util.Result
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

class SubjectRepositoryImpl @Inject constructor(
    private val studyAppDB: FirebaseFirestore,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
): SubjectRepository {

    val userId = FirebaseAuth.getInstance().currentUser!!.uid

    val subjectsList: MutableList<Subject> = mutableListOf()


    override suspend fun upsertSubject(subject: Subject) {
//        return withContext(ioDispatcher) {
//            try {
//                if (userId != null) {
//                    subject.uid = userId
//                    studyAppDB.collection("subjects")
//                        .add(subject)
//                    Result.Success(Unit)
//                } else {
//                    Result.Failure(Exception("No user is currently logged in"))
//                }
//            } catch (e: Exception) {
//                Result.Failure(e)
//            }
//        }
        return withContext(ioDispatcher) {
            try {
                if (userId != null) {
                    subject.uid = userId
                    val subjectData = hashMapOf(
                        "name" to subject.name,
                        "goalHours" to subject.goalHours,
                        "uid" to subject.uid
                    )

                    if (subject.id != null) {
                        // Nếu có ID, sử dụng set với ID đã cho để cập nhật tài liệu
                        studyAppDB.collection("subjects")
                            .document(subject.id!!)
                            .set(subjectData)
                            .await()
                    } else {
                        // Nếu không có ID, sử dụng add để tạo mới tài liệu
                        studyAppDB.collection("subjects")
                            .add(subjectData)
                            .await()
                    }

                    Result.Success(Unit)
                } else {
                    Result.Failure(Exception("No user is currently logged in"))
                }
            } catch (e: Exception) {
                Result.Failure(e)
            }
        }
    }

    override fun getTotalSubjectCount(): Flow<Int> = callbackFlow{
        val subscription = studyAppDB.collection("subjects")
            .whereEqualTo("uid", userId)
            .addSnapshotListener { querySnapshot, exception ->
                if (exception != null) {
                    close(exception)
                    return@addSnapshotListener
                }

                val subjectCount = querySnapshot?.size() ?: 0
                trySend(subjectCount).isSuccess
            }
        awaitClose { subscription.remove() }
    }

    override fun getTotalGoalHours(): Flow<Float> {
        Log.d("SRI log", "userId: $userId")
        return flow {
            emit(0f)
        }
    }

    override suspend fun deleteSubject(subjectId: String) {
        try {
            // Xóa môn học từ Firestore
            studyAppDB.collection("subjects")
                .document(subjectId)
                .delete()
                .await() // Chờ cho việc xóa thành công

        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun getSubjectById(subjectId: String): Subject? {
        if (userId != null) {
            try {
                // Truy vấn Firestore để lấy môn học theo subjectId
                val documentSnapshot = studyAppDB.collection("subjects")
                    .document(subjectId)
                    .get()
                    .await() // Chờ cho việc lấy dữ liệu hoàn thành

                if (documentSnapshot.exists()) {
                    // Chuyển đổi dữ liệu từ Firestore thành đối tượng Subject
                    return documentSnapshot.toObject(Subject::class.java)
                }
            } catch (e: Exception) {
                throw e
            }
        }
        return null // Trả về null nếu không tìm thấy môn học hoặc có lỗi xảy ra
    }

    override fun getAllSubjects(): Flow<List<Subject>> = callbackFlow {
        val subscription = studyAppDB.collection("subjects")
            .whereEqualTo("uid", userId)
            .addSnapshotListener { querySnapshot, exception ->
                if (exception != null) {
                    close(exception) // Đóng flow và trả về exception nếu có lỗi
                    return@addSnapshotListener
                }

                val subjectsList: MutableList<Subject> = mutableListOf()
                querySnapshot?.forEach { document ->
                    val id = document.id
                    val name = document.getString("name") ?: ""
                    val goalHours = document.getDouble("goalHours")?.toFloat() ?: 0f
                    val uid = document.getString("uid") ?: ""

                    val subject = Subject(id, name, goalHours, uid)
                    subjectsList.add(subject)
                }
                // Gửi danh sách các môn học mới qua flow
                trySend(subjectsList).isSuccess
            }

        // Hủy đăng ký khi flow bị hủy
        awaitClose { subscription.remove() }
    }
}