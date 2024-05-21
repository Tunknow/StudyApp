package com.example.studyapp

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.app.ActivityCompat
import com.example.studyapp.di.TimerServiceHolder
import com.example.studyapp.domain.model.Session
import com.example.studyapp.domain.model.Subject
import com.example.studyapp.domain.model.Task
import com.example.studyapp.presentation.study.session.StudySessionTimerService
import com.example.studyapp.presentation.theme.StudyAppTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private var isBound by mutableStateOf(false)
//    private lateinit var timerService: StudySessionTimerService
    private val connection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as StudySessionTimerService.StudySessionTimerBinder
            TimerServiceHolder.timerService = binder.getService()
            isBound = true
            Log.d("MainActivity", "Service connected: $isBound")
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            isBound = false
            Log.d("MainActivity", "Service disconnected: $isBound")
        }

    }

    override fun onStart() {
        super.onStart()
        Intent(this, StudySessionTimerService::class.java).also { intent ->
            bindService(intent, connection, Context.BIND_AUTO_CREATE)
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            if(isBound) {
                StudyAppTheme {
                    // A surface container using the 'background' color from the theme
                    // Surface(color = MaterialTheme.colors.background) {
                    //     Greeting("Android")
//                DestinationsNavHost(navGraph = NavGraphs.root)
                    StudyApp()
                }
            }

        }
        requestPermission()
    }

    private fun requestPermission() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),
                0
            )
        }
    }

    override fun onStop() {
        super.onStop()
        unbindService(connection)
        isBound = false
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
