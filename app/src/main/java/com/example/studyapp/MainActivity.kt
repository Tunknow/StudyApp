package com.example.studyapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.ExperimentalMaterial3Api
import com.example.studyapp.domain.model.Session
import com.example.studyapp.domain.model.Subject
import com.example.studyapp.domain.model.Task
import com.example.studyapp.presentation.theme.StudyAppTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            StudyAppTheme {
                // A surface container using the 'background' color from the theme
                // Surface(color = MaterialTheme.colors.background) {
                //     Greeting("Android")
//                DestinationsNavHost(navGraph = NavGraphs.root)
                StudyApp()
            }
        }
    }
}











val subjects = listOf(
    Subject(name = "Toán", goalHours = 10f, id = "a", uid = ""),
    Subject(name = "Tiếng Anh", goalHours = 10f, id = "b", uid = ""),
    Subject(name = "Vật lý", goalHours = 10f, id = "c",   uid = ""),
    Subject(name = "Hóa học", goalHours = 10f, id = "d", uid = ""),
    Subject(name = "Sinh học", goalHours = 10f, id = "e", uid = ""),
)

val tasks = listOf(
    Task(
        title = "Làm bài tập về nhà",
        description = "",
        dueDate = 0L,
        priority = 1,
        relatedToSubject = "",
        isCompleted = true,
        id = "a",
        sid = "a"
    ),
    Task(
        title = "Học từ vụng",
        description = "",
        dueDate = 0L,
        priority = 0,
        relatedToSubject = "",
        isCompleted = true,
        id = "b",
        sid = "b"
    ),
    Task(
        title = "Xem bài giảng",
        description = "",
        dueDate = 0L,
        priority = 2,
        relatedToSubject = "",
        isCompleted = false,
        id = "c",
        sid = "c"
    ),
    Task(
        title = "Ôn tập kiểm tra",
        description = "",
        dueDate = 0L,
        priority = 0,
        relatedToSubject = "",
        isCompleted = false,
        id = "d",
        sid = "d"
    ),
    Task(
        title = "Ôn tập kiểm tra",
        description = "",
        dueDate = 0L,
        priority = 1,
        relatedToSubject = "",
        isCompleted = false,
        id = "e",
        sid = "e"
    ),
    Task(
        title = "Ôn tập kiểm tra",
        description = "",
        dueDate = 0L,
        priority = 1,
        relatedToSubject = "",
        isCompleted = false,
        id = "f",
        sid = "f"
    ),
    Task(
        title = "Ôn tập kiểm tra",
        description = "",
        dueDate = 0L,
        priority = 1,
        relatedToSubject = "",
        isCompleted = false,
        id = "g",
        sid = "g"
    )
)

val sessions = listOf(
    Session(
        relatedToSubject = "Tiếng Anh",
        date = 0L,
        duration = 2L,
        sid = "",
        id = "b"
    ),
    Session(
        relatedToSubject = "Tiếng Anh",
        date = 0L,
        duration = 2L,
        sid = "",
        id = "d"
    )
)
