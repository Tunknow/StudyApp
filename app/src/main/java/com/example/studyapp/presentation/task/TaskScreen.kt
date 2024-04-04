package com.example.studyapp.presentation.task

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.studyapp.presentation.components.DeleteDiaglog
import com.example.studyapp.presentation.components.TaskCheckBox
import com.example.studyapp.presentation.theme.Red
import com.example.studyapp.util.Priority

@Composable
fun TaskScreen() {

    var isDeleteDialogOpen by rememberSaveable { mutableStateOf(false) }

    var title by remember { mutableStateOf("") }

    var description by remember { mutableStateOf("") }

    var taskTitleError by rememberSaveable {
        mutableStateOf<String?>(null)
    }
    taskTitleError = when {
        title.isBlank() -> "Nhập tiêu đề!"
        title.length < 4 -> "Tiêu đề quá ngắn!"
        title.length > 30 -> "Tiêu đề quá dài!"
        else -> null
    }
    
    DeleteDiaglog(
        isOpen = isDeleteDialogOpen,
        title = "Xác nhận xóa nhiệm vụ",
        bodyText = "Bạn có chắc chắn muốn xóa nhiệm vụ này không?",
        onDismissRequest = { isDeleteDialogOpen = false },
        onConfirmButtonClick = { isDeleteDialogOpen = false }
    )

    Scaffold(
        topBar = {
            TaskScreenTopBar(
                isTaskExisted = true,
                isCompleted = false,
                checkBoxCorderColor = Red,
                onBackButtonClick = {  },
                onDeleteButtonClick = { isDeleteDialogOpen = true },
                onCheckBoxClick = {  }
            )
        }
    ) {paddingValues ->
        Column(
            modifier = Modifier
                .verticalScroll(state = rememberScrollState())
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 12.dp)
        ) {
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = title,
                onValueChange = {title = it},
                label = { Text(text = "Tiêu đề") },
                singleLine = true,
                isError = taskTitleError != null && title.isNotBlank(),
                supportingText = { Text(text = taskTitleError.orEmpty())}
            )
            Spacer(modifier = Modifier.height(10.dp))
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = description,
                onValueChange = {description = it},
                label = { Text(text = "Mô tả") }
            )
            Spacer(modifier = Modifier.height(20.dp))
            Text(text = "Ngày đến hạn", style = MaterialTheme.typography.bodySmall)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "30 tháng 4, 2024",
                    style = MaterialTheme.typography.bodyLarge
                )
                IconButton(onClick = { /*TODO*/ }) {
                    Icon(
                        imageVector = Icons.Default.DateRange,
                        contentDescription = "Chọn ngày đến hạn"
                    )
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(text = "Mức ưu tiên", style = MaterialTheme.typography.bodySmall)
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
            ) {
                Priority.entries.forEach() {priority ->
                    PriorityButton(
                        modifier = Modifier.weight(1f),
                        label = priority.title,
                        backgroundColor = priority.color,
                        borderColor = if(priority == Priority.MEDIUM) {
                            Color.White
                        } else Color.Transparent,
                        labelColor = if(priority == Priority.MEDIUM) {
                            Color.White
                        } else Color.White.copy(alpha = 0.7f),
                        onClick = {}
                    )
                }
            }
            Spacer(modifier = Modifier.height(30.dp))
            Text(text = "Môn học", style = MaterialTheme.typography.bodySmall)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Tiếng Anh",
                    style = MaterialTheme.typography.bodyLarge
                )
                IconButton(onClick = { /*TODO*/ }) {
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = "Chọn môn học"
                    )
                }
            }
            Button(
                enabled = taskTitleError == null,
                onClick = { /*TODO*/ },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp)
            ) {
                Text(text = "Lưu")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TaskScreenTopBar(
    isTaskExisted: Boolean,
    isCompleted: Boolean,
    checkBoxCorderColor: androidx.compose.ui.graphics.Color,
    onBackButtonClick: () -> Unit,
    onDeleteButtonClick: () -> Unit,
    onCheckBoxClick: () -> Unit
) {
    TopAppBar(
        navigationIcon = {
            IconButton(onClick = { onBackButtonClick }) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Quay lại"
                )
            }
        },
        title = { Text(text = "Nhiệm vụ", style = MaterialTheme.typography.headlineSmall) },
        actions = {
            if(isTaskExisted) {
                TaskCheckBox(
                    isCompleted = isCompleted,
                    borderColor = checkBoxCorderColor,
                    onCheckBoxClick = onCheckBoxClick
                )
                IconButton(onClick = { onDeleteButtonClick }) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Xóa nhiệm vụ"
                    )
                }
            }
        }
    )
}

@Composable
private fun PriorityButton(
    modifier: Modifier = Modifier,
    label: String,
    backgroundColor: Color,
    borderColor: Color,
    labelColor: Color,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .background(backgroundColor)
            .clickable { onClick() }
            .padding(5.dp)
            .border(1.dp, borderColor, RoundedCornerShape(5.dp))
            .padding(5.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(text = label, color = labelColor)
    }

}