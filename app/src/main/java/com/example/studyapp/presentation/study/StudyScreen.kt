package com.example.studyapp.presentation.study

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.example.studyapp.R
import com.example.studyapp.domain.model.Subject
import com.example.studyapp.presentation.navigation.Screens
import com.example.studyapp.presentation.study.components.AddSubjectDiaglog
import com.example.studyapp.presentation.study.components.CountCard
import com.example.studyapp.presentation.study.components.DeleteDiaglog
import com.example.studyapp.presentation.study.components.SubjectCard
import com.example.studyapp.presentation.study.components.studySessionsList
import com.example.studyapp.presentation.study.components.tasksList
import com.example.studyapp.util.SnackbarEvent
import kotlinx.coroutines.flow.collectLatest

@Composable
fun StudyScreen(
    navController: NavHostController
) {

    val viewModel : StudyViewModel = hiltViewModel()
    val state by viewModel.studyScreenState;
    val onEvent : (StudyScreenEvent) -> Unit = viewModel::onEvent

    val subjects by viewModel.subjects.collectAsStateWithLifecycle()
    val tasks by viewModel.tasks.collectAsStateWithLifecycle()
    val recentSession by viewModel.recentSession.collectAsStateWithLifecycle()

    var isAddSubjectDialogOpen by rememberSaveable { mutableStateOf(false) }
    var isDeleteSessionDialogOpen by rememberSaveable { mutableStateOf(false) }

    val snackbarEventFlow = viewModel.snackbarEventFlow
    val snackbarHostState = remember {
        SnackbarHostState()
    }

    LaunchedEffect(key1 = true) {
        snackbarEventFlow.collectLatest { event ->
            when (event) {
                SnackbarEvent.NavigateUp -> TODO()
                is SnackbarEvent.ShowSnackbar -> {
                    snackbarHostState.showSnackbar(
                        message = event.message,
                        duration = event.duration
                    )
                }
            }
        }
    }

    AddSubjectDiaglog(
        isOpen = isAddSubjectDialogOpen,
        subjectName = state.subjectName,
        goalHours = state.goalStudyHours,
        onSubjectNameChange = { onEvent(StudyScreenEvent.OnSubjectNameChange(it))},
        onGoalHoursChange = { onEvent(StudyScreenEvent.OnGoalStudyHoursChange(it)) },
        onDismissRequest = { isAddSubjectDialogOpen = false },
        onConfirmButtonClick = {
            onEvent(StudyScreenEvent.SaveSubject)
            isAddSubjectDialogOpen = false
        }
    )

    DeleteDiaglog(
        isOpen = isDeleteSessionDialogOpen,
        title = "Xoá phiên học tập?",
        bodyText = "Bạn có muốn xóa phiên học này? \nThời gian đã học sẽ bị hủy.",
        onDismissRequest = { isDeleteSessionDialogOpen = false },
        onConfirmButtonClick = {
            onEvent(StudyScreenEvent.DeleteSession)
            isDeleteSessionDialogOpen = false
        }
    )

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = { StudyScreenTopBar()}
    ) {paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            item {
                CountCardsSection(
                    subjectCount = state.totalSubjectCount,
                    studiedHours = state.totalStudiedHours.toString(),
                    goalHours = state.totalGoalStudyHours.toString(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 20.dp, bottom = 12.dp, start = 12.dp, end = 12.dp)
                )
            }
            item {
                SubjectCardsSection(
                    subjectList = subjects,
                    onAddIconClick = {isAddSubjectDialogOpen = true},
                    onSubjectCardClick = { index ->
                        navController.navigate(Screens.SubjectScreenRoute.passSubjectId(index?: "")) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 12.dp, end = 12.dp),
                )
            }
            item {
                Button(
                    onClick = {navController.navigate(Screens.SessionScreenRoute.route)},
                    colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.primary),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 48.dp, vertical = 20.dp)
                ) {
                    Text(
                        text = "Bắt đầu phiên học",
                        color = MaterialTheme.colorScheme.onPrimary,
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }
            tasksList(
                sectionTitle = "NHIỆM VỤ SẮP TỚI",
                emptyListText = "Không có nhiệm vụ nào!",
                tasks = tasks,
                onCheckBoxClick = {onEvent(StudyScreenEvent.OnTaskIsCompleteChange(it))},
                onTaskCardClick = {taskId ->
                    navController.navigate(Screens.TaskScreenRoute.passTaskId(taskId = taskId))
                }
            )
            item {
                Spacer(modifier = Modifier.height(20.dp))
            }
            studySessionsList(
                sectionTitle = "PHIÊN HỌC GẦN ĐÂY",
                emptyListText = "Bạn không có phiên học tập nào gần đây.",
                sessions = recentSession,
                onDeleteIconClick = {
                    onEvent(StudyScreenEvent.OnDeleteSessionButtonClick(it))
                    isDeleteSessionDialogOpen = true
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun StudyScreenTopBar() {
    CenterAlignedTopAppBar(
        title = {
            Text(
                text = "Học tập",
                style = MaterialTheme.typography.headlineMedium
            )
        }
    )
}

@Composable
private fun CountCardsSection(
    subjectCount: Int,
    studiedHours: String,
    goalHours: String,
    modifier: Modifier
) {
    Row(modifier = modifier) {
        CountCard(
            headingText = "Số môn học",

            count = "$subjectCount",
            modifier = Modifier.weight(1f)
        )
        Spacer(modifier = Modifier.width(10.dp))
        CountCard(
            headingText = "Số giờ đã học",
            count = "$studiedHours",
            modifier = Modifier.weight(1f)
        )
        Spacer(modifier = Modifier.width(10.dp))
        CountCard(
            headingText = "Mục tiêu",
            count = "$goalHours",
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun SubjectCardsSection(
    subjectList: List<Subject>,
    onAddIconClick: () -> Unit,
    modifier: Modifier,
    onSubjectCardClick: (String?) -> Unit
) {
    Column(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "MÔN HỌC",
                style = MaterialTheme.typography.bodyMedium,
            )
            IconButton(onClick = onAddIconClick) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Thêm môn học"
                )
            }
        }
        if (subjectList.isNullOrEmpty()) {
            Image(
                modifier = Modifier
                    .size(120.dp)
                    .align(Alignment.CenterHorizontally),
                painter = painterResource(id = R.drawable.bookshelf),
                contentDescription = "Hiện chưa có môn học nào.\n" +
                        " Bấm vào dấu + để thêm môn học mới."
            )
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = "Hiện chưa có môn học nào.\n Bấm vào dấu + để thêm môn học mới.",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray,
                textAlign = TextAlign.Center
            )
        } else {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(subjectList) { subject ->
                    SubjectCard(
                        subjectName = subject.name,
                        color = MaterialTheme.colorScheme.tertiaryContainer,
                        onClick = { onSubjectCardClick(subject.id)}
                    )
                }
            }
        }

    }
}

