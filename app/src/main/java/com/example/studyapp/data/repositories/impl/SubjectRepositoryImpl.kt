package com.example.studyapp.data.repositories.impl

import android.util.Log
import com.example.studyapp.data.repositories.SubjectRepository
import com.example.studyapp.di.IoDispatcher
import com.example.studyapp.domain.model.Subject
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

class SubjectRepositoryImpl @Inject constructor(
    private val studyAppDB: FirebaseFirestore,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
): SubjectRepository {

    val userId = FirebaseAuth.getInstance().currentUser!!.uid

    val subjectsList: MutableList<Subject> = mutableListOf()


    override suspend fun insertSubject(subject: Subject) : String {
        return withContext(ioDispatcher) {
            try {
                if (userId != null) {
                    val subjectData = hashMapOf(
                        "name" to subject.name,
                        "goalHours" to subject.goalHours
                    )
                    // Thêm tài liệu mới vào collection "subjects" của người dùng hiện tại
                    val documentRef = studyAppDB.collection("users").document(userId)
                        .collection("subjects")
                        .add(subjectData)
                        .await()
                    // Trả về ID của tài liệu vừa được thêm vào
                    documentRef.id
                } else {
                    throw Exception("No user is currently logged in")
                }
            } catch (e: Exception) {
                throw e
            }
        }
    }

    override suspend fun updateSubject(subject: Subject) {
        withContext(ioDispatcher) {
            try {
                // Cập nhật dữ liệu của môn học trong subcollection "subjects" của người dùng
                studyAppDB.collection("users").document(userId)
                    .collection("subjects")
                    .document(subject.id!!)
                    .update(
                        mapOf(
                            "name" to subject.name,
                            "goalHours" to subject.goalHours
                        )
                    )
                    .await()
            } catch (e: Exception) {
                throw e
            }
        }
    }

    override fun getTotalSubjectCount(): Flow<Int> = callbackFlow{

        Log.d("SRI log", "Inside getTotalSubjectCount")
        val collectionRef = studyAppDB.collection("users").document(userId)
            .collection("subjects")

        // Tạo một listener để theo dõi thay đổi trong collection
        val registration = collectionRef.addSnapshotListener { querySnapshot, exception ->
            if (exception != null) {
                close(exception)
                return@addSnapshotListener
            }

            // Kiểm tra nếu querySnapshot không null và có dữ liệu
            if (querySnapshot != null && !querySnapshot.isEmpty) {
                // Lấy số lượng tài liệu trong collection
                val count = querySnapshot.size()
                // Gửi giá trị count tới luồng
                trySend(count).isSuccess
            } else {
                // Nếu không có dữ liệu, gửi giá trị 0 tới luồng
                trySend(0).isSuccess
            }
        }

        // Đóng luồng khi không cần thiết nữa
        awaitClose { registration.remove() }

    }


    override suspend fun deleteSubject(subjectId: String) {
        try {
            val subjectRef = studyAppDB.collection("users").document(userId).collection("subjects").document(subjectId)

            // Xóa tất cả documents trong subcollection "tasks"
            val tasksCollectionRef = subjectRef.collection("tasks")
            deleteCollection(tasksCollectionRef, 100)

            // Xóa tất cả documents trong subcollection "sessions"
            val sessionsCollectionRef = subjectRef.collection("sessions")
            deleteCollection(sessionsCollectionRef, 100)

            // Cuối cùng xóa document subject
            subjectRef.delete().await()

        } catch (e: Exception) {
            throw e
        }
    }

    suspend fun deleteCollection(collectionRef: CollectionReference, batchSize: Long) {
        try {
            var query = collectionRef.limit(batchSize)
            while (true) {
                val snapshot = query.get().await()
                if (snapshot.isEmpty) {
                    break
                }
                val batch = collectionRef.firestore.batch()
                for (document in snapshot.documents) {
                    batch.delete(document.reference)
                }
                batch.commit().await()
            }
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun getSubjectById(subjectId: String): Subject? {
        if (userId != null) {
            try {
                // Truy vấn Firestore để lấy môn học theo subjectId trong subcollection "subjects" của người dùng
                val documentSnapshot = studyAppDB.collection("users").document(userId)
                    .collection("subjects")
                    .document(subjectId)
                    .get()
                    .await() // Chờ cho việc lấy dữ liệu hoàn thành

                if (documentSnapshot.exists()) {
                    val id = documentSnapshot.id
                    val name = documentSnapshot.getString("name") ?: ""
                    val goalHours = documentSnapshot.getDouble("goalHours")?.toFloat() ?: 0f
                    val uid = userId
                    return Subject(id, name, goalHours, uid)
                }
            } catch (e: Exception) {
                throw e

            }
        }
        return null // Trả về null nếu không tìm thấy môn học hoặc có lỗi xảy ra
    }


    override fun getAllSubjects(): Flow<List<Subject>> = callbackFlow {
        val subscription = studyAppDB.collection("users").document(userId)
            .collection("subjects")
            .addSnapshotListener { querySnapshot, exception ->
                if (exception != null) {
                    close(exception) // Đóng flow và trả về exception nếu có lỗi
                    Log.d("SRI log", "cannot get subject")
                    return@addSnapshotListener
                }

                val subjectsList: MutableList<Subject> = mutableListOf()
                querySnapshot?.forEach { document ->
                    val id = document.id
                    val name = document.getString("name") ?: ""
                    val goalHours = document.getDouble("goalHours")?.toFloat() ?: 0f
                    val uid = userId

                    val subject = Subject(id, name, goalHours, uid)
                    subjectsList.add(subject)
                }
                // Gửi danh sách các môn học mới qua flow
                trySend(subjectsList).isSuccess
                Log.d("SRI log", "subjectsList: $subjectsList")
            }

        // Hủy đăng ký khi flow bị hủy
        awaitClose { subscription.remove() }
    }

}