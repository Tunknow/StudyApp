package com.example.studyapp.data.repositories.impl

import android.util.Log
import com.example.studyapp.data.repositories.SessionRepository
import com.example.studyapp.di.IoDispatcher
import com.example.studyapp.domain.model.Session
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import java.util.concurrent.atomic.AtomicInteger
import javax.inject.Inject

class SessionRepositoryImpl @Inject constructor(
    private val studyAppDB: FirebaseFirestore,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : SessionRepository {

    val userId = FirebaseAuth.getInstance().currentUser!!.uid
    override suspend fun insertSession(session: Session) {
        studyAppDB.collection("users").document(userId)
            .collection("subjects").document(session.sid)
            .collection("sessions").add(
                hashMapOf(
                    "date" to session.date,
                    "duration" to session.duration
                )
            )
    }

    override suspend fun deleteSession(session: Session) {
        studyAppDB.collection("users").document(userId)
            .collection("subjects").document(session.sid)
            .collection("sessions").document(session.id)
            .delete()
            .addOnSuccessListener {
                Log.d("SRI log", "deleteSession: Success")
            }
    }

    override fun getAllSessions(): Flow<List<Session>> = callbackFlow {val sessionsList = mutableListOf<Session>() // Danh sách các phiên

        // Lấy danh sách tất cả các môn học của người dùng từ Firestore
        studyAppDB.collection("users").document(userId)
            .collection("subjects")
            .get()
            .addOnSuccessListener { subjectsQuerySnapshot ->
                val pendingRequests = AtomicInteger(subjectsQuerySnapshot.size())
                // Duyệt qua từng môn học
                subjectsQuerySnapshot.documents.forEach { subjectDocument ->
                    val subjectId = subjectDocument.id
                    val subjectName = subjectDocument.getString("name") ?: ""

                    // Lấy danh sách các phiên gần đây của môn học hiện tại từ Firestore
                    studyAppDB.collection("users").document(userId)
                        .collection("subjects").document(subjectId)
                        .collection("sessions")
                        .get()
                        .addOnSuccessListener { sessionsQuerySnapshot ->
                            // Duyệt qua từng phiên của môn học hiện tại
                            sessionsQuerySnapshot.documents.forEach { sessionDocument ->
                                // Đọc dữ liệu của phiên từ Firestore
                                val sessionId = sessionDocument.id
                                val sessionSid = subjectId
                                val sessionDate = sessionDocument.getLong("date") ?: 0
                                val sessionDuration = sessionDocument.getLong("duration") ?: 0

                                // Tạo đối tượng Session và thêm vào danh sách sessionsList
                                val session = Session(
                                    sessionId,
                                    sessionSid,
                                    subjectName, // Thêm tên môn học vào đây
                                    sessionDate,
                                    sessionDuration
                                )
                                sessionsList.add(session)
                            }
                            if (pendingRequests.decrementAndGet() == 0) {
                                // Nếu không còn yêu cầu đang chờ, gửi danh sách các phiên qua luồng
                                Log.d("SRI log", "getAllSessions: $sessionsList")
                                trySend(sessionsList).isSuccess
                            }

                        }
                        .addOnFailureListener { exception ->
                            // Xử lý nếu có lỗi xảy ra khi lấy danh sách phiên của môn học
//                            Log.d("SRI log", "getRecentFiveSessions: $exception")
                            close(exception)
                        }
                }

            }
            .addOnFailureListener { exception ->
                // Xử lý nếu có lỗi xảy ra khi lấy danh sách các môn học
                close(exception)
            }

        // Gửi danh sách các phiên qua luồng khi đã lấy được tất cả

//        trySend(sessionsList).isSuccess
//        Log.d("SRI log", "getRecentFiveSessions: $sessionsList")
        // Đóng luồng khi không cần thiết nữa
        awaitClose { }
    }

    override fun getRecentFiveSessions(): Flow<List<Session>> = callbackFlow {
        val sessionsList = mutableListOf<Session>() // Danh sách các phiên

        // Lấy danh sách tất cả các môn học của người dùng từ Firestore
        studyAppDB.collection("users").document(userId)
            .collection("subjects")
            .get()
            .addOnSuccessListener { subjectsQuerySnapshot ->
                val pendingRequests = AtomicInteger(subjectsQuerySnapshot.size())
                // Duyệt qua từng môn học
                subjectsQuerySnapshot.documents.forEach { subjectDocument ->
                    val subjectId = subjectDocument.id
                    val subjectName = subjectDocument.getString("name") ?: ""

                    // Lấy danh sách các phiên gần đây của môn học hiện tại từ Firestore
                    studyAppDB.collection("users").document(userId)
                        .collection("subjects").document(subjectId)
                        .collection("sessions")
                        .orderBy("date", Query.Direction.DESCENDING)
                        .limit(5) // Giới hạn số lượng phiên
                        .get()
                        .addOnSuccessListener { sessionsQuerySnapshot ->
                            // Duyệt qua từng phiên của môn học hiện tại
                            sessionsQuerySnapshot.documents.forEach { sessionDocument ->
                                // Đọc dữ liệu của phiên từ Firestore
                                val sessionId = sessionDocument.id
                                val sessionSid = subjectId
                                val sessionDate = sessionDocument.getLong("date") ?: 0
                                val sessionDuration = sessionDocument.getLong("duration") ?: 0

                                // Tạo đối tượng Session và thêm vào danh sách sessionsList
                                val session = Session(
                                    sessionId,
                                    sessionSid,
                                    subjectName, // Thêm tên môn học vào đây
                                    sessionDate,
                                    sessionDuration
                                )
                                sessionsList.add(session)
                            }
                            if (pendingRequests.decrementAndGet() == 0) {
                                // Nếu không còn yêu cầu đang chờ, gửi danh sách các phiên qua luồng
                                Log.d("SRI log", "getRecentFiveSessions: $sessionsList")
                                trySend(sessionsList).isSuccess
                            }

                        }
                        .addOnFailureListener { exception ->
                            // Xử lý nếu có lỗi xảy ra khi lấy danh sách phiên của môn học
//                            Log.d("SRI log", "getRecentFiveSessions: $exception")
                            close(exception)
                        }
                }

            }
            .addOnFailureListener { exception ->
                // Xử lý nếu có lỗi xảy ra khi lấy danh sách các môn học
                close(exception)
            }

        // Gửi danh sách các phiên qua luồng khi đã lấy được tất cả

//        trySend(sessionsList).isSuccess
//        Log.d("SRI log", "getRecentFiveSessions: $sessionsList")
        // Đóng luồng khi không cần thiết nữa
        awaitClose { }
    }




    override fun getRecentTenSessionsForSubject(subjectId: String): Flow<List<Session>> = callbackFlow {
        val sessionsList = mutableListOf<Session>() // Danh sách các phiên

        // Lấy danh sách các phiên gần đây của môn học từ Firestore
        studyAppDB.collection("users").document(userId)
            .collection("subjects").document(subjectId)
            .collection("sessions")
            .orderBy("date", Query.Direction.DESCENDING)
            .limit(10) // Giới hạn số lượng phiên
            .get()
            .addOnSuccessListener { sessionsQuerySnapshot ->
                // Duyệt qua từng phiên của môn học
                sessionsQuerySnapshot.documents.forEach { sessionDocument ->
                    // Đọc dữ liệu của phiên từ Firestore
                    val sessionId = sessionDocument.id
                    val sessionSid = subjectId
                    val sessionDate = sessionDocument.getLong("date") ?: 0
                    val sessionDuration = sessionDocument.getLong("duration") ?: 0

                    // Tạo đối tượng Session và thêm vào danh sách sessionsList
                    val session = Session(
                        sessionId,
                        sessionSid,
                        "", // Tên môn học sẽ được cập nhật sau
                        sessionDate,
                        sessionDuration
                    )
                    sessionsList.add(session)
                }
                trySend(sessionsList).isSuccess
            }
            .addOnFailureListener { exception ->
                // Xử lý nếu có lỗi xảy ra khi lấy danh sách phiên của môn học
                close(exception)
            }

        // Đóng luồng khi không cần thiết nữa
        awaitClose { }
    }

    override fun getTotalSessionsDuration(): Flow<Long> = callbackFlow {

    }




    override fun getTotalSessionsDurationBySubject(subjectId: String): Flow<Long> = callbackFlow {
        val subjectSessionsRef = studyAppDB.collection("users").document(userId)
            .collection("subjects").document(subjectId)
            .collection("sessions")

        // Thực hiện truy vấn để lấy dữ liệu
        val registration = subjectSessionsRef.addSnapshotListener { querySnapshot, exception ->
            if (exception != null) {
                // Nếu có lỗi, đóng luồng và gửi exception
                close(exception)
                return@addSnapshotListener
            }

            // Tính tổng số giờ học
            var totalStudyHours = 0L
            querySnapshot?.documents?.forEach { sessionDocument ->
                val duration = sessionDocument.getLong("duration") ?: 0L
                totalStudyHours += duration
            }

            // Gửi tổng số giờ học qua luồng

            trySend(totalStudyHours).isSuccess

        }

        // Đóng luồng khi không cần thiết nữa
        awaitClose { registration.remove() }
    }
}