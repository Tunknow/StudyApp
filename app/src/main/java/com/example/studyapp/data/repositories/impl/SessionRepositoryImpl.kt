package com.example.studyapp.data.repositories.impl

import android.util.Log
import com.example.studyapp.data.repositories.SessionRepository
import com.example.studyapp.di.IoDispatcher
import com.example.studyapp.domain.model.Session
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
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

    override fun getAllSessions(): Flow<List<Session>> = callbackFlow {
        val sessionsMap = mutableMapOf<String, List<Session>>() // Map lưu trữ danh sách các phiên cho mỗi môn học
        val sessionListeners = mutableMapOf<String, ListenerRegistration>() // Map lưu trữ các listener cho mỗi môn học

        // Lắng nghe sự thay đổi của tất cả các môn học của người dùng
        val subjectsListener = studyAppDB.collection("users").document(userId)
            .collection("subjects")
            .addSnapshotListener { subjectsQuerySnapshot, subjectsException ->
                subjectsException?.let { close(it) } // Đóng luồng nếu có lỗi xảy ra khi lấy danh sách các môn học
                subjectsQuerySnapshot?.let { subjectsSnapshot ->
                    subjectsSnapshot.documents.forEach { subjectDocument ->
                        val subjectId = subjectDocument.id

                        // Lắng nghe sự thay đổi của tất cả các phiên trong mỗi môn học
                        val sessionsListener = studyAppDB.collection("users").document(userId)
                            .collection("subjects").document(subjectId)
                            .collection("sessions")
                            .addSnapshotListener { sessionsQuerySnapshot, sessionsException ->
                                sessionsException?.let { close(it) } // Đóng luồng nếu có lỗi xảy ra khi lấy danh sách các phiên
                                sessionsQuerySnapshot?.let { sessionsSnapshot ->
                                    val sessionsList = mutableListOf<Session>()

                                    sessionsSnapshot.documents.forEach { sessionDocument ->
                                        // Đọc dữ liệu của phiên từ Firestore
                                        val sessionId = sessionDocument.id
                                        val sessionDate = sessionDocument.getLong("date") ?: 0
                                        val sessionDuration = sessionDocument.getLong("duration") ?: 0

                                        // Tạo đối tượng Session và thêm vào danh sách sessionsList
                                        val session = Session(
                                            sessionId,
                                            subjectId,
                                            "", // Tên môn học sẽ được cập nhật sau
                                            sessionDate,
                                            sessionDuration
                                        )
                                        sessionsList.add(session)
                                    }

                                    // Lưu danh sách phiên của môn học vào map
                                    sessionsMap[subjectId] = sessionsList

                                    // Gộp danh sách phiên của tất cả các môn học và gửi qua flow
                                    val allSessions = sessionsMap.values.flatten()
                                    trySend(allSessions).isSuccess
                                }
                            }

                        // Thêm listener vào map để quản lý
                        sessionListeners[subjectId]?.remove()
                        sessionListeners[subjectId] = sessionsListener
                    }
                }
            }

        awaitClose {
            // Hủy tất cả các listener khi không cần thiết nữa
            subjectsListener.remove()
            sessionListeners.forEach { (_, listener) -> listener.remove() }
            sessionListeners.clear()
        }
    }



    override fun getRecentFiveSessions(): Flow<List<Session>> = callbackFlow {
        val sessionsList = mutableListOf<Session>() // Danh sách các phiên

        // Lắng nghe sự thay đổi của tất cả các môn học của người dùng
        val subjectsListener = studyAppDB.collection("users").document(userId)
            .collection("subjects")
            .addSnapshotListener { subjectsQuerySnapshot, subjectsException ->
                subjectsException?.let { close(it) } // Đóng luồng nếu có lỗi xảy ra khi lấy danh sách các môn học
                subjectsQuerySnapshot?.let { subjectsSnapshot ->
                    val pendingRequests = AtomicInteger(subjectsSnapshot.size())
                    subjectsSnapshot.documents.forEach { subjectDocument ->
                        val subjectId = subjectDocument.id
                        val subjectName = subjectDocument.getString("name") ?: ""

                        // Lắng nghe sự thay đổi của các phiên gần đây của mỗi môn học
                        val sessionsListener = studyAppDB.collection("users").document(userId)
                            .collection("subjects").document(subjectId)
                            .collection("sessions")
                            .orderBy("date", Query.Direction.DESCENDING)
                            .limit(5) // Giới hạn số lượng phiên
                            .addSnapshotListener { sessionsQuerySnapshot, sessionsException ->
                                sessionsException?.let { close(it) } // Đóng luồng nếu có lỗi xảy ra khi lấy danh sách phiên
                                sessionsQuerySnapshot?.let { sessionsSnapshot ->
                                    sessionsList.clear() // Xóa danh sách cũ để cập nhật dữ liệu mới
                                    sessionsSnapshot.documents.forEach { sessionDocument ->
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
                                        trySend(sessionsList).isSuccess
                                    }
                                }
                            }
                    }
                }
            }

        awaitClose {
            // Hủy lắng nghe khi không cần thiết nữa
            subjectsListener.remove()
        }
    }





    override fun getRecentTenSessionsForSubject(subjectId: String): Flow<List<Session>> = callbackFlow {
        val sessionsList = mutableListOf<Session>() // Danh sách các phiên

        // Lắng nghe sự thay đổi của các phiên gần đây của môn học
        val sessionsListener = studyAppDB.collection("users").document(userId)
            .collection("subjects").document(subjectId)
            .collection("sessions")
            .orderBy("date", Query.Direction.DESCENDING)
            .limit(10) // Giới hạn số lượng phiên
            .addSnapshotListener { sessionsQuerySnapshot, sessionsException ->
                sessionsException?.let { close(it) } // Đóng luồng nếu có lỗi xảy ra khi lấy danh sách phiên
                sessionsQuerySnapshot?.let { sessionsSnapshot ->
                    sessionsList.clear() // Xóa danh sách cũ để cập nhật dữ liệu mới
                    sessionsSnapshot.documents.forEach { sessionDocument ->
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
            }

        awaitClose {
            // Hủy lắng nghe khi không cần thiết nữa
            sessionsListener.remove()
        }
    }


    override fun getTotalSessionsDuration(): Flow<Long> = callbackFlow {
        val collectionRef = studyAppDB.collection("users").document(userId)
            .collection("subjects")

        // Tạo một listener để theo dõi thay đổi trong collection
        val registration = collectionRef.addSnapshotListener { querySnapshot, exception ->
            if (exception != null) {
                close(exception)
                return@addSnapshotListener
            }

            // Tính tổng số giờ học
            var totalStudyHours = 0L
            querySnapshot?.documents?.forEach { subjectDocument ->
                val subjectId = subjectDocument.id
                val subjectSessionsRef = studyAppDB.collection("users").document(userId)
                    .collection("subjects").document(subjectId)
                    .collection("sessions")

                subjectSessionsRef.get()
                    .addOnSuccessListener { sessionsQuerySnapshot ->
                        sessionsQuerySnapshot.documents.forEach { sessionDocument ->
                            val duration = sessionDocument.getLong("duration") ?: 0L
                            totalStudyHours += duration
                        }
                        trySend(totalStudyHours).isSuccess
                    }
                    .addOnFailureListener { exception ->
                        close(exception)
                    }
            }
        }

        // Đóng luồng khi không cần thiết nữa
        awaitClose { registration.remove() }
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