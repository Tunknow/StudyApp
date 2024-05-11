package com.example.studyapp.presentation.study.session

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.studyapp.presentation.study.components.DeleteDiaglog
import com.example.studyapp.presentation.study.components.SubjectListBottomSheet
import com.example.studyapp.presentation.study.components.studySessionsList
import com.example.studyapp.sessions
import com.example.studyapp.subjects
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SessionScreen(
    navController: NavHostController
) {

    val viewModel : SessionViewModel = hiltViewModel()

    var isDeleteDialogOpen by rememberSaveable { mutableStateOf(false) }

    val scope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState()
    var isBottomSheetOpen by remember { mutableStateOf(false) }

    SubjectListBottomSheet(
        sheetState = sheetState,
        isOpen = isBottomSheetOpen,
        subjects = subjects,
        onDismissRequess = { isBottomSheetOpen = false },
        onSubjectClicked = {
            scope.launch { sheetState.hide() }.invokeOnCompletion {
                if (!sheetState.isVisible) isBottomSheetOpen = false
            }
        }
    )

    DeleteDiaglog(
        isOpen = isDeleteDialogOpen,
        title = "Xác nhận xóa nhiệm vụ",
        bodyText = "Bạn có chắc chắn muốn xóa nhiệm vụ này không?",
        onDismissRequest = { isDeleteDialogOpen = false },
        onConfirmButtonClick = { isDeleteDialogOpen = false }
    )

    Scaffold(
        topBar = {
            SessionScreenTopBar(
                onBackButtonClick = {navController.navigateUp()}
            )
        }
    ) {paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            item {
                TimerSection(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f)
                )
            }
            item {
                RelatedToSubjectSection(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp),
                    relatedToSubject = "Tiếng anh",
                    selectSubjectButtonClick = { isBottomSheetOpen = true}
                )
            }
            item {
                ButtonsSection(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    startButtonClick = {},
                    cancelButtonClick = {},
                    finishButtonClick = {}
                )
            }
            studySessionsList(
                sectionTitle = "LỊCH SỬ",
                emptyListText = "Bạn không có phiên học tập nào gần đây.",
                sessions = sessions,
                onDeleteIconClick = {isDeleteDialogOpen = true}
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SessionScreenTopBar(
    onBackButtonClick: () -> Unit
) {
    TopAppBar(
        navigationIcon = {
             IconButton(onClick = onBackButtonClick) {
                 Icon(
                     imageVector = Icons.Default.ArrowBack,
                     contentDescription = "Back"
                 )
             }
        },
        title = {
            Text(text = "Phiên học", style = MaterialTheme.typography.headlineSmall)
        }
    )
}

@Composable
private fun TimerSection(
    modifier: Modifier
) {
    Box(
        modifier =modifier,
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(250.dp)
                .border(5.dp, MaterialTheme.colorScheme.surfaceVariant, CircleShape)
        )
        Text(
            text = "00:05:32",
            style = MaterialTheme.typography.titleLarge.copy(fontSize = 45.sp)
        )
    }
}

@Composable
private fun RelatedToSubjectSection(
    modifier: Modifier,
    relatedToSubject: String,
    selectSubjectButtonClick: () -> Unit
) {
    Column(
        modifier = modifier
    ) {
        Text(text = "Môn học", style = MaterialTheme.typography.bodySmall)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = relatedToSubject,
                style = MaterialTheme.typography.bodyLarge
            )
            IconButton(onClick = selectSubjectButtonClick) {
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = "Chọn môn học"
                )
            }
        }
    }
}

@Composable
private fun ButtonsSection(
    modifier: Modifier,
    startButtonClick: () -> Unit,
    cancelButtonClick: () -> Unit,
    finishButtonClick: () -> Unit
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Button(onClick = cancelButtonClick) {
            Text(
                text = "Hủy",
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
            )
        }
        Button(onClick = startButtonClick) {
            Text(
                text = "Bắt đầu",
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
            )
        }
        Button(onClick = finishButtonClick) {
            Text(
                text = "Hoàn thành",
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
            )
        }
    }
}