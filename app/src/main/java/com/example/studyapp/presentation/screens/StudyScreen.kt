package com.example.studyapp.presentation.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.example.studyapp.R
import com.example.studyapp.domain.model.Subject
import com.example.studyapp.presentation.components.AddSubjectDiaglog
import com.example.studyapp.presentation.components.CountCard
import com.example.studyapp.presentation.components.DeleteDiaglog
import com.example.studyapp.presentation.components.SubjectCard
import com.example.studyapp.presentation.components.studySessionsList
import com.example.studyapp.presentation.components.tasksList
import com.example.studyapp.sessions
import com.example.studyapp.subjects
import com.example.studyapp.tasks

@Composable
fun Dashboard(
    innerPadding: PaddingValues
) {

    var isAddSubjectDialogOpen by rememberSaveable { mutableStateOf(false) }
    var isDeleteSessionDialogOpen by rememberSaveable { mutableStateOf(false) }
    var subjectName by remember { mutableStateOf("") }
    var goalHours by remember { mutableStateOf("") }

    AddSubjectDiaglog(
        isOpen = isAddSubjectDialogOpen,
        subjectName = subjectName,
        goalHours = goalHours,
        onSubjectNameChange = {subjectName = it},
        onGoalHoursChange = {goalHours = it},
        onDismissRequest = { isAddSubjectDialogOpen = false },
        onConfirmButtonClick = {
            isAddSubjectDialogOpen = false
        }
    )

    DeleteDiaglog(
        isOpen = isDeleteSessionDialogOpen,
        title = "Xoá phiên học tập?",
        bodyText = "Bạn có muốn xóa phiên học này? \nThời gian đã học sẽ bị hủy.",
        onDismissRequest = { isDeleteSessionDialogOpen = false },
        onConfirmButtonClick = {isDeleteSessionDialogOpen = false}
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
    ) {
        item {
            CountCardsSection(
                subjectCount = 5,
                studiedHours = "10",
                goalHours = "30",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 20.dp, bottom = 12.dp, start = 12.dp, end = 12.dp)
            )
        }
        item {
            SubjectCardsSection(
                subjectList = subjects,
                onAddIconClick = {isAddSubjectDialogOpen = true},
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 12.dp, end = 12.dp),
            )
        }
        item {
            Button(
                onClick = { /*TODO*/ },
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
            onCheckBoxClick = {},
            onTaskCardClick = {}
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
    }
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
    modifier: Modifier
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
        if (subjectList.isEmpty()) {
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
        }
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(subjectList) { subject ->
                SubjectCard(
                    subjectName = subject.name,
                    color = MaterialTheme.colorScheme.tertiaryContainer,
                    onClick = {}
                )
            }
        }
    }
}

