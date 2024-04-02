package com.example.studyapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.ExperimentalMaterial3Api
import com.example.studyapp.domain.model.Session
import com.example.studyapp.domain.model.Subject
import com.example.studyapp.domain.model.Task
import com.example.studyapp.presentation.subject.SubjectScreen
import com.example.studyapp.presentation.theme.StudyAppTheme

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            StudyAppTheme {
//                val menuItems = listOf(
//                    NavigationItem(
//                        title = "Học tập",
//                        route = Screens.Study.route,
//                        selectedIcon = Icons.Filled.DateRange,
//                        unSelectedIcon = Icons.Outlined.DateRange
//                    ),
//                    NavigationItem(
//                        title = "Ghi chú",
//                        route = Screens.Note.route,
//                        selectedIcon = Icons.Filled.Edit,
//                        unSelectedIcon = Icons.Outlined.Edit
//                    )
//                )
//
//                val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
//                val scope = rememberCoroutineScope()
//                val navController = rememberNavController()
//
//                val navBackStackEntry by navController.currentBackStackEntryAsState()
//                val currentRoute = navBackStackEntry?.destination?.route
//                val topbarTitle =
//                    if (currentRoute != null) {
//                        menuItems[menuItems.indexOfFirst {
//                            it.route == currentRoute
//                        }].title
//                    } else {
//                        menuItems[0].title
//                    }
//
//                ModalNavigationDrawer(
//                    drawerContent = {
//                        ModalDrawerSheet(
//
//                        ) {
//                            Spacer(modifier = Modifier.height(12.dp))
//                            NavBarBody(items = menuItems, currentRoute = currentRoute) { currentNavigationItem ->
//                                navController.navigate(currentNavigationItem.route) {
//                                    navController.graph.startDestinationRoute?.let {
//                                        popUpTo(it) {
//                                            saveState = true
//                                        }
//                                    }
//                                    launchSingleTop = true
//                                    restoreState = true
//                                }
//                                scope.launch {
//                                    drawerState.close()
//                                }
//                            }
//                        }
//                    },
//                    drawerState = drawerState
//                ) {
//                    Scaffold(
//                        topBar = {
//                            TopAppBar(
//                                title = {
//                                    Text(
//                                        text = topbarTitle,
//                                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
//                                    )
//                                },
//                                navigationIcon = {
//                                    IconButton(onClick = {
//                                        scope.launch {
//                                            drawerState.open()
//                                        }
//                                    }) {
//                                        Icon(imageVector = Icons.Default.Menu, contentDescription = "Menu")
//                                    }
//                                }
//                            )
//                        }
//                    ) {innerPadding ->
//                        SetUpNavGraph(navController = navController, innerPadding = innerPadding)
//                    }
//                }
                SubjectScreen()
            }
        }
    }
}

val subjects = listOf(
    Subject(name = "Toán", goalHours = 10f, subjectId = 0),
    Subject(name = "Tiếng Anh", goalHours = 10f, subjectId = 1),
    Subject(name = "Vật lý", goalHours = 10f, subjectId = 2),
    Subject(name = "Hóa học", goalHours = 10f, subjectId = 3),
    Subject(name = "Sinh học", goalHours = 10f, subjectId = 4)
)

val tasks = listOf(
    Task(
        title = "Làm bài tập về nhà",
        description = "",
        dueDate = 0L,
        priority = 1,
        relatedToSubject = "",
        isCompleted = true,
        taskId = 0,
        taskSubjectId = 0
    ),
    Task(
        title = "Học từ vụng",
        description = "",
        dueDate = 0L,
        priority = 0,
        relatedToSubject = "",
        isCompleted = true,
        taskId = 0,
        taskSubjectId = 0
    ),
    Task(
        title = "Xem bài giảng",
        description = "",
        dueDate = 0L,
        priority = 2,
        relatedToSubject = "",
        isCompleted = false,
        taskId = 0,
        taskSubjectId = 0
    ),
    Task(
        title = "Ôn tập kiểm tra",
        description = "",
        dueDate = 0L,
        priority = 0,
        relatedToSubject = "",
        isCompleted = false,
        taskId = 0,
        taskSubjectId = 0
    ),
    Task(
        title = "Ôn tập kiểm tra",
        description = "",
        dueDate = 0L,
        priority = 1,
        relatedToSubject = "",
        isCompleted = false,
        taskId = 0,
        taskSubjectId = 0
    ),
    Task(
        title = "Ôn tập kiểm tra",
        description = "",
        dueDate = 0L,
        priority = 1,
        relatedToSubject = "",
        isCompleted = false,
        taskId = 0,
        taskSubjectId = 0
    ),
    Task(
        title = "Ôn tập kiểm tra",
        description = "",
        dueDate = 0L,
        priority = 1,
        relatedToSubject = "",
        isCompleted = false,
        taskId = 0,
        taskSubjectId = 0
    )
)

val sessions = listOf(
    Session(
        relatedToSubject = "Toán",
        date = 0L,
        duration = 2L,
        sessionSubjectId = 0,
        sessionId = 0
    ),
    Session(
        relatedToSubject = "Tiếng Anh",
        date = 0L,
        duration = 2L,
        sessionSubjectId = 0,
        sessionId = 0
    ),
    Session(
        relatedToSubject = "Vật lý",
        date = 0L,
        duration = 2L,
        sessionSubjectId = 0,
        sessionId = 0
    ),
    Session(
        relatedToSubject = "Hóa học",
        date = 0L,
        duration = 2L,
        sessionSubjectId = 0,
        sessionId = 0
    )
)
