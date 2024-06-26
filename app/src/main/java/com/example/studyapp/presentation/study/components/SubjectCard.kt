package com.example.studyapp.presentation.study.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.studyapp.R

@Composable
fun SubjectCard(
    modifier: Modifier = Modifier,
    subjectName: String,
    color: Color,
    onClick: () -> Unit
) {
    Card {
        Box(
            modifier = modifier
                .size(150.dp)
                .clickable { onClick() }
                .background(
                    color = color,
                    shape = MaterialTheme.shapes.medium
                )
        )
        {
            Column(
                modifier = Modifier.padding(12.dp),
                verticalArrangement = Arrangement.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.bookshelf),
                    contentDescription = subjectName,
                    modifier = Modifier.size(80.dp)
                )
                Text(
                    text = subjectName,
                    style = MaterialTheme.typography.headlineMedium.copy(fontSize = 25.sp),
                    color = MaterialTheme.colorScheme.onTertiaryContainer,
                    maxLines = 1
                )
            }
        }
    }
}