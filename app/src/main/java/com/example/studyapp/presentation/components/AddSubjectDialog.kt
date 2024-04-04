package com.example.studyapp.presentation.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

@Composable
fun AddSubjectDiaglog(
    isOpen: Boolean,
    title: String = "Thêm/Sửa môn học",
    subjectName: String,
    goalHours: String,
    onSubjectNameChange: (String) -> Unit,
    onGoalHoursChange: (String) -> Unit,
    onDismissRequest: () -> Unit,
    onConfirmButtonClick: () -> Unit
) {
    var subjectNameError by rememberSaveable { mutableStateOf<String?>(null)}
    var goalHoursError by rememberSaveable { mutableStateOf<String?>(null)}

    subjectNameError = when {
        subjectName.isBlank() -> "Nhập tên môn học!"
        subjectName.length < 2 -> "Tên quá ngắn!"
        subjectName.length > 20 -> "Tên quá dài!"
        else -> null
    }

    goalHoursError = when {
        goalHours.isBlank() -> "Nhập số giờ học."
        goalHours.toFloatOrNull() == null -> "Giá trị không hợp lệ."
        goalHours.toFloat() < 1f -> "Hãy học ít nhất 1 giờ."
        goalHours.toFloat() > 1000f -> "Không nhập quá 1000 giờ."
        else -> null
    }

    if(isOpen) {
        AlertDialog(
            onDismissRequest = onDismissRequest,
            title = { Text(text = title)},
            text = {
                Column {
                    OutlinedTextField(
                        value = subjectName,
                        onValueChange = onSubjectNameChange,
                        label = { Text(text = "Tên môn học")},
                        singleLine = true,
                        isError = subjectNameError != null && subjectName.isNotBlank(),
                        supportingText = { Text(text = subjectNameError.orEmpty())}
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = goalHours,
                        onValueChange = onGoalHoursChange,
                        label = {Text(text = "Giờ học mục tiêu")},
                        singleLine = true,
                        isError = goalHoursError != null && goalHours.isNotBlank(),
                        supportingText = { Text(text = goalHoursError.orEmpty())},
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                }
            },
            dismissButton = {
                TextButton(onClick =  onDismissRequest ) {
                    Text(text = "Hủy")
                }
            },
            confirmButton = {
                TextButton(
                    onClick = onConfirmButtonClick,
                    enabled = subjectNameError == null && goalHoursError == null
                ) {
                    Text(text = "Lưu")
                }
            }

        )
    }
}