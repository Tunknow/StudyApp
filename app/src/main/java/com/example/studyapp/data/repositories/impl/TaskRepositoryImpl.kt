package com.example.studyapp.data.repositories.impl

import android.util.Log
import com.example.studyapp.data.repositories.TaskRepository
import com.example.studyapp.di.IoDispatcher
import com.example.studyapp.domain.model.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class TaskRepositoryImpl @Inject constructor(
    private val studyAppDB: FirebaseFirestore,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
): TaskRepository {

    val userId = FirebaseAuth.getInstance().currentUser!!.uid
    override suspend fun upsertTask(task: Task) {
        // Tạo một HashMap chứa dữ liệu của task
        val taskData = hashMapOf(
            "title" to task.title,
            "description" to task.description,
            "dueDate" to task.dueDate,
            "priority" to task.priority,
            "isCompleted" to task.isCompleted
        )

        // Thêm tài liệu mới vào collection "tasks" của môn học hiện tại
        studyAppDB.collection("users").document(userId)
            .collection("subjects").document(task.sid)
            .collection("tasks")
            .add(taskData)
            .addOnSuccessListener { documentReference ->
                // Lấy ID của tài liệu vừa được thêm vào
                val taskId = documentReference.id
                Log.d("TaskRepositoryImpl", "Task added with ID: $taskId")
            }
            .addOnFailureListener { exception ->
                // Xử lý nếu có lỗi xảy ra khi thêm tài liệu
                Log.w("TaskRepositoryImpl", "Error adding task", exception)
            }
    }

    override suspend fun deleteTask(taskId: String, subjectId: String) {
        // Xóa tài liệu task dựa trên taskId
        studyAppDB.collection("users").document(userId)
            .collection("subjects").document(subjectId)
            .collection("tasks")
            .document(taskId)
            .delete()
            .await()
    }

    override suspend fun getTaskById(taskId: String, subjectId: String): Task? {
        return try {
            val subjectName = studyAppDB.collection("users").document(userId)
                .collection("subjects")
                .document(subjectId)
                .get()
                .await()
                .getString("name") ?: ""
            // Thực hiện truy vấn Firestore để lấy tài liệu task dựa trên taskId
            val taskDocument = studyAppDB.collection("users").document(userId)
                .collection("subjects")
                .document(subjectId)
                .collection("tasks")
                .document(taskId)
                .get()
                .await()

            // Kiểm tra xem tài liệu có tồn tại không
            if (taskDocument.exists()) {
                // Nếu tài liệu tồn tại, lấy dữ liệu từ tài liệu và tạo đối tượng Task
                val title = taskDocument.getString("title") ?: ""
                val description = taskDocument.getString("description") ?: ""
                val priority = taskDocument.getLong("priority")?.toInt() ?: 0
                val dueDate = taskDocument.getLong("dueDate") ?: 0
                val isCompleted = taskDocument.getBoolean("isCompleted") ?: false

                Task(taskId, subjectId, title, description, dueDate, priority, subjectName, isCompleted)
            } else {
                // Nếu tài liệu không tồn tại, trả về null
                null
            }
        } catch (e: Exception) {
            // Xử lý ngoại lệ nếu có lỗi xảy ra và trả về null
            e.printStackTrace()
            null
        }
    }

    override fun getUpcomingTasksForSubject(subjectId: String): Flow<List<Task>> = callbackFlow {
        val taskList = mutableListOf<Task>()
        val subjectName = studyAppDB.collection("users").document(userId)
            .collection("subjects")
            .document(subjectId)
            .get()
            .await()
            .getString("name") ?: ""
        val tasksQuerySnapshot = studyAppDB.collection("users").document(userId)
            .collection("subjects").document(subjectId)
            .collection("tasks")
            .get()
            .await()
        tasksQuerySnapshot.documents.forEach { taskDocument ->
            val taskId = taskDocument.id
            val taskTitle = taskDocument.getString("title") ?: ""
            val taskDescription = taskDocument.getString("description") ?: ""
            val taskDueDate = taskDocument.getLong("dueDate") ?: 0
            val taskPriority = taskDocument.getLong("priority")?.toInt() ?: 0
            val taskCompleted = taskDocument.getBoolean("isCompleted") ?: false

            // Tạo đối tượng Task từ dữ liệu đọc được
            val task = Task(
                taskId,
                subjectId, // SubjectId được truyền từ tham số
                taskTitle,
                taskDescription,
                taskDueDate,
                taskPriority,
                subjectName,
                taskCompleted
            )

            // Thêm task vào danh sách nếu chưa hoàn thành
            if (!taskCompleted) {
                taskList.add(task)
            }
        }

        // Sắp xếp danh sách taskList theo dueDate
        val sortedTasks = taskList.sortedBy { it.dueDate }

        // Gửi danh sách taskList đã sắp xếp qua luồng
        trySend(sortedTasks).isSuccess

        // Đóng luồng khi không cần thiết nữa
        awaitClose { }
    }

    override fun getCompletedTasksForSubject(subjectId: String): Flow<List<Task>> = callbackFlow {
        val taskList = mutableListOf<Task>()
        val subjectName = studyAppDB.collection("users").document(userId)
            .collection("subjects")
            .document(subjectId)
            .get()
            .await()
            .getString("name") ?: ""
        val tasksQuerySnapshot = studyAppDB.collection("users").document(userId)
            .collection("subjects").document(subjectId)
            .collection("tasks")
            .get()
            .await()
        tasksQuerySnapshot.documents.forEach { taskDocument ->
            val taskId = taskDocument.id
            val taskTitle = taskDocument.getString("title") ?: ""
            val taskDescription = taskDocument.getString("description") ?: ""
            val taskDueDate = taskDocument.getLong("dueDate") ?: 0
            val taskPriority = taskDocument.getLong("priority")?.toInt() ?: 0
            val taskCompleted = taskDocument.getBoolean("isCompleted") ?: false

            // Tạo đối tượng Task từ dữ liệu đọc được
            val task = Task(
                taskId,
                subjectId, // SubjectId được truyền từ tham số
                taskTitle,
                taskDescription,
                taskDueDate,
                taskPriority,
                subjectName,
                taskCompleted
            )

            // Thêm task vào danh sách nếu chưa hoàn thành
            if (taskCompleted) {
                taskList.add(task)
            }
        }

        // Gửi danh sách taskList đã sắp xếp qua luồng
        trySend(taskList).isSuccess

        // Đóng luồng khi không cần thiết nữa
        awaitClose { }
    }


    override fun getAllUpcomingTasks(): Flow<List<Task>> = callbackFlow {
        val tasksList = mutableListOf<Task>()

        // Lấy danh sách tất cả các môn học của người dùng từ Firestore
        studyAppDB.collection("users").document(userId)
            .collection("subjects")
            .get()
            .addOnSuccessListener { subjectsQuerySnapshot ->
                // Duyệt qua từng môn học
                subjectsQuerySnapshot.documents.forEach { subjectDocument ->
                    val subjectId = subjectDocument.id
                    val subjectName = subjectDocument.getString("name") ?: "";

                    // Lấy danh sách các task của môn học hiện tại từ Firestore
                    studyAppDB.collection("users").document(userId)
                        .collection("subjects").document(subjectId)
                        .collection("tasks")
                        .get()
                        .addOnSuccessListener { tasksQuerySnapshot ->
                            // Duyệt qua từng task của môn học hiện tại
                            tasksQuerySnapshot.documents.forEach { taskDocument ->
                                // Đọc dữ liệu của task từ Firestore
                                val taskId = taskDocument.id
                                val taskSid = subjectId
                                val taskTitle = taskDocument.getString("title") ?: ""
                                val taskDescription = taskDocument.getString("description") ?: ""
                                val taskDueDate = taskDocument.getLong("dueDate") ?: 0
                                val taskPriority = taskDocument.getLong("priority")?.toInt() ?: 0
                                val taskCompleted = taskDocument.getBoolean("isCompleted") ?: false
                                val taskRelatedToSubject = subjectName


                                // Tạo đối tượng Task và thêm vào danh sách tasksList
                                val task = Task(
                                    taskId,
                                    taskSid,
                                    taskTitle,
                                    taskDescription,
                                    taskDueDate,
                                    taskPriority,
                                    taskRelatedToSubject,
                                    taskCompleted
                                )
                                tasksList.add(task)
                            }
                            val filteredTasks = tasksList.filter { !it.isCompleted }
                            val sortedTasks = filteredTasks.sortedWith(compareBy<Task> { it.dueDate }.thenByDescending { it.priority })

                            // Gửi danh sách tasksList qua luồng
                            trySend(sortedTasks).isSuccess
                        }
                        .addOnFailureListener { exception ->
                            // Xử lý nếu có lỗi xảy ra khi lấy danh sách tasks của môn học
                            close(exception)
                        }
                }
            }
            .addOnFailureListener { exception ->
                // Xử lý nếu có lỗi xảy ra khi lấy danh sách các môn học
                close(exception)
            }

        // Đóng luồng khi không cần thiết nữa
        awaitClose { }
    }
}