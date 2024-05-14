package com.example.studyapp.presentation.note.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
@Composable
@Preview
fun WelcomeMessageComponent() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            text = "Chào mừng quay trở lại👋,",
            fontSize = 18.sp,
        )

        Text(
            text = "Ghi chú hôm nay",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
        )
    }
}