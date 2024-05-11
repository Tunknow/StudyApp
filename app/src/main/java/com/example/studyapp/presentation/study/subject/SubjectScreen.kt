package com.example.studyapp.presentation.study.subject

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.studyapp.presentation.navigation.Screens
import com.example.studyapp.presentation.study.components.AddSubjectDiaglog
import com.example.studyapp.presentation.study.components.CountCard
import com.example.studyapp.presentation.study.components.DeleteDiaglog
import com.example.studyapp.presentation.study.components.studySessionsList
import com.example.studyapp.presentation.study.components.tasksList
import com.example.studyapp.sessions
import com.example.studyapp.tasks

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubjectScreen(
    subjectId: String,
    navController: NavHostController
) {

    val viewModel : SubjectViewModel = hiltViewModel()

    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val listState = rememberLazyListState()
    val isFABExpanded by remember { derivedStateOf { listState.firstVisibleItemIndex == 0 } }

    var isEditSubjectDialogOpen by rememberSaveable { mutableStateOf(false) }
    var isDeleteSubjectDialogOpen by rememberSaveable { mutableStateOf(false) }
    var isDeleteSessionDialogOpen by rememberSaveable { mutableStateOf(false) }
    var subjectName by remember { mutableStateOf("") }
    var goalHours by remember { mutableStateOf("") }

    AddSubjectDiaglog(
        isOpen = isEditSubjectDialogOpen,
        subjectName = subjectName,
        goalHours = goalHours,
        onSubjectNameChange = {subjectName = it},
        onGoalHoursChange = {goalHours = it},
        onDismissRequest = { isEditSubjectDialogOpen = false },
        onConfirmButtonClick = {
            isEditSubjectDialogOpen = false
        }
    )

    DeleteDiaglog(
        isOpen = isDeleteSubjectDialogOpen,
        title = "Xoá môn học?",
        bodyText = "Bạn có muốn môn học này? \nTất cả nhiệm vụ và phiên học liên quan sẽ bị xóa.",
        onDismissRequest = { isDeleteSubjectDialogOpen = false },
        onConfirmButtonClick = {isDeleteSubjectDialogOpen = false}
    )

    DeleteDiaglog(
        isOpen = isDeleteSessionDialogOpen,
        title = "Xoá phiên học tập?",
        bodyText = "Bạn có muốn xóa phiên học này? \nThời gian đã học sẽ bị hủy.",
        onDismissRequest = { isDeleteSessionDialogOpen = false },
        onConfirmButtonClick = {isDeleteSessionDialogOpen = false}
    )

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            SubjectScreenTopBar(
                title = "Toán",
                onBackButtonClick = {navController.navigateUp()},
                onDeleteButtonClick = { isDeleteSubjectDialogOpen = true },
                onEditButtonClick = { isEditSubjectDialogOpen = true},
                scrollBehavior = scrollBehavior
            )
        },
        floatingActionButton = {
            if (isFABExpanded) {
                ExtendedFloatingActionButton(
                    onClick = {
                              navController.navigate(Screens.TaskScreenRoute.passTaskId(subjectId = subjectId))
                    },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Thêm nhiệm vụ"
                        )
                    },
                    text = { Text("Thêm nhiệm vụ") },
                    expanded = true
                )
            }
        }
    ) { paddingValue ->
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(paddingValue)
        ) {
            item {
                SubjectOverviewSection(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    studiedHours = "10",
                    goalHours = "15",
                    progress = 0.75f
                )
            }
            tasksList(
                sectionTitle = "NHIỆM VỤ SẮP TỚI",
                emptyListText = "Không có nhiệm vụ nào!",
                tasks = tasks,
                onCheckBoxClick = {},
                onTaskCardClick = {taskId ->
                    navController.navigate(Screens.TaskScreenRoute.passTaskId(taskId = taskId))}
            )
            item {
                Spacer(modifier = Modifier.height(20.dp))
            }
            tasksList(
                sectionTitle = "NHIỆM VỤ HOÀN THÀNH",
                emptyListText = "Không có nhiệm vụ nào!",
                tasks = tasks,
                onCheckBoxClick = {},
                onTaskCardClick = {taskId ->
                    navController.navigate(Screens.TaskScreenRoute.passTaskId(taskId = taskId, subjectId = subjectId))}
            )
            item {
                Spacer(modifier = Modifier.height(20.dp))
            }
            studySessionsList(
                sectionTitle = "PHIÊN HỌC GẦN ĐÂY",
                emptyListText = "Bạn không có phiên học tập nào gần đây.",
                sessions = sessions,
                onDeleteIconClick = {isDeleteSessionDialogOpen = true}
            )
            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SubjectScreenTopBar(
    title: String,
    onBackButtonClick: () -> Unit,
    onDeleteButtonClick: () -> Unit,
    onEditButtonClick: () -> Unit,
    scrollBehavior: TopAppBarScrollBehavior
) {
    LargeTopAppBar(
        scrollBehavior = scrollBehavior,
        navigationIcon = {
            IconButton(onClick = onBackButtonClick) {
                Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "quay lại")
            }                 
        },
        title = { 
            Text(
                text = title,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.headlineSmall
            )
        },
        actions = {
            IconButton(onClick = onDeleteButtonClick) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "xóa môn học"
                )
            }
            IconButton(onClick = onEditButtonClick) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "sửa môn học"
                )
            }
        }
    )
}

@Composable
private fun SubjectOverviewSection(
    modifier: Modifier,
    studiedHours: String,
    goalHours: String,
    progress: Float
) {
    val percentageProcess = remember(progress) {
        (progress * 100).toInt().coerceIn(0, 100)
    }
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {
        CountCard(
            headingText = "Mục tiêu",
            count = goalHours,
            modifier = Modifier.weight(1f)
        )
        Spacer(modifier = Modifier.width(10.dp))
        CountCard(
            headingText = "Đã học",
            count = studiedHours,
            modifier = Modifier.weight(1f)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Box(
            modifier = Modifier.size(75.dp),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(
                modifier = Modifier.fillMaxSize(),
                progress = 1f,
                strokeWidth = 4.dp,
                strokeCap = StrokeCap.Round,
                color = MaterialTheme.colorScheme.surfaceVariant
            )
            CircularProgressIndicator(
                modifier = Modifier.fillMaxSize(),
                progress = progress,
                strokeWidth = 4.dp,
                strokeCap = StrokeCap.Round
            )
            Text(text = "$percentageProcess%")
        }
    }
}